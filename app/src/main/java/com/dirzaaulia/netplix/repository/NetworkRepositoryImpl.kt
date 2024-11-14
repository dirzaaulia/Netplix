package com.dirzaaulia.netplix.repository

import com.dirzaaulia.netplix.model.Response
import com.dirzaaulia.netplix.model.VideoResponse
import com.dirzaaulia.netplix.network.KtorClient
import com.dirzaaulia.netplix.network.resources.Movie
import com.dirzaaulia.netplix.network.resources.Search
import com.dirzaaulia.netplix.utils.ResponseResult
import com.dirzaaulia.netplix.utils.executeWithData
import com.dirzaaulia.netplix.utils.executeWithResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NetworkRepositoryImpl @Inject constructor(
    private val ktorClient: KtorClient
): NetworkRepository {
    override fun getPopularMovie() = flow {
        emit(ResponseResult.Loading)
        emit(
            executeWithResponse {
                ktorClient.get<Movie.Popular, Response>(Movie.Popular())
            }
        )
    }.flowOn(Dispatchers.IO)

    override fun getNowPlayingMovie() = flow {
        emit(ResponseResult.Loading)
        emit(
            executeWithResponse {
                ktorClient.get<Movie.NowPlaying, Response>(Movie.NowPlaying())
            }
        )
    }.flowOn(Dispatchers.IO)

    override fun getMovieVideos(movieId: Int) = flow {
        emit(ResponseResult.Loading)
        emit(
            executeWithResponse {
                ktorClient.get<Movie.MovieId.Videos, VideoResponse>(
                    Movie.MovieId.Videos(
                        Movie.MovieId(movie_id = movieId)
                    )
                )
            }
        )
    }.flowOn(Dispatchers.IO)

    override suspend fun searchMovie(
        query: String,
        page: Int
    ): ResponseResult<Response> {
        return withContext(Dispatchers.IO) {
            executeWithData {
                ktorClient.getRequestApiWithQuery<Search.Movie, Response>(
                    resources = Search.Movie(),
                    query = mapOf(
                        "query" to query,
                        "page" to page.toString()
                    )
                )
            }
        }
    }
}