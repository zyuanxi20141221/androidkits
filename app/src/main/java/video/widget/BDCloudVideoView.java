
package video.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.cloud.media.player.BDCloudMediaPlayer;
import com.baidu.cloud.media.player.BDTimedText;
import com.baidu.cloud.media.player.IMediaPlayer;

import java.io.IOException;
import java.util.Map;

public class BDCloudVideoView extends FrameLayout implements MediaController.MediaPlayerControl {
    private static final String TAG = "VideoView";

    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT = 1;

    public static final int VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING = 2;

    public static final int VIDEO_SCALING_MODE_SCALE_TO_MATCH_PARENT = 3;

    private boolean mUseTextureViewFirst = true;

    private Uri mUri;

    private Map<String, String> mHeaders;

    public enum PlayerState {
        STATE_ERROR(-1),
        STATE_IDLE(0),
        STATE_PREPARING(1),
        STATE_PREPARED(2),
        STATE_PLAYING(3),
        STATE_PAUSED(4),
        STATE_PLAYBACK_COMPLETED(5),

        STATE_PLAY_CACHEING(6),
        STATE_PLAY_CACHEING_END(7);

        private int code;

        private PlayerState(int oCode) {
            code = oCode;
        }
    }

    // 播放器当前的状态
    private PlayerState mCurrentState = PlayerState.STATE_IDLE;

    public PlayerState getCurrentPlayerState() {
        return mCurrentState;
    }

    private void setCurrentState(PlayerState newState) {
        if (mCurrentState != newState) {
            mCurrentState = newState;
            if (mOnPlayerStateListener != null) {
                mOnPlayerStateListener.onPlayerStateChanged(mCurrentState);
            }
        }
    }

    public interface OnPlayerStateListener {
        public void onPlayerStateChanged(final PlayerState nowState);
    }

    public void setOnPlayerStateListener(OnPlayerStateListener listener) {
        mOnPlayerStateListener = listener;
    }

    private boolean isTryToPlaying = false;


    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
    private BDCloudMediaPlayer mMediaPlayer = null;
    // private int         mAudioSession;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    private int mVideoRotationDegree;
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
    private int mCurrentBufferPercentage;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private OnPlayerStateListener mOnPlayerStateListener;
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    private String mDrmToken = null;

    private int mCurrentAspectRatio = IRenderView.AR_ASPECT_FIT_PARENT;

    private Context mAppContext;
    private IRenderView mRenderView;
    private int mVideoSarNum;
    private int mVideoSarDen;

    private int mCacheTimeInMilliSeconds = 0;
    private boolean mbShowCacheInfo = true;
    private int mDecodeMode = BDCloudMediaPlayer.DECODE_AUTO;
    private boolean mLogEnabled =  true;
    private long mInitPlayPositionInMilliSec = 0;
    private int mWakeMode = 0;
    private float mLeftVolume = -1f;
    private float mRightVolume = -1f;
    private int mMaxProbeTimeInMs = -1;
    private int mMaxProbeSizeInBytes = 0;
    private int mMaxCacheSizeInBytes = 0;
    private boolean mLooping = false;
    private int mBufferSizeInBytes = 0;
    private int mFrameChasing = -1;
    private float mSpeed = 1.0f;

    private RelativeLayout cachingHintViewRl = null;
    private ProgressBar cachingProgressBar = null;
    private TextView cachingProgressHint = null;

    private FrameLayout renderRootView = null;

    private static final int MESSAGE_CHANGE_CACHING = 1;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_CHANGE_CACHING) {
                setCachingHintViewVisibility(msg.arg1 == 1);
            }
