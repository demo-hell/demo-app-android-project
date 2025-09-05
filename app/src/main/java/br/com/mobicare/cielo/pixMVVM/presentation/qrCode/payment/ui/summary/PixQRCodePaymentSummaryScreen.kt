package br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import br.com.cielo.libflue.card.CieloCardInformationCompose
import br.com.cielo.libflue.util.res.CieloTextStyle
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.commons.constants.EMPTY_STRING
import br.com.mobicare.cielo.commons.utils.toPtBrRealString
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components.PaymentSummaryBillingDetailsComponent
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components.PaymentSummaryChangePaymentDateComponent
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components.PaymentSummaryDetailDataComponent
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components.PaymentSummaryFooterComponent
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.ui.summary.components.PaymentSummaryTransactionAmountComponent
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.viewModel.PixQRCodePaymentViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.utils.PixQRCodeUtils

@Composable
fun PixQRCodePaymentSummaryScreen(
    viewModel: PixQRCodePaymentViewModel,
    onClickChangePaymentAmount: (String) -> Unit,
    onClickChangeChangeAmount: (String) -> Unit,
    onClickChangeDatePayment: (String) -> Unit,
    onClickWriteMessage: (String) -> Unit,
    onClickCancelTransactionButton: (String) -> Unit,
    onClickToPayButton: (String) -> Unit,
) {
    val pixDecodeQRCode = viewModel.pixDecodeQRCode.observeAsState()
    val finalAmount = viewModel.finalAmount.observeAsState()

    Column {
        pixDecodeQRCode.value?.let {
            PaymentSummaryTransactionAmountComponent(
                enabledChangePaymentAmount = PixQRCodeUtils.isAllowedChangePaymentValue(it),
                enabledChangeChangeAmount = PixQRCodeUtils.isAllowedChangeChangeValue(it),
                paymentAmount = viewModel.paymentAmount.observeAsState(),
                changeAmount = viewModel.changeAmount.observeAsState(),
                onClickChangePaymentAmount,
                onClickChangeChangeAmount,
            )

            if (PixQRCodeUtils.isPixTypeCharge(it)) {
                PaymentSummaryBillingDetailsComponent(it)
            }

            CieloCardInformationCompose(
                message =
                    stringResource(
                        id = R.string.pix_qr_code_payment_summary_payment_alert_free_rate,
                        finalAmount.value?.toPtBrRealString().orEmpty(),
                    ),
                messageTextStyle =
                    CieloTextStyle.mediumMontserrat(
                        fontSize = R.dimen.dimen_12sp,
                        textColor = R.color.cloud_600,
                    ),
                customBackgroundColor = R.color.cloud_100,
                customIcon = R.drawable.ic_circle_alert,
                customIconColor = R.color.color_cloud_600,
                paddingValues =
                    when {
                        PixQRCodeUtils.isPixTypeCharge(it) -> {
                            PaddingValues(horizontal = dimensionResource(id = R.dimen.dimen_16dp))
                        }

                        PixQRCodeUtils.isAllowedChangePaymentValue(it) || PixQRCodeUtils.isAllowedChangeChangeValue(it) -> {
                            PaddingValues(
                                start = dimensionResource(id = R.dimen.dimen_16dp),
                                end = dimensionResource(id = R.dimen.dimen_16dp),
                                top = dimensionResource(id = R.dimen.dimen_3dp),
                            )
                        }

                        else -> {
                            PaddingValues(
                                start = dimensionResource(id = R.dimen.dimen_16dp),
                                end = dimensionResource(id = R.dimen.dimen_16dp),
                                top = dimensionResource(id = R.dimen.dimen_13dp),
                            )
                        }
                    },
            )

            if (PixQRCodeUtils.isPixTypeChangeOrWithdrawal(it).not()) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dimen_16dp)))

                PaymentSummaryChangePaymentDateComponent(
                    selectedDate = viewModel.paymentDate.observeAsState(),
                    isSchedulable = it.isSchedulable == true,
                    onClickChangeDatePayment,
                )
            }

            Spacer(
                modifier =
                    Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.dimen_24dp),
                            bottom = dimensionResource(id = R.dimen.dimen_24dp),
                        ).height(dimensionResource(id = R.dimen.dimen_8dp))
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.surface_neutral)),
            )

            PaymentSummaryDetailDataComponent(it)

            Spacer(
                modifier =
                    Modifier
                        .padding(
                            top = dimensionResource(id = R.dimen.dimen_24dp),
                            bottom = dimensionResource(id = R.dimen.dimen_18dp),
                        ).height(dimensionResource(id = R.dimen.dimen_8dp))
                        .fillMaxWidth()
                        .background(colorResource(id = R.color.surface_neutral)),
            )

            PaymentSummaryFooterComponent(
                optionalMessage = viewModel.optionalMessage.observeAsState(initial = EMPTY_STRING),
                isChangeOrWithdrawal = PixQRCodeUtils.isPixTypeChangeOrWithdrawal(it),
                onClickWriteMessage,
                onClickCancelTransactionButton,
                onClickToPayButton,
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.dimen_20dp)))
        }
    }
}
