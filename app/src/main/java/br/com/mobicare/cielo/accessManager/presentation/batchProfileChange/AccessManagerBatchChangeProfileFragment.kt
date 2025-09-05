package br.com.mobicare.cielo.accessManager.presentation.batchProfileChange

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersContract
import br.com.mobicare.cielo.accessManager.customProfile.selectProfile.AccessManagerCustomProfileSelectBottomSheet
import br.com.mobicare.cielo.accessManager.presentation.batchProfileChange.adapter.AccessManagerBatchChangeProfileAdapter
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.accessManager.utils.AccessManagerBatchChangeProfileUiState
import br.com.mobicare.cielo.accessManager.utils.AccessManagerConstants.EMPTY_LIST
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.dialog.BottomSheetValidationTokenWrapper
import br.com.mobicare.cielo.commons.utils.showGenericSnackBar
import br.com.mobicare.cielo.commons.utils.viewBinding
import br.com.mobicare.cielo.databinding.FragmentAccessManagerBatchChangeProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccessManagerBatchChangeProfileFragment : BaseFragment(), CieloNavigationListener {

    private val binding: FragmentAccessManagerBatchChangeProfileBinding by viewBinding()
    private val viewModel: AccessManagerBatchChangeProfileViewModel by viewModel()
    private val args: AccessManagerBatchChangeProfileFragmentArgs by navArgs()
    private lateinit var navigation: CieloNavigation
    private lateinit var adapter: AccessManagerBatchChangeProfileAdapter
    private lateinit var userList: MutableList<AccessManagerUser>

    private val validationTokenWrapper: BottomSheetValidationTokenWrapper by lazy {
        BottomSheetValidationTokenWrapper(requireActivity().supportFragmentManager)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.showTechnicalProfile()
        viewModel.getCustomActiveProfiles(args.customProfileIsEnabled)
        setupNavigation()
        setupListeners()
        setupAdapter()
        setupObservables()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding.apply {
            btBackArrow.setOnClickListener {
                findNavController().popBackStack()
            }

            btContinue.setOnClickListener {
                openProfileChangeBottomSheet(args.profileId, args.profileName)
            }
        }
    }

    private fun callAssignRole(selectedRole: String) {
        validationTokenWrapper.generateOtp(showAnimation = true) { otpCode ->
            val usersIdList = mutableListOf<String>()
            userList.forEach { user ->
                user.id?.let { userId ->
                    usersIdList.add(userId)
                }
            }
            viewModel.assignRole(usersIdList, selectedRole, otpCode)
        }
    }

    private fun setupAdapter() {
        userList = args.usersList.toMutableList()
        adapter = AccessManagerBatchChangeProfileAdapter(
            usersList = userList
        ) { accessManagerUser ->
            showAlertBottomSheet(accessManagerUser)
        }

        binding.apply {
            rvUsers.layoutManager = LinearLayoutManager(context)
            rvUsers.adapter = adapter
        }
    }

    private fun setupObservables() {
        viewModel.accessManagerBatchChangeProfileLiveData.observe(viewLifecycleOwner) { uiState ->
            when(uiState) {
                is AccessManagerBatchChangeProfileUiState.AssignRoleSuccess -> {
                    validationTokenWrapper.playAnimationSuccess(
                        object: BottomSheetValidationTokenWrapper.CallbackValidateToken {
                            override fun callbackTokenSuccess() {
                                showSuccessSnackBar()
                            }
                        }
                    )
                }
                is AccessManagerBatchChangeProfileUiState.AssignRoleError -> {
                    validationTokenWrapper.playAnimationError(callbackValidateToken =
                    object : BottomSheetValidationTokenWrapper.CallbackValidateToken {
                        override fun callbackTokenError() {
                            showErrorBottomSheet()
                        }
                    })
                }
            }
        }
    }

    private fun openProfileChangeBottomSheet(profileId: String, profileName: String) {
        val idProfile = profileId.ifEmpty { args.profileId }
        val nameProfile = profileName.ifEmpty { args.profileName }

        BatchChangeProfileBottomSheet.onCreate(
            isShowTechnical = viewModel.showTechnicalProfile(),
            isShowCustomProfiles = viewModel.showCustomProfiles(),
            selectedProfileId = idProfile,
            selectedProfileName = nameProfile,
            currentRole = userList[ZERO].profile?.id.toString(),
            roleDescription = userList[ZERO].mainRoleDescription(),
            changedCustomProfile = args.profileId != idProfile,
            saveCallback = {
                callAssignRole(it)
            },
            openCustomProfile = {
                openCustomProfileBottomSheet()
            }
        ).show(childFragmentManager, AccessManagerBatchChangeProfileFragment::class.java.simpleName)
    }

    private fun openCustomProfileBottomSheet() {
        val listener = object : AccessManagerAssignedUsersContract.Listener {
            override fun openDetailsBottomSheet(
                userSelected: AccessManagerUser,
                profileIdSelected: String,
                profileNameSelected: String
            ) {
                openProfileChangeBottomSheet(profileIdSelected, profileNameSelected)
            }

            override fun showErrorProfile() {
                showErrorBottomSheet()
            }

            override fun showErrorEmptyProfiles(userSelected: AccessManagerUser) {
                showErrorBottomSheet()
            }
        }

        AccessManagerCustomProfileSelectBottomSheet.onCreate(userList[ZERO], listener)
            .show(
                this.childFragmentManager,
                AccessManagerBatchChangeProfileFragment::class.java.simpleName
            )
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAlertBottomSheet(accessManagerUser: AccessManagerUser) {
        navigation.showCustomBottomSheet(
            image = R.drawable.img_dark_07,
            title = getString(R.string.access_manager_batch_profile_change_bs_title),
            message = getString(R.string.access_manager_batch_profile_change_bs_subtitle),
            bt2Title = getString(R.string.confirm),
            bt2Callback = {
                showCustomSnackBar()
                userList.remove(accessManagerUser)
                adapter.notifyDataSetChanged()
                false
            },
            titleBlack = true,
            isCancelable = true,
        )
    }

    private fun showErrorBottomSheet() {
        navigation.showCustomBottomSheet(
            image = R.drawable.img_dark_07,
            title = getString(R.string.batch_profile_change_error_bs_title),
            message = getString(R.string.batch_profile_change_error_bs_subtitle),
            bt2Title = getString(R.string.back),
            bt2Callback = {
                openProfileChangeBottomSheet(args.profileId, args.profileName)
                true
            },
            titleBlack = true,
            isCancelable = true,
        )
    }

    private fun showCustomSnackBar() {
        showGenericSnackBar(
            binding.btContinue,
            getString(R.string.access_manager_batch_profile_change_snackbar),
            R.drawable.ic_check,
            THREE
        ) {
            if (userList.isEmpty() && this.isAttached()) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(EMPTY_LIST, true)
                findNavController().popBackStack()
            }
        }
    }

    private fun showSuccessSnackBar() {
        showGenericSnackBar(
            binding.btContinue,
            getString(R.string.assigned_user_bs_assign_role_success),
            R.drawable.ic_check,
            THREE
        ) {
            if (this.isAttached()) {
                findNavController().popBackStack(R.id.accessManagerHomeFragment, false)
            }
        }
    }

}