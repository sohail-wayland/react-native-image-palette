import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import type {
  UnsafeObject,
  Int32,
} from 'react-native/Libraries/Types/CodegenTypes';

export interface AverageColorNativeConfig {
  pixelSpacingAndroid?: Int32;
  headers?: UnsafeObject;
}

export interface PaletteNativeConfig {
  fallbackColor?: string;
  headers?: UnsafeObject;
}

export interface PaletteResult {
  vibrant: string;
  darkVibrant: string;
  lightVibrant: string;
  muted: string;
  darkMuted: string;
  lightMuted: string;
  dominantAndroid?: string;
}

export interface ImageSegmentConfig {
  fromX: Int32;
  toX: Int32;
  fromY: Int32;
  toY: Int32;
  pixelSpacingAndroid?: Int32;
}

export interface ImageAverageColorSectorsNativeConfig {
  headers?: UnsafeObject;
}

export interface Spec extends TurboModule {
  getAverageColor(uri: string, config: PaletteNativeConfig): Promise<string>;
  getSegmentsAverageColor(
    uri: string,
    segments: ImageSegmentConfig[],
    config?: ImageAverageColorSectorsNativeConfig
  ): Promise<string[]>;
  getPalette(
    uri: string,
    config: AverageColorNativeConfig
  ): Promise<PaletteResult>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ImagePalette');
