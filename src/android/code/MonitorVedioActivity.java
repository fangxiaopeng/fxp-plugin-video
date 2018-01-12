package fxp.plugin.video;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.fxp.videoDemo.R;

import org.MediaPlayer.PlayM4.Player;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 播放监控视频
 * 支持单路/多路监控视频
 * 配置columnNum参数可设置多通道视频播放列数
 *
 * @author fxp
 * @mail 850899969@qq.com
 * @date 2018/1/10 下午6:21
 */
public class MonitorVedioActivity extends Activity implements SurfaceHolder.Callback {

    private String TAG = "MonitorVedioActivity";

    // 多通道播放
    private boolean bMultiPlay = false;

    // 多通道播放-视频列数
    private int columnNum = 2;

    // 设备模拟通道个数
    private int iChanNum = 0;

    // 登录结果id
    private int iLogId = -1;

    // 视频播放连接标志位
    private int iPlayId = -1;

    private int iPort = -1;

    // 模拟通道起始通道号
    private int iStartChan = 0;

    // 是否需要解码
    private boolean needDecode = true;

    // 多通道播放surfaceView
    private PlaySurfaceView[] playView;

    // 单通道播放surfaceView
    private SurfaceView surfaceView;

    private VideoInfo videoInfo;

    private LinearLayout vedioLayout;

    // 正常返回
    private static final int RESULT_NORMAL = 10;

    // 错误返回
    private static final int RESULT_ERROR = 11;

    // 返回信息
    private String resultMsg = "";

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        // 设置当前Activity no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_monitor_vedio);

        findViews();

        initData();

