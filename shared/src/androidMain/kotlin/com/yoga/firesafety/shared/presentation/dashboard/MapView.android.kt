package com.yoga.firesafety.shared.presentation.dashboard

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.yoga.firesafety.shared.domain.model.WorkOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
actual fun MapView(modifier: Modifier, workOrders: List<WorkOrder>) {
    val context = LocalContext.current
    var isMapInitialized by remember(context) { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(context) {
        withContext(Dispatchers.IO) {
            try {
                val availability = GoogleApiAvailability.getInstance()
                val resultCode = availability.isGooglePlayServicesAvailable(context)
                if (resultCode == ConnectionResult.SUCCESS) {
                    try {
                        MapsInitializer.initialize(context, MapsInitializer.Renderer.LATEST) {
                            isMapInitialized = true
                        }
                    } catch (_: Throwable) {
                        isMapInitialized = false
                    }
                } else {
                    isMapInitialized = false
                }
            } catch (_: Throwable) {
                // Catch SecurityException: Unknown calling package name 'com.google.android.gms'
                // or any GMS broker IPC failure
                isMapInitialized = false
            }
        }
    }

    if (isMapInitialized == false) {
        FallbackMapView(modifier = modifier, workOrders = workOrders)
        return
    }

    if (isMapInitialized == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isGeocoderAvailable = remember {
        try {
            Geocoder.isPresent()
        } catch (_: Throwable) {
            false
        }
    }

    val defaultLocation = LatLng(43.6532, -79.3832)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 11f)
    }

    val markers = remember { mutableStateListOf<Pair<LatLng, WorkOrder>>() }

    LaunchedEffect(workOrders, isGeocoderAvailable) {
        markers.clear()
        if (!isGeocoderAvailable) return@LaunchedEffect

        val geocoder = Geocoder(context, Locale.getDefault())
        workOrders.forEach { order ->
            try {
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(order.address, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
                        withContext(Dispatchers.Main) {
                            markers.add(latLng to order)
                            if (markers.size == 1) {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 13f)
                            }
                        }
                    }
                }
            } catch (_: Throwable) {
                // Ignore geocoding errors for individual markers
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            mapToolbarEnabled = true,
        ),
        properties = MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = false,
        )
    ) {
        markers.forEach { (latLng, order) ->
            Marker(
                state = rememberMarkerState(position = latLng),
                title = order.buildingName,
                snippet = order.address
            )
        }
    }
}

@Composable
private fun FallbackMapView(modifier: Modifier, workOrders: List<WorkOrder>) {
    Card(
        modifier = modifier.padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Map View",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Google Play Services is required for interactive map features.",
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
            if (workOrders.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Locations (${workOrders.size}):",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                workOrders.take(3).forEach { order ->
                    Text(
                        text = "• ${order.buildingName} - ${order.address}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
