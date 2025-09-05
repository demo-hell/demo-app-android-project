package br.com.mobicare.cielo.accessManager.presentation.batchProfileChange

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.THREE
import br.com.mobicare.cielo.commons.constants.TWO
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.BatchChangeProfileBottomSheetBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.login.domains.entities.UserObj
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BatchChangeProfileBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BatchChangeProfileBottomSheetBinding

    private var isShowTechnicalProfile: Boolean = false
    private var isShowCustomProfiles: Boolean = false
    private var saveCallback: ((String) -> Unit)? = null
    private var openCustomProfile: (() -> Unit)? = null
    private var selectedProfileId: String = EMPTY
    private var selectedProfileName: String = EMPTY
    private var selectedRole: String = EMPTY
    private var currentRole: String = EMPTY
    private var roleDescription: String = EMPTY
    private var changedCustomProfile: Boolean = false

    companion object {
        fun onCreate(
            isShowTechnical: Boolean,
            isShowCustomProfiles: Boolean,
            selectedProfileId: String,
            selectedProfileName: String,
            currentRole: String,
            roleDescription: String,
            changedCustomProfile: Boolean,
            saveCallback: ((String) -> Unit),
            openCustomProfile: (() -> Unit)
        ) = BatchChangeProfileBottomSheet().apply {
            this.isShowTechnicalProfile = isShowTechnical
            this.isShowCustomProfiles = isShowCustomProfiles
            this.roleDescription = roleDescription
            this.selectedProfileId = selectedProfileId
            this.selectedProfileName = selectedProfileName
            this.currentRole = currentRole
            this.changedCustomProfile = changedCustomProfile
            this.saveCallback = saveCallback
            this.openCustomProfile = openCustomProfile
            this.selectedRole = currentRole
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        binding = BatchChangeProfileBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    private fun setupView() {
        binding.apply {
            rbTechnical.visible(isShowTechnicalProfile)
            btnSelectCustomProfile.visible(isShowCustomProfiles)
            setBtnSaveChangesIsEnabled(selectedRole != currentRole)

            val radioButtonId = when (currentRole) {
                UserObj.ADMIN, UserObj.MASTER -> R.id.rbAdmin
                UserObj.READER -> R.id.rbReader
                UserObj.ANALYST -> R.id.rbAnalyst
                UserObj.TECHNICAL -> R.id.rbTechnical
                else -> R.id.rbCustomProfile
            }

            rgProfileType.check(radioButtonId)

            if (isCustomProfile(selectedProfileId)) {
                rbCustomProfile.visible()
                rbCustomProfile.text = selectedProfileName
                rgProfileType.check(R.id.rbCustomProfile)
                setBtnSaveChangesIsEnabled(changedCustomProfile)
                selectedRole = selectedProfileId
            } else {
                rbCustomProfile.gone()
            }

            binding.rgProfileType.setOnCheckedChangeListener { radioGroup, _ ->
                val selectedRoleView =
                    radioGroup.findViewById<AppCompatRadioButton>(radioGroup.checkedRadioButtonId)
                selectedRole = when (radioGroup.indexOfChild(selectedRoleView)) {
                    ZERO -> UserObj.ADMIN
                    ONE -> UserObj.READER
                    TWO -> UserObj.ANALYST
                    THREE -> UserObj.TECHNICAL
                    FOUR -> selectedProfileId
                    else -> roleDescription
                }
                setBtnSaveChangesIsEnabled(selectedRole != currentRole)
            }

            btnSaveChanges.setOnClickListener {
                this@BatchChangeProfileBottomSheet.dismiss()
                saveCallback?.invoke(selectedRole)
            }

            btnSelectCustomProfile.setOnClickListener {
                this@BatchChangeProfileBottomSheet.dismiss()
                openCustomProfile?.invoke()
            }
        }
    }

    private fun setBtnSaveChangesIsEnabled(canSave: Boolean) {
        binding.apply {
            btnSaveChanges.isButtonEnabled = canSave
        }
    }

    private fun isCustomProfile(profileId: String?): Boolean {
        return when (profileId) {
            UserObj.ADMIN, UserObj.MASTER, UserObj.ANALYST, UserObj.TECHNICAL, UserObj.READER -> false
            else -> true
        }
    }
}