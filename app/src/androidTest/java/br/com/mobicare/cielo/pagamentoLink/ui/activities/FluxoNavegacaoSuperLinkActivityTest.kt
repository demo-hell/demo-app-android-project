package br.com.mobicare.cielo.pagamentoLink.ui.activities

import android.content.Intent
import androidx.test.rule.ActivityTestRule
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.FluxoNavegacaoSuperlinkActivity
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers.pathContains
import org.junit.Before
import org.junit.Test
import java.lang.Thread.sleep

class FluxoNavegacaoSuperLinkActivityTest {

    private var activityTestRule: ActivityTestRule<FluxoNavegacaoSuperlinkActivity> =
            ActivityTestRule(FluxoNavegacaoSuperlinkActivity::class.java,
                    true, true)

    @Before
    fun setup() {
        UserPreferences.getInstance().saveToken("123456")
        UserPreferences.getInstance().keepEC("2000463023")
    }

    @Test
    fun startNavigationSuperLinkSuccessfuly() {

        RESTMockServer.whenGET(pathContains("appcielo/v1/user/eligibility/pagamento-link"))
                .thenReturnFile(200,
                        "pagamentoLink/pagamento_link_eligibility_success_response.json")

        RESTMockServer
                .whenGET(pathContains("site-cielo/v1/ecommerce/payment/link"))
                .thenReturnFile(200,
                        "pagamentoLink/pagamento_link_last_links_success.json")

        activityTestRule.launchActivity(Intent())

        sleep(5000)


    }

}