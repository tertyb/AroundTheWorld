package com.harelshaigal.aroundtw.helpers

import com.squareup.picasso.Transformation
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.BitmapFactory
import android.graphics.Color

class CircleTransform(val borderWidth: Int = 4, val borderColor: Int = Color.GRAY) : Transformation {
    override fun key(): String {
        return "circleWithBorder"
    }

    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size + borderWidth * 2, size + borderWidth * 2, source.config)
        val canvas = Canvas(bitmap)

        // Draw the border circle
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = borderColor
        paint.style = Paint.Style.FILL
        val rectF = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        canvas.drawOval(rectF, paint)

        // Draw the image inside the circle
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(squaredBitmap, borderWidth.toFloat(), borderWidth.toFloat(), paint)
        squaredBitmap.recycle()
        return bitmap
    }
}
