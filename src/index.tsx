import {
  Image,
  type ImageRequireSource,
  NativeModules,
  Platform,
} from 'react-native';

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

const resolveImageSource = (source: string | ImageRequireSource): string => {
  if (typeof source === 'string') {
    return source;
  } else {
    return Image.resolveAssetSource(source).uri;
  }
};

export const getAverageColor = async (
  uri: string | ImageRequireSource
): Promise<string> => {
  const resolvedSrc = resolveImageSource(uri);

  return ImagePalette.getAverageColor(resolvedSrc);
};
