package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoNr
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.MobileNetworksScanner
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.CellInfo
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.MobileNetworkData
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

internal class DefaultMobileNetworksScanner(private val context: Context) : MobileNetworksScanner {

    private val telephonyManager =
        context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    private val _mobileNetworkDataFlow = MutableSharedFlow<MobileNetworkData>(
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    override val mobileNetworkDataFlow = _mobileNetworkDataFlow.asSharedFlow()

    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var scheduledFuture: ScheduledFuture<*>? = null

    override fun startScanning(): Result<Unit> {
        if (!hasRequiredPermissions()) {
            return Result.failure(SecurityException("Missing required permissions for Mobile Network scanning"))
        }

        scheduledFuture = executor.scheduleWithFixedDelay(
            { processCellInfo() },
            0,
            10,
            TimeUnit.SECONDS
        )

        return Result.success(Unit)
    }

    override fun stopScanning() {
        scheduledFuture?.cancel(false)
        scheduledFuture = null
    }

    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_PHONE_STATE
                ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    private fun processCellInfo() {
        if (!hasRequiredPermissions()) return

        val allCellInfo = telephonyManager.allCellInfo ?: return
        val timestamp = System.currentTimeMillis()

        val primaryCell = allCellInfo.firstOrNull { it.isRegistered }?.let { mapToDomain(it) }
        val neighboringCells = allCellInfo.filter { !it.isRegistered }.map { mapToDomain(it) }

        val mobileNetworkData = MobileNetworkData(
            timestamp = timestamp,
            primaryCell = primaryCell,
            neighboringCells = neighboringCells
        )

        _mobileNetworkDataFlow.tryEmit(mobileNetworkData)
    }

    private fun mapToDomain(cellInfo: android.telephony.CellInfo): CellInfo {
        return when (cellInfo) {
            is CellInfoLte -> {
                val identity = cellInfo.cellIdentity
                val signal = cellInfo.cellSignalStrength
                CellInfo(
                    type = "LTE",
                    cellId = identity.ci.takeIf { it != Int.MAX_VALUE },
                    lac = identity.tac.takeIf { it != Int.MAX_VALUE },
                    mcc = identity.mccString,
                    mnc = identity.mncString,
                    signalStrength = signal.dbm,
                    timingAdvance = signal.timingAdvance.takeIf { it != Int.MAX_VALUE }
                )
            }

            is CellInfoGsm -> {
                val identity = cellInfo.cellIdentity
                val signal = cellInfo.cellSignalStrength
                CellInfo(
                    type = "GSM",
                    cellId = identity.cid.takeIf { it != Int.MAX_VALUE },
                    lac = identity.lac.takeIf { it != Int.MAX_VALUE },
                    mcc = identity.mccString,
                    mnc = identity.mncString,
                    signalStrength = signal.dbm,
                    timingAdvance = null
                )
            }

            is CellInfoWcdma -> {
                val identity = cellInfo.cellIdentity
                val signal = cellInfo.cellSignalStrength
                CellInfo(
                    type = "WCDMA",
                    cellId = identity.cid.takeIf { it != Int.MAX_VALUE },
                    lac = identity.lac.takeIf { it != Int.MAX_VALUE },
                    mcc = identity.mccString,
                    mnc = identity.mncString,
                    signalStrength = signal.dbm,
                    timingAdvance = null
                )
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cellInfo is CellInfoNr) {
                    val identity = cellInfo.cellIdentity as android.telephony.CellIdentityNr
                    val signal = cellInfo.cellSignalStrength as android.telephony.CellSignalStrengthNr
                    CellInfo(
                        type = "NR",
                        cellId = null,
                        lac = identity.tac.takeIf { it != Int.MAX_VALUE },
                        mcc = identity.mccString,
                        mnc = identity.mncString,
                        signalStrength = signal.dbm,
                        timingAdvance = null
                    )
                } else {
                    CellInfo(
                        type = "Unknown",
                        cellId = null,
                        lac = null,
                        mcc = null,
                        mnc = null,
                        signalStrength = null,
                        timingAdvance = null
                    )
                }
            }
        }
    }
}
