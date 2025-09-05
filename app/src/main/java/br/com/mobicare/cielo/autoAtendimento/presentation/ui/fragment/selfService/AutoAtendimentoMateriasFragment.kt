package br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.THREE
import br.com.cielo.libflue.util.TWO
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.model.Supply
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.SuppliesEngineActivity
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.adapter.MateriasAdapter
import br.com.mobicare.cielo.coil.presentation.activity.CoilEngineActivity
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.Text.CAROUSEL
import br.com.mobicare.cielo.commons.constants.Text.AUTOATENDIMENTO
import br.com.mobicare.cielo.commons.presentation.utils.DefaultViewHolderKotlin
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.ui.adapter.DefaultViewListAdapter
import br.com.mobicare.cielo.databinding.AutoAtendimentoMateriasFragmentBinding
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import br.com.mobicare.cielo.main.presentation.REQUEST_CODE_COIL
import br.com.mobicare.cielo.main.presentation.REQUEST_CODE_STICKER
import br.com.mobicare.cielo.migration.presentation.presenter.ItemBannerMigration
import kotlinx.android.synthetic.main.item_home_shortcut.view.*
import org.jetbrains.anko.startActivityForResult

class AutoAtendimentoMateriasFragment : BaseFragment(), AutoAtendimentoContract.View {

    private var binding: AutoAtendimentoMateriasFragmentBinding? = null
    lateinit var adapter: MateriasAdapter
    val listMaterias = ArrayList<ItemBannerMigration>()
    var listSupply: ArrayList<Supply>? = null
    var myListSupplies: ArrayList<SupplyDTO>? = ArrayList()

    var isCheckBolbina = false
    var isCheckPelicula = false
    var isCheckAdesivo = false


    companion object {
        val VALUEARRAY: String = "valuesArray"
        val LISTSUPPLIES: String = "listSticker"
        val TAGNAME: String = "tagName"
        val BOBINA: String = "Bobinas"
        val ADESIVO: String = "Adesivos"
        val PELICULA: String = "Películas"

        val COIL_UNIFIELD = "5070"
        val COIL_LIO = "5071"
        val ADESIVO_MULTIVAN = "5099"
        val ADESIVO_MULTIBANDEIRA = "5007"

        val PELICULA_ACESSIBILIDADE_ICMP = "6150"
        val PELICULA_ACESSIBILIDADE_D200 = "6152"
        val PELICULA_ACESSIBILIDADE_ZIP = "6154"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = AutoAtendimentoMateriasFragmentBinding.inflate(
        inflater,
        container,
        false
    ).also { binding = it }.root

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    @SuppressLint("NewApi")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getListSupplies()
        organizeListSupplies()
    }

    private fun organizeListSupplies() {

        if (isAttached()) {
            val sortedAppsList = listMaterias.sortedBy { it.id }
            sortedAppsList.let {
                val adapterMaterials = DefaultViewListAdapter(it, R.layout.item_home_shortcut)
                adapterMaterials.setBindViewHolderCallback(object :
                    DefaultViewListAdapter.OnBindViewHolder<ItemBannerMigration> {
                    override fun onBind(
                        item: ItemBannerMigration,
                        holder: DefaultViewHolderKotlin
                    ) {
                        holder.mView.textHeaderLabel?.text = item.firstName
                        holder.mView.imageHeaderButton?.setImageDrawable(item.imageUrl)
                    }
                })

                adapterMaterials.onItemClickListener =
                    object : DefaultViewListAdapter.OnItemClickListener<ItemBannerMigration> {
                        override fun onItemClick(item: ItemBannerMigration) {
                            this@AutoAtendimentoMateriasFragment.selectItem(item)
                        }
                    }
                binding?.apply {
                    recyclerMaterias.layoutManager = GridLayoutManager(context, THREE)
                    recyclerMaterias.setHasFixedSize(true)
                    recyclerMaterias.adapter = adapterMaterials
                    LinearSnapHelper().attachToRecyclerView(recyclerMaterias)
                }
            }
        }
    }

    private fun getListSupplies() {
        listSupply = requireArguments().getParcelableArrayList(VALUEARRAY)
        listSupply?.forEach {
            verificationSupplies(it)
        }
    }

    private fun verificationSupplies(it: Supply) {
        chooseSuppliesCoil(it)
        chooseSuppliesSticker(it)
        chooseSuppliesSkin(it)
    }

