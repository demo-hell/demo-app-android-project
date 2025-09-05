package br.com.mobicare.cielo.component.requiredDataField.presentation.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.component.requiredDataField.data.model.request.Order
import br.com.mobicare.cielo.component.requiredDataField.data.model.response.Required
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class UiRequiredDataField(
    val requiredField: Required,
    val order: Order?
) : Parcelable