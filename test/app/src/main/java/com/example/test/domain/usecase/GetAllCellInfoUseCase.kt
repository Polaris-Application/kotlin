package com.example.test.domain.usecase

import com.example.test.data.local.entity.CellInfoEntity
import com.example.test.domain.repository.CellInfoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCellInfoUseCase @Inject constructor(
    private val repository: CellInfoRepository
) {
    operator fun invoke(): Flow<List<CellInfoEntity>> {
        return repository.getAllCellInfo()
    }
}
