package br.com.mobicare.cielo.commons.utils.fingerprint

import android.annotation.SuppressLint
import android.content.Context
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences


class FingerPrintChanged {
    private var mContext: Context? = null

    @SuppressLint("NewApi")
    fun hasNewFingerPrint(context: Context?): Boolean {
        mContext = context
        return newFingerprintAdded(context)
    }

    private fun removeAccountFingerPrint() {
        UserPreferences.getInstance().cleanFingerprintData()
    }

    private fun newFingerprintAdded(context: Context?): Boolean {
        val setFingerPrintDeviceIds: Set<Int?> = context?.let { getFingerPrintUniqueID(it) }!!
        if (isSequentialID(setFingerPrintDeviceIds.toList() as List<Int>)) {
            removeAccountFingerPrint()
            return false
        } else {
            val setFingerPrintSaved: Set<Int>? = UserPreferences.getInstance().getFingerPrintIds()
            val setFingerPrintDesconhecidos: MutableSet<Int?> = HashSet(setFingerPrintDeviceIds)
            setFingerPrintSaved?.let { setFingerPrintDesconhecidos.removeAll(it) }
            updateStoredFingerPrintIds(setFingerPrintDeviceIds)

            if (setFingerPrintDesconhecidos.size > ZERO || setFingerPrintDeviceIds.size != setFingerPrintSaved?.size) {
                removeAccountFingerPrint()
                return true
            }
            return false
        }
    }

    private fun updateStoredFingerPrintIds(setFingerPrintDeviceIds: Set<Int?>) {
        val setFingerPrintSalvos: Set<Int>? =
            UserPreferences.getInstance().getFingerPrintIds()

        val setFingerPrintSalvosRemovidos: MutableSet<Int?> = HashSet(setFingerPrintSalvos)
        setFingerPrintSalvosRemovidos.removeAll(setFingerPrintDeviceIds)
        val setFingerPrintSalvosFinal: MutableSet<Int?> = HashSet(setFingerPrintSalvos)

        if (setFingerPrintSalvosRemovidos.size > ZERO) {
            setFingerPrintSalvosFinal.removeAll(setFingerPrintSalvosRemovidos)
            UserPreferences.getInstance().setFingerprintIds(setFingerPrintSalvosFinal)
        }

        if (setFingerPrintSalvosFinal.size == ZERO) {
            UserPreferences.getInstance().setFingerprintIds(setFingerPrintDeviceIds)
        }
    }

}