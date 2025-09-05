package br.com.mobicare.cielo.component.feeandplans.model

import android.os.Parcelable
import androidx.annotation.Keep
import br.com.mobicare.cielo.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class ComponentLayoutFeeAndPlansItem(var labelTitle: String? = "",
                                          var labelValue: String? = "",
                                          var labelSubTitle: String? = null,
                                          var labelValueColor: Int = R.color.black_5A646E) : Parcelable