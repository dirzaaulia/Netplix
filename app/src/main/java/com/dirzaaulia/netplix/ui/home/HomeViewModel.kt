package com.dirzaaulia.netplix.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.dirzaaulia.netplix.model.Movie
import com.dirzaaulia.netplix.model.Response
import com.dirzaaulia.netplix.model.VideoResponse
import com.dirzaaulia.netplix.paging.TmdbPagingSource
import com.dirzaaulia.netplix.repository.NetworkRepository
import com.dirzaaulia.netplix.utils.ResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val networkRepository: NetworkRepository
): ViewModel() {

    private val _popularState: MutableStateFlow<ResponseResult<Response?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val popularState = _popularState.asStateFlow()

    private val _nowPlayingState: MutableStateFlow<ResponseResult<Response?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val nowPlayingState = _nowPlayingState.asStateFlow()

    private val _movieVideosState: MutableStateFlow<ResponseResult<VideoResponse?>> =
        MutableStateFlow(ResponseResult.Success(null))
    val movieVideosState = _movieVideosState.asStateFlow()


    val searchQuery = MutableStateFlow("")
    @OptIn(ExperimentalCoroutinesApi::class)
    val searchMovieList = searchQuery.flatMapLatest { query ->
        Pager(PagingConfig(pageSize = 20)) {
            TmdbPagingSource(
                repository = networkRepository,
                searchQuery = query
            )
        }.flow.cachedIn(viewModelScope)
    }

    var movieId: Int = -1
    var videoId: String = ""
    var selectedMovie: Movie? = null

    init {
        getPopularMovie()
        getNowPlayingMovie()
    }

    fun resetMovieVideoState() {
        _movieVideosState.value = ResponseResult.Success(null)
    }

    private fun getPopularMovie() {
        networkRepository.getPopularMovie()
            .onEach { result ->
                _popularState.value = result
            }
            .launchIn(viewModelScope)
    }

    private fun getNowPlayingMovie() {
        networkRepository.getNowPlayingMovie()
            .onEach { result ->
                _nowPlayingState.value = result
            }
            .launchIn(viewModelScope)
    }

    fun getMovieVideos(movieId: Int) {
        networkRepository.getMovieVideos(movieId)
            .onEach { result ->
                _movieVideosState.value = result
            }
            .launchIn(viewModelScope)
    }
}