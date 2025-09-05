package br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.failed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.databinding.LayoutPixExtractDetailStatusNotExecutedBinding
import br.com.mobicare.cielo.extensions.ifNullSimpleLine
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.views.status.PixStatusBaseViewBuilder

abstract class PixStatusFailedViewBuilder(
    layoutInflater: LayoutInflater
) : PixStatusBaseViewBuilder() {

    protected val binding = LayoutPixExtractDetailStatusNotExecutedBinding.inflate(layoutInflater)

    final override val context: Context = binding.root.context

    abstract val content: Content
    abstract val information: Information

    open fun build(): View {
        configureViews()
        configureContent()
        configureInformation()
        selectTextStrikeThrough()

        return binding.root
    }

    private fun configureViews() {
        binding.apply {
            containerAlert.applyCustomStyle(R.color.red_100)
            includeContent.apply {
                root.applyCustomStyle(R.color.cloud_100)
                ivStatus.apply {
                    applyCustomStyle(R.color.color_2F363E_alpha_12, R.dimen.dimen_8dp)
                    setImageResource(R.drawable.ic_symbols_slash_neutral_main_20_dp)
                }
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
            ivStatus.setImageResource(R.drawable.ic_symbols_slash_neutral_main_20_dp)
        }
    }

    private fun configureInformation() {
        binding.includeInfo.apply {
            tvChannel.text = information.channel.ifNullSimpleLine()
            tvMerchant.text = information.merchant.ifNullSimpleLine()
        }
    }

    private fun selectTextStrikeThrough() {
        binding.includeContent.apply {
            applyTextStrikeThrough(
                tvStatus,
                tvLabel,
                tvAmount,
                tvSentTo,
                tvDocument
            )
        }
    }

}