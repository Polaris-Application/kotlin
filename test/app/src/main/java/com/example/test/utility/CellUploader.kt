package com.example.test.utility


import android.content.Context
import android.util.Log
import com.example.test.data.local.entity.CellInfoEntity
import com.example.test.data.remote.MobileDataApi
import com.example.test.data.remote.dto.MobileDataRequest
import com.example.test.domain.usecase.MarkCellInfoAsUploadedUseCase
import com.example.test.domain.usecase.GetUnsentCellInfoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

fun CellInfoEntity.toMobileDataRequest(): MobileDataRequest {
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
        rx_lev = rxLev,
        actualTechnology = actualTechnology,
        frequencyMhz = frequencyMhz
    )
}
suspend fun sendUnsentCellDataNow(
    context: Context,
    getUnsentUseCase: GetUnsentCellInfoUseCase,
    markUploadedUseCase: MarkCellInfoAsUploadedUseCase,
    mobileDataApi: MobileDataApi,
    uploadSettings: UploadSettingsHelper
) = withContext(Dispatchers.IO) {
    val unsent = getUnsentUseCase()

    val token = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        .getString("access_token", null)

    if (token == null) {
        Log.w("MANUAL_UPLOAD", "🔐 No access token found")
        return@withContext
    }

    val uploadedIds = mutableListOf<Long>()

    for (item in unsent) {
        try {
            val response = mobileDataApi.sendMobileData(
                "Bearer $token",
                item.toMobileDataRequest()
            )
            if (response.isSuccessful) {
                uploadedIds.add(item.id.toLong())
            } else {
                Log.e("MANUAL_UPLOAD", "❌ Upload failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MANUAL_UPLOAD", "❌ Exception: ${e.message}")
        }
    }

    if (uploadedIds.isNotEmpty()) {
        markUploadedUseCase(uploadedIds)
        uploadSettings.setLastCellUploadTime(System.currentTimeMillis())
        Log.d("MANUAL_UPLOAD", "✅ Uploaded ${uploadedIds.size} items")
    }
}


