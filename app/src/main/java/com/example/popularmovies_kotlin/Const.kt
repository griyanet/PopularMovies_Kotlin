package com.example.popularmovies_kotlin

object Const {

    private const val BASE_IMAGE_URL = "http://image.tmdb.org/t/p/"

    private const val IMAGE_SIZE_W342 = "w342"
    private const val IMAGE_SIZE_W185 = "w185"
    private const val IMAGE_SIZE_W154 = "w154"

    const val BASE_IMAGE_LARGE = BASE_IMAGE_URL + IMAGE_SIZE_W342
    const val BASE_IMAGE_MEDIUM = BASE_IMAGE_URL + IMAGE_SIZE_W185
    const val BASE_IMAGE_SMALL = BASE_IMAGE_URL + IMAGE_SIZE_W154
}