package br.com.mobicare.cielo.pixMVVM.domain.usecase

import br.com.mobicare.cielo.commons.domain.useCase.UseCase
import br.com.mobicare.cielo.pixMVVM.data.model.request.PixDecodeQRCodeRequest
import br.com.mobicare.cielo.pixMVVM.domain.model.PixDecodeQRCode
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixQRCodeRepository

class PostPixDecodeQRCodeUseCase(
    private val repository: PixQRCodeRepository,
) : UseCase<PostPixDecodeQRCodeUseCase.Params, PixDecodeQRCode> {
    override suspend fun invoke(params: Params) =
        repository.postDecodeQRCode(
            PixDecodeQRCodeRequest(
                qrCode = params.qrCode,
                paymentDateIntended = params.paymentDateIntended,
            ),
        )

    data class Params(
        val qrCode: String? = null,
        val paymentDateIntended: String? = null,
    )
}
