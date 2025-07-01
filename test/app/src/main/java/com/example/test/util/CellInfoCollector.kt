package com.example.test.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.telephony.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.test.data.local.entity.CellInfoEntity

object CellInfoCollector {

    fun collect(context: Context): CellInfoEntity {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val simPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val selectedSim = simPref.getString("selected_sim", "SIM1")

        val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
        val subscriptionList = subManager.activeSubscriptionInfoList ?: emptyList()

        if (subscriptionList.isEmpty()) {
            Toast.makeText(context, "هیچ سیم‌کارتی فعال نیست!", Toast.LENGTH_LONG).show()
            Log.e("CellInfoCollector", "❌ No SIMs available")
            return emptyEntity("No SIM")
        }

        val simSlotIndex = if (selectedSim == "SIM2") 1 else 0
        val subInfo = subManager.getActiveSubscriptionInfoForSimSlotIndex(simSlotIndex)

        if (subInfo == null) {
            Toast.makeText(context, "سیم‌کارت انتخاب‌شده موجود نیست!", Toast.LENGTH_LONG).show()
            Log.e("CellInfoCollector", "❌ SIM at slot $simSlotIndex not available")
            return emptyEntity("Missing SIM slot $simSlotIndex")
        }

        val subId = subInfo.subscriptionId
        val baseTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val telephonyManager = baseTelephonyManager.createForSubscriptionId(subId)

        val location = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        } else null

        val latitude = location?.latitude
        val longitude = location?.longitude

        val networkTypeInt = telephonyManager.dataNetworkType
        val networkType = networkTypeToString(networkTypeInt)
        val plmnId = telephonyManager.networkOperator

        var rsrp: Int? = null
        var rsrq: Int? = null
        var rssi: Int? = null
        var rscp: Int? = null
        var ecNo: Int? = null

        var tac: Int? = null
        var lac: Int? = null
        var rac: Int? = null
        var cellId: Int? = null
        var band: Int? = null
        var arfcn: Int? = null
        var rxLev: Int? = null
        var actualTech: String? = null
        var frequencyMhz: Double? = null


        val cellInfos = telephonyManager.allCellInfo
        val targetCell: CellInfo? =
            // API 28: نمی‌تونیم subscriptionId رو داشته باشیم
            if (subscriptionList.size == 1) {
                // فقط یک سیم‌کارت داریم => تنها cell موجود مربوط به همون سیم‌کارته
                cellInfos?.firstOrNull()
            } else {
                // دوتا سیم‌کارت داریم، ولی subscriptionId در دسترس نیست => فرض بر ترتیب
                val indexInList = subscriptionList.indexOfFirst { it.subscriptionId == subId }
                cellInfos?.getOrNull(indexInList)
            }

