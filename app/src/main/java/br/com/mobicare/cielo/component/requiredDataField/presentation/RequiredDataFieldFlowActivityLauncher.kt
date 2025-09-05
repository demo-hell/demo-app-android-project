package br.com.mobicare.cielo.component.requiredDataField.presentation

import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.component.requiredDataField.presentation.model.UiRequiredDataField

class RequiredDataFieldFlowActivityLauncher(private val activity: FragmentActivity?) {

    private var launcher: ActivityResultLauncher<UiRequiredDataField>? = null

    fun register(callback: (RequiredDataFieldFlowActivityContract.Result) -> Unit): RequiredDataFieldFlowActivityLauncher {
        launcher = activity?.registerForActivityResult(RequiredDataFieldFlowActivityContract()) { result ->
            if (result != null) {
                callback(result)
            } else {
                callback(RequiredDataFieldFlowActivityContract.Result.CANCELED)
            }
        }
        return this
    }

    fun launch(input: UiRequiredDataField) {
        launcher?.launch(input)
    }

}