package com.dirzaaulia.netplix.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dirzaaulia.netplix.model.Movie
import com.dirzaaulia.netplix.repository.NetworkRepository
import com.dirzaaulia.netplix.utils.pagingSucceeded

class TmdbPagingSource(
    private val repository: NetworkRepository,
    private val searchQuery: String,
): PagingSource<Int, Movie>() {

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return repository.searchMovie(searchQuery, page).pagingSucceeded { data ->
            loadResult(data = data.results, page = page)
        }
    }

    private fun loadResult(data: List<Movie>, page: Int) = LoadResult.Page(
        data = data,
        prevKey = if (page == 1) null else page.minus(1),
        nextKey = if (data.isEmpty()) null else page.plus(1)
    )
}