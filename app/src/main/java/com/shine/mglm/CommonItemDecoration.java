package com.shine.mglm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by zdd on 2019/8/7.
 * <p>
 * Description:
 */
public class CommonItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable color;
    private int diffTop, diffBottom;

    public CommonItemDecoration(Context context, int diffTop, int diffBottom) {
        color = context.getResources().getDrawable(R.drawable.common_item_separate_dp);
        this.diffTop = diffTop;
        this.diffBottom = diffBottom;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = 0;
        int right = parent.getWidth();
        int childCount = parent.getChildCount();
        for (int i = diffTop; i < childCount - diffBottom; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
            int bottom = top + color.getIntrinsicHeight();
            color.setBounds(left, top, right, bottom);
            color.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, color.getIntrinsicHeight());
    }
}
