package br.com.mobicare.cielo.contactCielo.utils

import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import br.com.cielo.libflue.bottomsheet.CieloListBottomSheet
import br.com.cielo.libflue.bottomsheet.base.CieloBottomSheet
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.contactCielo.analytics.ContactCieloAnalytics.trackCloseClickButtonOnBottomSheetContactCielo
import br.com.mobicare.cielo.contactCielo.analytics.ContactCieloAnalytics.trackDisplayButtonsOnBottomSheetContactCielo
import br.com.mobicare.cielo.contactCielo.domain.model.ContactCieloWhatsapp
import br.com.mobicare.cielo.databinding.BottomSheetCieloWhatsappInfoItemBinding

object BottomSheetContactCieloWhatsappList {
    fun show(
        fragment: Fragment,
        contactsCieloWhatsapp: List<ContactCieloWhatsapp>,
        onItemClicked: (ContactCieloWhatsapp, Int, CieloListBottomSheet<ContactCieloWhatsapp>) -> Unit
    ) {
        CieloListBottomSheet.create(
            headerConfigurator = CieloBottomSheet.HeaderConfigurator(
                title = fragment.getString(R.string.contact_cielo_envie_duvidas_via_whatsapp),
                onCloseTap = {
                    trackCloseClickButtonOnBottomSheetContactCielo()
                }
            ),
            layoutItemRes = R.layout.bottom_sheet_cielo_whatsapp_info_item,
            data = contactsCieloWhatsapp,
            onViewBound = { contactCielo, _, view ->
                val binding = BottomSheetCieloWhatsappInfoItemBinding.bind(view)
                trackDisplayButtonsOnBottomSheetContactCielo(contactCielo)
                binding.apply {
                    tvWhatsappItemTitle.text = fragment.getString(contactCielo.title)
                    tvWhatsappItemSubtitle.text = fragment.getString(contactCielo.description)
                }
            },
            onItemClicked = onItemClicked,
            dividerItemDecoration = DividerItemDecoration(
                fragment.requireContext(),
                DividerItemDecoration.VERTICAL
            ).apply {
                AppCompatResources.getDrawable(
                    fragment.requireContext(),
                    R.drawable.bottom_sheet_cielo_divider_inset
                )?.let {
                    setDrawable(it)
                }
            }
        ).show(fragment.childFragmentManager, fragment.javaClass.simpleName)
    }
}