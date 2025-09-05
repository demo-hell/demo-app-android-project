package br.com.mobicare.cielo.minhasVendas.fragments

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.widget.ClosableFullscrenDialog
import br.com.mobicare.cielo.commons.ui.widget.FullscreenBottomSheetDialog
import kotlinx.android.synthetic.main.fragment_sheet_bottom_tutorial_cancel.*
import kotlinx.android.synthetic.main.toolbar_rounded_white.*

class CancelTutorialBottomSheetFragment : FullscreenBottomSheetDialog() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sheet_bottom_tutorial_cancel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCloseActionListener = object : ClosableFullscrenDialog.OnCloseActionButton {

            override fun onCloseButtonClick() {
                dismiss()
            }
        }

        textCommonDialogToolbarTitle.text = getString(R.string.text_cancel_tutorial_title)

        lottieAnimationViewCancelTutorial.visibility = View.VISIBLE

        lottieAnimationViewCancelTutorial.playAnimation()

        UserPreferences.getInstance()
                .saveCancelTutorialExibitionCount(
                        UserPreferences.getInstance().cancelTutorialExibitionCount + ONE)

        lottieAnimationViewCancelTutorial.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator) {
            }

            override fun onAnimationEnd(animator: Animator) {
                animator.pause()
                dismiss()
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationStart(p0: Animator) {
            }

        })

    }
}