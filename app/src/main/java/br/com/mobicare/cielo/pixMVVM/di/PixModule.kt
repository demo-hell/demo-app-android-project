package br.com.mobicare.cielo.pixMVVM.di

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.commons.data.clients.api.newcieloservice.createCieloService
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixAccountBalanceRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixAuthorizationStatusRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixExtractRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixInfringementRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixKeysRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixOnBoardingRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixProfileRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixQRCodeRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixRefundsRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixScheduledSettlementRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.PixTransactionsRemoteDataSourceImpl
import br.com.mobicare.cielo.pixMVVM.data.datasource.remote.PixServiceApi
import br.com.mobicare.cielo.pixMVVM.data.repository.PixAccountBalanceRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixAuthorizationStatusRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixExtractRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixInfringementRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixKeysRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixOnBoardingRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixProfileRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixQRCodeRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixRefundsRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixScheduledSettlementRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.data.repository.PixTransactionsRepositoryImpl
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAccountBalanceRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixAuthorizationStatusRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixExtractRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixInfringementDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixKeysRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixOnBoardingRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixProfileRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixQRCodeRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixRefundsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixScheduledSettlementRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.datasource.PixTransactionsRemoteDataSource
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAccountBalanceRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixAuthorizationStatusRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixExtractRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixInfringementRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixKeysRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixOnBoardingRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixProfileRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixQRCodeRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixRefundsRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixScheduledSettlementRepository
import br.com.mobicare.cielo.pixMVVM.domain.repository.PixTransactionsRepository
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CancelPixTransferScheduleUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.ChangePixProfileUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CreatePixRefundUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.CreatePixScheduledSettlementUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetOnBoardingFulfillmentUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAccountBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixAuthorizationStatusUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixEligibilityInfringementUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixExtractUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixMasterKeyUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixReceiptsScheduledUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundDetailFullUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundDetailUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixRefundReceiptsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferBanksUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferDetailsUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixTransferScheduleDetailUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixUserDataUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.GetPixValidateKeyUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.PostPixDecodeQRCodeUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.PostPixInfringementUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferScheduledBalanceUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferToBankAccountUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.RequestPixTransferWithKeyUseCase
import br.com.mobicare.cielo.pixMVVM.domain.usecase.UpdatePixScheduledSettlementUseCase
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixAccountChangeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixReceiptMethodViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.account.viewmodel.PixScheduledTransferBalanceViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.PixNewExtractDetailViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixRefundReceiptsResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixRefundResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixScheduleResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.detail.utils.handler.PixTransferResultHandler
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.PixHomeExtractViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.extract.home.ui.pager.viewModel.PixExtractPageViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixAccountBalanceViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.home.viewmodel.PixHomeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.selectReason.PixInfringementSelectReasonViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.infringement.ui.sendRequest.PixInfringementSendRequestViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixBankAccountKeyViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.key.viewmodel.PixInsertAllKeysViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.payment.viewModel.PixQRCodePaymentViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.receipt.viewModel.PixQRCodeReceiptViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.qrCode.validateQRCode.viewModel.PixValidateQRCodeViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers.PixRefundDetailSuccessStateHandler
import br.com.mobicare.cielo.pixMVVM.presentation.refund.handlers.PixRefundReceiptsSuccessStateHandler
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixCreateRefundViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.refund.viewmodel.PixRequestRefundViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.router.viewmodel.PixRouterViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.status.PixAuthorizationStatusViewModel
import br.com.mobicare.cielo.pixMVVM.presentation.transfer.viewmodel.PixTransferViewModel
import br.com.mobicare.cielo.pixMVVM.utils.bottomSheets.pixAlertNewLayout.PixAlertNewLayoutBottomSheetHandler
import br.com.mobicare.cielo.pixMVVM.utils.bottomSheets.pixAlertNewLayout.PixAlertNewLayoutViewModel
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule =
    module {
        viewModel { PixRouterViewModel(get(), get(), get(), get()) }
        viewModel { PixHomeViewModel(get(), get(), get(), get()) }
        viewModel { PixAuthorizationStatusViewModel(get()) }
        viewModel { PixNewExtractDetailViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
        viewModel { PixAccountBalanceViewModel(get()) }
        viewModel { PixExtractPageViewModel(get(), get(), get()) }
        viewModel { PixHomeExtractViewModel(get(), get(), get()) }
        viewModel { PixInfringementSelectReasonViewModel(get(), get()) }
        viewModel { PixInfringementSendRequestViewModel(get(), get()) }
        viewModel { PixBankAccountKeyViewModel(get(), get()) }
        viewModel { PixInsertAllKeysViewModel(get(), get()) }
        viewModel { PixReceiptMethodViewModel(get(), get(), get()) }
        viewModel { PixAccountChangeViewModel(get(), get(), get(), get()) }
        viewModel { PixTransferViewModel(get(), get(), get(), get(), get()) }
        viewModel { PixRequestRefundViewModel(get(), get(), get(), get()) }
        viewModel { PixCreateRefundViewModel(get(), get(), get(), get()) }
        viewModel { PixAlertNewLayoutViewModel(get(), get(), get()) }
        viewModel { PixScheduledTransferBalanceViewModel(get(), get()) }
        viewModel { PixValidateQRCodeViewModel(get(), get()) }
        viewModel { PixQRCodePaymentViewModel(get(), get()) }
        viewModel { PixQRCodeReceiptViewModel(get(), get(), get()) }
    }

val useCaseModule =
    module {
        factory { GetOnBoardingFulfillmentUseCase(get()) }
        factory { GetPixAuthorizationStatusUseCase(get()) }
        factory { GetPixAccountBalanceUseCase(get()) }
        factory { GetPixMasterKeyUseCase(get()) }
        factory { GetPixUserDataUseCase(MenuPreference.instance) }
        factory { GetPixExtractUseCase(get()) }
        factory { GetPixRefundDetailFullUseCase(get()) }
        factory { GetPixRefundDetailUseCase(get()) }
        factory { GetPixRefundReceiptsUseCase(get()) }
        factory { GetPixTransferDetailsUseCase(get()) }
        factory { GetPixTransferScheduleDetailUseCase(get()) }
        factory { GetPixTransferBanksUseCase(get()) }
        factory { CancelPixTransferScheduleUseCase(get()) }
        factory { GetPixEligibilityInfringementUseCase(get()) }
        factory { PostPixInfringementUseCase(get()) }
        factory { ChangePixProfileUseCase(get()) }
        factory { CreatePixScheduledSettlementUseCase(get()) }
        factory { UpdatePixScheduledSettlementUseCase(get()) }
        factory { GetPixValidateKeyUseCase(get()) }
        factory { RequestPixTransferWithKeyUseCase(get()) }
        factory { RequestPixTransferToBankAccountUseCase(get()) }
        factory { CreatePixRefundUseCase(get()) }
        factory { RequestPixTransferScheduledBalanceUseCase(get()) }
        factory { GetPixReceiptsScheduledUseCase(get()) }
        factory { PostPixDecodeQRCodeUseCase(get()) }
    }

val repositoryModule =
    module {
        factory<PixOnBoardingRepository> { PixOnBoardingRepositoryImpl(get()) }
        factory<PixAuthorizationStatusRepository> { PixAuthorizationStatusRepositoryImpl(get()) }
        factory<PixKeysRepository> { PixKeysRepositoryImpl(get()) }
        factory<PixAccountBalanceRepository> { PixAccountBalanceRepositoryImpl(get()) }
        factory<PixExtractRepository> { PixExtractRepositoryImpl(get()) }
        factory<PixRefundsRepository> { PixRefundsRepositoryImpl(get()) }
        factory<PixTransactionsRepository> { PixTransactionsRepositoryImpl(get()) }
        factory<PixInfringementRepository> { PixInfringementRepositoryImpl(get()) }
        factory<PixProfileRepository> { PixProfileRepositoryImpl(get()) }
        factory<PixScheduledSettlementRepository> { PixScheduledSettlementRepositoryImpl(get()) }
        factory<PixQRCodeRepository> { PixQRCodeRepositoryImpl(get()) }
    }

val dataSourceModule =
    module {
        factory<PixOnBoardingRemoteDataSource> { PixOnBoardingRemoteDataSourceImpl(get(), get()) }
        factory<PixAuthorizationStatusRemoteDataSource> {
            PixAuthorizationStatusRemoteDataSourceImpl(get(), get())
        }
        factory<PixKeysRemoteDataSource> { PixKeysRemoteDataSourceImpl(get(), get()) }
        factory<PixAccountBalanceRemoteDataSource> { PixAccountBalanceRemoteDataSourceImpl(get(), get()) }
        factory<PixExtractRemoteDataSource> { PixExtractRemoteDataSourceImpl(get(), get()) }
        factory<PixRefundsRemoteDataSource> { PixRefundsRemoteDataSourceImpl(get(), get()) }
        factory<PixTransactionsRemoteDataSource> { PixTransactionsRemoteDataSourceImpl(get(), get()) }
        factory<PixInfringementDataSource> { PixInfringementRemoteDataSourceImpl(get(), get()) }
        factory<PixProfileRemoteDataSource> { PixProfileRemoteDataSourceImpl(get(), get()) }
        factory<PixScheduledSettlementRemoteDataSource> { PixScheduledSettlementRemoteDataSourceImpl(get(), get()) }
        factory<PixQRCodeRemoteDataSource> { PixQRCodeRemoteDataSourceImpl(get(), get()) }
    }

val apiModule =
    module {
        factory("pixAPI") {
            createCieloService(PixServiceApi::class.java, BuildConfig.HOST_API, get())
        }
    }

val utilsModule =
    module {
        factory { PixTransferResultHandler() }
        factory { PixRefundResultHandler() }
        factory { PixScheduleResultHandler() }
        factory { PixRefundReceiptsResultHandler() }
        factory { PixRefundReceiptsSuccessStateHandler() }
        factory { PixRefundDetailSuccessStateHandler() }
        factory { PixAlertNewLayoutBottomSheetHandler(get()) }
    }

val pixModulesList =
    listOf(
        viewModelModule,
        useCaseModule,
        repositoryModule,
        dataSourceModule,
        apiModule,
        utilsModule,
    )
