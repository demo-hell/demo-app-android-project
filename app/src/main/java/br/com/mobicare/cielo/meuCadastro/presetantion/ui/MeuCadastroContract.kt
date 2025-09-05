package br.com.mobicare.cielo.meuCadastro.presetantion.ui

//import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MinhaEmpresaFragment
import android.content.Context
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.ui.IAttached
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
import br.com.mobicare.cielo.meuCadastro.presetantion.ui.fragments.MinhaEmpresaContract
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.BodyChangePassword

interface MeuCadastroContract {

    interface View : IAttached {
        fun hideContent()
        fun showContent()
        fun showProgress()
        fun hideProgress()
        fun showError(error: ErrorMessage)
        fun loadDadosEstabelecimento(meuCadastroObj: MeuCadastroObj)

        fun loadBandeirasHabilitadas(bandeiras: CardBrandFees)
        fun hideBandeirasHabilitadas()

        fun context(): Context
    }

    interface Presenter {
        fun getChangePassword(body: BodyChangePassword, callback: MinhaEmpresaContract.View)
        fun callAPI()
    }
}
