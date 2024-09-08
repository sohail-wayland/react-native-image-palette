import React, { useState, useEffect } from 'react';
import {
  StyleSheet,
  View,
  Text,
  Image,
  Dimensions,
  Platform,
  ScrollView,
} from 'react-native';
import {
  getAverageColorSectors,
  getAverageColor,
  getPalette,
  type PaletteResult,
} from 'react-native-image-palette';

// const farm = require('./farm.jpeg');
// const yunaUrl = 'https://i.imgur.com/68jyjZT.jpg';
// const catUrl = 'https://i.imgur.com/O3XSdU7.jpg';
const bird =
  'https://cdn.pixabay.com/photo/2024/05/26/10/15/bird-8788491_1280.jpg';

const image = bird;

export default function App() {
  const [avgSectors, setAverageSectors] = useState<string[]>([]);
  const [averageColor, setAverageColor] = useState<string>('');
  const [palette, setPalette] = useState<Partial<PaletteResult>>({});

  useEffect(() => {
    getAverageColorSectors(image, [
      { fromX: 0, toX: 33, fromY: 0, toY: 33 },
      { fromX: 34, toX: 66, fromY: 0, toY: 33 },
      { fromX: 67, toX: 100, fromY: 0, toY: 33 },

      { fromX: 0, toX: 33, fromY: 34, toY: 66 },
      { fromX: 34, toX: 66, fromY: 34, toY: 66 },
      { fromX: 67, toX: 100, fromY: 34, toY: 66 },

      { fromX: 0, toX: 33, fromY: 67, toY: 100 },
      { fromX: 34, toX: 66, fromY: 67, toY: 100 },
      { fromX: 67, toX: 100, fromY: 67, toY: 100 },
    ])
      .then(setAverageSectors)
      .catch(console.error);

    getPalette(image, {
      fallbackColor: '#ABABAB',
      headers: { Auth: 'Bearer 123' },
    })
      .then(setPalette)
      .catch(console.error);

    getAverageColor(image, {
      // pixelSpacingAndroid: 1,
      headers: { Auth: 'Bearer 123' },
    })
      .then(setAverageColor)
      .catch(console.error);
  }, []);

  return (
    <ScrollView style={styles.container}>
      <View style={styles.row}>
        {avgSectors[0] && <ColorView color={avgSectors[0]} name="1:1" />}
        {avgSectors[1] && <ColorView color={avgSectors[1]} name="1:2" />}
        {avgSectors[2] && <ColorView color={avgSectors[2]} name="1:3" />}
      </View>
      <View style={styles.row}>
        {avgSectors[3] && <ColorView color={avgSectors[3]} name="2:1" />}
        {avgSectors[4] && <ColorView color={avgSectors[4]} name="2:2" />}
        {avgSectors[5] && <ColorView color={avgSectors[5]} name="2:3" />}
      </View>
      <View style={styles.row}>
        {avgSectors[6] && <ColorView color={avgSectors[6]} name="3:1" />}
        {avgSectors[7] && <ColorView color={avgSectors[7]} name="3:2" />}
        {avgSectors[8] && <ColorView color={avgSectors[8]} name="3:3" />}
      </View>
      <Image source={{ uri: image }} style={styles.img} />

      {Boolean(averageColor) && (
        <ColorView name="Average Color" color={averageColor} />
      )}
      {Object.entries(palette).map(([name, color]) => (
        <ColorView key={name} name={name} color={color} />
      ))}
    </ScrollView>
  );
}

const ColorView: React.FC<{ color: string; name: string }> = ({
  name,
  color,
}) => {
  return (
    <View style={[styles.colorView, { backgroundColor: color }]}>
      <View style={styles.textWrapper}>
        <Text style={styles.text}>{name}</Text>
        <Text style={styles.text}>{color}</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  img: {
    height: Dimensions.get('window').width,
    flex: 1,
  },
  row: {
    flexDirection: 'row',
  },
  colorView: {
    minHeight: Dimensions.get('window').width / 3,
    minWidth: Dimensions.get('window').width / 3,
    justifyContent: 'center',
    alignItems: 'center',
  },
  textWrapper: {
    backgroundColor: 'rgba(251,251,251,0.47)',
    paddingVertical: 5,
    paddingHorizontal: 15,
    borderRadius: 5,
  },
  text: {
    textAlign: 'center',
    fontSize: 24,
    fontWeight: Platform.OS === 'ios' ? 500 : 'semibold',
    color: '#202020',
  },
});
