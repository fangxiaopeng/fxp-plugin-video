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




插件代码中有详细注释，欢迎提issue or fork，共同完善。