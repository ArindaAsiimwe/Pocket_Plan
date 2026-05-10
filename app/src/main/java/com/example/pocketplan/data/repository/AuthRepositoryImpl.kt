package com.example.pocketplan.data.repository

import com.example.pocketplan.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user!!.uid
            val doc = firestore.collection("users").document(uid).get().await()
            val user = User(
                id = uid,
                name = doc.getString("name") ?: "",
                email = email
            )
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user!!.uid

            // Save user profile to Firestore
            val userData = mapOf(
                "name" to name,
                "email" to email,
                "createdAt" to Timestamp.now()
            )
            firestore.collection("users").document(uid).set(userData).await()

            val user = User(id = uid, name = name, email = email)
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Registration failed"))
        }
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun restoreSession(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return try {
            val doc = firestore.collection("users").document(firebaseUser.uid).get().await()
            val user = User(
                id = firebaseUser.uid,
                name = doc.getString("name") ?: "",
                email = firebaseUser.email ?: ""
            )
            _currentUser.value = user
            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to send reset email"))
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        _currentUser.value = null
    }
}