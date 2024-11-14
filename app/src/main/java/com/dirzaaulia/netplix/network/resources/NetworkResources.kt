package com.dirzaaulia.netplix.network.resources

import io.ktor.resources.Resource

@Resource("/movie")
class Movie {
    @Resource("/popular")
    class Popular(val parent: Movie = Movie())
    @Resource("/now_playing")
    class NowPlaying(val parent: Movie = Movie())
    @Resource("/{movie_id}")
    class MovieId(val parent: Movie = Movie(), val movie_id: Int) {
        @Resource("/videos")
        class Videos(val parent: MovieId)
    }
}

@Resource("/search")
class Search {
    @Resource("/movie")
    class Movie(val parent: Search = Search())
}