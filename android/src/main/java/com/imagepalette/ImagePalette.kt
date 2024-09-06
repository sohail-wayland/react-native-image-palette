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
import com.facebook.react.bridge.WritableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.MalformedURLException
import java.net.URI
import kotlin.math.ceil



class ImagePalette {

  private val service = CoroutineScope(Dispatchers.IO)

  private fun parseFallbackColor(hex: String): String {
    if(!hex.matches(Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"))) {
      throw Exception("Invalid fallback hex color. Must be in the format #ffffff or #fff")
    }

    if(hex.length == 7) {
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
      image = BitmapFactory.decodeResource(context.resources, resourceId)
    }

    // check if base64
    if (uri.startsWith("data:image")) {
      val base64Uri = uri.split(",")[1]
      val decodedBytes = Base64.decode(base64Uri, Base64.DEFAULT)

      image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    if (URLUtil.isValidUrl(uri)) {
      val parsedUri = URI(uri)
      val connection = parsedUri.toURL().openConnection()

      if (headers != null) {
        for (header in headers) {
          connection.setRequestProperty(header.key, header.value)
        }
      }

      image = BitmapFactory.decodeStream(connection.getInputStream())
    }

    if (image == null) {
      throw Exception("Filed to get image")
    }

    return image
  }


//  fun getPalette(
//    uri: String,
//    context: Context,
//    fallback: String,
//    headers: Map<String, String>? = null,
//    pixelSpacing: Int,
//    promise: Promise
//  ) {
//    service.launch {
//      try {
//
//        val image = getImageBitMap(uri, context, headers)
//
//        val fallbackColor = parseFallbackColor(fallback)
//        val fallbackColorInt = Color.parseColor(fallbackColor)
//
//        val paletteBuilder = Palette.Builder(image)
//        val result: WritableMap = Arguments.createMap()
//
//        result.putString("average", getHex(calculateAverageColor(image, pixelSpacing)))
//        result.putString("platform", "android")
//
//        try {
//          val palette = paletteBuilder.generate()
//
//          result.putString("dominant", getHex(palette.getDominantColor(fallbackColorInt)))
//          result.putString("vibrant", getHex(palette.getVibrantColor(fallbackColorInt)))
//          result.putString("darkVibrant", getHex(palette.getDarkVibrantColor(fallbackColorInt)))
//          result.putString("lightVibrant", getHex(palette.getLightVibrantColor(fallbackColorInt)))
//          result.putString("muted", getHex(palette.getMutedColor(fallbackColorInt)))
//          result.putString("darkMuted", getHex(palette.getDarkMutedColor(fallbackColorInt)))
//          result.putString("lightMuted", getHex(palette.getLightMutedColor(fallbackColorInt)))
//
//          promise.resolve(result)
//        } catch (err: Exception) {
//          result.putString("dominant", fallbackColor)
//          result.putString("vibrant", fallbackColor)
//          result.putString("darkVibrant", fallbackColor)
//          result.putString("lightVibrant", fallbackColor)
//          result.putString("muted", fallbackColor)
//          result.putString("darkMuted", fallbackColor)
//          result.putString("lightMuted", fallbackColor)
//
//          promise.resolve(result)
//        }
//      } catch (err: MalformedURLException) {
//        handleError(promise, Exception("Invalid URL"))
//      } catch (err: Exception) {
//        handleError(promise, err)
//      }
//    }
//  }


  private fun calculateAverageColor(bitmap: Bitmap, pixelSpacing: Int): Int {
    val segmentWidth = 500

    val width = bitmap.width
    val height = bitmap.height

    val numSegments = ceil(width.toDouble() / segmentWidth).toInt()
    val segmentPixels = IntArray(segmentWidth * height)

    var redSum = 0
    var greenSum = 0
    var blueSum = 0
    var pixelCount = 0

    for (i in 0 until numSegments) {
      val xStart = i * segmentWidth
      val xEnd = minOf(width, (i + 1) * segmentWidth)

      bitmap.getPixels(segmentPixels, 0, segmentWidth, xStart, 0, xEnd - xStart, height)

      for (index in segmentPixels.indices step pixelSpacing) {
        redSum += Color.red(segmentPixels[index])
        greenSum += Color.green(segmentPixels[index])
        blueSum += Color.blue(segmentPixels[index])
        pixelCount++
      }
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
    val image = this.getImageBitMap(uri, context, headers)
    service.launch {
      try {
        promise.resolve(getHex(calculateAverageColor(image, pixelSpacing)))
      } catch (err: MalformedURLException) {
        handleError(promise, Exception("Invalid URL"))
      } catch (err: Exception) {
        handleError(promise, err)
      }
    }
  }

}
