package br.com.mobicare.cielo.commons.domain.useCase

import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.CieloDataResult

interface UseCase<P, R> {
    suspend operator fun invoke(params: P): CieloDataResult<R>
}

interface UseCaseWithoutParams<R> {
    suspend operator fun invoke(): CieloDataResult<R>
}