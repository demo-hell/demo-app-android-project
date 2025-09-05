package br.com.mobicare.cielo.commons.utils

import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.biometricNotification.ui.BiometricNotificationBottomSheetFragment
import com.google.crypto.tink.Aead
import com.google.crypto.tink.Config
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.config.TinkConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager


fun FragmentActivity.createAead(key: String, masterKeyUri: String): Aead {

    Config.register(TinkConfig.LATEST)

    val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(this, BiometricNotificationBottomSheetFragment.KEY_NAME, null)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(BiometricNotificationBottomSheetFragment.MASTER_KEY_URI)
            .build().keysetHandle

    return keysetHandle.getPrimitive(Aead::class.java)
}