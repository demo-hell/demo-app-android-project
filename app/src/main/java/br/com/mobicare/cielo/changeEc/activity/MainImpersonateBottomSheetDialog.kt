package br.com.mobicare.cielo.changeEc.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.changeEc.ChangeEc
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.Merchant
import br.com.mobicare.cielo.changeEc.fragment.ChangeEcChildFragment
import br.com.mobicare.cielo.changeEc.fragment.ChangeEcMerchantFragment
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.featureToggle.FeatureToggleContract
import br.com.mobicare.cielo.featureToggle.presenter.FeatureTogglePresenter
import br.com.mobicare.cielo.main.presentation.ui.activities.MainBottomNavigationActivity
import br.com.mobicare.cielo.newLogin.updateregister.OnBoardUpdateRegister
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_change_ec.*
import kotlinx.android.synthetic.main.toolbar_dialog.*
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

private const val SCREEN_SIZE = 0.85F
private const val SCREEN_GONE = -1.0f
private const val CHANGE_NEW_EC = "CHANGE_NEW_EC"


class MainImpersonateBottomSheetDialog : BottomSheetDialogFragment(), ChangeEcListener,
    FeatureToggleContract.View {

    private val presenterFeatureToggle: FeatureTogglePresenter by inject {
        parametersOf(this)
    }

    private var merchant: Merchant? = null
    private var sequence = 0

    private var listener: MainImpersonateBottomSheetDialogListener? = null
    private var updateToolbarHome: (() -> Unit)? = null
    private var updateBottomNavigation: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_change_ec, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragment()
    }

    override fun onResume() {
        super.onResume()
        presenterFeatureToggle.onResume()
    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            val bottomSheet: View? = dialog?.findViewById(R.id.design_bottom_sheet)
            this.view?.let { _ ->
                bottomSheet?.let { itBottomSheet ->
                    itBottomSheet.layoutParams.height =
                        (Resources.getSystem().displayMetrics.heightPixels * SCREEN_SIZE).toInt()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        presenterFeatureToggle.onPause()
    }

    fun setListener(listener: MainImpersonateBottomSheetDialogListener) {
        this.listener = listener
    }

    fun setUpdateToolbarHome(listener: () -> Unit) {
        updateToolbarHome = listener
    }

    fun setUpdateBottomNavigation(listener: () -> Unit) {
        updateBottomNavigation = listener
    }

    fun setFragment() {
        ChangeEcMerchantFragment.create(this)
            .addInFrame(this.childFragmentManager, R.id.frameFormContentInput)
    }

    fun setFragment(isBackAnimation: Boolean) {
        when (sequence) {
            ZERO -> {
                ChangeEcMerchantFragment.create(this)
                    .addWithAnimation(
                        this.childFragmentManager,
                        R.id.frameFormContentInput,
                        isBackAnimation
                    )
            }
            ONE -> {
                ChangeEcChildFragment.create(this, merchant)
                    .addWithAnimation(
                        this.childFragmentManager,
                        R.id.frameFormContentInput,
                        isBackAnimation
                    )
            }
        }
    }

    override fun onNextStep(isFinish: Boolean) {
        if (isFinish)
            dismissAllowingStateLoss()
        else {
            sequence++
            setFragment(false)
        }
    }

    override fun onBackStep() {
        if (sequence <= ZERO)
            dismissAllowingStateLoss()
        else {
            sequence--
            setFragment(true)
        }
    }

    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(requireContext())
    }

    override fun onMerchant(merchant: Merchant) {
        this.merchant = merchant
    }

    override fun onChangeNewEc(impersonate: Impersonate, merchant: Merchant) {
        if (impersonate.isConvivenciaUser != false) {
            val changeEc = ChangeEc()
            changeEc.createNewLoginConvivencia(impersonate, merchant)
            this.listener?.onImpersonated()
            LocalBroadcastManager.getInstance(requireContext())
                .sendBroadcast(Intent(MainBottomNavigationActivity.IMPERSONATE_USER_ACTION))
        } else
            startActivity(Intent(context, OnBoardUpdateRegister::class.java))

        updateToolbarHome?.invoke()
        updateBottomNavigation?.invoke()
        this.dismiss()
    }

    override fun onButtonLeftVisible(isVisible: Boolean) {
        btnLeft?.visible(isVisible)
    }

    override fun showLoading() {
        super.showLoading()
        progress_loading?.visible()
    }

    override fun hideLoading() {
        super.hideLoading()
        progress_loading?.gone()
    }

    override fun onFeatureToogleSuccess() {
        finishChangeEc()
    }

    override fun onFeatureToogleError() {
        finishChangeEc()
    }

    private fun finishChangeEc() {
        val result = Intent()
        result.putExtra(CHANGE_NEW_EC, true)
        dismissAllowingStateLoss()
    }

    /**
     *onCreateDialog
     * @param savedInstanceState
     * @return dialog
     * */
    @SuppressLint("StringFormatMatches")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val impersonateDialog = super.onCreateDialog(savedInstanceState)
        changeDialog(impersonateDialog)

        impersonateDialog.setOnKeyListener { dialog, keyCode, _ ->
            if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                dialog.dismiss()
                return@setOnKeyListener true
            } else
                return@setOnKeyListener false
        }
        return impersonateDialog
    }

    private fun changeDialog(dialog: Dialog) {
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
            val behavior = BottomSheetBehavior.from(bottomSheet)

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight =
                (Resources.getSystem().displayMetrics.heightPixels * SCREEN_SIZE).toInt()
            behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    if (slideOffset == SCREEN_GONE)
                        this@MainImpersonateBottomSheetDialog.dismiss()
                }
            })
        }

    }

    interface MainImpersonateBottomSheetDialogListener {
        fun onImpersonated()
    }
}