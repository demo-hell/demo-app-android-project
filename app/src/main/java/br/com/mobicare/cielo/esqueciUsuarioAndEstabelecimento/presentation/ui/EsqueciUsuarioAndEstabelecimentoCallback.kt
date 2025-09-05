package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.ui

import br.com.mobicare.cielo.commons.data.managers.APICallbackDefault
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.UserNameObj

/**
 * Created by benhur.souza on 31/08/2017.
 */
interface EsqueciUsuarioAndEstabelecimentoCallback : APICallbackDefault<EsqueciUsuarioObj?, String>{
    fun onSuccess(response: EsqueciEstabelecimentoObj?)
    fun onUserSuccess(response: Array<UserNameObj>?)
}