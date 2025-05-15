package id.iot.geoair.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.iot.geoair.data.api.ApiService
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _debu = MutableLiveData<Int?>()
    val debu: MutableLiveData<Int?> get() = _debu

    private val _co2 = MutableLiveData<Int?>()
    val co2: MutableLiveData<Int?> get() = _co2

    private val _co = MutableLiveData<Int?>()
    val co: MutableLiveData<Int?> get() = _co

    private val _kelembapan = MutableLiveData<Double?>()
    val kelembapan: MutableLiveData<Double?> get() = _kelembapan

    private val _suhu = MutableLiveData<Double?>()
    val suhu: MutableLiveData<Double?> get() = _suhu

    private val _latitude = MutableLiveData<Double?>()
    val latitude: MutableLiveData<Double?> get() = _latitude

    private val _longitude = MutableLiveData<Double?>()
    val longitude: MutableLiveData<Double?> get() = _longitude

    fun getSensorDebu(token: String) {
        viewModelScope.launch {
            val response = apiService.getSensorDebu(token)
            if (response.isSuccessful) {
                _debu.value = response.body()
            } else {
                // Handle error
                Log.e("API Error", "Failed to get sensor debu: ${response.errorBody()?.string()}")
                _debu.value = null
            }
        }
    }

    fun getSensorCo2(token: String) {
        viewModelScope.launch {
            val response = apiService.getSensorCo2(token = token)
            if (response.isSuccessful) {
                _co2.value = response.body()
            } else {
                // Handle error
                Log.e("API Error", "Failed to get sensor co2: ${response.errorBody()?.string()}")
                _co2.value = null
            }
        }
    }

    fun getSensorCo(token: String) {
        viewModelScope.launch {
            val response = apiService.getSensorCo(token = token)
            if (response.isSuccessful) {
                _co.value = response.body()
            } else {
                // Handle error
                Log.e("API Error", "Failed to get sensor co: ${response.errorBody()?.string()}")
                _co.value = null
            }
        }
    }

    fun getSensorKelembapan(token: String) {
        viewModelScope.launch {
            val response = apiService.getSensorKelembapan(token = token)
            if (response.isSuccessful) {
                _kelembapan.value = response.body()
            } else {
                // Handle error
                Log.e(
                    "API Error",
                    "Failed to get sensor kelembapan: ${response.errorBody()?.string()}"
                )
                _kelembapan.value = null
            }
        }
    }

    fun getSensorSuhu(token: String) {
        viewModelScope.launch {
            val response = apiService.getSensorSuhu(token = token)
            if (response.isSuccessful) {
                _suhu.value = response.body()
            } else {
                // Handle error
                Log.e("API Error", "Failed to get sensor suhu: ${response.errorBody()?.string()}")
                _suhu.value = null
            }
        }
    }

    fun getSensorLat(token: String) {
        viewModelScope.launch {
            val response = apiService.getSensorLat(token = token)
            if (response.isSuccessful) {
                _latitude.value = response.body()
            } else {
                // Handle error
                Log.e(
                    "API Error",
                    "Failed to get sensor latitude: ${response.errorBody()?.string()}"
                )
                _latitude.value = null
            }
        }
    }

    fun getSensorLong(token: String) {
        viewModelScope.launch {
            val response = apiService.getSensorLong(token = token)
            if (response.isSuccessful) {
                _longitude.value = response.body()
                Log.d("API Success", "Sensor longitude: ${response.body()}")
            } else {
                Log.e("API ERROR", "Failed to get sensor longitude: ${response.errorBody()?.string()}")
                _longitude.value = null
            }
        }
    }

}