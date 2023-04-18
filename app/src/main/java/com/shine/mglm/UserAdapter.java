package com.shine.mglm;


import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;


public class UserAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    private Context context;

    public UserAdapter(Context context) {
        super(R.layout.item_user);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean userBean) {
        helper.setText(R.id.name, String.format(context.getResources().getString(R.string.user_name), userBean.name))
                .setText(R.id.total_amount, String.format(context.getResources().getString(R.string.total_amount), userBean.total_amount))
                .setText(R.id.user_phone, String.format(context.getResources().getString(R.string.phone), userBean.phone))
                .setText(R.id.user_times, String.format(context.getResources().getString(R.string.times), userBean.times))
                .setText(R.id.last_time, String.format(context.getResources().getString(R.string.last_time), userBean.last_time))
                .setText(R.id.rebate, String.format(context.getResources().getString(R.string.rebate), userBean.rebate));

    }
}
