package br.com.mobicare.cielo.idOnboarding.model

data class IDOnboardingWhitelistResponse(
    val businessMessage: BusinessMessage? = null,
    val cause: Cause? = null,
    val httpcode: Int? = null,
    val localizedMessage: String? = null,
    val message: String? = null,
    val messageAsJson: String? = null,
    val stackTrace: List<StackTraceX>? = null,
    val suppressed: List<Suppressed>? = null
)

data class BusinessMessage(
    val errorCode: String?,
    val errorMessage: String?
)

data class Cause(
    val cause: String?,
    val localizedMessage: String?,
    val message: String?,
    val stackTrace: List<StackTrace>?,
    val suppressed: List<String>?
)

data class StackTraceX(
    val className: String?,
    val fileName: String?,
    val lineNumber: Int?,
    val methodName: String?,
    val nativeMethod: Boolean?
)

data class Suppressed(
    val cause: String?,
    val localizedMessage: String?,
    val message: String?,
    val stackTrace: List<StackTraceXX>?,
    val suppressed: List<String>?
)

data class StackTrace(
    val className: String?,
    val fileName: String?,
    val lineNumber: Int?,
    val methodName: String?,
    val nativeMethod: Boolean?
)

data class StackTraceXX(
    val className: String?,
    val fileName: String?,
    val lineNumber: Int?,
    val methodName: String?,
    val nativeMethod: Boolean?
)