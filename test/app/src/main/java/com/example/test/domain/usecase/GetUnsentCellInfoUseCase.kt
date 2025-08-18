package com.example.test.domain.usecase

import com.example.test.domain.repository.CellInfoRepository
import com.example.test.data.local.entity.CellInfoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnsentCellInfoUseCase @Inject constructor(
    private val repository: CellInfoRepository
) {
    suspend operator fun invoke(): List<CellInfoEntity> {
        return repository.getUnsentCellInfo()
    }
}
