package com.example.administrator.videoplayer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import Util.PixeUtil;

public class MainActivity extends AppCompatActivity {
    private FrameLayout progress_layout;
    private VideoView videoView;
    private LinearLayout controllerbar_layout;
    private ImageView play_controller_img,screen_img,volume_img;
    private TextView time_current_tv,time_total_tv;
    private SeekBar play_seek,volume_seek;
    public static  final  int UPDATE_UI = 1;
  private  int screen_width,screen_height;
  private RelativeLayout videoLayout;
  private AudioManager mAudioManager;
  private boolean isFullScreen = false;
  private boolean isAdjust = false;
  private int threshold = 54;
  private float mBrightness;
  private  ImageView operation_percent,operation_bg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initUi();
        setPlayerEvent();
        //网路播放
        videoView.setVideoURI(Uri.parse( "http://ips.ifeng.com/video19.ifeng.com/video09/2017/05/24/4664192-102-008-1012.mp4"));
        videoView.start();
        UIHandler.sendEmptyMessage(UPDATE_UI);

    }

    private void updateTextViewWithTimeFormat(TextView textView,int millisecond){
        int second = millisecond/1000; //
        int hh = second/3600;  //1小时=3600秒
        int mm =second%3600/60; //一分钟=60秒
        int ss = second%60;  //一秒=60ms
        String str= null;
         if ( hh!=0  )
         {
          str = String.format("%02d:%02d,%02d,hh,mm,ss");  //保持每次都是两位数
         }else {
             str = String.format("%02d:%02d",mm,ss);
         }
         textView.setText(str);
    }
        private Handler UIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if ( msg.what==UPDATE_UI) {
                    //获取视频当前得播放时间
                    int currentPosition = videoView.getCurrentPosition();
                    //获取播放得总时间
                    int totalduration = videoView.getDuration();
                    //格式化视频得播放时间
                    updateTextViewWithTimeFormat(time_current_tv, currentPosition);
                    updateTextViewWithTimeFormat(time_total_tv, totalduration);
                    play_seek.setMax(totalduration);
                    play_seek.setProgress(currentPosition);
                    UIHandler.sendEmptyMessageDelayed(UPDATE_UI, 5000);
                }
            }
        };

    @Override
    protected void onPause() {
        super.onPause();
        UIHandler.removeMessages(UPDATE_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //控制播放器暂停得
    private void setPlayerEvent() {
        //控制视频播放和暂停
        play_controller_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( videoView.isPlaying() ) {
                    play_controller_img.setImageResource(R.drawable.play_btn_style);
                    //暂停播放
                    videoView.pause();
                    UIHandler.sendEmptyMessage(UPDATE_UI);
                } else {
                    play_controller_img.setImageResource(R.drawable.pause_btn_style);
                    //继续播放
                    videoView.start();
                    UIHandler.sendEmptyMessage(UPDATE_UI);
                }
            }
        });
   play_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
       @Override
       public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
           updateTextViewWithTimeFormat(time_current_tv,progress);
       }
       @Override
       public void onStartTrackingTouch(SeekBar seekBar) {
       UIHandler.removeMessages(UPDATE_UI);
       }
       @Override
       public void onStopTrackingTouch(SeekBar seekBar) {
           int progress = seekBar.getProgress();
           //另视频播放进度遵循seekbar停止拖动得这一刻进度
           videoView.seekTo(progress);
           UIHandler.sendEmptyMessage(UPDATE_UI);
       }
   });
        volume_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置当前设备得音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        //横竖屏切换
        screen_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( isFullScreen ) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                }
            }
        });
        //控制VideoView得手势事件
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            //触摸时当前得x轴和y轴的距离
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX();
                float y = event.getY();
                float lastX = 0, lastY = 0;
                switch (event.getAction()) {
                    //手指落下屏幕得那一刻（只会调用一次)
                    case MotionEvent.ACTION_DOWN: {
                        lastX = x;
                        lastY = y;
                        break;
                    }
                    //手指在屏幕上移动（调用多次）
                    case MotionEvent.ACTION_MOVE: {
                        float detlaX = x - lastX;
                        float detlaY = y - lastY;
                        //手指滑动时x轴和y轴得偏移量绝对值
                        float absdetlaX = Math.abs(detlaX);
                        float absdetlaY = Math.abs(detlaY);
                        if ( absdetlaX > threshold && absdetlaY > threshold ) {
                            if ( absdetlaX < absdetlaY ) {
                                isAdjust = true;
                            } else {
                                isAdjust = false;
                            }
                        } else if ( absdetlaX < threshold && absdetlaY > threshold ) {
                            isAdjust = true;
                        } else if ( absdetlaX > threshold && absdetlaY < threshold ) {
                            isAdjust = false;
                        }
                        Log.e("Main", "手势是否合法" + isAdjust);
                        if ( isAdjust ) {
                            //判断好当前手势事件已经合法得前提下，在区分此时手势应该调节亮度还是调节声音
                            if ( x < screen_width / 2 ) {
                                //调节亮度
                                if ( detlaY > 0 ) {
                                    //降低亮度
                                    Log.e("Main", "降低亮度" + detlaY);
                                } else {
                                    //升高亮度
                                    Log.e("Main", "升高亮度" + detlaY);
                                }
                                changeBrightness(-detlaY);
                            } else {
                                //调节声音
                                if ( detlaY > 0 ) {
                                    //减小声音
                                    Log.e("Main", "减小声音" + detlaY);
                                } else {
                                    //增大声音
                                    Log.e("Main", "增大声音" + detlaY);
                                }
                              changeVolume(-detlaY);
                            }
                        }
                        lastX = x;
                        lastY = y;
                        break;
                    }
                    //手指离开屏幕得那一刻调用一次
                    case MotionEvent.ACTION_UP: {
                        progress_layout.setVisibility(View.GONE);//手机离开屏幕隐藏图标声音亮度
                        break;
                    }
                }
                return true;
            }
        });
    }

