        
        该Cordova插件基于海康威视视频SDK二次开发，支持Android平台。

        当前支持单路实时监控视频，后续会支持多路视频，以及监控视频回放等功能。

        用法如下：

        参数格式：
        var params = {
            host:"192.168.10.95",
            port:"8000",
            user:"admin",
            pass:"12345",
            channel:"1",
            desc:"location"
        }

        js中调用：
        fxp.plugin.HIKVideo.playSingle(params,function(result){
            console.log("********************HIKVideo Success");

        },function(error){
            console.log("********************HIKVideo Error:" + error);

        });

        插件代码中有详细注释，不明之处可随时反馈。