package com.example.pocketplan.domain.usecase

import com.example.pocketplan.data.model.User
import com.example.pocketplan.data.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, passwordHash: String): Result<User> {
        return authRepository.login(email, passwordHash)
    }
}
