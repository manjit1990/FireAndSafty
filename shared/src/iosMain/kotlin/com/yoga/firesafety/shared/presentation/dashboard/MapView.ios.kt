package com.yoga.firesafety.shared.presentation.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import platform.WebKit.WKWebView
import platform.Foundation.NSURL
import platform.Foundation.NSURLRequest

import com.yoga.firesafety.shared.domain.model.WorkOrder

@OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
@Composable
actual fun MapView(modifier: Modifier, workOrders: List<WorkOrder>) {
    val htmlContent = remember(workOrders) {
        val markersJs = workOrders.mapIndexed { index, order ->
            """
            fetch('https://nominatim.openstreetmap.org/search?format=json&q=${order.address.replace("'", "\\'")}')
                .then(response => response.json())
                .then(data => {
                    if (data && data.length > 0) {
                        var lat = data[0].lat;
                        var lon = data[0].lon;
                        var marker = L.marker([lat, lon]).addTo(map);
                        marker.bindPopup("<b>${order.buildingName.replace("'", "\\'")}</b><br>${order.address.replace("'", "\\'")}").openPopup();
                        if (${index == 0}) map.setView([lat, lon], 13);
                    }
                });
            """
        }.joinToString("\n")

        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
            <style>
                body, html, #map { margin: 0; padding: 0; height: 100%; width: 100%; background: #070A13; }
                .leaflet-control-attribution { display: none !important; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', {
                    zoomControl: true,
                    attributionControl: false
                }).setView([43.6532, -79.3832], 12);

                L.tileLayer('https://{s}.basemaps.cartocdn.com/dark_all/{z}/{x}/{y}{r}.png', {
                    maxZoom: 19
                }).addTo(map);

                $markersJs
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    UIKitView(
        modifier = modifier,
        factory = {
            WKWebView().apply {
                loadHTMLString(htmlContent, baseURL = null)
            }
        },
        update = { webView ->
            webView.loadHTMLString(htmlContent, baseURL = null)
        }
    )
}
