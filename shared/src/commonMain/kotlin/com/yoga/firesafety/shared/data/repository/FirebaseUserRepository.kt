package com.yoga.firesafety.shared.data.repository

import com.yoga.firesafety.shared.domain.model.Role
import com.yoga.firesafety.shared.domain.model.User
import com.yoga.firesafety.shared.domain.repository.UserRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseUserRepository : UserRepository {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val collection = firestore.collection("users")

    override suspend fun register(firstName: String, lastName: String, email: String, password: String, role: Role, phoneNumber: String): User {
        val result = auth.createUserWithEmailAndPassword(email, password)
        val firebaseUser = result.user ?: throw Exception("Registration failed")
        
        val user = User(
            id = firebaseUser.uid,
            email = email,
            firstName = firstName,
            lastName = lastName,
            role = role,
            phoneNumber = phoneNumber,
            isAdmin = false
        )
        
        collection.document(user.id).set(user)
        return user
    }

    override suspend fun login(email: String, password: String): User {
        val result = auth.signInWithEmailAndPassword(email, password)
        val firebaseUser = result.user ?: throw Exception("Login failed")
        
        val doc = collection.document(firebaseUser.uid).get()
        return doc.data<User>()
    }

    override fun getAllUsers(): Flow<List<User>> {
        return collection.snapshots.map { querySnapshot ->
            querySnapshot.documents.map { it.data() }
        }
    }

    override suspend fun updateUserRole(userId: String, role: Role) {
        collection.document(userId).update("role" to role.name)
    }

    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val doc = collection.document(firebaseUser.uid).get()
        return doc.data<User>()
    }

    override suspend fun updateUserProfile(userId: String, firstName: String, lastName: String, phoneNumber: String, profileImageUrl: String?) {
        collection.document(userId).update(
            "firstName" to firstName,
            "lastName" to lastName,
            "phoneNumber" to phoneNumber,
            "profileImageUrl" to profileImageUrl
        )
    }
}