        if (targetCell == null) {
            Log.e("CellInfoCollector", "❌ No matching cell info found")
        } else {
            actualTech = when (targetCell) {
                is CellInfoLte -> "LTE (4G)"
                is CellInfoWcdma -> {
                    // معمولاً این می‌تونه UMTS، HSDPA، HSUPA یا HSPA باشه، ولی Android اینو تفکیک نمی‌ده
                    "WCDMA (3G)"
                }
                is CellInfoGsm -> {
                    // بین GPRS، EDGE، و GSM تفاوتی قائل نمی‌شه، پس فقط می‌گیم 2G
                    "GSM (2G)"
                }
                is CellInfoCdma -> "CDMA (2G)"
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetCell.javaClass.simpleName == "CellInfoNr") {
                        "NR (5G)"
                    } else {
                        "Unknown"
                    }
                }
            }
            when (targetCell) {
                is CellInfoLte -> {
                    val ss = targetCell.cellSignalStrength
                    rsrp = ss.rsrp
                    rsrq = ss.rsrq
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        rssi = ss.rssi
                    }
                    val identity = targetCell.cellIdentity
                    tac = identity.tac
                    cellId = identity.ci
                    band = identity.bandwidth
                    arfcn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) identity.earfcn else null
                    frequencyMhz = calculateFrequencyMhz(band, arfcn, actualTech)

                }
                is CellInfoWcdma -> {
                    val ss = targetCell.cellSignalStrength
                    rscp = ss.dbm
                    val identity = targetCell.cellIdentity
                    lac = identity.lac
                    rac = try {
                        val racMethod = identity.javaClass.getMethod("getRac")
                        racMethod.invoke(identity) as? Int
                    } catch (e: Exception) {
                        Log.e("CellInfoCollector", "WCDMA RAC reflection failed: ${e.message}")
                        null
                    }
                    cellId = identity.cid
                    arfcn = identity.uarfcn
                    ecNo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        try {
                            val ecnoMethod = ss.javaClass.getMethod("getEcNo")
                            ecnoMethod.invoke(ss) as? Int
                        } catch (e: Exception) {
                            Log.e("CellInfoCollector", "WCDMA ecNo reflection failed: ${e.message}")
                            null
                        }
                    } else null
                    frequencyMhz = calculateFrequencyMhz(band, arfcn, actualTech)
                }
                is CellInfoGsm -> {
                    val ss = targetCell.cellSignalStrength
                    rssi = ss.dbm
                    val identity = targetCell.cellIdentity
                    lac = identity.lac
                    rac = try {
                        val racMethod = identity.javaClass.getMethod("getRac")
                        racMethod.invoke(identity) as? Int
                    } catch (e: Exception) {
                        Log.e("CellInfoCollector", "GSM RAC reflection failed: ${e.message}")
                        null
                    }
                    cellId = identity.cid
                    arfcn = identity.arfcn
                    val rawDbm = ss.dbm
                    rxLev = if (rawDbm != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                        ((rawDbm + 113) / 2).coerceIn(0, 63)
                    } else null
                    frequencyMhz = calculateFrequencyMhz(band, arfcn, actualTech)
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && targetCell.javaClass.simpleName == "CellInfoNr") {
                        try {
                            val identityMethod = targetCell.javaClass.getMethod("getCellIdentity")
                            val identity = identityMethod.invoke(targetCell)
                            val tacField = identity?.javaClass?.getMethod("getTac")
                            tac = tacField?.invoke(identity) as? Int
                            val ciField = identity?.javaClass?.getMethod("getNci")
                            cellId = ciField?.invoke(identity) as? Int
                            val arfcnField = identity?.javaClass?.getMethod("getNrarfcn")
                            arfcn = arfcnField?.invoke(identity) as? Int
                            val signalMethod = targetCell.javaClass.getMethod("getCellSignalStrength")
                            val ss = signalMethod.invoke(targetCell)
                            val dbmMethod = ss?.javaClass?.getMethod("getDbm")
                            rsrp = dbmMethod?.invoke(ss) as? Int
                            frequencyMhz = calculateFrequencyMhz(band, arfcn, actualTech)
                        } catch (e: Exception) {
                            Log.e("CellInfoCollector", "NR reflection failed: ${e.message}")
                        }
                    }
                }
            }
        }

        return CellInfoEntity(
            timestamp = System.currentTimeMillis(),
            latitude = latitude,
            longitude = longitude,
            networkType = "$networkType ($networkTypeInt)",
            plmnId = plmnId,
            lac = lac,
            rac = rac,
            tac = tac,
            cellId = cellId,
            band = band,
            arfcn = arfcn,
            rsrp = rsrp,
            rsrq = rsrq,
            rssi = rssi,
            rscp = rscp,
            ecNo = ecNo,
            rxLev = rxLev,
            actualTechnology = actualTech,
            frequencyMhz = frequencyMhz

        )
    }
    private fun emptyEntity(reason: String): CellInfoEntity {
        return CellInfoEntity(
            timestamp = System.currentTimeMillis(),
            networkType = reason,
            plmnId = null,
            latitude = null,
            longitude = null,
            lac = null,
            rac = null,
            tac = null,
            cellId = null,
            band = null,
            arfcn = null,
            rsrp = null,
            rsrq = null,
            rssi = null,
            rscp = null,
            ecNo = null,
            rxLev = null,
            actualTechnology=null,
            frequencyMhz = null

        )
    }

    private fun networkTypeToString(networkType: Int): String {
        return when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS (2G)"
            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE (2.75G)"
            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS (3G)"
            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA (2G)"
            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO rev.0 (3G)"
            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO rev.A (3G)"
            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO rev.B (3G)"
            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT (2G)"
            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA (3.5G)"
            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA (3.5G)"
            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA (3G)"
            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+ (3.75G)"
            TelephonyManager.NETWORK_TYPE_LTE -> "LTE (4G)"
            TelephonyManager.NETWORK_TYPE_NR -> "NR (5G)"
            TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD (3.75G)"
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA (3G - China)"
            TelephonyManager.NETWORK_TYPE_IWLAN -> "IWLAN (WiFi Calling)"
            else -> "Unknown ($networkType)"
        }
    }
}
data class BandFrequency(
    val band: Int,
    val tech: String, // LTE, NR, WCDMA, GSM
    val uplinkRange: Pair<Double, Double>?,  // Optional
    val downlinkRange: Pair<Double, Double>?,
    val centerFrequencyMhz: Double? = null
)

