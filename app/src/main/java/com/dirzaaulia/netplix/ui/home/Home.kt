package com.dirzaaulia.netplix.ui.home

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import com.dirzaaulia.netplix.common.CommonLoading
import com.dirzaaulia.netplix.common.ErrorContent
import com.dirzaaulia.netplix.ui.detail.DialogDetail
import com.dirzaaulia.netplix.utils.ResponseResult
import com.dirzaaulia.netplix.utils.success
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isSearchActive = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menu", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text(text = "Home") },
                    selected = false,
                    onClick = { }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.systemBarsPadding(),
            topBar = {
                when (isSearchActive.value) {
                    true -> {
                        TopBarSearch(
                            viewModel = viewModel,
                            updateSearchActive = {
                                isSearchActive.value = isSearchActive.value.not()
                            },
                        ) {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        }
                    }
                    false -> {
                        TopBarNonSearch(
                            updateSearchActive = {
                                isSearchActive.value = isSearchActive.value.not()
                            },
                            updateDrawerState = {
                                scope.launch {
                                    drawerState.apply {
                                        if (isClosed) open() else close()
                                    }
                                }
                            }
                        )
                    }
                }

            }
        ) { innerPadding ->
            when (isSearchActive.value) {
                true -> {
                    SearchContent(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
                false -> {
                    HomeContent(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNonSearch(
    updateSearchActive: () -> Unit,
    updateDrawerState: () -> Unit
) {
    TopAppBar(
        title = {
            Text("Netplix")
        },
        actions = {
            IconButton(onClick = {
                updateSearchActive.invoke()
            }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                updateDrawerState.invoke()
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSearch(
    viewModel: HomeViewModel,
    updateSearchActive: () -> Unit,
    updateDrawerState: () -> Unit
) {

    val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle()

    TopAppBar(
        title = {
            OutlinedTextField(
                modifier = Modifier.padding(top = 8.dp),
                value = searchQuery.value,
                onValueChange = { viewModel.searchQuery.value = it },
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(onClick = {
                        updateSearchActive.invoke()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                updateDrawerState.invoke()
            }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = null
                )
            }
        }
    )

}

@Composable
fun SearchContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {

    val searchState = viewModel.searchMovieList.collectAsLazyPagingItems()

    when (searchState.loadState.refresh) {
        LoadState.Loading -> { CommonLoading() }
        is LoadState.NotLoading -> {
            LazyVerticalGrid(
                modifier = modifier,
                columns = GridCells.Fixed(2)
            ){
                items(
                    count = searchState.itemCount,
                    key = searchState.itemKey { it.id }
                ) { index ->
                    searchState[index]?.let {
                        Card(modifier = Modifier.padding(8.dp)) {
                            AsyncImage(
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth(),
                                model = "https://image.tmdb.org/t/p/w342/${it.posterPath}",
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds
                            )
                        }
                    }
                }
            }
        }
        is LoadState.Error -> {
            val errorMessage = (searchState.loadState.refresh as LoadState.Error).error.message
            ErrorContent(
                text = errorMessage.orEmpty().ifEmpty { "Something went wrong" },
            ) { searchState.retry() }
        }
    }


}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel
) {

    val popularState = viewModel.popularState.collectAsStateWithLifecycle()
    val nowPlayingState = viewModel.nowPlayingState.collectAsStateWithLifecycle()
    val movieVideosState = viewModel.movieVideosState.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val dialogOpen = remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        when (val state = popularState.value) {
            ResponseResult.Loading -> {}
            is ResponseResult.Success -> {
                state.success { response ->
                    val list = response?.results?.subList(0, 10)
                    AutoSlidingCarousel(
                        itemsCount = 10
                    ) { index ->
                        val posterPath = list?.get(index)?.posterPath
                        val imagePath = "https://image.tmdb.org/t/p/w780/${posterPath}"
                        Card(
                            modifier = Modifier.padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                        ) {
                            AsyncImage(
                                model = imagePath,
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }

            is ResponseResult.Error -> {}
        }

        when (val state = nowPlayingState.value) {
            ResponseResult.Loading -> { CommonLoading() }
            is ResponseResult.Success -> {
                state.success { response ->
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = "Now Playing",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    LazyRow {
                        items(10) { index ->
                            val posterPath = response?.results?.get(index)?.posterPath
                            val movieId = response?.results?.get(index)?.id
                            Card(
                                modifier = Modifier.padding(8.dp),
                                shape = RoundedCornerShape(16.dp),
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.movieId = movieId ?: -1
                                        viewModel.selectedMovie = response?.results?.get(index)
                                        viewModel.getMovieVideos(movieId ?: -1)
                                    }
                                }
                            ) {
                                AsyncImage(
                                    modifier = Modifier.size(height = 200.dp, width = 150.dp),
                                    model = "https://image.tmdb.org/t/p/w342/${posterPath}",
                                    contentDescription = null,
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                    }
                }
            }

            is ResponseResult.Error -> {}
        }

        when (val state = movieVideosState.value) {
            ResponseResult.Loading -> {
                CommonLoading()
            }
            is ResponseResult.Success -> {
                state.success { response ->
                    val videoId = response?.results?.find {
                        it.type.equals("Final Trailer", ignoreCase = true)
                    }?.key ?: response?.results?.find {
                        it.type.equals("Trailer", ignoreCase = true)
                    }?.key
                    viewModel.videoId = videoId.toString()
                    dialogOpen.value = true
                }
            }
            is ResponseResult.Error -> {
                val errorMessage = (movieVideosState.value as ResponseResult.Error).throwable.message
                ErrorContent(text = errorMessage.orEmpty()) {
                    viewModel.getMovieVideos(viewModel.movieId)
                }
            }
        }
    }

    when (dialogOpen.value) {
        true -> {
            DialogDetail(
                onDismissRequest = {
                    coroutineScope.launch {
                        viewModel.resetMovieVideoState()
                        dialogOpen.value = false
                    }
                },
                onConfirmation = { dialogOpen.value = false },
                videoId = viewModel.videoId,
                data = viewModel.selectedMovie
            )
        }
        false -> { }
    }
}

@Composable
fun AutoSlidingCarousel(
    modifier: Modifier = Modifier,
    autoSlideDuration: Long = 2000L,
    itemsCount: Int,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val pagerState = rememberPagerState { itemsCount }
    val isDragged = pagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(autoSlideDuration)
            tween<Float>(600)
            if (pagerState.pageCount != 0) {
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % (pagerState.pageCount)
                )
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier.fillMaxWidth(),
        ) {
            HorizontalPager(
                modifier = Modifier.fillMaxWidth(),
                state = pagerState
            ) { page ->
                itemContent(page)
            }

            // you can remove the surface in case you don't want
            // the transparant bacground
        }
        Surface(
            modifier = Modifier
                .padding(bottom = 8.dp),
            shape = CircleShape,
            color = Color.Black.copy(alpha = 0.5f)
        ) {
            DotsIndicator(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                totalDots = itemsCount,
                selectedIndex = if (isDragged.value) pagerState.currentPage else pagerState.targetPage,
                dotSize = 8.dp
            )
        }
    }
}

@Composable
fun DotsIndicator(
    modifier: Modifier = Modifier,
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = MaterialTheme.colorScheme.onTertiary /* Color.Yellow */,
    unSelectedColor: Color = MaterialTheme.colorScheme.onSurfaceVariant /* Color.Gray */,
    dotSize: Dp
) {
    LazyRow(
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
    ) {
        items(totalDots) { index ->
            IndicatorDot(
                color = if (index == selectedIndex) selectedColor else unSelectedColor,
                size = dotSize
            )

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun IndicatorDot(
    modifier: Modifier = Modifier,
    size: Dp,
    color: Color
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}