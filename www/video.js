/**
* cordova.define 的第一个参数为【插件id.js-module的name值】，对应安装后cordova_plugins.js里面定义的id
* exec方法，参数说明：
* 参数1：成功回调function
* 参数2：失败回调function
* 参数3：feature name，与config.xml中注册的一致
* 参数4：调用java类时的action
* 参数5：要传递的参数，json格式
*/
// cordova.define("fxp-plugin-video", function(require, exports, module) {
var exec = require('cordova/exec');

            exports.playSingle=function(params, successCallback, errorCallback){
                exec(successCallback,errorCallback,"HIKVideo","playSingle",[params]);
            };
            
            exports.playVideo=function(params, successCallback, errorCallback){
                exec(successCallback,errorCallback,"HIKVideo","playVideo",[params]);
            };
            
// });
