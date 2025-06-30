package com.example.test.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cell_info")
data class CellInfoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?,
    val networkType: String?,
    val plmnId: String?,
    val lac: Int?,
    val rac: Int?,
    val tac: Int?,
    val cellId: Int?,
    val band: Int?,
    val arfcn: Int?,
    val rsrp: Int?,
    val rsrq: Int?,
    val rssi: Int?,
    val rscp: Int?,
    val ecNo: Int?,
    val rxLev: Int?,
    val actualTechnology: String?
)
