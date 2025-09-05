package br.com.mobicare.cielo.commons

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.listener.OnCommonActivityFragmentStatusListener
import br.com.mobicare.cielo.commons.listener.OnGenericFragmentListener
import br.com.mobicare.cielo.commons.ui.BaseLoggedActivity
import br.com.mobicare.cielo.commons.utils.SessionExpiredHandler
import br.com.mobicare.cielo.commons.utils.hideSoftKeyboard
import kotlinx.android.synthetic.main.activity_generic.*
import kotlinx.android.synthetic.main.card_error.*

const val EXTRA_PARAM_FRAGMENT = "EXTRA_PARAM_FRAGMENT"
const val EXTRA_PARAM_OBJECT = "EXTRA_PARAM_OBJECT"
const val EXTRA_BUTTON_CANCEL = "EXTRA_BUTTON_CANCEL"
const val EXTRA_BUTTON_SAVE = "EXTRA_BUTTON_SAVE"
const val EXTRA_BUTTON_TRY = "EXTRA_BUTTON_TRY"

class CommonActivityWithFragment : BaseLoggedActivity(), OnCommonActivityFragmentStatusListener {

    private var fragmentListener: OnGenericFragmentListener? = null

    companion object {
        const val FINISH_OK = "br.com.cielo.commons.finishOk"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generic)
        this.onSetTitleToolbar("")
        loadFragment()
        configureListeners()
    }

    private fun loadFragment() {
        intent.extras?.let {
            it.getString(EXTRA_PARAM_FRAGMENT)?.let { itParamFragment ->
                val paramObject = it.getBundle(EXTRA_PARAM_OBJECT)
                createFragment(itParamFragment, paramObject)
            }
            it.getString(EXTRA_BUTTON_CANCEL, "").let {
                if(it.isNotEmpty()) {
                    this.cancelButton?.setText(it)
                }
            }
            it.getString(EXTRA_BUTTON_SAVE, "").let {
                if(it.isNotEmpty()) {
                    this.saveButton?.setText(it)
                }
            }
            it.getString(EXTRA_BUTTON_TRY, "").let {
                if(it.isNotEmpty()) {
                    this.button_try?.setText(it)
                }
            }
        }
    }

    private fun configureListeners() {
        this.cancelButton?.setOnClickListener {
            this.finish()
        }
        this.saveButton?.setOnClickListener {
            this@CommonActivityWithFragment.hideSoftKeyboard()
            this.fragmentListener?.onSaveButtonClicked()
        }
        this.button_try?.setOnClickListener {
            this.fragmentListener?.onReload()
        }
    }

    private fun createFragment(fragmentClassName: String, bundle: Bundle?) {
        try {
            val clazz = Class.forName(fragmentClassName)
            if (clazz != null) {
                val fragment: Fragment = clazz.newInstance() as Fragment
                if (fragment is OnGenericFragmentListener) {
                    this.fragmentListener = fragment
                }
                fragment.arguments = bundle
                startFragment(fragment)
            }
        }
        catch(e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun startFragment(fragment: Fragment) {
        this.supportFragmentManager.beginTransaction().replace(R.id.contentLayout, fragment).commit()
    }

    override fun onError() {
        if (isAttached()) {
            this.contentLayout?.visibility = View.GONE
            this.cardButtons?.visibility = View.GONE
            this.layout_card_error?.visibility = View.VISIBLE
            this.progress_loading?.visibility = View.GONE
        }
    }

    override fun onErrorHandlerRetryWithMessage(message: ErrorMessage) {
        onError()
        this.text_view_card_error_msg?.text = SpannableStringBuilder.valueOf(message.message)
    }

    override fun onError(message: ErrorMessage) {
        if (isAttached()) {
            this.contentLayout?.visibility = View.VISIBLE
            this.cardButtons?.visibility = View.GONE
            this.layout_card_error?.visibility = View.GONE
            this.progress_loading?.visibility = View.GONE

            showErrorMessage(message)
        }
    }

    override fun onErrorAndClose(message: ErrorMessage) {
        if (isAttached()) {
            this.contentLayout?.visibility = View.VISIBLE
            this.cardButtons?.visibility = View.GONE
            this.layout_card_error?.visibility = View.GONE
            this.progress_loading?.visibility = View.GONE

            showErrorMessage(message, "") {
                this.finish()
            }
        }
    }

    override fun onShowLoading() {
        if (isAttached()) {
            this.contentLayout?.visibility = View.GONE
            this.layout_card_error?.visibility = View.GONE
            this.cardButtons?.visibility = View.GONE
            this.progress_loading?.visibility = View.VISIBLE
        }
    }

    override fun onHideLoading() {
        if (isAttached()) {
            this.contentLayout?.visibility = View.VISIBLE
            this.layout_card_error?.visibility = View.GONE
            this.cardButtons?.visibility = View.VISIBLE
            this.progress_loading?.visibility = View.GONE
        }
    }

    override fun onExpiredSession() {
        SessionExpiredHandler.userSessionExpires(this, true)
    }

    override fun onSetTitleToolbar(title: String) {
        if (isAttached()) {
            setupToolbar(this.toolbar_generic_activity as Toolbar, title)
        }
    }

    override fun onSuccess(result: String) {
        if (isAttached()) {
            LocalBroadcastManager.getInstance(this)
                    .sendBroadcast(Intent(FINISH_OK))
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFragmentManager.executePendingTransactions()
        this.finish()
    }
}