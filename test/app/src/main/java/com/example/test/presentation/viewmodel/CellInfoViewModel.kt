package com.example.test.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.entity.CellInfoEntity
import com.example.test.data.remote.MobileDataApi
import com.example.test.domain.usecase.CellInfoUseCases
import com.example.test.util.CellInfoCollector
import com.example.test.utility.UploadSettingsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.test.utility.sendUnsentCellDataNow

@HiltViewModel
class CellInfoViewModel @Inject constructor(
    private val useCases: CellInfoUseCases,
    private val mobileDataApi: MobileDataApi,
    private val uploadSettings: UploadSettingsHelper
) : ViewModel() {

    private val _cellInfoList = MutableStateFlow<List<CellInfoEntity>>(emptyList())
    val cellInfoList: StateFlow<List<CellInfoEntity>> = _cellInfoList.asStateFlow()

    init {
        observeCellInfo()
    }

    private fun observeCellInfo() {
        viewModelScope.launch {
            useCases.getAllCellInfo().collectLatest {
                _cellInfoList.value = it
            }
        }
    }

    fun insertCellInfo(cellInfo: CellInfoEntity) {
        viewModelScope.launch {
            useCases.insertCellInfo(cellInfo)
        }
    }

    fun clearAllCellInfo() {
        viewModelScope.launch {
            useCases.clearAllCellInfo()
        }
    }

    fun collectAndSaveCellInfo(context: Context) {
        viewModelScope.launch {
            Log.d("CELL_SERVICE", "✅ شروع collectAndSaveCellInfo در ${System.currentTimeMillis()}")
            try {
                val entity = CellInfoCollector.collect(context)
                useCases.insertCellInfo(entity)
                Log.d("CELL_SERVICE", "✅ داده ذخیره شد (بدون ارسال مستقیم)")
            } catch (e: Exception) {
                Log.e("CellInfoViewModel", "❌ Error: ${e.message}", e)
            }
        }
    }


}
