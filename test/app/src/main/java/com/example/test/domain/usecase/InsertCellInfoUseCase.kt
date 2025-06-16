package com.example.test.domain.usecase

import com.example.test.data.local.entity.CellInfoEntity
import com.example.test.domain.repository.CellInfoRepository
import javax.inject.Inject

class InsertCellInfoUseCase @Inject constructor(
    private val repository: CellInfoRepository
) {
    suspend operator fun invoke(cellInfo: CellInfoEntity) {
        repository.insertCellInfo(cellInfo)
    }
}
