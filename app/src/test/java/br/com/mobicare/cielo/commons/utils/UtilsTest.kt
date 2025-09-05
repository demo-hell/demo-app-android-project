package br.com.mobicare.cielo.commons.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.nfc.NfcManager
import androidx.core.content.ContextCompat
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class UtilsTest {
    @Mock
    private lateinit var fragmentActivity: Activity

    @Mock
    private lateinit var context: Context

    private lateinit var nfcManager: NfcManager

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        nfcManager = mockk(relaxed = true)
    }

    @Test
    fun `Check device has nfc when device has nfc`() {
        `when`(fragmentActivity.getSystemService(Context.NFC_SERVICE)).thenReturn(nfcManager)

        val deviceHasNFC = fragmentActivity.deviceHasNFC()

        assertEquals(true, deviceHasNFC)
    }

    @Test
    fun `Check device has nfc when device doesn't have nfc`() {
        `when`(fragmentActivity.getSystemService(Context.NFC_SERVICE)).thenReturn(null)

        val deviceHasNFC = fragmentActivity.deviceHasNFC()

        assertEquals(false, deviceHasNFC)
    }

    @Test
    fun `Check sequential digits when call hasRepeatedDigitsOrDozens`() {
        val hasSequential = hasRepeatedDigitsOrDozens(UtilsFactory.sequentialDigits)

        assertEquals(true, hasSequential)
    }

    @Test
    fun `Check repeated dozens when call hasRepeatedDigitsOrDozens`() {
        val hasRepeated = hasRepeatedDigitsOrDozens(UtilsFactory.repeatedDozens)

        assertEquals(true, hasRepeated)
    }

    @Test
    fun `Call hasRepeatedDigitsOrDozens when not repeated values`() {
        val hasRepeated = hasRepeatedDigitsOrDozens(UtilsFactory.repeatedNot)

        assertEquals(false, hasRepeated)
    }

    @Test
    fun `Check has sequential dozens when call hasDozensSequential`() {
        val hasRepeated = hasDozensSequential(UtilsFactory.sequentialDozens)

        assertEquals(true, hasRepeated)
    }

    @Test
    fun `Call hasDozensSequential when not sequential values`() {
        val hasSequential = hasDozensSequential(UtilsFactory.sequentialDozensNot)

        assertEquals(false, hasSequential)
    }

    @Test
    fun `Check has mirorred digits when call hasMirroredDigits`() {
        val hasMirrored = hasMirroredDigits(UtilsFactory.mirroredDigits)

        assertEquals(true, hasMirrored)
    }

    @Test
    fun `Call hasMirroredDigits when not mirrored values`() {
        val hasMirrored = hasMirroredDigits(UtilsFactory.mirroredDigitsNot)

        assertEquals(false, hasMirrored)
    }

    @Test
    fun `Check has drawing keyboard digits when call isDrawingKeyboard`() {
        val hasDrawing = isDrawingKeyboard(UtilsFactory.drawingDigits)

        assertEquals(true, hasDrawing)
    }

    @Test
    fun `Call isDrawingKeyboard when not drawing keyboard values`() {
        val hasDrawing = isDrawingKeyboard(UtilsFactory.drawingDigitsNot)

        assertEquals(false, hasDrawing)
    }

    @Test
    fun `Camera is enabled when permission is granted`() {
        `when`(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)).thenReturn(PackageManager.PERMISSION_GRANTED)

        val isCameraEnabled = cameraIsEnabled(context)

        assertEquals(true, isCameraEnabled)
    }

    @Test
    fun `Camera is not enabled when permission is denied`() {
        `when`(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)).thenReturn(PackageManager.PERMISSION_DENIED)

        val isCameraEnabled = cameraIsEnabled(context)

        assertEquals(false, isCameraEnabled)
    }
}
