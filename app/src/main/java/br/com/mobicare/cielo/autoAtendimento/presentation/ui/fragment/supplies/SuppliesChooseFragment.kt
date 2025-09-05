package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.supplies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import br.com.cielo.libflue.util.TEN
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.COILS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.FILMS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_COIL
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_FILM
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.SCREEN_VIEW_REQUEST_MATERIALS_STICKER
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.STICKERS
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.TO_ADD
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics.Companion.TO_REMOVE
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.SuppliesAcitivytContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.adapter.SuppliesChooseAdapter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment.Companion.ADESIVO
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment.Companion.PELICULA
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.analytics.SOLICITAR_MATERIAIS
import br.com.mobicare.cielo.commons.constants.ONE
import br.com.mobicare.cielo.commons.constants.ONE_FLOAT
import br.com.mobicare.cielo.commons.constants.Text.ADESIVOS
import br.com.mobicare.cielo.commons.constants.Text.AUTOATENDIMENTO
import br.com.mobicare.cielo.commons.constants.Text.EMPTY
import br.com.mobicare.cielo.commons.constants.Text.PELICULAS
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.constants.ZERO_COMMA_FIVE_FLOAT
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.analytics.pipeJoin
import br.com.mobicare.cielo.databinding.FragmentStickerChooseNewBinding
import org.koin.android.ext.android.inject

