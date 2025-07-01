
package com.example.test.data.remote.dto

data class MobileDataRequest(
    val network_type: String,
    val timestamp: String,
    val latitude: Double?,
    val longitude: Double?,
    val plmn_id: Int?,
    val lac: Int?,
    val rac: Int?,
    val tac: Int?,
    val cell_id: Int?,
    val band: String?,
    val arfcn: Int?,
    val rsrp: Int?,
    val rsrq: Int?,
    val rssi: Double?,
    val rscp: Int?,
    val ec_no: Int?,
    val rx_lev: Int?,
    val actualTechnology: String?,
    val frequencyMhz: Double?
)
