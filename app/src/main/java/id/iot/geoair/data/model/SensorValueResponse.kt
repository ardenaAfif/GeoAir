package id.iot.geoair.data.model

import com.google.gson.annotations.SerializedName

data class SensorValueResponse(
    @SerializedName("body")
    val value: Int?
)
