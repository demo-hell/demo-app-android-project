package br.com.mobicare.cielo.migration.presentation.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.*
import br.com.mobicare.cielo.commons.analytics.Action.FECHAR
import br.com.mobicare.cielo.commons.analytics.Action.VOLTAR
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.commons.utils.addWithAnimation
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain
import br.com.mobicare.cielo.migration.presentation.ui.fragment.*
import kotlinx.android.synthetic.main.activity_migration.*
import kotlinx.android.synthetic.main.activity_receba_mais.toolbar_include
import kotlinx.android.synthetic.main.toolbar_dialog.*

class MigrationActivity : BaseLoggedActivity(), MigrationActionListener {

    private var sequence = 0
    private val STEP_BANNER = 0
    private val STEP_01 = 1
    private val STEP_02 = 2
    private val STEP_03 = 3
    private val STEP_04 = 4

    private var migrationDomain = MigrationDomain()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_migration)
        UserPreferences.getInstance().saveBannerMigration(true)

        btnLeft.setOnClickListener {
            gaSendButton(VOLTAR)
            onBackPressed()
        }

        btnRight.setOnClickListener {
            gaSendButton(FECHAR)
            finish()
        }

        startFragment()
    }

    override fun onBackPressed() {
        if (sequence <= 0) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        } else {
            sequence--
            setFragment(true)
        }
    }

    fun startFragment() {
        hideTopBar()
        hideProgressBar()
        BannerMigrationFragment.create(this, migrationDomain).addInFrame(supportFragmentManager, R.id.frameFormContentInput)
    }


    fun setFragment(isBackAnimation: Boolean) {
        when (sequence) {
            STEP_BANNER -> {
                hideTopBar()
                hideProgressBar()
                BannerMigrationFragment.create(this, migrationDomain).addWithAnimation(supportFragmentManager, R.id.frameFormContentInput, isBackAnimation)
            }
            STEP_01 -> {
                showTopBar()
                showProgress01()
                MigrationStep01.create(this, migrationDomain).addWithAnimation(supportFragmentManager, R.id.frameFormContentInput, isBackAnimation)
            }
            STEP_02 -> {
                showTopBar()
                showProgress02()
                MigrationStep02.create(this, migrationDomain).addWithAnimation(supportFragmentManager, R.id.frameFormContentInput, isBackAnimation)
            }
            STEP_03 -> {
                showTopBar()
                showProgress03()
                MigrationStep03.create(this, migrationDomain).addWithAnimation(supportFragmentManager, R.id.frameFormContentInput, isBackAnimation)
            }
            STEP_04 -> {
                hideTopBar()
                hideProgressBar()
                MigrationMsgSuccess.create(this, migrationDomain).addWithAnimation(supportFragmentManager, R.id.frameFormContentInput, isBackAnimation)
            }
        }
    }

    //region MigrationActionListener
    override fun onNextStep(isFinish: Boolean) {
        if (isFinish) {
            val result = Intent()
            result.putExtra("VALUE_CONTRATED", "200,00")
            setResult(Activity.RESULT_OK, result)
            finish()
        } else {
            sequence++
            setFragment(false)
        }
    }

    override fun setTitle(title: String) {
        setupToolbar(toolbar_include as Toolbar, title)
    }

    fun hideTopBar(){
        toolbar_include.visibility = View.GONE
    }

    fun showTopBar(){
        toolbar_include.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progress_01.visibility = View.GONE
        progress_02.visibility = View.GONE
        progress_03.visibility = View.GONE
    }


    fun showProgress01() {
        progress_01.visibility = View.VISIBLE
        progress_02.visibility = View.INVISIBLE
        progress_03.visibility = View.INVISIBLE
    }

    fun showProgress02() {
        progress_01.visibility = View.VISIBLE
        progress_02.visibility = View.VISIBLE
        progress_03.visibility = View.INVISIBLE
    }

    fun showProgress03() {
        progress_01.visibility = View.VISIBLE
        progress_02.visibility = View.VISIBLE
        progress_03.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        if (isAttached())
            progress_loading.visibility = View.GONE
    }

    override fun showProgress() {
        if (isAttached())
            progress_loading.visibility = View.VISIBLE
    }

    override fun bannerDimmiss() {
        finish()
    }

    private fun gaSendButton(labelButton: String) {
        if (isAttached()) {
            Analytics.trackEvent(
                category = listOf(Category.APP_CIELO,  HOME_LOGADA),
                action = listOf(Action.ATUALIZAR_ACESSO, Action.FORMULARIO),
                label = listOf(Label.BOTAO, labelButton.replace("\n", ""))
            )
        }
    }

}