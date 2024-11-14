package com.dirzaaulia.netplix.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    val id: Int,
    val results: List<Video>
)

@Serializable
data class Video(
    val key: String,
    val site: String,
    val type: String,
)