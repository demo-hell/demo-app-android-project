package br.com.mobicare.cielo.coil.presentation.activity

import android.app.Activity
import android.os.Bundle
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.SuppliesAcitivytContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment.Companion.BOBINA
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment.Companion.LISTSUPPLIES
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.supplies.SuppliesChooseFragment
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.coil.presentation.adress.ServiceAddressFragment
import br.com.mobicare.cielo.coil.presentation.choose.CoilChooseFragment
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.cielo.libflue.util.ONE
import br.com.cielo.libflue.util.TWO
import br.com.cielo.libflue.util.ZERO
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment
import br.com.mobicare.cielo.coil.presentation.fragments.CoilAmountFragment
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.databinding.FragmentCoilEngineBinding
import br.com.mobicare.cielo.extensions.toLowerCasePTBR

class CoilEngineActivity : BaseLoggedActivity(), ActivityStepCoordinatorListener ,
    SuppliesAcitivytContract.View {

    private var binding: FragmentCoilEngineBinding? = null

    private var sequence = ZERO
    private var coilOptionObj: CoilOptionObj = CoilOptionObj()
    private var myListSticker: ArrayList<SupplyDTO>? = null
    private var coilOptions: ArrayList<CoilOptionObj>? = null
    private var tagName: String? = null
    private var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = FragmentCoilEngineBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            setupToolbar(it.toolbarInclude.root, getString(R.string.coils_title))
            it.toolbarInclude.root.setBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimary
                )
            )
        }

        intent?.extras.run {
            myListSticker =
                this?.getParcelableArrayList(LISTSUPPLIES)
            tagName = this?.getString(AutoAtendimentoMateriasFragment.TAGNAME)
        }

        setFragment(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onBackPressed() {
        if (isAttached())
            if (sequence <= ZERO) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                sequence--
                setFragment(true)
                if (sequence == ONE) {
                    gaBackPressFinal()
                } else {
                    gaBackPress()
                }
            }
    }

    override fun listStickers(coilOptions: ArrayList<CoilOptionObj>) {
        this.coilOptions = coilOptions
    }

    private fun gaBackPress() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(BOBINA, coilOptionObj.title),
            label = listOf(Label.BOTAO, VOLTAR)
        )
    }

    private fun gaBackPressFinal() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(BOBINA, coilOptionObj.title, CONFIRMACAO),
            label = listOf(Label.BOTAO, VOLTAR)
        )
    }

    //region ActivityJorneyActionListener
    override fun onNextStep(isFinish: Boolean) {
        if (isAttached())
            if (isFinish) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                sequence++
                setFragment(false)
            }
    }

    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun setTitle(title: String) {
        if (isAttached()) binding?.toolbarInclude?.root?.let { setupToolbar(it, title) }
    }

    fun setFragment(isBackAnimation: Boolean) {
        when (sequence) {
            ZERO -> {
                CoilChooseFragment.create(this) {
                    coilOptionObj = it
                }.addWithAnimation(
                    supportFragmentManager,
                    R.id.frameFormContentInput,
                    isBackAnimation
                )
            }
            ONE -> {
                CoilAmountFragment.create(this, coilOptionObj, this)
                    .addWithAnimation(
                        supportFragmentManager,
                        R.id.frameFormContentInput,
                        isBackAnimation
                    )
            }
            TWO -> {
                this.coilOptions?.let {
                    this.description = it[ZERO].description
                    this.tagName?.let { itTagName ->
                        it.forEach { itCoilObject ->
                            itCoilObject.tagService = itTagName.toLowerCasePTBR()
                        }
                    }
                    ServiceAddressFragment.create(this, it)
                        .addWithAnimation(
                            supportFragmentManager,
                            R.id.frameFormContentInput,
                            isBackAnimation
                        )
                }
            }
        }
    }
}