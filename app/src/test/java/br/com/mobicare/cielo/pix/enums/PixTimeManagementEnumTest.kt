package br.com.mobicare.cielo.pix.enums

import org.junit.Assert.assertEquals
import org.junit.Test

class PixTimeManagementEnumTest {

    @Test
    fun `it should parse the time string to the right hour when calling findByTime`() {
        val hourTwenty = PixTimeManagementEnum.findByTime("20:00:00")
        val hourTwentyTwo = PixTimeManagementEnum.findByTime("22:00:00")

        assertEquals(20, hourTwenty.hour)
        assertEquals(22, hourTwentyTwo.hour)
    }

    @Test
    fun `it should parse the bad formatted or out of range time string to the default 10pm hour when calling findByTime`() {
        val badFormattedTime = PixTimeManagementEnum.findByTime("20:0:")
        val outOfRangeTime = PixTimeManagementEnum.findByTime("15:00:00")

        assertEquals(22, badFormattedTime.hour)
        assertEquals(22, outOfRangeTime.hour)
    }

    @Test
    fun `it should parse the time string to the right display hour when calling findByTime`() {
        val hourTwenty = PixTimeManagementEnum.findByTime("20:00:00")
        val hourTwentyTwo = PixTimeManagementEnum.findByTime("22:00:00")

        assertEquals("20h", hourTwenty.displayHour)
        assertEquals("22h", hourTwentyTwo.displayHour)
    }

    @Test
    fun `it should parse the bad formatted or out of range time string to the default 10pm display hour when calling findByTime`() {
        val badFormattedTime = PixTimeManagementEnum.findByTime("20:0:")
        val outOfRangeTime = PixTimeManagementEnum.findByTime("15:00:00")

        assertEquals("22h", badFormattedTime.displayHour)
        assertEquals("22h", outOfRangeTime.displayHour)
    }

}