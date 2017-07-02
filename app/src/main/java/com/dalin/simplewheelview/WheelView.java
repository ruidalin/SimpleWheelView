package com.dalin.simplewheelview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thinker on 2017/7/1.
 */

public class WheelView extends ScrollView {

    public WheelView(Context context) {
        this(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WheelView);
        offset = a.getInteger(R.styleable.WheelView_wheelOffset, 2);
        lineColor = a.getColor(R.styleable.WheelView_lineColor, lineColor);
        lineStroke = a.getDimensionPixelSize(R.styleable.WheelView_lineStroke, 1);
        linePadding = a.getDimensionPixelSize(R.styleable.WheelView_linePadding, 0);
        itemTextColor = a.getColor(R.styleable.WheelView_itemTextColor, itemTextColor);
        itemTextSize = a.getDimensionPixelSize(R.styleable.WheelView_itemTextSize, 18);
        itemTextPadding = a.getDimensionPixelOffset(R.styleable.WheelView_itemTextPadding, 0);
        itemTextSelectColor = a.getColor(R.styleable.WheelView_itemTextSelectColor, Color.BLACK);
        itemTextSelectScale = a.getFloat(R.styleable.WheelView_itemTextSelectScale, 1.2f);
        a.recycle();
        init();
    }

    private int offset = 2;//
    private int lineColor = Color.parseColor("#cc333333");//横线的颜色
    private int lineStroke = 1;//横线的宽度
    private int linePadding = 0;//横线之间相对的偏移量
    private int itemTextColor = Color.parseColor("#66333333");//字体的颜色
    private int itemTextSize = 18;//字体的大小
    private int itemTextPadding = 0;//字体的内上下边距
    private int itemTextSelectColor = Color.BLACK;//选中字体的颜色
    private float itemTextSelectScale = 1.2f;//选中字体的缩放大小


    private Context mContext;
    private List<String> items = new ArrayList<String>();
    private LinearLayout mLayout;
    private int mSelectedIndex;
    private int mItemViewHeight;
    private boolean mIsScrollNext;


    private void init() {
        mContext = this.getContext();
        mLayout = new LinearLayout(mContext);
        ViewGroup.LayoutParams layoutParams = mLayout.getLayoutParams();
        if (null == layoutParams) layoutParams = new LinearLayout.LayoutParams(0, 0);
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mLayout.setLayoutParams(layoutParams);
        mLayout.setOrientation(LinearLayout.VERTICAL);
        this.addView(mLayout);
        this.setItems(new ArrayList<String>(0));
        this.setVerticalScrollBarEnabled(false);
        this.setBackgroundDrawable(null);
    }

    public int getOffset() {
        return this.offset;
    }

    public WheelView setOffset(int offset) {
        if (offset < 0) offset = 0;
        this.offset = offset;
        return this;
    }

    public WheelView setItems(@Nullable List<String> items) {
        if (null == items) throw new RuntimeException("set items is null");
        this.items.clear();
        this.items.addAll(items);
        this.mLayout.removeAllViews();
        //头部和底部的空白显示
        for (int i = 0; i < offset; i++) {
            this.items.add(0, "");//add at top
            this.items.add("");//add at last
        }
        for (String item : this.items) {
            this.mLayout.addView(createItemView(item));
        }
        this.setSelectedText();
        return this;
    }

    protected View createItemView(String item) {
        TextView itemView = new TextView(mContext);
        itemView.setText(item);
        itemView.setTextSize(itemTextSize);
        itemView.setTextColor(itemTextColor);
        itemView.setPadding(0, itemTextPadding, 0, itemTextPadding);
        itemView.setGravity(Gravity.CENTER);
        return itemView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View childView = mLayout.getChildAt(0);
        childView.measure(0, 0);//计算childView宽高, 否则为0
        mItemViewHeight = childView.getMeasuredHeight();
        int viewHeight = (int) (mItemViewHeight * offset * 2 +
                mItemViewHeight * itemTextSelectScale +
                getPaddingTop() + getPaddingBottom());
        viewHeight = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, viewHeight);//通过计算显示的个数设置View的高度
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //嵌套ScrollView取消拦截事件
        this.requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP: {
                if (mIsScrollNext) scrollNext();
                else scrollPreview();
                break;
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //判断是否滑动到下一个
        mIsScrollNext = t > oldt;
        setSelectedText();
    }

    private void setSelectedText() {
        mSelectedIndex = (int) (getScrollY() * 1.0f / mItemViewHeight + 0.5f) + offset;

        for (int i = 0; i < this.items.size(); i++) {
            TextView itemView = (TextView) mLayout.getChildAt(i);
            if (i == mSelectedIndex) {//选中的放大itemTextSelectScale 倍
                itemView.setTextColor(itemTextSelectColor);
                itemView.setTextSize(itemTextSize * itemTextSelectScale);
            } else {
                itemView.setTextColor(itemTextColor);
                itemView.setTextSize(itemTextSize);
            }

        }
    }


    private void scrollPreview() {
        int preY = getScrollY() / mItemViewHeight * mItemViewHeight;
        postSmoothScrollTo(0, preY);
    }

    private void scrollNext() {
        int nextY = getScrollY() / mItemViewHeight * mItemViewHeight + mItemViewHeight;
        postSmoothScrollTo(0, nextY);
    }

    public String getSelected() {
        return this.items.get(getSelectedIndex() + offset);
    }

    public WheelView setSelected(int index) {
        if (index < 0) index = 0;
        if (index > items.size() - 1 - 2 * offset)
            index = items.size() - 1;
        mSelectedIndex = index;
        postSmoothScrollTo(0, mSelectedIndex * mItemViewHeight);
        return this;
    }

    public int getSelectedIndex() {
        int cruuentScrollY = getScrollY() / mItemViewHeight * mItemViewHeight;
        postSmoothScrollTo(0, cruuentScrollY);
        return mSelectedIndex = cruuentScrollY / mItemViewHeight;
    }


    @Override
    public void fling(int velocityY) {
        //降低fling速度
        super.fling(velocityY / 5);
    }

    //动画移动
    private void postSmoothScrollTo(final int x, final int y) {
        this.post(new Runnable() {
            @Override
            public void run() {
                smoothScrollTo(x, y);
            }
        });
    }

    private Paint paint;
    private int viewWidth;

    @Override//画选中的横线
    public void setBackgroundDrawable(Drawable background) {
        viewWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
        paint = new Paint();
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineStroke);
        background = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {

                canvas.drawLine(0, mItemViewHeight * offset - linePadding,
                        viewWidth, mItemViewHeight * offset - linePadding, paint);
                canvas.drawLine(0, mItemViewHeight * offset + mItemViewHeight * itemTextSelectScale + linePadding,
                        viewWidth, mItemViewHeight * offset + mItemViewHeight * itemTextSelectScale + linePadding, paint);
            }

            @Override
            public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {

            }

            @Override
            public void setColorFilter(@Nullable ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        super.setBackgroundDrawable(background);
    }
}