//手势触摸得音量
    private void changeVolume(float detalaY){
        //获取最大音量
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获取当前得声音
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index = (int)(detalaY/screen_height*max*3);
        int volume = Math.max(current+index+index,0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);
       if ( progress_layout.getVisibility()==View.GONE){
           progress_layout.setVisibility(View.VISIBLE);
       }
        progress_layout.setVisibility(View.VISIBLE);
        operation_bg.setImageResource(R.mipmap.video_volumn_bg);
        ViewGroup.LayoutParams layoutParams = operation_percent.getLayoutParams();
        layoutParams.width = (int) (PixeUtil.dp2px(94)*(float)volume/max);
        operation_percent.setLayoutParams(layoutParams);
        volume_seek.setProgress(volume);
    }

    //手势触摸得亮度调节
    private void changeBrightness (float detlaY){
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        mBrightness = attributes.screenBrightness;
        float index = detlaY/screen_height/3;
        mBrightness+=index;
        if ( mBrightness>1.0f ){
            mBrightness=1.0f;
        }
        if ( mBrightness<0.01f ){
            mBrightness = 0.01f;
        }
        attributes.screenBrightness = mBrightness;

        if ( progress_layout.getVisibility()==View.GONE){
            progress_layout.setVisibility(View.VISIBLE);
        }
        progress_layout.setVisibility(View.VISIBLE);
        operation_bg.setImageResource(R.mipmap.video_brightness_bg);
        ViewGroup.LayoutParams layoutParams = operation_percent.getLayoutParams();
        layoutParams.width = (int) (PixeUtil.dp2px(94)*mBrightness);
        operation_percent.setLayoutParams(layoutParams);
        getWindow().setAttributes(attributes);

    }


    // 初始化UI布局
    private void initUi() {
        progress_layout = findViewById(R.id.progress_layout);
        videoView = (VideoView) findViewById(R.id.videoview);
        controllerbar_layout = (LinearLayout) findViewById(R.id.controllerbar_layout);
        play_controller_img = (ImageView) findViewById(R.id.pause_img);
        screen_img = (ImageView) findViewById(R.id.screen_img);
        time_current_tv = (TextView) findViewById(R.id.time_current_tv);
        time_total_tv = (TextView) findViewById(R.id.time_total_tv);
        play_seek = (SeekBar) findViewById(R.id.play_seek);
        volume_seek = (SeekBar) findViewById(R.id.volume_seek);
        screen_width = getResources().getDisplayMetrics().widthPixels;
        screen_height = getResources().getDisplayMetrics().heightPixels;
        videoLayout = findViewById(R.id.videoLayout);
        volume_img = findViewById(R.id.volume_img);
        operation_bg = findViewById(R.id.operation_bg);
        operation_percent = findViewById(R.id.operation_percent);
        PixeUtil.initContext(this);
        //当前设备得最大音量
        int streamMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获取设备当前得音量
        int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume_seek.setMax(streamMaxVolume);
        volume_seek.setProgress(streamVolume);

    }

    private void setVideoViewScale(int width, int height){
        ViewGroup.LayoutParams layoutParams = videoView.getLayoutParams();
        layoutParams.width=width;
        layoutParams.height=height;
        videoView.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams1 =videoLayout.getLayoutParams();
        layoutParams1.width=width;
        layoutParams1.height=height;
        videoLayout.setLayoutParams(layoutParams1);

    }




//监听屏幕方向改变
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //当屏幕方向为横屏得时候
        if ( getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE )
        {
              setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
              volume_img.setVisibility(View.VISIBLE);
              volume_seek.setVisibility(View.VISIBLE);
              isFullScreen = true;
              getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
              //设置全屏
              getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }//当屏幕方向为竖屏得时候
        else{
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, PixeUtil.dp2px(240));
            volume_img.setVisibility(View.GONE);
            volume_seek.setVisibility(View.GONE);
            isFullScreen = false;
            //设置全屏
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        }
}
}
