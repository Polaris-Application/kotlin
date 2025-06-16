package com.example.test.data.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.test.data.local.entity.CellInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CellInfoDao {

    @Insert
    suspend fun insertCellInfo(info: CellInfoEntity)

    @Query("SELECT * FROM cell_info ORDER BY timestamp DESC")
    fun getAllCellInfo(): Flow<List<CellInfoEntity>>

    @Query("DELETE FROM cell_info")
    suspend fun clearAll()
}
