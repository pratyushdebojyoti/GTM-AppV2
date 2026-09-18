package com.gotechmedia.app.presentation.base

/**
 * Standard generic asynchronous UI State for MVVM screens.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data object Empty : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
