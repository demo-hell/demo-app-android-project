package br.com.mobicare.cielo.accessManager.assignedUsers

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.AccessManagerNavigationActivity
import br.com.mobicare.cielo.accessManager.UnlinkUserReason
import br.com.mobicare.cielo.accessManager.assignedUsers.details.AssignedUserDetailBottomSheet
import br.com.mobicare.cielo.accessManager.assignedUsers.details.AssignedUserRemoveBottomSheet
import br.com.mobicare.cielo.accessManager.customProfile.selectProfile.AccessManagerCustomProfileSelectBottomSheet
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.processErrorMessage
import br.com.mobicare.cielo.commons.utils.showSuccess
import br.com.mobicare.cielo.databinding.FragmentAccessManagerAssignedUsersBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ADMIN
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.ANALYST
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.READER
import br.com.mobicare.cielo.login.domains.entities.UserObj.MainRole.TECHNICAL
import br.com.cielo.libflue.util.EMPTY
import br.com.mobicare.cielo.accessManager.assignedUsers.adapter.AccessManagerAssignedUserAdapter
import br.com.mobicare.cielo.accessManager.utils.AccessManagerConstants.EMPTY_LIST
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.utils.viewBinding
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.net.HttpURLConnection

class AccessManagerAssignedUsersFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerAssignedUsersContract.View, AccessManagerAssignedUsersContract.Listener {

    private var navigation: CieloNavigation? = null
    private val binding: FragmentAccessManagerAssignedUsersBinding by viewBinding()
    private val args: AccessManagerAssignedUsersFragmentArgs by navArgs()
    private var userList: ArrayList<AccessManagerUser> = arrayListOf()
    private var selectedUserList = mutableListOf<AccessManagerUser>()
    private lateinit var usersAdapter: AccessManagerAssignedUserAdapter
    private var userIdToUnlink: String = EMPTY
    private var reasonToUnlinkUser: UnlinkUserReason? = null
    private val presenter: AccessManagerAssignedUsersPresenter by inject {
        parametersOf(this)
    }
    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupText()
        setupListeners()
        setUserList(args.usersList.toMutableList())
        setupRecyclerView()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        presenter.onResume()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(EMPTY_LIST)?.observe(viewLifecycleOwner) {result ->
            selectedUserList.clear()
            usersAdapter.notifyDataSetChanged()
        }
        enableButton()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun showLoading() {
        binding.containerProgressBar.visible()
    }

