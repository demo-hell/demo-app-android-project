package br.com.mobicare.cielo.commons.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZonedDateTimeUtilsTest {
    private val dateTimeWithOffsetString = "2023-12-05T10:08:30.000-03:00"
    private val dateTimeWithUtc = "2023-12-05T10:08:30.000Z"

    private val zoneIdForSaoPaulo = ZoneId.of("America/Sao_Paulo") // -03:00
    private val zoneIdForManaus = ZoneId.of("America/Manaus") // -04:00
    private val zoneIdForNoronha = ZoneId.of("America/Noronha") // -02:00
    private val zoneIdForAcre = ZoneId.of("Brazil/Acre") // -05:00
    private val zoneIdForTokyo = ZoneId.of("Asia/Tokyo") // +09:00

    private val expectedOffsetDateTime =
        OffsetDateTime.of(
            2023,
            12,
            5,
            10,
            8,
            30,
            0,
            ZoneOffset.of("-03:00"),
        )

    private val expectedZonedDateTime =
        ZonedDateTime.of(
            2023,
            12,
            5,
            10,
            8,
            30,
            0,
            zoneIdForSaoPaulo,
        )

    @Test
    fun `it should parse string datetime with offset to OffsetDateTime object`() {
        val result = dateTimeWithOffsetString.parseToOffsetDateTime()

        assertThat(result.toString()).isEqualTo(expectedOffsetDateTime.toString())
    }

    @Test
    fun `it should parse OffsetDateTime to ZonedDateTime correctly`() {
        val result =
            dateTimeWithOffsetString
                .parseToOffsetDateTime()
                ?.parseToZonedDateTime(zoneIdForSaoPaulo)

        assertThat(result.toString()).isEqualTo(expectedZonedDateTime.toString())
    }

    @Test
    fun `it should parse ZonedDateTime to the correct formatted datetime and timezone`() {
        val dateTimeInSaoPaulo =
            dateTimeWithOffsetString
                .parseFromOffsetToZonedDateTime(zoneIdForSaoPaulo)
                ?.parseToString()

        val dateTimeInManaus =
            dateTimeWithOffsetString
                .parseFromOffsetToZonedDateTime(zoneIdForManaus)
                ?.parseToString()

        val dateTimeInNoronha =
            dateTimeWithOffsetString
                .parseFromOffsetToZonedDateTime(zoneIdForNoronha)
                ?.parseToString()

        val dateTimeInAcre =
            dateTimeWithOffsetString
                .parseFromOffsetToZonedDateTime(zoneIdForAcre)
                ?.parseToString()

        assertThat(dateTimeInSaoPaulo).isEqualTo("05/12/2023 10:08:30")
        assertThat(dateTimeInManaus).isEqualTo("05/12/2023 09:08:30")
        assertThat(dateTimeInNoronha).isEqualTo("05/12/2023 11:08:30")
        assertThat(dateTimeInAcre).isEqualTo("05/12/2023 08:08:30")
    }

    @Test
    fun `it should parse string to ZonedDateTime correctly`() {
        val result = dateTimeWithUtc.parseToZonedDateTime(zoneId = zoneIdForSaoPaulo)

        assertThat(result.toString()).isEqualTo(expectedZonedDateTime.toString())
    }

    @Test
    fun `isToday should return true when ZonedDateTime is today`() {
        val zonedDateTime = ZonedDateTime.now()

        val result = zonedDateTime.isToday()

        assertThat(result).isTrue()
    }

    @Test
    fun `isToday should return false when ZonedDateTime is not today`() {
        val zonedDateTime = ZonedDateTime.now().minusDays(1)

        val result = zonedDateTime.isToday()

        assertThat(result).isFalse()
    }

    @Test
    fun `isToday should return true when ZonedDateTime is today in different timezone`() {
        val zonedDateTime = ZonedDateTime.now(zoneIdForTokyo)

        val result = zonedDateTime.isToday()

        assertThat(result).isTrue()
    }

    @Test
    fun `isToday should return false when ZonedDateTime is tomorrow in different timezone`() {
        val zonedDateTime = ZonedDateTime.now(zoneIdForTokyo).plusDays(1)

        val result = zonedDateTime.isToday()

        assertThat(result).isFalse()
    }

    @Test
    fun `toStringDayAndMonthOrTimeOfDay should return time when ZonedDateTime is today`() {
        val zonedDateTime = ZonedDateTime.now()

        val result = zonedDateTime.toStringDayAndMonthOrTimeOfDay()

        assertThat(result).isEqualTo(DateTimeFormatter.ofPattern("HH:mm").format(zonedDateTime))
    }

    @Test
    fun `toStringDayAndMonthOrTimeOfDay should return date when ZonedDateTime is not today`() {
        val zonedDateTime = ZonedDateTime.now().minusDays(1)

        val result = zonedDateTime.toStringDayAndMonthOrTimeOfDay()

        assertThat(result).isEqualTo(DateTimeFormatter.ofPattern("dd/MM").format(zonedDateTime))
    }

    @Test
    fun `toStringDayAndMonthOrTimeOfDay should return empty string when ZonedDateTime is null`() {
        val zonedDateTime: ZonedDateTime? = null

        val result = zonedDateTime.toStringDayAndMonthOrTimeOfDay()

        assertThat(result).isEmpty()
    }
}
