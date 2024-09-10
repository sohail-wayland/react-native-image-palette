package com.imagepalette

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

abstract class ImagePaletteSpec internal constructor(context: ReactApplicationContext) :
  ReactContextBaseJavaModule(context) {
  abstract fun getPalette(uri: String, config: ReadableMap, promise: Promise)
  abstract fun getAverageColor(uri: String, config: ReadableMap, promise: Promise)
  abstract fun getSegmentsAverageColor(uri: String, segments: ReadableArray, config: ReadableMap, promise: Promise)
  abstract fun getSegmentsPalette(uri: String, segments: ReadableArray, config: ReadableMap, promise: Promise)
}
