package com.example.test.domain.usecase

import com.example.test.domain.repository.CellInfoRepository
import com.example.test.data.local.entity.CellInfoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarkCellInfoAsUploadedUseCase @Inject constructor(
    private val repository: CellInfoRepository
) {
    suspend operator fun invoke(ids: List<Long>) {
        repository.markAsUploaded(ids)
    }
}
