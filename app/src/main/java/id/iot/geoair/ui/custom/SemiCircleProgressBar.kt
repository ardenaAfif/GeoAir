package id.iot.geoair.ui.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import id.iot.geoair.R

class SemiCircleProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var aqi: Int? = null
    private var statusText: String = "Low"
    private var statusColor: Int = context.getColor(R.color.green_dark) // default green
    private var emoji: String = "\uD83D\uDE0A" // 🙂 smile

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 48f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val smallTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 40f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
//        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
    }

    private val statusBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val strokeWidth = 40f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.gray_100) // Light green track
        style = Paint.Style.STROKE
        this.strokeWidth = this@SemiCircleProgressBar.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.green_dark) // Green progress
        style = Paint.Style.STROKE
        this.strokeWidth = this@SemiCircleProgressBar.strokeWidth
        strokeCap = Paint.Cap.ROUND
    }

    private var progress = 0f // from 0 to 100

    fun setProgress(value: Float) {
        progress = value.coerceIn(0f, 100f)
        invalidate()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = strokeWidth / 2
        val rect = RectF(
            padding,
            padding,
            width.toFloat() - padding,
            height.toFloat() * 2 - padding
        )

        // Draw background semi-circle (track)
        canvas.drawArc(rect, 180f, 179.9f, false, backgroundPaint)

        // Draw progress arc
        val sweepAngle = (progress / 100f) * 180f
        canvas.drawArc(rect, 180f, sweepAngle, false, progressPaint)

        // 1. Gambar teks AQI terpisah: angka besar, teks kecil
        val centerX = width / 2f
        val baseY = height / 2f

        // Paint untuk angka AQI
        val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 72f // lebih besar dari textPaint
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }

        // Paint untuk teks "AQI"
        val aqiTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 36f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT
        }

        // Gambar angka AQI (angka di atas)
        canvas.drawText("${aqi ?: 0}", centerX, baseY + 10f, numberPaint)

        // Gambar teks "AQI" (di bawah angka)
        canvas.drawText("AQI", centerX, baseY + 50f, aqiTextPaint)

        // 2. Gambar background icon (kotak kecil)
        val rectWidth = 100f
        val rectHeight = 60f
        val rectLeft = centerX - rectWidth / 2
        val rectTop = baseY + 50f
        val rectText = RectF(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight)

        smallTextPaint.color = statusColor

        // 3. Gambar emoji + teks status di dalam kotak
        val statusTextCombined = "$emoji $statusText"
        canvas.drawText(statusTextCombined, centerX, rectTop + rectHeight / 2 + 35f, smallTextPaint)
    }


    fun setAQI(value: Int) {
        aqi = value
        when (value) {
            in 0..50 -> {
                statusText = "Baik" // Good
                statusColor = ContextCompat.getColor(context, R.color.green_dark)
                emoji = "\uD83D\uDE0A" // 🙂 smile
            }

            in 51..150 -> {
                statusText = "Sedang" // Moderate
                statusColor =
                    ContextCompat.getColor(context, R.color.yellow) // Kombinasi kuning dan oranye
                emoji = "\uD83D\uDE42" // 🙂 slight smile
            }

            in 151..300 -> {
                statusText = "Tidak Sehat" // Unhealthy
                statusColor =
                    ContextCompat.getColor(context, R.color.orange) // Kombinasi merah dan oranye
                emoji = "\uD83D\uDE37" // 😷 mask
            }

            else -> { // 301 ke atas
                statusText = "Berbahaya" // Hazardous
                statusColor = ContextCompat.getColor(context, R.color.red)
                emoji = "⚠\uFE0F" // Warning sign
            }
        }
        invalidate()
    }

    fun setAQIProgress(aqiValue: Int, progressValue: Float) {
        setAQI(aqiValue)
        aqi = aqiValue
        when (aqiValue) {
            in 0..50 -> {
                statusText = "Baik" // Good
                statusColor = ContextCompat.getColor(context, R.color.green_dark)
                emoji = "\uD83D\uDE0A" // 🙂 smile
            }

            in 51..150 -> {
                statusText = "Sedang" // Moderate
                statusColor =
                    ContextCompat.getColor(context, R.color.yellow) // Kombinasi kuning dan oranye
                emoji = "\uD83D\uDE42" // 🙂 slight smile
            }

            in 151..300 -> {
                statusText = "Tidak Sehat" // Unhealthy
                statusColor =
                    ContextCompat.getColor(context, R.color.orange) // Kombinasi merah dan oranye
                emoji = "\uD83D\uDE37" // 😷 mask
            }

            else -> { // 301 ke atas
                statusText = "Berbahaya" // Hazardous
                statusColor = ContextCompat.getColor(context, R.color.red)
                emoji = "⚠\uFE0F" // Warning sign
            }
        }

        // Set progress bar color mengikuti status
        progressPaint.color = statusColor

        setProgress(progressValue)
    }
}