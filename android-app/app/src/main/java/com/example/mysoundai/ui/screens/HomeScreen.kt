package com.example.mysoundai.ui.screens

import android.R.attr.title
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.mysoundai.ui.viewmodel.HomeViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import com.example.mysoundai.ui.components.SongItem
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mysoundai.ui.components.DynamicGradientBox
import com.example.mysoundai.ui.components.SongCard
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.mysoundai.R
import android.content.Intent
import com.example.mysoundai.data.remote.SpotifyAuthManager
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.zIndex
import androidx.room.util.query
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel, paddingValues: PaddingValues) {
    val songs = viewModel.songList.value
    val isLoading = viewModel.isLoading.value
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val firstSongImageUrl = songs.firstOrNull()?.imageUrl ?: ""
    val scrollState = rememberLazyListState()
    val searchBarAlpha by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 0.7f else 1f
        }
    }
    DynamicGradientBox(imageUrl = firstSongImageUrl, modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 80.dp,
                        top = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        HomeHeader()
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
//                        SpotifyConnectButton(onConnectClick = {
//                            val authUrl = SpotifyAuthManager.getAuthUrl()
//                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
//                            context.startActivity(intent)
//                        })
                        }
                    }
                    item {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                SearchBarComponent(
                                    query = viewModel.searchQuery,
                                    onQueryChange = { viewModel.onSearchQueryChanged(it)
                                        if (scrollState.firstVisibleItemIndex > 0) {
                                            scope.launch {
                                                scrollState.animateScrollToItem(0)
                                            }
                                        }
                                    },
                                    alpha = searchBarAlpha,
                                    modifier = Modifier
                                        .statusBarsPadding()
                                )
                            }
                        }
                    }
                    item {
                        val titleText = if (viewModel.searchQuery.isEmpty())
                            "Gợi ý cho bạn"
                        else "Kết quả tìm kiếm"
                        HomeRowTitle(title = titleText)
                    }
                    items(viewModel.filteredSongs) { song ->
                        SongItem(song = song)
                    }
                    if (viewModel.filteredSongs.isEmpty() && viewModel.searchQuery.isNotEmpty()) {
                        item {
                            Text(
                                text = "Không tìm thấy bài hát nào cho '${viewModel.searchQuery}'",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    item {
                        HomeRowTitle("Playlist nổi bật")
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(songs) { song ->
                                SongCard(song = song)
                            }
                        }
                    }
                    item {
                        HomeRowTitle("Gợi ý cho bạn")
                    }
                    items(songs) { song ->
                        SongItem(song = song)
                    }
                }
            }

        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarComponent(query:String,
                       onQueryChange: (String) -> Unit,
                       alpha: Float,
                       modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp),
        placeholder = { Text("Bạn muốn nghe gì?", color = Color.Gray)},
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = alpha),
            unfocusedContainerColor = Color.White.copy(alpha = alpha),
            disabledContainerColor = Color.White.copy(alpha = alpha),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
            focusedPlaceholderColor = Color.White
        ),
        shape = RoundedCornerShape(25.dp),
        singleLine = true
    )
}


//@Composable
//fun SpotifyConnectButton(onConnectClick: () -> Unit) {
//    Button(
//        onClick = onConnectClick,
//        colors = ButtonDefaults.buttonColors(
//            containerColor = Color(0xFF1DB954)
//        ),
//        modifier = Modifier.padding(16.dp),
//        shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp)
//    ) {
//        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(painter = painterResource(
//                id = R.drawable.ic_spotify),
//                contentDescription = null,
//                tint = Color.White)
//            Text(
//                text = "Kết nối Spotify",
//                style = MaterialTheme.typography.titleMedium,
//                color = Color.White
//            )
//        }
//    }
//}

@Composable
fun HomeHeader() {
    Text(text = "Chào buổi tối",
        style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
        color = androidx.compose.ui.graphics.Color.White,
        modifier = Modifier.padding(bottom = 16.dp))
}
@Composable
fun HomeRowTitle(title: String) {
    Text(text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp))
}