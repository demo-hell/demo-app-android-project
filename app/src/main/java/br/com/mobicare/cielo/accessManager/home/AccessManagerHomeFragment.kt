package br.com.mobicare.cielo.accessManager.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.accessManager.model.ForeignUsersItem
import br.com.mobicare.cielo.commons.constants.Text.OTP
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.messageError
import br.com.mobicare.cielo.databinding.FragmentAccessManagerHomeBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.orZero
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.login.domains.entities.UserObj
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class AccessManagerHomeFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerHomeContract.View {

    private val presenter: AccessManagerHomePresenter by inject {
        parametersOf(this)
    }

    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(activity ?: requireActivity())
    }

    private var navigation: CieloNavigation? = null
    private var binding: FragmentAccessManagerHomeBinding? = null
    private var noRoleUsers: List<AccessManagerUser>? = null
    private var adminUsers: List<AccessManagerUser>? = null
    private var readerUsers: List<AccessManagerUser>? = null
    private var analystUsers: List<AccessManagerUser>? = null
    private var foreignUsers: List<ForeignUsersItem>? = null
    private var technicalUsers: List<AccessManagerUser>? = null
    private var customUsers: List<AccessManagerUser>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentAccessManagerHomeBinding.inflate(layoutInflater, container, false)
            .also { binding = it }
            .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        setupListeners()
        configMfaRouteHandler()
    }

    override fun onPauseActivity() {
        super.onPauseActivity()
        presenter.onPause()
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
        presenter.getNoRoleUsers()
        presenter.getUsers()
        presenter.getExpiredInvites()
        presenter.getForeignUsers()
        presenter.getCustomerSettings()
    }

    private fun configMfaRouteHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show) {
                showLoading()
            } else
                hideLoading()
        }
    }

    override fun showCardCustomProfile() {
        binding?.cardCustom?.visible()
    }

    override fun getCustomUsers(customProfileEnabled: Boolean) {
        presenter.getCustomUsers(customProfileEnabled)
    }

    private fun setupListeners() {
        binding?.apply {
            btBackArrow.setOnClickListener {
                requireActivity().finish()
            }

            cardNoRole.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections
                            .actionAccessManagerHomeFragmentToAccessManagerSelectRoleFragment(
                                noRoleUsers?.toTypedArray() ?: arrayOf()
                            )
                    )
                }
            }

            cardAdmin.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections
                            .actionAccessManagerHomeFragmentToAccessManagerAssignedUsersFragment(
                                adminUsers?.toTypedArray() ?: arrayOf(),
                                UserObj.ADMIN,
                                EMPTY,
                                presenter.getCustomProfileEnabled()
                            )
                    )
                }
            }

            cardReader.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections
                            .actionAccessManagerHomeFragmentToAccessManagerAssignedUsersFragment(
                                readerUsers?.toTypedArray() ?: arrayOf(),
                                UserObj.READER,
                                EMPTY,
                                presenter.getCustomProfileEnabled()
                            )
                    )
                }
            }
            cardAnalyst.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections
                            .actionAccessManagerHomeFragmentToAccessManagerAssignedUsersFragment(
                                analystUsers?.toTypedArray() ?: arrayOf(),
                                UserObj.ANALYST,
                                EMPTY,
                                presenter.getCustomProfileEnabled()
                            )
                    )
                }
            }

            addUserButton.setOnClickListener {
                addUserForeignFlowAllowed(presenter.getForeignFlowAllowed())
            }

            cardExpired.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections
                            .actionAccessManagerHomeFragmentToAccessManagerExpiredInvitationFragment()
                    )
                }
            }

            cardForeign.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections.actionAccessManagerHomeFragmentToAccessManagerPendingForeignUsersFragment(
                            foreignUsers?.toTypedArray() ?: arrayOf()
                        )
                    )
                }
            }

            carTechnical.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections
                            .actionAccessManagerHomeFragmentToAccessManagerAssignedUsersFragment(
                                technicalUsers?.toTypedArray() ?: arrayOf(),
                                UserObj.TECHNICAL,
                                EMPTY,
                                presenter.getCustomProfileEnabled()
                            )
                    )
                }
            }

            cardCustom.setOnClickListener {
                if (cardCustom.count > ZERO){
                    findNavController().navigate(
                        AccessManagerHomeFragmentDirections
                            .actionAccessManagerHomeFragmentToAccessManagerCustomProfileFragment(
                                customUsers?.toTypedArray() ?: arrayOf()
                            )
                    )
                }else{
                    showErrorEmptyProfiles()
                }
            }
        }
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    override fun showNoRoleUsers(noRoleUsers: List<AccessManagerUser>?) {
        this.noRoleUsers = noRoleUsers
        doWhenResumed {
            binding?.apply {
                cardNoRole.visible()
                cardNoRole.count = noRoleUsers?.count().orZero
            }
        }
    }

    override fun showAdminUsers(adminUsers: List<AccessManagerUser>?) {
        this.adminUsers = adminUsers
        doWhenResumed {
            binding?.apply {
                cardAdmin.visible()
                cardAdmin.count = adminUsers?.count().orZero
            }
        }
    }

    override fun showReaderUsers(readerUsers: List<AccessManagerUser>?) {
        this.readerUsers = readerUsers
        doWhenResumed {
            binding?.apply {
                cardReader.visible()
                cardReader.count = readerUsers?.count().orZero
            }
        }
    }

    override fun showAnalystUsers(analystUsers: List<AccessManagerUser>?) {
        this.analystUsers = analystUsers
        doWhenResumed {
            binding?.apply {
                cardAnalyst.visible()
                cardAnalyst.count = analystUsers?.count().orZero
            }
        }
    }

    override fun showForeignUsers(foreignUsers: List<ForeignUsersItem>?) {
        this.foreignUsers = foreignUsers
        doWhenResumed {
            binding?.apply {
                cardForeign.visible()
                cardForeign.count = foreignUsers?.count().orZero
            }
        }
    }

    override fun showExpiredInvitation(numberExpiredInvitations: Int) {
        doWhenResumed {
            binding?.apply {
                cardExpired.visible()
                cardExpired.count = numberExpiredInvitations
            }
        }
    }

    override fun showTechnicalUsers(technicalUsers: List<AccessManagerUser>?) {
        this.technicalUsers = technicalUsers
        doWhenResumed {
            binding?.apply {
                carTechnical.visible()
                carTechnical.count = technicalUsers?.count().orZero
            }
        }
    }

    override fun hideTechnicalUsers() {
        doWhenResumed {
            binding?.carTechnical.gone()
        }
    }

    override fun showCustomUsers(customUsers: List<AccessManagerUser>?) {
        this.customUsers = customUsers
        doWhenResumed {
            binding?.apply {
                cardCustom.visible(customUsers.isNullOrEmpty().not())
            }
        }
    }

    override fun hideExpiredInvitation() {
        doWhenResumed {
            binding?.cardExpired.gone()
        }
    }

    override fun hideForeignUsers() {
        doWhenResumed {
            binding?.cardForeign.gone()
        }
    }

    private fun addUserForeignFlowAllowed(foreignFlowAllowed: Boolean) {
        mfaRouteHandler.runWithMfaToken {
            findNavController().navigate(
                if (foreignFlowAllowed)
                    AccessManagerHomeFragmentDirections.
                    actionAccessManagerHomeFragmentToAccessManagerAddUserOptionForeignFragment()
                else
                    AccessManagerHomeFragmentDirections.
                    actionAccessManagerHomeFragmentToAccessManagerAddUserTypeFragment(false, EMPTY)
            )
        }
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
        if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString() || error.errorCode.contains(
                OTP
            )
        )
            doWhenResumed(
                action = {
                    navigation?.showCustomBottomSheet(
                        image = R.drawable.img_dark_07,
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
                            false
                        },
                    ) ?: baseLogout()
                },
                errorCallback = { baseLogout() }
            )
    }

    private fun showErrorEmptyProfiles() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_dark_07,
                    title = getString(R.string.access_manager_custom_profiles_title_error_empty),
                    message = getString(R.string.access_manager_custom_profiles_message_error_empty),
                    bt2Title = getString(R.string.back),
                    bt2Callback = {
                        false
                    },
                    closeCallback = {
                    },
                    titleBlack = true,
                    isCancelable = false,
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun onBackButtonClicked(): Boolean {
        findNavController().navigateUp()
        return super.onBackButtonClicked()
    }
}