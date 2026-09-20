package com.yoga.firesafety.shared.presentation.dashboard

import android.location.Geocoder
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    
    // Default to Toronto coordinates
    val defaultLocation = LatLng(43.6532, -79.3832)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 11f)
    }

    // State for geocoded markers
    val markers = remember { mutableStateListOf<Pair<LatLng, WorkOrder>>() }

    // Geocode addresses whenever workOrders change
    LaunchedEffect(workOrders) {
        markers.clear()
        workOrders.forEach { order ->
            try {
                withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    val addresses = geocoder.getFromLocationName(order.address, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val latLng = LatLng(addresses[0].latitude, addresses[0].longitude)
                        withContext(Dispatchers.Main) {
                            markers.add(latLng to order)
                            // If it's the first marker, move the camera
                            if (markers.size == 1) {
                                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 13f)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore geocoding errors for individual markers
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            mapToolbarEnabled = true
        ),
        properties = MapProperties(
            mapType = MapType.NORMAL,
            isMyLocationEnabled = false
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
