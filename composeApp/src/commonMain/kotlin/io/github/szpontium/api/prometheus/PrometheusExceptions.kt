package io.github.szpontium.api.prometheus

open class PrometheusException(message: String, cause: Throwable? = null) : Exception(message, cause)

class PrometheusEmailNotConfirmedException(
    message: String = "Twój adres e-mail nie został jeszcze potwierdzony."
) : PrometheusException(message)

class PrometheusConsentException(
    message: String = "Wymagana akceptacja zgody w portalu EduVULCAN."
) : PrometheusException(message)

class PrometheusJwtException(
    message: String
) : PrometheusException(message)