    private fun chooseSuppliesSkin(it: Supply) {

        if (isAttached()) {
            if (it.code.toInt() == PELICULA_ACESSIBILIDADE_ICMP.toInt() ||
                it.code.toInt() == PELICULA_ACESSIBILIDADE_D200.toInt() ||
                it.code.toInt() == PELICULA_ACESSIBILIDADE_ZIP.toInt()
            ) {
                if (!isCheckPelicula) {
                    val drawable3 =
                        ContextCompat.getDrawable(requireContext(), R.drawable.pelicula) as Drawable
                    val item3 = drawable3.let { ItemBannerMigration(PELICULA, it, THREE) }
                    listMaterias.add(item3)
                    isCheckPelicula = true
                }
            }
        }
    }

    private fun chooseSuppliesSticker(it: Supply) {

        if (isAttached()) {
            if (it.code.toInt() == ADESIVO_MULTIVAN.toInt() || it.code.toInt() == ADESIVO_MULTIBANDEIRA.toInt()) {
                if (!isCheckAdesivo) {
                    val drawable2 =
                        ContextCompat.getDrawable(requireContext(), R.drawable.adesivos) as Drawable
                    val item2 = drawable2.let { ItemBannerMigration(ADESIVO, it, TWO) }
                    listMaterias.add(item2)
                    isCheckAdesivo = true
                }
            }
        }
    }

    private fun chooseSuppliesCoil(it: Supply) {

        if (isAttached()) {
            if (it.code.toInt() == COIL_UNIFIELD.toInt() || it.code.toInt() == COIL_LIO.toInt()) {
                if (!isCheckBolbina) {
                    val drawable1 =
                        ContextCompat.getDrawable(requireContext(), R.drawable.bolbina) as Drawable
                    val item1 = drawable1.let { ItemBannerMigration(BOBINA, it, ONE) }
                    listMaterias.add(item1)
                    isCheckBolbina = true
                }
            }
        }
    }

    override fun selectItem(items: ItemBannerMigration) {

        myListSupplies?.clear()
        super.selectItem(items)
        when (items.id) {
            ONE -> {
                if (isAttached()) {
                    listSupply?.forEach {
                        if (it.code.toInt() == COIL_UNIFIELD.toInt() || it.code.toInt() == COIL_LIO.toInt()) {
                            val supplyObj =
                                SupplyDTO(it.allowedQuantity, it.code, it.description, it.type, ZERO)
                            myListSupplies?.add(supplyObj)
                        }
                    }
                    requireActivity().startActivityForResult<CoilEngineActivity>(
                        REQUEST_CODE_COIL,
                        LISTSUPPLIES to myListSupplies,
                        TAGNAME to BOBINA
                    )
                }
            }

            TWO -> {

                if (isAttached()) {
                    listSupply?.forEach {
                        if (it.code.toInt() == ADESIVO_MULTIBANDEIRA.toInt() || it.code.toInt() == ADESIVO_MULTIVAN.toInt()) {
                            val supplyObj =
                                SupplyDTO(it.allowedQuantity, it.code, it.description, it.type, ZERO)
                            myListSupplies?.add(supplyObj)
                        }
                    }
                    requireActivity().startActivityForResult<SuppliesEngineActivity>(
                        REQUEST_CODE_STICKER,
                        LISTSUPPLIES to myListSupplies,
                        TAGNAME to ADESIVO
                    )
                }
            }

            THREE -> {

                if (isAttached()) {
                    listSupply?.forEach {
                        if (it.code.toInt() == PELICULA_ACESSIBILIDADE_D200.toInt() || it.code.toInt() == PELICULA_ACESSIBILIDADE_ICMP.toInt() || it.code.toInt() == PELICULA_ACESSIBILIDADE_ZIP.toInt()) {
                            val supplyObj =
                                SupplyDTO(it.allowedQuantity, it.code, it.description, it.type, ZERO)
                            myListSupplies?.add(supplyObj)
                        }
                    }
                    requireActivity().startActivityForResult<SuppliesEngineActivity>(
                        REQUEST_CODE_STICKER,
                        LISTSUPPLIES to myListSupplies,
                        TAGNAME to PELICULA
                    )
                }
            }
        }

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, AUTOATENDIMENTO),
            action = listOf(CAROUSEL),
            label = listOf(items.firstName.toLowerCasePTBR())
        )
    }
}