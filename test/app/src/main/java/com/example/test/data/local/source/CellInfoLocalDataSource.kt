package com.example.test.data.local.source

import com.example.test.data.local.entity.CellInfoEntity
import kotlinx.coroutines.flow.Flow

interface CellInfoLocalDataSource {
    suspend fun insertCellInfo(cellInfo: CellInfoEntity)
    fun getAllCellInfo(): Flow<List<CellInfoEntity>>
    suspend fun clearAllCellInfo()
    suspend fun getUnsentCellInfo(): List<CellInfoEntity>
    suspend fun markAsUploaded(ids: List<Long>)

}
