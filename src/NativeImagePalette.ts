import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import type {
  UnsafeObject,
  Int32,
} from 'react-native/Libraries/Types/CodegenTypes';

export interface AverageConfigNative {
  pixelSpacingAndroid?: Int32;
  headers?: UnsafeObject;
}

export interface PaletteConfigNative {
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

export interface Spec extends TurboModule {
  getAverageColor(uri: string, config: PaletteConfigNative): Promise<string>;
  getPalette(uri: string, config: AverageConfigNative): Promise<PaletteResult>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ImagePalette');
