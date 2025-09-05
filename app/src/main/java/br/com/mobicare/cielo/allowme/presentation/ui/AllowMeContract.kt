package br.com.mobicare.cielo.allowme.presentation.ui

import android.app.Activity
import android.content.Context
import androidx.fragment.app.FragmentManager
import br.com.allowme.android.contextual.AllowMeContextual
import br.com.mobicare.cielo.commons.ui.IAttached

interface AllowMeContract {
    interface View : IAttached {
        fun successCollectToken(result: String)
        fun errorCollectToken(result: String?, errorMessage: String, mandatory: Boolean)
        fun getSupportFragmentManagerInstance(): FragmentManager
        fun stopAction() {}
    }

    interface Presenter {
        fun init(context: Context): AllowMeContextual
        fun collect(
            mAllowMeContextual: AllowMeContextual,
            context: Activity,
            mandatory: Boolean,
            hasAnimation: Boolean = false,
            askLocalizationPermission: Boolean = true
        )

        fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray,
            context: Activity
        )

        fun permissionValidate(
            activity: Activity,
            permission: String,
            permissionValue: Int,
            mandatory: Boolean
        ): Boolean

        fun onCreateBottomSheetPermission(
            permissionValue: Int,
            permission: String,
            context: Activity,
            mandatory: Boolean
        )
    }
}
