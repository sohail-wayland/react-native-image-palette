import { Image, type ImageRequireSource } from 'react-native';

export const resolveImageSource = (
  source: string | ImageRequireSource
): string => {
  if (typeof source === 'string') {
    return source;
  } else {
    return Image.resolveAssetSource(source).uri;
  }
};
