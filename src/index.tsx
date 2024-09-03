import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-image-palette' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

// @ts-expect-error
const isTurboModuleEnabled = global.__turboModuleProxy != null;

const ImagePaletteModule = isTurboModuleEnabled
  ? require('./NativeImagePalette').default
  : NativeModules.ImagePalette;

const ImagePalette = ImagePaletteModule
  ? ImagePaletteModule
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return ImagePalette.multiply(a, b);
}
