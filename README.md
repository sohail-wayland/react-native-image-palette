# react-native-image-palette

Get color palette from an image.

## Installation

```sh
npm install react-native-image-palette
```
or
```sh
yarn add react-native-image-palette
```

## Usage

```ts
import { getAverageColor, getPalette } from 'react-native-image-palette';


const image = "https://picsum.photos/id/237/200/300"
//or
// const image = require('./image.jpeg');

getPalette(image, {
  fallbackColor: '#ABABAB',
})
  .then((palette) => console.log(palette.vibrant))
  .catch(console.error);

getAverageColor(image, {
  pixelSpacingAndroid: 1,
})
  .then((averageColor) => console.log(averageColor))
  .catch(console.error);

```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
