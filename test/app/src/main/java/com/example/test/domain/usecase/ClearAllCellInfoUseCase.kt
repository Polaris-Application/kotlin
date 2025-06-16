package com.example.test.domain.usecase

import com.example.test.domain.repository.CellInfoRepository
import javax.inject.Inject

class ClearAllCellInfoUseCase @Inject constructor(
    private val repository: CellInfoRepository
) {
    suspend operator fun invoke() {
        repository.clearAllCellInfo()
    }
}
