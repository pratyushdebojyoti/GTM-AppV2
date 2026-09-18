package com.gotechmedia.app.core.utils

/**
 * Generic class that encapsulates data along with its loading status and errors.
 * Used across the Domain and Presentation boundary for predictable asynchronous state.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : Resource<Nothing>()
    data object Loading : Resource<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isLoading: Boolean get() = this is Loading
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = (this as? Success)?.data

    inline fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(message, throwable)
            is Loading -> Loading
        }
    }

    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (String, Throwable?) -> Unit): Resource<T> {
        if (this is Error) action(message, throwable)
        return this
    }
}
