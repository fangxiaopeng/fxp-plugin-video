#import <Cordova/CDVPlugin.h>
#import "CDVMPlayVideo.h"

@class CDVVideoViewController;

#pragma mark : interface
@interface CDVVideoPlugin : CDVPlugin {}
-(void) playVideo:(CDVInvokedUrlCommand*)command;
-(void) returnSuccess:(NSString*)text  callback:(NSString*)callbackId;
-(void) returnError:(NSString*)message callback:(NSString*)callbackId;
@end

//video control model
@interface CDVVideoCtl : NSObject {}
@property (nonatomic, retain) CDVVideoPlugin*           plugin;
@property (nonatomic, retain) NSString*                 callback;
@property (nonatomic, retain) UIViewController*         parentViewController;
@property (nonatomic, retain) CDVVideoViewController*   viewController;
@property (nonatomic, retain) NSString*                 alternateXib;
@property (nonatomic) BOOL                              isTransitionAnimated;
@property (nonatomic) BOOL                              capturing;
@property (nonatomic, retain) CDVMPlayVideo*            playVideo;
@property (nonatomic, retain) NSDictionary*             options;

-(id)initWidthPlugin:(CDVVideoPlugin*)plugin callback:(NSString*)callback parentViewController:(UIViewController*)parentViewController alternateOverlayXib:(NSString*)alternateXib  options:(NSDictionary*)opts;
-(void)beginCaptrue;
-(void)openDialog;
-(void)captrueCancelled;
@end

//view controller for the ui
@interface CDVVideoViewController : UIViewController {}
@property (nonnull, retain) CDVVideoCtl*         controller;
@property (nonatomic, retain) NSString*          alternateXib;
@property (nonatomic ,retain) IBOutlet UIView*   overlayView;
@property (nonatomic ,retain) UIView*            playView;

-(id)initWithVideoCtl:(CDVVideoCtl*)controller alternateOverlay:(NSString*)alternateXib;
-(UIView*)buildOverlayView;
//-(UIImage*)buildReticleImage;

@end



#pragma mark : implementation

@implementation CDVVideoPlugin

-(void) playVideo:(CDVInvokedUrlCommand*)command{
    //NSLog(@"in the playVideo function exec ");
    
    CDVVideoCtl* controller;
    NSString*    callback;
    NSString*    capabilityError;
    
    callback = command.callbackId;
    
    NSDictionary*  options;
    if(command.arguments.count == 0){
        options = [NSDictionary dictionary];
    }else{
        options = command.arguments[0];
    }
    
    NSString* overlayXib = options[@"overlayXib"];
    
     NSLog(@"host is %@ ", options[@"host"]);
     NSLog(@"port is %@ ", options[@"port"]);
     NSLog(@"user is %@ ", options[@"user"]);
     NSLog(@"pass is %@ ", options[@"pass"]);
     NSLog(@"channel is %@ ", options[@"channel"]);
    
    controller = [[CDVVideoCtl alloc] initWidthPlugin:self callback:callback parentViewController:self.viewController alternateOverlayXib:overlayXib options:options];
    
    [controller performSelector:@selector(beginCaptrue) withObject:nil afterDelay:0 ];
    
    controller.isTransitionAnimated = YES;
    
    [controller release];
    
}

-(void)returnSuccess:(NSString *)text callback:(NSString *)callbackId{
    NSMutableDictionary* resultDict = [[NSMutableDictionary new] autorelease];
    resultDict[@"text"] = text;
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDict];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

-(void)returnError:(NSString *)message callback:(NSString *)callbackId{
    CDVPluginResult* result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:message];
    [self.commandDelegate sendPluginResult:result callbackId:callbackId];
}

@end


@implementation CDVVideoCtl

@synthesize plugin    = _plugin;
@synthesize callback  = _callback;
@synthesize parentViewController  = _parentViewController;
@synthesize viewController = _viewController;
@synthesize alternateXib = _alternateXib;
@synthesize capturing    =   _capturing;
@synthesize playVideo = _playVideo;


 // SystemSoundID _sourceFileObject;

-(id)initWidthPlugin:(CDVVideoPlugin*)plugin callback:(NSString*)callback parentViewController:(UIViewController *)parentViewController alternateOverlayXib:(NSString *)alternateXib options:(NSDictionary*)opts{
    
    self  = [super init];
    if(!self) return self;
    
    self.plugin = plugin;
    self.callback = callback;
    self.parentViewController = parentViewController;
    self.alternateXib = alternateXib;
    self.options = opts;
    
    /*CFURLRef sourceFileURLRef = CFBundleCopyResourceURL(CFBundleGetMainBundle(), CFSTR("CDVBarcodeScanner.bundle/beep"), CFSTR("caf"), NULL)
    AudioServicesCreateSystemSoundID(sourceFileURLRef,&_sourceFileObject) */
    
    return self;
    
}

-(void)beginCaptrue{
    
    self.viewController = [[CDVVideoViewController alloc] initWithVideoCtl:self alternateOverlay:self.alternateXib];
    [self performSelector:@selector(openDialog) withObject:nil afterDelay:1];
}

-(void) openDialog{
    [self.parentViewController presentViewController:self.viewController animated:self.isTransitionAnimated completion:^(void){
        self.playVideo = [[CDVMPlayVideo alloc] initPlayview:self.viewController.playView];
        [self.playVideo startPlay:self.options];
    }];
}

