package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.machine

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.AutoAtendimentoAnalytics
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.engine.InstalacaoMaquinaAdicionalEngineActivity
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.commons.utils.typeDensity
import br.com.mobicare.cielo.databinding.AutoAtendimentoItemMachineBinding
import br.com.mobicare.cielo.databinding.RequestMachineFragmentBinding
import br.com.mobicare.cielo.machine.domain.MachineItemOfferResponse
import br.com.mobicare.cielo.machine.domain.MachineListOffersResponse
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

@Keep
class RequestMachineFragment : BaseFragment(), RequestMachineContract.View {

    private val typeDensity: String by lazy {
        requireContext().typeDensity()
    }

    private val presenter: RequestMachinePresenter by inject {
        parametersOf(this)
    }

    private val analytics: AutoAtendimentoAnalytics by inject()

    private var _binding: RequestMachineFragmentBinding? = null
    private val binding get() = _binding!!

    private val adapter = DefaultViewListAdapter<MachineItemOfferResponse>(
        emptyList(),
        R.layout.auto_atendimento_item_machine
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = RequestMachineFragmentBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        analytics.logScreenView()
        presenter.loadOffers(typeDensity)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        presenter.onCleared()
        super.onDestroy()
    }

    private fun setupRecyclerView() {
        binding.recycleView.let { rv ->
            rv.layoutManager = LinearLayoutManager(
                requireActivity(), LinearLayoutManager.VERTICAL, false)
            rv.adapter = adapter.also {
                it.setBindViewHolderCallback(onBindViewHolder)
                it.onItemClickListener = onItemClickListener
            }
        }
    }

    private val onBindViewHolder = object : DefaultViewListAdapter.OnBindViewHolder<MachineItemOfferResponse> {
        override fun onBind(item: MachineItemOfferResponse, holder: DefaultViewHolderKotlin) {
            AutoAtendimentoItemMachineBinding.bind(holder.mView).apply {
                textTitleMachine.text = item.title
                textAmountMachine.text = item.rentalAmount.toPtBrRealString()
                Picasso.get()
                    .load(item.imageUrl)
                    .into(imageViewMachine, object : Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception?) { e?.printStackTrace() }
                    })
            }
        }
    }

    private val onItemClickListener = object: DefaultViewListAdapter.OnItemClickListener<MachineItemOfferResponse> {
        override fun onItemClick(item: MachineItemOfferResponse) {
            sendGaScreenView(item.advertisement)
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, SOLICITAR_MAQUININHA),
                action = listOf(Action.SOLICITAR_MAQUININHA),
                label = listOf(Label.CARD, item.title)
            )
            analytics.logRequestMachineBeginCheckout(item.title)
            InstalacaoMaquinaAdicionalEngineActivity.create(item, requireActivity())
        }
    }

    override fun showError(error: ErrorMessage?) {
        if (isAttached()) {
            analytics.logException(
                screenName = AutoAtendimentoAnalytics.SCREEN_VIEW_REQUEST_MACHINE,
                errorCode = error?.httpStatus?.toString().orEmpty(),
                errorMessage = error?.errorMessage.orEmpty()
            )

            binding.apply {
                progress.root.gone()
                includeError.root.visible()
                textViewTitleRequestMachine.gone()
                recycleView.gone()
                includeError.buttonUpdate.setOnClickListener {
                    presenter.loadOffers(typeDensity)
                }
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        if (isAttached()) {
            SessionExpiredHandler.userSessionExpires(requireContext(), true)
        }
    }

    override fun showLoading() {
        if (isAttached()) {
            binding.apply {
                progress.root.visible()
                includeError.root.gone()
                textViewTitleRequestMachine.gone()
                recycleView.gone()
            }
        }
    }

    override fun hideLoading() {
        if (isAttached()) {
            binding.progress.root.gone()
        }
    }

    override fun showOffers(response: MachineListOffersResponse) {
        if (isAttached()) {
            binding.apply {
                progress.root.gone()
                includeError.root.gone()
                textViewTitleRequestMachine.visible()
                recycleView.visible()

                adapter.updateList(response.offers.flatMap { it.items })
            }
        }
    }

    private fun sendGaScreenView(name: String) {
        if (isAttached()) {
            Analytics.trackScreenView(
                screenName = "/maquina-$name",
                screenClass = this.javaClass
            )
        }
    }

}