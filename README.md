# react-native-image-palette

![Platform](https://img.shields.io/badge/platform-android%20%7C%20ios-%239cf)
[![NPM Badge](https://img.shields.io/npm/v/@somesoap/react-native-image-palette)](https://www.npmjs.com/package/@somesoap/react-native-image-palette)


Get average color and palette from image or from multiple different segments of the image.

This package was inspired by [react-native-image-colors](https://www.npmjs.com/package/react-native-image-colors)
enhancing its functionality, making it independent of Expo modules and unifying return types.\
In current implementation you can define a **specific segment** of the image to get palette or average color of.

For palette calculation this module uses the [Palette](https://developer.android.com/reference/androidx/palette/graphics/Palette) class on Android and [swift-vibrant](https://github.com/bd452/swift-vibrant) on iOS.

Since the implementation of getting palette is different for each platform you can get **different color results for each**.\
Results of calculating average color will be more accurate and only have insufficient difference.


<p align="center" >
  <img
    width="720px"
    src="https://github.com/someSOAP/react-native-image-palette/blob/main/assets/preview.png?raw=true"
    alt="Example 1"
  />
</p>

## Installation

```sh
npm install @somesoap/react-native-image-palette
```
or
```sh
yarn add @somesoap/react-native-image-palette
```

## Usage
Import functions you need:
```ts
import {
  getPalette,
  getAverageColor,
  getSegmentsAverageColor,
  getSegmentsPalette,
} from '@somesoap/react-native-image-palette';
```

Define image source
```js
const image = "https://picsum.photos/id/85/1280/774"
// const image = "https://picsum.photos/id/28/4928/3264"
```
Or
```js
const image = require('./image.jpeg');
```
Receive the result:
```ts

getPalette(image)
  .then((palette) => console.log(palette.vibrant))

getAverageColor(image)
  .then((averageColor) => console.log(averageColor))

```

## API

### `getPalette(image, config): Primise<PaletteResult>`

Retrieve the pallet of the given image.\
⚠️ Results can be slightly different on each platform.

| Property | Type                         | Description                                                                  |
|----------|------------------------------|------------------------------------------------------------------------------|
| `image`  | string \| ImageRequireSource | - can be string (image uri or base64) or local file `require('./image.jpg')` |
| `config` | PaletteConfig?               | Optional config object                                                       |

`PaletteConfig` - optional config type description

| Property        | Type                    | Description                                                                                             |
|-----------------|-------------------------|---------------------------------------------------------------------------------------------------------|
| `headers`       | Record<string, string>? | HTTP headers to be sent along with the GET request to download the image (Auth token or e.g.)           |
| `fallbackColor` | string?                 | If a color property couldn't be retrieved, it will default to this hex color string. By default is #fff |


#### Return type `PaletteResult`

| Property          | Type    |
|-------------------|---------|
| `vibrant`         | string  |
| `darkVibrant`     | string  |
| `lightVibrant`    | string  |
| `muted`           | string  |
| `darkMuted`       | string  |
| `lightMuted`      | string  |
| `dominantAndroid` | string? |

Note `dominantAndroid` is only available on Android.

### `getAverageColor(image, config): Primise<string>`

Returns average (mean) color from the given image.

| Property | Type                           | Description                                                                  |
|----------|--------------------------------|------------------------------------------------------------------------------|
| `image`  | string \| ImageRequireSource   | - can be string (image uri or base64) or local file `require('./image.jpg')` |
| `config` | AverageColorConfig?            | Optional config object                                                       |

`AverageColorConfig` - optional config type description

| Property              | Type                    | Description                                                                                                                                               |
|-----------------------|-------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------|
| `headers`             | Record<string, string>? | HTTP headers to be sent along with the GET request to download the image (Auth token or e.g.)                                                             |
| `pixelSpacingAndroid` | `number?`               | How many pixels to skip when iterating over image pixels. Higher means better performance (**note**: value cannot be lower than 1). Default value is `5`. |

#### Return type `string`

## Get average color or palette by image segments

This module provides functionality to get average color or palette from multiple segments of the given image.\
For example, you can calculate average color only for top and bottom borders of the image, or get palette only from
center of the image.


Functions  `getSegmentsAverageColor(image, segments, config): Promise<string[]>` and `getSegmentsPalette(image, segments, config): Promise<PaletteResult[]>
enhancing functionality of the functions described above, by adding `segments` argument.\
This argument is required array of coordinates in percents from which to which points you need to crop an image.\
In the result you will receive an array with the same length as the length of `segments` array being passed.

`ImageSegmentConfig` has following structure:

| Property | Type             | Description                                                                                                                                                             |
|----------|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `fromX`  | number (0..100)  | Offset in % from the left side of the image. Under the hood we get image size and multiply this value on the amount of pixels in width of the image.                    |
| `toX`    | number (0..100)  | Must be > `fromX`. Offset in % from the left side of the image. Under the hood we get image size and multiply this value on the amount of pixels in width of the image. |
| `fromY`  | number (0..100)  | Offset in % from the top of the image. Under the hood we get image size and multiply this value on the amount of pixels in height of the image.                         |
| `toX`    | number (0..100)  | Must be > `fromY`. Offset in % from the top of the image. Under the hood we get image size and multiply this value on the amount of pixels in height of the image.      |


### Usage

```ts
getSegmentsAverageColor(image, [
  { fromX: 0, toX: 100, fromY: 0, toY: 10 }, // from 0% to 100% by width and from 0% to 10% of height
  { fromX: 40, toX: 60, fromY: 40, toY: 60 }, // from 40% to 60% by width and from 40% to 60% of height
  { fromX: 0, toX: 100, fromY: 90, toY: 100 }, // from 0% to 100% by width and from 90% to 100% of height
])
  .then(([top, center, bottom]) => {
    console.log("Top border average color is: ", top)
    console.log("Center average color is: ", center)
    console.log("Bottom border average color is: ", bottom)
  })


getSegmentsPalette(image, [
  { fromX: 0, toX: 100, fromY: 0, toY: 10 }, // from 0% to 100% by width and from 0% to 10% of height
  { fromX: 40, toX: 60, fromY: 40, toY: 60 }, // from 40% to 60% by width and from 40% to 60% of height
  { fromX: 0, toX: 100, fromY: 90, toY: 100 }, // from 0% to 100% by width and from 90% to 100% of height
])
  .then(([top, center, bottom]) => {
    console.log("Top vibrant color is: ", top.vibrant)
    console.log("Center darkMuted color is: ", center.darkMuted)
    console.log("Bottom lightVibrant color is: ", bottom.lightVibrant)
  })
  .catch(console.error);
```
