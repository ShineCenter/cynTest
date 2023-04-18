package com.shine.mglm;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class DetailTimeAdapter extends BaseQuickAdapter<Times, BaseViewHolder> {
    private Context context;

    public DetailTimeAdapter(Context context) {
        super(R.layout.item_detail);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, Times item) {
        helper.setText(R.id.tv_time, item.time);
    }
}
