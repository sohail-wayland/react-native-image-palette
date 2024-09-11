import type { Int32 } from 'react-native/Libraries/Types/CodegenTypes';

export type { PaletteResult, ImageSegmentConfig } from './NativeImagePalette';

export type ImagePaletteCommonConfig = {
  /**
   * Headers for image request. For example auth token if image is only available for authenticated users
   */
  headers?: Record<string, string>;
};

export type PaletteConfig = ImagePaletteCommonConfig & {
  fallbackColor?: string;
};

export type AverageColorConfig = ImagePaletteCommonConfig & {
  pixelSpacingAndroid?: Int32;
};