private val bandFrequencies = listOf(
    BandFrequency(1, "LTE", Pair(1920.0, 1980.0), Pair(2110.0, 2170.0), 2140.0),
    BandFrequency(3, "LTE", Pair(1710.0, 1785.0), Pair(1805.0, 1880.0), 1842.5),
    BandFrequency(7, "LTE", Pair(2500.0, 2570.0), Pair(2620.0, 2690.0), 2655.0),
    BandFrequency(8, "LTE", Pair(880.0, 915.0), Pair(925.0, 960.0), 942.5),
    BandFrequency(20, "LTE", Pair(832.0, 862.0), Pair(791.0, 821.0), 806.0),

    BandFrequency(78, "NR", null, Pair(3300.0, 3800.0), 3550.0),
    BandFrequency(41, "NR", null, Pair(2496.0, 2690.0), 2593.0),
    BandFrequency(1, "NR", null, Pair(2110.0, 2170.0), 2140.0),
)

private fun calculateFrequencyMhz(
    band: Int?,
    arfcn: Int?,
    tech: String?
): Double? {
    if (tech == null) return null

    // اگه band موجود بود، از اون استفاده کن
    val bandMatch = bandFrequencies.firstOrNull {
        it.tech.equals(tech, ignoreCase = true) && it.band == band
    }
    if (bandMatch != null) return bandMatch.centerFrequencyMhz

    // اگر arfcn نامعتبر یا null باشه، اصلاً ادامه نده
    val nonNullArfcn = arfcn ?: return null

    return when {
        tech.contains("LTE", true) -> when (nonNullArfcn) {
            in 0..599 -> 2110.0 + 0.1 * (nonNullArfcn - 0)      // Band 1
            in 1200..1949 -> 1930.0 + 0.1 * (nonNullArfcn - 1200) // Band 2
            in 2400..2649 -> 1805.0 + 0.1 * (nonNullArfcn - 2400) // Band 3
            in 2750..3449 -> 2110.0 + 0.1 * (nonNullArfcn - 2750) // Band 7
            in 6150..6449 -> 925.0 + 0.1 * (nonNullArfcn - 6150)  // Band 20
            else -> null
        }

        tech.contains("NR", true) -> when (nonNullArfcn) {
            in 620000..653333 -> 3300.0 + 0.015 * (nonNullArfcn - 620000) // Band n78
            in 499200..537999 -> 2496.0 + 0.015 * (nonNullArfcn - 499200) // Band n41
            else -> null
        }

        tech.contains("WCDMA", true) -> when (nonNullArfcn) {
            in 10562..10838 -> 2112.4 + 0.2 * (nonNullArfcn - 10562) // Band 1
            in 9662..9938 -> 1852.4 + 0.2 * (nonNullArfcn - 9662)   // Band 2
            else -> null
        }

        tech.contains("GSM", true) -> when (nonNullArfcn) {
            in 0..124 -> 935.0 + 0.2 * nonNullArfcn  // GSM 900
            in 512..885 -> 1805.0 + 0.2 * (nonNullArfcn - 512) // DCS 1800
            else -> null
        }

        else -> null
    }
}