        initViews();
    }

    private void findViews() {
        vedioLayout = ((LinearLayout) findViewById(R.id.vedio_layout));
    }

    private void initData() {
        videoInfo = ((VideoInfo) getIntent().getSerializableExtra("videoInfo"));

        if (!MethodUtils.getInstance().initHCNetSDK()) {
            MethodUtils.getInstance().quitActivity(MonitorVedioActivity.this, RESULT_ERROR, "HCNetSDK init failed");
            return;
        }
        if (!initActivity()) {
            MethodUtils.getInstance().quitActivity(MonitorVedioActivity.this, RESULT_ERROR, "View init failed");
            return;
        }
    }

    private void initViews() {
        new LoginAsyncTask(this, iStartChan, iChanNum, new AsyncTaskExecuteListener() {
            @Override
            public void asyncTaskResult(String result) {
                Log.i(TAG, "asyncTaskResult-" + result);
                loginResultHandler(result);
            }
        }).execute(videoInfo);
    }

    private boolean initActivity() {
        surfaceView = (SurfaceView) findViewById(R.id.Sur_Player);
        surfaceView.getHolder().addCallback(this);
        return true;
    }

    /**
     * 登录结果处理
     *
     * @param result
     */
    private void loginResultHandler(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            iLogId = jsonObject.getInt("iLogId");
            iChanNum = jsonObject.getInt("iChanNum");
            iStartChan = jsonObject.getInt("iStartChan");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (iLogId < 0) {
            // 调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因
            int errorCode = HCNetSDK.getInstance().NET_DVR_GetLastError();
            MethodUtils.getInstance().quitActivity(MonitorVedioActivity.this, RESULT_ERROR, MethodUtils.getInstance().getNETDVRErrorMsg(errorCode));
        } else {
            playVideo(iLogId);
        }
    }

    private void playVideo(int loginState) {
        Log.i(TAG, "playVideo");

        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MonitorVedioActivity.this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (needDecode) {
                if (iChanNum > 1)// preview more than a channel
                {
                    if (!bMultiPlay) {
                        startMultiPreview(iChanNum,columnNum);
                        bMultiPlay = true;
                    } else {
                        stopMultiPreview();
                        bMultiPlay = false;
                    }
                } else
                // preivew a channel
                {
                    if (iPlayId < 0) {
                        startSinglePreview();
                    } else {
                        stopSinglePreview();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMultiPreview(int chanNum,int columnNum) {

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 多通道播放-视频item项宽度
        int videoViewWidth = (metric.widthPixels / columnNum);
        // 多通道播放-视频item项高度
        int videoViewHeigth = (3 * videoViewWidth / 4);

        playView = new PlaySurfaceView[chanNum];
        // 建立frameLayout容纳所有通道视频画面
        FrameLayout videoLayout = new FrameLayout(this);
        for (int i = 0; i < chanNum; i++) {
            if (playView[i] == null) {
                // 第i通道SurfaceView
                playView[i] = new PlaySurfaceView(this);
                // 设置第i通道监控画面尺寸
                playView[i].setViewSize(videoViewWidth, videoViewHeigth);
                // 设置第i通道监控画面布局参数
                FrameLayout.LayoutParams videoItemParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                videoItemParams.topMargin = i / columnNum * playView[i].getCurHeight();
                videoItemParams.leftMargin = i % columnNum * playView[i].getCurWidth();
                videoItemParams.gravity = Gravity.TOP | Gravity.LEFT;
                // 将第i通道SurfaceView添加到FrameLayout
                videoLayout.addView(playView[i], videoItemParams);
            }
            // 播放第i+1通道视频
            playView[i].startPreview(iLogId, i + iStartChan);
        }
        FrameLayout.LayoutParams scrollViewParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ScrollView scrollView = new ScrollView(this);
        // 将含有多通道视频画面的frameLayout添加到scrollView
        scrollView.addView(videoLayout);
        // 设置scrollView布局参数
        addContentView(scrollView, scrollViewParams);
        // 获取起始通道监控视频播放状态码
        iPlayId = playView[0].m_iPreviewHandle;
    }

    private void startSinglePreview() {
        Log.i(TAG, "startSinglePreview");

        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            return;
        }

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        // 通道号，模拟通道号从1开始，数字通道号从33开始，具体取值在登录接口返回
        previewInfo.lChannel = iStartChan;
        // 码流类型
        previewInfo.dwStreamType = 1;
        // 连接方式，0-TCP方式，1-UDP方式，2-多播方式，3-RTP方式，4-RTP/RTSP，5-RSTP/HTTP
        // previewInfo.dwLinkMode = 5;
        // 0-非阻塞取流，1-阻塞取流
        previewInfo.bBlocked = 1;
        // 实时预览，返回值-1表示失败
        iPlayId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iLogId, previewInfo, fRealDataCallBack);
        if (iPlayId < 0) {
            // 调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因
            int errorCode = HCNetSDK.getInstance().NET_DVR_GetLastError();
            HCNetSDK.getInstance().NET_DVR_Logout_V30(this.iLogId);
            MethodUtils.getInstance().quitActivity(MonitorVedioActivity.this, RESULT_ERROR, MethodUtils.getInstance().getNETDVRErrorMsg(errorCode));
        }
    }

    private void stopMultiPreview() {
        if (playView != null) {
            for (int i = 0; i < playView.length; i++) {
                playView[i].stopPreview();
            }
        }
    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        if ((Player.getInstance().stop(this.iPort)) && (Player.getInstance().closeStream(this.iPort)) && (Player.getInstance().freePort(this.iPort)))
            this.iPort = -1;
    }

    private void stopSinglePreview() {
        Log.i(TAG, "stopSinglePreview");

        if (iPlayId < 0) {
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(iPlayId)) {
            return;
        }

        iPlayId = -1;
        stopSinglePlayer();
    }

    protected void onRestoreInstanceState(Bundle paramBundle) {
        this.iPort = paramBundle.getInt("iPort");
        super.onRestoreInstanceState(paramBundle);
    }

    protected void onSaveInstanceState(Bundle paramBundle) {
        paramBundle.putInt("iPort", this.iPort);
        super.onSaveInstanceState(paramBundle);
    }

    @Override
    public void onBackPressed() {
        stopMultiPreview();
        HCNetSDK.getInstance().NET_DVR_Logout_V30(this.iLogId);
        MethodUtils.getInstance().quitActivity(MonitorVedioActivity.this, RESULT_NORMAL, resultMsg);
        super.onBackPressed();
    }

    @Override
    public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
        this.surfaceView.getHolder().setFormat(-3);
        if ((-1 != this.iPort) && (paramSurfaceHolder.getSurface().isValid()))
            Player.getInstance().setVideoWindow(this.iPort, 0, paramSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
        if ((-1 != this.iPort) && (paramSurfaceHolder.getSurface().isValid()))
            Player.getInstance().setVideoWindow(this.iPort, 0, null);
    }

    private RealPlayCallBack getRealPlayerCbf() {
        return new RealPlayCallBack() {
            public void fRealDataCallBack(int paramAnonymousInt1, int paramAnonymousInt2, byte[] paramAnonymousArrayOfByte, int paramAnonymousInt3) {
                MonitorVedioActivity.this.processRealData(1, paramAnonymousInt2, paramAnonymousArrayOfByte, paramAnonymousInt3, 0);
            }
        };
    }

    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (!needDecode) {

        } else {
            if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                if (iPort >= 0) {
                    return;
                }
                iPort = Player.getInstance().getPort();
                if (iPort == -1) {
                    return;
                }
                if (iDataSize > 0) {
                    if (!Player.getInstance().setStreamOpenMode(iPort, iStreamMode)) // set stream mode
                    {
                        return;
                    }
                    if (!Player.getInstance().openStream(iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) // open stream
                    {
                        return;
                    }
                    if (!Player.getInstance().play(iPort, surfaceView.getHolder())) {
                        return;
                    }
                    if (!Player.getInstance().playSound(iPort)) {
                        return;
                    }
                }
            } else {
                if (!Player.getInstance().inputData(iPort, pDataBuffer, iDataSize)) {
                    for (int i = 0; i < 4000; i++) {
                        if (!Player.getInstance().inputData(iPort, pDataBuffer, iDataSize)) {

                        } else {
                            break;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }

    }
}