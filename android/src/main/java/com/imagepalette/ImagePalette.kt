package com.imagepalette

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.webkit.URLUtil
import androidx.palette.graphics.Palette
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URI

class ImagePalette {


  class ImageSegmentConfig(
    val fromX: Int,
    val toX: Int,
    val fromY: Int,
    val toY: Int,
    val pixelSpacingAndroid: Int
  )

  private val service = CoroutineScope(Dispatchers.IO)

  private fun parseFallbackColor(hex: String): String {
    if (!hex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))) {
      throw Exception("Invalid fallback hex color. Must be in the format #ffffff or #fff")
    }

    if (hex.length == 7) {
      return hex
    }

    return "#${hex[1]}${hex[1]}${hex[2]}${hex[2]}${hex[3]}${hex[3]}"
  }


  private fun getHex(rgb: Int): String {
    return String.format("#%06X", 0xFFFFFF and rgb)
  }

  private fun handleError(promise: Promise, err: Exception) {
    promise.reject("[ImagePalette]", err.message, err)
  }

  private fun getImageBitMap(
    uri: String,
    context: Context,
    headers: Map<String, String>? = null,
  ): Bitmap {
    var image: Bitmap? = null

    val resourceId =
      context.resources.getIdentifier(uri, "drawable", context.packageName) ?: 0

    // check if local resource
    if (resourceId != 0) {
      return BitmapFactory.decodeResource(context.resources, resourceId)
    }

    // check if base64
    if (uri.startsWith("data:image")) {
      val base64Uri = uri.split(",")[1]
      val decodedBytes = Base64.decode(base64Uri, Base64.DEFAULT)

      return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    if (URLUtil.isValidUrl(uri)) {
      val parsedUri = URI(uri)
      val connection = parsedUri.toURL().openConnection()

      if (headers != null) {
        for (header in headers) {
          connection.setRequestProperty(header.key, header.value)
        }
      }

      return BitmapFactory.decodeStream(connection.getInputStream())
    }

    throw Exception("Filed to get image")
  }

  private fun cropImageBitmap(image: Bitmap, fromX: Int, toX: Int, fromY: Int, toY: Int): Bitmap {
    val height = toY - fromY
    val width = toX - fromX
    return Bitmap.createBitmap(image, fromX, fromY, width, height)
  }


  private fun calculatePaletteBySegment(
    image: Bitmap,
    fallback: String,
    fromX: Int,
    toX: Int,
    fromY: Int,
    toY: Int,
  ): WritableMap {

    val fallbackColor = parseFallbackColor(fallback)
    val fallbackColorInt = Color.parseColor(fallbackColor)

    val croppedImage = this.cropImageBitmap(image, fromX, toX, fromY, toY)

    val paletteBuilder = Palette.Builder(croppedImage)
    val result: WritableMap = Arguments.createMap()

    try {
      val palette = paletteBuilder.generate()

      result.putString("dominantAndroid", getHex(palette.getDominantColor(fallbackColorInt)))
      result.putString("vibrant", getHex(palette.getVibrantColor(fallbackColorInt)))
      result.putString("darkVibrant", getHex(palette.getDarkVibrantColor(fallbackColorInt)))
      result.putString("lightVibrant", getHex(palette.getLightVibrantColor(fallbackColorInt)))
      result.putString("muted", getHex(palette.getMutedColor(fallbackColorInt)))
      result.putString("darkMuted", getHex(palette.getDarkMutedColor(fallbackColorInt)))
      result.putString("lightMuted", getHex(palette.getLightMutedColor(fallbackColorInt)))

    } catch (err: Exception) {
      result.putString("dominantAndroid", fallbackColor)
      result.putString("vibrant", fallbackColor)
      result.putString("darkVibrant", fallbackColor)
      result.putString("lightVibrant", fallbackColor)
      result.putString("muted", fallbackColor)
      result.putString("darkMuted", fallbackColor)
      result.putString("lightMuted", fallbackColor)
    }

    return result
  }

  private fun calculateSegmentAverageColor(
    bitmap: Bitmap,
    pixelSpacing: Int,
    fromX: Int,
    toX: Int,
    fromY: Int,
    toY: Int
  ): Int {

    val height = toY - fromY
    val width = toX - fromX

    val segmentPixels = IntArray(width * height)

    bitmap.getPixels(segmentPixels, 0, width, fromX, fromY, width, height)

    var redSum = 0
    var greenSum = 0
    var blueSum = 0
    var pixelCount = 0

    for (index in segmentPixels.indices step pixelSpacing) {
      redSum += Color.red(segmentPixels[index])
      greenSum += Color.green(segmentPixels[index])
      blueSum += Color.blue(segmentPixels[index])
      pixelCount++
    }

    if (pixelCount == 0) {
      return Color.BLACK
    }

    val red = redSum / pixelCount
    val green = greenSum / pixelCount
    val blue = blueSum / pixelCount

    return Color.rgb(red, green, blue)
  }

  fun getAverageColor(
    uri: String,
    context: Context,
    headers: Map<String, String>? = null,
    pixelSpacing: Int,
    promise: Promise
  ) {
    service.launch {
      try {
        val image = getImageBitMap(uri, context, headers)
        val avgColor = calculateSegmentAverageColor(
          image,
          pixelSpacing,
          0,
          image.width,
          0,
          image.height
        )
        val hexAvgColor = getHex(avgColor)
        promise.resolve(hexAvgColor)
      } catch (err: MalformedURLException) {
        handleError(promise, Exception("Invalid URL"))
      } catch (err: Exception) {
        handleError(promise, err)
      }
    }
  }

  fun getSegmentsAverageColor(
    uri: String,
    context: Context,
    headers: Map<String, String>? = null,
    segments: ArrayList<ImageSegmentConfig>,
    promise: Promise
  ) {
    service.launch {
      try {
        val image = getImageBitMap(uri, context, headers)

        val resultArray: WritableArray = Arguments.createArray()

        for (segment in segments) {
          val result = calculateSegmentAverageColor(
            image,
            pixelSpacing = segment.pixelSpacingAndroid,
            fromX = image.width * segment.fromX / 100,
            toX = image.width * segment.toX / 100,
            fromY = image.height * segment.fromY / 100,
            toY = image.height * segment.toY / 100
          )
          resultArray.pushString(getHex(result))
        }

        promise.resolve(resultArray)
      } catch (err: MalformedURLException) {
        handleError(promise, Exception("Invalid URL"))
      } catch (err: Exception) {
        handleError(promise, err)
      }
    }

  }


  fun getPalette(
    uri: String,
    context: Context,
    fallback: String,
    headers: Map<String, String>? = null,
    promise: Promise
  ) {
    service.launch {
      try {

        val image = getImageBitMap(uri, context, headers)

        val palette = calculatePaletteBySegment(
          image, fallback,
          0,
          image.width,
          0,
          image.height,
        )
        promise.resolve(palette)
      } catch (err: MalformedURLException) {
        handleError(promise, Exception("Invalid URL"))
      } catch (err: Exception) {
        handleError(promise, err)
      }
    }
  }

  fun getSegmentsPalette(
    uri: String,
    context: Context,
    fallback: String,
    headers: Map<String, String>? = null,
    segments: ArrayList<ImageSegmentConfig>,
    promise: Promise
  ) {
    service.launch {
      try {
        val image = getImageBitMap(uri, context, headers)

        val resultArray: WritableArray = Arguments.createArray()

        for (segment in segments) {
          val result = calculatePaletteBySegment(
            image,
            fallback,
            fromX = image.width * segment.fromX / 100,
            toX = image.width * segment.toX / 100,
            fromY = image.height * segment.fromY / 100,
            toY = image.height * segment.toY / 100
          )
          resultArray.pushMap(result)
        }

        promise.resolve(resultArray)


      } catch (err: MalformedURLException) {
        handleError(promise, Exception("Invalid URL"))
      } catch (err: Exception) {
        handleError(promise, err)
      }
    }
  }

}
