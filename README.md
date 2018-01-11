        
        
        1，说明

        该Cordova插件基于海康威视视频SDK二次开发，支持Android平台。

        当前支持单路/多路实时监控视频，后续会支持监控视频回放等功能。

        2，安装

        cordova plugin add fxp-plugin-video

        由于插件体积较大（海康SDK文件太大），下载安装会比较慢，请耐心等待。也可以下载插件后本地安装。

        3，使用
        3.1 参数格式：
        var params = {
            host:"192.168.10.95",
            port:"8000",
            user:"admin",
            pass:"12345",
            channel:"1",
            desc:"location"
        }

        3.2 播放单路视频（支持指定通道）：
        fxp.plugin.HIKVideo.playSingle(params,function(result){
            console.log("********************HIKVideo Success");

        },function(error){
            console.log("********************HIKVideo Error:" + error);

        });

        3.3 播放单路/多路视频（自动适配单路/多路）
        fxp.plugin.HIKVideo.playVideo(params,function(result){
            console.log("********************HIKVideo Success");

        },function(error){
            console.log("********************HIKVideo Error:" + error);

        });

        插件代码中有详细注释，不明之处可随时反馈。欢迎提bug，共同完善。