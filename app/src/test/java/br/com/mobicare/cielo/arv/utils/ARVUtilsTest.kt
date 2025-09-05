package br.com.mobicare.cielo.arv.utils
import br.com.mobicare.cielo.commons.utils.DataCustomNew
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.util.Calendar

class ARVUtilsTest {

    @Test
    fun `minAnticipationRangeDate should be initialized correctly`() {
        val expectedDate = DataCustomNew()
        assertEquals(expectedDate.formatBRDate(), ARVUtils.minAnticipationRangeDate.formatBRDate())
    }

    @Test
    fun `maxAnticipationRangeDate should be two years from now`() {
        val calendar = Calendar.getInstance().apply { add(Calendar.YEAR, 2) }
        val expectedDate = DataCustomNew().apply { setDate(calendar.time) }
        assertEquals(expectedDate.formatBRDate(), ARVUtils.maxAnticipationRangeDate.formatBRDate())
    }

    @Test
    fun `maxAnticipationRangeDate should not be null`() {
        assertNotNull(ARVUtils.maxAnticipationRangeDate)
    }

    @Test
    fun `minAnticipationRangeDate should not be null`() {
        assertNotNull(ARVUtils.minAnticipationRangeDate)
    }
}