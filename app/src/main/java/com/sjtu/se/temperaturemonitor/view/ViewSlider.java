package com.sjtu.se.temperaturemonitor.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xinywu on 2017/11/3.
 */

public class ViewSlider extends ViewGroup {
    private ViewDragHelper dragHelper;
    private View historyChart;
    private View summaryData;
    private View dailyChart;
    private int historyChartHeight;
    private int summaryDataHeight;
    private int dailyChartHeight;
    private int width;
    private boolean isUp;

    public ViewSlider(Context context) {
        super(context);
    }

    public ViewSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        isUp = true;
        historyChart = getChildAt(0);
        summaryData = getChildAt(1);
        dailyChart = getChildAt(2);
        dragHelper = ViewDragHelper.create(this, new DragHelper());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        summaryData.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(462, MeasureSpec.EXACTLY));
        int heightMeasure =  MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) - summaryData.getMeasuredHeight(), MeasureSpec.EXACTLY);
        historyChart.measure(widthMeasureSpec, heightMeasure);
        dailyChart.measure(widthMeasureSpec, heightMeasure);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        width = historyChart.getMeasuredWidth();
        historyChartHeight = historyChart.getMeasuredHeight();
        historyChart.layout(0, 0, width, historyChartHeight);
        summaryDataHeight = summaryData.getMeasuredHeight();
        summaryData.layout(0, historyChartHeight, width, summaryDataHeight + historyChartHeight);
        dailyChartHeight = dailyChart.getMeasuredHeight();
        dailyChart.layout(0, historyChartHeight + summaryDataHeight, width, dailyChartHeight + historyChartHeight + summaryDataHeight);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    class DragHelper extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return historyChart == child || summaryData == child || dailyChart == child;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return 0;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            if(child == historyChart) {
                if(top > 0) {
                    return 0;
                }
                else if(-top > dailyChartHeight) {
                    return -dailyChartHeight;
                }
            }
            else if(child == summaryData) {
                if(top > historyChartHeight) {
                    return historyChartHeight;
                }
                else if (top < 0) {
                    return 0;
                }
            }
            else if(child == dailyChart) {
                if(top > historyChartHeight + summaryDataHeight) {
                    return historyChartHeight + summaryDataHeight;
                }
                else if (top < summaryDataHeight) {
                    return summaryDataHeight;
                }
            }
            return top;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            invalidate();
            if(changedView == historyChart) {
                summaryData.layout(0, historyChartHeight + top, width, historyChartHeight + top + summaryDataHeight);
                dailyChart.layout(0, historyChartHeight + top + summaryDataHeight,
                        width, historyChartHeight + top + summaryDataHeight + dailyChartHeight);
            }
            else if(changedView == summaryData) {
                historyChart.layout(0, top - historyChartHeight, width, top);
                dailyChart.layout(0, top + summaryDataHeight, width, top + summaryDataHeight + dailyChartHeight);
            }
            else if(changedView == dailyChart) {
                historyChart.layout(0, top - historyChartHeight - summaryDataHeight, width, top - summaryDataHeight);
                summaryData.layout(0, top - summaryDataHeight, width, top);
            }
        }

        @Override
        public void onViewReleased(View releaseChild, float x, float y) {
            int sumDataPos = summaryData.getTop();
            if(isUp){
                if(sumDataPos < 2/3f*historyChartHeight) {
                    isShowHistory(false);
                }
                else {
                    isShowHistory(true);
                }
            }
            else {
                if(sumDataPos > 1/3f*historyChartHeight) {
                    isShowHistory(true);
                }
                else{
                    isShowHistory(false);
                }
            }
            super.onViewReleased(releaseChild, x, y);
        }
    }

    private void isShowHistory(boolean isShowHistory) {
        isUp = isShowHistory;
        if(isShowHistory) {
            dragHelper.smoothSlideViewTo(historyChart, 0, 0);
            dragHelper.smoothSlideViewTo(summaryData, 0, historyChartHeight);
            dragHelper.smoothSlideViewTo(dailyChart, 0, historyChartHeight + summaryDataHeight);
        }
        else {
            dragHelper.smoothSlideViewTo(historyChart, 0, -historyChartHeight);
            dragHelper.smoothSlideViewTo(summaryData, 0, 0);
            dragHelper.smoothSlideViewTo(dailyChart, 0, summaryDataHeight);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        if(dragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
