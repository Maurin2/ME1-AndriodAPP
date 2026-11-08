package com.example.me1_app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View

class TrayectoriaView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    val puntos = mutableListOf(PointF(0f, 0f))
    val escala = 50f

    val pincel = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    val pincelMarcador = Paint().apply { color = Color.RED }

    override fun onDraw(canvas: Canvas) {
        val centroX = width / 2f
        val centroY = height / 2f

        val path = Path()
        path.moveTo(centroX, centroY)
        for (p in puntos) {
            path.lineTo(centroX + p.x * escala, centroY - p.y * escala)
        }
        canvas.drawPath(path, pincel)

        val ultimo = puntos.last()
        canvas.drawCircle(centroX + ultimo.x * escala, centroY - ultimo.y * escala, 20f, pincelMarcador)
    }
}
