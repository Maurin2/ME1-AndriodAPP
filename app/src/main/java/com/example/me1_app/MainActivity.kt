package com.example.me1_app

import android.graphics.PointF
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(), SensorEventListener {

    val UMBRAL = 11f
    val L = 0.7f

    lateinit var sensorManager: SensorManager
    lateinit var dibujo: TrayectoriaView
    lateinit var textoPasos: TextView
    lateinit var textoDistancia: TextView

    var ultimaAccel = FloatArray(3)
    var ultimoMag = FloatArray(3)
    var azimut = 0f

    var x = 0f
    var y = 0f
    var pasos = 0
    var distancia = 0f
    var ultimoPaso = 0L
    var grabando = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dibujo = findViewById(R.id.dibujo)
        textoPasos = findViewById(R.id.texto_pasos)
        textoDistancia = findViewById(R.id.texto_distancia)

        findViewById<Button>(R.id.boton_iniciar).setOnClickListener { grabando = true }
        findViewById<Button>(R.id.boton_pausar).setOnClickListener { grabando = false }
        findViewById<Button>(R.id.boton_reiniciar).setOnClickListener {
            grabando = false
            x = 0f
            y = 0f
            pasos = 0
            distancia = 0f
            dibujo.puntos.clear()
            dibujo.puntos.add(PointF(0f, 0f))
            dibujo.invalidate()
            textoPasos.text = "Pasos: 0"
            textoDistancia.text = "Distancia: 0 m"
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null ||
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null) {
            Toast.makeText(this, "Falta el acelerómetro o el magnetómetro", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val mag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, mag, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            ultimoMag = event.values.clone()
            return
        }

        ultimaAccel = event.values.clone()
        if (!grabando) return

        val ax = event.values[0]
        val ay = event.values[1]
        val az = event.values[2]
        val modulo = sqrt(ax * ax + ay * ay + az * az)
        val ahora = System.currentTimeMillis()
        if (modulo > UMBRAL && ahora - ultimoPaso > 300) {
            ultimoPaso = ahora
            onPaso()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    fun onPaso() {
        val R = FloatArray(9)
        val orientacion = FloatArray(3)
        if (SensorManager.getRotationMatrix(R, null, ultimaAccel, ultimoMag)) {
            SensorManager.getOrientation(R, orientacion)
            azimut = orientacion[0]
        }

        x += L * sin(azimut)
        y += L * cos(azimut)
        dibujo.puntos.add(PointF(x, y))
        pasos++
        distancia += L

        textoPasos.text = "Pasos: $pasos"
        textoDistancia.text = "Distancia: %.1f m".format(distancia)
        dibujo.invalidate()
    }
}
