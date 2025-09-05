package br.com.mobicare.cielo.posVirtual.presentation.router.views

import android.app.Dialog
import android.content.Context
import android.view.View
import br.com.cielo.libflue.screen.HandlerViewBuilderFlui
import br.com.mobicare.cielo.R

class PosVirtualCanceledHandlerView(private val context: Context) {

    operator fun invoke(onMakeCallClick: () -> Unit, onFinishClick: () -> Unit) =
        HandlerViewBuilderFlui.Builder(context)
            .title(context.getString(R.string.pos_virtual_eligibility_canceled_title))
            .titleStyle(R.style.bold_montserrat_20_cloud_600_spacing_8)
            .titleAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .message(context.getString(R.string.pos_virtual_eligibility_canceled_message))
            .messageStyle(R.style.regular_ubuntu_16_cloud_400_spacing_6)
            .messageAlignment(View.TEXT_ALIGNMENT_TEXT_START)
            .contentImage(R.drawable.img_50_nao_elegivel)
            .labelContained(context.getString(R.string.text_call_center_action))
            .isShowButtonContained(true)
            .isShowHeaderImage(true)
            .isShowButtonBack(false)
            .containedClickListener(object : HandlerViewBuilderFlui.ContainedOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    onMakeCallClick()
                }
            })
            .headerClickListener(object : HandlerViewBuilderFlui.HeaderOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    onFinishClick()
                }
            })
            .finishClickListener(object : HandlerViewBuilderFlui.FinishOnClickListener {
                override fun onClick(dialog: Dialog?) {
                    dialog?.dismiss()
                    onFinishClick()
                }
            })
            .build()

}