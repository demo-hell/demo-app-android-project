package br.com.mobicare.cielo.injection

import android.content.Context
import br.com.mobicare.cielo.component.bankData.BankDataAPIDataSource
import br.com.mobicare.cielo.component.bankData.BankDataRepository
import br.com.mobicare.cielo.esqueciSenha.data.clients.api.EsqueciSenhaNewAPIDataSource
import br.com.mobicare.cielo.esqueciSenha.data.clients.managers.EsqueciSenhaNewRepository
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.api.EsqueciUsuarioAndEstabelecimentoAPIDataSource
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.managers.EsqueciUsuarioAndEstabelecimentoRepository
import br.com.mobicare.cielo.meusCartoes.CreditCardsRepository
import br.com.mobicare.cielo.meusCartoes.clients.api.CreditCardsDataSource
import com.google.gson.internal.`$Gson$Preconditions`.checkNotNull

object Injection {

    fun provideEsqueciSenhaNewRepository(context: Context): EsqueciSenhaNewRepository {
        checkNotNull(context)
        return EsqueciSenhaNewRepository.getInstance(EsqueciSenhaNewAPIDataSource.getInstance(context))
    }

    fun provideEsqueciUsuarioRepository(context: Context): EsqueciUsuarioAndEstabelecimentoRepository {
        checkNotNull(context)
        return EsqueciUsuarioAndEstabelecimentoRepository.getInstance(EsqueciUsuarioAndEstabelecimentoAPIDataSource.getInstance(context))
    }

    fun provideBankDataRepository(context: Context): BankDataRepository {
        checkNotNull(context)
        return BankDataRepository.getInstance(BankDataAPIDataSource.getInstance(context))
    }

    fun provideCreditCardsRepository(context: Context): CreditCardsRepository {
        checkNotNull(context)
        return CreditCardsRepository.getInstance(CreditCardsDataSource.getInstance(context))
    }
}
