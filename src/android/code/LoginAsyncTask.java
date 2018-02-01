package fxp.plugin.video;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登录播放视频异步任务类
 *
 * @author fxp
 * @mail 850899969@qq.com
 * @date 2018/1/11 下午4:26
 */
public class LoginAsyncTask extends AsyncTask<VideoInfo, Integer, String> {

    private String TAG = "LoginAsyncTask";

    private Context context;

    private int iLogId = -1;

    private int iStartChan = 0;

    private int iChanNum = 0;

    private ProgressDialog dialog = null;

    private AsyncTaskExecuteListener asyncTaskExecuteListener = null;

    public LoginAsyncTask(Context context, int iStartChan, int iChanNum, AsyncTaskExecuteListener asyncTaskExecuteListener) {
        this.context = context;
        this.iStartChan = iStartChan;
        this.iChanNum = iChanNum;
        this.asyncTaskExecuteListener = asyncTaskExecuteListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.show();
    }

    @Override
    protected String doInBackground(VideoInfo... videoInfos) {
        NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

        if (null == m_oNetDvrDeviceInfoV30) {
            return assembleAsyncResultStr(-1);
        }

        iLogId = HCNetSDK.getInstance().NET_DVR_Login_V30(videoInfos[0].getIp(), videoInfos[0].getPort(), videoInfos[0].getUserName(), videoInfos[0].getPassword(), m_oNetDvrDeviceInfoV30);

        if (iLogId < 0) {
            return assembleAsyncResultStr(-1);
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        ExceptionCallBack exceptionCallBack = MethodUtils.getInstance().getExceptiongCbf();
        if (exceptionCallBack == null || !HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(exceptionCallBack)) {
            return assembleAsyncResultStr(-1);
        }

        return assembleAsyncResultStr(iLogId);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate();

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog.dismiss();
        asyncTaskExecuteListener.asyncTaskResult(result);
    }

    /**
     * 组装异步任务类执行结果
     *
     * @param iLogId 登录id
     * @return 登录结果信息
     */
    private String assembleAsyncResultStr(int iLogId) {
        JSONObject result = new JSONObject();
        try {
            result.put("iLogId", iLogId);
            result.put("iChanNum", iChanNum);
            result.put("iStartChan", iStartChan);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
