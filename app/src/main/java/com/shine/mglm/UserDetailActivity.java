package com.shine.mglm;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserDetailActivity extends Activity {

    private DetailTimeAdapter adapter;
    private UserBean userBean;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        if (Build.VERSION.SDK_INT >= 21) {
            setStatusBarView();
        }
        Intent intent = this.getIntent();
        userBean = (UserBean) intent.getSerializableExtra("data");

        ImageView backIcon = findViewById(R.id.common_title_back);
        backIcon.setOnClickListener(v -> finish());


        ImageView updateIcon = findViewById(R.id.update_img);
        updateIcon.setOnClickListener(v -> {
            Intent updateIntent = new Intent(this, AddOrUpdateActivity.class);
            updateIntent.putExtra("user", userBean);
            startActivity(updateIntent);
            finish();
        });


        TextView userId = findViewById(R.id.user_id);
        TextView total_amount = findViewById(R.id.total_amount);
        TextView current_amount = findViewById(R.id.user_amount);
        TextView user_phone = findViewById(R.id.user_phone);
        TextView last_time = findViewById(R.id.last_time);
        TextView user_times = findViewById(R.id.user_times);
        TextView tv_rebate = findViewById(R.id.tv_rebate);
        TextView name = findViewById(R.id.name);
        RecyclerView rv_time = findViewById(R.id.rv_times);

        userId.setText(String.format(getResources().getString(R.string.id), userBean.userId.toString()));
        total_amount.setText(String.format(getResources().getString(R.string.total_amount), userBean.total_amount));
        current_amount.setText(String.format(getResources().getString(R.string.amount), userBean.current_amount));
        user_phone.setText(String.format(getResources().getString(R.string.phone), userBean.phone));
        last_time.setText(String.format(getResources().getString(R.string.last_time), userBean.last_time));
        user_times.setText(String.format(getResources().getString(R.string.times), userBean.times));
        tv_rebate.setText(String.format(getResources().getString(R.string.rebate), userBean.rebate));
        name.setText(String.format(getResources().getString(R.string.user_name), userBean.name));


        rv_time.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DetailTimeAdapter(this);
        rv_time.setAdapter(adapter);


        handlerTimes();
    }

    private void handlerTimes() {
        String arrivalTimes = userBean.arrivalTimes;
        List<Times> timesList = new ArrayList<>();
        if (!TextUtils.isEmpty(arrivalTimes)) {
            String[] split = arrivalTimes.split(",");
            if (split.length > 0) {

                for (String s : split) {
                    Times times = new Times(s);
                    timesList.add(times);
                }
            }
            adapter.setNewData(timesList);
        }
    }
}