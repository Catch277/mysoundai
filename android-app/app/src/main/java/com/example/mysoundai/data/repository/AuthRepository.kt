package com.example.mysoundai.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.example.mysoundai.data.model.UserData

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
}
