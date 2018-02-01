/*******************************************************************************
 CopyRight:			
 FileName:			CDVMPlayVideo.m
 Description:		play video
 Author:			fxp
 Data:				2018/1/8
 Modification History:
 *******************************************************************************/

#import "CDVMPlayVideo.h"
#import "hcnetsdk.h"
#import "HikDec.h"
#import "Preview.h"
#include <stdio.h>
#include <ifaddrs.h>
#include <sys/socket.h>
#include <sys/poll.h>
#include <net/if.h>
#include <map>

@class VideoUIView;

@implementation CDVMPlayVideo

@synthesize  m_nPlaybackPort = _m_nPlaybackPort;
@synthesize  m_playThreadID = _m_playThreadID;
@synthesize  m_bThreadRun = _m_bThreadRun;
@synthesize  m_lUserID = _m_lUserID;
@synthesize  m_lRealPlayID = _m_lRealPlayID;
@synthesize  m_lPlaybackID = _m_lPlaybackID;
@synthesize  m_bPreview = _m_bPreview;
@synthesize  m_bRecord = _m_bRecord;
@synthesize  m_bPTZL = _m_bPTZL;
@synthesize  m_bVoiceTalk = _m_bVoiceTalk;
@synthesize  m_bStopPlayback = _m_bStopPlayback;

@synthesize  m_playView=_m_playView;

CDVMPlayVideo* mplayController = NULL;

int g_iStartChan = 0;
int g_iPreviewChanNum = 0;

int itemW = 0;
int itemH = 0;

void g_fExceptionCallBack(DWORD dwType, LONG lUserID, LONG lHandle, void *pUser)
{
    NSLog(@"g_fExceptionCallBack Type[0x%x], UserID[%d], Handle[%d]", dwType, lUserID, lHandle);
}

-(bool)validateValue:(DeviceInfo*)deviceInfo{
    return false;
}
-(bool)isValidIP:(NSString*)ipStr{
    return false;
}

-(void)stopPlay{}

-(void)startPlay:(NSDictionary*)options{

    if(_m_lUserID == -1){
        BOOL bRet = NET_DVR_Init();
        if(!bRet){
            NSLog(@"NET_DVR_Init failed");
        }
        NET_DVR_SetExceptionCallBack_V30(0, NULL, g_fExceptionCallBack, NULL);
        NSString *documentPath = [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) objectAtIndex:0];
        const char* pDir = [documentPath UTF8String];
        NET_DVR_SetLogToFile(3, (char*)pDir, true);
        
        if([self loginNormalDevice:options]){
            NSLog(@"login is successfull ");
            
            [self beginPlayVideo];
        }
        
    }else{
        NET_DVR_Logout(_m_lUserID);
        NET_DVR_Cleanup();
        _m_lUserID = -1;
        NSLog(@"logout is sucessfull ");
    }
    
}
-(bool)loginNormalDevice:(NSDictionary*)options{
    
    if(options == nil){
        NSLog(@"the argument options is null ");
        return false;
    }
    
    NSString* ip = options[@"host"];
    NSString* port = options[@"port"];
    NSString* userName = options[@"user"];
    NSString* password = options[@"pass"];
    NSString* channel = options[@"channel"];
    
    if(ip == nil || port == nil || userName ==nil || password == nil ){
        NSLog(@"-- Error login device infomation is not complete ");
        [self showMessage:@"登陆信息不完整"];
        return false;
    }
    
    DeviceInfo *deviceInfo = [[DeviceInfo alloc] init];
    deviceInfo.chDeviceAddr = ip;
    deviceInfo.nDevicePort = [port intValue];
    deviceInfo.chLoginName = userName;
    deviceInfo.chPassWord = password;
    
    //device login
    NET_DVR_DEVICEINFO_V30 logindeviceInfo = {0};
    
    //encode type
    NSStringEncoding enc = CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingGB_18030_2000);
    _m_lUserID = NET_DVR_Login_V30((char*)[deviceInfo.chDeviceAddr UTF8String], deviceInfo.nDevicePort, (char*)[deviceInfo.chLoginName cStringUsingEncoding:enc], (char*)[deviceInfo.chPassWord UTF8String], &logindeviceInfo);
    
    printf("ip:%s\n",(char*)[deviceInfo.chDeviceAddr UTF8String]);
    printf("Port:%d\n", deviceInfo.nDevicePort);
    printf("UsrName:%s\n", (char*)[deviceInfo.chLoginName cStringUsingEncoding:enc]);
    printf("Password:%s\n", (char*)[deviceInfo.chPassWord UTF8String]);
    
    if(_m_lUserID == -1){
        [self showMessage:@"登陆失败！"];
        return false;
    }
    
    if(logindeviceInfo.byChanNum >0){
        g_iStartChan = logindeviceInfo.byStartChan;
        g_iPreviewChanNum = logindeviceInfo.byChanNum;
    }else if(logindeviceInfo.byIPChanNum>0){
        g_iStartChan = logindeviceInfo.byStartDChan;
        g_iPreviewChanNum = logindeviceInfo.byIPChanNum+logindeviceInfo.byHighDChanNum*256;
    }
    
    if(g_iPreviewChanNum>1 && [self isPureInt:channel]){
        int channelNum = [channel intValue];
        if(channelNum >= g_iStartChan && channelNum < g_iStartChan+g_iPreviewChanNum){
            if(g_iPreviewChanNum>1){
                g_iStartChan = channelNum;
                g_iPreviewChanNum = 1;
            }
        }else{
            NSString* errorStr = @"通道号配置错误 ,起始通道号是 %d ,共有 %d 个通道. ";
            errorStr = [NSString stringWithFormat:errorStr ,g_iStartChan ,g_iPreviewChanNum];
            [self showMessage:errorStr];
            return false;
        }
    }
    
    [self viewDidLoad:g_iPreviewChanNum];    
    return true;
}

