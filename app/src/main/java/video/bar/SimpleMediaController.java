package video.bar;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import video.widget.BDCloudVideoView;
import zheng.androidkits.R;

/**
 * Created by zf on 2018/8/16.
 */
public class SimpleMediaController extends RelativeLayout {

    private static final String TAG = "MediaController";

    private ImageButton playButton;

    private TextView positionView;
    private SeekBar seekBar;
    private TextView durationView;

    private Timer positionTimer;
    private static final int POSITION_REFRESH_TIME = 1000;

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public Handler getMainThreadHandler() {
        return mainThreadHandler;
    }

    private BDCloudVideoView mVideoView = null;

    private ControllerCallBack mControllerCallBack;

    static boolean mbIsDragging = false;
    static boolean mbIsCaching = false;

    public static String TV_AIRPLAY = "tv_airplay";

    public static String TV_ANDROID_USB = "tv_android_usb";

    public static String TV_ANDROID_WIFI = "tv_android_wifi";

    private String tv_type;

    private static int playRate;

    private static int playPosition;

    private static int playDuration;

    public void setControllerCallBack(ControllerCallBack controllerCallBack) {
        this.mControllerCallBack = controllerCallBack;
    }

    public SimpleMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI();
    }

    public SimpleMediaController(Context context) {
        super(context);
        initUI();
    }

    public void play() {
        Log.d(TAG, "play");
        if (mVideoView != null) {
            Log.d(TAG, "mVideoView start");
            mVideoView.start();
        }
    }

    public void pause() {
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    private void initUI() {
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.bar_simple_media_controller, this);

        playButton = (ImageButton) layout.findViewById(R.id.btn_play);
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView == null) {
                    Log.d(TAG, "playButton checkstatus changed, but bindView=null");
                } else {
                    if (mVideoView.isPlaying()) {
                        Log.v(TAG, "playButton: Will invoke pause()");
                        playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                        mVideoView.pause();
                    } else {
                        Log.v(TAG, "playButton: Will invoke resume()");
                        playButton.setBackgroundResource(R.drawable.toggle_btn_pause);
                        mVideoView.start();
                    }
                }
            }
        });

        positionView = (TextView) layout.findViewById(R.id.tv_position);
        seekBar = (SeekBar) layout.findViewById(R.id.seekbar);
        seekBar.setMax(0);
        durationView = (TextView) layout.findViewById(R.id.tv_duration);

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updatePostion(progress);
				/*
                if (mControllerCallBack != null && fromUser) {
                    mControllerCallBack.onUpdatePosition();
                }*/
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                mbIsDragging = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mVideoView.getDuration() > 0) {
				    currentPositionInMilliSeconds = seekBar.getProgress();
                    previousPosition = seekBar.getProgress();
                    if (mVideoView != null) {
                        mVideoView.seekTo(seekBar.getProgress());
                    }
					/*
                    if (mControllerCallBack != null) {
                        mControllerCallBack.onUpdatePositionStop();
                    }*/
                }
                mbIsDragging = false;
            }
        });
        enableControllerBar(false);
    }

    public void changeState() {
        final BDCloudVideoView.PlayerState status = mVideoView.getCurrentPlayerState();
        Log.v(TAG, "mediaController: changeStatus=" + status.name());
        isMaxSetted = false;
        mainThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                if (status == BDCloudVideoView.PlayerState.STATE_IDLE || status == BDCloudVideoView.PlayerState.STATE_ERROR) {
                    stopPositionTimer();
                    playButton.setEnabled(true);
//                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
//                    seekBar.setEnabled(false);
//                    updatePostion(mVideoView == null ? 0 : mVideoView.getCurrentPosition());
//                    updateDuration(mVideoView == null ? 0 : mVideoView.getDuration());
                } else if (status == BDCloudVideoView.PlayerState.STATE_PREPARING) {
                    playButton.setEnabled(false);
//                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                    seekBar.setEnabled(false);
                } else if (status == BDCloudVideoView.PlayerState.STATE_PREPARED) {
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                    seekBar.setEnabled(true);
                    updateDuration(mVideoView == null ? 0 : mVideoView.getDuration());
                    seekBar.setMax(mVideoView.getDuration());
                    playDuration = mVideoView.getDuration() / 1000;
                    if (mVideoView.getDuration() > 0) {
                    }
                } else if (status == BDCloudVideoView.PlayerState.STATE_PLAYBACK_COMPLETED) {
                    stopPositionTimer();
                    seekBar.setProgress(seekBar.getMax());
                    seekBar.setEnabled(false);
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                } else if (status == BDCloudVideoView.PlayerState.STATE_PLAYING) {
                    //Log.v(TAG, "STATE_PLAYING currentPositionInMilliSeconds " + currentPositionInMilliSeconds);
                    startPositionTimer();
                    seekBar.setEnabled(true);
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_pause);
                    playRate = 1;
                } else if (status == BDCloudVideoView.PlayerState.STATE_PAUSED) {
                    playButton.setEnabled(true);
                    playButton.setBackgroundResource(R.drawable.toggle_btn_play);
                    playRate = 0;
                } else if (status == BDCloudVideoView.PlayerState.STATE_PLAY_CACHEING) {
                    mbIsCaching = true;
                    previousPosition = currentPositionInMilliSeconds;
                }
                /*else if (status == BDCloudVideoView.PlayerState.STATE_PLAY_CACHEING_END) {
                    mbIsCaching = false;
                    if (mVideoView != null) {
                        if (!mVideoView.isPlaying()) {
                            mVideoView.start();
                        }
                    }
                    if ((mVideoView.getDuration() - (int) currentPositionInMilliSeconds) > 3000) {
                        Log.d(TAG, "previousPosition--->" + previousPosition);
                        Log.d(TAG, "mVideoView.getCurrentPosition()--->" + mVideoView.getCurrentPosition());
                        if (previousPosition > mVideoView.getCurrentPosition()) {
                            Log.d(TAG, "执行重试操作changeState");
//                            seek((int) previousPosition);
                        }
                    }
                }*/
            }
        });
    }

    private void startPositionTimer() {
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
        positionTimer = new Timer();
        positionTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPositionUpdate();
                    }
                });
            }
        }, 0, POSITION_REFRESH_TIME);
    }

    private void stopPositionTimer() {
        if (positionTimer != null) {
            positionTimer.cancel();
            positionTimer = null;
        }
    }

    public void setMediaPlayerControl(BDCloudVideoView player) {
        mVideoView = player;
    }

    public void show() {
        if (mVideoView == null) {
            return;
        }
        setProgress((int) currentPositionInMilliSeconds);
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    public boolean getIsDragging() {
        return mbIsDragging;
    }

    private void updateDuration(int milliSecond) {
        if (durationView != null) {
            durationView.setText(formatMilliSecond(milliSecond));
        }
    }

    private void updatePostion(int milliSecond) {
        if (positionView != null) {
            positionView.setText(formatMilliSecond(milliSecond));
        }
    }

    private String formatMilliSecond(int milliSecond) {
        int second = milliSecond / 1000;
        int hh = second / 3600;
        int mm = second % 3600 / 60;
        int ss = second % 60;
        String strTemp = null;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }

    private boolean isMaxSetted = false;

    public void setMax(int maxProgress) {
        if (isMaxSetted) {
            return;
        }
        if (seekBar != null) {
            seekBar.setMax(maxProgress);
        }
        updateDuration(maxProgress);
        if (maxProgress > 0) {
            isMaxSetted = true;
        }
    }

    public void setProgress(int progress) {
        if (seekBar != null) {
            seekBar.setProgress(progress);
        }
    }

    public void setCache(int cache) {
        if (seekBar != null && cache != seekBar.getSecondaryProgress()) {
            seekBar.setSecondaryProgress(cache);
        }
    }

    public void enableControllerBar(boolean isEnable) {
        seekBar.setEnabled(isEnable);
        playButton.setEnabled(isEnable);
    }

    public static long currentPositionInMilliSeconds = 0L; //当前播放进度
    private long previousPosition;
    private boolean replaySeek;

    public boolean onPositionUpdate() {
        if (mVideoView == null) {
            return false;
        }
        long newPositionInMilliSeconds = mVideoView.getCurrentPosition();

        previousPosition = currentPositionInMilliSeconds;

        Log.d(TAG, "onPositionUpdate newPositionInMilliSeconds " + newPositionInMilliSeconds);
        Log.d(TAG, "onPositionUpdate previousPosition " + previousPosition);

        long speed = mVideoView.getDownloadSpeed();
//        Log.d(TAG, "DownloadSpeed " + speed / 1000);

        if (previousPosition > newPositionInMilliSeconds) {
            if (!replaySeek) {
                Log.d(TAG, "执行重试操作");
                replaySeek = true;
//                seek((int) previousPosition);
                return false;
            }
        }

        if (newPositionInMilliSeconds > 0 && !getIsDragging()) {
            currentPositionInMilliSeconds = newPositionInMilliSeconds;
            playPosition = (int) currentPositionInMilliSeconds / 1000;
        }
        if (getVisibility() != View.VISIBLE) {
            return false;
        }
        if (!getIsDragging()) {
            int durationInMilliSeconds = mVideoView.getDuration();
            if (durationInMilliSeconds > 0) {
                this.setMax(durationInMilliSeconds);
                if (previousPosition != newPositionInMilliSeconds) {
                    this.setProgress((int) newPositionInMilliSeconds);
                }
            }
        }
        return false;
    }

    public void onTotalCacheUpdate(final long milliSeconds) {
        mainThreadHandler.post(new Runnable() {

            @Override
            public void run() {
                setCache((int) milliSeconds);
            }
        });
    }

    public String getTv_type() {
        return tv_type;
    }

    public void setTv_type(String tv_type) {
        this.tv_type = tv_type;
    }

    public interface ControllerCallBack {

        void onUpdatePosition();

        void onUpdatePositionStop();
    }

    public static int getPlaybackRate() {
        Log.d(TAG, "getPlaybackRate playRate " + playRate);
        if (mbIsDragging || mbIsCaching) {
            return 0;
        }
        return playRate;
    }

    public static int getPlaybackPosition() {
        Log.d(TAG, "getPlaybackPosition playPosition " + playPosition);
        return playPosition;
    }

    public static int getPlaybackDuartion() {
        Log.d(TAG, "getPlaybackDuartion playDuration " + playDuration);
        return playDuration;
    }
}
