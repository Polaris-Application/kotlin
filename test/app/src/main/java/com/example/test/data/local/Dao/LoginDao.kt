package com.example.test.data.local.Dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.test.data.local.entity.LoginEntity

@Dao
interface LoginDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoginData(loginEntity: LoginEntity)

    @Query("SELECT * FROM login_data WHERE phoneNumber = :phone LIMIT 1")
    suspend fun getLoginData(phone: String): LoginEntity?

    @Query("DELETE FROM login_data")
    suspend fun deleteAllLoginData()
}
