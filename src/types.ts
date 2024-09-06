import type {
  PaletteConfigNative,
  AverageConfigNative,
} from './NativeImagePalette';

export type PaletteConfig = Omit<PaletteConfigNative, 'headers'> & {
  /**
   * Headers for image request. For example auth token if image is only available for authenticated users
   */
  headers?: Record<string, string>;
};

export type AverageColorConfig = Omit<AverageConfigNative, 'headers'> & {
  /**
   * Headers for image request. For example auth token if image is only available for authenticated users
   */
  headers?: Record<string, string>;
};
