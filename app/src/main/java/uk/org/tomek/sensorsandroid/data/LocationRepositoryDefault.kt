package uk.org.tomek.sensorsandroid.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat
import uk.org.tomek.sensorsandroid.domain.LocationRepository

class LocationRepositoryDefault(private val context: Context) : LocationRepository {
    override fun getLastKnownLocation(): Result<Location> {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
            ?: return Result.failure(IllegalStateException("LocationManager not available"))

        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            return Result.failure(SecurityException("Location permission not granted"))
        }

        return try {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            
            for (provider in providers) {
                val location = locationManager.getLastKnownLocation(provider) ?: continue
                if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                    bestLocation = location
                }
            }

            if (bestLocation != null) {
                Result.success(bestLocation)
            } else {
                Result.failure(NoSuchElementException("No last known location found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
