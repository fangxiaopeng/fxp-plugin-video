//
//  Preview.m
//  SimpleDemo
//
//  Created by Netsdk on 15/4/22.
//
//

#import <Foundation/Foundation.h>
#import "Preview.h"
#import "hcnetsdk.h"
#import "IOSPlayM4.h"
typedef struct tagHANDLE_STRUCT
{
    int iPreviewID;
    int iPlayPort;
    UIView *pView;
    tagHANDLE_STRUCT()
    {
        iPreviewID = -1;
        iPlayPort = -1;
        pView = NULL;
    }
}HANDLE_STRUCT, *LPHANDLE_STRUCT;

HANDLE_STRUCT g_structHandle[MAX_VIEW_NUM];

void CALLBACK g_fStdDataCallBack(LONG lReadHandle, DWORD dwDataType, BYTE *pBuffer, DWORD dwBufSize, DWORD pUser)
{
    HANDLE_STRUCT *pHandle = &g_structHandle[0];
    switch(dwDataType)
    {
        case NET_DVR_SYSHEAD:
            
            if(pHandle->iPlayPort != -1)
            {
                break;
            }
            
            if(!PlayM4_GetPort(&pHandle->iPlayPort))
            {
                break;
            }
            if(dwBufSize > 0)
            {
                if(!PlayM4_SetStreamOpenMode(pHandle->iPlayPort, STREAME_REALTIME))
                {
                    NSLog(@"PlayM4_SetStreamOpenMode failed:%d",  PlayM4_GetLastError(pHandle->iPlayPort));
                    break;
                }
                if(!PlayM4_OpenStream(pHandle->iPlayPort, pBuffer, dwBufSize, 2*1024*1024))
                {
                    NSLog(@"PlayM4_OpenStream failed:%d",  PlayM4_GetLastError(pHandle->iPlayPort));
                    break;
                }
                dispatch_async(dispatch_get_main_queue(), ^{
                    if(!PlayM4_Play(pHandle->iPlayPort, pHandle->pView))
                    {
                        NSLog(@"PlayM4_Play fail");
                    }
                });
                
            }
        break;
    default:
        if(dwBufSize > 0 && pHandle->iPlayPort != -1)
        {
            if(!PlayM4_InputData(pHandle->iPlayPort, pBuffer, dwBufSize))
            {
                NSLog(@"PlayM4_InputData failed:%d",  PlayM4_GetLastError(pHandle->iPlayPort));
                break;
            }
        }
    }
}


int startPreview(int iUserID, int iStartChan, UIView *pView, int iIndex)
{
    BOOL gInsideDecode = true;
    
    if(gInsideDecode)
    {
        NET_DVR_PREVIEWINFO struPreviewInfo = {0};
        struPreviewInfo.lChannel = iStartChan + iIndex;
        struPreviewInfo.dwStreamType = 1;
        struPreviewInfo.bBlocked = 1;
        struPreviewInfo.hPlayWnd = pView;
        g_structHandle[iIndex].iPreviewID = NET_DVR_RealPlay_V40(iUserID, &struPreviewInfo, NULL, NULL);
        if (g_structHandle[iIndex].iPreviewID == -1)
        {
            NSLog(@"NET_DVR_RealPlay_V40 failed:%d",  NET_DVR_GetLastError());
            return -1;
        }
        else{
            NSLog(@"NET_DVR_RealPlay_V40  InsideDecode Succ");
        }

        
//            if(!NET_DVR_OpenSound(g_structHandle[iIndex].iPreviewID))
//            {
//                NSLog(@"NET_DVR_OpenSound failed:%d",  NET_DVR_GetLastError());
//            }
//            
//            if(!NET_DVR_Volume(g_structHandle[iIndex].iPreviewID, 55))
//            {
//                NSLog(@"NET_DVR_Volume failed:%d",  NET_DVR_GetLastError());
//            }
//            if(!NET_DVR_CloseSound())
//            {
//                NSLog(@"NET_DVR_CloseSound failed:%d",  NET_DVR_GetLastError());
//            }
    }
    
    else
    {
        NET_DVR_PREVIEWINFO struPreviewInfo = {0};
        struPreviewInfo.lChannel = iStartChan + iIndex;
        struPreviewInfo.dwStreamType = 1;
        struPreviewInfo.bBlocked = 1;
        g_structHandle[iIndex].pView = pView;
        g_structHandle[iIndex].iPreviewID = NET_DVR_RealPlay_V40(iUserID, &struPreviewInfo, NULL, &g_structHandle[iIndex]);
        if (g_structHandle[iIndex].iPreviewID == -1)
        {
            NSLog(@"NET_DVR_RealPlay_V40 failed:%d",  NET_DVR_GetLastError());
            return -1;
        }
        else
        {
            if(!NET_DVR_SetStandardDataCallBack(g_structHandle[iIndex].iPreviewID, g_fStdDataCallBack, iIndex))
            {
                NSLog(@"NET_DVR_SetStandardDataCallBack failed:%d",  NET_DVR_GetLastError());
                return -1;
            }
            NSLog(@"NET_DVR_RealPlay_V40 OutsideDecode Succ");
        }
    }
     return g_structHandle[iIndex].iPreviewID;
}



void stopPreview(int iIndex)
{
    if(!NET_DVR_StopRealPlay(g_structHandle[iIndex].iPreviewID))
    {
        NSLog(@"NET_DVR_StopRealPlay failed:%d",  NET_DVR_GetLastError());
        return;
    }
}
