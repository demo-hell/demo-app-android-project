package br.com.mobicare.cielo.pix.ui.keys.myKeys.home.details.claims.sent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.utils.*
import br.com.mobicare.cielo.extensions.clearDate
import br.com.mobicare.cielo.extensions.formatterDate
import br.com.mobicare.cielo.pix.constants.PIX_MY_KEY_DETAILS
import br.com.mobicare.cielo.pix.domain.MyKey
import br.com.mobicare.cielo.pix.enums.PixClaimTypeEnum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_pix_claim_key_details_bottom_sheet.*

class PixKeySentClaimDetailsBottomSheet : BottomSheetDialogFragment() {

    private var key: MyKey? = null
    private var listener: PixKeySentClaimDetailsContract? = null

    companion object {
        fun onCreate(
                keyDetails: MyKey,
                listener: PixKeySentClaimDetailsContract,
        ) = PixKeySentClaimDetailsBottomSheet().apply {
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
        return inflater.inflate(
            R.layout.layout_pix_claim_key_details_bottom_sheet,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getKeyInfo()
        setupView()
        onCancel()
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogResize
    }

    private fun getKeyInfo() {
        key = arguments?.getParcelable(PIX_MY_KEY_DETAILS)
    }

    private fun setupView() {
        key?.let { itKey ->
            val isOwnership = itKey.claimType == PixClaimTypeEnum.OWNERSHIP.name
            val title: String
            val replyDateInformation: String

            val date = itKey.claimDetail?.resolutionLimitDate?.let {
                val clearDate = it.clearDate()
                getString(
                    R.string.text_pix_claims_date,
                    clearDate.formatterDate(LONG_TIME_NO_UTC)
                )
            } ?: if (isOwnership)
                getString(R.string.text_pix_create_claims_ownership_day)
            else getString(R.string.text_pix_create_claims_portability_day)

            if (isOwnership) {
                title = getString(R.string.text_pix_claims_details_ownership_title)
                replyDateInformation = getString(
                    R.string.text_pix_claims_details_ownership_reply_date_information,
                    date
                )
            } else {
                title = getString(R.string.text_pix_claims_details_portability_title)
                replyDateInformation = getString(
                    R.string.text_pix_claims_details_portability_reply_date_information,
                    date
                )
            }
            tv_title?.text = title
            tv_key_value?.text = itKey.key?.let { getFormattedKey(it, itKey.keyType, isMask = false) }
            tv_key_name?.text = getKeyTypeName(itKey.keyType)
            iv_key_type?.setBackgroundResource(getIconKeyPix(itKey.keyType))
            tv_reply_date_information?.text = replyDateInformation
        }
    }

    private fun onCancel() {
        tv_cancel_claim?.setOnClickListener {
            listener?.onCancel(key)
            dismiss()
        }
    }
}