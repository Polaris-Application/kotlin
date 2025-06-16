package com.example.test.domain.usecase

import javax.inject.Inject

data class CellInfoUseCases @Inject constructor(
    val insertCellInfo: InsertCellInfoUseCase,
    val getAllCellInfo: GetAllCellInfoUseCase,
    val clearAllCellInfo: ClearAllCellInfoUseCase
)
