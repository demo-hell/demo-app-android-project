package br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupplyDTO
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.supplies.SuppliesChooseFragment
import br.com.mobicare.cielo.coil.domains.CoilOptionObj
import br.com.mobicare.cielo.coil.presentation.adress.ServiceAddressFragment
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.CONFIRMACAO
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.analytics.Category.SOLICITAR_MATERIAIS
import br.com.mobicare.cielo.commons.analytics.Label
import br.com.mobicare.cielo.commons.ui.ActivityStepCoordinatorListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.extensions.toLowerCasePTBR
import kotlinx.android.synthetic.main.activity_receba_mais.*


class SuppliesEngineActivity : BaseLoggedActivity(), ActivityStepCoordinatorListener,
    SuppliesAcitivytContract.View {


    private var sequence = 0
    private var myListSticker: ArrayList<SupplyDTO>? = null
    private var coilOptions: ArrayList<CoilOptionObj>? = null
    private var tagName: String? = null
    private var description: String? = null
    private var isBackFinish = true
    private var nameSupplies:String?= ""
    private var nameSuppliesFinal:String?= ""
    var account = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        tagName?.run {

            setupToolbar(toolbar_include as Toolbar, this)

        }

        setFragment(false)
    }

    private fun initView() {
        setContentView(R.layout.fragment_sticker_engine)
        toolbar_include.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))

        intent?.extras.run {
            myListSticker =
                this?.getParcelableArrayList(AutoAtendimentoMateriasFragment.LISTSUPPLIES)
            tagName = this?.getString(AutoAtendimentoMateriasFragment.TAGNAME)

        }
    }

    override fun listStickers(coilOptions: ArrayList<CoilOptionObj>) {
        this.coilOptions = coilOptions
    }

    override fun onBackPressed() {
        if (sequence <= 0) {
            gaBackPressFinish()
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            sequence--
            setFragment(true)
            if (sequence == 0) {
                gaBackPressFinal()
            } else {
                gaBackPress()
            }
        }
    }

    private fun gaBackPressFinish() {

        myListSticker?.let {
            it.forEach {
                if (it.quantidade > 0) {
                    account += 1
                    nameSupplies += "${it.description} "
                }
            }
            if(account > 0){
                Analytics.trackEvent(
                    category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
                    action = listOf("${tagName?.toLowerCasePTBR()}", nameSupplies ?: ""),
                    label = listOf(Label.BOTAO, VOLTAR)
                )
            }
        }


        if (account == 0) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
                action = listOf("${tagName?.toLowerCasePTBR()}"),
                label = listOf(Label.BOTAO, VOLTAR)
            )
        }


    }

    private fun gaBackPress() {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf("${tagName?.toLowerCasePTBR()}", description ?: ""),
            label = listOf(Label.BOTAO, VOLTAR)
        )
    }

    private fun gaBackPressFinal() {

        myListSticker?.let {
            it.forEach {
                if (it.quantidade > 0) {
                    account += 1
                    nameSuppliesFinal += "${it.description} "
                }
            }
        }

        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, SOLICITAR_MATERIAIS),
            action = listOf(
                "${tagName?.toLowerCasePTBR()}",
                nameSuppliesFinal ?: "",
                CONFIRMACAO
            ),
            label = listOf(Label.BOTAO, VOLTAR)
        )
    }

    override fun onNextStep(isFinish: Boolean) {
        if (isFinish) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            sequence++
            setFragment(false)
        }
    }

    override fun setTitle(title: String) {
        if (isAttached())
            setupToolbar(toolbar_include as Toolbar, title)
    }

    override fun onLogout() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }


    fun setFragment(isBackAnimation: Boolean) {
        when (sequence) {
            0 -> {
                myListSticker?.let {
                    tagName?.let { it1 ->
                        SuppliesChooseFragment.create(this, it, this, it1)
                            .addWithAnimation(
                                supportFragmentManager,
                                R.id.frameFormContentInput,
                                isBackAnimation
                            )
                    }
                }
            }
            1 -> {
                this.coilOptions?.let {
                    this.description = it[0].description
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