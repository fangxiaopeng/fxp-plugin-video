/** @file       iOS Interface
 *  @note       HangZhou Hikvision Software Co., Ltd. All Right Reserved.
 *  @brief      declaration of iOS PlayM4 APIs
 *
 *  @author     Media Play SDK Team of Hikvision
 *
 *  @version    V7.3.3
 *  @date       2016/09/12
 *
 *  @note       iOS播放库PlayM4接口声明
 */

#ifndef __IOS_PLAYM4_H__
#define __IOS_PLAYM4_H__

#include "MobilePlaySDKInterface.h"

////////////////iOS平台特殊接口/////////////////
////////////////iOS Special Interface///////////

// 1
//Stop 后是否保留最后一帧
//After Stop, Keep the last frame on window
int  PlayM4_KeepLastFrame(int nPort, bool bFlag);

#endif 

