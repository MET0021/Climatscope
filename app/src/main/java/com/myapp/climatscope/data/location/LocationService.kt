package com.myapp.climatscope.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface LocationService {
    suspend fun getCurrentLocation(): Result<Location>
    fun getLocationUpdates(): Flow<Location>
    fun hasLocationPermission(): Boolean
}

class DefaultLocationService(
    private val context: Context
) : LocationService {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Result<Location> {
        return try {
            if (!hasLocationPermission()) {
                return Result.failure(SecurityException("Location permission not granted"))
            }

            suspendCancellableCoroutine { continuation ->
                val cancellationToken = CancellationTokenSource()

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationToken.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(Result.success(location))
                    } else {
                        continuation.resume(Result.failure(Exception("Location not found")))
                    }
                }.addOnFailureListener { exception ->
                    continuation.resume(Result.failure(exception))
                }

                continuation.invokeOnCancellation {
                    cancellationToken.cancel()
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close(SecurityException("Location permission not granted"))
            return@callbackFlow
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L // 10 seconds
        ).setMinUpdateIntervalMillis(5000L) // 5 seconds
         .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    trySend(location)
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            context.mainLooper
        )

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun hasLocationPermission(): Boolean {
        return context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) ==
               android.content.pm.PackageManager.PERMISSION_GRANTED ||
               context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
               android.content.pm.PackageManager.PERMISSION_GRANTED
    }
}
