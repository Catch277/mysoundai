package com.example.mysoundai.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mysoundai.R
import com.example.mysoundai.ui.components.SongItem
import kotlinx.coroutines.delay
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Delete
import com.example.mysoundai.core.util.UiText
import com.example.mysoundai.data.local.room.toDomain
import com.example.mysoundai.domain.model.DownloadState
import com.example.mysoundai.ui.components.ToastMessage
import com.example.mysoundai.ui.viewmodel.DownloadUiEvent
import com.example.mysoundai.ui.viewmodel.DownloadViewModel
import com.example.mysoundai.ui.viewmodel.LibraryViewModel
import com.example.mysoundai.ui.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadedMusicScreen(
    viewModel: DownloadViewModel,
    playerViewModel: PlayerViewModel,
    libraryViewModel: LibraryViewModel,
    onNavigateToExplore: () -> Unit,
    onBack: () -> Unit
) {
    var toastMessage by remember { mutableStateOf<UiText?>(null) }
    var toastTrigger by remember { mutableIntStateOf(0) }

    val downloadStates by viewModel.downloadStates.collectAsState()
    val downloadedSongs by viewModel.downloadedSongs.collectAsState()
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val favoriteSongs by libraryViewModel.favoriteSongs.collectAsState()
    val favoriteIds = remember(favoriteSongs) { favoriteSongs.map { it.id }.toSet() }


    val filteredSongs = remember(searchQuery, downloadedSongs) {
        if (searchQuery.isEmpty()) downloadedSongs
        else downloadedSongs.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.artist.contains(searchQuery, ignoreCase = true)
            }
    }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchFocusRequester = remember { FocusRequester() }

    val noResultsText = stringResource(R.string.home_search_no_results, searchQuery)
    val searchPlaceholder = stringResource(R.string.download_search_placeholder)

    LaunchedEffect(showSearch) {
        if (showSearch) {
            delay(200)
            searchFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DownloadUiEvent.ShowToast -> {
                    toastMessage = event.message
                    toastTrigger++
                }
            }
        }
    }
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        AnimatedVisibility(
                            visible = !showSearch,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                stringResource(R.string.download_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        if (!showSearch) {
                            IconButton(onClick = { onBack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.action_back)
                                )
                            }
                        }
                    },
                    actions = {
                        if (!showSearch) {
                            IconButton(onClick = { showSearch = true }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.action_search)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    )
                )
                AnimatedVisibility(
                    visible = showSearch,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(searchPlaceholder, color = Color.Gray)},
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null,
                                tint = Color.Gray)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = null)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .focusRequester(searchFocusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        shape = RoundedCornerShape(25.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .pointerInput(showSearch) {
                    detectTapGestures(onTap = {
                        if (showSearch) {
                            showSearch = false
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    })
                }
        ) {
            when {
                downloadedSongs.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(0.3f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.download_empty_title),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = stringResource(R.string.download_empty_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onNavigateToExplore,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = MaterialTheme.shapes.extraLarge,
                            modifier = Modifier
                                .height(56.dp)
                                .fillMaxWidth(0.7f)
                        ) {
                            Text(
                                stringResource(R.string.download_explore_btn),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                searchQuery.isNotEmpty() && filteredSongs.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(0.3f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.download_search_result_title),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Text(
                            text = noResultsText,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = filteredSongs, key = { it.songId }) { downloadedSong ->
                            val uiSong = downloadedSong.toDomain()
                            val state = downloadStates[downloadedSong.songId] ?: DownloadState.Completed
                            val dismissState = rememberSwipeToDismissBoxState()
                            LaunchedEffect(dismissState.currentValue) {
                                if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart)
                                    viewModel.deleteDownloadedSong(downloadedSong)
                            }
                            SwipeToDismissBox(
                                state = dismissState,
                                enableDismissFromStartToEnd = false,
                                enableDismissFromEndToStart = true,
                                backgroundContent = {
                                    val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                        Color.Red.copy(alpha = 0.8f)
                                    } else {
                                        Color.Transparent
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(color)
                                            .padding(horizontal = 20.dp),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = stringResource(R.string.download_delete_song),
                                            tint = Color.White
                                        )
                                    }
                                }
                            ) {
                                SongItem(
                                    song = uiSong,
                                    state = state,
                                    isFavorite = favoriteIds.contains(uiSong.id),
                                    onFavoriteClick = {
                                        libraryViewModel.toggleFavorite(uiSong, favoriteIds.contains(uiSong.id))
                                    },
                                    onDownloadClick = {
                                        viewModel.downloadSong(uiSong)
                                    },
                                    onCancelClick = {
                                        viewModel.cancelDownload(uiSong)
                                    },
                                    onItemClick = {
                                        playerViewModel.playAudio(
                                            song = uiSong,
                                            isOffline = true,
                                            localPath = downloadedSong.filePath
                                        )
                                    }
                                )
                            }
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .alpha(0.5f)
                            )
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
}