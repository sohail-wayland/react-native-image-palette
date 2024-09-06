package com.imagepalette

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType

class ImagePaletteModule internal constructor(context: ReactApplicationContext) :
  ImagePaletteSpec(context) {

  private val context = context
  private val imgPalette = ImagePalette()

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  override fun getPalette(uri: String, config: ReadableMap, promise: Promise) {

    val headers = mutableMapOf<String, String>()
    val configHeaders = config.getMap("headers")

    configHeaders?.let {
      val iterator = it.keySetIterator()
      while (iterator.hasNextKey()) {
        val key = iterator.nextKey()
        when (it.getType(key)) {
          ReadableType.String -> headers[key] = it.getString(key) ?: ""
          else -> throw IllegalArgumentException("Unsupported type")
        }
      }
    }

    val fallback = config.getString("fallbackColor") ?: "#fff"

    imgPalette.getPalette(uri, context, fallback, headers, promise)
  }

  @ReactMethod
  override fun getAverageColor(uri: String, config: ReadableMap, promise: Promise) {

    val headers = mutableMapOf<String, String>()
    val configHeaders = config.getMap("headers")

    configHeaders?.let {
      val iterator = it.keySetIterator()
      while (iterator.hasNextKey()) {
        val key = iterator.nextKey()
        when (it.getType(key)) {
          ReadableType.String -> headers[key] = it.getString(key) ?: ""
          else -> throw IllegalArgumentException("Unsupported type")
        }
      }
    }


    var pixelSpacing = 5
    config.getDouble("pixelSpacingAndroid").let {
      if(it !== null) {
        pixelSpacing = it.toInt()
      }
    }

    imgPalette.getAverageColor(uri, context, headers, pixelSpacing, promise)
  }

  companion object {
    const val NAME = "ImagePalette"
  }
}
