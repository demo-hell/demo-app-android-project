package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics.MenuLabels.CUSTOMER_SERVICE_CHANNELS
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.Contact
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.ContactType
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.OmbudsmanNavigationFlowActivity
import br.com.mobicare.cielo.commons.constants.HelpCenter.FONT_12
import br.com.mobicare.cielo.commons.constants.HelpCenter.FONT_24
import br.com.mobicare.cielo.commons.constants.HelpCenter.HELP_CENTER
import br.com.mobicare.cielo.commons.constants.HelpCenter.LINK
import br.com.mobicare.cielo.commons.constants.HelpCenter.MAIL
import br.com.mobicare.cielo.commons.constants.HelpCenter.OMBUDSMAN
import br.com.mobicare.cielo.commons.constants.HelpCenter.OMBUDSMAN_ID
import br.com.mobicare.cielo.commons.constants.HelpCenter.PHONE
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.LogoutListener
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.*
import kotlinx.android.synthetic.main.content_contact_item.view.*
import kotlinx.android.synthetic.main.content_contact_main_fragment.*
import kotlinx.android.synthetic.main.content_little_error.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import br.com.mobicare.cielo.centralDeAjuda.analytics.TechnicalSupportAnalytics as GA4

class CentralAjudaContatosFragment : BaseFragment(), CentralAjudaContatosContract.View {

    private var logoutListener: LogoutListener? = null

    val presenter: CentralAjudaContatosPresenter by inject {
        parametersOf(this)
    }

    companion object {
        fun create(logoutListener: LogoutListener?): CentralAjudaContatosFragment {
            val fragment = CentralAjudaContatosFragment()
            fragment.logoutListener = logoutListener
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.content_contact_main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.setView(this)
        configureRecycleView()
        presenter.loadContacts()
    }

    private fun configureRecycleView() {
        recycle_view.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun showError(error: ErrorMessage?) {
        trackError(
            errorCode = error?.code.orEmpty(),
            errorMessage = error?.message.orEmpty(),
        )
        recycle_view?.gone()
        card_view_step?.visible()
        content_progress?.gone()
        content_error?.visible()

        content_litle_error_button_retry?.setOnClickListener {
            presenter.loadContacts()
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            SessionExpiredHandler.userSessionExpires(requireContext())
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            super.showLoading()
            recycle_view?.gone()
            card_view_step?.visible()
            content_progress?.visible()
            content_error?.gone()
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            super.hideLoading()
            recycle_view?.visible()
            card_view_step?.gone()
            content_progress?.gone()
            content_error?.gone()
        }
    }

    override fun showContacts(contacts: List<Contact>) {
        if (isAttached().not()) return

        val adapter = DefaultViewListAdapter(contacts, R.layout.content_contact_item)
        adapter.setBindViewHolderCallback(object : DefaultViewListAdapter.OnBindViewHolder<Contact> {


            override fun onBind(item: Contact, holder: DefaultViewHolderKotlin) {
                var amount = 0
                holder.mView.contactsLayout?.removeAllViews()
                holder.mView.text_view_title.text = item.category
                item.types.forEach {

                    if (item.category == OMBUDSMAN || item.id == OMBUDSMAN_ID) {
                        inflateContactOmbudsmanLayout(holder.mView, it)
                        return@forEach
                    }
                    if (amount++ >= 2)
                        return@forEach

                    when (it.type) {
                        PHONE -> inflatePhoneLayout(it, holder)
                        MAIL -> inflateEmailLayout(it, holder)
                        LINK -> inflateLinkLayout(it, holder)
                        else -> null
                    }?.let { itView ->
                        holder.mView.tag = item
                        holder.mView.contactsLayout?.addView(itView)
                    }
                }
            }
        })
        this.recycle_view.adapter = adapter
    }

    private fun inflateContactOmbudsmanLayout(view: View, contactType: ContactType) {
        val ombudsmanLayout = LayoutInflater.from(view.context).inflate(R.layout.content_contact_item_type_ombudsman, null, false)?.also {
            it.findViewById<TextView>(R.id.tv_phone_item_ombudsman)?.apply {
                text = getString(R.string.text_number_item_type_ombudsman, contactType.contact)
            }
        }
        view.card_view_main?.setOnClickListener {
            trackSelectContent(contactType.description)
            requireContext().startActivity<OmbudsmanNavigationFlowActivity>()
        }

        view.tag = contactType
        view.contactsLayout?.addView(ombudsmanLayout)
    }

    private fun inflateContactTypeLayout(fontSize: Float, contactType: ContactType, holder: DefaultViewHolderKotlin) =
            LayoutInflater.from(holder.mView.context).inflate(R.layout.content_contact_item_type, null, false)?.also {
                it.findViewById<TextView>(R.id.text_view_contact)?.apply {
                    text = contactType.contact
                    textSize = fontSize
                }
                it.findViewById<TextView>(R.id.text_view_description)?.text = contactType.description
            }

    private fun inflatePhoneLayout(contactType: ContactType, holder: DefaultViewHolderKotlin): View? =
            inflateContactTypeLayout(FONT_24, contactType, holder)?.also {
                it.setOnClickListener {
                    trackSelectContent(contactType.description)
                    callPhone(contactType.contact)
                }
            }

    private fun inflateEmailLayout(contactType: ContactType, holder: DefaultViewHolderKotlin): View? =
            inflateContactTypeLayout(FONT_12, contactType, holder)?.also {
                it.setOnClickListener {
                    trackSelectContent(contactType.description)
                    sendEmail(contactType.contact)
                }
            }

    private fun inflateLinkLayout(contactType: ContactType, holder: DefaultViewHolderKotlin): View? =
            inflateContactTypeLayout(FONT_12, contactType, holder)?.also {
                it.setOnClickListener {
                    trackSelectContent(contactType.description)
                    openLink(contactType.contact)
                }
            }

    private fun callPhone(phoneNumber: String) {
        Utils.callPhone(requireActivity(), phoneNumber)
    }

    private fun sendEmail(email: String) {
        Utils.sendEmail(requireActivity(), email)
    }

    private fun openLink(link: String) {
        Utils.openLink(requireActivity(), link)
    }

    private fun trackSelectContent(contentName: String) {
        GA4.logSelectContent(
            HELP_CENTER,
            CUSTOMER_SERVICE_CHANNELS,
            contentName
        )
    }

    private fun trackError(errorCode: String, errorMessage: String) {
        if (isAttached()) {
            GA4.logException(HELP_CENTER, errorCode, errorMessage)
        }
    }
}