package com.example.popularmovies_kotlin.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.popularmovies_kotlin.MovieApiStatus
import com.example.popularmovies_kotlin.api.MovieRepo
import com.example.popularmovies_kotlin.api.models.Movie
import com.example.popularmovies_kotlin.api.models.Trailer
import com.example.popularmovies_kotlin.ui.detail.DetailViewState.*
import com.example.popularmovies_kotlin.ui.home.HomeViewState
import com.example.popularmovies_kotlin.ui.home.MovieApiFilter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailViewModel @Inject constructor(private val movieRepo: MovieRepo) : ViewModel() {

    fun onViewInit(movie: Movie) {
        _selectedMovie.value = movie
        _movieId.value = movie.id
        movieId.value?.let { getTrailers(it) }
    }

    // MutableLiveData that stores the selected movie
    private val _selectedMovie = MutableLiveData<Movie>()
    val selectedMovie: LiveData<Movie>
        get() = _selectedMovie

    // View State
    private val _viewState = MutableLiveData<DetailViewState>()
    val viewState: LiveData<DetailViewState>
        get() = _viewState

    // ID to get the Trailers and the Reviews.
    private val _movieId = MutableLiveData<Int>()
    val movieId: LiveData<Int>
        get() = _movieId

    private var viewModelJob = Job() // Coroutines Job

    // A coroutine scope for that new job using the main dispatcher
    private val coroutineScope = CoroutineScope(
        viewModelJob + Dispatchers.Main )

    private fun getTrailers(id: Int) {
        _viewState.value = Loading
        add(
            movieRepo.getTrailers(id)
                .subscribe(
                    {
                        _viewState.value = Presenting(it.results)
                    }, {
                        _viewState.value = Error
                    }
                ))
    }

    val compositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    protected fun add(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}