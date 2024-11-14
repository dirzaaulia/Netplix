package com.dirzaaulia.netplix.repository

import com.dirzaaulia.netplix.model.Movie
import com.dirzaaulia.netplix.model.Response
import com.dirzaaulia.netplix.model.VideoResponse
import com.dirzaaulia.netplix.utils.ResponseResult
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getPopularMovie(): Flow<ResponseResult<Response>>
    fun getNowPlayingMovie(): Flow<ResponseResult<Response>>
    fun getMovieVideos(movieId: Int): Flow<ResponseResult<VideoResponse>>
    suspend fun searchMovie(query: String, page: Int): ResponseResult<Response>
}