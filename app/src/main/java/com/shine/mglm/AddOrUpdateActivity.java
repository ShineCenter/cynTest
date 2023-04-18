package com.shine.mglm;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.Group;

import com.cazaea.sweetalert.SweetAlertDialog;
import com.shine.mglm.util.DbOperationUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.currentTimeMillis;

public class AddOrUpdateActivity extends Activity implements View.OnClickListener {

    private UserBean oldUser;
    private boolean isAdd = true;
    private Group constraintGroup;
    private EditText etName;
    private EditText current_amount;
    private EditText etPhone;
    private EditText etOriginal;
    private DbOperationUtils dbOperationUtils;
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
        setContentView(R.layout.activity_add_or_update);

        if (Build.VERSION.SDK_INT >= 21) {
            setStatusBarView();
        }
        Intent intent = this.getIntent();
        oldUser = (UserBean) intent.getSerializableExtra("user");
        TextView title = findViewById(R.id.common_title_content);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etOriginal = findViewById(R.id.et_original);
        current_amount = findViewById(R.id.et_user_amount);
        Button addBtn = findViewById(R.id.btn_add);
        Button changeBtn = findViewById(R.id.btn_change);
        Button changeNoBtn = findViewById(R.id.btn_change_no);
        constraintGroup = findViewById(R.id.constraintGroup);
        ImageView backIcon = findViewById(R.id.common_title_back);
        backIcon.setOnClickListener(v -> finish());
        if (oldUser != null) {
            title.setText("修改用户");
            isAdd = false;
            etName.setText(oldUser.getName());
            etPhone.setText(oldUser.getPhone());
            etOriginal.setText(oldUser.getCurrent_amount());
        }

        if (isAdd) {
            addBtn.setVisibility(View.VISIBLE);
            constraintGroup.setVisibility(View.GONE);
        } else {

            addBtn.setVisibility(View.GONE);
            constraintGroup.setVisibility(View.VISIBLE);
        }


        addBtn.setOnClickListener(this);
        changeBtn.setOnClickListener(this);
        changeNoBtn.setOnClickListener(this);
        dbOperationUtils = new DbOperationUtils(this);
    }

    @Override
    public void onClick(View v) {
        String currDate = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒 EEEE").format(currentTimeMillis());
        UserBean userBean = new UserBean();
        switch (v.getId()) {
            case R.id.btn_add:
                boolean isPhoneNum = isMobilPhone(etPhone.getText().toString().trim());
                if (TextUtils.isEmpty(etPhone.getText().toString().trim())) {
                    Toast.makeText(AddOrUpdateActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isPhoneNum) {
                    etPhone.setText("");
                    Toast.makeText(AddOrUpdateActivity.this, "请输入有效的手机号码！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(etName.getText().toString().trim())) {
                    Toast.makeText(AddOrUpdateActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(current_amount.getText().toString().trim())) {
                    Toast.makeText(AddOrUpdateActivity.this, "消费金额不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }


                userBean.setName(etName.getText().toString().trim());
                userBean.setPhone(etPhone.getText().toString().trim());
                userBean.setCurrent_amount(current_amount.getText().toString().trim());
                userBean.setLast_time(currDate);
                userBean.setRebate("0");
                userBean.setTimes("1");
                userBean.setArrivalTimes(currDate);
                userBean.setTotal_amount(current_amount.getText().toString().trim());
                boolean insertuser = dbOperationUtils.insertuser(userBean);
                if (insertuser) {
                    Toast.makeText(AddOrUpdateActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.btn_change:
                new SweetAlertDialog(AddOrUpdateActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("是否确定返利修改")
                        .setConfirmText("确定")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                updateUser(currDate, userBean, true);
                                sweetAlertDialog.cancel();

                            }
                        })
                        .setCancelText("取消")
                        .setCancelClickListener(sweetAlertDialog -> sweetAlertDialog.dismiss())
                        .show();

                break;
            case R.id.btn_change_no:
                new SweetAlertDialog(AddOrUpdateActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("提示")
                        .setContentText("是否确定不返利")
                        .setConfirmText("确定")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            updateUser(currDate, userBean, false);
                            sweetAlertDialog.cancel();

                        })
                        .setCancelText("取消")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();
                break;
        }

    }

    public void updateUser(String currDate, UserBean userBean, boolean flag) {
        StringBuilder arrivalTimes = new StringBuilder();
        userBean.setUserId(oldUser.getUserId());
        userBean.setName(etName.getText().toString().trim());
        userBean.setPhone(etPhone.getText().toString().trim());
        userBean.setTotal_amount(addMoney(new BigDecimal(oldUser.getTotal_amount()), new BigDecimal(current_amount.getText().toString().trim()).abs()));
        userBean.setLast_time(currDate);
        BigDecimal b1 = new BigDecimal(oldUser.current_amount);
        BigDecimal b2;
        if (flag) {
            b2 = new BigDecimal(current_amount.getText().toString().trim());
            userBean.setRebate(add(oldUser.rebate, 1));
            userBean.setCurrent_amount(subtractMoney(b1, b2));

        } else {
            b2 = new BigDecimal(current_amount.getText().toString().trim()).abs();
            userBean.setRebate(oldUser.getRebate());
            userBean.setCurrent_amount(addMoney(b1, b2));

        }
        userBean.setTimes(add(oldUser.times, 1));
        arrivalTimes.append(oldUser.getArrivalTimes()).append(",").append(currDate);
        userBean.setArrivalTimes(arrivalTimes.toString());

        boolean updateuser = dbOperationUtils.updateuser(userBean);
        if (updateuser) {
            Toast.makeText(AddOrUpdateActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public static boolean isMobilPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$";
        if (phone.length() != 11) {
            return false;
        } else {
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(phone);
            boolean isMatch = m.matches();
            return isMatch;
        }
    }

    public String add(String oldVal, int nuwVal) {
        int i = Integer.parseInt(oldVal) + nuwVal;
        return String.valueOf(i);
    }

    public String addMoney(BigDecimal oldVal, BigDecimal nuwVal) {
        BigDecimal add = oldVal.add(nuwVal);
        return add.toString();
    }
    public String subtractMoney(BigDecimal oldVal, BigDecimal nuwVal) {
        BigDecimal subtract = oldVal.add(nuwVal).subtract(BigDecimal.valueOf(200));
        return subtract.toString();
    }
}