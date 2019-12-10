# fxp-plugin-video
*****

### 平台
Android，IOS

### 说明
该Cordova插件基于海康威视视频SDK二次开发，当前支持单路/多路实时监控视频，后续会支持监控视频回放等功能。[点击](http://blog.csdn.net/fxp850899969/article/details/79165941 "海康摄像头监控视频播放详解")查看详细实践教程。

### 安装
```
    cordova plugin add fxp-plugin-video
```
由于插件体积较大（海康SDK文件太大），下载安装会比较慢，请耐心等待。也可以下载插件后本地安装。


### 使用
#### 参数格式
```
    var params = {
        host:"192.168.10.95",
        port:"8000",
        user:"admin",
        pass:"12345",
        channel:"1",
        desc:"description information ..."
    }
```

#### 播放单路视频（支持指定通道）
```
    fxp.plugin.HIKVideo.playSingle(params,function(result){
        console.log("********************HIKVideo Success");

    },function(error){
        console.log("********************HIKVideo Error:" + error);

    });
```
该api仅支持Android。

#### 播放单路/多路视频（自动适配单路/多路）
```
    fxp.plugin.HIKVideo.playVideo(params,function(result){
        console.log("********************HIKVideo Success");

    },function(error){
        console.log("********************HIKVideo Error:" + error);

    });
```
该api支持Android和IOS，IOS不支持自动适配单路/多路。


### 注意
安卓版本插件：MonitorVedioActivity、HCVideoActivity中均有用到资源文件，因此，在插件安装完成之后，需要Rebuild安卓工程，重新生成R文件。


插件代码中有详细注释，欢迎提issue or fork，共同完善。


****
## 2019.12.10更新（重要）
1. 此项目开发于2017年底，基于海康SDK V5.3.3.2版本（当时最新版本），只适用于2019年前海康监控设备；（海康监控产品更新换代，旧版SDK不再适用于新产品。故老款设备监控视频可正常查看，新款无法查看）

2. 近期已重新开发新版本，已兼容新/旧款设备，支持单路/多路实时监控，以及单路历史监控视频回放，且采用不同方案实现。详情如下：
* 单路视频预览（使用xml布局SurfaceView控件）
* 单路视频预览（动态new View 添加到 ViewGroup）
* 多路视频预览（动态new View 添加到 ViewGroup）
* 多路视频预览（使用RecyclerView）
* 单路视频回放（选择时间段）
* 多路视频预览集成示例-在指定ViewGroup显示（动态new View 添加到 ViewGroup）

3. 新版本已解决安装后需Rebuild安卓工程重新生成R文件问题；

4. 新版本部分效果图如下：  
![HikvisionClient-2019](https://github.com/fangxiaopeng/fxp-plugin-video/blob/master/HikvisionClient-2019.gif "HikvisionClient-2019")  


由于某些原因不便开源，有问题的朋友可私聊交流；

****
