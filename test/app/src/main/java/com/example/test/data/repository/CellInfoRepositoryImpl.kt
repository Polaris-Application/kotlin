package com.example.test.data.repository

import com.example.test.data.local.entity.CellInfoEntity
import com.example.test.data.local.source.CellInfoLocalDataSource
import com.example.test.domain.repository.CellInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CellInfoRepositoryImpl @Inject constructor(
    private val localDataSource: CellInfoLocalDataSource
) : CellInfoRepository {

    override suspend fun insertCellInfo(cellInfo: CellInfoEntity) {
        localDataSource.insertCellInfo(cellInfo)
    }

    override fun getAllCellInfo(): Flow<List<CellInfoEntity>> {
        return localDataSource.getAllCellInfo()
    }

    override suspend fun clearAllCellInfo() {
        localDataSource.clearAllCellInfo()
    }
}
