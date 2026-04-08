package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.UserDao
import com.example.pocketplan.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : AuthRepository {
    override suspend fun login(email: String, passwordHash: String): Result<User> {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.passwordHash == passwordHash) {
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid credentials"))
        }
    }

    override suspend fun register(name: String, email: String, passwordHash: String): Result<User> {
        val user = User(id = java.util.UUID.randomUUID().toString(), name = name, email = email, passwordHash = passwordHash)
        userDao.insertUser(user)
        return Result.success(user)
    }

    override fun getCurrentUser(): Flow<User?> = flow {
        // Placeholder for session management
        emit(null)
    }
}