class SuppliesChooseFragment : BaseFragment(), SuppliesChooseContract.View,
    SuppliesChooseAdapter.OnItemSelectedListener {

    private var binding: FragmentStickerChooseNewBinding? = null

    private lateinit var actionListener: ActivityStepCoordinatorListener
    private var callBack: SuppliesAcitivytContract.View? = null
    private var myListSticker: ArrayList<SupplyDTO>? = null
    private lateinit var tagName: String
    private var nameSupplies: String? = EMPTY

    private var suppliesChooseAdapter: SuppliesChooseAdapter? = null

    private val ga4: SelfServiceAnalytics by inject()
    private val screenPath
        get() = when (tagName) {
            ADESIVO ->
                SCREEN_VIEW_REQUEST_MATERIALS_STICKER

            PELICULA ->
                SCREEN_VIEW_REQUEST_MATERIALS_FILM

            else ->
                SCREEN_VIEW_REQUEST_MATERIALS_COIL
        }

    companion object {
        fun create(
            listener: ActivityStepCoordinatorListener,
            myListSticker: ArrayList<SupplyDTO>,
            callBack: SuppliesAcitivytContract.View,
            tagName: String
        ): Fragment {
            val fragment = SuppliesChooseFragment().apply {
                this.actionListener = listener
                this.myListSticker = myListSticker
                this.callBack = callBack
                this.tagName = tagName
            }

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentStickerChooseNewBinding.inflate(
        inflater, container, false
    ).also { binding = it }.root

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showSupplies()
        setupListener()
        verifyQuantity()
    }

    override fun onResume() {
        super.onResume()
        logScreenView()
    }

    private fun setupListener() {
        binding?.buttonNext?.setOnClickListener {
            buttonClick(suppliesChooseAdapter?.supplies?.map {
                if (it.quantidade > ZERO) {
                    nameSupplies += "${it.description} "
                }
                br.com.mobicare.cielo.coil.domain
                    .SupplyDTO(it.allowedQuantity, it.code, it.description, it.type)
                    .apply {
                        this.quantidade = it.quantidade
                    }
            })
        }
    }

    private fun verifyQuantity() {
        suppliesChooseAdapter?.supplies?.run {
            if (this.sumBy { it.quantidade } == ZERO) {
                disableButton()
            } else {
                enableButton()
            }
        }
    }

    private fun disableButton() {
        binding?.buttonNext?.apply {
            isEnabled = false
            alpha = ZERO_COMMA_FIVE_FLOAT
        }
    }

    private fun enableButton() {
        binding?.buttonNext?.apply {
            isEnabled = true
            alpha = ONE_FLOAT
        }
    }

    override fun showSupplies() {
        getTagName()

        suppliesChooseAdapter = myListSticker?.let {
            SuppliesChooseAdapter(it).apply {
                this.onItemSelectedListener = this@SuppliesChooseFragment
            }
        }
        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = suppliesChooseAdapter
            suppliesChooseAdapter?.notifyDataSetChanged()
            LinearSnapHelper().attachToRecyclerView(this)
        }
        binding?.frameErrorInclude?.apply {
            containerError.background = context?.let {
                ContextCompat.getDrawable(
                    it,
                    R.drawable.item_shape_fail
                )
            }
            ivTitle.gone()
            tvTitle.text = String.format(getString(R.string.limit_title), tagName)
            tvMessage.apply {
                text = String.format(getString(R.string.limit_message), tagName)
                setTextColor(ContextCompat.getColor(
                    context,
                    R.color.red_fail
                ))
            }
        }
    }

    private fun getTagName() {
        binding?.apply {

            containerErrorLimit.gone()

            when (tagName) {
                ADESIVO -> {
                    tvTitleContract.text = getString(R.string.tv_title_contract_sticker)
                    tvTitleMsg.gone()
                    tvSubtitleMsg.gone()
                }
                PELICULA -> {
                    tvTitleContract.text = getString(R.string.tv_title_contract_skin)
                    tvTitleMsg.visible()
                    tvSubtitleMsg.visible()
                }
                else -> {
                    tvTitleContract.text = getString(R.string.tv_title_contract_coil)
                    tvTitleMsg.gone()
                    tvSubtitleMsg.gone()
                }
            }
        }
    }

    override fun buttonClick(listMerchants: List<br.com.mobicare.cielo.coil.domain.SupplyDTO>?) {
        sendTagEvent(nameSupplies.orEmpty())

        val coilOptions = ArrayList<CoilOptionObj>(ONE)

        listMerchants?.forEach {
            if (it.quantidade != ZERO) {
                val coilOptionObj = CoilOptionObj()
                coilOptionObj.allowedQuantity = it.allowedQuantity
                coilOptionObj.code = it.code
                coilOptionObj.description = it.description
                coilOptionObj.type = it.type
                coilOptionObj.quantity = it.quantidade
                coilOptionObj.descriptionComplement = it.description
                coilOptionObj.title = it.description
                coilOptions.add(coilOptionObj)
            }
        }

        logClickButtonContinue(coilOptions)
        callBack?.listStickers(coilOptions)
        actionListener.onNextStep(false)

    }

    override fun onItemAddedQuantity(supplies: List<SupplyDTO>, selectedPosition: Int) {
        val supplyDTO = supplies[selectedPosition]
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(getAction()),
            label = listOf(Label.BOTAO, TO_ADD, supplyDTO.description)
        )
        if (supplies[selectedPosition].quantidade > ZERO) {
            enableButton()
        }

        showQuantityLimit(supplies)
    }

    override fun onItemRemovedQuantity(supplies: List<SupplyDTO>, selectedPosition: Int) {
        val supplyDTO = supplies[selectedPosition]
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(getAction()),
            label = listOf(Label.BOTAO, TO_REMOVE, supplyDTO.description)
        )

        if (supplies.sumBy { it.quantidade } == ZERO) {
            disableButton()
        }

        showQuantityLimit(supplies)
    }

    fun showQuantityLimit(supplies: List<SupplyDTO>) {
        var showMessageLimit = false

        supplies.forEach {
            if (it.quantidade == TEN) {
                showMessageLimit = true
                return@forEach
            }
        }
        binding?.apply {
            containerErrorLimit.visibility = if (showMessageLimit)
                View.VISIBLE
            else
                View.GONE
        }
    }

    private fun sendTagEvent(description: String = EMPTY) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(getAction(), description),
            label = listOf(Label.BOTAO, binding?.buttonNext?.text?.toString() ?: EMPTY)
        )
    }

    private fun getAction() =
        when (tagName) {
            ADESIVO ->
                STICKERS

            PELICULA ->
                FILMS

            else ->
                COILS
        }

    private fun logScreenView() {
        val tagScreenPath = if (tagName == ADESIVO) {
            pipeJoin(Category.APP_CIELO, AUTOATENDIMENTO, ADESIVOS)
        } else {
            pipeJoin(Category.APP_CIELO, AUTOATENDIMENTO, PELICULAS)
        }

        Analytics.trackScreenView(
            screenName = tagScreenPath,
            screenClass = this.javaClass
        )

        ga4.logScreenView(screenPath)
    }

    private fun logClickButtonContinue(coilOptionObj: ArrayList<CoilOptionObj>) {
        if (tagName == ADESIVO) {
            ga4.logBeginCheckoutSticker(coilOptionObj)
        } else {
            ga4.logBeginCheckoutFilm(coilOptionObj)
        }
    }

}