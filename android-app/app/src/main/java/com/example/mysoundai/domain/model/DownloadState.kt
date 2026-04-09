package com.example.mysoundai.domain.model

sealed class DownloadState {
    data object Idle: DownloadState()

    data class Downloading(
        val progress: Int //0 -> 100
    ) : DownloadState()

    data object Completed : DownloadState()

    data class Failed(
        val message: String,
    ) : DownloadState()

    data object Checking : DownloadState()
    data object Cancelled : DownloadState()
}

