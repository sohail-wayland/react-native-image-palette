
#ifdef RCT_NEW_ARCH_ENABLED
#import "RNImagePaletteSpec.h"

@interface ImagePalette : NSObject <NativeImagePaletteSpec>
#else
#import <React/RCTBridgeModule.h>

@interface ImagePalette : NSObject <RCTBridgeModule>
#endif

@end
