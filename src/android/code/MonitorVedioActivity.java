package fxp.plugin.video;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xbrother.gongjiyunweibao.R;

import org.MediaPlayer.PlayM4.Player;

/**
 * 播放监控视频
 * 支持单路/多路监控视频
 *
 * @author fxp
 * @mail 850899969@qq.com
 * @date 2018/1/10 下午6:21
 *
 */
public class MonitorVedioActivity extends Activity implements SurfaceHolder.Callback
{
	private String TAG = "MonitorVedioActivity";

	private boolean bMultiPlay = false;
	
	private int iChanNum = 0;

	private int iLogId = -1;

	private int iPlayId = -1;

	private int iPort = -1;

	private int iStartChan = 0;

	private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

	private DisplayMetrics metric;

	private boolean needDecode = true;

	private PlaySurfaceView[] playView;

	private SurfaceView surfaceView;

	private VideoInfo videoInfo;

	private LinearLayout vedioLayout;

	private int videoViewWidth;

	private int videoViewHeigth;

	private static final int RESULT_NORMAL = 10;  // 正常返回

	private static final int RESULT_ERROR = 11; // 错误返回

	private String resultMsg = "";   // 返回信息

	@Override
	protected void onCreate(Bundle paramBundle)
	{
		super.onCreate(paramBundle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_monitor_vedio);

		findViews();

		initData();

		initViews();
	}

	private void findViews(){
		vedioLayout = ((LinearLayout) findViewById(R.id.vedio_layout));
	}

	private void initData(){
		videoInfo = ((VideoInfo) getIntent().getSerializableExtra("videoInfo"));

		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		videoViewWidth = (metric.widthPixels / 2);
		videoViewHeigth = (3 * videoViewWidth / 4);

		if (!initSdk())
		{
			quitCurrentActivity(RESULT_ERROR,"HCNetSDK init failed");
			return;
		}
		if (!initActivity())
		{
			quitCurrentActivity(RESULT_ERROR,"View init failed");
			return;
		}
	}

	private void initViews(){
		new LoginAndVedioTask().execute(videoInfo);

	}

	private boolean initActivity()
	{
		surfaceView = (SurfaceView) findViewById(R.id.Sur_Player);
		surfaceView.getHolder().addCallback(this);
		return true;
	}

	private boolean initSdk()
	{
		if (!HCNetSDK.getInstance().NET_DVR_Init())
			return false;
		HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
		return true;
	}

	private class LoginAndVedioTask extends AsyncTask<VideoInfo, Integer, Integer>
	{

		private ProgressDialog dialog;

		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			dialog = new ProgressDialog(MonitorVedioActivity.this);
			dialog.show();
		}

		@Override
		protected Integer doInBackground(VideoInfo... params)
		{
			m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

			if (null == m_oNetDvrDeviceInfoV30)
			{
				return -1;
			}

			String ip = videoInfo.getIp();
			int port = videoInfo.getPort();
			String user = videoInfo.getUserName();
			String pwd = videoInfo.getPassword();

			iLogId = HCNetSDK.getInstance().NET_DVR_Login_V30(ip, port, user, pwd, m_oNetDvrDeviceInfoV30);

			if (iLogId < 0)
			{
				return -1;
			}
			if (m_oNetDvrDeviceInfoV30.byChanNum > 0)
			{
				iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
				iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
			}
			else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0)
			{
				iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
				iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
			}

