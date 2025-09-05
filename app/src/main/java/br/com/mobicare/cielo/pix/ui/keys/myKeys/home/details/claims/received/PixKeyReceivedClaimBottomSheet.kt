package br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.claims.received

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.databinding.PixKeyReceivedClaimBottomSheetBinding
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.pix.constants.EMPTY
import br.com.mobicare.cielo.pix.constants.PIX_MY_KEY_DETAILS
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum.*
import br.com.mobicare.cielo.pix.enums.PixKeyTypeEnum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PixKeyReceivedClaimBottomSheet : BottomSheetDialogFragment() {

    private var myKey: MyKey? = null
    private var listener: PixKeyReceivedClaimContract.View? = null
    private var binding: PixKeyReceivedClaimBottomSheetBinding? = null

    companion object {
        fun onCreate(
            keyDetails: MyKey,
            listener: PixKeyReceivedClaimContract.View,
        ) = PixKeyReceivedClaimBottomSheet().apply {
            this.listener = listener
            this.arguments = Bundle().apply {
                this.putParcelable(PIX_MY_KEY_DETAILS, keyDetails)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupBottomSheet(dialog = dialog,
            action = { dismiss() }
        )
        binding = PixKeyReceivedClaimBottomSheetBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getKeyInfo()
        setupView()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    private fun getKeyInfo() {
        myKey = arguments?.getParcelable(PIX_MY_KEY_DETAILS)
    }

    private fun setupView() {
        binding?.apply {
            myKey?.let { itKey ->
                tvKeyValue.text = itKey.key?.let { getFormattedKey(it, itKey.keyType) }
                tvKeyName.text = getKeyTypeName(itKey.keyType)
                ivKeyType.setBackgroundResource(getIconKeyPix(itKey.keyType))

                if (itKey.claimType == PORTABILITY.name) {
                    setupTitleAndDeadlineText(
                        title = getString(R.string.bs_details_portability_requested),
                        isPortability = true
                    )
                } else if (itKey.claimType == OWNERSHIP.name) {
                    setupTitleAndDeadlineText(
                        title = getString(R.string.bs_details_claim_requested)
                    )
                }

                setupListeners(itKey)
            }
        }
    }

    private fun setupTitleAndDeadlineText(title: String, isPortability: Boolean = false) {
        binding?.tvTitle?.text = title
        setupDeadlineText(isPortability)
    }

    private fun setupDeadlineText(isPortability: Boolean) {
        myKey?.claimDetail?.resolutionLimitDate?.let {
            val clearDate = it.clearDate().formatterDate(LONG_TIME_NO_UTC)

            binding?.tvDeadline?.text = if (isPortability) {
                getString(
                    R.string.bs_details_portability_requested_subtitle_institution_name,
                    clearDate,
                    myKey?.claimDetail?.claimantIspbName ?: EMPTY
                )
            } else {
                getString(
                    R.string.bs_details_portability_requested_subtitle,
                    clearDate
                )
            }
        }
    }

    private fun setupListeners(key: MyKey) {
        binding?.btReleaseKey?.setOnClickListener {
            dismiss()
            listener?.releaseKey(key)
        }

        binding?.btKeepKey?.setOnClickListener {
            dismiss()
            ownershipValidation(key = key, onAction = {
                listener?.keepKey(key)
            })
        }
    }

    private fun ownershipValidation(key: MyKey, onAction: () -> Unit) {
        val isOwnershipType = key.keyType == PixKeyTypeEnum.PHONE.name
                || key.keyType == PixKeyTypeEnum.EMAIL.name

        if (key.claimType == OWNERSHIP.name
            && isOwnershipType
        ) {
            val type = if (key.keyType == PixKeyTypeEnum.PHONE.name)
                PixKeyTypeEnum.PHONE
            else PixKeyTypeEnum.EMAIL
            listener?.ownershipValidation(key, type)
        } else
            onAction.invoke()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}