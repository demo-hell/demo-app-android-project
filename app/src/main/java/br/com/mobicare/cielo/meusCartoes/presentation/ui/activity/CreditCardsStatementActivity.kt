package br.com.mobicare.cielo.meusCartoes.presentation.ui.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.CreditCard.PROXY_CARD
import br.com.mobicare.cielo.commons.constants.CreditCard.TITLE_CARD_ARGS
import br.com.mobicare.cielo.commons.ui.BaseActivity
import br.com.mobicare.cielo.commons.utils.addInFrame
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTransicaoObj
import br.com.mobicare.cielo.extrato.presentation.ui.fragments.ExtratoTimeLineFragment
import kotlinx.android.synthetic.main.activity_statement_cards_credit.*

class CreditCardsStatementActivity : BaseActivity() {

    private val title: String by lazy {
        intent?.getStringExtra(TITLE_CARD_ARGS)
                ?: getString(R.string.text_my_cards_title)
    }
    private val proxyCard: String?
        get() = intent?.getStringExtra(PROXY_CARD)

    private var statementAccumulatedList: ExtratoTimeLineFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statement_cards_credit)
        setupToolbar(toolbarCardsStatement as Toolbar, title)

        statementAccumulatedList = ExtratoTimeLineFragment.getInstance(statementFooter = false,
                readOnlyStatements = true, withCreditCardStatements = true, proxyCard = proxyCard)
                .apply {
                    this.statementListener = object : ExtratoTimeLineFragment.OnStatementSelectedListener {
                        override fun onStatementSelect(statement: ExtratoTransicaoObj) {
                        }
                    }
                }

        statementAccumulatedList?.run {
            this.addInFrame(supportFragmentManager,
                    R.id.frameCreditCardsStatementContent)
        }
    }
}