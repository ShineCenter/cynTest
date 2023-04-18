package com.shine.mglm;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.shine.mglm.util.DbOperationUtils;

import java.io.Serializable;
import java.util.List;


public class MainActivity extends Activity {

    private ImageView backIcon;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private SearchView mSearchView;
    private SmartRefreshLayout smartRefreshLayout;
    static final int PAGE_SIZE = 5;//每次加载10项

    int page = 1;//加载第一页
    private DbOperationUtils dbOperationUtils;
    private List<UserBean> userBeans;
    private View statusBarView;

    private void setStatusBarView() {
        //延时加载数据，保证Statusbar绘制完成后再findview。
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                initStatusBar();
                //不加监听,也能实现改变statusbar颜色的效果。但是会出现问题：比如弹软键盘后,弹popwindow后,引起window状态改变时,statusbar的颜色就会复原.
                getWindow().getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        initStatusBar();
                        // getWindow().getDecorView().removeOnLayoutChangeListener(this);

                    }

                });
                //只走一次
                return false;
            }
        });
    }

    /**
     * 颜色渐变
     */

    private void initStatusBar() {
        if (statusBarView == null) {
            //利用反射机制修改状态栏背景
            int identifier = getResources().getIdentifier("statusBarBackground", "id", "android");
            statusBarView = getWindow().findViewById(identifier);
        }
        if (statusBarView != null) {
            statusBarView.setBackgroundResource(R.drawable.home_shape);
        } else {
            // Log.w( "base","statusBarView is null.");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 21) {
            setStatusBarView();
        }
        backIcon = findViewById(R.id.common_title_back);
        backIcon.setOnClickListener(v -> onKeyDown(KeyEvent.KEYCODE_BACK, null));
        ImageView addIcon = findViewById(R.id.add_img);
        addIcon.setOnClickListener(v -> {
            Intent addIntent = new Intent(this, AddOrUpdateActivity.class);
            startActivity(addIntent);
        });

        mSearchView = findViewById(R.id.searview);
        smartRefreshLayout = findViewById(R.id.refreshLayout);
        userRecyclerView = findViewById(R.id.rv_user);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userRecyclerView.addItemDecoration(new CommonItemDecoration(this, 0, 1));
        userAdapter = new UserAdapter(this);
        userRecyclerView.setAdapter(userAdapter);
        dbOperationUtils = new DbOperationUtils(this);
        userAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent(this, UserDetailActivity.class);
            //用Bundle 携带数组
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) adapter.getItem(position));
            //将 data Intent 添加Bundle
            intent.putExtras(bundle);
            startActivity(intent);

        });
        userAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("是否确定删除")
                        .setConfirmText("确定")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                dbOperationUtils.deleteuser((UserBean) adapter.getItem(position));
                                initData();
                                sweetAlertDialog.cancel();

                            }
                        })
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
                return false;
            }
        });

        userRecyclerView.postDelayed(this::initData, 0);
        //头部刷新样式
//        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(this));
        //尾部刷新样式
//        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            /*重新刷新列表控件的数据*/
            initData();
            smartRefreshLayout.finishRefresh(2000);
        });
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            page++;
            userBeans = dbOperationUtils.queryLikeUser(page, PAGE_SIZE);
            userAdapter.setNewData(userBeans);
            /*重新刷新列表控件的数据*/
            smartRefreshLayout.finishLoadMore(2000);

        });


        //搜索图标按钮的点击事件
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "打开搜索框", Toast.LENGTH_SHORT).show();
            }
        });


        //搜索框内容变化监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//点击提交按钮时
                userBeans = dbOperationUtils.queryLikeUser(query);
                userAdapter.setNewData(userBeans);
                Toast.makeText(MainActivity.this, "Submit---提交", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//搜索框内容变化时
                if (!TextUtils.isEmpty(newText)) {
//              mListView.setFilterText(newText);
//                    mAdapter.getFilter().filter(newText);
                } else {
//                    mListView.clearTextFilter();
                }
                return true;
            }
        });

        //搜索框展开时点击叉叉按钮关闭搜索框的点击事件
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(MainActivity.this, "关闭搜索框", Toast.LENGTH_SHORT).show();
                initData();
                return false;
            }
        });
    }


    private void initData() {
        page = 1;
        userBeans = dbOperationUtils.queryLikeUser(page, PAGE_SIZE);
        userAdapter.setNewData(userBeans);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}