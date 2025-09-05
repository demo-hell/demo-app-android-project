package br.com.mobicare.cielo.newLogin.about

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.sobreApp.presentation.ui.fragment.SobreAppFragment
import kotlinx.android.synthetic.main.activity_about_app.*

class AboutAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_app)
        setupToolbar(this.toolbar as Toolbar, "Sobre o App")
        setupFragment()
    }

    fun setupToolbar(toolbar: Toolbar?, toolbarTitle: String = "") {
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
            this.setDisplayShowHomeEnabled(true)
            this.setDisplayShowTitleEnabled(false)

            if (toolbarTitle.isNotBlank()) {
                val toolbarTitleTextView = findViewById<TextView>(R.id.textToolbarMainTitle)
                toolbarTitleTextView?.text = toolbarTitle
            }
        }
    }

    fun setupFragment() {
        val fragment = SobreAppFragment()
        this.supportFragmentManager.beginTransaction().replace(R.id.contentLayout, fragment).commit()
    }

    override fun onBackPressed() {
        this.finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}