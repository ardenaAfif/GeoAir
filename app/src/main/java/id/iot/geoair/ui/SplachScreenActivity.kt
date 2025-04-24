package id.iot.geoair.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import id.iot.geoair.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplachScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splach_screen)

        // Delay selama 2 detik, lalu pindah ke HomeActivity
        lifecycleScope.launch {
            delay(2000) // 2000ms = 2 detik
            startActivity(Intent(this@SplachScreenActivity, HomeActivity::class.java))
            finish() // Supaya splash screen tidak bisa diakses kembali dengan tombol back
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}