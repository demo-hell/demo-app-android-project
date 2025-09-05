package br.com.mobicare.cielo.pixMVVM.presentation.account.ui.receiptMethod.builders

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.DrawableRes
import br.com.cielo.libflue.util.imageUtils.setCustomDrawable
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.ifNull
import br.com.mobicare.cielo.commons.utils.joinWithLastCustomSeparator
import br.com.mobicare.cielo.databinding.LayoutPixReceiptMethodCardBinding
import br.com.mobicare.cielo.extensions.gone

import br.com.mobicare.cielo.extensions.visible

class PixReceiptMethodCardViewBuilder(
    layoutInflater: LayoutInflater,
    private val data: Data
) {

    private val binding = LayoutPixReceiptMethodCardBinding.inflate(layoutInflater)

    fun build(): View {
        setupTitle()
        setupDescription()
        setupIcon()
        setupActiveModel()
        setupScheduleHourList()
        setupTapListener()

        return binding.root
    }

    private fun setupTitle() {
        binding.tvTitle.text = data.title
    }

    private fun setupDescription() {
        binding.tvDescription.text = data.description
    }

    private fun setupIcon() {
        binding.ivIcon.setImageResource(data.iconRes)
    }

    private fun setupActiveModel() {
        binding.tvActiveModel.apply {
            setCustomDrawable {
                solidColor = R.color.green_100
                radius = R.dimen.dimen_8dp
            }
            visible(data.isActiveModel)
        }
    }

    private fun setupScheduleHourList() {
        data.scheduleHourList?.let {
            binding.tvScheduleList.apply {
                setCustomDrawable {
                    solidColor = R.color.purple_alpha_16
                    radius = R.dimen.dimen_8dp
                }
                text = it.joinWithLastCustomSeparator()
                visible()
            }
        }
    }

    private fun setupTapListener() {
        binding.apply {
            data.onTap?.let { onTap ->
                container.apply {
                    setOnClickListener { onTap() }
                    isClickable = true
                    isFocusable = true
                }
                ivArrowEnd.visible()
            }.ifNull {
                container.apply {
                    setOnClickListener(null)
                    isClickable = false
                    isFocusable = false
                }
                ivArrowEnd.gone()
            }
        }
    }

    data class Data(
        val title: String,
        val description: String,
        @DrawableRes val iconRes: Int,
        val isActiveModel: Boolean,
        val scheduleHourList: List<String>? = null,
        val onTap: (() -> Unit)? = null
    )

}