//package com.example.test.util
//
//import android.Manifest
//import android.content.Context
//import android.content.pm.PackageManager
//import android.location.LocationManager
//import android.os.Build
//import android.telephony.*
//import android.util.Log
//import android.widget.Toast
//import androidx.core.app.ActivityCompat
//import com.example.test.data.local.entity.CellInfoEntity
//
//object CellInfoCollector {
//
//    fun collect(context: Context): CellInfoEntity {
//        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        val simPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
//        val selectedSim = simPref.getString("selected_sim", "SIM1")
//
//        val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
//        val subscriptionList = subManager.activeSubscriptionInfoList ?: emptyList()
//
//        if (subscriptionList.isEmpty()) {
//            Toast.makeText(context, "هیچ سیم‌کارتی فعال نیست!", Toast.LENGTH_LONG).show()
//            Log.e("CellInfoCollector", "❌ No SIMs available")
//            return emptyEntity("No SIM")
//        }
//
//        if (selectedSim == "SIM2" && subscriptionList.size < 2) {
//            Toast.makeText(context, "سیم‌کارت دوم موجود نیست!", Toast.LENGTH_LONG).show()
//            Log.e("CellInfoCollector", "❌ SIM2 not available")
//            return emptyEntity("Missing SIM2")
//        }
//
//        val subId = when (selectedSim) {
//            "SIM2" -> subscriptionList.getOrNull(1)?.subscriptionId
//            else -> subscriptionList.getOrNull(0)?.subscriptionId
//        } ?: run {
//            Toast.makeText(context, "خطا در دریافت اطلاعات سیم‌کارت!", Toast.LENGTH_LONG).show()
//            Log.e("CellInfoCollector", "❌ Failed to resolve subscription ID")
//            return emptyEntity("Subscription ID error")
//        }
//
//        val baseTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
//        val telephonyManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            baseTelephonyManager.createForSubscriptionId(subId)
//        } else baseTelephonyManager
//
//        val location = if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
//        } else null
//
//        val latitude = location?.latitude
//        val longitude = location?.longitude
//
//        val networkTypeInt = telephonyManager.dataNetworkType
//        val networkType = networkTypeToString(networkTypeInt)
//        val plmnId = telephonyManager.networkOperator
//
//        var rsrp: Int? = null
//        var rsrq: Int? = null
//        var rssi: Int? = null
//        var rscp: Int? = null
//        var ecNo: Int? = null
//
//        var tac: Int? = null
//        var lac: Int? = null
//        var rac: Int? = null
//        var cellId: Int? = null
//        var band: Int? = null
//        var arfcn: Int? = null
//        var rxLev: Int? = null
//
//        val cellInfos = telephonyManager.allCellInfo
//        if (!cellInfos.isNullOrEmpty()) {
//            val cell = cellInfos.firstOrNull {
//                try {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                        val subIdField = it.javaClass.getMethod("getSubscriptionId")
//                        val id = subIdField.invoke(it) as? Int
//                        id == subId
//                    } else {
//                        // فرض کن اولین یا دومین سلول متعلق به SIM1 یا SIM2 است
//                        val index = if (selectedSim == "SIM2" && cellInfos.size >= 2) 1 else 0
//                        it == cellInfos[index]
//                    }
//                } catch (e: Exception) {
//                    false
//                }
//            }
//
//            when (cell) {
//                is CellInfoLte -> {
//                    val ss = cell.cellSignalStrength
//                    rsrp = ss.rsrp
//                    rsrq = ss.rsrq
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        rssi = ss.rssi
//                    }
//                    val identity = cell.cellIdentity
//                    tac = identity.tac
//                    cellId = identity.ci
//                    band = identity.bandwidth
//                    arfcn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) identity.earfcn else null
//                }
//                is CellInfoWcdma -> {
//                    val ss = cell.cellSignalStrength
//                    rscp = ss.dbm
//                    val identity = cell.cellIdentity
//                    lac = identity.lac
//                    rac = try {
//                        val racMethod = identity.javaClass.getMethod("getRac")
//                        racMethod.invoke(identity) as? Int
//                    } catch (e: Exception) {
//                        Log.e("CellInfoCollector", "WCDMA RAC reflection failed: ${e.message}")
//                        null
//                    }
//                    cellId = identity.cid
//                    arfcn = identity.uarfcn
//                    ecNo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        try {
//                            val ecnoMethod = ss.javaClass.getMethod("getEcNo")
//                            ecnoMethod.invoke(ss) as? Int
//                        } catch (e: Exception) {
//                            Log.e("CellInfoCollector", "WCDMA ecNo reflection failed: ${e.message}")
//                            null
//                        }
//                    } else null
//                }
//                is CellInfoGsm -> {
//                    val ss = cell.cellSignalStrength
//                    rssi = ss.dbm
//                    val identity = cell.cellIdentity
//                    lac = identity.lac
//                    rac = try {
//                        val racMethod = identity.javaClass.getMethod("getRac")
//                        racMethod.invoke(identity) as? Int
//                    } catch (e: Exception) {
//                        Log.e("CellInfoCollector", "GSM RAC reflection failed: ${e.message}")
//                        null
//                    }
//                    cellId = identity.cid
//                    arfcn = identity.arfcn
//                    val rawDbm = ss.dbm
//                    rxLev = if (rawDbm != CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
//                        ((rawDbm + 113) / 2).coerceIn(0, 63)
//                    } else null
//                }
//                else -> {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell?.javaClass?.simpleName == "CellInfoNr") {
//                        try {
//                            val identityMethod = cell.javaClass.getMethod("getCellIdentity")
//                            val identity = identityMethod.invoke(cell)
//                            val tacField = identity?.javaClass?.getMethod("getTac")
//                            tac = tacField?.invoke(identity) as? Int
//                            val ciField = identity?.javaClass?.getMethod("getNci")
//                            cellId = ciField?.invoke(identity) as? Int
//                            val arfcnField = identity?.javaClass?.getMethod("getNrarfcn")
//                            arfcn = arfcnField?.invoke(identity) as? Int
//                            val signalMethod = cell.javaClass.getMethod("getCellSignalStrength")
//                            val ss = signalMethod.invoke(cell)
//                            val dbmMethod = ss?.javaClass?.getMethod("getDbm")
//                            rsrp = dbmMethod?.invoke(ss) as? Int
//                        } catch (e: Exception) {
//                            Log.e("CellInfoCollector", "NR reflection failed: ${e.message}")
//                        }
//                    }
//                }
//            }
//        }
//
//        return CellInfoEntity(
//            timestamp = System.currentTimeMillis(),
//            latitude = latitude,
//            longitude = longitude,
//            networkType = "$networkType ($networkTypeInt)",
//            plmnId = plmnId,
//            lac = lac,
//            rac = rac,
//            tac = tac,
//            cellId = cellId,
//            band = band,
//            arfcn = arfcn,
//            rsrp = rsrp,
//            rsrq = rsrq,
//            rssi = rssi,
//            rscp = rscp,
//            ecNo = ecNo,
//            rxLev = rxLev
//        )
//    }
//
//    private fun emptyEntity(reason: String): CellInfoEntity {
//        return CellInfoEntity(
//            timestamp = System.currentTimeMillis(),
//            networkType = reason,
//            plmnId = null,
//            latitude = null,
//            longitude = null,
//            lac = null,
//            rac = null,
//            tac = null,
//            cellId = null,
//            band = null,
//            arfcn = null,
//            rsrp = null,
//            rsrq = null,
//            rssi = null,
//            rscp = null,
//            ecNo = null,
//            rxLev = null
//        )
//    }
//
//    private fun networkTypeToString(networkType: Int): String {
//        return when (networkType) {
//            TelephonyManager.NETWORK_TYPE_GPRS -> "GPRS (2G)"
//            TelephonyManager.NETWORK_TYPE_EDGE -> "EDGE (2.75G)"
//            TelephonyManager.NETWORK_TYPE_UMTS -> "UMTS (3G)"
//            TelephonyManager.NETWORK_TYPE_CDMA -> "CDMA (2G)"
//            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO rev.0 (3G)"
//            TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO rev.A (3G)"
//            TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO rev.B (3G)"
//            TelephonyManager.NETWORK_TYPE_1xRTT -> "1xRTT (2G)"
//            TelephonyManager.NETWORK_TYPE_HSDPA -> "HSDPA (3.5G)"
//            TelephonyManager.NETWORK_TYPE_HSUPA -> "HSUPA (3.5G)"
//            TelephonyManager.NETWORK_TYPE_HSPA -> "HSPA (3G)"
//            TelephonyManager.NETWORK_TYPE_HSPAP -> "HSPA+ (3.75G)"
//            TelephonyManager.NETWORK_TYPE_LTE -> "LTE (4G)"
//            TelephonyManager.NETWORK_TYPE_NR -> "NR (5G)"
//            TelephonyManager.NETWORK_TYPE_EHRPD -> "eHRPD (3.75G)"
//            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA (3G - China)"
//            TelephonyManager.NETWORK_TYPE_IWLAN -> "IWLAN (WiFi Calling)"
//            else -> "Unknown ($networkType)"
//        }
//    }
//}
