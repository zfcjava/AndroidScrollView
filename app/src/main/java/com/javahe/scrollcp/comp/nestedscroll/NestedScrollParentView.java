package com.javahe.scrollcp.comp.nestedscroll;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParentHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.support.v4.view.NestedScrollingParent;

/**
 * Created by zfc on 2018/5/24.
 */

public class NestedScrollParentView extends LinearLayout implements NestedScrollingParent {

    private NestedScrollingParentHelper nestedScrollingParentHelper;

    private View imageView;
    private View textView;
    private int imageHeight;
    private int textViewHeight;
    private NestedScrollChildView myNestedScrollChild;

    public NestedScrollParentView(Context context) {
        this(context, null);
    }

    public NestedScrollParentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NestedScrollParentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //传入需要滚动的parent
        nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageView = getChildAt(0);
        textView = getChildAt(1);
        myNestedScrollChild = (NestedScrollChildView) getChildAt(2);

        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (imageHeight < 0) {
                    imageHeight = imageView.getMeasuredHeight();
                }
            }
        });

        textView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (textViewHeight < 0) {
                    textViewHeight = textView.getMeasuredHeight();
                }
            }
        });
    }


    //是否需要混合滚动，true；可以混合滚动
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (child instanceof NestedScrollChildView) {
            return true;
        }
        return false;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        nestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }


    @Override
    public void onStopNestedScroll(View child) {
        nestedScrollingParentHelper.onStopNestedScroll(child);
    }


    //先于child进行滚动，前3个是入参数，最后一个输出参数
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if(showImage(dy)||hideImg(dy)){
            scrollBy(0, -dy);
            consumed[1] = dy; //告诉child消耗了多少
        }
        super.onNestedPreScroll(target, dx, dy, consumed);
    }

    //后于child滚动
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }

    //是否消耗了Filing时间
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }


    @Override
    public int getNestedScrollAxes() {
        return nestedScrollingParentHelper.getNestedScrollAxes();
    }

    //下拉的时候是否要向下滚动以显示图片
    public boolean showImage(int dy) {
        if (dy > 0) {
            if (getScrollY() > 0 && myNestedScrollChild.getScrollY() == 0) {
                return true;
            }
        }

        return false;
    }

    //上拉的时候，是否要向上滚动，隐藏图片
    public boolean hideImg(int dy) {
        if (dy < 0) {
            if (getScrollY() < imageHeight) {
                return true;
            }
        }
        return false;
    }


    //scrollBy内部会调用scrollTo
    //限制滚动范围
    @Override
    public void scrollTo(int x, int y) {
        if (y < 0) {
            y = 0;
        }
        if (y > imageHeight) {
            y = imageHeight;
        }

        super.scrollTo(x, y);
    }



}
