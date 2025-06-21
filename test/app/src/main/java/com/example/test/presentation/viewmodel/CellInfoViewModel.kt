package com.example.test.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.test.data.local.entity.CellInfoEntity
import com.example.test.data.remote.MobileDataApi
import com.example.test.domain.usecase.CellInfoUseCases
import com.example.test.util.CellInfoCollector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.test.data.remote.dto.MobileDataRequest
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

@HiltViewModel
class CellInfoViewModel @Inject constructor(
    private val useCases: CellInfoUseCases,
    private val mobileDataApi: MobileDataApi
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

                // ⬇️ گرفتن توکن
                val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val accessToken = prefs.getString("access_token", null)

                if (accessToken != null) {
                    val request = entity.toMobileDataRequest()

                    try {
                        val response = mobileDataApi.sendMobileData("Bearer $accessToken", request)
                        if (response.isSuccessful) {
                            Log.d("CELL_SERVICE", "✅ ارسال موفق به سرور از ویومدل")
                        } else {
                            Log.e("CELL_SERVICE", "❌ ارسال ناموفق: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        Log.e("CELL_SERVICE", "❌ خطا هنگام ارسال به سرور: ${e.message}", e)
                    }
                } else {
                    Log.w("CELL_SERVICE", "🔐 توکن وجود ندارد")
                }

            } catch (e: Exception) {
                Log.e("CellInfoViewModel", "❌ Error: ${e.message}", e)
            }
        }
    }




    private fun CellInfoEntity.toMobileDataRequest(): MobileDataRequest {
        val formatted = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val timestampStr = formatted.format(Date(timestamp))

        return MobileDataRequest(
            network_type = networkType ?: "Unknown",
            timestamp = timestampStr,
            latitude = latitude,
            longitude = longitude,
            plmn_id = plmnId?.toIntOrNull(),
            lac = lac,
            rac = rac,
            tac = tac,
            cell_id = cellId,
            band = band?.toString(),
            arfcn = arfcn,
            rsrp = rsrp,
            rsrq = rsrq,
            rssi = rssi?.toDouble(),
            rscp = rscp,
            ec_no = ecNo,
            rx_lev = rxLev
        )
    }

}
