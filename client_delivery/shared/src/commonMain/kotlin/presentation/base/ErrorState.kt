package presentation.base

sealed interface ErrorState {
    object WifiDisabled : ErrorState
    object NoInternet : ErrorState
    object NetworkNotSupported : ErrorState
    object UnAuthorized : ErrorState
    object RequestFailed : ErrorState
    object InvalidUsername : ErrorState
    object InvalidPassword : ErrorState
    data class UserAlreadyExists(val message: String) : ErrorState
    data class UserNotFound(val message: String) : ErrorState
    data class WrongPassword(val message: String) : ErrorState
}