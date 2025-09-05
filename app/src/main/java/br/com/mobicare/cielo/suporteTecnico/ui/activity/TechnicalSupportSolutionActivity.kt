package br.com.mobicare.cielo.suporteTecnico.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.os.PersistableBundle
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.engine.TrocaMaquinaEngineActivity
import br.com.mobicare.cielo.commons.analytics.Analytics
import br.com.mobicare.cielo.commons.analytics.Category
import br.com.mobicare.cielo.commons.constants.ONE_SPACE
import br.com.mobicare.cielo.commons.constants.Text.SIMPLE_LINE
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.EMPTY_VALUE
import br.com.mobicare.cielo.commons.utils.configureItemDecoration
import br.com.mobicare.cielo.commons.utils.handleSslError
import br.com.mobicare.cielo.commons.utils.open
import br.com.mobicare.cielo.databinding.ActivityTechnicalSupportSolutionBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.suporteTecnico.domain.entities.Problem
import br.com.mobicare.cielo.suporteTecnico.domain.entities.ProblemSolution
import br.com.mobicare.cielo.suporteTecnico.ui.adapter.TechnicalSolutionItems
import br.com.mobicare.cielo.suporteTecnico.ui.adapter.TechnicalSupportSolutionsAdapter
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.serialState
import com.tinsuke.icekick.extension.unfreezeInstanceState
import org.jetbrains.anko.makeCall

