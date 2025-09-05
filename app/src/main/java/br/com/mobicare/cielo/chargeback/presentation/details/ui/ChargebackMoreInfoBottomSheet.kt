package br.com.mobicare.cielo.chargeback.presentation.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.cielo.libflue.enum.CieloCardBrandIcons
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.CHARGEBACK_SALES_PENDING
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.CHARGEBACK_SALES_TREATED
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.INFO_SALES_CONTESTATION
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_PENDING_DETAILS
import br.com.mobicare.cielo.chargeback.analytics.ChargebackGA4.Companion.SCREEN_VIEW_CHARGEBACK_TREATED_DETAILS
import br.com.mobicare.cielo.chargeback.domain.model.Chargeback
import br.com.mobicare.cielo.commons.analytics.Action.MODAL
import br.com.mobicare.cielo.commons.constants.FOUR
import br.com.mobicare.cielo.commons.utils.convertToBrDateFormat
import br.com.mobicare.cielo.commons.utils.setupBottomSheet
import br.com.mobicare.cielo.databinding.BottomSheetChargebackMoreInfoBinding
import br.com.mobicare.cielo.extensions.capitalizePTBR
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.pix.constants.PENDING
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.android.ext.android.inject
import java.time.format.DateTimeFormatter

class ChargebackMoreInfoBottomSheet : BottomSheetDialogFragment() {

    private var chargebackStatus: String? = null
    private val ga4: ChargebackGA4 by inject()
    private var _binding: BottomSheetChargebackMoreInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupBottomSheet(dialog = dialog, action = { dismiss() })

        return BottomSheetChargebackMoreInfoBinding.inflate(
            inflater, container, false
        ).also {
            _binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(arguments?.getParcelable(ARG_CHARGEBACK))
    }

    private fun setupView(chargeback: Chargeback?) {
        binding.apply {
            chargeback?.transactionDetails?.let { transactionDetails ->
                tvValueDate.text =
                    transactionDetails.transactionDate?.toString()?.convertToBrDateFormat(
                        DateTimeFormatter.ISO_LOCAL_DATE
                    )
                tvValueAuthorizationCode.text = transactionDetails.authorizationCode
                tvValuePaymentMethod.text =
                    transactionDetails.productType?.toLowerCasePTBR()?.capitalizePTBR()
                tvValueBankCard.text = getString(
                    R.string.chargeback_more_info_bottom_sheet_bank_card_end_digits,
                    transactionDetails.truncatedCardNumber?.takeLast(FOUR)
                )
                tvValueTerminal.text = transactionDetails.terminal
                tvValueNsu.text = transactionDetails.nsu?.toString()
                tvValueTid.text = transactionDetails.tid
                tvValueCardBrand.apply {
                    text = transactionDetails.cardBrandName?.toLowerCasePTBR()?.capitalizePTBR()
                    setCompoundDrawablesWithIntrinsicBounds(
                        CieloCardBrandIcons.getCardBrandIconResourceId(transactionDetails.cardBrandCode), ZERO, ZERO, ZERO
                    )
                }
            }
            ibClose.setOnClickListener { dismissAllowingStateLoss() }
        }
    }

    override fun onResume() {
        super.onResume()
        ga4PendingOrTreated()
    }
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun ga4PendingOrTreated(){
        if (chargebackStatus == PENDING) {
            ga4.logDisplayContentPendingOrTreatedChargebacks(
                SCREEN_VIEW_CHARGEBACK_PENDING_DETAILS,
                MODAL,
                CHARGEBACK_SALES_PENDING,
                INFO_SALES_CONTESTATION
            )
        } else {
            ga4.logDisplayContentPendingOrTreatedChargebacks(
                SCREEN_VIEW_CHARGEBACK_TREATED_DETAILS,
                MODAL,
                CHARGEBACK_SALES_TREATED,
                INFO_SALES_CONTESTATION
            )
        }
    }

    companion object {
        private const val ARG_CHARGEBACK = "ChargebackMoreInfoBottomSheet.ARG_CHARGEBACK"

        fun create(chargeback: Chargeback, chargebackStatus: String): ChargebackMoreInfoBottomSheet {
            return ChargebackMoreInfoBottomSheet().also {
                it.chargebackStatus = chargebackStatus
                it.arguments = Bundle().apply {
                    putParcelable(ARG_CHARGEBACK, chargeback)
                }
            }
        }
    }
}