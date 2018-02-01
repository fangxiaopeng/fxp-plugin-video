/*******************************************************************************
 CopyRight:			
 FileName:			CDVMPlayVideo.h
 Description:		play video
 Author:			fxp
 Data:				2018/1/8
 Modification History:
 *******************************************************************************/

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "Preview.h"
#import "DeviceInfo.h"

@interface CDVMPlayVideo : NSObject {
    UIView* m_multiView[MAX_VIEW_NUM];
}

@property int  m_nPlaybackPort;
@property (nonatomic, retain) id m_playThreadID;
@property bool m_bThreadRun;
@property int m_lUserID;
@property int m_lRealPlayID;
@property int m_lPlaybackID;
@property bool m_bPreview;
@property bool m_bRecord;
@property bool m_bPTZL;
@property bool m_bVoiceTalk;
@property bool m_bStopPlayback;

@property (nonatomic, retain) UIView   *m_playView;

-(bool)validateValue:(DeviceInfo*)deviceInfo;
-(bool)isValidIP:(NSString*)ipStr;
-(void)startPlay:(NSDictionary*)options;
-(void)stopPlay;
-(CDVMPlayVideo*)initPlayview:(UIView*)playView;
-(void)releaseVideo;

@end

@interface VideoUIView: UIView {}
@property int px;
@property int py;
@end
