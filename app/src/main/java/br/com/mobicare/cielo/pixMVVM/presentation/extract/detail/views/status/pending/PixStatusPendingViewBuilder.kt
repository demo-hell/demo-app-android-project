package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.pending

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailStatusPendingBinding
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.PixStatusBaseViewBuilder

abstract class PixStatusPendingViewBuilder(
    layoutInflater: LayoutInflater
) : PixStatusBaseViewBuilder() {

    protected val binding = LayoutPixExtractDetailStatusPendingBinding.inflate(layoutInflater)

    final override val context: Context = binding.root.context

    abstract val content: Content
    abstract val information: Information

    open fun build(): View {
        configureViews()
        configureContent()
        configureInformation()

        return binding.root
    }

    private fun configureViews() {
        binding.apply {
            containerAlert.applyCustomStyle(R.color.yellow_100)
            includeContent.apply {
                root.applyCustomStyle(R.color.cloud_100)
                ivStatus.applyCustomStyle(R.color.color_2F363E_alpha_12, R.dimen.dimen_8dp)
            }
        }
    }

    private fun configureContent() {
        binding.includeContent.apply {
            tvStatus.text = content.status
            tvDate.text = content.date
            tvLabel.text = content.label
            tvAmount.text = content.amount
            tvSentTo.text = content.sentTo
            tvDocument.text = content.document
            content.payerAnswer?.let {
                tvMessage.text = it
                containerMessage.visible()
            }
        }
    }

    private fun configureInformation() {
        binding.includeInfo.apply {
            tvChannel.text = information.channel.ifNullSimpleLine()
            tvMerchant.text = information.merchant.ifNullSimpleLine()
        }
    }

}