-(void)captrueDone:(void(^)(void))callbackBlock {
    self.capturing = NO;
    [self.parentViewController dismissViewControllerAnimated:self.isTransitionAnimated completion:callbackBlock];
    
}

-(void) captrueCancelled{
    [self captrueDone:^{
        [self.plugin returnSuccess:@"cancel the playVideo" callback:self.callback];
        [self.playVideo releaseVideo];
        [self.playVideo release];
        [self release];
        
    }];
    
}

-(void)dealloc{
    self.playVideo = nil;
    self.viewController = nil;
    self.parentViewController = nil;
    self.plugin = nil;
    self.alternateXib = nil;
    self.options = nil;
    [super dealloc];
}

@end


@implementation CDVVideoViewController

@synthesize controller   =  _controller;
@synthesize alternateXib =  _alternateXib;
@synthesize overlayView  =  _overlayView;
@synthesize playView = _playView;

-(id)initWithVideoCtl:(CDVVideoCtl*)controller alternateOverlay:(NSString*)alternateXib{
    self = [super init];
    if(!self) return self;
    self.controller = controller;
    self.alternateXib = alternateXib;
    self.overlayView = nil;
    self.playView = nil;
    return self;
}

-(UIView*)buildOverlayView{
    if(self.alternateXib != nil){
        return [self buildOverlayViewFromXib];
    }
    CGRect bounds = self.view.bounds;
    bounds = CGRectMake(0, 0, bounds.size.width, bounds.size.height);
    
    UIView* overlayView = [[UIView alloc] initWithFrame:bounds];
    overlayView.autoresizesSubviews = YES;
    overlayView.autoresizingMask =  UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    overlayView.opaque = NO;
    overlayView.backgroundColor = UIColor.whiteColor;
    
    UIToolbar* toolbar = [[UIToolbar alloc] init];
    toolbar.autoresizingMask = UIViewAutoresizingFlexibleWidth | UIViewAutoresizingFlexibleHeight;
    
    
    //id cancelButton = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:(id)self action:@selector(cancelButtonPressed:)]  autorelease];
    
    id cancelButton = [[[UIBarButtonItem alloc] initWithTitle:@"关闭" style:UIBarButtonItemStylePlain target:(id)self action:@selector(cancelButtonPressed:)] autorelease];
    
    //id flexSpace = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action: nil] autorelease];
    
    NSMutableArray *items =[@[cancelButton] mutableCopy];
    //items = [@[flexSpace ,cancelButton ,flexSpace ,playVideo ,flexSpace] mutableCopy];
    toolbar.items = items;
    
    int statebar = 20;
    
    toolbar.layer.borderColor = [[UIColor whiteColor] CGColor];
    bounds = overlayView.bounds;
    [toolbar sizeToFit];
    CGFloat toolbarHeight = [toolbar frame].size.height;
    CGFloat rootViewHeight = CGRectGetHeight(bounds);
    CGFloat rootViewWidth = CGRectGetWidth(bounds);
    CGRect rectArea   =  CGRectMake(0, 0, rootViewWidth, toolbarHeight+statebar);
    [toolbar setFrame:rectArea];
    
    
    rectArea  =  CGRectMake(0, toolbarHeight+statebar, rootViewWidth, rootViewHeight - toolbarHeight);
    UIView* playView = [[UIView alloc] initWithFrame:rectArea];
    playView.opaque = NO;
    playView.backgroundColor  = UIColor.grayColor;
    [overlayView addSubview:playView];
    self.playView = playView;
    
    [overlayView addSubview:toolbar];
    
    return overlayView;
    
}

-(IBAction)cancelButtonPressed:(id)sender{
    [self.controller performSelector:@selector(captrueCancelled) withObject:nil afterDelay:0];
}

-(UIView*)buildOverlayViewFromXib{
    return nil;
}

-(UIImage*)buildArrowImage{
    UIImage* arrow;
    float width = 20.0f;
    float height = 20.0f;
    UIGraphicsBeginImageContext(CGSizeMake(width,height));
    CGContextRef context =  UIGraphicsGetCurrentContext();
    CGContextBeginPath(context);
    CGContextMoveToPoint(context, width*5.0/6.0, height*0.0/10.0);
    CGContextAddLineToPoint(context, width * 0.0/6.0, height * 5.0/10.0);
    CGContextAddLineToPoint(context, width * 5.0/6.0, height * 10.0/10.0);
    CGContextAddLineToPoint(context, width * 6.0/6.0, height * 9.0/10.0);
    CGContextAddLineToPoint(context, width * 2.0/6.0, height * 5.0/10.0);
    CGContextAddLineToPoint(context, width * 6.0/6.0, height * 1.0/10.0);
    CGContextClosePath(context);
    
    CGContextSetFillColorWithColor(context, [UIColor blackColor].CGColor);
    CGContextFillPath(context);
    
    arrow = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return arrow;
}

-(void)loadView {
    self.view = [[UIView alloc] initWithFrame:self.controller.parentViewController.view.frame];
    [self.view addSubview:[self buildOverlayView]];
}

- (void)viewDidUnload
{
}

-(void)dealloc{
    self.view = nil;
    self.controller = nil;
    self.alternateXib = nil;
    self.overlayView = nil;
    self.playView = nil;
    [super dealloc];
}

@end



