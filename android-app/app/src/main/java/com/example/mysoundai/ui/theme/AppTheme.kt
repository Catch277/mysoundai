package com.example.mysoundai.ui.theme

enum class AppTheme { LIGHT, DARK, SYSTEM;

    companion object {
        fun from(value: String?) = entries.find { it.name == value } ?: SYSTEM
    }
}