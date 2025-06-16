package com.example.test.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "sms_test",
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
data class SMSTestEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testId: Long,          // به NetworkTest اشاره می‌کند
    val timestamp: Long,      // زمان ثبت تست
    val SMSTime: Long?,        // زمان ارسال SMS
    val sentTime:Long?,
    val deliveryTime:Long?
)
