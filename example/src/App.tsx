import React, { useState, useEffect } from 'react';
import {
  StyleSheet,
  View,
  Text,
  Image,
  Dimensions,
  Platform,
} from 'react-native';
import { getAverageColor } from 'react-native-image-palette';

const farm = require('./farm.jpeg');
// const yunaUrl = 'https://i.imgur.com/68jyjZT.jpg';
// const catUrl = 'https://i.imgur.com/O3XSdU7.jpg';
// const bird =
//   'https://cdn.pixabay.com/photo/2024/05/26/10/15/bird-8788491_1280.jpg';

const image = farm;

export default function App() {
  const [averageColor, setAverageColor] = useState<string>('#fff');

  useEffect(() => {
    getAverageColor(image).then(setAverageColor).catch(console.error);
  }, []);

  return (
    <View style={styles.container}>
      <Image source={farm} style={styles.img} />
      <View style={[styles.avgColorView, { backgroundColor: averageColor }]}>
        <View style={styles.textWrapper}>
          <Text style={styles.text}>Average Color: {averageColor}</Text>
        </View>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  img: {
    height: Dimensions.get('window').width,
  },
  avgColorView: {
    flex: 1,
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
