package id.iot.geoair.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.iot.geoair.data.remote.ApiService
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

    private val _kelembapan = MutableLiveData<Int?>()
    val kelembapan: MutableLiveData<Int?> get() = _kelembapan

    private val _suhu = MutableLiveData<Int?>()
    val suhu: MutableLiveData<Int?> get() = _suhu

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
}