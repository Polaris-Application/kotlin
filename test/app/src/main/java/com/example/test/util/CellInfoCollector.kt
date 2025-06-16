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

        if (selectedSim == "SIM2" && subscriptionList.size < 2) {
            Toast.makeText(context, "سیم‌کارت دوم موجود نیست!", Toast.LENGTH_LONG).show()
            Log.e("CellInfoCollector", "❌ SIM2 not available")
            return emptyEntity("Missing SIM2")
        }

        val subId = when (selectedSim) {
            "SIM2" -> subscriptionList.getOrNull(1)?.subscriptionId
            else -> subscriptionList.getOrNull(0)?.subscriptionId
        } ?: run {
            Toast.makeText(context, "خطا در دریافت اطلاعات سیم‌کارت!", Toast.LENGTH_LONG).show()
            Log.e("CellInfoCollector", "❌ Failed to resolve subscription ID")
            return emptyEntity("Subscription ID error")
        }

        val baseTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val telephonyManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            baseTelephonyManager.createForSubscriptionId(subId)
        } else baseTelephonyManager

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

        val cellInfos = telephonyManager.allCellInfo
        if (!cellInfos.isNullOrEmpty()) {
            val cell = cellInfos[0]
            when (cell) {
                is CellInfoLte -> {
                    val ss = cell.cellSignalStrength
                    rsrp = ss.rsrp
                    rsrq = ss.rsrq
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        rssi = ss.rssi
                    }
                    val identity = cell.cellIdentity
                    tac = identity.tac
                    cellId = identity.ci
                    band = identity.bandwidth
                    arfcn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) identity.earfcn else null
                }
                is CellInfoWcdma -> {
                    val ss = cell.cellSignalStrength
                    rscp = ss.dbm
                    val identity = cell.cellIdentity
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
                }
                is CellInfoGsm -> {
                    val ss = cell.cellSignalStrength
                    rssi = ss.dbm
                    val identity = cell.cellIdentity
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
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell.javaClass.simpleName == "CellInfoNr") {
                        try {
                            val identityMethod = cell.javaClass.getMethod("getCellIdentity")
                            val identity = identityMethod.invoke(cell)
                            val tacField = identity?.javaClass?.getMethod("getTac")
                            tac = tacField?.invoke(identity) as? Int
                            val ciField = identity?.javaClass?.getMethod("getNci")
                            cellId = ciField?.invoke(identity) as? Int
                            val arfcnField = identity?.javaClass?.getMethod("getNrarfcn")
                            arfcn = arfcnField?.invoke(identity) as? Int
                            val signalMethod = cell.javaClass.getMethod("getCellSignalStrength")
                            val ss = signalMethod.invoke(cell)
                            val dbmMethod = ss?.javaClass?.getMethod("getDbm")
                            rsrp = dbmMethod?.invoke(ss) as? Int
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
            rxLev = rxLev
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
            rxLev = null
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
