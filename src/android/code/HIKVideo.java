package fxp.plugin.video;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HIKVideo extends CordovaPlugin {

    private String TAG = "HIKVideo";

    private CallbackContext callbackContext;

    private JSONArray executeArgs;

    private final int REQUEST_HCVideo = 500;

    private final int REQUEST_MonitorVideo = 501;

    private static final int RESULT_NORMAL = 10;  // 正常返回

    private static final int RESULT_ERROR = 11; // 错误返回

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        this.executeArgs = args;

        if (action.equals("playSingle")) {
            Log.i(TAG, "execute:playSingle");
            toHCVideoActivity(getVedioInfo(args));
        }else if (action.equals("playVideo")){
            Log.i(TAG, "execute:playVideo");
            toMonitorVideoActivity(getVedioInfo(args));
        }else {
            return false;
        }

        return true;
    }

    /**
     * 跳转到单路视频播放页面
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2018/1/5 下午7:51
     */
    private void toHCVideoActivity(VideoInfo videoInfo) {
        Log.i(TAG, "toHCVideoActivity");

        Intent intent = new Intent(this.cordova.getActivity(), HCVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoInfo", videoInfo);
        intent.putExtras(bundle);
        if (this.cordova != null) {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, REQUEST_HCVideo);
        }
    }

    /**
     * 跳转到单路/多路视频播放页面
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2018/1/10 下午7:59
     *
     */
    private void toMonitorVideoActivity(VideoInfo videoInfo) {
        Log.e(TAG, "toMonitorVideoActivity");

        Intent intent = new Intent(this.cordova.getActivity(), MonitorVedioActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("videoInfo", videoInfo);
        intent.putExtras(bundle);
        if (this.cordova != null) {
            this.cordova.startActivityForResult((CordovaPlugin) this, intent, REQUEST_MonitorVideo);
        }
    }

    /**
     * 获取js传递过来的数据
     *
     * @author fxp
     * @mail 850899969@qq.com
     * @date 2018/1/5 下午7:51
     */
    private VideoInfo getVedioInfo(JSONArray args) throws JSONException {

        JSONObject jsonArgs = new JSONObject(args.getString(0));

        String host = (String) jsonArgs.get("host");
        String port = (String) jsonArgs.get("port");
        String user = (String) jsonArgs.get("user");
        String pass = (String) jsonArgs.get("pass");
        String channel = (String) jsonArgs.get("channel");
        String desc = (String) jsonArgs.get("desc");

        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setIp(host);
        videoInfo.setPort(Integer.parseInt(port));
        videoInfo.setUserName(user);
        videoInfo.setPassword(pass);
        videoInfo.setChannel(channel);
        videoInfo.setDesc(desc);

        return videoInfo;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode){
            case REQUEST_HCVideo:
                Log.i(TAG,"back from HCVideoActivity");
                break;
            case REQUEST_MonitorVideo:
                Log.i(TAG,"back from MonitorVedioActivity");
                break;
        }
        setCallBack(resultCode,intent);
    }

    /**
     * 设置js回调
     *
     * @author fxp 
     * @mail 850899969@qq.com
     * @date 2018/1/10 下午8:01
     * 
     */
    private void setCallBack(int resultCode, Intent intent){
        if (this.callbackContext != null){
            String resultMsg = intent.getExtras().getString("result");
            Log.i(TAG,"resultCode:" + resultCode);
            Log.i(TAG,"resultMsg:" + resultMsg);
            switch (resultCode){
                case RESULT_NORMAL:
                    Log.i(TAG,"RESULT_NORMAL");
                    callbackContext.success("success");
                    break;
                case RESULT_ERROR:
                    Log.e(TAG,"RESULT_ERROR");
                    callbackContext.error(resultMsg);
                    break;
            }
        }
    }

}
