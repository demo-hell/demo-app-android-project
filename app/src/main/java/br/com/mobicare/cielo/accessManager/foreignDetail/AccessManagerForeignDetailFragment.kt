package br.com.mobicare.cielo.accessManager.foreignDetail

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.model.AccessManagerForeignUserDetailResponse
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.databinding.FragmentAccessManagerForeignDetailBinding
import br.com.mobicare.cielo.extensions.backToHome
import br.com.mobicare.cielo.extensions.capitalizeWords
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.fromHtml
import br.com.mobicare.cielo.login.domains.entities.UserObj
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AccessManagerForeignDetailFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerForeignDetailContract.View {

    private val presenter: AccessManagerForeignDetailPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }
    val args: AccessManagerForeignDetailFragmentArgs by navArgs()
    private var navigation: CieloNavigation? = null
    private var binding: FragmentAccessManagerForeignDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAccessManagerForeignDetailBinding
        .inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onResume() {
        super.onResume()
        presenter.onResume()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading()
        setupNavigation()
        setupClickListeners()
        presenter.getForeignUserDetail(args.userId)
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    override fun showLoading() {
        binding?.containerProgressBar.visible()
    }

    override fun hideLoading() {
        binding?.containerProgressBar.gone()
    }

    private fun setupClickListeners() {
        binding?.apply {
            btBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            btnDecline.setOnClickListener {
                showDeclineBottomSheet()
            }

            btnAccept.setOnClickListener {
                showAcceptBottomSheet()
            }
        }
    }

    override fun getDetailSuccess(userDetail: AccessManagerForeignUserDetailResponse) {
        binding?.apply {
            tvForeignName.text = userDetail.name.capitalizeWords()
            tvForeignPhone.text = userDetail.cellphone
            tvForeignEmail.text = userDetail.email

            tvForeignPermission.text = getTextFromRole(userDetail.profile?.id,
                if (userDetail.profile?.name.isNullOrEmpty()) {
                    requireContext().getString(R.string.custom_rb)
                }else
                    userDetail.profile?.name
            )

            if (userDetail.photo.isNullOrEmpty().not()) {
                val imageBytes = Base64.decode(userDetail.photo, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                imgForeign.setImageBitmap(decodedImage)
            } else {
                imgForeign.gone()
            }

            hideLoading()
        }
    }

    private fun getTextFromRole(role: String?, profileName: String?): String? {
        return if (role.isNullOrEmpty()) {
            requireContext().getString(R.string.reader_rb)
        } else {
            when (role) {
                UserObj.ADMIN -> requireContext().getString(R.string.admin_rb)
                UserObj.ANALYST -> requireContext().getString(R.string.analyst_rb)
                UserObj.TECHNICAL -> requireContext().getString(R.string.technical_rb)
                UserObj.READER -> requireContext().getString(R.string.reader_rb)
                else -> profileName.capitalizeWords()
            }
        }
    }

    override fun showGenericError() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_10_erro,
                    title = requireContext().getString(R.string.id_onboarding_title_bs_error_p2_login),
                    message = requireContext().getString(R.string.access_manager_foreign_detail_error_message).fromHtml()
                        .toString(),
                    bt2Title = requireContext().getString(R.string.label_bt_try_again),
                    bt2Callback = {
                        goToAccessManagerHome()
                        false
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun showDeclineBottomSheet() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_117_perfil_impedido,
                    title = requireContext().getString(R.string.access_manager_foreign_detail_reprove_bs_title),
                    message = requireContext().getString(R.string.access_manager_foreign_detail_reprove_bs_message),
                    bt1Title = requireContext().getString(R.string.invite_success_bt1),
                    bt1Callback = {
                        goToAccessManagerHome()
                        false
                    },
                    bt2Title = requireContext().getString(R.string.confirm),
                    bt2Callback = {
                        sendDecision(REJECTED)
                        false
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun showAcceptBottomSheet() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_131_gestao_acesso,
                    title = requireContext().getString(R.string.access_manager_foreign_detail_approve_bs_title),
                    message = requireContext().getString(
                        R.string.access_manager_foreign_detail_approve_bs_message,
                        binding?.tvForeignPermission?.text
                    ),
                    bt1Title = requireContext().getString(R.string.invite_success_bt1),
                    bt1Callback = {
                        goToAccessManagerHome()
                        false
                    },
                    bt2Title = requireContext().getString(R.string.confirm),
                    bt2Callback = {
                        sendDecision(APPROVED)
                        false
                    }
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun sendDecision(decision: String) {
        validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
            presenter.sendForeignUserDecision(args.userId, decision, otpCode)
        }
    }

    private fun goToAccessManagerHome() {
        findNavController().popBackStack(R.id.accessManagerHomeFragment, false)
    }

    override fun decisionSuccess(decision: String) {
        val isApproved = decision.equals(APPROVED, true)
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    doWhenResumed(
                        action = {
                            navigation?.showCustomBottomSheet(
                                image =
                                if (isApproved)
                                    R.drawable.img_118_conta_criada_sucesso
                                else
                                    R.drawable.img_117_perfil_impedido,
                                title =
                                if (isApproved)
                                    requireContext().getString(R.string.access_manager_foreign_detail_success_aproved_title)
                                else
                                    requireContext().getString(R.string.access_manager_foreign_detail_success_reproved_title),
                                message =
                                if (isApproved)
                                    requireContext().getString(R.string.access_manager_foreign_detail_success_approved_message)
                                else
                                    requireContext().getString(R.string.access_manager_foreign_detail_success_declined_message),
                                bt1Title = requireContext().getString(R.string.access_manager_foreign_detail_success_button1),
                                bt1Callback = {
                                    goToAccessManagerHome()
                                    false
                                },
                                bt2Title = requireContext().getString(R.string.finish),
                                bt2Callback = {
                                    goToHome()
                                    false
                                }
                            ) ?: baseLogout()
                        },
                        errorCallback = { baseLogout() }
                    )
                }

            })
    }

    override fun decisionError() {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    doWhenResumed(
                        action = {
                            showGenericError()
                        },
                        errorCallback = { baseLogout() }
                    )
                }
            })
    }

    override fun onErrorOTP() {
        validationTokenWrapper.playAnimationError(callbackValidateToken =
        object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
            override fun callbackTokenError() {
                doWhenResumed(
                    action = {
                        showGenericError()
                    },
                    errorCallback = { baseLogout() }
                )
            }
        })
    }

    private fun goToHome() {
        requireActivity().backToHome()
    }

    companion object {
        const val APPROVED = "APPROVED"
        const val REJECTED = "REJECTED"
    }
}