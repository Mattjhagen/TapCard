package com.tapcard.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageCompressor {
    
    fun compressImage(context: Context, uriString: String, maxSize: Int = 1024): ByteArray? {
        try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            var scale = 1
            while (options.outWidth / scale / 2 >= maxSize && options.outHeight / scale / 2 >= maxSize) {
                scale *= 2
            }

            val options2 = BitmapFactory.Options()
            options2.inSampleSize = scale
            val inputStream2: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, options2)
            inputStream2?.close()

            if (bitmap == null) return null

            val outputStream = ByteArrayOutputStream()
            // Compress as JPEG
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val byteArray = outputStream.toByteArray()
            
            bitmap.recycle()
            
            return byteArray
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
