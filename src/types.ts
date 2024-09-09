import type {
  PaletteNativeConfig,
  AverageColorNativeConfig,
  ImageAverageColorSectorsNativeConfig,
} from './NativeImagePalette';

export type { PaletteResult, ImageSectorConfig } from './NativeImagePalette';

export type ImagePaletteCommonConfig = {
  /**
   * Headers for image request. For example auth token if image is only available for authenticated users
   */
  headers?: Record<string, string>;
};

export type PaletteConfig = Omit<PaletteNativeConfig, 'headers'> &
  ImagePaletteCommonConfig;

export type AverageColorConfig = Omit<AverageColorNativeConfig, 'headers'> &
  ImagePaletteCommonConfig;

export type AverageBorderColorConfig = Omit<
  ImageAverageColorSectorsNativeConfig,
  'headers'
> &
  ImagePaletteCommonConfig;
