package br.com.mobicare.cielo.accessManager.addUser

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.assignRole.AccessManagerSelectRoleFragmentDirections
import br.com.mobicare.cielo.changeEc.domain.CnpjHierarchy
import br.com.mobicare.cielo.changeEc.domain.Hierarchy
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.ButtonBottomStyle
import br.com.mobicare.cielo.commons.ui.widget.TextToolbaNameStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtSubTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.TxtTitleStyle
import br.com.mobicare.cielo.commons.ui.widget.flui.BottomSheetFluiGenericFragment
import br.com.mobicare.cielo.commons.utils.CNPJ_MASK_INITIAL
import br.com.mobicare.cielo.commons.utils.CustomCaretString
import br.com.mobicare.cielo.commons.utils.bottomSheetGenericFlui
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAddUserEstablishmentBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.pix.constants.EMPTY
import com.redmadrobot.inputmask.helper.Mask
import com.redmadrobot.inputmask.model.CaretString
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AccessManagerAddUserEstablishmentFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerAddUserEstablishmentContract.View {
    private var navigation: CieloNavigation? = null
    private var cpfIsNull: String? = null
    private val args: AccessManagerAddUserEstablishmentFragmentArgs by navArgs()
    private var _binding: FragmentAccessManagerAddUserEstablishmentBinding? = null
    private val binding get() = _binding
    private val mPresenter: AccessManagerAddUserEstablishmentPresenter by inject { parametersOf(this) }
    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }
    private var mAdapter: AccessManagerAddUserEstablishmentAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentAccessManagerAddUserEstablishmentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configPresenter()
        setupNavigation()
        setupListeners()
        setupView()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun configPresenter() {
        mPresenter.loadItens()
    }

    private fun setupListeners() {
        binding?.btBackArrow?.setOnClickListener {
            activity?.onBackPressed()
        }

        binding?.tvTitle?.text = when (args.roleargs) {
            ADMINROLE -> getString(
                R.string.access_manager_addUser_establishment_title,
                getString(R.string.access_manager_admins)
            )
            ANALYSTROLE -> getString(
                R.string.access_manager_addUser_establishment_title,
                getString(R.string.access_manager_analyst)
            )
            READERROLE -> getString(
                R.string.access_manager_addUser_establishment_title,
                getString(R.string.access_manager_readers)
            )
            TECHNICALROLE -> getString(
                R.string.access_manager_addUser_establishment_title,
                getString(R.string.access_manager_technical)
            )
            else -> getString(
                R.string.access_manager_addUser_establishment_title,
                getString(R.string.access_manager_readers)
            )
        }

        binding?.NextButton?.setOnClickListener {
            validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                mPresenter.sendInvitation(
                    otp = otpCode,
                    cpfInviteRequest = cpfIsNull,
                    emailInviteRequest = args.email,
                    roleInviteRequest = args.roleargs,
                    foreignInviteRequest = args.isforeignargs,
                    countryCodeInviteRequest = args.nationalitycodeargs,
                )
            }
        }
    }

    private fun setupView() {
        binding?.tvSubtitle?.fromHtml(
            R.string.access_manager_addUser_establishment_desc,
            mPresenter.getRootCNPJ()
        )
        cpfIsNull = if (args.cpf.isEmpty()){
            null
        }else{
            args.cpf
        }
    }

    override fun showMerchants(merchants: Array<Hierarchy>?) {
        if (isAttached()) {
            val merchantList = merchants?.toMutableList()
            if (merchantList?.size!! > ZERO) {
                binding?.recyclerExtractPageAll?.layoutManager =
                    (androidx.recyclerview.widget.LinearLayoutManager(context))
                mAdapter = AccessManagerAddUserEstablishmentAdapter(
                    ArrayList(merchantList)
                )
                binding?.recyclerExtractPageAll?.adapter = mAdapter
            } else {
                merchantList.add(
                    ZERO, Hierarchy(
                        false,
                        EMPTY,
                        UserPreferences.getInstance().ecUserLogged,
                        false,
                        EMPTY,
                        EMPTY,
                        UserPreferences.getInstance().userInformation?.merchant?.name.toString(),
                        UserPreferences.getInstance().userInformation?.merchant?.tradingName.toString(),
                        EMPTY,
                        EMPTY,
                        CnpjHierarchy(
                            EMPTY,
                            UserPreferences.getInstance().userInformation?.merchant?.cnpj?.number.toString()
                        )
                    )
                )
                binding?.recyclerExtractPageAll?.layoutManager =
                    (androidx.recyclerview.widget.LinearLayoutManager(context))
                mAdapter = AccessManagerAddUserEstablishmentAdapter(
                    ArrayList(merchantList)
                )
                binding?.recyclerExtractPageAll?.adapter = mAdapter
            }

        }
    }

    override fun showSuccess(result: Any) {
        super.showSuccess(result)
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    doWhenResumed {
                        bottomSheetGenericFlui(
                            nameTopBar = EMPTY,
                            image = R.drawable.ic_validado,
                            title = getString(R.string.invite_success_title),
                            subtitle = getString(
                                R.string.invite_success_subtitle,
                                UserPreferences.getInstance().userInformation?.merchant?.tradingName.toString()
                            ),
                            nameBtn1Bottom = getString(R.string.invite_success_bt1),
                            nameBtn2Bottom = getString(R.string.invite_success_bt2),
                            isPhone = false,
                            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE_TXT_BLUE,
                            isCancelable = true,
                            txtTitleStyle = TxtTitleStyle.TXT_TITLE_DARK_BLUE,
                            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_FLUI_BOTTOM_SHEET,
                        ).apply {
                            onClick =
                                object :
                                    BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {

                                    override fun onBtnFirst(dialog: Dialog) {
                                        findNavController().navigate(
                                            AccessManagerSelectRoleFragmentDirections.actionToAccessManagerHomeFragment()
                                        )
                                    }

                                    override fun onBtnSecond(dialog: Dialog) {
                                        mPresenter.getCustomerSettings()
                                    }

                                    override fun onSwipeClosed() {
                                        findNavController().navigate(
                                            AccessManagerSelectRoleFragmentDirections.actionToAccessManagerHomeFragment()
                                        )
                                    }

                                    override fun onCancel() {
                                        findNavController().navigate(
                                            AccessManagerSelectRoleFragmentDirections.actionToAccessManagerHomeFragment()
                                        )
                                    }
                                }
                        }.show(
                            childFragmentManager,
                            getString(R.string.bottom_sheet_generic)
                        )
                    }
                }

            })
    }

    override fun showLoading(@StringRes loadingMessage: Int?, vararg messageArgs: String) {
        doWhenResumed {
            navigation?.showLoading(true, loadingMessage, *messageArgs)
        }
    }

    override fun hideLoading(
        @StringRes successMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        doWhenResumed(
            action = {
                navigation?.showContent(true, successMessage, loadingSuccessCallback, *messageArgs)
                    ?: loadingSuccessCallback?.invoke()
            },
            errorCallback = { loadingSuccessCallback?.invoke() }
        )
    }

    override fun showError(error: ErrorMessage?, retryCallback: (() -> Unit)?) {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.ic_generic_error_image,
                    title = getString(R.string.generic_error_title),
                    message = messageError(error, requireActivity()),
                    bt1Title = if (retryCallback != null)
                        getString(R.string.text_try_again_label)
                    else
                        null,
                    bt1Callback = {
                        retryCallback?.invoke()
                        false
                    },
                    bt2Title = getString(R.string.entendi),
                    bt2Callback = {
                        findNavController().navigate(
                            AccessManagerSelectRoleFragmentDirections.actionToAccessManagerHomeFragment()
                        )
                        false
                    },
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun cnpjMaskFormatter(inputCnpj: String): Mask.Result {
        val cnpjMask = Mask(CNPJ_MASK_INITIAL)
        return cnpjMask.apply(CustomCaretString.forward(inputCnpj))
    }

    override fun onErrorOTP() {
        bottomSheetErrorOTP {
            validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
                sendInvitation(otpCode)
            }
        }
    }

    private fun sendInvitation(otpCode: String) {
        mPresenter.sendInvitation(
            otp = otpCode,
            cpfInviteRequest = cpfIsNull,
            emailInviteRequest = args.email,
            roleInviteRequest = args.roleargs,
            foreignInviteRequest = args.isforeignargs,
            countryCodeInviteRequest = args.nationalitycodeargs
        )
    }

    private fun bottomSheetErrorOTP(onTryAgain: () -> Unit) {
        bottomSheetGenericFlui(
            nameTopBar = EMPTY,
            R.drawable.ic_lock_error,
            getString(R.string.text_pix_transfer_error_otp_title),
            getString(R.string.text_pix_error_otp_message),
            nameBtn1Bottom = getString(R.string.back),
            nameBtn2Bottom = getString(R.string.text_pix_transfer_error_btn_try_again),
            statusNameTopBar = false,
            statusTitle = true,
            statusSubTitle = true,
            statusImage = true,
            statusBtnClose = false,
            statusBtnFirst = true,
            statusBtnSecond = true,
            statusView1Line = true,
            statusView2Line = false,
            txtToolbarNameStyle = TextToolbaNameStyle.TXT_TOOlBAR_NAME_NORMAL,
            txtTitleStyle = TxtTitleStyle.TXT_TITLE_BLUE,
            txtSubtitleStyle = TxtSubTitleStyle.TXT_SUBTITLE_BLACK,
            btn1BottomStyle = ButtonBottomStyle.BNT_BOTTOM_WHITE,
            btn2BottomStyle = ButtonBottomStyle.BNT_BOTTOM_BLUE,
            isFullScreen = true
        ).apply {
            this.onClick =
                object : BottomSheetFluiGenericFragment.OnClickButtonsOptionsListener {
                    override fun onBtnSecond(dialog: Dialog) {
                        dialog.dismiss()
                        onTryAgain.invoke()
                    }

                    override fun onBtnFirst(dialog: Dialog) {
                        dialog.dismiss()
                    }

                    override fun onSwipeClosed() {
                        dismiss()
                    }
                }
        }.show(childFragmentManager, getString(R.string.bottom_sheet_generic))
    }

    override fun addUserForeignFlowAllowed(foreignFlowAllowed: Boolean) {
        findNavController().navigate(
            if (foreignFlowAllowed)
                AccessManagerAddUserEstablishmentFragmentDirections.actionAccessManagerAddUserEstablishmentFragmentToAccessManagerAddUserOptionForeignFragment()
            else
                AccessManagerAddUserEstablishmentFragmentDirections.actionAccessManagerAddUserEstablishmentFragmentToAccessManagerAddUserTypeFragment(false, EMPTY)
        )
    }

    companion object {
        private const val ADMINROLE = "ADMIN"
        private const val READERROLE = "READER"
        private const val ANALYSTROLE = "ANALYST"
        private const val TECHNICALROLE = "TECHNICAL"
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}