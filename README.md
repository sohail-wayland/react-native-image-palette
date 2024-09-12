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

getSegmentsAverageColor(image, [
  { fromX: 0, toX: 100, fromY: 0, toY: 10 },
  { fromX: 40, toX: 60, fromY: 40, toY: 60 },
  { fromX: 0, toX: 100, fromY: 90, toY: 100 },
])
  .then(([top, center, bottom]) => {
    console.log("Top border average color is: ", top)
    console.log("Center average color is: ", center)
    console.log("Bottom border average color is: ", bottom)
  })


getSegmentsPalette(image, [
  { fromX: 0, toX: 100, fromY: 0, toY: 10 },
  { fromX: 40, toX: 60, fromY: 40, toY: 60 },
  { fromX: 0, toX: 100, fromY: 90, toY: 100 },
])
  .then(([top, center, bottom]) => {
    console.log("Top vibrant color is: ", top.vibrant)
    console.log("Center darkMuted color is: ", center.darkMuted)
    console.log("Bottom lightVibrant color is: ", bottom.lightVibrant)
  })
  .catch(console.error);

```
