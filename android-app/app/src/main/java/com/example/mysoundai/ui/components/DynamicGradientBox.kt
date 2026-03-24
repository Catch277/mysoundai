package com.example.mysoundai.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.text.DynamicLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.palette.graphics.Palette
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DynamicGradientBox(
    imageUrl: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    var gradientColors by remember { mutableStateOf(listOf(Color.Black, Color.Gray)) }

    LaunchedEffect(imageUrl) {
        val bitmap = loadBitmapFromUrl(context, imageUrl)
        if (bitmap != null) {
            Palette.from(bitmap).generate().vibrantSwatch?.let { swatch ->
                val mainColor = Color(swatch.rgb)
                gradientColors = listOf(mainColor.copy(alpha = 0.8f), Color(0xFF121212))
            }
        }
    }

    Box(
        modifier = modifier.background(
            brush = Brush.verticalGradient(colors = gradientColors)
        ),
        content = content
    )
}

suspend fun loadBitmapFromUrl(context: Context, url: String): Bitmap? = withContext(Dispatchers.IO) {
    val request = ImageRequest.Builder(context)
        .data(url)
        .allowHardware(false)
        .build()

    val result = ImageLoader(context).execute(request)
    if (result is SuccessResult) {
        (result.drawable as android.graphics.drawable.BitmapDrawable).bitmap
    } else {
        null
    }
}

