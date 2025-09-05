package br.com.mobicare.cielo.minhasVendas.fragments.cancelamento

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.minhasVendas.constants.ITEM_CANCEL_TUTORIAL_ARGS
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_introduce_login.*
import kotlinx.android.synthetic.main.layout_onbording_full.*
import java.io.Serializable

/**
 * create by Enzo Teles
 * */
class OnbordingCancelSheetFragment: BottomSheetDialogFragment(), ViewPager.OnPageChangeListener{

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.layout_onbording_full, container, false)

    val NUM_PAGES = 3
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
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
        UserPreferences.getInstance().saveCancelStatus(true)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }


        val pagerAdapter = childFragmentManager?.let { ScreenSlidePagerAdapter(it)}
        vp_onbording.adapter = pagerAdapter
        indicator_onbording.setViewPager(vp_onbording)

        btn_onbording_close.setOnClickListener {
            dismiss()
        }

        vp_onbording.addOnPageChangeListener(this)

        btn_tut_next.setOnClickListener {
            vp_onbording.setCurrentItem(getItem(1), true)
            if(vp_onbording.currentItem == 2){
                btn_tut_next.visibility = View.GONE
                btn_tut_end.visibility = View.VISIBLE
            }else{
                btn_tut_next.visibility = View.VISIBLE
                btn_tut_end.visibility = View.GONE
            }
        }

        btn_tut_end.setOnClickListener {
            dismiss()
        }



    }

    /**
     * método que set mais uma posição no view page
     * @param i
     * */
    private fun getItem(i: Int): Int {
        return vp_onbording.getCurrentItem() + i
    }

    companion object {
        fun newInstance(): OnbordingCancelSheetFragment {
            return OnbordingCancelSheetFragment()
        }
    }

    /**
     * Adapter que popula o tutorial do cancelamento
     * @param fm
     * */
    private inner class ScreenSlidePagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = NUM_PAGES

        @SuppressLint("NewApi", "ResourceType")
        override fun getItem(position: Int): Fragment {

            val item = when (position) {
                0 -> {
                   createItemBannerTutorial(R.drawable.ic_can_tut_01,
                            R.string.titulo_tutorial_01, R.string.subtitulo_tutorial_01, 1)
                }
                1 -> {
                    createItemBannerTutorial(R.drawable.ic_can_tut_02,
                            R.string.titulo_tutorial_02, R.string.subtitulo_tutorial_02, 2)
                }
                2 -> {
                   createItemBannerTutorial(R.drawable.ic_can_tut_03,
                            R.string.titulo_tutorial_03, R.string.subtitulo_tutorial_03, 3)
                }
                else -> null
            }

            return ItemTutoCancelFragment.newInstance(item)
        }

    }

    /**
     * método para popular o objeto
     * @param drawableId, title, subtitle, id
     * */
    private fun createItemBannerTutorial(@DrawableRes drawableId: Int,
                                          @StringRes title: Int, @StringRes subtitle: Int, @IntegerRes id: Int):
            ItemCancelTutorial {
        return ItemCancelTutorial(getString(title), getString(subtitle),ContextCompat.getDrawable(requireContext(), drawableId), id)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if(position == 2){
            btn_tut_next.visibility = View.GONE
            btn_tut_end.visibility = View.VISIBLE
        }else{
            btn_tut_next.visibility = View.VISIBLE
            btn_tut_end.visibility = View.GONE
        }
    }

}

/**
 * fragment que mostra os itens do cancelamento no tutorial
 * @param item
 * */
class ItemTutoCancelFragment : BaseFragment() {

    var item: ItemCancelTutorial? = null

    companion object {
        fun newInstance(itemCancel: ItemCancelTutorial?): ItemTutoCancelFragment {
            return ItemTutoCancelFragment().apply {
                this.item = itemCancel
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_item_tutorial_cancel_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        item?.title?.let { txt_welcome_title?.text = it }
        item?.subtitle?.let { txt_welcome_subtitle?.text = it }
        item?.imageUrl?.let { iv_banner_introduce?.setImageDrawable(it) }
    }
}
/**
 * classe de modelo do tutorial de cancelamento
 * @param title , subtitle , imageUrl, id
 * */
data class ItemCancelTutorial(val title: String?, val subtitle: String?, val imageUrl: Drawable?, val id:Int?) : Serializable