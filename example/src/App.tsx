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
import { getAverageColor, getPalette } from 'react-native-image-palette';

// const farm = require('./farm.jpeg');
const yunaUrl = 'https://i.imgur.com/68jyjZT.jpg';
// const catUrl = 'https://i.imgur.com/O3XSdU7.jpg';
// const bird =
//   'https://cdn.pixabay.com/photo/2024/05/26/10/15/bird-8788491_1280.jpg';

const image = yunaUrl;

export default function App() {
  const [averageColor, setAverageColor] = useState<string>('');
  const [palette, setPalette] = useState<Record<string, string>>({});

  useEffect(() => {
    getPalette(image, {
      fallbackColor: '#ABABAB',
      headers: { Auth: 'Bearer 123' },
    })
      .then(setPalette)
      .catch(console.error);
    getAverageColor(image, {
      pixelSpacingAndroid: 1,
      headers: { Auth: 'Bearer 123' },
    })
      .then(setAverageColor)
      .catch(console.error);
  }, []);

  return (
    <ScrollView style={styles.container}>
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
        <Text style={styles.text}>
          {name}: {color}
        </Text>
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
  },
  colorView: {
    height: 150,
    justifyContent: 'center',
    alignItems: 'center',
  },
  textWrapper: {
    backgroundColor: '#fbfbfb',
    paddingVertical: 5,
    paddingHorizontal: 15,
    borderRadius: 5,
  },
  text: {
    fontSize: 24,
    fontWeight: Platform.OS === 'ios' ? 500 : 'semibold',
    color: '#202020',
  },
});
