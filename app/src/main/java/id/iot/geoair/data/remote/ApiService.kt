package id.iot.geoair.data.remote

import id.iot.geoair.data.model.SensorValueResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("external/api/get")
    suspend fun getSensorDebu(
        @Query("token") token: String,
        @Query("V1") pm10: String = ""
    ): Response<Int>

    @GET("external/api/get")
    suspend fun getSensorCo2(
        @Query("token") token: String,
        @Query("V2") co2: String = ""
    ): Response<Int>

    @GET("external/api/get")
    suspend fun getSensorCo(
        @Query("token") token: String,
        @Query("V3") co: String = ""
    ): Response<Int>

    @GET("external/api/get")
    suspend fun getSensorKelembapan(
        @Query("token") token: String,
        @Query("V4") kelembapan: String = ""
    ): Response<Int>

    @GET("external/api/get")
    suspend fun getSensorSuhu(
        @Query("token") token: String,
        @Query("V5") suhu: String = ""
    ): Response<Int>
}