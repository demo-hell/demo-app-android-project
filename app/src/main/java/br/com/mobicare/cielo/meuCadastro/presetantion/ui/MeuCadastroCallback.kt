package br.com.mobicare.cielo.meuCadastro.presetantion.ui

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj

/**
 * Created by Benhur on 14/09/17.
 */
interface MeuCadastroCallback: APICallbackDefault<MeuCadastroObj, ErrorMessage>{
    fun onLoadContactAddress(status: String?)
    fun onLoadPhysicalAddress(status: String?)
    fun onSuccessBrands(status: CardBrandFees)
    fun onErrorBrands(error : ErrorMessage)
}