-(BOOL)isPureInt:(NSString*)str{
    NSScanner* scan = [NSScanner scannerWithString:str];
    int val;
    return [scan scanInt:&val] && [scan isAtEnd];
}

-(void)beginPlayVideo {
    if(g_iPreviewChanNum>1){ //多路视频
        if(!_m_bPreview){
            int iPreviewID[g_iPreviewChanNum];
            for(int i =0; i<g_iPreviewChanNum; i++){
                iPreviewID[i] = startPreview(_m_lUserID, g_iStartChan, m_multiView[i], i);
                if(iPreviewID[i] >= 0){
                    _m_lRealPlayID = iPreviewID[i];
                }else{
                    [self showPlayError];
                }
            }
            _m_bPreview = true;
            
        }else{
            for(int i=0; i<g_iPreviewChanNum; i++){
                stopPreview(i);
            }
            _m_bPreview = false;
            
        }
    }else{
        if(!_m_bPreview){
            _m_lRealPlayID = startPreview(_m_lUserID, g_iStartChan, _m_playView, 0);
            if(_m_lRealPlayID >= 0){
                _m_bPreview = true;
            }else{
                [self showPlayError];
            }
        }else{
            stopPreview(0);
            _m_bPreview = false;
            
        }
    }
}

-(CDVMPlayVideo*)initPlayview:(UIView*)playView {
    
    _m_lUserID = -1;
    _m_lRealPlayID = -1;
    _m_lPlaybackID = -1;
    _m_nPlaybackPort = -1;
    _m_bRecord = false;
    _m_bPTZL = false;
    self.m_playView = playView;
    
    return self;
}

-(void)showPlayError {
    int code = NET_DVR_GetLastError();
    NSString *errorMsg  = nil;
    switch (code) {
        case 4:
            errorMsg = [NSString stringWithFormat:@"通道号错误，错误码 %d ",code];
            [self showMessage:errorMsg];
            break;
        case 13:
            errorMsg = [NSString stringWithFormat:@"没有权限，错误码 %d ",code];
            [self showMessage:errorMsg];
            break;
        default:
            errorMsg = [NSString stringWithFormat:@"播放出错，错误码 %d ",code];
            [self showMessage:errorMsg];
            break;
    }
    
}

-(void)showMessage:(NSString*)msg {
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil message:msg delegate:nil cancelButtonTitle:@"确定" otherButtonTitles:nil];
    
    [alert show];
    [alert release];
}

-(void)releaseVideo{
    if (_m_lRealPlayID != -1)
    {
        NET_DVR_StopRealPlay(_m_lRealPlayID);
        _m_lRealPlayID = -1;
    }
    
    if(_m_lPlaybackID != -1)
    {
        NET_DVR_StopPlayBack(_m_lPlaybackID);
        _m_lPlaybackID = -1;
        
    }
    
    if(_m_lUserID != -1)
    {
        NET_DVR_Logout(_m_lUserID);
        NET_DVR_Cleanup();
        _m_lUserID = -1;
    }
}

-(void) dealloc {
    self.m_playView = nil;
    [super dealloc];
}


-(void)viewDidLoad:(int)num
{
    int len = num;
    if(len <= 1){
        return;
    }
    if(len > MAX_VIEW_NUM){
        len = MAX_VIEW_NUM;
        [self showMessage:[NSString stringWithFormat:@"最大显示 %d 个视频" ,MAX_VIEW_NUM]];
    }
    int rowN = 2;
    if(len == 2){
        rowN = 1;
    }
    int width = _m_playView.frame.size.width;
    int height = _m_playView.frame.size.height;
    itemW = width / rowN;
    itemH = itemW * 3 / 4;
    
    UIScrollView *scrollView = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 0, width, height)];
    scrollView.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleWidth;

    for(int i = 0; i<len; i++){
        VideoUIView *view= [[VideoUIView alloc] initWithFrame:CGRectMake((i%rowN) * itemW, (i/rowN) * itemH, itemW - 1, itemH - 1)];
        view.backgroundColor = [UIColor blackColor];
        view.px = (i%rowN) * itemW;
        view.py = (i/rowN) * itemH;
        UITapGestureRecognizer *tap = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(viewTapAction:)];
        [view addGestureRecognizer:tap];
        
        m_multiView[i] = view;
        [scrollView addSubview:view];
        [view release];
    }
    
    [scrollView setContentSize:CGSizeMake(width, (len/rowN) * itemH)];
    scrollView.contentInset=UIEdgeInsetsMake(10.0,0.0,30.0,0.0);
    
    [_m_playView addSubview:scrollView];
    
}

-(IBAction)viewTapAction:(UITapGestureRecognizer*)sender{
    
    int width = _m_playView.frame.size.width;
    int height = _m_playView.frame.size.height;
    VideoUIView* view = (VideoUIView*)sender.view;
    int rW = view.frame.size.width;
    UIScrollView *scrollView = (UIScrollView*)view.superview;
    
    if(rW>itemW){
        [view setFrame:CGRectMake(view.px, view.py, itemW-1, itemH-1)];
        scrollView.scrollEnabled = YES;
    }else{
        scrollView.contentOffset = CGPointMake(0,0);
        scrollView.scrollEnabled = NO;
        [view setFrame:CGRectMake(0, 0, width, height)];
    }
    
    [scrollView bringSubviewToFront:view];
    
}

@end


@implementation VideoUIView
@end




