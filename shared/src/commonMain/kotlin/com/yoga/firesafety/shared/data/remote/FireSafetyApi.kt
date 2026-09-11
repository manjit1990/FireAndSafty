package com.yoga.firesafety.shared.data.remote

import com.yoga.firesafety.shared.data.remote.dto.AuthenticationRequest
import com.yoga.firesafety.shared.data.remote.dto.AuthenticationResponse
import com.yoga.firesafety.shared.data.remote.dto.RegisterRequest
import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.model.WorkOrder
import com.yoga.firesafety.shared.domain.model.WorkOrderStatus
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class FireSafetyApi(private val client: HttpClient) {
    // Change this to true when deploying to your phone to use the Render URL
    private val useProduction = true 
    
    private val productionUrl = "https://fireandsafty.onrender.com/api/v1"
    private val localUrl = "http://10.0.2.2:8080/api/v1"

    private val baseUrl = if (useProduction) productionUrl else localUrl

    private var authToken: String? = null

    fun setAuthToken(token: String?) {
        authToken = token
    }

    private fun HttpRequestBuilder.addAuthHeader() {
        authToken?.let { token ->
            header(HttpHeaders.Authorization, "Bearer $token")
        }
    }

    suspend fun register(request: RegisterRequest): AuthenticationResponse {
        val response = client.post("$baseUrl/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw Exception("Registration failed (${response.status.value}): $errorBody")
        }
        val authResponse: AuthenticationResponse = response.body()
        setAuthToken(authResponse.token)
        return authResponse
    }

    suspend fun authenticate(request: AuthenticationRequest): AuthenticationResponse {
        val response = client.post("$baseUrl/auth/authenticate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw Exception("Login failed (${response.status.value}): $errorBody")
        }
        val authResponse: AuthenticationResponse = response.body()
        setAuthToken(authResponse.token)
        return authResponse
    }

    suspend fun getWorkOrders(): List<WorkOrder> {
        return client.get("$baseUrl/work-orders/my-jobs") {
            addAuthHeader()
        }.body()
    }

    suspend fun updateWorkOrderStatus(id: String, status: WorkOrderStatus): WorkOrder {
        return client.patch("$baseUrl/work-orders/$id/status") {
            addAuthHeader()
            parameter("status", status)
        }.body()
    }

    suspend fun getAllUsers(): List<com.yoga.firesafety.shared.domain.model.User> {
        return client.get("$baseUrl/users") {
            addAuthHeader()
        }.body()
    }

    suspend fun updateUserRole(userId: String, role: Role): com.yoga.firesafety.shared.domain.model.User {
        return client.patch("$baseUrl/users/$userId/role") {
            addAuthHeader()
            parameter("role", role.name)
        }.body()
    }

    suspend fun assignWorkOrder(id: String, technicianId: String, scheduledAt: String?): WorkOrder {
        return client.patch("$baseUrl/work-orders/$id/assign") {
            addAuthHeader()
            parameter("technicianId", technicianId)
            scheduledAt?.let { parameter("scheduledAt", it) }
        }.body()
    }
}
