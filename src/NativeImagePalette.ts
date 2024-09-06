import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

import type { ConfigNative } from './types';

export interface Spec extends TurboModule {
  getAverageColor(uri: string, configs: ConfigNative): Promise<string>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('ImagePalette');
