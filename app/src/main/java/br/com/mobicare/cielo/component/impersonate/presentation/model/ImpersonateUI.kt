package br.com.mobicare.cielo.component.impersonate.presentation.model

import android.os.Parcelable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.component.impersonate.data.model.response.MerchantResponse
import br.com.mobicare.cielo.component.impersonate.utils.TypeImpersonateEnum
import kotlinx.android.parcel.Parcelize
import org.androidannotations.annotations.res.StringRes

@Parcelize
data class ImpersonateUI(
    @StringRes val title: Int = R.string.impersonate_title_default,
    val typeImpersonate: TypeImpersonateEnum = TypeImpersonateEnum.HIERARCHY,
    val subTitle: String,
    val merchants: List<MerchantResponse>,
    var flowOpenFinance: Boolean = false,
) : Parcelable