    override fun hideLoading() {
        binding.containerProgressBar.gone()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupText() {
        binding.apply {
            when (args.profileId) {
                ADMIN -> {
                    tvTitle.text = getString(R.string.access_manager_assigned_users_admin_title)
                    tvSubtitle.text = getString(R.string.access_manager_assigned_users_admin_desc)
                }
                READER -> {
                    tvTitle.text = getString(R.string.access_manager_assigned_users_reader_title)
                    tvSubtitle.text = getString(R.string.access_manager_assigned_users_reader_desc)
                }
                ANALYST -> {
                    tvTitle.text = getString(R.string.access_manager_assigned_users_analyst_title)
                    tvSubtitle.text = getString(R.string.access_manager_assigned_users_analyst_desc)
                }
                TECHNICAL -> {
                    tvTitle.text = getString(R.string.access_manager_assigned_users_technical_title)
                    tvSubtitle.text =
                        getString(R.string.access_manager_assigned_users_technical_desc)
                }
                else -> {
                    tvTitle.text = getString(
                        R.string.access_manager_assigned_users_custom_title,
                        args.profileName
                    )
                    tvSubtitle.text = getString(R.string.access_manager_assigned_users_custom_desc)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupListeners() {
        binding.apply {
            btBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            btSelectAll.setOnClickListener {
                val wasNotFull = selectedUserList.size != userList.size

                selectedUserList.clear()
                btSelectAll.text = getString(R.string.access_manager_assign_role_select_all)

                if (wasNotFull) {
                    selectedUserList.addAll(userList)
                    btSelectAll.text = getString(R.string.access_manager_assign_role_clear_all)
                }

                enableButton()
                usersAdapter.notifyDataSetChanged()
            }

            btContinue.setOnClickListener {
                if (selectedUserList.size == ONE) {
                    openDetailsBottomSheet(selectedUserList[0], args.profileId, args.profileName)
                } else {
                    findNavController().navigate(
                        AccessManagerAssignedUsersFragmentDirections
                            .actionAccessManagerAssignedUsersFragmentToAccessManagerBatchChangeProfileFragment2(
                                selectedUserList.toTypedArray(),
                                args.profileId,
                                args.profileName,
                                args.customProfileIsEnabled
                            )
                    )
                }
            }
        }
    }

    override fun onUserUnlinked(userId: String) {
        validationTokenWrapper.playAnimationSuccess(
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenSuccess() {
                    onUserUnlinkedSuccessful(userId)
                }
            }
        )
    }

    private fun onUserUnlinkedSuccessful(userId: String) {
        Toast(requireContext()).showSuccess(
            message = getString(R.string.assigned_users_success_toast),
            activity = requireActivity()
        )
        resetAssignRoleFields()
        updateUsersList(userId)
    }

    private fun resetAssignRoleFields() {
        userIdToUnlink = EMPTY
        reasonToUnlinkUser = null
    }

    override fun onUserProfileTypeUpdated(userId: String) {
        updateUsersList(userId)
    }

    private fun updateUsersList(userId: String) {
        try {
            userList = ArrayList(userList.filterNot {
                it.id == userId
            })
            usersAdapter.updateList(userList)
        } catch (t: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(t)
            activity?.onBackPressed()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecyclerView() {
        usersAdapter = AccessManagerAssignedUserAdapter(
            userList,
            selectedUserList
        ) { accessManagerUser ->
            if (selectedUserList.remove(accessManagerUser).not()) {
                selectedUserList.add(accessManagerUser)
            }

            enableButton()
            usersAdapter.notifyDataSetChanged()
        }

        binding.apply {
            rvUsers.layoutManager = LinearLayoutManager(context)
            rvUsers.adapter = usersAdapter
        }
    }

    override fun openDetailsBottomSheet(
        item: AccessManagerUser,
        profileId: String,
        profileName: String
    ) {
        val idProfile = if (profileId.isEmpty())
            args.profileId
        else
            profileId

        val nameProfile = if (profileName.isEmpty())
            args.profileName
        else
            profileName

        AssignedUserDetailBottomSheet.onCreate(
            item,
            this,
            this,
            presenter.canUserBeRemoved(item.id.toString()),
            idProfile,
            nameProfile,
            args.customProfileIsEnabled,
            args.profileId != idProfile
        )
            .show(this.childFragmentManager, AssignedUserDetailBottomSheet::class.java.simpleName)
    }

    override fun onRemoveClicked(item: AccessManagerUser) {
        val dialog = AssignedUserRemoveBottomSheet.onCreate(item, this)
        dialog.isDragDisabled = true
        dialog.show(this.childFragmentManager, AssignedUserDetailBottomSheet::class.java.simpleName)
    }

    override fun openSelectCustomProfile(item: AccessManagerUser) {
        AccessManagerCustomProfileSelectBottomSheet.onCreate(item, this)
            .show(
                this.childFragmentManager,
                AccessManagerCustomProfileSelectBottomSheet::class.java.simpleName
            )
    }

    override fun onRemoveConfirmed(userId: String, reason: UnlinkUserReason) {
        userIdToUnlink = userId
        reasonToUnlinkUser = reason
        unlinkUser(true)
    }

    override fun unlinkUser(isAnimation: Boolean) {
        validationTokenWrapper.generateOtp(showAnimation = isAnimation) { otpCode ->
            reasonToUnlinkUser?.let {
                presenter.unlinkUser(
                    userIdToUnlink,
                    it,
                    otpCode
                )
            }
        }
    }

    override fun showError(error: ErrorMessage?) {
        validationTokenWrapper.playAnimationError(error,
            object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                override fun callbackTokenError() {
                    if (error?.code != HttpURLConnection.HTTP_FORBIDDEN.toString()) {
                        (activity as AccessManagerNavigationActivity).showErrorBottomSheet(
                            textButton = getString(R.string.cancelar),
                            error = processErrorMessage(
                                error,
                                getString(R.string.assigned_user_bs_error),
                                getString(R.string.assigned_user_bs_error)
                            ),
                            title = getString(R.string.assigned_user_bs_error_title),
                            isFullScreen = false
                        )
                    }
                }
            })
    }

    override fun onClickSecondButtonError() {
        unlinkUser(true)
    }

    override fun setUserList(userList: List<AccessManagerUser>) {
        this.userList = ArrayList(userList)
    }

    override fun showErrorProfile() {
        hideLoading()
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_dark_07,
                    title = getString(R.string.access_manager_custom_profiles_title_error),
                    message = getString(R.string.access_manager_generic_error_message_default),
                    bt2Title = getString(R.string.back),
                    bt2Callback = {
                        comeBack()
                        false
                    },
                    closeCallback = {
                        comeBack()
                    },
                    titleBlack = true,
                    isCancelable = false,
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    override fun showErrorEmptyProfiles(userSelected: AccessManagerUser) {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_dark_07,
                    title = getString(R.string.access_manager_custom_profiles_title_error_empty),
                    message = getString(R.string.access_manager_custom_profiles_message_error_empty),
                    bt2Title = getString(R.string.back),
                    bt2Callback = {
                        openDetailsBottomSheet(userSelected, args.profileId, args.profileName)
                        false
                    },
                    closeCallback = {
                        openDetailsBottomSheet(userSelected, args.profileId, args.profileName)
                    },
                    titleBlack = true,
                    isCancelable = false,
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun comeBack() {
        findNavController().popBackStack()
    }

    private fun enableButton() {
        binding.btContinue.isButtonEnabled = selectedUserList.isNotEmpty()
    }
}