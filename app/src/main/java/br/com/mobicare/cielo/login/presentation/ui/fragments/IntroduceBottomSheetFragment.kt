package br.com.mobicare.cielo.login.presentation.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerIntroduce
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_introduce_full.*

class IntroduceBottomSheetFragment : BottomSheetDialogFragment(){

    private var screenName: String = EMPTY
    val NUM_PAGES = THREE

    companion object {
        fun newInstance(screenName: String = EMPTY): IntroduceBottomSheetFragment =
            IntroduceBottomSheetFragment().apply {
                this.screenName = screenName
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_introduce_full, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        gaSendEvent()
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(
                    com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState >= 4) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
            })
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }


        val pagerAdapter = childFragmentManager?.let { ScreenSlidePagerAdapter(it) }
        vp_introduce.adapter = pagerAdapter
        indicator_introduce.setViewPager(vp_introduce)


        btn_introduce_close.setOnClickListener {
            dialog?.dismiss()
            gaSendButton("sair")
        }

    }

    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int = NUM_PAGES

        @SuppressLint("NewApi")
        override fun getItem(position: Int): Fragment {
            return when (position) {
                ZERO -> {
                     ItemIntroduceFragment.newInstance(
                        createItemBannerIntroduce(
                            R.drawable.tooltip01,
                            R.string.title_vp_introduce_01,
                            R.string.subtitle_vp_introduce_01,
                            ONE)
                    )
                }
                ONE -> {
                    ItemIntroduceFragment.newInstance(
                        createItemBannerIntroduce(
                            R.drawable.tooltip02,
                            R.string.title_vp_introduce_02,
                            R.string.subtitle_vp_introduce_02_a,
                            TWO)
                    )
                }
                TWO -> {
                    CentralAjudaIntroduceFragment.newInstance(this@IntroduceBottomSheetFragment.screenName)
                }
                else -> ItemIntroduceFragment()
            }
        }
    }

    private fun createItemBannerIntroduce(@DrawableRes drawableId: Int,
                                          @StringRes title: Int, @StringRes subtitle: Int, id: Int):
            ItemBannerIntroduce {
        return ItemBannerIntroduce(getString(title), getString(subtitle), ContextCompat.getDrawable(requireContext(), drawableId), id)
    }

    private fun gaSendEvent() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO,  LOGIN_COMO_ACESSAR),
            action = listOf(Action.MODAL),
            label = listOf("carrosel")
        )
    }

    private fun gaSendButton(labelButton: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, LOGIN_COMO_ACESSAR),
            action = listOf(Action.FORMULARIO),
            label = listOf(Label.BOTAO, labelButton.replace("\n", ""))
        )
    }

}