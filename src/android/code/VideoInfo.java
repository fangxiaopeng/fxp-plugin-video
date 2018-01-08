package fxp.plugin.video;

import java.io.Serializable;

/**
 * @author fxp
 * @mail 850899969@qq.com
 * @date 2017/12/28 下午3:08
 */
public class VideoInfo implements Serializable {

    private String ip = "";

    private int port;

    private String userName = "";

    private String password = "";

    private String channel = "";

    private String cameraName = "";

    private String desc = "";

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", channel='" + channel + '\'' +
                ", cameraName='" + cameraName + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

}
