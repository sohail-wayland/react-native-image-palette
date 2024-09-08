package com.imagepalette

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType

class ImagePaletteModule internal constructor(context: ReactApplicationContext) :
  ImagePaletteSpec(context) {

  private val context = context
  private val imgPalette = ImagePalette()

  override fun getName(): String {
    return NAME
  }

  private fun getHeadersFromConfig(config: ReadableMap): MutableMap<String, String> {
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

    return headers
  }

  @ReactMethod
  override fun getPalette(uri: String, config: ReadableMap, promise: Promise) {

    val headers = getHeadersFromConfig(config)

    val fallback = config.getString("fallbackColor") ?: "#fff"

    imgPalette.getPalette(uri, context, fallback, headers, promise)
  }

  @ReactMethod
  override fun getAverageColor(uri: String, config: ReadableMap, promise: Promise) {

    val headers = getHeadersFromConfig(config)

    val pixelSpacing = try {
      config.getInt("pixelSpacingAndroid")
    } catch (exception: Exception) {
      5
    }

    imgPalette.getAverageColor(uri, context, headers, pixelSpacing, promise)
  }


  @ReactMethod
  override fun getAverageColorSectors(uri: String, sectors: ReadableArray, config: ReadableMap, promise: Promise) {
    val headers = getHeadersFromConfig(config)

    val sectorsParsed: ArrayList<ImagePalette.ImageSectorConfig> = ArrayList()

    for (i in 0 until sectors.size()) {
      val readableMap = sectors.getMap(i)

      val pixelSpacing = try {
        readableMap.getInt("pixelSpacingAndroid")
      } catch (exception: Exception) {
        5
      }

      sectorsParsed.add(
        ImagePalette.ImageSectorConfig(
        fromY = readableMap.getInt("fromY"),
          toY = readableMap.getInt("toY"),
          fromX = readableMap.getInt("fromX"),
          toX = readableMap.getInt("toX"),
          pixelSpacingAndroid = pixelSpacing,
      ))
    }

    imgPalette.getAverageColorSectors(
      uri,
      context,
      headers,
      sectorsParsed,
      promise
    )
  }

  companion object {
    const val NAME = "ImagePalette"
  }
}
