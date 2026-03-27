package com.example.mysoundai.data.remote

object SpotifyAuthManager {
    private const val CLIENT_ID = "99f0422e64ce44bd1e0721d0d92d9da"
    private const val REDIRECT_URI = "mysoundai://callback"
    private const val SCOPES = "user-read-private user-read-email"

    fun getAuthUrl(): String {
        return "https://accounts.spotify.com/authorize?" +
                "client_id=$CLIENT_ID" +
                "&response_type=code" +
                "&redirect_uri=$REDIRECT_URI" +
                "&scope=$SCOPES"
    }
}