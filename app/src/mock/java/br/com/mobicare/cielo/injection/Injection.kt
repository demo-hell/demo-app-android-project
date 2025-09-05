package br.com.mobicare.cielo.injection

import android.content.Context
import br.com.mobicare.cielo.antecipeSuasVendas.data.clients.managers.AntecipeRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.managers.CentralDeAjudaRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.managers.technicalSupport.TechnicalSupportMockRepository
import br.com.mobicare.cielo.esqueciSenha.data.clients.managers.EsqueciSenhaRepository
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.data.clients.managers.EsqueciUsuarioAndEstabelecimentoRepository
import br.com.mobicare.cielo.extrato.data.managers.ExtratoRepository
import br.com.mobicare.cielo.featureToggle.data.managers.FeatureToggleRepository
import br.com.mobicare.cielo.login.data.managers.LoginRepository
import br.com.mobicare.cielo.main.data.managers.MenuRepository
import br.com.mobicare.cielo.massiva.data.managers.LoginMassivaRepository
import br.com.mobicare.cielo.meuCadastro.data.clients.managers.MeuCadastroRepository
import br.com.mobicare.cielo.meusRecebimentos.managers.MeusRecebimentosRepository
import br.com.mobicare.cielo.postecipado.data.managers.PostecipadoRepository
import br.com.mobicare.cielo.splash.data.managers.SplashRepository
import br.com.mobicare.cielo.suporteTecnico.domain.repo.TechnicalSupportRepository
import com.google.gson.internal.`$Gson$Preconditions`.checkNotNull


/**
 * Created by benhur.souza on 31/03/2017.
 */

object Injection {

    fun provideSplashRepository(context: Context): SplashRepository {
        checkNotNull(context)
        return SplashRepository.getInstance(context)
    }

    fun provideMeusRecebimentosHomeRepository(context: Context): MeusRecebimentosRepository {
        checkNotNull(context)
        return  MeusRecebimentosRepository.getInstance(context)
    }

    fun provideExtratoRepository(context: Context): ExtratoRepository {
        checkNotNull(context)
        return ExtratoRepository.getInstance(context)
    }

    fun provideAntecipacaoRepository(context: Context): AntecipeRepository {
        checkNotNull(context)
        return AntecipeRepository.getInstance(context)
    }

    fun provideMenuRepository(context: Context): MenuRepository {
        checkNotNull(context)
        return MenuRepository.getInstance(context)
    }

    fun provideLoginMassivaRepository(context: Context): LoginMassivaRepository {
        checkNotNull(context)
        return LoginMassivaRepository.getInstance(context)
    }

    fun provideLoginRepository(context: Context): LoginRepository {
        checkNotNull(context)
        return LoginRepository.getInstance(context)
    }

    fun provideEsqueciSenhaRepository(context: Context): EsqueciSenhaRepository {
        checkNotNull(context)
        return EsqueciSenhaRepository.getInstance(context)
    }

    fun provideEsqueciUsuarioRepository(context: Context): EsqueciUsuarioAndEstabelecimentoRepository {
        checkNotNull(context)
        return EsqueciUsuarioAndEstabelecimentoRepository.getInstance(context)
    }

    fun provideCentralDeAjudaRepository(context: Context): CentralDeAjudaRepository {
        checkNotNull(context)
        return CentralDeAjudaRepository.getInstance(context)
    }

    fun provideTechnicalSupportRepository(context: Context): TechnicalSupportRepository {
        checkNotNull(context)
        return TechnicalSupportMockRepository.getInstance(context)
    }

    fun provideMeuCadastroRepository(context: Context): MeuCadastroRepository {
        checkNotNull(context)
        return MeuCadastroRepository.getInstance(context)
    }

    fun provideMeusRecebimentosRepository(context: Context): MeusRecebimentosRepository {
        checkNotNull(context)
        return MeusRecebimentosRepository.getInstance(context)
    }

    fun provideFeatureToggleRepository(context: Context): FeatureToggleRepository {
        checkNotNull(context)
        return FeatureToggleRepository.getInstance(context)
    }

    fun postecipadoRepository(context: Context): PostecipadoRepository {
        checkNotNull(context)
        return PostecipadoRepository.getInstance(context)
    }


}
