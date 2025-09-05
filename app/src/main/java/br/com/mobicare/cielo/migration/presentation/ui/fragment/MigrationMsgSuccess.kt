package br.com.mobicare.cielo.migration.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.ui.BaseFragment
import br.com.mobicare.cielo.commons.utils.TimeUtils
import br.com.mobicare.cielo.migration.domains.entities.MigrationDomain
import br.com.mobicare.cielo.migration.presentation.ui.activity.MigrationActionListener
import br.com.mobicare.cielo.migration.presentation.ui.activity.MigrationActivity
import kotlinx.android.synthetic.main.fragment_migration_msg_sucess.*


class MigrationMsgSuccess : BaseFragment() {

    private var mIsNameValidate = false

    private lateinit var recebaMaisActionListener: MigrationActionListener
    private var migrationDomain: MigrationDomain? = null

    companion object {
        fun create(actionListener: MigrationActionListener, migrationDomain: MigrationDomain): MigrationMsgSuccess {
            val fragment = MigrationMsgSuccess().apply {
                recebaMaisActionListener = actionListener
                this.migrationDomain = migrationDomain
            }

            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_migration_msg_sucess, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_migration_user_email?.apply {
            text = migrationDomain?.email
        }

        migrationDomain?.let {
            val textWithMinutes = String.format(getString(R.string.migration_successfull_subtitle),
                    TimeUtils.convertMinutesToHours(it.tokenExpirationInMinutes))
            tv_migration_subtitle.text = textWithMinutes
        }

        button_migration_next.setOnClickListener {
            val activity = activity as MigrationActivity
            activity.finish()
        }
    }

}
