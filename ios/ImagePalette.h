
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNImagePaletteSpec.h"


@interface ImagePalette : NSObject <NativeImagePaletteSpec>
@property (nonatomic, strong) NSObject *manager;
#else
#import <React/RCTBridgeModule.h>

@interface ImagePalette : NSObject <RCTBridgeModule>
@property (nonatomic, strong) NSObject *manager;
#endif

@end
