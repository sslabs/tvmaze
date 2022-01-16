package org.sslabs.tvmaze.util

class ErrorHandling {

    companion object {

        const val UNABLE_TO_RESOLVE_HOST = "Unable to resolve host"
        const val INVALID_PAGE = "Invalid page"
        const val UNKNOWN_ERROR = "Unknown error"

        fun isNetworkError(msg: String): Boolean {
            return when {
                msg.contains(UNABLE_TO_RESOLVE_HOST) -> true
                else -> false
            }
        }

        fun isPaginationDone(errorResponse: String?): Boolean {
            // if error response = '{"detail":"Invalid page."}' then pagination is finished
            return errorResponse?.contains(INVALID_PAGE) ?: false
        }
    }
}
