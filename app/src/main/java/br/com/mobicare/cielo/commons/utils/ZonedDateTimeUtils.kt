package br.com.mobicare.cielo.commons.utils

import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.utils.crashlytics.logFirebaseCrashlytics
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun String.parseToOffsetDateTime(): OffsetDateTime? =
    try {
        OffsetDateTime.parse(this)
    } catch (e: Exception) {
        e.message.logFirebaseCrashlytics()
        null
    }

fun OffsetDateTime.parseToZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime? =
    try {
        atZoneSameInstant(zoneId)
    } catch (e: Exception) {
        e.message.logFirebaseCrashlytics()
        null
    }

fun String.parseFromOffsetToZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime? =
    parseToOffsetDateTime()?.parseToZonedDateTime(zoneId)

fun ZonedDateTime.parseToString(pattern: String = SIMPLE_DATE_FORMAT_DATE_TIME_FULL) =
    try {
        DateTimeFormatter.ofPattern(pattern).format(this)
    } catch (e: Exception) {
        e.message.logFirebaseCrashlytics()
        null
    }

fun String.parseToZonedDateTime(
    pattern: String = LONG_TIME,
    zoneOffset: ZoneOffset = ZoneOffset.ofHours(-THREE),
    zoneId: ZoneId = ZoneId.systemDefault(),
): ZonedDateTime? =
    try {
        ZonedDateTime
            .parse(this, DateTimeFormatter.ofPattern(pattern).withZone(zoneOffset))
            .withZoneSameInstant(zoneId)
    } catch (e: Exception) {
        e.message.logFirebaseCrashlytics()
        null
    }

fun ZonedDateTime.isToday(): Boolean = this.toLocalDate() == LocalDate.now(this.zone)

fun ZonedDateTime?.toStringDayAndMonthOrTimeOfDay(): String =
    this
        ?.let { date ->
            date.parseToString(
                if (date.isToday()) {
                    SIMPLE_HOUR_MINUTE_24h
                } else {
                    SIMPLE_DT_FORMAT_MASK_DAY_MONTH
                },
            )
        }.orEmpty()
