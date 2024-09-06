#import "ImagePalette.h"

#import "react_native_image_palette-Swift.h"

@implementation ImagePalette
RCT_EXPORT_MODULE()


- (instancetype)init
{
    self = [super init];
    if (self) {
        self.manager = [[ImagePaletteModule alloc] init];
    }
    return self;
}

// Example method
// See // https://reactnative.dev/docs/native-modules-ios
RCT_EXPORT_METHOD(multiply:(double)a
                  b:(double)b
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    NSNumber *result = @(a * b);

    resolve(result);
}


//RCT_EXPORT_METHOD(getPalette:(NSString*)uri
////                  config:(NSDictionary*)config
//                  resolve:(RCTPromiseResolveBlock)resolve
//                  reject:(RCTPromiseRejectBlock)reject)
//{
//
//    void (^onResolve)(NSDictionary<NSString *, NSString *> *) = ^(NSDictionary<NSString *, NSString *> *result) {
//        resolve(result);
//    };
//
//    void (^onReject)(NSError *) = ^(NSError *error) {
//        reject(@"1", error.description, error);
//    };
//
//
//    ImagePaletteManager* manager = [[ImagePaletteManager alloc] initOnResolve:onResolve onReject:onReject];
//
//    [manager getPaletteWithUri:uri fallback:@"#ffffff" headers:NULL quality:@"low"];
//}



RCT_EXPORT_METHOD(getAverageColor:(NSString*)uri
                  configs:(NSDictionary*)configs
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)
{
    ImagePaletteModule *imagePalette = (ImagePaletteModule *)self.manager;
    
    NSDictionary* headers = [configs objectForKey:@"headers"];

    [imagePalette getAverageColorWithUri:uri fallback:@"#fff" headers:headers onResolve:resolve onReject:reject];
}



// Don't compile this code when we build for the old architecture.
#ifdef RCT_NEW_ARCH_ENABLED
- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeImagePaletteSpecJSI>(params);
}
#endif

@end
