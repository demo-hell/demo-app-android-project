package br.com.mobicare.cielo.mfa

import androidx.test.rule.ActivityTestRule
import br.com.mobicare.cielo.mfa.token.TokenGeneratorActivity
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.FluxoNavegacaoSuperlinkActivity

class TokenGeneratorActivityTest {

    private var activityTestRule: ActivityTestRule<TokenGeneratorActivity> =
            ActivityTestRule(TokenGeneratorActivity::class.java,
                    true, true)




}