package com.example.pocketplan.data.repository

import com.example.pocketplan.data.local.SessionManager
import com.example.pocketplan.data.local.UserDao
import com.example.pocketplan.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : AuthRepository {
    
    private val _currentUser = MutableStateFlow<User?>(null)

    override suspend fun login(email: String, passwordHash: String): Result<User> {
        val user = userDao.getUserByEmail(email)
        return if (user != null && user.passwordHash == passwordHash) {
            sessionManager.saveUserId(user.id)
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override suspend fun register(name: String, email: String, passwordHash: String): Result<User> {
        val existingUser = userDao.getUserByEmail(email)
        if (existingUser != null) {
            return Result.failure(Exception("Email already registered"))
        }
        
        val user = User(
            id = java.util.UUID.randomUUID().toString(), 
            name = name, 
            email = email, 
            passwordHash = passwordHash
        )
        userDao.insertUser(user)
        sessionManager.saveUserId(user.id)
        _currentUser.value = user
        return Result.success(user)
    }

    override fun getCurrentUser(): Flow<User?> = _currentUser.asStateFlow()

    override suspend fun restoreSession(): User? {
        val userId = sessionManager.getUserId() ?: return null
        val user = userDao.getUserById(userId)
        _currentUser.value = user
        return user
    }

    override suspend fun logout() {
        sessionManager.clearSession()
        _currentUser.value = null
    }

    override suspend fun updateProfilePicture(userId: String, path: String): Result<Unit> {
        return try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(profilePicPath = path)
                userDao.insertUser(updatedUser)
                _currentUser.value = updatedUser
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateName(userId: String, newName: String): Result<Unit> {
        return try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(name = newName)
                userDao.insertUser(updatedUser)
                _currentUser.value = updatedUser
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePassword(userId: String, newPasswordHash: String): Result<Unit> {
        return try {
            val user = userDao.getUserById(userId)
            if (user != null) {
                val updatedUser = user.copy(passwordHash = newPasswordHash)
                userDao.insertUser(updatedUser)
                _currentUser.value = updatedUser
                Result.success(Unit)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
