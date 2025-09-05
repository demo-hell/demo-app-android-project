package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfServiceSupply

import androidx.annotation.DrawableRes
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.THREE
import br.com.cielo.libflue.util.TWO
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.api.AutoAtendimentoRepository
import br.com.mobicare.cielo.autoAtendimento.domain.model.Supply
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.NetworkConstants.HTTP_STATUS_420
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.utils.ResourcesLoader
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration

class SelfServiceSupplyPresenter(
        private val view: SelfServiceSupplyContract.View,
        private val repository: AutoAtendimentoRepository) : SelfServiceSupplyContract.Presenter {

    private val BOBINA: String = "Bobinas"
    private val ADESIVO: String = "Adesivos"
    private val PELICULA: String = "Películas"

    private val PELICULA_ACESSIBILIDADE_ICMP = 6150
    private val PELICULA_ACESSIBILIDADE_D200 = 6152
    private val PELICULA_ACESSIBILIDADE_ZIP = 6154

    private val ADESIVO_MULTIVAN = 5099
    private val ADESIVO_MULTIBANDEIRA = 5007

    private val COIL_UNIFIELD = 5070
    private val COIL_LIO = 5071

    private var supplyList = ArrayList<Supply>()


    override fun onPause() {
        this.repository.disposable()
    }

    override fun load() {
        this.view.showLoading()

        val accessToken = UserPreferences.getInstance().token
        val authorization = Utils.authorization()

        this.repository.loadSupplies(accessToken, authorization, object : APICallbackDefault<List<Supply>, String> {
            override fun onError(error: ErrorMessage) {
                this@SelfServiceSupplyPresenter.view.hideLoading()
                if (error.logout) {
                    this@SelfServiceSupplyPresenter.view.logout(error)
                }
                else if (error.httpStatus == HTTP_STATUS_420) {
                    this@SelfServiceSupplyPresenter.view.showIneligibleUser(error.errorMessage)
                }
                else {
                    this@SelfServiceSupplyPresenter.view.showError(error)
                }
            }

            override fun onSuccess(response: List<Supply>) {
                this@SelfServiceSupplyPresenter.createSuppliesByResponse(response)
                this@SelfServiceSupplyPresenter.view.hideLoading()
            }
        })
    }

    override fun selectItem(item: ItemBannerMigration) {
        when(item.id) {
            ONE -> { this.view.openCoinEngine(BOBINA, createDTOListForCoil(item)) }
            TWO -> { this.view.openSuppliesEngine(ADESIVO, createDTOListForSticker(item)) }
            THREE -> { this.view.openSuppliesEngine(PELICULA, createDTOListForFilm(item)) }
        }
    }

    private fun createDTOListForCoil(item: ItemBannerMigration)
        = this.supplyList.filter {
            it.code.toInt() == COIL_UNIFIELD || it.code.toInt() == COIL_LIO
        }.map { SupplyDTO(it.allowedQuantity, it.code, it.description, it.type, ZERO) }

    private fun createDTOListForSticker(item: ItemBannerMigration)
        = this.supplyList.filter {
            it.code.toInt() == ADESIVO_MULTIBANDEIRA || it.code.toInt() == ADESIVO_MULTIVAN
        }.map { SupplyDTO(it.allowedQuantity, it.code, it.description, it.type, ZERO) }

    private fun createDTOListForFilm(item: ItemBannerMigration)
        = this.supplyList.filter {
                it.code.toInt() == PELICULA_ACESSIBILIDADE_D200
                || it.code.toInt() == PELICULA_ACESSIBILIDADE_ICMP
                || it.code.toInt() == PELICULA_ACESSIBILIDADE_ZIP
        }.map { SupplyDTO(it.allowedQuantity, it.code, it.description, it.type, ZERO) }

    private fun createSuppliesByResponse(supplies: List<Supply>) {
        var listItemBannerMigration = ArrayList<ItemBannerMigration>()

        this.supplyList.clear()
        this.supplyList.addAll(supplies)

        supplies.forEach {
            val code = it.code.toInt()
            when(code) {
                PELICULA_ACESSIBILIDADE_ICMP, PELICULA_ACESSIBILIDADE_D200, PELICULA_ACESSIBILIDADE_ZIP -> {
                    createItemBannerMigration(R.drawable.pelicula, PELICULA, THREE, listItemBannerMigration)
                }
                ADESIVO_MULTIVAN, ADESIVO_MULTIBANDEIRA -> {
                    createItemBannerMigration(R.drawable.adesivos, ADESIVO, TWO, listItemBannerMigration)
                }
                COIL_UNIFIELD, COIL_LIO -> {
                    createItemBannerMigration(R.drawable.bolbina, BOBINA, ONE, listItemBannerMigration)
                }
                else -> null
            }?.let { itItem ->
                listItemBannerMigration.add(itItem)
            }
        }

        listItemBannerMigration.sortBy { it.id }

        this.view.show(listItemBannerMigration)
    }

    private fun createItemBannerMigration(@DrawableRes idRes: Int, firstName: String, id: Int, itens: List<ItemBannerMigration>) : ItemBannerMigration? {
        var item: ItemBannerMigration? = null
        if (itens.find { itItem -> itItem.id == id } == null) {
            ResourcesLoader.instance.getDrawable(idRes)?.let {
                item = ItemBannerMigration(firstName, it, id)
            }
        }
        return item
    }

}