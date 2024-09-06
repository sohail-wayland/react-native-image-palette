import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import type { UnsafeObject } from 'react-native/Libraries/Types/CodegenTypes';

export interface ConfigNative {
  headers?: UnsafeObject;
}

export interface Spec extends TurboModule {
  getAverageColor(uri: string, configs: ConfigNative): Promise<string>;
  getPalette(uri: string): Promise<UnsafeObject>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ImagePalette');
