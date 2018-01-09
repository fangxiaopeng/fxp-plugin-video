package fxp.plugin.video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.fxp.videoDemo.R;

import org.MediaPlayer.PlayM4.Player;

/**
 * @author fxp
 * @mail 850899969@qq.com
 * @date 2017/12/28 下午2:33
 */
public class HCVideoActivity extends Activity implements View.OnClickListener {

    private SurfaceView videoSurfaceview;

    private TextView backBtn;

    private TextView cameraDescTextView;

    private String TAG = "HCVideoActivity";

    private int Login_id = -1;   // return by NET_DVR_Login_v30

    private int player_id = -1;     //视频播放连接标志位

    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;   //设备信息

    private int m_iStartChan = 0; // 模拟通道起始通道号

    private int m_iChanNum = 0; // 设备模拟通道个数

    private boolean m_bInsideDecode = true;

    private int iPort = -1;

    private VideoInfo videoInfo;

    private String title;

    private static final int RESULT_NORMAL = 10;  // 正常返回

    private static final int RESULT_ERROR = 11; // 错误返回

    private String resultMsg = "";   // 返回信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_hcvideo);

        findViews();

        initData();

        initListener();

        initViews();
    }

    /**
     * 绑定控件
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/28 下午2:44
     */
    private void findViews() {
        videoSurfaceview = (SurfaceView) this.findViewById(R.id.video_surfaceview);
        backBtn = (TextView) this.findViewById(R.id.back_btn);
        cameraDescTextView = (TextView) this.findViewById(R.id.camera_desc_tv);
    }

    /**
     * 初始化数据
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/28 下午2:41
     */
    private void initData() {

        Bundle bundle = this.getIntent().getExtras();

        videoInfo = (VideoInfo) bundle.getSerializable("videoInfo");

        if (videoInfo != null) {
            title = videoInfo.getDesc();
        }

        if (!initHCNetSDK()) {
            quitCurrentActivity(RESULT_ERROR,"HCNetSDK init failed");
            return;
        }
    }

    /**
     * 初始化视图
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/28 下午2:42
     */
    private void initViews() {

        cameraDescTextView.setText(title);

        new LoginAndVedioTask().execute(videoInfo);
    }

    /**
     * 绑定事件
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/28 下午2:44
     */
    private void initListener() {
        backBtn.setOnClickListener(this);
        videoSurfaceview.getHolder().addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder paramSurfaceHolder) {
                videoSurfaceview.getHolder().setFormat(-3);
                if ((-1 != iPort) && (paramSurfaceHolder.getSurface().isValid()))
                    Player.getInstance().setVideoWindow(iPort, 0, paramSurfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder) {
                if ((-1 != iPort) && (paramSurfaceHolder.getSurface().isValid()))
                    Player.getInstance().setVideoWindow(iPort, 0, null);
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                HCNetSDK.getInstance().NET_DVR_Logout_V30(Login_id);
                quitCurrentActivity(RESULT_NORMAL,resultMsg);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed");
        HCNetSDK.getInstance().NET_DVR_Logout_V30(Login_id);
        quitCurrentActivity(RESULT_NORMAL,resultMsg);
        super.onBackPressed();
    }

    protected void onRestoreInstanceState(Bundle paramBundle) {
        Log.i(TAG, "onRestoreInstanceState");
        this.iPort = paramBundle.getInt("iPort");
        super.onRestoreInstanceState(paramBundle);
    }

    protected void onSaveInstanceState(Bundle paramBundle) {
        Log.i(TAG, "onSaveInstanceState");
        paramBundle.putInt("iPort", this.iPort);
        super.onSaveInstanceState(paramBundle);
    }


    /**
     * 初始化海康视频SDK
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/28 下午3:16
     */
    private boolean initHCNetSDK() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.i(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);

        return true;
    }

    /**
     * 登录硬盘录像机，播放视频
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/29 上午10:11
     */
    private class LoginAndVedioTask extends AsyncTask<VideoInfo, Integer, Integer> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(HCVideoActivity.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("正在加载...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(VideoInfo... videoInfos) {

            m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

            if (null == m_oNetDvrDeviceInfoV30) {
                return -1;
            }

            Login_id = HCNetSDK.getInstance().NET_DVR_Login_V30(videoInfo.getIp(), videoInfo.getPort(), videoInfo.getUserName(), videoInfo.getPassword(), m_oNetDvrDeviceInfoV30);

            if (Login_id < 0) {
                // 调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因
                int errorCode = HCNetSDK.getInstance().NET_DVR_GetLastError();
                Log.i(TAG, "NET_DVR_Login_V30 is failed! errorCode:" + errorCode);
                Log.i(TAG, "NET_DVR_Login_V30 is failed! errorMsg:" + getNETDVRErrorMsg(errorCode));
                quitCurrentActivity(RESULT_ERROR,getNETDVRErrorMsg(errorCode));
            }

            if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
                m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
                m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
            } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
                m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
                m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
            }

            ExceptionCallBack exceptionCallBack = getExceptiongCbf();
            if (exceptionCallBack == null || !HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exceptionCallBack)) {
                return -1;
            }

            return Login_id;
        }

        @Override
        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            playSingleVideo(result);
        }
    }

    /**
     * 播放监控视频
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/29 上午11:30
     */
    private void playSingleVideo(int loginState) {
        try {
            if (loginState < 0)
            {
                return;
            }
            if (m_bInsideDecode) {
                if (m_iChanNum > 1) // more than a channel
                {
                    Log.i(TAG, "more than a channel");
                    m_iStartChan = m_iStartChan + Integer.parseInt(videoInfo.getChannel());
                }
                if (player_id < 0) {
                    startSinglePreview(Login_id, m_iStartChan);
                } else {
                    stopSinglePreview();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始播放单路监控视频
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/29 上午11:32
     */
    public void startSinglePreview(int loginId, int channelNum)
    {
        RealPlayCallBack localRealPlayCallBack = getRealPlayerCbf();
        if (localRealPlayCallBack == null)
        {
            Log.i(TAG, "fRealDataCallBack object is failed!");
            return;
        }

        NET_DVR_PREVIEWINFO localNET_DVR_PREVIEWINFO = new NET_DVR_PREVIEWINFO();
        // 通道号，模拟通道号从1开始，数字通道号从33开始，具体取值在登录接口返回
        localNET_DVR_PREVIEWINFO.lChannel = channelNum;
        // 码流类型
        localNET_DVR_PREVIEWINFO.dwStreamType = 1;
        // 连接方式，0-TCP方式，1-UDP方式，2-多播方式，3-RTP方式，4-RTP/RTSP，5-RSTP/HTTP
        // previewInfo.dwLinkMode = 5;
        // 0-非阻塞取流，1-阻塞取流
        localNET_DVR_PREVIEWINFO.bBlocked = 1;
        // 实时预览，返回值-1表示失败
        player_id = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(loginId, localNET_DVR_PREVIEWINFO, localRealPlayCallBack);

        if (player_id < 0)
        {
            // 调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因
            int errorCode = HCNetSDK.getInstance().NET_DVR_GetLastError();
            Log.i(TAG, "NET_DVR_RealPlay is failed! errorCode:" + errorCode);
            Log.i(TAG, "NET_DVR_RealPlay is failed! errorMsg:" + getNETDVRErrorMsg(errorCode));
            HCNetSDK.getInstance().NET_DVR_Logout_V30(Login_id);
            quitCurrentActivity(RESULT_ERROR,getNETDVRErrorMsg(errorCode));
        }
    }

    /**
     * 停止播放单路视频
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/29 上午11:32
     */
    private void stopSinglePreview() {
        Log.i(TAG, "stopSinglePreview");

        if (player_id < 0) {
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(player_id)) {
            return;
        }

        player_id = -1;
        stopSinglePlayer();
    }

    private void stopSinglePlayer() {
        Log.i(TAG, "stopSinglePlayer");
        Player.getInstance().stopSound();
        if ((Player.getInstance().stop(this.iPort)) && (Player.getInstance().closeStream(this.iPort)) && (Player.getInstance().freePort(this.iPort)))
            this.iPort = -1;
    }

    /**
     * 码流数据回调
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2018/1/8 上午11:13
     *
     */
    private RealPlayCallBack getRealPlayerCbf()
    {
        RealPlayCallBack cbf = new RealPlayCallBack()
        {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize)
            {
                processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }

    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (!m_bInsideDecode) {

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
                    if (!Player.getInstance().play(iPort, videoSurfaceview.getHolder())) {
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

    /**
     * 捕获异常
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2017/12/28 下午3:30
     */
    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    /**
     * 退出当前Activity，设置返回值
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2018/1/8 下午3:06
     *
     */
    private void quitCurrentActivity(int resultCode,String msg){
        Intent intent = new Intent();
        intent.putExtra("result",msg);
        HCVideoActivity.this.setResult(resultCode,intent);
        HCVideoActivity.this.finish();
    }

    /**
     * 获取连接错误信息
     * 当前只例举了常见错误码对应错误信息，更新错误码信息详见《设备网络编程指南（Android）》第4章
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2018/1/8 下午2:44
     *
     */
    private String getNETDVRErrorMsg(int errorCode){
        String errorMsg = "";
        switch (errorCode){
            case 1:
                errorMsg = "用户名或密码错误";
                break;
            case 2:
                errorMsg = "无当前设备操作权限";
                break;
            case 3:
                errorMsg = "SDK未初始化";
                break;
            case 4:
                errorMsg = "通道号错误";
                break;
            case 5:
                errorMsg = "连接到设备的用户数超过最大";
                break;
            case 7:
                errorMsg = "连接设备失败";
                break;
            case 11:
                errorMsg = "传送的数据有误";
                break;
            case 13:
                errorMsg = "无此权限";
                break;
            default:
                errorMsg = "错误码" + errorCode;
                break;
        }
        return errorMsg;
    }
}
