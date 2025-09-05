package br.com.mobicare.cielo.dirf.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.FileUtils
import kotlinx.android.synthetic.main.activity_pdf_view.*
import java.io.File

class PdfViewActivity : BaseLoggedActivity() {

    private val file by lazy {
        intent.getSerializableExtra(FILE_EXTRA) as File

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)
        setupToolbar(toolbar as Toolbar, getString(R.string.title_sub_menu_Dirf))

        pdfView.fromFile(file).load()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_common_share, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_share -> {
                FileUtils(this).startShare(file)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val FILE_EXTRA = "FILE"
    }
}