package com.example.administrator.videoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/10/31.
 */

public class CustomVideoView extends VideoView{
    int defaultwidth = 1920;
    int defaultHeight = 1000;

    public CustomVideoView(Context context) {
        super(context);
    }

    public CustomVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    int width = getDefaultSize(defaultwidth,widthMeasureSpec);
    int height = getDefaultSize(defaultHeight,heightMeasureSpec);
    setMeasuredDimension(width,height);
    }
}
