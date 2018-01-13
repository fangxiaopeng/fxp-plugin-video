package fxp.plugin.video;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ScrollView;

import com.hikvision.netsdk.HCNetSDK;
import com.fxp.videoDemo.R;

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
public class MonitorVedioActivity extends Activity {

    private String TAG = "MonitorVedioActivity";

    // 播放状态标志
    private boolean isPlaying = false;

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

    private DisplayMetrics metric = null;

    private VideoInfo videoInfo;

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

    }

    private void initData() {
        videoInfo = ((VideoInfo) getIntent().getSerializableExtra("videoInfo"));

        metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);

        if (!MethodUtils.getInstance().initHCNetSDK()) {
            MethodUtils.getInstance().quitActivity(MonitorVedioActivity.this, RESULT_ERROR, "HCNetSDK init failed");
            return;
        }
    }

    private void initViews() {
        new LoginAsyncTask(this, iStartChan, iChanNum, new AsyncTaskExecuteListener() {
            @Override
            public void asyncTaskResult(String result) {
                loginResultHandler(result);
            }
        }).execute(videoInfo);
    }

    /**
     * 登录结果处理
     *
     * @param result
     */
    private void loginResultHandler(String result) {
        Log.i(TAG, "loginResult-" + result);
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

    /**
     * 播放监控视频
     *
     * @param loginState    登录码
     */
    private void playVideo(int loginState) {

        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MonitorVedioActivity.this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            if (needDecode) {
                if (!isPlaying) {
                    if (iChanNum == 1) {
                        // 单路播放时单列显示
                        columnNum = 1;
                    }
                    startPreview(iChanNum, columnNum);
                    isPlaying = true;
                } else {
                    stopPreview();
                    isPlaying = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始播放实时监控视频
     *
     * @param chanNum   通道数目
     * @param columnNum 展示列数
     */
    private void startPreview(int chanNum, int columnNum) {

        playView = new PlaySurfaceView[chanNum];
        // 建立frameLayout容纳所有通道视频画面
        FrameLayout videoLayout = new FrameLayout(this);
        for (int i = 0; i < chanNum; i++) {
            if (playView[i] == null) {
                // 第i通道SurfaceView
                playView[i] = new PlaySurfaceView(this);
                // 设置第i通道监控画面尺寸，单路时全屏播放，多路时分列4：3播放
                playView[i].setViewSize(metric.widthPixels / columnNum, columnNum == 1 ? metric.heightPixels : 3 * metric.widthPixels / (4 * columnNum));
                // 设置第i通道监控画面布局参数
                FrameLayout.LayoutParams videoItemParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                videoItemParams.topMargin = i / columnNum * playView[i].getCurHeight();
                videoItemParams.leftMargin = i % columnNum * playView[i].getCurWidth();
                videoItemParams.gravity = Gravity.TOP | Gravity.LEFT;
                // 将第i通道SurfaceView添加到FrameLayout
                videoLayout.addView(playView[i], videoItemParams);
            }
            // 播放第i通道视频
            playView[i].startPreview(iLogId, i + iStartChan);
        }
        // 设置scrollView布局参数
        FrameLayout.LayoutParams scrollViewParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ScrollView scrollView = new ScrollView(this);
        // 将含有多通道视频画面的frameLayout添加到scrollView
        scrollView.addView(videoLayout);
        // 动态添加scrollView布局
        addContentView(scrollView, scrollViewParams);
        // 获取起始通道监控视频播放状态码
        iPlayId = playView[0].m_iPreviewHandle;
    }

    /**
     * 停止播放
     */
    private void stopPreview() {
        if (playView != null) {
            for (int i = 0; i < playView.length; i++) {
                playView[i].stopPreview();
            }
        }
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
        stopPreview();
        HCNetSDK.getInstance().NET_DVR_Logout_V30(this.iLogId);
        MethodUtils.getInstance().quitActivity(MonitorVedioActivity.this, RESULT_NORMAL, resultMsg);
        super.onBackPressed();
    }

}