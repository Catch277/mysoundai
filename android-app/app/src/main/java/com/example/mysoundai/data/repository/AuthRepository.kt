package com.example.mysoundai.data.repository

import android.net.Uri
import com.example.mysoundai.data.model.UserData
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth) {
    private val firestore = Firebase.firestore

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

    suspend fun signInWithEmail(email: String, password: String): Result<UserData?> = try {
        auth.signInWithEmailAndPassword(email, password).await()
        val userData = auth.currentUser?.let {
            UserData(it.uid, it.displayName, it.photoUrl?.toString())
        }
        Result.success(userData)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun sendPasswordResetEmail(email: String) = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    suspend fun signInWithGoogle(idToken: String): Result<UserData?> {
        return Result.failure(Exception("ERROR_GOOGLE_SDK_NOT_CONFIGURED"))
    }

    suspend fun signInWithFacebook(accessToken: String): Result<UserData?> {
        return Result.failure(Exception("ERROR_FACEBOOK_SDK_NOT_CONFIGURED"))
    }

    suspend fun updateProfile(displayName: String? = null, photoUri: Uri? = null): Result<UserData?> {
        return try {
            val user = auth.currentUser
                ?: return Result.failure(Exception("ERROR_NOT_LOGGED_IN"))
            val profileUpdates = userProfileChangeRequest {
                displayName?.let { name -> this.displayName = name }
                photoUri?.let { uri -> this.photoUri = uri }
            }
            user.updateProfile(profileUpdates).await()
            user.reload().await()
            val updatedUser = auth.currentUser?.let {
                UserData(it.uid, it.displayName, it.photoUrl?.toString())
            }
            Result.success(updatedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveUserSettings(settings: Map<String, Any>) = try {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid)
                .set(settings, SetOptions.merge())
                .await()
            Result.success(Unit)
        } else {
            Result.failure(Exception("ERROR_NOT_LOGGED_IN"))
        }
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getUserSettings(): Result<Map<String, Any>?> = try {
        val uid = auth.currentUser?.uid
            ?: return Result.failure(Exception("ERROR_NOT_LOGGED_IN"))
            val snapshot = firestore.collection("users")
                .document(uid).get().await()
            Result.success(snapshot.data)
    } catch (e: Exception) { Result.failure(e) }
}
