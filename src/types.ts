import type { ConfigNative } from './NativeImagePalette';

export type Config = Omit<ConfigNative, 'headers'> & {
  /**
   * Headers for image request. For example auth token if image is only available for authenticated users
   */
  headers?: Record<string, string>;
};
