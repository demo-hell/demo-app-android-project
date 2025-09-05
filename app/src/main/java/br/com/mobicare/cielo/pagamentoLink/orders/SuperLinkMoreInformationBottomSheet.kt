package br.com.mobicare.cielo.pagamentoLink.orders

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.IntentAction.SHARE_WITH
import br.com.mobicare.cielo.commons.constants.IntentAction.TEXT_PLAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp.PLEASE_INSTALL_APP_AGAIN
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_NOT_INSTALLED
import br.com.mobicare.cielo.commons.constants.WhatsApp.WHATSAPP_PACKAGE_NAME
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.showMessage
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.item_super_link_more_information.view.*
import kotlinx.android.synthetic.main.layout_information_super_link.*


data class ItemSuperLink(val key: String, val value: String?)

class SuperLinkMoreInformationBottomSheet(val arrayList: List<ItemSuperLink>, val nameProduct: String?, val link: String? ) : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(arrayList: List<ItemSuperLink>, nameProduct: String?, link: String?): SuperLinkMoreInformationBottomSheet {
            return SuperLinkMoreInformationBottomSheet(arrayList,nameProduct,link)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val view =
            inflater.inflate(R.layout.layout_information_super_link, container, false)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnShowListener {
            // For AndroidX use: com.google.android.material.R.id.design_bottom_sheet
            val bottomSheet = dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            ) as? FrameLayout
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.peekHeight = 0
                behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState >= 4) {
                            dismiss()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    }
                })
            }
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UserPreferences.getInstance().saveBannerStatusBalcaoRebevies(true)
        view.viewTreeObserver.addOnGlobalLayoutListener {
            val dialog = dialog as BottomSheetDialog
            val bottomSheet =
                dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.peekHeight = 0
        }
        populateRecycleView()
        setupListener()
    }

    private fun populateRecycleView() {
        recycleViewMoreInformation.layoutManager = LinearLayoutManager(requireContext())
        recycleViewMoreInformation?.setHasFixedSize(true)
        val adapter = DefaultViewListAdapter(arrayList, R.layout.item_super_link_more_information)
        adapter.setBindViewHolderCallback(object: DefaultViewListAdapter.OnBindViewHolder<ItemSuperLink> {
            override fun onBind(item: ItemSuperLink, holder: DefaultViewHolderKotlin) {
                holder.mView.textViewLabel.text = item.key
                holder.mView.textViewValue.text = item.value
            }
        })
        recycleViewMoreInformation.adapter = adapter
    }

    private fun setupListener(){
        imageViewCommunication?.setOnClickListener {
            shareDefaultDevice()
        }
        imageViewWhatsApp?.setOnClickListener {
            verificationZapInDevice()
        }

    }

    private fun verificationZapInDevice() {
        if (!appInstalledOrNot(WHATSAPP_PACKAGE_NAME)) {
            requireContext().showMessage(
                message = PLEASE_INSTALL_APP_AGAIN,
                title = WHATSAPP_NOT_INSTALLED
            )
        } else {
            sendMsgWhatsapp()
        }
    }

    fun appInstalledOrNot(uri: String): Boolean {
        val pm = requireActivity().packageManager
        var app_installed: Boolean
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }

        return app_installed
    }

    private fun sendMsgWhatsapp() {
            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = TEXT_PLAIN
            val text = getString(R.string.payment_link_for,nameProduct,link)
            val pm = requireActivity().packageManager
            pm.getApplicationInfo(WHATSAPP_PACKAGE_NAME, PackageManager.GET_META_DATA)
            waIntent.setPackage(WHATSAPP_PACKAGE_NAME)
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            startActivity(Intent.createChooser(waIntent, SHARE_WITH))
    }

    private fun shareDefaultDevice() {
        val intent = Intent()
        intent.setAction(Intent.ACTION_SEND)
        intent.type = TEXT_PLAIN
        intent.putExtra(Intent.EXTRA_TEXT, link)
        startActivity(Intent.createChooser(intent, SHARE_WITH))
    }
}