package id.iot.geoair.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import com.github.AAChartModel.AAChartCore.AAChartCreator.AAChartModel
import com.github.AAChartModel.AAChartCore.AAChartCreator.AASeriesElement
import com.github.AAChartModel.AAChartCore.AAChartEnum.AAChartType
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import id.iot.geoair.R
import id.iot.geoair.databinding.ActivityHomeBinding
import java.util.Calendar
import kotlin.math.abs

@SuppressLint("SetTextI18n")
@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private val handler = Handler(Looper.getMainLooper())
    private val refreshInterval = 1000L // 1 detik
    private val token = "zBOrTJZFZAXNKH5beRH1Di_oRQrwFujP"

    private lateinit var googleMap: GoogleMap

    private val baseHourlyAqi = MutableList(24) { 0 } // Default awal, nanti diisi saat pertama kali dapat debu
    private var lastAqiValue = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mapSetup()
        observeSensorData()

        handler.post(refreshSensorRunnable)
    }

    private fun mapSetup() {
       val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        viewModel.latitude.observe(this) { latitude ->
            val lat = latitude
            val lng = viewModel.longitude.value

            if (lat != null && lng != null) {
                val location = LatLng(lat, lng)
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(location).title("Lokasi Sensor"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
                Log.d(">> Location", "$lat, $lng")
            }
        }

    }

    private fun getDebuProgress(debu: Int): Float {
        val min = 0
        val max = 100
        return ((debu - min).coerceAtLeast(0).toFloat() / (max - min) * 100f).coerceIn(0f, 100f)
    }

    private fun observeSensorData() {
        viewModel.debu.observe(this) { debu ->
            if (debu != null) {
                val progress = getDebuProgress(debu)
                binding.pbAqi.setAQIProgress(debu, progress)

                // Tampilkan ivWarning jika debu > 50
                if (debu > 50) {
                    binding.ivWarning.visibility = View.VISIBLE
                    binding.ivWarning.setOnClickListener {
                        showUnhealthyAirQualityInfo()
                    }
                } else {
                    binding.ivWarning.visibility = View.GONE
                }

                if (abs(debu - lastAqiValue) >= 5) {
                    showDynamicHourlyAqiChart(debu)
                    lastAqiValue = debu
                }
            } else {
                binding.pbAqi.setAQIProgress(0, 0f)
            }
        }


        viewModel.suhu.observe(this) { suhu ->
            if (suhu != null) {
                binding.tvTemperaturePercent.text = "$suhu°C"
            } else {
                binding.tvTemperaturePercent.text = "--°C"
            }
        }

        viewModel.kelembapan.observe(this) { kelembapan ->
            if (kelembapan != null) {
                binding.tvHumidityPercent.text = "$kelembapan%"
            } else {
                binding.tvHumidityPercent.text = "--%"
            }
        }

        viewModel.co2.observe(this) { co2 ->
            if (co2 != null) {
                binding.tvCo2.text = "$co2 ppm"
                binding.progressCo2.progress = getCo2Progress(co2)
                binding.progressCo2.setIndicatorColor(getColorForCo2(co2))
            } else {
                binding.tvCo2.text = "-- ppm"
                binding.progressCo2.progress = 0
            }
        }

        viewModel.co.observe(this) { co ->
            if (co != null) {
                binding.tvCo.text = "$co ppm"
                binding.progressCo.progress = getCoProgress(co)
                binding.progressCo.setIndicatorColor(getColorForCo(co))
            } else {
                binding.tvCo.text = "-- ppm"
                binding.progressCo.progress = 0
            }
        }
    }

    private fun getCo2Progress(co2: Int): Int {
        val min = 10
        val max = 1000
        return ((co2 - min).coerceAtLeast(0).toFloat() / (max - min) * 100).toInt().coerceIn(0, 100)
    }

    private fun getCoProgress(co: Int): Int {
        val min = 20
        val max = 2000
        return ((co - min).coerceAtLeast(0).toFloat() / (max - min) * 100).toInt().coerceIn(0, 100)
    }

    private fun getColorForCo2(co2: Int): Int {
        return when {
            co2 <= 200 -> ContextCompat.getColor(this, R.color.green_dark)   // Healthy
            co2 <= 500 -> ContextCompat.getColor(this, R.color.yellow)       // Normal
            co2 <= 700 -> ContextCompat.getColor(this, R.color.orange)      // Medium
            else -> ContextCompat.getColor(this, R.color.red)                // High
        }
    }

    private fun getColorForCo(co: Int): Int {
        return when {
            co <= 500 -> ContextCompat.getColor(this, R.color.green_dark)    // Healthy
            co <= 1000 -> ContextCompat.getColor(this, R.color.yellow)       // Normal
            co <= 1500 -> ContextCompat.getColor(this, R.color.orange)       // Medium
            else -> ContextCompat.getColor(this, R.color.red)                // High
        }
    }

    private fun showUnhealthyAirQualityInfo() {
        val warningDialog = Dialog(this)
        warningDialog.setContentView(R.layout.dialog_warning)
        warningDialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        warningDialog.setCancelable(false)

        val btnOk = warningDialog.findViewById<TextView>(R.id.btn_ok)

        btnOk.setOnClickListener {
            warningDialog.dismiss()
        }

        warningDialog.show()
        warningDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun showDynamicHourlyAqiChart(currentDebu: Int) {
        val now = Calendar.getInstance()
        val currentHour = now.get(Calendar.HOUR_OF_DAY)

        val baseHourlyAqi = listOf(
            67, 58, 85, 48, 46, 49, 38, 48, 57, 82, 92, 80,
            61, 72, 25, 30, 79, 50, 44, 32, 68, 69, 58, 59
        ).toMutableList()

        // Ganti nilai jam sekarang dengan debu terbaru dari sensor
        baseHourlyAqi[currentHour] = currentDebu

        // Geser agar jam sekarang menjadi index ke-0
        val shiftedAqi = (baseHourlyAqi.drop(currentHour) + baseHourlyAqi.take(currentHour)).toMutableList()

        // Prediksi untuk 3 jam ke depan berdasarkan rata-rata 3 jam terakhir
        for (i in 0 until 3) {
            val recentAvg = shiftedAqi.takeLast(3).average().toInt()
            shiftedAqi.add(recentAvg)
        }

        // Buat label jam dari sekarang
        val hourlyLabels = mutableListOf<String>()
        for (i in 0 until shiftedAqi.size) {
            val labelHour = (currentHour + i) % 24
            hourlyLabels.add(String.format("%02d:00", labelHour))
        }

        val chartModel = AAChartModel()
            .chartType(AAChartType.Column)
            .title("Kualitas Udara Historis & Prediksi")
            .subtitle("PM2.5 per Jam")
            .backgroundColor("#FFFFFF")
            .dataLabelsEnabled(true)
            .categories(hourlyLabels.toTypedArray())
            .yAxisTitle("PM2.5 (μg/m³)")
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("PM2.5")
                        .data(shiftedAqi.toTypedArray())
                        .color("#28a745")
                )
            )

        binding.chartForecast.aa_drawChartWithChartModel(chartModel)
    }

    private val refreshSensorRunnable = object : Runnable {
        override fun run() {
            viewModel.getSensorDebu(token)
            viewModel.getSensorSuhu(token)
            viewModel.getSensorKelembapan(token)
            viewModel.getSensorCo2(token)
            viewModel.getSensorCo(token)
            viewModel.getSensorLat(token)
            viewModel.getSensorLong(token)
            handler.postDelayed(this, refreshInterval)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(refreshSensorRunnable)
    }
}