//            super.handleMessage(msg);
        }
    };

    private TextView subtitleDisplay;

    public BDCloudVideoView(Context context) {
        super(context);
        initVideoView(context);
    }

    public BDCloudVideoView(Context context, boolean useTextureViewFirst) {
        super(context);
        this.mUseTextureViewFirst = useTextureViewFirst;
        initVideoView(context);
    }

    public BDCloudVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public BDCloudVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BDCloudVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        mAppContext = context.getApplicationContext();

        renderRootView = new FrameLayout(context);
        LayoutParams fllp = new LayoutParams(-1, -1);
        addView(renderRootView, fllp);

        reSetRender();

        addSubtitleView();
        addCachingHintView();

        mVideoWidth = 0;
        mVideoHeight = 0;

        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setCurrentState(PlayerState.STATE_IDLE);
    }

    private void addSubtitleView() {
        subtitleDisplay = new TextView(this.getContext());
        subtitleDisplay.setTextSize(24);
        subtitleDisplay.setGravity(Gravity.CENTER);
        LayoutParams layoutParamsTxt = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        addView(subtitleDisplay, layoutParamsTxt);
    }

    private void addCachingHintView() {
        cachingHintViewRl = new RelativeLayout(this.getContext());
        LayoutParams fllp = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        cachingHintViewRl.setVisibility(View.GONE);
        addView(cachingHintViewRl, fllp);

        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllp.addRule(RelativeLayout.CENTER_IN_PARENT);

        cachingProgressBar = new ProgressBar(this.getContext());
        cachingProgressBar.setId(android.R.id.text1); // setId() param can be random number, use text1 to avoid lints
        cachingProgressBar.setMax(100);
        cachingProgressBar.setProgress(10);
        cachingProgressBar.setSecondaryProgress(100);
        cachingHintViewRl.addView(cachingProgressBar, rllp);

        rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rllp.addRule(RelativeLayout.BELOW, android.R.id.text1);
        cachingProgressHint = new TextView(this.getContext());
        cachingProgressHint.setTextColor(0xffffffff);
        cachingProgressHint.setText("正在缓冲...");
        cachingProgressHint.setGravity(Gravity.CENTER_HORIZONTAL);
        cachingHintViewRl.addView(cachingProgressHint, rllp);
    }

    private void setCachingHintViewVisibility(boolean bShow) {
        if (bShow) {
            cachingHintViewRl.setVisibility(View.VISIBLE);
            setCurrentState(PlayerState.STATE_PLAY_CACHEING);
        } else {
            cachingHintViewRl.setVisibility(View.GONE);
            setCurrentState(PlayerState.STATE_PLAY_CACHEING_END);
        }
    }

    private void sendCachingHintViewVisibilityMessage(boolean bShow) {
        if (mbShowCacheInfo) {
            Message msg = mainThreadHandler.obtainMessage();
            msg.what = MESSAGE_CHANGE_CACHING;
            msg.arg1 = bShow ? 1 : 0;
            mainThreadHandler.sendMessage(msg);
        }
    }

    protected void setRenderView(IRenderView renderView) {
        if (mRenderView != null) {
            if (mMediaPlayer != null) {
                mMediaPlayer.setDisplay(null);
            }

            View renderUIView = mRenderView.getView();
            mRenderView.removeRenderCallback(mSHCallback);
            mRenderView.release();
            mRenderView = null;
            mSurfaceHolder = null;
            renderRootView.removeView(renderUIView);
        }

        if (renderView == null) {
            return;
        }

        mRenderView = renderView;
        renderView.setAspectRatio(mCurrentAspectRatio);
        if (mVideoWidth > 0 && mVideoHeight > 0) {
            renderView.setVideoSize(mVideoWidth, mVideoHeight);
        }
        if (mVideoSarNum > 0 && mVideoSarDen > 0) {
            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
        }

        View renderUIView = mRenderView.getView();
        LayoutParams lp = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        renderRootView.addView(renderUIView);

        mRenderView.addRenderCallback(mSHCallback);
        mRenderView.setVideoRotation(mVideoRotationDegree);
    }

    public void reSetRender() {
        if (mUseTextureViewFirst && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            TextureRenderView renderView = new TextureRenderView(getContext());
            if (mMediaPlayer != null) {
                renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
                renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
                renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
                renderView.setAspectRatio(mCurrentAspectRatio);
            }
            setRenderView(renderView);
        } else {
            SurfaceRenderView renderView = new SurfaceRenderView(getContext());
            setRenderView(renderView);
        }
    }

    public void setVideoPath(String path) {
        setVideoPathWithToken(path, null);
    }

    public void setVideoPathWithToken(String path, String token) {
        this.mDrmToken = token;
        setVideoURI(Uri.parse(path));
    }

    public void setHeaders(Map<String, String> headers) {
        mHeaders = headers;
    }

    private void setVideoURI(Uri uri) {
        mUri = uri;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            release(true);
        }
    }

    @TargetApi(14)
    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null) {
            return;
        }
        release(false);
        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        try {

            mMediaPlayer = createPlayer();
            if (!TextUtils.isEmpty(this.mDrmToken)) {
                mMediaPlayer.setDecryptTokenForHLS(mDrmToken);
            }
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setOnTimedTextListener(mOnTimedTextListener);
            mMediaPlayer.setOnMetadataListener(mOnMetadataListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.setTimeoutInUs(15000000);
            mMediaPlayer.prepareAsync();
            sendCachingHintViewVisibilityMessage(true);

            setCurrentState(PlayerState.STATE_PREPARING);
//            attachMediaController();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            setCurrentState(PlayerState.STATE_ERROR);
//            mTargetState = STATE_ERROR;
            isTryToPlaying = false;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } catch (IllegalArgumentException ex) {
            Log.w(TAG, "Unable to open content: " + mUri, ex);
            setCurrentState(PlayerState.STATE_ERROR);
//            mTargetState = STATE_ERROR;
            isTryToPlaying = false;
            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
        } finally {
            // REMOVED: mPendingSubtitleTracks.clear();
        }
    }

    public BDCloudMediaPlayer createPlayer() {
        BDCloudMediaPlayer bdCloudMediaPlayer = new BDCloudMediaPlayer(this.getContext());

        bdCloudMediaPlayer.setLogEnabled(mLogEnabled); // 打开播放器日志输出
        bdCloudMediaPlayer.setDecodeMode(mDecodeMode); // 设置解码模式
        if (mInitPlayPositionInMilliSec > -1) {
            bdCloudMediaPlayer.setInitPlayPosition(mInitPlayPositionInMilliSec); // 设置初始播放位置
            mInitPlayPositionInMilliSec = -1; // 置为-1，防止影响下个播放源
        }
        if (mWakeMode > 0) {
            bdCloudMediaPlayer.setWakeMode(this.getContext(), mWakeMode);
        }
        if (mLeftVolume > -1 && mRightVolume > -1) {
            bdCloudMediaPlayer.setVolume(mLeftVolume, mRightVolume);
        }
        if (mCacheTimeInMilliSeconds > 0) {
            bdCloudMediaPlayer.setBufferTimeInMs(mCacheTimeInMilliSeconds); // 设置『加载中』的最长缓冲时间
        }

        if (mMaxProbeTimeInMs >= 0) {
            bdCloudMediaPlayer.setMaxProbeTime(mMaxProbeTimeInMs);
        }
        if (mMaxProbeSizeInBytes > 0) {
            bdCloudMediaPlayer.setMaxProbeSize(mMaxProbeSizeInBytes);
        }
        if (mMaxCacheSizeInBytes > 0) {
            bdCloudMediaPlayer.setMaxCacheSizeInBytes(mMaxCacheSizeInBytes);
        }
        if (mLooping) {
            bdCloudMediaPlayer.setLooping(mLooping);
        }
        if (mBufferSizeInBytes > 0) {
            bdCloudMediaPlayer.setBufferSizeInBytes(mBufferSizeInBytes);
        }

        if (mFrameChasing >= 0) {
            bdCloudMediaPlayer.toggleFrameChasing(mFrameChasing == 1);
        }

        bdCloudMediaPlayer.setSpeed(mSpeed);

        return bdCloudMediaPlayer;
    }

    public void setBufferTimeInMs(int milliSeconds) {
        this.mCacheTimeInMilliSeconds = milliSeconds;
        if (mMediaPlayer != null) {
            mMediaPlayer.setBufferTimeInMs(mCacheTimeInMilliSeconds);
        }
    }

    public void setBufferSizeInBytes(int sizeInBytes) {
        this.mBufferSizeInBytes = sizeInBytes;
        if (mMediaPlayer != null) {
            mMediaPlayer.setBufferSizeInBytes(mBufferSizeInBytes);
        }
    }

    public void setLooping(boolean isLoop) {
        this.mLooping = isLoop;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLooping(mLooping);
        }
    }

    public void setMaxCacheSizeInBytes(int sizeInBytes) {
        mMaxCacheSizeInBytes = sizeInBytes;
        if (mMediaPlayer != null) {
            mMediaPlayer.setMaxCacheSizeInBytes(mMaxCacheSizeInBytes);
        }
    }

    public void setMaxProbeSize(int maxProbeSizeInBytes) {
        mMaxProbeSizeInBytes = maxProbeSizeInBytes;
        if (mMediaPlayer != null) {
            mMediaPlayer.setMaxProbeSize(mMaxProbeSizeInBytes);
        }
    }

    public void setMaxProbeTime(int maxProbeTimeInMs) {
        mMaxProbeTimeInMs = maxProbeTimeInMs;
        if (mMediaPlayer != null) {
            mMediaPlayer.setMaxProbeTime(mMaxProbeTimeInMs);
        }
    }

    public void setTimeoutInUs(int timeout) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setTimeoutInUs(timeout);
        }
    }

    public void setVolume(float leftVolume, float rightVolume) {
        mLeftVolume = leftVolume;
        mRightVolume = rightVolume;
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(mLeftVolume, mRightVolume);
        }
    }

    public void setSpeed(float playSpeed) {
        mSpeed = playSpeed;
        if (mMediaPlayer != null) {
            mMediaPlayer.setSpeed(mSpeed);
        }
    }

    public void setWakeMode(Context context, int mode) {
        mWakeMode = mode;
        if (mMediaPlayer != null) {
            mMediaPlayer.setWakeMode(context, mWakeMode);
        }
    }

    public void setInitPlayPosition(long milliSeconds) {
        mInitPlayPositionInMilliSec = milliSeconds;
        if (mMediaPlayer != null) {
            mMediaPlayer.setInitPlayPosition(mInitPlayPositionInMilliSec);
            mInitPlayPositionInMilliSec = -1;
        }
    }

    public void setLogEnabled(boolean enabled) {
        mLogEnabled = enabled;
        if (mMediaPlayer != null) {
            mMediaPlayer.setLogEnabled(mLogEnabled);
        }
    }

    public void setDecodeMode(int decodeMode) {
        mDecodeMode = decodeMode;
        if (mMediaPlayer != null) {
            mMediaPlayer.setDecodeMode(mDecodeMode);
        }
    }

    public void toggleFrameChasing(boolean isEnable) {
        mFrameChasing = isEnable ? 1 : 0;
        if (mMediaPlayer != null) {
            mMediaPlayer.toggleFrameChasing(isEnable);
        }
    }

    public void showCacheInfo(boolean bShow) {
        mbShowCacheInfo = bShow;
    }

    public IMediaPlayer getCurrentMediaPlayer() {
        return mMediaPlayer;
    }

    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener =
            new IMediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
                    Log.d(TAG, "onVideoSizeChanged width=" + width + ";height=" + height + ";sarNum="
                            + sarNum + ";sarDen=" + sarDen);
                    mVideoWidth = mp.getVideoWidth();
                    mVideoHeight = mp.getVideoHeight();
                    mVideoSarNum = mp.getVideoSarNum();
                    mVideoSarDen = mp.getVideoSarDen();
                    if (mVideoWidth != 0 && mVideoHeight != 0) {
                        if (mRenderView != null) {
                            mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                            mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                        }
                        // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
                        requestLayout();
                    }
                }
            };

    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {
            Log.d(TAG, "onPrepared");
            setCurrentState(PlayerState.STATE_PREPARED);

            sendCachingHintViewVisibilityMessage(false);


            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }

            Log.d(TAG, "onPrepared: mVideoWidth=" + mVideoWidth + ";mVideoHeight="
                    + mVideoHeight + ";mSurfaceWidth=" + mSurfaceWidth + ";mSurfaceHeight=" + mSurfaceHeight);
            if (mVideoWidth != 0 && mVideoHeight != 0) {
                if (mRenderView != null) {
                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
                        if (isTryToPlaying) {
                            start();
                        }
                    }
                }
            } else {
                if (isTryToPlaying) {
                    start();
                }
            }
        }
    };

    private IMediaPlayer.OnCompletionListener mCompletionListener =
            new IMediaPlayer.OnCompletionListener() {
                public void onCompletion(IMediaPlayer mp) {
                    Log.d(TAG, "onCompletion");
                    sendCachingHintViewVisibilityMessage(false);
                    setCurrentState(PlayerState.STATE_PLAYBACK_COMPLETED);
                    isTryToPlaying = false;
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mMediaPlayer);
                    }
                }
            };

    private IMediaPlayer.OnInfoListener mInfoListener =
            new IMediaPlayer.OnInfoListener() {
                public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
                    Log.d(TAG, "onInfo: arg1=" + arg1 + "; arg2=" + arg2);
                    if (mOnInfoListener != null) {
                        mOnInfoListener.onInfo(mp, arg1, arg2);
                    }
                    switch (arg1) {
                        case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
                            sendCachingHintViewVisibilityMessage(true);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                            Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
                            sendCachingHintViewVisibilityMessage(false);
                            break;
                        case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                            Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                            Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                            Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                            Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                            Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                            Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                            break;
                        case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                            mVideoRotationDegree = arg2;
                            Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
                            if (mRenderView != null)
                                mRenderView.setVideoRotation(arg2);
                            break;
                        case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                            Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
                            break;
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnErrorListener mErrorListener =
            new IMediaPlayer.OnErrorListener() {
                public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
                    Log.d(TAG, "onError: " + framework_err + "," + impl_err);
                    setCurrentState(PlayerState.STATE_ERROR);
                    isTryToPlaying = false;

                    sendCachingHintViewVisibilityMessage(false);

                    if (mOnErrorListener != null) {
                        if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
                            return true;
                        }
                    }
                    return true;
                }
            };

    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener =
            new IMediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(IMediaPlayer mp, int percent) {
//                    Log.d(TAG, "onBufferingUpdate: percent=" + percent);
                    mCurrentBufferPercentage = percent;
                    if (mOnBufferingUpdateListener != null) {
                        mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
                    }
                }
            };

    private IMediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {

        @Override
        public void onSeekComplete(IMediaPlayer mp) {
            Log.d(TAG, "onSeekComplete");
            sendCachingHintViewVisibilityMessage(false);
            if (mOnSeekCompleteListener != null) {
                mOnSeekCompleteListener.onSeekComplete(mp);
            }
        }
    };

    private IMediaPlayer.OnTimedTextListener mOnTimedTextListener = new IMediaPlayer.OnTimedTextListener() {
        @Override
        public void onTimedText(IMediaPlayer mp, BDTimedText text) {
            Log.d(TAG, "onTimedText text=" + text.getText());
            if (text != null) {
                subtitleDisplay.setText(text.getText());
            }
        }
    };

    private IMediaPlayer.OnMetadataListener mOnMetadataListener = new IMediaPlayer.OnMetadataListener() {
        @Override
        public void onMetadata(IMediaPlayer mp, Bundle meta) {
            for (String key : meta.keySet()) {
                Log.d(TAG, "onMetadata: key = " + key + ", value = " + meta.getString(key));
            }
        }
    };

    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
        mOnInfoListener = l;
    }

    public void setOnBufferingUpdateListener(IMediaPlayer.OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener l) {
        mOnSeekCompleteListener = l;
    }

    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
        if (mp == null)
            return;

        if (holder == null) {
            mp.setDisplay(null);
            return;
        }

        holder.bindToMediaPlayer(mp);
    }

    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
        @Override
        public void onSurfaceChanged(IRenderView.ISurfaceHolder holder, int format, int w, int h) {
            Log.d(TAG, "mSHCallback onSurfaceChanged");
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
                return;
            }

            mSurfaceWidth = w;
            mSurfaceHeight = h;
            boolean isValidState = isTryToPlaying;
            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
            if (mMediaPlayer != null && isValidState && hasValidSize) {
                start();
            }
        }

        @Override
        public void onSurfaceCreated(IRenderView.ISurfaceHolder holder, int width, int height) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
                return;
            }

            mSurfaceHolder = holder;
            if (mMediaPlayer != null)
                bindSurfaceHolder(mMediaPlayer, holder);
            else
                openVideo();
        }

        @Override
        public void onSurfaceDestroyed(IRenderView.ISurfaceHolder holder) {
            if (holder.getRenderView() != mRenderView) {
                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
                return;
            }
            mSurfaceHolder = null;
            releaseWithoutStop();
        }
    };

    private void releaseWithoutStop() {
        if (mMediaPlayer != null) {
            if (mRenderView instanceof SurfaceRenderView) {
                mMediaPlayer.setDisplay(null);
            }
        }
    }

    public void release() {
        // 释放播放器player
        release(true);
        // 释放显示资源
        if (mRenderView != null) {
            mRenderView.release();
        }
    }

    private void release(boolean cleartargetstate) {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.setDisplay(null);
            synchronized (mMediaPlayer) {
//                mMediaPlayer.release(); //在没有网络的情况下，调用这个释放会ANR
                mMediaPlayer = null;
            }
            setCurrentState(PlayerState.STATE_IDLE);
            if (cleartargetstate) {
                isTryToPlaying = false;
            }
            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
            am.abandonAudioFocus(null);
        }
    }

    @Override
    public void start() {
        if (mMediaPlayer != null && (mCurrentState == PlayerState.STATE_ERROR)
                || mCurrentState == PlayerState.STATE_PLAYBACK_COMPLETED) {

            if (mCurrentState == PlayerState.STATE_PLAYBACK_COMPLETED) {
                mMediaPlayer.stop();
            }

            mMediaPlayer.prepareAsync();
            sendCachingHintViewVisibilityMessage(true);
            setCurrentState(PlayerState.STATE_PREPARING);
        } else if (isInPlaybackState()) {
            mMediaPlayer.start();
            setCurrentState(PlayerState.STATE_PLAYING);
        }
        isTryToPlaying = true;
    }

    @Override
    public void pause() {
        if (isInPlaybackState()) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                setCurrentState(PlayerState.STATE_PAUSED);
            }
        }
        isTryToPlaying = false;
    }

    public String getCurrentPlayingUrl() {
        if (this.mUri != null) {
            return this.mUri.toString();
        }
        return null;
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getDuration();
        }

        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return (int) mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public void seekTo(int msec) {
        if (isInPlaybackState()) {
            mMediaPlayer.seekTo(msec);
            this.sendCachingHintViewVisibilityMessage(true);
        }
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null) {
            return mCurrentBufferPercentage;
        }
        return 0;
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null &&
                mCurrentState != PlayerState.STATE_ERROR &&
                mCurrentState != PlayerState.STATE_IDLE &&
                mCurrentState != PlayerState.STATE_PREPARING);
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    @Override
    public boolean canSeekBackward() {
        return mCanSeekBack;
    }

    @Override
    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public void setVideoScalingMode(int mode) {
        if (mode == VIDEO_SCALING_MODE_SCALE_TO_FIT || mode == VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                || mode == VIDEO_SCALING_MODE_SCALE_TO_MATCH_PARENT) {
            if (mode == VIDEO_SCALING_MODE_SCALE_TO_FIT) {
                mCurrentAspectRatio = IRenderView.AR_ASPECT_FIT_PARENT;
            } else if (mode == VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING) {
                mCurrentAspectRatio = IRenderView.AR_ASPECT_FILL_PARENT;
            } else {
                mCurrentAspectRatio = IRenderView.AR_MATCH_PARENT;
            }
            if (mRenderView != null) {
                mRenderView.setAspectRatio(mCurrentAspectRatio);
            }
        } else {
            Log.e(TAG, "setVideoScalingMode: param should be VID");
        }
    }

    public String[] getVariantInfo() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getVariantInfo();
        }
        return null;
    }

    public long getDownloadSpeed() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getDownloadSpeed();

        }
        return 0L;
    }

    public boolean selectResolutionByIndex(int index) {
        boolean selectRight = false;
        if (mMediaPlayer != null) {
            this.sendCachingHintViewVisibilityMessage(true);
            selectRight = mMediaPlayer.selectResolutionByIndex(index);
            if (!selectRight) {
                this.sendCachingHintViewVisibilityMessage(false);
            }
        }
        return selectRight;
    }

    public Bitmap getBitmap() {
        if (mRenderView != null) {
            return mRenderView.getBitmap();
        }
        return null;
    }

    public static void setAK(String ak) {
        BDCloudMediaPlayer.setAK(ak);
    }

    public void enterBackground() {
        if (mRenderView != null && !(mRenderView instanceof SurfaceRenderView)) {
            renderRootView.removeView(mRenderView.getView());
        }
    }

    public void enterForeground() {
        if (mRenderView != null && !(mRenderView instanceof SurfaceRenderView)) {
            View renderUIView = mRenderView.getView();
            // getParent() == null : is removed in enterBackground()
            if (renderUIView.getParent() == null) {
                LayoutParams lp = new LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER);
                renderUIView.setLayoutParams(lp);
                renderRootView.addView(renderUIView);
            } else {
                Log.d(TAG, "enterForeground; but getParent() is not null");
            }
        }
    }
}
