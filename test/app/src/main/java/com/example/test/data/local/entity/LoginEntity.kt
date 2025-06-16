package com.example.test.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login_data")
data class LoginEntity(
    @PrimaryKey val phoneNumber: String,
    val hashedPassword: String
)
