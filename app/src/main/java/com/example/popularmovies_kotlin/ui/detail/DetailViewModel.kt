package com.example.popularmovies_kotlin.ui.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.popularmovies_kotlin.BuildConfig
import com.example.popularmovies_kotlin.api.MovieApiStatus
import com.example.popularmovies_kotlin.api.MovieServiceApi
import com.example.popularmovies_kotlin.api.models.Movie
import com.example.popularmovies_kotlin.api.models.Trailer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DetailViewModel(movie: Movie, app: Application) : AndroidViewModel(app) {

    // MutableLiveData that stores the selected movie
    private val _selectedMovie = MutableLiveData<Movie>()
    val selectedMovie: LiveData<Movie>
        get() = _selectedMovie

    // The most recent API response
    private val _status = MutableLiveData<MovieApiStatus>()
    val status: LiveData<MovieApiStatus>
        get() = _status

    // A Trailer
    private val _trailers = MutableLiveData<List<Trailer>>()
    val trailers: LiveData<List<Trailer>>
        get() = _trailers

    // ID to get the Trailers and the Reviews.
    private val _movieId = MutableLiveData<Int>()
    val movieId: LiveData<Int>
        get() = _movieId

    private var viewModelJob = Job() // Coroutines Job

    // A coroutine scope for that new job using the main dispatcher
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main )


    init {
        _selectedMovie.value = movie
        _movieId.value = selectedMovie.value?.id
        movieId.value?.let { getTrailers(it) }
    }

    private fun getTrailers(movieId: Int) {

        // Using Coroutines
        coroutineScope.launch {
            var getTrailersDeferred = movieId?.let {
                MovieServiceApi.retrofitService
                    .getTrailers(
                        it, BuildConfig.MOVIE_DATA_BASE_API, "en-us")
            }
            try {
                _status.value = MovieApiStatus.LOADING
                var apiResult = getTrailersDeferred?.await()
                _status.value = MovieApiStatus.DONE
                _trailers.value = apiResult?.results
            } catch (e: Exception) {
                _status.value = MovieApiStatus.ERROR
                _trailers.value = ArrayList()
            }
        }
    }



}