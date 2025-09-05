package br.com.mobicare.cielo.component.requiredDataField.presentation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.Keep
import br.com.mobicare.cielo.commons.utils.getParcelableCustom
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField
import br.com.mobicare.cielo.component.requiredDataField.presentation.ui.RequiredDataFieldFragment
import kotlinx.android.parcel.Parcelize

class RequiredDataFieldFlowActivityContract :
    ActivityResultContract<UiRequiredDataField, RequiredDataFieldFlowActivityContract.Result>() {

    override fun createIntent(context: Context, input: UiRequiredDataField): Intent {
        return Intent(context, RequiredDataFieldFlowActivity::class.java).apply {
            putExtra(RequiredDataFieldFragment.REQUIRED_DATA_ARG, input)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result {
        val result = intent?.getParcelableCustom<Result>(REQUIRED_DATA_FIELD_RESULT)

        return if (resultCode == Activity.RESULT_OK && result != null) result else Result.CANCELED
    }

    @Keep
    @Parcelize
    enum class Result : Parcelable {
        CANCELED, SUCCESS
    }

    companion object {
        const val REQUIRED_DATA_FIELD_RESULT = "REQUIRED_DATA_FIELD_RESULT"
    }

}