class TechnicalSupportSolutionActivity : BaseLoggedActivity(), BaseActivity.AnimationListener,
        BaseActivity.OnBackButtonListener {

    private lateinit var binding: ActivityTechnicalSupportSolutionBinding
    private lateinit var page: String

    companion object {
        const val TECHNICAL_SUPPORT_PROBLEM_KEY = "br.com.cielo.suporteTecnico.solucaoProblemaSuporteTecnico"
        const val TECHNICAL_SUPPORT_SOLUTION_KEY = "br.com.cielo.suporteTecnico.solucaoDefinitivaSuporteTecnico"
        const val TECHNICAL_SUPPORT_WEB_VIEW_KEY = "br.com.cielo.suporteTecnico.showWebView"
        const val ID_PROBLEM_OR_SOLUTION_MASK = "{idProblemOrSolution}"
        const val TRUE_ACCESS = "&logged=true"
        const val OPEN_OS = "#OpenOS"
        const val TEL = "tel:"

        const val CALL_FROM_WEBVIEW_PERMISSION = 0x21

        const val BASE_WEBVIEW_URL = BuildConfig.TECHNICAL_SUPPORT_URL + "?supportId={idProblemOrSolution}"
        const val REQUEST_CODE = 1999
    }

    private val showWebView: Boolean
        get() = intent.getBooleanExtra(TECHNICAL_SUPPORT_WEB_VIEW_KEY, false)


    private var phoneNumber: String? by serialState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTechnicalSupportSolutionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!showWebView) {

            val technicalSupportProblem = intent
                    .getParcelableExtra<Problem>(TECHNICAL_SUPPORT_PROBLEM_KEY)

            technicalSupportProblem?.let {
                page = it.name.replace(ONE_SPACE, SIMPLE_LINE)
                setupSolutionList(it)

                setupToolbar(binding.toolbarTechnicalSupportSolution.toolbarMain, it.name)

                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)

            }
        } else {

            showWebView()

            val solution = intent.getParcelableExtra<ProblemSolution>(TECHNICAL_SUPPORT_SOLUTION_KEY)

            solution?.let {
                page = it.name.replace(ONE_SPACE, SIMPLE_LINE)
                configureWebView(it)
                setupToolbar(binding.toolbarTechnicalSupportSolution.toolbarMain, it.name)
                overridePendingTransition(R.anim.slide_from_bottom_to_up,
                    R.anim.slide_from_up_to_bottom)
            }
        }

        this.animationListener = this
        this.onBackButtonListener = this
        unfreezeInstanceState(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        sendGaScreenView()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        freezeInstanceState(outState)
    }

    private fun setupSolutionList(technicalSupportProblem: Problem) {
        binding.recyclerTechnicalSupportSolutionsMenu.apply {
            layoutManager = LinearLayoutManager(context).also {
                configureItemDecoration(context, it, R.drawable.shape_item_technical)
            }
            adapter = TechnicalSupportSolutionsAdapter(
                TechnicalSolutionItems(technicalSupportProblem.solutions!!.toList()),
                object : TechnicalSupportSolutionsAdapter.OnClickListener {
                    override fun onClick(solution: ProblemSolution) = onItemClick(solution)
                }
            )
            setHasFixedSize(true)
        }
    }

    private fun onItemClick(solution: ProblemSolution) {
        sendGaButton(solution.name)

        Intent(this, TechnicalSupportSolutionActivity::class.java).let {
            it.putExtra(TECHNICAL_SUPPORT_SOLUTION_KEY, solution)
            it.putExtra(TECHNICAL_SUPPORT_WEB_VIEW_KEY, true)
            startActivity(it)
        }
    }

    private fun showWebView() {
        binding.apply {
            cardTechnicalSupportSolutionsListWrapper.gone()
            linearTechnicalSupportContent.visible()
        }
    }

    private fun showLoading() {
        binding.apply {
            linearTechnicalSupportContent.gone()
            progressTechnicalSolution.visible()
        }
    }

    private fun hideLoading() {
        binding.apply {
            progressTechnicalSolution.gone()
            linearTechnicalSupportContent.visible()
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    private fun configureWebView(problemSolution: ProblemSolution) {
        val technicalSupportUrl = BASE_WEBVIEW_URL.replace(
            ID_PROBLEM_OR_SOLUTION_MASK,
            problemSolution.let { if (it.idSolution != EMPTY_VALUE) it.idSolution else it.idProblem }
        ) + TRUE_ACCESS

        binding.webviewTechnicalSupportSolution.open(technicalSupportUrl, object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showLoading()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String) {
                hideLoading()
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                //Support API < 24
                return handleOverrideUrlLoading(url)
                        ?: super.shouldOverrideUrlLoading(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest): Boolean {
                //Support API >= 24
                return handleOverrideUrlLoading(request.url.toString())
                        ?: super.shouldOverrideUrlLoading(view, request)
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
                view?.handleSslError(handler, this@TechnicalSupportSolutionActivity)
            }
        })
    }

    private fun handleOverrideUrlLoading(url: String?): Boolean? {
        return when {
            url == null -> null
            url.endsWith(OPEN_OS) -> {
                val token = UserPreferences.getInstance().token ?: ""
                if (token.isNotEmpty()) {
                    TrocaMaquinaEngineActivity.create(this@TechnicalSupportSolutionActivity)
                    finish()
                }
                true
            }
            url.startsWith(TEL) -> {
                callWhenHasPermission(url)
                true
            }
            else -> null
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "NewApi")
    private fun callWhenHasPermission(url: String) {
        if (ContextCompat.checkSelfPermission(
                        this@TechnicalSupportSolutionActivity,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            phoneNumber = url.split(":")[1]

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this@TechnicalSupportSolutionActivity,
                            Manifest.permission.CALL_PHONE)) {

                setupPermissionInfoDialog()

            } else {
                ActivityCompat
                        .requestPermissions(this@TechnicalSupportSolutionActivity,
                                arrayOf(Manifest.permission.CALL_PHONE),
                                CALL_FROM_WEBVIEW_PERMISSION)
            }
        } else {
            makeCall(url.split(":")[1])
        }
    }

    private fun setupPermissionInfoDialog() {

        showOptionDialogMessage {
            title = getString(R.string.text_technical_suppport_title)
            setBtnLeft("Não")
            setBtnRight("Sim")
            setMessage("Para contatar o suporte é preciso permitir o acesso ao telefone. " +
                    "Deseja conceder esse acesso?")
            setOnclickListenerRight {
                ActivityCompat
                        .requestPermissions(this@TechnicalSupportSolutionActivity,
                                arrayOf(Manifest.permission.CALL_PHONE),
                                CALL_FROM_WEBVIEW_PERMISSION)
            }
        }
    }

    override fun whenClose() {
        if (!showWebView) {
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        } else {
            overridePendingTransition(R.anim.slide_from_up_to_bottom,
                    R.anim.slide_from_bottom_to_up)
        }
    }

    override fun onBackTouched() {
        this.finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        sendGaButton("voltando")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {

        when (requestCode) {
            CALL_FROM_WEBVIEW_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    if (phoneNumber != null) {
                        makeCall(phoneNumber.toString())
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    //region GaFirebase

    private fun sendGaScreenView() {
        if (isAttached()) {
            Analytics.trackScreenView(
                screenName = "/suporte-tecnico/$page",
                screenClass = this.javaClass
            )
        }
    }

    private fun sendGaButton(label: String) {
        Analytics.trackEvent(
            category = listOf(Category.APP_CIELO, "central de ajuda"),
            action = listOf("clique:suporte-tecnico"),
            label = listOf(label)
        )
    }

    //endregion
}