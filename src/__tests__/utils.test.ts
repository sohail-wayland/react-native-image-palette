import { resolveImageSource } from '../utils';

jest.mock('react-native', () => ({
  Image: {
    resolveAssetSource: jest.fn(),
  },
}));

describe('resolveImageSource', () => {
  it('should return the source if it is a string', () => {
    const source = 'https://example.com/image.png';
    const result = resolveImageSource(source);
    expect(result).toBe(source);
  });
});
