package br.com.mobicare.cielo.centralDeAjuda.data.clients.managers.technicalSupport

import android.annotation.SuppressLint
import android.content.Context
import br.com.mobicare.cielo.commons.utils.ReaderMock
import br.com.mobicare.cielo.suporteTecnico.domain.entities.Support
import br.com.mobicare.cielo.suporteTecnico.domain.repo.TechnicalSupportRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


open class TechnicalSupportMockRepository(val context: Context) : TechnicalSupportRepository {


    companion object {

        @SuppressLint("StaticFieldLeak")
        var instance: TechnicalSupportMockRepository? = null

        fun getInstance(context: Context): TechnicalSupportMockRepository {
            if (instance == null) {
                instance = TechnicalSupportMockRepository(context)
            }
            return instance as TechnicalSupportMockRepository
        }
    }

    override fun fetchTechnicalSupportRepository(): Observable<Support> {

        return Observable.just(ReaderMock.getTechnicalSupport(context))
                .delay(5000, TimeUnit.MILLISECONDS)

    }



}