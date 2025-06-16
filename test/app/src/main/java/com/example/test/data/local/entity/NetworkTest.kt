package com.example.test.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "network_tests")
data class NetworkTest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val param: String?,
    val repeatInterval: String,
    val isPaused: Boolean = false
)