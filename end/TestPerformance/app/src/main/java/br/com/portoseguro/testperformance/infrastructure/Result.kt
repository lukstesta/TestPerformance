package br.com.portoseguro.testperformance.infrastructure

sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    object Error : Result<Nothing>()
}

inline fun <T : Any> Result<T>.onSuccess(action: (T) -> Unit): Result<T> {
    if (this is Result.Success) {
        action(this.value)
    }
    return this
}

inline fun <T : Any> Result<T>.onError(action: (Result.Error) -> Unit): Result<T> {
    if (this is Result.Error) {
        action(this)
    }
    return this
}