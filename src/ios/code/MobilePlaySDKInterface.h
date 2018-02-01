/** @file    MobilePlaySDKInterface.h
 *  @note    HIKVISION
 *  @brief   Implementation of Image Data Conversion API
 *
 *  @author  Media PlaySDK Team
 *  @date    2017/01/18
 *
 *  @note    Mobile PlaySDK Interface for Android and iOS
 *  @note    V1.1 PlayCtrl Version 7.3.4.x
 */

#ifndef __MOBILE_PLAY_SDK_INTERFACE_H__
#define __MOBILE_PLAY_SDK_INTERFACE_H__

#ifdef __cplusplus
extern "C"
{
#endif

typedef void * PLAYM4_HWND;
typedef void * PLAYM4_HDC;

#define PLAYM4_API 
#define __stdcall

#ifndef CALLBACK
#define CALLBACK
#endif

    //Timer type
#define TIMER_1 1 //Only 16 timers for every process.Default TIMER;
#define TIMER_2 2 //Not limit;But the precision less than TIMER_1;
    
// 播放库支持的最大port号
// Max channel numbers
#define PLAYM4_MAX_SUPPORTS             32

// 针对PlayM4_AdjustWaveAudio接口，调节范围
// PlayM4_AdjustWaveAudio，Wave coefficient range;
#define MIN_WAVE_COEF                   -100
#define MAX_WAVE_COEF                   100

//BUFFER TYPE
#define BUF_VIDEO_SRC                1
#define BUF_AUDIO_SRC                2
#define BUF_VIDEO_RENDER             3
#define BUF_AUDIO_RENDER             4
#define BUF_VIDEO_DECODED           (5) //video decoded node count to render
#define BUF_AUDIO_DECODED           (6) //audio decoded node count to render

//Error code
#define  PLAYM4_NOERROR                 0   //no error
#define  PLAYM4_PARA_OVER               1   //input parameter is invalid;
#define  PLAYM4_ORDER_ERROR             2   //The order of the function to be called is error.
#define  PLAYM4_TIMER_ERROR             3   //Create multimedia clock failed;
#define  PLAYM4_DEC_VIDEO_ERROR         4   //Decode video data failed.
#define  PLAYM4_DEC_AUDIO_ERROR         5   //Decode audio data failed.
#define  PLAYM4_ALLOC_MEMORY_ERROR      6   //Allocate memory failed.
#define  PLAYM4_OPEN_FILE_ERROR         7   //Open the file failed.
#define  PLAYM4_CREATE_OBJ_ERROR        8   //Create thread or event failed
#define  PLAYM4_BUF_OVER               11   //buffer is overflow
#define  PLAYM4_CREATE_SOUND_ERROR     12   //failed when creating audio device.	
#define  PLAYM4_SET_VOLUME_ERROR       13   //Set volume failed
#define  PLAYM4_SUPPORT_FILE_ONLY      14   //The function only support play file.
#define  PLAYM4_SUPPORT_STREAM_ONLY    15   //The function only support play stream.
#define  PLAYM4_SYS_NOT_SUPPORT        16   //System not support.
#define  PLAYM4_FILEHEADER_UNKNOWN     17   //No file header.
#define  PLAYM4_VERSION_INCORRECT      18   //The version of decoder and encoder is not adapted.  
#define  PLAYM4_INIT_DECODER_ERROR     19   //Initialize decoder failed.
#define  PLAYM4_CHECK_FILE_ERROR       20   //The file data is unknown.
#define  PLAYM4_INIT_TIMER_ERROR       21   //Initialize multimedia clock failed.
#define  PLAYM4_BLT_ERROR              22   //Display failed.
#define  PLAYM4_OPEN_FILE_ERROR_MULTI  24   //openfile error, streamtype is multi
#define  PLAYM4_OPEN_FILE_ERROR_VIDEO  25   //openfile error, streamtype is video
#define  PLAYM4_JPEG_COMPRESS_ERROR    26   //JPEG compress error
#define  PLAYM4_EXTRACT_NOT_SUPPORT    27   //Don't support the version of this file.
#define  PLAYM4_EXTRACT_DATA_ERROR     28   //extract video data failed.
#define  PLAYM4_SECRET_KEY_ERROR       29   //Secret key is error //add 20071218
#define  PLAYM4_DECODE_KEYFRAME_ERROR  30   //add by hy 20090318
#define  PLAYM4_NEED_MORE_DATA         31   //add by hy 20100617
#define  PLAYM4_INVALID_PORT           32   //add by cj 20100913
#define  PLAYM4_NOT_FIND               33  //add by cj 20110428
#define  PLAYM4_NEED_LARGER_BUFFER     34  //add by pzj 20130528
#define  PLAYM4_DEMUX_ERROR            35  //demux err
#define  PLAYM4_FAIL_UNKNOWN           99   //Fail, but the reason is unknown;
    
//鱼眼功能错误码
#define PLAYM4_FEC_ERR_ENABLEFAIL               100 // 鱼眼模块加载失败
#define PLAYM4_FEC_ERR_NOTENABLE                101 // 鱼眼模块没有加载
#define PLAYM4_FEC_ERR_NOSUBPORT                102 // 子端口没有分配
#define PLAYM4_FEC_ERR_PARAMNOTINIT             103 // 没有初始化对应端口的参数
#define PLAYM4_FEC_ERR_SUBPORTOVER              104 // 子端口已经用完
#define PLAYM4_FEC_ERR_EFFECTNOTSUPPORT         105 // 该安装方式下这种效果不支持
#define PLAYM4_FEC_ERR_INVALIDWND               106 // 非法的窗口
#define PLAYM4_FEC_ERR_PTZOVERFLOW              107 // PTZ位置越界
#define PLAYM4_FEC_ERR_RADIUSINVALID            108 // 圆心参数非法
#define PLAYM4_FEC_ERR_UPDATENOTSUPPORT         109 // 指定的安装方式和矫正效果，该参数更新不支持
#define PLAYM4_FEC_ERR_NOPLAYPORT               110 // 播放库端口没有启用
#define PLAYM4_FEC_ERR_PARAMVALID               111 // 参数为空
#define PLAYM4_FEC_ERR_INVALIDPORT              112 // 非法子端口
#define PLAYM4_FEC_ERR_PTZZOOMOVER              113 // PTZ矫正范围越界
#define PLAYM4_FEC_ERR_OVERMAXPORT              114 // 矫正通道饱和，最大支持的矫正通道为四个
#define PLAYM4_FEC_ERR_ENABLED                  115 // 该端口已经启用了鱼眼模块
#define PLAYM4_FEC_ERR_D3DACCENOTENABLE         116 // D3D加速没有开启
#define PLAYM4_FEC_ERR_PLACE_TYPE               117 // 错误的矫正类型
    
//Max display regions.
#define MAX_DISPLAY_WND                4

//Display buffers
#define MAX_DIS_FRAMES                 50
#define MIN_DIS_FRAMES                 1

//Locate by
#define BY_FRAMENUM                    1
#define BY_FRAMETIME                   2
    
//Source buffer
#define SOURCE_BUF_MAX               (1024*100000)
#define SOURCE_BUF_MIN               (1024*50)

//Stream type
#define STREAME_REALTIME              0
#define STREAME_FILE                  1

//frame type
#define T_AUDIO16                     101
#define T_AUDIO8                      100
#define T_UYVY                        1
#define T_YV12                        3
#define T_RGB32                       7

// 以下宏定义用于HIK_MEDIAINFO结构
#define FOURCC_HKMI                   0x484B4D49     // "HKMI" HIK_MEDIAINFO结构标记
// 系统封装格式 
#define SYSTEM_NULL                   0x0            // 没有系统层，纯音频流或视频流	
#define SYSTEM_HIK                    0x1            // 海康文件层
#define SYSTEM_MPEG2_PS               0x2            // PS封装
#define SYSTEM_MPEG2_TS               0x3            // TS封装
#define SYSTEM_RTP                    0x4            // rtp封装
#define SYSTEM_RTPHIK                 0x401          // rtp封装

// 视频编码类型
#define VIDEO_NULL                    0x0           // 没有视频
#define VIDEO_H264                    0x1           // 标准H.264和海康H.264都可以用这个定义
#define VIDEO_MPEG4                   0x3           // 标准MPEG4
#define VIDEO_MJPEG                   0x4           
#define VIDEO_AVC264                  0x0100

// 音频编码类型
#define AUDIO_NULL                    0x0000        // 没有音频
#define AUDIO_ADPCM                   0x1000        // ADPCM 
#define AUDIO_MPEG                    0x2000        // MPEG 系列音频，解码器能自适应各种MPEG音频
// G系列音频
#define AUDIO_RAW_DATA8               0x7000        // 采样率为8k的原始数据
#define AUDIO_RAW_UDATA16             0x7001        // 采样率为16k的原始数据，即L16
#define AUDIO_G711_U                  0x7110
#define AUDIO_G711_A                  0x7111
#define AUDIO_G722_1                  0x7221
#define AUDIO_G723_1                  0x7231
#define AUDIO_G726_U                  0x7260
#define AUDIO_G726_A                  0x7261
#define AUDIO_G729                    0x7290
#define AUDIO_AMR_NB                  0x3000

// 系统时间（码流中的全局时间）
typedef struct tagSystemTime
{
    short wYear;
    short wMonth;
    short wDayOfWeek;
    short wDay;
    short wHour;
    short wMinute;
    short wSecond;
    short wMilliseconds;
}SYSTEMTIME;

typedef struct tagHKRect
{
    unsigned long nLeft;
    unsigned long nTop;
    unsigned long nRight;
    unsigned long nBottom;
}HKRECT;

//Frame Info
typedef struct
{
    int nWidth;
    int nHeight;
    int nStamp;
    int nType;
    int nFrameRate;
    unsigned int dwFrameNum;
}FRAME_INFO;
        
//Watermark Info  //add by gb 080119
typedef struct
{
    char *pDataBuf;
    int  nSize;
    int  nFrameNum;
    int  bRsaRight;
    int  nReserved;
}WATERMARK_INFO;
    
//ENCRYPT Info
typedef struct{
    long nVideoEncryptType;  //视频加密类型
    long nAudioEncryptType;  //音频加密类型
    long nSetSecretKey;      //是否设置，1表示设置密钥，0表示没有设置密钥
}ENCRYPT_INFO;

#ifndef _HIK_MEDIAINFO_FLAG_
#define _HIK_MEDIAINFO_FLAG_
typedef struct _HIK_MEDIAINFO_              // modified by gb 080425
{
    unsigned int    media_fourcc;           // "HKMI": 0x484B4D49 Hikvision Media Information
    unsigned short  media_version;          // 版本号：指本信息结构版本号，目前为0x0101,即1.01版本，01：主版本号；01：子版本号。
    unsigned short  device_id;              // 设备ID，便于跟踪/分析 

    unsigned short  system_format;          // 系统封装层
    unsigned short  video_format;           // 视频编码类型

    unsigned short  audio_format;           // 音频编码类型
    unsigned char   audio_channels;         // 通道数  
    unsigned char   audio_bits_per_sample;  // 样位率
    unsigned int    audio_samplesrate;      // 采样率 
    unsigned int    audio_bitrate;          // 压缩音频码率,单位：bit

    unsigned int    reserved[4];            // 保留
}HIK_MEDIAINFO;
#endif
        
typedef struct
{
    int nPort;
    char * pBuf;
    int nBufLen;
    int nWidth;
    int nHeight;
    int nStamp;
    int nType;
    void* nUser;
}DISPLAY_INFO;

typedef struct PLAYM4_SYSTEM_TIME //绝对时间 
{
    unsigned int dwYear;   //年
    unsigned int dwMon;    //月
    unsigned int dwDay;    //日
    unsigned int dwHour;   //时
    unsigned int dwMin;    //分
    unsigned int dwSec;    //秒
    unsigned int dwMs;     //毫秒
} PLAYM4_SYSTEM_TIME;
        
//Frame position
typedef struct
{
    int nFilePos;
    int nFrameNum;
    int nFrameTime;
    int nErrorFrameNum;
    SYSTEMTIME *pErrorTime;
    int nErrorLostFrameNum;
    int nErrorFrameSize;
}FRAME_POS, *PFRAME_POS;
    
///<矫正类型
#ifndef _TAG_VR_DISPLAY_EFFECT_
#define _TAG_VR_DISPLAY_EFFECT_
typedef enum tagVRDisplayEffect
{
    VR_ET_NULL                      = 0x100,        ///<不矫正
    VR_ET_FISH_PTZ_CEILING          = 0x101,        ///<应用于顶装鱼眼
    VR_ET_FISH_PTZ_FLOOR            = 0x102,        ///<应用于地面安装鱼眼
    VR_ET_FISH_PTZ_WALL             = 0x103,        ///<应用于壁装鱼眼
    VR_ET_FISH_PANORAMA_CEILING360  = 0x104,        ///<应用于顶装鱼眼1P
    VR_ET_FISH_PANORAMA_CEILING180  = 0x105,        ///<应用于顶装鱼眼2P
    VR_ET_FISH_PANORAMA_FLOOR360    = 0x106,        ///<应用于地面安装鱼眼1P
    VR_ET_FISH_PANORAMA_FLOOR180    = 0x107,        ///<应用于地面安装鱼眼2P
    VR_ET_FISH_LATITUDE_WALL        = 0x108,        ///<应用于壁装维度展开(广角)
}VRDISPLAYEFFECT;
#endif
    
///<鱼眼参数结构
#ifndef _TAG_VR_FISH_PARAM_
#define _TAG_VR_FISH_PARAM_
typedef struct tagVRFishParam
{
    float fRXLeft;                                  ///<水平坐标(min)
    float fRXRight;                                 ///<水平坐标(max)
    float fRYTop;                                   ///<垂直坐标(min)
    float fRYBottom;                                ///<垂直坐标(max)
    float fAngle;                                   ///<180°矫正中心角
    float fZoom;                                    ///<PTZ矫正放大系数
    float fPTZX;                                    ///<PTZ矫正的中心坐标
    float fPTZY;                                    ///<PTZ矫正的中心坐标
}VRFISHPARAM;
#endif
#ifndef _PLAYM4_SESSION_INFO
#define _PLAYM4_SESSION_INFO
typedef struct _PLAYM4_SESSION_INFO_     //
{
    int            nSessionInfoType;   //
    int            nSessionInfoLen;    //
    unsigned char* pSessionInfoData;   //
        
}PLAYM4_SESSION_INFO;
#endif
typedef struct
{
    long    lDataType;          //私有数据类型
    long    lDataStrVersion;    //数据返回的结构体版本，主要是为了兼容性
    long    lDataTimeStamp;
    long    lDataLength;
    char*   pData;
}AdditionDataInfo;
    
typedef struct
{
    long nPort;
    char *pVideoBuf;
    long nVideoBufLen;
    char *pPriBuf;
    long nPriBufLen;
    long nWidth;
    long nHeight;
    long nStamp;
    long nType;
    void* nUser;
}DISPLAY_INFOEX;
    
typedef struct SYNCDATA_INFO
{
    unsigned int  dwDataType;        //和码流数据同步的附属信息类型，目前有：智能信息，车载信息
    unsigned int  dwDataLen;        //附属信息数据长度
    unsigned int  *pData;           //指向附属信息数据结构的指针,比如IVS_INFO结构
} SYNCDATA_INFO;
    
typedef struct
{
    int            nRunTimeModule;     //当前运行模块
    int            nStrVersion;        //结构体版本
    int            nFrameTimeStamp;    //帧号
    int            nFrameNum;          //时间戳
    int            nErrorCode;         //错误码
    unsigned char  nChangeEncode;      //1表示视频编码改变，2表示音频编码改变
    unsigned char  reserved[11];       //保留字节
}RunTimeInfo;
    
//预录像数据信息
typedef struct
{
    int nType;                     // 数据类型，如文件头，视频，音频，私有数据等
    int nStamp;                    // 时间戳
    int nFrameNum;                 // 帧号
    int  nBufLen;                   // 数据长度
    char* pBuf;                     // 帧数据，以帧为单位回调
    PLAYM4_SYSTEM_TIME  stSysTime;  // 全局时间
}RECORD_DATA_INFO;
    
//Frame
typedef struct{
    char *pDataBuf;
    long  nSize;
    long  nFrameNum;
    bool  bIsAudio;
    long  nReserved;
}FRAME_TYPE;
#ifndef CROP_PIC_INFO_TAG
#define CROP_PIC_INFO_TAG
    typedef struct
    {
        unsigned char  *pDataBuf;      //抓图数据buffer
        unsigned long dwPicSize;      //实际图片大小
        unsigned long dwBufSize;      //数据buffer大小
        unsigned long dwPicWidth;     //截图宽
        unsigned long dwPicHeight;    //截图高
        unsigned long dwReserve;      //多加一个reserve字段
        HKRECT       *pCropRect;     //选择区域NULL, 同老的抓图接口
    }CROP_PIC_INFO;
#endif
    
///<私有信息模块类型
typedef enum _PLAYM4_PRIDATA_RENDER
{
    PLAYM4_RENDER_ANA_INTEL_DATA    = 0x00000001, //智能分析
    PLAYM4_RENDER_MD                = 0x00000002, //移动侦测
    PLAYM4_RENDER_ADD_POS           = 0x00000004, //POS信息后叠加
    PLAYM4_RENDER_ADD_PIC           = 0x00000008, //图片叠加信息
    PLAYM4_RENDER_FIRE_DETCET       = 0x00000010, //热成像信息
    PLAYM4_RENDER_TEM               = 0x00000020, //温度信息
}PLAYM4_PRIDATA_RENDER;
    
typedef enum _PLAYM4_FIRE_ALARM
{
    PLAYM4_FIRE_FRAME_DIS           = 0x00000001, //火点框显示
    PLAYM4_FIRE_MAX_TEMP            = 0x00000002, //最高温度
    PLAYM4_FIRE_MAX_TEMP_POSITION   = 0x00000004, //最高温度位置显示
    PLAYM4_FIRE_DISTANCE            = 0x00000008, //最高温度距离
}PLAYM4_FIRE_ALARM;

typedef enum _PLAYM4_TEM_FLAG
{
    PLAYM4_TEM_REGION_BOX             = 0x00000001, //框测温
    PLAYM4_TEM_REGION_LINE            = 0x00000002, //线测温
    PLAYM4_TEM_REGION_POINT           = 0x00000004, //点测温
}PLAYM4_TEM_FLAG;

#ifndef PLAYM4_HIKSR_TAG
#define PLAYM4_HIKSR_TAG
//旋转单元结构体
typedef struct tagPLAYM4SRTransformElement
{
    float fAxisX;
    float fAxisY;
    float fAxisZ;
    float fValue;
}PLAYM4SRTRANSFERELEMENT;
    
//旋转组合参数
typedef struct tagPLAYM4SRTransformParam
{
    PLAYM4SRTRANSFERELEMENT* pTransformElement;  // 旋转的坐标轴
    unsigned int		 nTransformCount;		 // 旋转组合次数
}PLAYM4SRTRANSFERPARAM;
#endif
//API
//////////////////////////////////////////////////////////////////////////////

// Baseline 1
// 打开待播放文件
//Open the file want to play
int            PlayM4_OpenFile(int nPort, char *sFileName);

// Baseline 2
// 关闭播放文件
//
int            PlayM4_CloseFile(int nPort);

// Baseline 3
// 开启播放
//
int            PlayM4_Play(int nPort, PLAYM4_HWND hWnd);

// Baseline 4
// 停止播放
//
int            PlayM4_Stop(int nPort);

// Baseline 5
// 暂停播放
//
int            PlayM4_Pause(int nPort, unsigned int nPause);

// Baseline 6
// 加速，每调一次速度*2
//
int            PlayM4_Fast(int nPort);

// Baseline 7
// 减速，每调一次速度/2
//
int            PlayM4_Slow(int nPort);

// Baseline 8
// 开启声音
//
int            PlayM4_PlaySound(int nPort);

// Baseline 9
// 关闭声音
//
int            PlayM4_StopSound();

// Baseline 10
// 设置开启流播放模式
//
int            PlayM4_SetStreamOpenMode(int nPort, unsigned int nMode);

// Baseline 11
// 实时流、回放流时40字节头开流；回放流时可以无头开流
//
int            PlayM4_OpenStream(int            nPort,
                                 unsigned char *pFileHeadBuf,
                                 unsigned int   nSize,
                                 unsigned int   nBufPoolSize);

// Baseline 12
// 实时流、回放流送数据
//
int            PlayM4_InputData(int nPort, unsigned char *pBuf, unsigned int nSize);

// Baseline 13
// 关闭流
//
int            PlayM4_CloseStream(int nPort);

// Baseline 14
// 获得文件总时长，单位：秒
//
unsigned int   PlayM4_GetFileTime(int nPort);

// Baseline 15
// 当前播放时长，单位：秒
//
unsigned int   PlayM4_GetPlayedTime(int nPort);

// Baseline 16
// 当前播放时长，单位：毫秒
//
unsigned int   PlayM4_GetPlayedTimeEx(int nPort);

// Baseline 17
// 按时间定位，单位：毫秒
//
int            PlayM4_SetPlayedTimeEx(int nPort, unsigned int nTime);

// Baseline 18
// 解码回调
// 不建议使用，用PlayM4_RegisterDecCallBack代替
int            PlayM4_SetDecCallBack(int nPort,
                                     void (CALLBACK* DecCBFun)(int         nPort,
                                                               char*       pBuf,
                                                               int         nSize,
                                                               FRAME_INFO* pFrameInfo,
                                                               void*       nReserved1,
                                                               void*       nReserved2));

// Baseline 19
// 显示回调
// 不建议使用，用PlayM4_RegisterDisplayCallBackEx代替
int            PlayM4_SetDisplayCallBack(int nPort,
                                         void (CALLBACK* DisplayCBFun)(int nPort,
                                                                       char *pBuf,
                                                                       int nSize,
                                                                       int nWidth,
                                                                       int nHeight,
                                                                       int nStamp,
                                                                       int nType,
                                                                       void* nReserved));

// Baseline 20
// 显示回调，带用户参数
// 不建议使用，用PlayM4_RegisterDisplayCallBackEx代替
int            PlayM4_SetDisplayCallBackEx(int nPort,
                                           void (CALLBACK* DisplayCBFun)(DISPLAY_INFO *pstDisplayInfo),
                                           void* nUser);

// Baseline 21
// 获得当前码流帧率
//
int            PlayM4_GetCurrentFrameRateEx(int nPort, float* pfFrameRate);

// Baseline 22
// 当前播放库版本号
//
unsigned int   PlayM4_GetSdkVersion();

// Baseline 23
// 获得错误码
//
unsigned int   PlayM4_GetLastError(int nPort);

// Baseline 24
// 刷新当前窗口
//
int            PlayM4_RefreshPlay(int nPort);

// Baseline 25
// 获得码流分辨率
//
int            PlayM4_GetPictureSize(int nPort, int *pWidth, int *pHeight);

// Baseline 26
// 获得解析缓存中，剩余数据量大小
//
unsigned int   PlayM4_GetSourceBufferRemain(int nPort);

// Baseline 27
// 重置解析缓存
//
int            PlayM4_ResetSourceBuffer(int nPort);

// Baseline 28
// 设置解码后缓存。Android硬解码时，nNum表示实际设置的延时值，单位毫秒
//
int            PlayM4_SetDisplayBuf(int nPort,unsigned int nNum);

// Baseline 29
// 获得解码后缓存
//
unsigned int   PlayM4_GetDisplayBuf(int nPort);

// Baseline 30
// 设置文件索引回调
//
int            PlayM4_SetFileRefCallBack(int nPort,
                                         void (__stdcall *pFileRefDone)( int nPort,void*  nUser),
                                         void* nUser);
// Baseline 31
// 电子放大
//
int            PlayM4_SetDisplayRegion(int          nPort,
                                       unsigned int nRegionNum,
                                       HKRECT*      pSrcRect,
                                       PLAYM4_HWND  hDestWnd,
                                       int          bEnable);

// Baseline 32
// 重置解析、解码、渲染等缓存
//
int            PlayM4_ResetBuffer(int nPort, unsigned int nBufType);

// Baseline 33
// 获取指定缓冲区的大小
//
unsigned int   PlayM4_GetBufferValue(int nPort, unsigned int nBufType);

// Baseline 34
// 设置要解码的帧类型.默认正常解码，1表示只解码I帧，6表示无论多大分辨率都全解码；3,4,5分别表示svc码流只解码1/2,1/4,1/8
//
int            PlayM4_SetDecodeFrameType(int nPort, unsigned int nFrameType);

// Baseline 35
// yuv数据转jpeg文件
//
int            PlayM4_ConvertToJpegFile(char*   pBuf,
                                        int     nSize,
                                        int     nWidth,
                                        int     nHeight,
                                        int     nType,
                                        char*   sFileName);

// Baseline 36
// 抓取BMP图
//
int            PlayM4_GetBMP(int            nPort,
                             unsigned char* pBitmap,
                             unsigned int   nBufSize,
                             unsigned int*  pBmpSize);

// Baseline 37
// 抓取JPEG图
//
int            PlayM4_GetJPEG(int               nPort,
                              unsigned char*    pJpeg,
                              unsigned int      nBufSize,
                              unsigned int*     pJpegSize);

// Baseline 38
// 设置解密秘钥
//
int            PlayM4_SetSecretKey(int nPort, int lKeyType, char *pSecretKey, int lKeyLen);

// Baseline 39
// 设置文件播放结束回调
//
int            PlayM4_SetFileEndCallback(int nPort,
                                         void(CALLBACK*FileEndCallback)(int nPort, void *pUser),
                                         void *pUser);

// Baseline 40
// 获得播放端口
//
int            PlayM4_GetPort(int* nPort);

// Baseline 41
// 释放播放端口
//
int            PlayM4_FreePort(int nPort);

// Baseline 42
// 跳过错误数据，默认开启
//
int            PlayM4_SkipErrorData(int nPort, int bSkip);

// Baseline 43
// 获得当前显示帧系统时间
//
int            PlayM4_GetSystemTime(int nPort, PLAYM4_SYSTEM_TIME *pstSystemTime);

// Baseline 44
// 获得当前显示帧系统时间，需要应用层转化
//
unsigned int   PlayM4_GetSpecialData(int nPort);

// Baseline 45
// 设置播放窗口
//
int            PlayM4_SetVideoWindow(int nPort, unsigned int nRegionNum, PLAYM4_HWND hWnd);

// Baseline 46
// 带用户参数解码回调，不带显示。
// 不建议使用，用PlayM4_RegisterDecCallBack代替
int            PlayM4_SetDecCallBackMend(int nPort,
                                         void (CALLBACK* DecCBFun)(int          nPort,
                                                                   char*        pBuf,
                                                                   int          nSize,
                                                                   FRAME_INFO*  pFrameInfo,
                                                                   void*        nUser,
                                                                   void*        nReserved2),
                                         void* nUser);
    

// Baseline 47
// 设置图像翻转
//
int            PlayM4_SetVerticalFlip(unsigned int nPort, int bFlag);

// Baseline 48
// 广角矫正
//
int            PlayM4_SetImageCorrection(int nPort, int bEnable);
    
// Baseline 49
//老版本鱼眼接口，设置鱼眼矫正效果
//
int            PlayM4_SetFECDisplayEffect(int nPort, int nRegionNum, VRDISPLAYEFFECT enDisplayEffect);

// Baseline 50
// 老版本鱼眼接口，设置鱼眼矫正参数
//
int            PlayM4_SetFECDisplayParam(int nPort, int nRegionNum, VRFISHPARAM *pstFishParam);

// Baseline 51
// 老版本鱼眼接口，获取鱼眼矫正参数
//
int            PlayM4_GetFECDisplayParam(int nPort, int nRegionNum, VRFISHPARAM *pstFishParam);

// Baseline 52
// 预录像接口，设置预录像开关
//
int            PlayM4_SetPreRecordFlag(int nPort, bool bFlag);

// Baseline 53
// 预录像接口，设置录像文件数据回调
//
int            PlayM4_SetPreRecordCallBack(int nPort,
                                           void (CALLBACK* PreRecordCBfun)(int nPort,
                                                                           void* pData,
                                                                           unsigned int nDataLen,
                                                                           void *pUser),
                                           void *pUser);

// Baseline 54
// 使用优先使用硬解码
//
int            PlayM4_SetHDPriority(int nPort);

// Baseline 55
// 获得当前解码引擎类型
//
int            PLAYM4_GetDecodeEngine(int nPort);

// Baseline 56
// 设置同步回放组，dwGroupIndex 暂约定取值0~3
//
int            PlayM4_SetSycGroup(int nPort, unsigned int dwGroupIndex);

// Baseline 57
// 渲染私有数据，大类
//
int            PlayM4_RenderPrivateData(int nPort, int nIntelType, int bTrue);

// Baseline 58
// 渲染私有数据，子类型
//
int            PlayM4_RenderPrivateDataEx(int nPort, int nIntelType, int nSubType, int bTrue);

// Baseline 59
// 设置视音频同步，默认开启
//
int            PlayM4_SyncToAudio(int nPort, int bSyncToAudio);

// Baseline 60
// 私有数据回调，车载信息等
//
int            PlayM4_SetAdditionDataCallBack(int nPort, 
                                              unsigned int nSyncType, 
                                              void (CALLBACK* AdditionDataCBFun)(int nPort, 
                                              AdditionDataInfo* pstAddDataInfo, 
                                              void* pUser), 
                                              void* pUser);

// Baseline 61
// 私有数据回调，点、线、框相关
//
int            PlayM4_RegisterIVSDrawFunCB(int nPort,
                                           void (CALLBACK* IVSDrawFun)(int  nPort,
                                           PLAYM4_HDC hDC,
                                           FRAME_INFO* pFrameInfo,
                                           SYNCDATA_INFO* pSyncData,
                                           void*  dwUser,
                                           int bDettach),
                                           void* dwUser);

// Baseline 62
// 获取mp4在线定位偏移
//
int            PLAYM4_GetMpOffset(int nPort, int nTime, int* nOffset);
    
// 以下实现鱼眼相关的接口
#ifndef _FISHEYE_DEF_
#define _FISHEYE_DEF_

#define R_ANGLE_0   -1  //不旋转
#define R_ANGLE_L90  0  //向左旋转90度
#define R_ANGLE_R90  1  //向右旋转90度
#define R_ANGLE_180  2  //旋转180度

#ifndef _FISH_STURCT_
#define _FISH_STURCT_

// 矫正类型
typedef enum tagFECPlaceType
{
    FEC_NULL          = 0x0,        // 不矫正
    FEC_PLACE_WALL    = 0x1,        // 壁装方式  (法线水平)
    FEC_PLACE_FLOOR   = 0x2,        // 地面安装  (法线向上)
    FEC_PLACE_CEILING = 0x3,        // 顶装方式  (法线向下)
}FECPLACETYPE;

typedef enum tagFECCorrectType
{
    FEC_CORRECT_PTZ = 0x100,        // PTZ
    FEC_CORRECT_180 = 0x200,        // 180度矫正  （对应2P）
    FEC_CORRECT_360 = 0x300,        // 360全景矫正 （对应1P）
    FEC_CORRECT_LAT = 0x400,         // 维度拉伸
    FEC_CORRECT_SEM = 0x500,        // 半球显示
    FEC_CORRECT_CYC = 0x600,        // 圆柱显示（桶形）
    FEC_CORRECT_PLA = 0x700,        // 小行星
    FEC_CORRECT_CYC_SPL = 0x800,     //圆柱显示（剪开）
    FEC_CORRECT_ARC = 0x900,        //壁装弧面鱼眼
}FECCORRECTTYPE;
    
// PTZ在原始鱼眼图上轮廓的显示模式
typedef enum tagFECShowMode
{
    FEC_PTZ_OUTLINE_NULL,   // 不显示
    FEC_PTZ_OUTLINE_RECT,   // 矩形显示
    FEC_PTZ_OUTLINE_RANGE,  // 真实区域显示
}FECSHOWMODE;

typedef struct tagCycleParam
{
    float	fRadiusLeft;           // 圆的最左边X坐标
    float	fRadiusRight;          // 圆的最右边X坐标
    float   fRadiusTop;            // 圆的最上边Y坐标
    float   fRadiusBottom;         // 圆的最下边Y坐标
}CYCLEPARAM;

typedef struct tagPTZParam
{
    float fPTZPositionX;     // PTZ 显示的中心位置 X坐标
    float fPTZPositionY;     // PTZ 显示的中心位置 Y坐标
}PTZPARAM;

typedef struct tagFECParam
{
    unsigned int    nUpDateType;            // 更新的类型
    unsigned int    nPlaceAndCorrect;       // 安装方式和矫正方式，只能用于获取，SetParam的时候无效,该值表示安装方式和矫正方式的和
    PTZPARAM        stPTZParam;             // PTZ 校正的参数
    CYCLEPARAM      stCycleParam;           // 鱼眼图像圆心参数
    float           fZoom;                  // PTZ 显示的范围参数
    float           fWideScanOffset;        // 180或者360度校正的偏移角度
    int             nResver[16];            // 保留字段
}FISHEYEPARAM;
#endif

// 更新标记变量定义
#define    FEC_UPDATE_RADIUS          0x1
#define    FEC_UPDATE_PTZZOOM         0x2
#define    FEC_UPDATE_WIDESCANOFFSET  0x4
#define    FEC_UPDATE_PTZPARAM        0x8

#endif

// Baseline 63
// 启用鱼眼
//
int             PlayM4_FEC_Enable(int nPort);

// Baseline 64
// 关闭鱼眼模块
//
int             PlayM4_FEC_Disable(int nPort);

// Baseline 65
// 获取鱼眼矫正处理子端口 [1~31] 
//
int             PlayM4_FEC_GetPort(int nPort , int* nSubPort, FECPLACETYPE emPlaceType , FECCORRECTTYPE emCorrectType);

// Baseline 66
// 删除鱼眼矫正处理子端口
//
int             PlayM4_FEC_DelPort(int nPort , int nSubPort);

// Baseline 67
// 设置鱼眼矫正参数
//
int             PlayM4_FEC_SetParam(int nPort , int nSubPort , FISHEYEPARAM * pPara);

// Baseline 68
// 获取鱼眼矫正参数
//
int             PlayM4_FEC_GetParam(int nPort , int nSubPort , FISHEYEPARAM * pPara);

// Baseline 69
// 设置显示窗口，可以随时切换
//
int             PlayM4_FEC_SetWnd(int nPort , int nSubPort , void * hWnd);

// Baseline 70
// 当前触发点对应的鱼眼矫正子port
//
int             PlayM4_FEC_GetCurrentPTZPort(int nPort, bool bPanorama, float fPositionX,float fPositionY, unsigned int *pnPort);

// Baseline 71
// 设置鱼眼矫正子port，使线框高亮
//
int             PlayM4_FEC_SetCurrentPTZPort(int nPort, unsigned int nSubPort);

// Baseline 72
// 线框类型
//
int             PlayM4_FEC_SetPTZOutLineShowMode(int nPort, FECSHOWMODE nPTZShowMode);

// Baseline 73
// 鱼眼播放库内部函数，用于实现一些界面PTZ的效果，实现PTZ窗口上点击的鼠标位置转换到原始图像上的点
//
int             PlayM4_FEC_PTZ2Window(int nPort, int nSubPort,
                                      PTZPARAM stPTZRefOrigin, PTZPARAM stPTZRefWindow,
                                      PTZPARAM stPTZWindow , float * fXWindow, float* fYWindow);

// Baseline 74
// 设置字体库路径
// 需要显示字符，在Play前设置
// int nIntelType, 保留参数，暂无效.填1
// int bTrue, 保留参数,填TRUE
int             PlayM4_SetOverlayPriInfoFlag(int nPort, int nIntelType, int bTrue, char* pFontPath);

// Baseline 75
// 音量算法调节
//
int            PlayM4_AdjustWaveAudio(int nPort,int nCoefficient);

// Baseline 76
// 设置跳I帧解码，需要在设置只解码I帧后配置
//
int            PlayM4_SetIFrameDecInterval(int nPort, unsigned int dwInterval);

// Baseline 77
// 预录像回调，带有帧信息
int            PlayM4_SetPreRecordCallBackEx(int nPort,
                                            void (CALLBACK* PreRecordCBfun)(int nPort,
                                                                        RECORD_DATA_INFO* pRecordDataInfo,
                                                                        void *pUser),
                                            void *pUser);

// Baseline 78
// 倒放接口
int            PlayM4_ReversePlay(int nPort);

// Baseline 79
// 带全局时间的显示回调
int           PlayM4_RegisterDisplayCallBackEx(int nPort,
                                               void (CALLBACK* DisplayCBFun)
                                               (DISPLAY_INFO *pstDisplayInfo ,
                                               PLAYM4_SYSTEM_TIME *pstSystemTime, 
                                               int bDettach),
                                               void* nUser);

// Baseline 80
// 带全局时间的解码回调
int           PlayM4_RegisterDecCallBack(int nPort, void (CALLBACK* DecCBFun)(int nPort,
                                                                              char *pBuf,
                                                                              int nSize,
                                                                              FRAME_INFO *pFrameInfo,
                                                                              PLAYM4_SYSTEM_TIME *pstSystemTime,
                                                                              void* nUser),
                                                    void* nUser);

// Baseline 81
// 共享模式音频播放
int           PlayM4_PlaySoundShare(int nPort);

// Baseline 82
// 共享模式音频停止
int           PlayM4_StopSoundShare(int nPort);

// Baseline 83
// 鱼眼3D模式的旋转函数
int           PlayM4_FEC_3DRotate(int nPort,  int nSubPort, PLAYM4SRTRANSFERPARAM *pstRotateParam);

// Baseline 83-1
// 鱼眼获取3D选择参数
int           PlayM4_FEC_Get3DRotate(int nPort, int nSubPort, PLAYM4SRTRANSFERPARAM *pstRotateParam);

// Baseline 84
// 鱼眼抓图需要的内存
int           PlayM4_FEC_GetCapPicSize(int nPort, int nSubPort, int* pnBufSize);

// Baseline 85
// 鱼眼抓图
int           PlayM4_FEC_Capture(int nPort, int nSubPort , unsigned int nType, char *pPicBuf, int nBufSize);

// Baseline 86
// 带私有数据的BMP抓图
int           PlayM4_GetBMPEx(int nPort,unsigned char* pBitmap,unsigned int nBufSize,unsigned int* pBmpSize);

///////////////////////////////////////////////////////////////////////////////////////
/////////// 以下接口为定制版本 接口保留不进行系统测试//////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

// 设置分辨率改变回调
//
int    PlayM4_SetEncTypeChangeCallBack(int nPort, void(CALLBACK *funEncChange)(int nPort, void* nUser), void* pUser);

int    PlayM4_SetEncryptTypeCallBack(int nPort, unsigned int nType,
                                     void (CALLBACK* EncryptTypeCBFun)(int nPort,
                                                                       ENCRYPT_INFO* pEncryptInfo,
                                                                       void* nUser,
                                                                       int nReserved2),
                                           void* nUser);

#define PLAYM4_SOURCE_MODULE             0 // 数据源模块 
#define PLAYM4_DEMUX_MODULE              1 // 解析模块 
#define PLAYM4_DECODE_MODULE             2 // 解码模块 
#define PLAYM4_RENDER_MODULE             3 // 渲染模块 

#define PLAYM4_RTINFO_HARDDECODE_ERROR   0 // 硬解码错误
#define PLAYM4_RTINFO_SOFTDECODE_ERROR   1 // 软解码错误
#define PLAYM4_RTINFO_MEDIAHEADER_ERROR  2 // 媒体头错误
#define PLAYM4_RTINFO_SWITCH_SOFT_DEC    3 // 切换至软解
#define PLAYM4_RTINFO_ALLOC_MEMORY_ERROR 4 // 内存分配失败
#define PLAYM4_RTINFO_DEMUX_STREAM_ERROR 5 // 解析码流报错

// 设置运行回调信息.编码类型改变，nModule填PLAYM4_DECODE_MODULE
// 收到变编码回调后，也需要有显示回调后再开启预录像，收到显示回调才能保证解析到编码参数
int    PlayM4_SetRunTimeInfoCallBackEx(int nPort, int nModule, 
                                       void (CALLBACK* RunTimeInfoCBFun)(int nPort,
                                                                          RunTimeInfo* pstRunTimeInfo,
                                                                          void* pUser),
                                       void* pUser);

// SDP开流
int           PlayM4_OpenStreamAdvanced(int nPort, int nProtocolType,
                                        PLAYM4_SESSION_INFO* pstSessionInfo, 
                                        unsigned int nBufPoolSize);


int           PlayM4_RegisterAudioDataCallBack(int nPort,void (CALLBACK* AudioDataCBFun)
                                                              (int   nPort,
                                                               char* pBuf,
                                                               int   nSize,
                                                               int   nSampleRate,
                                                               void* nUser),
                                               void* nUser);

int           PlayM4_GetStreamOpenMode(int nPort);

unsigned int  PlayM4_GetFileTotalFrames(int nPort);

int           PlayM4_SetPlayPos(int nPort, float fRelativePos);

float         PlayM4_GetPlayPos(int nPort);

unsigned int  PlayM4_GetPlayedFrames(int nPort);

unsigned int  PlayM4_GetCurrentFrameNum(int nPort);

int           PlayM4_SetCurrentFrameNum(int nPort,unsigned int nFrameNum);

unsigned int  PlayM4_GetCurrentFrameRate(int nPort);

int           PlayM4_ThrowBFrameNum(int nPort, unsigned int nNum);

int           PlayM4_SetJpegQuality(int nQuality);

int           PlayM4_SetDisplayMode(int nPort, unsigned int dwType);

int           PlayM4_SetVolume(int nPort,unsigned short nVolume);

///上层写数据开关
int           PlayM4_SwitchToWriteData(int nPort, int bWrite, int nDataType);
///////////////////////////// 定制接口结束////////////////////////////////////
    

#ifdef __cplusplus
}
#endif

#endif //_PLAYM4_H_
