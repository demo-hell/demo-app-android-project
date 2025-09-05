package br.com.mobicare.cielo.accessManager.expired

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.expired.adapter.UsersWithExpiredInvitationAdapter
import br.com.mobicare.cielo.accessManager.model.AccessManagerExpiredInviteResponse
import br.com.mobicare.cielo.accessManager.model.Item
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.widget.CustomAlertDialogFacilita
import br.com.mobicare.cielo.commons.utils.EndlessScrollListener
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.getErrorMessage
import br.com.mobicare.cielo.commons.utils.showSuccess
import br.com.mobicare.cielo.databinding.FragmentAccessManagerExpiredInvitationBinding
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AccessManagerExpiredInvitationFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerExpiredInvitationContract.View {

    private val presenter: AccessManagerExpiredInvitationPresenter by inject {
        parametersOf(this)
    }

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    private val mfaRouteHandler: MfaRouteHandler by inject {
        parametersOf(activity ?: requireActivity())
    }

    private var navigation: CieloNavigation? = null
    private var layoutManager: LinearLayoutManager? = null
    private var scrollListener: EndlessScrollListener? = null
    private var adapter: UsersWithExpiredInvitationAdapter? = null

    private var users: ArrayList<Item> = ArrayList()
    private var usersSelected: ArrayList<Item> = ArrayList()

    private var isLoading = true
    private var pageNumber = 1

    private var _binding: FragmentAccessManagerExpiredInvitationBinding? = null
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =
            FragmentAccessManagerExpiredInvitationBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
        configMfaRouteHandler()
        setupView()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()

        presenter.onResume()
        setupExpiredInvites()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupExpiredInvites() {
        resetList()
        presenter.getExpiredInvites(isLoading, pageNumber)
    }

    private fun setupView() {
        layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        setupRecyclerView()
        selectAll()
        enableButtons()
        arrowBackListener()
        setNumberSelectedUsers()
        retryGetExpiredInvites()
    }

    private fun configMfaRouteHandler() {
        mfaRouteHandler.showLoadingCallback = { show ->
            if (show) {
                showLoading()
            } else
                hideLoading()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetList() {
        isLoading = true

        binding?.rvInvites?.layoutManager?.scrollToPosition(ZERO)
        users.clear()
        adapter?.notifyDataSetChanged()
        presenter.resetPagination()
        scrollListener?.resetState()

        enableButtons()
        setNumberSelectedUsers()
    }

    private fun setupRecyclerView() {
        adapter = UsersWithExpiredInvitationAdapter(users, usersSelected, this, this)

        layoutManager?.let {
            scrollListener = object : EndlessScrollListener(it) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                    if (binding?.rvInvites?.canScrollVertically(ONE) == true)
                        presenter.getExpiredInvites(
                            isLoading = isLoading,
                            pageNumber = pageNumber + ONE
                        )
                }
            }
        }

        binding?.rvInvites?.apply {
            layoutManager = this@AccessManagerExpiredInvitationFragment.layoutManager
            scrollListener?.let { addOnScrollListener(it) }
            adapter = this@AccessManagerExpiredInvitationFragment.adapter
        }
    }

    private fun retryGetExpiredInvites() {
        binding?.btnTryAgain?.setOnClickListener {
            setupExpiredInvites()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun selectAll() {
        binding?.btSelectAll?.setOnClickListener {
            val wasNotFull = usersSelected.size != users.size

            usersSelected.clear()

            if (wasNotFull) {
                usersSelected.addAll(users)
            }

            enableButtons()
            setNumberSelectedUsers()
            adapter?.notifyDataSetChanged()
        }
    }

    private fun setNumberSelectedUsers() {
        binding?.tvUsersCount?.text = getString(
            R.string.x_dash_y,
            usersSelected.size,
            users.size
        )
    }

    private fun arrowBackListener() {
        binding?.btBackArrow?.setOnClickListener {
            comeBack()
        }
    }

    private fun comeBack() {
        findNavController().navigate(
            AccessManagerExpiredInvitationFragmentDirections.actionAccessManagerExpiredInvitationFragmentToAccessManagerHomeFragment()
        )
    }

    private fun enableButtons() {
        binding?.apply {
            btnResendInvite.isEnabled = usersSelected.isNotEmpty()
            btnDeleteInvite.isEnabled = usersSelected.isNotEmpty()
            if (usersSelected.isNotEmpty())
                btnTextDeleteInvite.setTextColor(ContextCompat.getColor(requireContext(), R.color.red_DC392A))
            else
                btnTextDeleteInvite.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey_C5CED7))
        }
    }

    private fun setupClickListeners() {
        binding?.apply {
            btnResendInvite.setOnClickListener {
                mfaRouteHandler.runWithMfaToken {
                    validationTokenWrapper.generateOtp(showAnimation = false) { otpCode ->
                        presenter.onResendInvite(usersSelected, otpCode)
                    }
                }
            }

            btnDeleteInvite.setOnClickListener {
                showDeleteAlert()
            }
        }
    }

    private fun showView(isShowList: Boolean = true) {
        binding?.apply {
            containerExpiredInvites.visible(isShowList)
            btnResendInvite.visible(isShowList)
            btnDeleteInvite.visible(isShowList)
            containerError.visible(isShowList.not())
            btSelectAll.isEnabled = isShowList
        }
    }

    override fun showLoading(loadingMessage: Int?, vararg messageArgs: String) {
        navigation?.showLoading(isShow = true, loadingMessage, *messageArgs)
    }

    override fun showLoadingMore() {
        binding?.loadingMore?.visible()
    }

    override fun hideLoading(
        successMessage: Int?,
        loadingSuccessCallback: (() -> Unit)?,
        vararg messageArgs: String
    ) {
        navigation?.showContent(
            isShow = true,
            successMessage,
            loadingSuccessCallback,
            *messageArgs
        )
    }

    override fun hideLoadingMore() {
        binding?.loadingMore?.gone()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onShowExpiredInvites(
        expiredInviteResponse: AccessManagerExpiredInviteResponse,
        isUpdate: Boolean
    ) {
        showView()
        expiredInviteResponse.items?.let { item ->
            if (isLoading)
                setupRecyclerView()

            pageNumber = expiredInviteResponse.pagination.pageNumber
            isLoading = false

            if (isUpdate) {
                users.addAll(item)
                adapter?.notifyDataSetChanged()
            }
            enableButtons()
            setNumberSelectedUsers()
        }
    }

    override fun onErrorGetExpiredInvites(errorMessage: ErrorMessage?) {
        binding?.apply {
            showView(isShowList = false)
            setupExpiredInvites()
            tvNoInvites.text = requireActivity().getErrorMessage(
                errorMessage,
                getString(R.string.access_manager_resend_invite_no_invites_message)
            )
        }
    }

    override fun onSuccessResendInvite(invitations: Int) {
        val title = resources.getQuantityString(
            R.plurals.access_resend_invite_success_title,
            invitations,
            invitations
        )
        val message = resources.getQuantityString(
            R.plurals.access_resend_invite_success_message,
            invitations
        )

        navigation?.showCustomBottomSheet(
            image = R.drawable.ic_img_email,
            title = title,
            message = message,
            bt2Title = getString(R.string.entendi),
            bt2Callback = {
                comeBack()
                false
            },
            closeCallback = {
                comeBack()
            }
        )
    }

    override fun onSuccessDeleteInvite(invitations: Int) {
        val message = resources.getQuantityString(
            R.plurals.access_delete_invite_success_message,
            invitations
        )

        Toast(requireContext()).showSuccess(
            message = message,
            activity = requireActivity()
        )

        if (users.isEmpty().not()) {
            val usersDeleted = usersSelected
            users.removeAll(usersDeleted.toSet())
            usersSelected.removeAll(usersDeleted.toSet())
            setNumberSelectedUsers()
            adapter?.notifyDataSetChanged()
        } else {
            requireActivity().onBackPressed()
        }
    }

    override fun showError(error: ErrorMessage?) {
        navigation?.showCustomBottomSheet(
            image = R.drawable.ic_generic_error_image,
            title = getString(R.string.access_manager_generic_error_title),
            message = requireActivity().getErrorMessage(
                error,
                getString(R.string.access_manager_generic_error_message_default)
            ),
            bt2Title = getString(R.string.back),
            bt2Callback = {
                comeBack()
                false
            },
            closeCallback = {
                comeBack()
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onUserSelected(user: Item) {
        if (usersSelected.remove(user).not())
            usersSelected.add(user)

        enableButtons()
        setNumberSelectedUsers()
        adapter?.notifyDataSetChanged()
    }

    override fun onBackButtonClicked(): Boolean {
        comeBack()
        return super.onBackButtonClicked()
    }

    private fun showDeleteAlert() {
        CustomAlertDialogFacilita(requireActivity(), cancelable = false)
            .setOnclickListenerBottom {
                mfaRouteHandler.runWithMfaToken {
                    validationTokenWrapper.generateOtp(
                        onResult = { otpCode ->
                            presenter.onDeleteInvite(usersSelected, otpCode)
                        }
                    )
                }
            }.show(
                getString(R.string.access_manager_delete_invite),
                getString(R.string.access_manager_delete_alert_message),
                getString(R.string.cancelar),
                getString(R.string.access_manager_delete_button)
            )
    }
}