			ExceptionCallBack exceptionCallBack = getExceptiongCbf();
			if (exceptionCallBack == null || !HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exceptionCallBack))
			{
				return -1;
			}

			return iLogId;
		}

		@Override
		protected void onPostExecute(Integer result)
		{
			dialog.dismiss();
			playVideo(result);
		}
	}

	private void playVideo(int loginState)
	{
		Log.i(TAG, "playVideo");

		try
		{
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MonitorVedioActivity.this.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			if (loginState < 0)
			{
				// 调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因
				int errorCode = HCNetSDK.getInstance().NET_DVR_GetLastError();
				quitCurrentActivity(RESULT_ERROR,"登录失败");
			}
			if (needDecode)
			{
				if (iChanNum > 1)// preview more than a channel
				{
					if (!bMultiPlay)
					{
						startMultiPreview(iChanNum);
						bMultiPlay = true;
					}
					else
					{
						stopMultiPreview();
						bMultiPlay = false;
					}
				}
				else
				// preivew a channel
				{
					if (iPlayId < 0)
					{
						startSinglePreview();
					}
					else
					{
						stopSinglePreview();
					}
				}
			}
			else
			{

			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void startMultiPreview(int paramInt)
	{
		playView = new PlaySurfaceView[paramInt];
		FrameLayout localFrameLayout = new FrameLayout(this);
		for (int i = 0; i < paramInt; i++)
		{
			if (playView[i] == null)
			{
				playView[i] = new PlaySurfaceView(this);
				playView[i].setParam(metric.widthPixels, metric.heightPixels);
				FrameLayout.LayoutParams localLayoutParams2 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				localLayoutParams2.topMargin = (playView[i].getCurHeight() - i / 2 * playView[i].getCurHeight());
				localLayoutParams2.topMargin = (i / 2 * playView[i].getCurHeight());
				localLayoutParams2.leftMargin = (i % 2 * playView[i].getCurWidth());
				localLayoutParams2.gravity = Gravity.TOP | Gravity.LEFT;
				localFrameLayout.addView(playView[i], localLayoutParams2);
			}
			playView[i].startPreview(iLogId, i + iStartChan);
		}
		FrameLayout.LayoutParams localLayoutParams1 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ScrollView localScrollView = new ScrollView(this);
		localScrollView.addView(localFrameLayout);
		addContentView(localScrollView, localLayoutParams1);
		iPlayId = playView[0].m_iPreviewHandle;
	}

	private void startSinglePreview()
	{
		Log.i(TAG, "startSinglePreview");

		RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
		if (fRealDataCallBack == null)
		{
			return;
		}

		NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
		previewInfo.lChannel = iStartChan;
		previewInfo.dwStreamType = 1;
		previewInfo.bBlocked = 1;
		// HCNetSDK start preview
		iPlayId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(iLogId, previewInfo, fRealDataCallBack);
		if (iPlayId < 0)
		{
			// 调用 NET_DVR_GetLastError 获取错误码，通过错误码判断出错原因
			int errorCode = HCNetSDK.getInstance().NET_DVR_GetLastError();
			HCNetSDK.getInstance().NET_DVR_Logout_V30(this.iLogId);
			quitCurrentActivity(RESULT_ERROR,getNETDVRErrorMsg(errorCode));
		}
	}

	private void stopMultiPreview()
	{
		if (playView != null)
		{
			for (int i = 0; i < playView.length; i++)
			{
				playView[i].stopPreview();
			}
		}
	}

	private void stopSinglePlayer()
	{
		Player.getInstance().stopSound();
		if ((Player.getInstance().stop(this.iPort)) && (Player.getInstance().closeStream(this.iPort)) && (Player.getInstance().freePort(this.iPort)))
			this.iPort = -1;
	}

	private void stopSinglePreview()
	{
		Log.i(TAG, "stopSinglePreview");

		if (iPlayId < 0)
		{
			return;
		}

		// net sdk stop preview
		if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(iPlayId))
		{
			return;
		}

		iPlayId = -1;
		stopSinglePlayer();
	}

	protected void onRestoreInstanceState(Bundle paramBundle)
	{
		this.iPort = paramBundle.getInt("iPort");
		super.onRestoreInstanceState(paramBundle);
	}

	protected void onSaveInstanceState(Bundle paramBundle)
	{
		paramBundle.putInt("iPort", this.iPort);
		super.onSaveInstanceState(paramBundle);
	}

	@Override
	public void onBackPressed()
	{
		stopMultiPreview();
		HCNetSDK.getInstance().NET_DVR_Logout_V30(this.iLogId);
		quitCurrentActivity(RESULT_NORMAL,resultMsg);
		super.onBackPressed();
	}

	@Override
	public void surfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3)
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder paramSurfaceHolder)
	{
		this.surfaceView.getHolder().setFormat(-3);
		if ((-1 != this.iPort) && (paramSurfaceHolder.getSurface().isValid()))
			Player.getInstance().setVideoWindow(this.iPort, 0, paramSurfaceHolder);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder paramSurfaceHolder)
	{
		if ((-1 != this.iPort) && (paramSurfaceHolder.getSurface().isValid()))
			Player.getInstance().setVideoWindow(this.iPort, 0, null);
	}

	private ExceptionCallBack getExceptiongCbf()
	{
		return new ExceptionCallBack()
		{
			public void fExceptionCallBack(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
			{
				System.out.println("recv exception, type:" + paramAnonymousInt1);
			}
		};
	}

	private RealPlayCallBack getRealPlayerCbf()
	{
		return new RealPlayCallBack()
		{
			public void fRealDataCallBack(int paramAnonymousInt1, int paramAnonymousInt2, byte[] paramAnonymousArrayOfByte, int paramAnonymousInt3)
			{
				MonitorVedioActivity.this.processRealData(1, paramAnonymousInt2, paramAnonymousArrayOfByte, paramAnonymousInt3, 0);
			}
		};
	}

	public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode)
	{
		if (!needDecode)
		{

		}
		else
		{
			if (HCNetSDK.NET_DVR_SYSHEAD == iDataType)
			{
				if (iPort >= 0)
				{
					return;
				}
				iPort = Player.getInstance().getPort();
				if (iPort == -1)
				{
					return;
				}
				if (iDataSize > 0)
				{
					if (!Player.getInstance().setStreamOpenMode(iPort, iStreamMode)) // set stream mode
					{
						return;
					}
					if (!Player.getInstance().openStream(iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) // open stream
					{
						return;
					}
					if (!Player.getInstance().play(iPort, surfaceView.getHolder()))
					{
						return;
					}
					if (!Player.getInstance().playSound(iPort))
					{
						return;
					}
				}
			}
			else
			{
				if (!Player.getInstance().inputData(iPort, pDataBuffer, iDataSize))
				{
					for (int i = 0; i < 4000; i++)
					{
						if (!Player.getInstance().inputData(iPort, pDataBuffer, iDataSize))
						{

						}
						else
						{
							break;
						}
						try
						{
							Thread.sleep(10);
						}
						catch(InterruptedException e)
						{
							e.printStackTrace();
						}
					}
				}

			}
		}

	}

	private void quitCurrentActivity(int resultCode,String msg){
		Intent intent = new Intent();
		intent.putExtra("result",msg);
		MonitorVedioActivity.this.setResult(resultCode,intent);
		MonitorVedioActivity.this.finish();
	}

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