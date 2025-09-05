package br.com.mobicare.cielo.dirf.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.analytics.GoogleAnalytics4Events.Other.FILE_EXTENSION
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.FileUtils
import br.com.mobicare.cielo.commons.utils.getMimeType
import br.com.mobicare.cielo.dirf.analytics.DirfGA4
import br.com.mobicare.cielo.dirf.analytics.DirfGA4.Companion.SCREEN_VIEW_DIRF_SUCESSO_COMPARTILHAR
import kotlinx.android.synthetic.main.activity_dirf_result_activity.*
import kotlinx.android.synthetic.main.activity_fluxo_navegacao_superlink.toolbar
import org.koin.android.ext.android.inject
import java.io.File


class DirfResultActivity : BaseLoggedActivity() {

    private val ga4: DirfGA4 by inject()
    private val file by lazy {
        intent.getSerializableExtra(FILE_EXTRA) as File
    }
    private val fileExtension by lazy {
        intent.getStringExtra(FILE_EXTENSION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dirf_result_activity)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_sub_menu_Dirf))
        txt_subtitle?.text = getString(R.string.txt_success_subtitle,
            this.getExternalFilesDir(null)?.let { file.toRelativeString(it) })

    }

    override fun onResume() {
        super.onResume()
        setupButtons()
        fileExtension?.let{
            ga4.logScreenViewShare(it)
        }
    }

    private fun setupButtons() {
        closeButton?.setOnClickListener {
            finish()
        }
        if (file.getMimeType() == PDF) {
            viewButton?.visibility = View.VISIBLE
            viewButton?.setOnClickListener {
                startActivity(
                    Intent(this@DirfResultActivity, PdfViewActivity::class.java).apply {
                        putExtra(PdfViewActivity.FILE_EXTRA, file)
                    }
                )
            }
        }

        shareButton?.setOnClickListener {
            FileUtils(this).startShare(file)
            fileExtension?.let{
                ga4.shareDirf(SCREEN_VIEW_DIRF_SUCESSO_COMPARTILHAR, it)
            }
        }
    }

    companion object {
        private const val PDF = "application/pdf"
        const val FILE_EXTRA = "FILE"
    }
}