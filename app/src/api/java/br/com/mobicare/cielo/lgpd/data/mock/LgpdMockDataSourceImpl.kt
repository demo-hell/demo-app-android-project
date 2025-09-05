package br.com.mobicare.cielo.lgpd.data.mock

import br.com.mobicare.cielo.lgpd.data.LgpdDataSource
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import io.reactivex.Observable
import retrofit2.Response

class LgpdMockDataSourceImpl : LgpdDataSource {

    override fun getEligibility(): Observable<LgpdElegibilityEntity> {
        return Observable.create {
            Thread.sleep(1500)
            // Cenario => Caso usuário seja Proprietário do EC logado e com Conta Digital
            it.onNext(
                LgpdElegibilityEntity(
                    eligible = true,
                    digitalAccount = true,
                    owner = true
                )
            )

//            // Cenario => Caso usuário seja Proprietário do EC logado e com Domicílio Bancário
//            it.onNext(
//                LgpdElegibilityEntity(
//                    eligible = true,
//                    digitalAccount = false,
//                    owner = true
//                )
//            )

//            // Cenario => Caso usuário simples
//            it.onNext(
//                LgpdElegibilityEntity(
//                    eligible = true,
//                    digitalAccount = false,
//                    owner = false
//                )
//            )
            it.onComplete()
        }
    }

    override fun postLgpdAgreement(): Observable<Response<Void>> {
        return Observable.create {
            Thread.sleep(1500)
            // Cenario de Sucesso
            it.onNext(Response.success<Void?>(null))
            it.onComplete()

            // Cenario de Erro
            //it.onError(Throwable("Falha"))
        }
    }

}