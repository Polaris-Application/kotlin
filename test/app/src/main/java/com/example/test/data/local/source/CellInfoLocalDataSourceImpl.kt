package com.example.test.data.local.source

import kotlinx.coroutines.flow.Flow
import com.example.test.data.local.Dao.CellInfoDao
import com.example.test.data.local.entity.CellInfoEntity
import javax.inject.Inject


class CellInfoLocalDataSourceImpl @Inject constructor(
    private val dao: CellInfoDao
) : CellInfoLocalDataSource {
    override suspend fun insertCellInfo(cellInfo: CellInfoEntity) {
        dao.insertCellInfo(cellInfo)
    }

    override fun getAllCellInfo(): Flow<List<CellInfoEntity>> {
        return dao.getAllCellInfo()
    }

    override suspend fun clearAllCellInfo() {
        dao.clearAll()
    }
    override suspend fun getUnsentCellInfo(): List<CellInfoEntity> {
        return dao.getUnsentCellInfo()
    }

    override suspend fun markAsUploaded(ids: List<Long>) {
        dao.markAsUploaded(ids)
    }

}
