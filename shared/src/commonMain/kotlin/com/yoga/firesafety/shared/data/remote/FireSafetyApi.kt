package com.yoga.firesafety.shared.data.remote

import com.yoga.firesafety.shared.data.remote.dto.AuthenticationRequest
import com.yoga.firesafety.shared.data.remote.dto.AuthenticationResponse
import com.yoga.firesafety.shared.data.remote.dto.RegisterRequest
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class FireSafetyApi(private val client: HttpClient) {
    // Change this to true when deploying to your phone to use the Render URL
    private val useProduction = false 
    
    private val productionUrl = "https://your-backend-app.onrender.com/api/v1"
    private val localUrl = "http://10.0.2.2:8080/api/v1"

    private val baseUrl = if (useProduction) productionUrl else localUrl

    suspend fun register(request: RegisterRequest): AuthenticationResponse {
        return client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        return client.post("$baseUrl/auth/authenticate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend fun getWorkOrders(): List<WorkOrder> {
        return client.get("$baseUrl/work-orders/my-jobs").body()
    }

    suspend fun updateWorkOrderStatus(id: String, status: WorkOrderStatus): WorkOrder {
        return client.patch("$baseUrl/work-orders/$id/status") {
            parameter("status", status)
        }.body()
    }

    suspend fun getAllUsers(): List<com.yoga.firesafety.shared.domain.model.User> {
        return client.get("$baseUrl/users").body()
    }

    suspend fun updateUserRole(userId: String, role: Role): com.yoga.firesafety.shared.domain.model.User {
        return client.patch("$baseUrl/users/$userId/role") {
            parameter("role", role.name)
        }.body()
    }
}
