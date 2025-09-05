package br.com.mobicare.cielo.idOnboarding.updateUser.homeCard

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnLayout
import br.com.mobicare.cielo.databinding.CardIdOnboardingHomeStatusBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.idOnboarding.updateUser.homeCard.IDOnboardingHomeCardStatusEnum.NONE

class IDOnboardingHomeCardStatusView(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var binding: CardIdOnboardingHomeStatusBinding? = null
    var mainRole: String? = null

    var status: IDOnboardingHomeCardStatusEnum = NONE
        set(value) {
            field = value

            doOnLayout {
                status.mainRole = mainRole
                binding?.apply {
                    ivOnboardingStatusImage.setImageResource(status.image)
                    tvOnboardingStatusTitle.text = context.getString(status.title)
                    tvOnboardingStatusTitle.setTextColor(
                        ContextCompat.getColor(context, status.titleColor)
                    )
                    tvOnboardingStatusMessage.text = context.getString(status.message)

                    ivChevron.visible(status != IDOnboardingHomeCardStatusEnum.DATA_ANALYSIS)
                }
            }
            requestLayout()
        }

    init {
        binding = CardIdOnboardingHomeStatusBinding.inflate(LayoutInflater.from(context), this, true)
    }
}
