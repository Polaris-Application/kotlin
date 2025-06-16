package com.example.test.domain.repository

import com.example.test.data.local.entity.CellInfoEntity
import kotlinx.coroutines.flow.Flow

interface CellInfoRepository {
    suspend fun insertCellInfo(cellInfo: CellInfoEntity)
    fun getAllCellInfo(): Flow<List<CellInfoEntity>>
    suspend fun clearAllCellInfo()
}
