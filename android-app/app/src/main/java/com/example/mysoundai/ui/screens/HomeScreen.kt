package com.example.mysoundai.ui.screens

import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.example.mysoundai.R
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.example.mysoundai.core.util.UiText
import com.example.mysoundai.domain.model.DownloadState
import com.example.mysoundai.ui.components.SongGridItem
import com.example.mysoundai.ui.components.ToastMessage
import com.example.mysoundai.ui.viewmodel.DownloadUiEvent
import com.example.mysoundai.ui.viewmodel.DownloadViewModel
import com.example.mysoundai.ui.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel,
               downloadViewModel: DownloadViewModel,
               playerViewModel: PlayerViewModel,
               paddingValues: PaddingValues) {
    val songs = viewModel.songList.value
    val isLoading = viewModel.isLoading.value
    val scope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val firstSongImageUrl = songs.firstOrNull()?.imageUrl ?: ""
    val scrollState = rememberLazyListState()
    val searchBarAlpha by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0) 0.7f else 1f
        }
    }

    val downloadStates by downloadViewModel.downloadStates.collectAsState()
    val downloadedSongs by downloadViewModel.downloadedSongs.collectAsState()
    val localPathsMap = downloadedSongs.associate { it.songId to it.filePath }

    var toastMessage by remember { mutableStateOf<UiText?>(null) }
    var toastTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        downloadViewModel.uiEvent.collect { event ->
            when (event) {
                is DownloadUiEvent.ShowToast -> {
                    toastMessage = event.message
                    toastTrigger++
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            downloadViewModel.resetCancelledStates()
            downloadViewModel.resetFailedStates()
        }
    }

    val titleSuggested = stringResource(R.string.home_title_suggested)
    val titleAllSong = stringResource(R.string.home_title_all_song)
    val titleSearchResults = stringResource(R.string.home_title_search_results)
    val titleFeatured = stringResource(R.string.home_title_featured)

    DynamicGradientBox(imageUrl = firstSongImageUrl,
                        modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                })
                            }
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                ) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = paddingValues.calculateBottomPadding() + 16.dp
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
                        SearchBarComponent(
                            query = viewModel.searchQuery,
                            onQueryChange = { viewModel.onSearchQueryChanged(it)
                                if (scrollState.firstVisibleItemIndex > 0) {
                                    scope.launch {
                                        scrollState.animateScrollToItem(0)
                                    }
                                } },
                            alpha = searchBarAlpha,
                            modifier = Modifier
                                .statusBarsPadding()
                        )
                    }
                    item {
                        val titleText = if (viewModel.searchQuery.isEmpty())
                            titleSuggested
                        else titleSearchResults
                        HomeRowTitle(title = titleText)
                    }
                    items(viewModel.filteredSongs) { song ->
                        val localSong = downloadedSongs.find { it.songId == song.id }
                        val state = if (localSong != null) {
                            DownloadState.Completed
                        } else {
                            downloadStates[song.id] ?: DownloadState.Idle
                        }
                        val isDownloaded = state is DownloadState.Completed
                        SongItem(song = song,
                                 state = state,
                                 onDownloadClick = {
                                     downloadViewModel.downloadSong(song)
                                 },
                                 onCancelClick = {
                                     downloadViewModel.cancelDownload(song)
                                 },
                                onItemClick = {
                                    val startIndex = songs.indexOf(song)
                                    if (startIndex != -1) {
                                        playerViewModel.playAudioList(
                                            songs = songs,
                                            startIndex = startIndex,
                                            localPaths = localPathsMap
                                        )
                                    }
                                }
                        )
                    }
                    if (viewModel.filteredSongs.isEmpty() && viewModel.searchQuery.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.home_search_no_results, viewModel.searchQuery),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    item {
                        HomeRowTitle(titleFeatured)
                        LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                            items(songs) { song ->
                                SongCard(song = song)
                            }
                        }
                    }
                    item {
                        HomeRowTitle(titleAllSong)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    val chunkedSongs = songs.chunked(2)

                    items(chunkedSongs) { rowSongs ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            for (song in rowSongs) {
                                val localSong = downloadedSongs.find { it.songId == song.id }
                                val state = if (localSong != null) {
                                    DownloadState.Completed
                                } else {
                                    downloadStates[song.id] ?: DownloadState.Idle
                                }
                                val isDownloaded = state is DownloadState.Completed
                                SongGridItem(
                                    modifier = Modifier.weight(1f),
                                    song = song,
                                    state = state,
                                    onDownloadClick = {
                                        downloadViewModel.downloadSong(song)
                                    },
                                    onCancelClick = {
                                        downloadViewModel.cancelDownload(song)
                                    },
                                    onItemClick = {
                                        val startIndex = songs.indexOf(song)
                                        if (startIndex != -1) {
                                            playerViewModel.playAudioList(
                                                songs = songs,
                                                startIndex = startIndex,
                                                localPaths = localPathsMap
                                            )
                                        }
                                    }
                                )
                            }
                            if (rowSongs.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
        toastMessage?.let { text ->
            ToastMessage(
                message = text.asString(),
                trigger = toastTrigger,
                onDismiss = { toastMessage = null }
            )
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
    val focusManager = LocalFocusManager.current

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(56.dp),
        placeholder = { Text(stringResource(R.string.home_search_placeholder), color = Color.DarkGray)},
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear Search"
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                focusManager.clearFocus()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White.copy(alpha = alpha),
            unfocusedContainerColor = Color.White.copy(alpha = alpha),
            disabledContainerColor = Color.White.copy(alpha = alpha),

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            unfocusedPlaceholderColor = Color.Black.copy(0.5f),
            focusedPlaceholderColor = Color.Black.copy(0.7f)
        ),
        shape = RoundedCornerShape(24.dp),
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
    val morning = stringResource(R.string.home_greeting_morning)
    val afternoon = stringResource(R.string.home_greeting_afternoon)
    val evening = stringResource(R.string.home_greeting_evening)
    val night = stringResource(R.string.home_greeting_night)

        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
                in 5..11 -> morning
                in 12..18 -> afternoon
                in 19..23 -> evening
                else -> night
            }

    Text(
        text = greeting,
        style = MaterialTheme.typography.headlineLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )
}
@Composable
fun HomeRowTitle(title: String) {
    Text(text = title,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        modifier = Modifier.padding(bottom = 8.dp))
}