package br.com.mobicare.cielo.accessManager.customProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.customProfile.detail.AccessManagerCustomProfileDetailFragment
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileDetailResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.accessManager.model.CustomProfileResources
import br.com.mobicare.cielo.commons.constants.SEPARATOR_POINT
import br.com.mobicare.cielo.commons.navigation.CieloNavigation
import br.com.mobicare.cielo.commons.navigation.CieloNavigationListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.databinding.CardAccessManagerCustomProfileSelectItemBinding
import br.com.mobicare.cielo.databinding.FragmentAccessManagerCustomProfileBinding
import br.com.mobicare.cielo.extensions.doWhenResumed
import br.com.mobicare.cielo.extensions.orZero
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AccessManagerCustomProfileFragment : BaseFragment(), CieloNavigationListener,
    AccessManagerCustomProfileContract.View {

    private var binding: FragmentAccessManagerCustomProfileBinding? = null
    val args: AccessManagerCustomProfileFragmentArgs by navArgs()
    private var _userList: ArrayList<AccessManagerUser> = arrayListOf()
    private var navigation: CieloNavigation? = null
   var viewHolder: View? = null

    private val presenter: AccessManagerCustomProfilePresenter by inject {
        parametersOf(this)
    }

    private var selectedProfileId: String = EMPTY
    private var selectedProfileName: String = EMPTY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentAccessManagerCustomProfileBinding.inflate(
        inflater,
        container,
        false
    ).also{
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupListeners()
        setUserList(args.usersList.toMutableList())
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.getCustomActiveProfiles()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun showLoading() {
        binding?.progress?.visible()
    }

    override fun hideLoading() {
        binding?.progress?.gone()
    }

    private fun setupNavigation() {
        if (requireActivity() is CieloNavigation) {
            navigation = requireActivity() as CieloNavigation
            navigation?.setNavigationListener(this)
        }
    }

    private fun setupListeners() {
        binding?.apply{
            btBackArrow.setOnClickListener {
                comeBack()
            }

            detailsButton.setOnClickListener {
                detailsButton.isEnabled = false
                presenter.getDetailCustomProfile(selectedProfileId)
            }

            seeUsersButton.setOnClickListener {
                if (getTotalUsers(selectedProfileId) > 0){
                    presenter.getCustomUsers(selectedProfileId)
                }else{
                    showErrorEmptyUsers()
                }
            }
        }
    }

    private fun getColorInt(isChecked: Boolean): Int{
        return if (isChecked)
            R.drawable.background_border_blue
        else
            R.drawable.background_gray_c5ced7
    }

    private fun setUserList(userList: List<AccessManagerUser>) {
        _userList = ArrayList(userList)
    }

    private fun setBtnSelectProfileIsEnabled(canEnabled: Boolean) {
        context?.let {
            val bgId = if (canEnabled) {
                R.color.brand_400
            } else {
                R.color.display_200
            }

            binding?.apply{
                seeUsersButton.isEnabled = canEnabled
                seeUsersButton.background = ContextCompat.getDrawable(it, bgId)

                detailsButton.isEnabled = canEnabled
                detailsButton.setTextColor(ContextCompat.getColor(it, bgId))
            }}
    }

    private fun returnResources(resources: List<CustomProfileResources>?): String{
        var listResourcesName: String = EMPTY

        resources?.forEach {
            listResourcesName += it.resourceName + SEPARATOR_POINT
        }

        return listResourcesName.dropLast(SEPARATOR_POINT.length)
    }
    private fun setSelectedProfile(profileId: String?, profileName: String?){
        selectedProfileId = profileId.toString()
        selectedProfileName = profileName.toString()
    }

    private fun getTotalUsers(idProfile: String?): Int {
       return _userList.filter{it.profile?.id == idProfile}.size
    }

    override fun showCustomProfiles(customProfiles: List<AccessManagerCustomProfileResponse>?) {
        binding?.apply{
            layoutRowProfiles.removeAllViews()
            if (customProfiles?.size.orZero > ZERO){
                customProfiles?.forEach { itModel ->
                    val itemBinding = CardAccessManagerCustomProfileSelectItemBinding.inflate(LayoutInflater.from(requireContext()))
                    itemBinding.apply{
                        layoutRowProfiles.addView(itemBinding.root)
                        tvTitle.text = itModel.name
                        tvSubTitle.text = returnResources(itModel.resources)
                        tvTotalQuantity.text = resources.getQuantityString(
                            R.plurals.access_manager_custom_profile_total_quantitiy,
                            getTotalUsers(itModel.id), getTotalUsers(itModel.id)
                        )

                        if (itModel.name?.equals(selectedProfileName) == true){
                            rbCustomProfile.isChecked = true
                            setBtnSelectProfileIsEnabled(rbCustomProfile.isChecked)

                            clCardContainer.background = ContextCompat.getDrawable(
                                requireContext(), getColorInt(rbCustomProfile.isChecked)
                            )
                        }

                        root.setOnClickListener { itView ->
                            for (idx in ZERO until layoutRowProfiles.childCount) {
                                layoutRowProfiles.getChildAt(idx)?.let { itChildView ->
                                    itChildView.findViewById<RadioButton>(R.id.rbCustomProfile)?.let { itRadioButton ->
                                        itRadioButton.isChecked = false
                                    }
                                    itChildView.findViewById<ConstraintLayout>(R.id.clCardContainer)?.let{ itConstraintLayout ->
                                        itConstraintLayout.background = ContextCompat.getDrawable(
                                            requireContext(), R.drawable.background_gray_c5ced7
                                        )
                                    }
                                }
                            }
                            itView.findViewById<RadioButton>(R.id.rbCustomProfile)?.let { itRadioButton ->
                                itRadioButton.isChecked = true
                                setSelectedProfile(itModel.id.toString(), itModel.name.toString())
                                setBtnSelectProfileIsEnabled(itRadioButton.isChecked)

                                clCardContainer.background = ContextCompat.getDrawable(
                                    requireContext(), getColorInt(itRadioButton.isChecked)
                                )
                            }
                        }
                    }
                }
            }else{
                showErrorEmptyProfiles(null)
            }
        }
    }

    override fun showCustomUsers(customUsers: List<AccessManagerUser>?) {
        findNavController().navigate(
            AccessManagerCustomProfileFragmentDirections
                .actionAccessManagerCustomProfileFragmentToAccessManagerAssignedUsersFragment(
                    customUsers?.toTypedArray() ?: arrayOf(),
                    selectedProfileId,
                    selectedProfileName,
                    true
                )
        )
    }

    override fun getDetailSuccess(userDetail: AccessManagerCustomProfileDetailResponse) {
        hideLoading()
        viewHolder?.isEnabled = true
        AccessManagerCustomProfileDetailFragment.onCreate(userDetail)
            .show(this.childFragmentManager, AccessManagerCustomProfileDetailFragment::class.java.simpleName)
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
                    titleBlack = true,
                    isCancelable = false
                ) ?: baseLogout()
            },
            errorCallback = { baseLogout() }
        )
    }

    private fun showErrorEmptyUsers() {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_dark_07,
                    title = getString(R.string.access_manager_custom_profiles_empty_users_title),
                    message = getString(R.string.access_manager_custom_profiles_empty_users_message),
                    bt2Title = getString(R.string.back),
                    bt2Callback = {
                        false
                    },
                    titleBlack = true,
                    isCancelable = true
                )
            }
        )
    }

    override fun showErrorEmptyProfiles(userSelected: AccessManagerUser?) {
        doWhenResumed(
            action = {
                navigation?.showCustomBottomSheet(
                    image = R.drawable.img_dark_07,
                    title = getString(R.string.access_manager_custom_profiles_title_error_empty),
                    message = getString(R.string.access_manager_custom_profiles_message_error_empty),
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

    private fun comeBack() {
        findNavController().popBackStack()
    }
}