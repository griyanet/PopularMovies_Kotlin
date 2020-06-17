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
    private val _apiStatus = MutableLiveData<MovieApiStatus>()
    val apiStatus: LiveData<MovieApiStatus>
        get() = _apiStatus

    // ID to get the Trailers and the Reviews.
    private val _movieId = MutableLiveData<Int>()
    val movieId: LiveData<Int>
        get() = _movieId

    // A Trailer
    private val _trailers = MutableLiveData<List<Trailer>>()
    val trailers: LiveData<List<Trailer>>
        get() = _trailers

    private var viewModelJob = Job() // Coroutines Job

    // A coroutine scope for that new job using the main dispatcher
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main )


    init {
        _selectedMovie.value = movie
        _movieId.value = movie.id
        movieId.value?.let { getTrailers(it) }
    }

    private fun getTrailers(id: Int) {

        // Using Coroutines
        coroutineScope.launch {
            var getTrailersDeferred = id.let {
                MovieServiceApi.retrofitService
                    .getTrailers(
                        it, BuildConfig.MOVIE_DATA_BASE_API, "en-us")
            }
            try {
                _apiStatus.value = MovieApiStatus.LOADING
                Log.d("TAG", "MovieApiStatus LOADING TRAILERS VM")
                var apiResultTrailer = getTrailersDeferred.await()
                _apiStatus.value = MovieApiStatus.DONE
                Log.d("TAG", "MovieApiStatus DONE TRAILERS VM")
                _trailers.value = apiResultTrailer.results
            } catch (e: Exception) {
                _apiStatus.value = MovieApiStatus.ERROR
                _trailers.value = ArrayList()
            }
        }
    }



    // Cancel the Coroutines Job
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}