package com.example.mysoundai.data.repository

import android.net.Uri
import com.example.mysoundai.data.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {
    fun getCurrentUser(): UserData? {
        val user = auth.currentUser
        return user?.let {
            UserData(it.uid, it.displayName, it.photoUrl?.toString())
        }
    }


    val authState: Flow<UserData?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            val userData = firebaseUser?.let {
                UserData(it.uid, it.displayName, it.photoUrl?.toString())
            }
            trySend(userData)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<UserData?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.sendEmailVerification()?.await()
            val userData = result.user?.let {
                UserData(it.uid, it.displayName, it.photoUrl?.toString())
            }
            Result.success(userData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String) = try {
        auth.signInWithEmailAndPassword(email, password).await()
        Result.success(auth.currentUser)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendPasswordResetEmail(email: String) = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun signInWithGoogle(idToken: String): Result<UserData?> {
        return Result.failure(Exception("Chưa cài đặt Google SDK"))
    }

    suspend fun signInWithFacebook(accessToken: String): Result<UserData?> {
        return Result.failure(Exception("Chưa cài đặt Facebook SDK"))
    }

    suspend fun updateProfile(displayName: String? = null, photoUri: Uri? = null) = try {
        val profileUpdates = userProfileChangeRequest {
            displayName?.let { name -> this.displayName = name }
            photoUri?.let { uri -> this.photoUri = uri }
        }
        auth.currentUser?.updateProfile(profileUpdates)?.await()
        Result.success(Unit)
        } catch (e: Exception) {
        Result.failure(e)
    }
}
