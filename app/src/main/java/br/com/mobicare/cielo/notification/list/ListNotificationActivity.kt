package br.com.mobicare.cielo.notification.list

import android.os.Bundle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.ui.fragment.CieloInfoFragment
import br.com.mobicare.cielo.commons.utils.AlertDialogCustom
import br.com.mobicare.cielo.commons.utils.Utils
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.databinding.ActivityListNotificationBinding
import br.com.mobicare.cielo.extensions.gone
import br.com.mobicare.cielo.extensions.visible
import br.com.mobicare.cielo.notification.analytics.NotificationAnalytics as analytics
import br.com.mobicare.cielo.notification.domain.NotificationItem
import org.koin.android.ext.android.inject

class ListNotificationActivity : BaseActivity(), ListNotificationContract.View {
    private val presenter: ListNotificationPresenter by inject()
    private lateinit var binding: ActivityListNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListNotificationBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            setupToolbar(
                toolbarBackCardActivation.toolbarMain,
                getString(R.string.text_notification_title_list),
            )

            recyclerViewMessage.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(this@ListNotificationActivity)

            contentError.buttonErrorTry.setOnClickListener {
                contentError.containerError.gone()
                presenter.getAllNotifications()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        presenter.onResume()
        presenter.setView(this)
        presenter.getAllNotifications()
        analytics.logScreenView()
    }

    override fun hideProgress() {
        if (isAttached()) {
            with(binding) {
                recyclerViewMessage.visible()
                progress.root.gone()
                contentError.containerError.gone()
            }
        }
    }

    override fun showProgress() {
        if (isAttached()) {
            binding.progress.root.visible()
        }
    }

    override fun showNotifications(notifications: List<NotificationItem>) {
        with(binding) {
            recyclerViewMessage.visible()
            frameEmptyNotifications.gone()
            if (isAttached()) {
                if (notifications.isEmpty()) {
                    configureMessageErroNotEmpty()
                } else {
                    recyclerViewMessage.adapter =
                        ListNotificationAdapter(notifications)
                }
            }
        }
    }

    private fun configureMessageErroNotEmpty() {
        val error = ErrorMessage()
        error.message = getString(R.string.text_notification_not_exist)
        showError(error)
    }

    override fun showError(error: ErrorMessage?) {
        error?.let {
            if (isAttached()) {
                with(binding) {
                    frameEmptyNotifications.gone()
                    recyclerViewMessage.gone()
                    contentError.containerError.visible()
                    contentError.textViewErrorMsg.text = it.message
                }
            }
        }
    }

    override fun logout(msg: ErrorMessage?) {
        msg?.let {
            if (isAttached()) {
                AlertDialogCustom.Builder(this, getString(R.string.text_notification_title_list))
                    .setTitle(R.string.ga_meu_cadastro)
                    .setMessage(it.message)
                    .setBtnRight(getString(R.string.ok))
                    .setOnclickListenerRight {
                        finish()
                        Utils.logout(this)
                    }
                    .show()
            }
        }
    }

    override fun showEmptyNotifications() {
        if (isAttached()) {
            with(binding) {
                recyclerViewMessage.gone()
                frameEmptyNotifications.visible()

                CieloInfoFragment.create(
                    getString(R.string.text_info_title),
                    getString(R.string.text_info_description),
                    R.drawable.ic_notification_deactivates,
                )
                    .addInFrame(
                        supportFragmentManager,
                        R.id.frameEmptyNotifications,
                    )
            }
        }
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }
}
