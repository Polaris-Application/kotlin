package com.example.test.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "web_test",
    foreignKeys = [
        ForeignKey(
            entity = NetworkTest::class,
            parentColumns = ["id"],
            childColumns = ["testId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["testId"])]
)
data class WebTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val testId: Long,          // به NetworkTest اشاره می‌کند
    val timestamp: Long,      // زمان ثبت تست
    val webResponseTime: Long, // زمان پاسخ وب
    val isuploaded: Boolean = false
)
