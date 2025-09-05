package br.com.mobicare.cielo.accessManager.customProfile.selectProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import br.com.cielo.libflue.util.EMPTY
import br.com.cielo.libflue.util.ZERO
import br.com.cielo.libflue.util.extensions.gone
import br.com.cielo.libflue.util.extensions.visible
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.accessManager.assignedUsers.AccessManagerAssignedUsersContract
import br.com.mobicare.cielo.accessManager.customProfile.AccessManagerCustomProfileContract
import br.com.mobicare.cielo.accessManager.customProfile.AccessManagerCustomProfilePresenter
import br.com.mobicare.cielo.accessManager.customProfile.detail.AccessManagerCustomProfileDetailFragment
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileDetailResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerCustomProfileResponse
import br.com.mobicare.cielo.accessManager.model.AccessManagerUser
import br.com.mobicare.cielo.accessManager.model.CustomProfileResources
import br.com.mobicare.cielo.commons.constants.SEPARATOR_POINT
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.CardAccessManagerCustomProfileSelectItemBinding
import br.com.mobicare.cielo.databinding.FragmentAccessManagerCustomProfileSelectBinding
import br.com.mobicare.cielo.extensions.orZero
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class AccessManagerCustomProfileSelectBottomSheet : BottomSheetDialogFragment(),
    AccessManagerCustomProfileContract.View {

    private var _binding: FragmentAccessManagerCustomProfileSelectBinding? = null
    private val binding get() = _binding

    private val presenter: AccessManagerCustomProfilePresenter by inject {
        parametersOf(this)
    }

    lateinit var listener: AccessManagerAssignedUsersContract.Listener
    lateinit var user: AccessManagerUser
    private var selectedProfileId: String = EMPTY
    private var selectedProfileName: String = EMPTY

    companion object {
        fun onCreate(
            user: AccessManagerUser,
            listener: AccessManagerAssignedUsersContract.Listener
        ) = AccessManagerCustomProfileSelectBottomSheet().apply {
            this.user = user
            this.listener = listener
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() },
            isFullScreen = true
        )
        _binding = FragmentAccessManagerCustomProfileSelectBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.behavior?.isDraggable = false
        setupListeners()
    }

    private fun setupListeners() {
        binding?.apply{
            btBackArrow.setOnClickListener {
                openDetailsBottomSheet(user, EMPTY, EMPTY)
            }

            detailsButton.setOnClickListener {
                detailsButton.isEnabled = false
                presenter.getDetailCustomProfile(selectedProfileId)
            }

            selectProfileButton.setOnClickListener {
                openDetailsBottomSheet(user, selectedProfileId, selectedProfileName)
            }
        }
    }

    private fun openDetailsBottomSheet(userSelected: AccessManagerUser, profileIdSelected: String, profileNameSelected: String){
        listener.openDetailsBottomSheet(userSelected, profileIdSelected, profileNameSelected)
        dismiss()
    }

    private fun returnResources(resources: List<CustomProfileResources>?): String{
       var listResourcesName: String = EMPTY

        resources?.forEach {
            listResourcesName += it.resourceName + SEPARATOR_POINT
        }

        return listResourcesName.dropLast(SEPARATOR_POINT.length)
    }

    override fun onResume() {
        super.onResume()
        presenter.onResume()
        presenter.getCustomActiveProfiles()
        binding?.selectProfileButton?.isButtonEnabled = selectedProfileId.isNotEmpty()
    }

    override fun onPause() {
        super.onPause()
        presenter.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun showLoading() {
        binding?.progress?.visible()
    }

    override fun hideLoading() {
        binding?.progress?.gone()
    }

    private fun setSelectedProfile(profileId: String?, profileName: String?){
        selectedProfileId = profileId.toString()
        selectedProfileName = profileName.toString()
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
                                binding?.selectProfileButton?.isButtonEnabled = itRadioButton.isChecked

                                clCardContainer.background = ContextCompat.getDrawable(
                                    requireContext(), getColorInt(itRadioButton.isChecked)
                                )
                            }
                        }
                    }
                }
            }else{
                showErrorEmptyProfiles(user)
            }
        }
    }

    private fun getColorInt(isChecked: Boolean): Int{
        return if (isChecked)
            R.drawable.background_border_blue
        else
            R.drawable.background_gray_c5ced7
    }

    override fun showErrorProfile() {
        hideLoading()
        binding?.detailsButton?.isEnabled = true
        listener.showErrorProfile()
        dismiss()
    }
    override fun showErrorEmptyProfiles(userSelected: AccessManagerUser?) {
        hideLoading()
        userSelected?.let { listener.showErrorEmptyProfiles(it) }
        dismiss()
    }
    override fun showCustomUsers(customUsers: List<AccessManagerUser>?) {
        hideLoading()
    }
    override fun getDetailSuccess(userDetail: AccessManagerCustomProfileDetailResponse) {
        hideLoading()
        binding?.detailsButton?.isEnabled = true
        AccessManagerCustomProfileDetailFragment.onCreate(userDetail)
            .show(this.childFragmentManager, AccessManagerCustomProfileDetailFragment::class.java.simpleName)
    }
}