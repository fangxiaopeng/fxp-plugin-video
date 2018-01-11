package fxp.plugin.video;

import android.content.Context;
import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;

/**
 * 通用方法类
 *
 * @author fxp
 * @mail 850899969@qq.com
 * @date 2018/1/11 下午8:40
 *
 */
public class MethodUtils {

    private Context context = null;

    private static MethodUtils mInstance = null;

    public MethodUtils(){

    }

    public static synchronized MethodUtils getInstance(){

        if (mInstance == null){
            mInstance = new MethodUtils();
        }

        return mInstance;
    }

    /**
     * 初始化海康视频SDK
     *
     * @return
     */
    public boolean initHCNetSDK() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);

        return true;
    }

    /**
     * 获取连接错误信息
     * 当前只例举了常见错误码对应错误信息，更多错误码信息详见《设备网络编程指南（Android）》第4章
     *
     * @param errorCode 错误码
     * @return
     */
    public String getNETDVRErrorMsg(int errorCode){
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
