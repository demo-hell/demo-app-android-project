package br.com.mobicare.cielo

import android.os.Handler
import android.os.Looper
import androidx.fragment.app.FragmentActivity
import br.com.mobicare.cielo.accessManager.AccessManagerRepository
import br.com.mobicare.cielo.adicaoEc.domain.api.AddEcRepository
import br.com.mobicare.cielo.adicaoEc.presentation.presenter.AddEcContract
import br.com.mobicare.cielo.adicaoEc.presentation.presenter.AddEcPresenter
import br.com.mobicare.cielo.allowme.presentation.presenter.AllowMePresenter
import br.com.mobicare.cielo.allowme.presentation.ui.AllowMeContract
import br.com.mobicare.cielo.autoAtendimento.analytics.AutoAtendimentoAnalytics
import br.com.mobicare.cielo.autoAtendimento.analytics.SelfServiceAnalytics
import br.com.mobicare.cielo.autoAtendimento.domain.AutoAtendimentoDataSource
import br.com.mobicare.cielo.autoAtendimento.domain.api.AutoAtendimentoRepository
import br.com.mobicare.cielo.autoAtendimento.presentation.presenter.AutoAtendimentoPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.InstalacaoMaquinaChooseAddressContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.InstalacaoMaquinaChooseAddressPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress.InstalacaoMaquinaChooseAddressNewContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.address.newAddress.InstalacaoMaquinaChooseaddressNewPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.contato.InstalacaoMaquinaAdicionalContatoContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.contato.InstalacaoMaquinaAdicionalContatoPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.horario.InstalacaoMaquinaAdicionalHorarioContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.horario.InstalacaoMaquinaAdicionalHorarioPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.quantidade.InstalacaoMaquinaAdicionalQuantidadeContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.quantidade.InstalacaoMaquinaAdicionalQuantidadePresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.resumoEfetivacao.InstalacaoMaquinaAdicionalResumoEfetivacaoContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.instalacaoMaquinaAdicional.fragments.resumoEfetivacao.InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequest.OpenRequestContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequest.OpenRequestPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestMachines.OpenRequestMachinesContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestMachines.OpenRequestMachinesPresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestResume.OpenRequestResumeContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.activity.trocaMaquina.fragments.openRequestResume.OpenRequestResumePresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.machine.RequestMachineContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.machine.RequestMachinePresenter
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoMateriasFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfService.AutoAtendimentoVenderMaisFragment
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfServiceSupply.SelfServiceSupplyContract
import br.com.mobicare.cielo.autoAtendimento.presentation.ui.fragment.selfServiceSupply.SelfServiceSupplyPresenter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoBanksContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.BalcaoRecebiveisExtratoContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.analytics.ReceivablesAnalyticsGA4
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.interactor.BalcaoRecebiveisExtratoBanksInteractor
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.interactor.BalcaoRecebiveisExtratoInteractor
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter.BalcaoRecebiveisExtratoBanksPresenter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter.BalcaoRecebiveisExtratoDetailsNegotiationsPresenter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.presenter.BalcaoRecebiveisExtratoPresenter
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.FiltroVendasUnitariasContract
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.fragment.ExtratoRecebiveisVendasUnitariasView
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.interactor.FiltroVendasUnitariasInteractor
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter.ExtratoRecebiveisVendasUnitariasPresenterImpl
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.unitsales.presenter.FiltroVendasUtitariasPresenter
import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoDataSource
import br.com.mobicare.cielo.centralDeAjuda.data.clients.CentralAjudaLogadoRepository
import br.com.mobicare.cielo.centralDeAjuda.data.clients.api.CentralDeAjudaAPIDataSource
import br.com.mobicare.cielo.centralDeAjuda.data.clients.managers.CentralDeAjudaRepository
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.CentralAjudaPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.FrequentQuestionHelpCenterPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.LoggedHelpCenterContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.presenter.QuestionAndAnswerPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.CentralAjudaContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.categories.CentralAjudaCategoriesPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.CentralAjudaContatosPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message.OmbudsmanMessageContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.message.OmbudsmanMessagePresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.personaldata.OmbudsmanPersonalDataContract
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.contatos.ombudsman.personaldata.OmbudsmanPersonalDataPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.perguntas.CentralAjudaPerguntasPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCategoriasPresenter
import br.com.mobicare.cielo.centralDeAjuda.presentation.ui.subcategorias.CentralAjudaSubCatregoriasContract
import br.com.mobicare.cielo.centralDeAjuda.search.HelpCenterSearchContract
import br.com.mobicare.cielo.centralDeAjuda.search.HelpCenterSearchPresenter
import br.com.mobicare.cielo.changeEc.ChangeEcDataSource
import br.com.mobicare.cielo.changeEc.ChangeEcRepository
import br.com.mobicare.cielo.coil.CoilDataDataSource
import br.com.mobicare.cielo.coil.CoilRepository
import br.com.mobicare.cielo.coil.presentation.adress.ServiceAddressPresenter
import br.com.mobicare.cielo.coil.presentation.choose.CoilChoosePresenter
import br.com.mobicare.cielo.commons.analytics.DatadogEvent
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPI
import br.com.mobicare.cielo.commons.data.clients.api.CieloAPIServices
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.dataSource.PosVirtualWhiteListDataSource
import br.com.mobicare.cielo.commons.data.filter.FilterRepositoryNetwork
import br.com.mobicare.cielo.commons.data.repository.PosVirtualWhiteListRepositoryImpl
import br.com.mobicare.cielo.commons.presentation.BaseView
import br.com.mobicare.cielo.commons.presentation.filter.FilterContract
import br.com.mobicare.cielo.commons.presentation.filter.FilterReceivablesPresenter
import br.com.mobicare.cielo.commons.router.deeplink.DeeplinkRouter
import br.com.mobicare.cielo.commons.secure.presentation.ui.presenter.OtpContract
import br.com.mobicare.cielo.commons.secure.presentation.ui.presenter.OtpPresenter
import br.com.mobicare.cielo.commons.ui.help.HelpMainPresenter
import br.com.mobicare.cielo.commons.utils.totp.SystemWallClock
import br.com.mobicare.cielo.commons.utils.totp.TotpClock
import br.com.mobicare.cielo.commons.utils.totp.TotpCounter
import br.com.mobicare.cielo.commons.warning.WarningAnalytics
import br.com.mobicare.cielo.commons.warning.WarningModalPresenter
import br.com.mobicare.cielo.debitoEmConta.analytics.DebitAccountGA4
import br.com.mobicare.cielo.deeplink.DeepLinkContract
import br.com.mobicare.cielo.deeplink.DeepLinkDataSource
import br.com.mobicare.cielo.deeplink.DeepLinkInteractor
import br.com.mobicare.cielo.deeplink.DeepLinkPresenter
import br.com.mobicare.cielo.dirf.DirfContract
import br.com.mobicare.cielo.dirf.DirfInteractor
import br.com.mobicare.cielo.dirf.DirfPresenter
import br.com.mobicare.cielo.dirf.analytics.DirfAnalytics
import br.com.mobicare.cielo.dirf.analytics.DirfGA4
import br.com.mobicare.cielo.extrato.data.clients.api.StatementApiDataSource
import br.com.mobicare.cielo.extrato.data.managers.CreditCardStatementRepository
import br.com.mobicare.cielo.extrato.presentation.presenter.ExtratoPresenter
import br.com.mobicare.cielo.extrato.presentation.presenter.ExtratoTimeLinePresenter
import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoContract
import br.com.mobicare.cielo.extrato.presentation.ui.ExtratoTimeLineContract
import br.com.mobicare.cielo.featureToggle.FeatureToggleContract
import br.com.mobicare.cielo.featureToggle.api.FeatureToggleAPIDataSource
import br.com.mobicare.cielo.featureToggle.data.clients.FeatureTogglePreference
import br.com.mobicare.cielo.featureToggle.data.managers.FeatureToggleRepository
import br.com.mobicare.cielo.featureToggle.presenter.FeatureTogglePresenter
import br.com.mobicare.cielo.home.presentation.analytics.HomeAnalytics
import br.com.mobicare.cielo.home.presentation.analytics.HomeGA4
import br.com.mobicare.cielo.home.presentation.main.BannersContract
import br.com.mobicare.cielo.home.presentation.main.MenuContract
import br.com.mobicare.cielo.home.presentation.main.OthersMenuContract
import br.com.mobicare.cielo.home.presentation.main.presenter.FeeAndPlansHomePresenter
import br.com.mobicare.cielo.home.presentation.main.presenter.HomePresenter
import br.com.mobicare.cielo.home.presentation.main.presenter.OthersMenuPresenter
import br.com.mobicare.cielo.home.presentation.main.ui.fragment.FeeAndPlansHomeContract
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MeusRecebimentosHomeContract
import br.com.mobicare.cielo.home.presentation.meusrecebimentonew.MyReceiptsHomePresenter
import br.com.mobicare.cielo.idOnboarding.IDOnboardingApi
import br.com.mobicare.cielo.idOnboarding.IDOnboardingRepository
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP1AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2Analytics
import br.com.mobicare.cielo.idOnboarding.analytics.IDOnboardingP2AnalyticsGA
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouterContract
import br.com.mobicare.cielo.idOnboarding.router.IDOnboardingRouterPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDigitalDocument.IDOnboardingDigitalDocumentContract
import br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDigitalDocument.IDOnboardingDigitalDocumentPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDocument.IDOnboardingUploadDocumentContract
import br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendDocument.IDOnboardingUploadDocumentPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendSelfie.IDOnboardingUploadSelfieContract
import br.com.mobicare.cielo.idOnboarding.updateUser.docPictures.sendSelfie.IDOnboardingUploadSelfiePresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.IDOnboardingContract
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.IDOnboardingPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.status.IDOnboardingP1CompletionStatusContract
import br.com.mobicare.cielo.idOnboarding.updateUser.onboarding.status.IDOnboardingP1CompletionStatusPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.IDOnboardingValidateP1PolicyContract
import br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.IDOnboardingValidateP1PolicyPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.p1Policy.benefits.IDOnboardingBenefitsInfoP1PolicyPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.p2Policy.IDOnboardingValidateP2PolicyContract
import br.com.mobicare.cielo.idOnboarding.updateUser.p2Policy.IDOnboardingValidateP2PolicyPresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.userInfo.IDOnboardingUpdateForeignPhoneContract
import br.com.mobicare.cielo.idOnboarding.updateUser.userInfo.IDOnboardingUpdateForeignPhonePresenter
import br.com.mobicare.cielo.idOnboarding.updateUser.userInfo.IDOnboardingUpdateUserPresenter
import br.com.mobicare.cielo.interactbannersoffers.InteractBannersPresenterImpl
import br.com.mobicare.cielo.interactbannersoffers.InteractBannersView
import br.com.mobicare.cielo.interactbannersoffers.analytics.InteractBannersAnalytics
import br.com.mobicare.cielo.interactbannersoffers.repository.InteractBannerRepository
import br.com.mobicare.cielo.interactbannersoffers.termoAceite.TermoAceiteContract
import br.com.mobicare.cielo.interactbannersoffers.termoAceite.TermoAceitePresenter
import br.com.mobicare.cielo.internaluser.InternalUserView
import br.com.mobicare.cielo.internaluser.presenter.InternalUserPresenterImpl
import br.com.mobicare.cielo.lgpd.LgpdContract
import br.com.mobicare.cielo.lgpd.LgpdPresenter
import br.com.mobicare.cielo.lgpd.data.LgpdRepositoryImpl
import br.com.mobicare.cielo.lgpd.data.remote.LgpdRemoteDataSourceImpl
import br.com.mobicare.cielo.lighthouse.presentation.presenter.LightHouseContract
import br.com.mobicare.cielo.lighthouse.presentation.presenter.LightHousePresenter
import br.com.mobicare.cielo.login.data.clients.api.LoginAPIDataSource
import br.com.mobicare.cielo.login.data.managers.LoginRepository
import br.com.mobicare.cielo.machine.MachineDataSource
import br.com.mobicare.cielo.machine.MachineRepository
import br.com.mobicare.cielo.main.AppMenuLocalDataSource
import br.com.mobicare.cielo.main.UserInformationRemoteDataSource
import br.com.mobicare.cielo.main.UserInformationRepository
import br.com.mobicare.cielo.main.data.clients.api.AppMenuRemoteDataSource
import br.com.mobicare.cielo.main.data.managers.AppMenuRepository
import br.com.mobicare.cielo.main.presentation.ServicesContract
import br.com.mobicare.cielo.main.presentation.analytics.ServicesGA4
import br.com.mobicare.cielo.main.presentation.data.clients.local.MenuPreference
import br.com.mobicare.cielo.main.presentation.presenter.MainBottomNavigationPresenter
import br.com.mobicare.cielo.main.presentation.presenter.ServicesPresenter
import br.com.mobicare.cielo.main.presentation.ui.MainBottomNavigationContract
import br.com.mobicare.cielo.merchant.data.MerchantRepositoryImpl
import br.com.mobicare.cielo.merchant.data.remote.MerchantRemoteDataSourceImpl
import br.com.mobicare.cielo.meuCadastroDomicilio.MeuCadastroDomicilioDataSource
import br.com.mobicare.cielo.meuCadastroDomicilio.MeuCadastroDomicilioRespository
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount.AddAccountContract
import br.com.mobicare.cielo.meuCadastroDomicilio.presetation.fragment.addAccount.transferAccount.AddAccountPresenter
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoDataSource
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroNovoRepository
import br.com.mobicare.cielo.meuCadastroNovo.MeuCadastroRepository
import br.com.mobicare.cielo.meuCadastroNovo.analytics.MeuCadastroAnalytics
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.DadosContaPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.DadosEstabelecimentoPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.DadosUsuarioPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.TrocaDomicilioSolicitacoesPresenterImpl
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.UserAddressContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.presenter.UserEditAddressPresenterImpl
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.MeuCadastroContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.TrocaDomicilioSolicitacoesView
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato.EditarDadosContatoFragment
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.contato.EditarDadosContatoPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.AlertaCadastralContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.establishment.DadosProprietarioPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.nomeFantasia.EditarDadosNomeFantasiaFragment
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.nomeFantasia.EditarDadosNomeFantasiaPresenter
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.owner.EditarDadosProprietarioContract
import br.com.mobicare.cielo.meuCadastroNovo.presetantion.ui.fragment.owner.EditarDadosProprietarioPresenter
import br.com.mobicare.cielo.meusCartoes.BankTransactionRepository
import br.com.mobicare.cielo.meusCartoes.CreditCardsNewDataSource
import br.com.mobicare.cielo.meusCartoes.CreditCardsNewRespository
import br.com.mobicare.cielo.meusCartoes.CreditCardsRepository
import br.com.mobicare.cielo.meusCartoes.PrepaidRepository
import br.com.mobicare.cielo.meusCartoes.clients.api.BankDatasource
import br.com.mobicare.cielo.meusCartoes.clients.api.CreditCardsDataSource
import br.com.mobicare.cielo.meusCartoes.clients.api.PrepaidDataSource
import br.com.mobicare.cielo.meusCartoes.presentation.ui.CreditCardsContract
import br.com.mobicare.cielo.meusCartoes.presentation.ui.LastTransactionsContract
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.cardActivateCateno.CardNewPasswordContract
import br.com.mobicare.cielo.meusCartoes.presentation.ui.activity.cardActivateCateno.CardNewPasswordPresenter
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountPresenter
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputContract
import br.com.mobicare.cielo.meusCartoes.presenter.BankAccountToTransferInputPresenter
import br.com.mobicare.cielo.meusCartoes.presenter.CreditCardsPresenter
import br.com.mobicare.cielo.meusCartoes.presenter.LastTransactionsPresenter
import br.com.mobicare.cielo.meusCartoes.presenter.PaymentContract
import br.com.mobicare.cielo.meusCartoes.presenter.PrepaidPaymentPresenter
import br.com.mobicare.cielo.meusRecebimentos.presentation.presenter.MeusRecebimentosInteractorImpl
import br.com.mobicare.cielo.meusrecebimentosnew.analytics.MyReceivablesGA4
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.MeusRecebimentosGraficoRepository
import br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada.VisaoDetalhadaMeusRecebimentosContract
import br.com.mobicare.cielo.meusrecebimentosnew.visaodetalhada.VisaoDetalhadaMeusRecebimentosPresenter
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.VisaoSumarizadaMeusRecebimentosContract
import br.com.mobicare.cielo.meusrecebimentosnew.visaosumarizada.VisaoSumarizadaMeusRecebimentosPresenter
import br.com.mobicare.cielo.mfa.MfaApiDataSource
import br.com.mobicare.cielo.mfa.MfaRepository
import br.com.mobicare.cielo.mfa.activation.PutValuePresenterImpl
import br.com.mobicare.cielo.mfa.activation.PutValueView
import br.com.mobicare.cielo.mfa.activation.repository.PutValueInteractor
import br.com.mobicare.cielo.mfa.activation.repository.PutValueInteractorImpl
import br.com.mobicare.cielo.mfa.activation.repository.PutValueRepository
import br.com.mobicare.cielo.mfa.analytics.MfaAnalytics
import br.com.mobicare.cielo.mfa.merchantActivition.MerchantChallengerActivationContract
import br.com.mobicare.cielo.mfa.merchantActivition.MerchantChallengerActivationPresenter
import br.com.mobicare.cielo.mfa.merchantstatus.challenge.MerchantValidateChallengePresenterImpl
import br.com.mobicare.cielo.mfa.merchantstatus.challenge.MerchantValidateChallengeView
import br.com.mobicare.cielo.mfa.resume.ResumeBankAndCNPJContract
import br.com.mobicare.cielo.mfa.resume.ResumeBankAndCNPJPresenter
import br.com.mobicare.cielo.mfa.router.MfaRouteHandler
import br.com.mobicare.cielo.mfa.router.MfaRouterContract
import br.com.mobicare.cielo.mfa.router.MfaRouterPresenter
import br.com.mobicare.cielo.mfa.router.userWithP2.MfaTokenConfigurationContract
import br.com.mobicare.cielo.mfa.router.userWithP2.MfaTokenConfigurationPresenter
import br.com.mobicare.cielo.mfa.selecioneBanco.SelecioneBancoMfaContract
import br.com.mobicare.cielo.mfa.selecioneBanco.SelecioneBancoMfaPresenter
import br.com.mobicare.cielo.mfa.token.CieloMfaTokenGenerator
import br.com.mobicare.cielo.mfa.validationprevioustoken.ValidationPreviousTokenContract
import br.com.mobicare.cielo.mfa.validationprevioustoken.ValidationPreviousTokenPresenter
import br.com.mobicare.cielo.migration.MigrationDataSource
import br.com.mobicare.cielo.migration.MigrationRepository
import br.com.mobicare.cielo.minhasVendas.datasource.MinhasVendasDataSource
import br.com.mobicare.cielo.minhasVendas.detalhe.MinhasVendasDetalhesContract
import br.com.mobicare.cielo.minhasVendas.detalhe.MinhasVendasDetalhesPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelamentoInteractor
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.CancelamentoPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.OnCancelamentoContract
import br.com.mobicare.cielo.minhasVendas.fragments.consolidado.MinhasVendasConsolidadoContract
import br.com.mobicare.cielo.minhasVendas.fragments.consolidado.MinhasVendasConsolidadoPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.filter.MinhasVendasFilterBottomSheetContract
import br.com.mobicare.cielo.minhasVendas.fragments.filter.MinhasVendasFilterBottomSheetPresenter
import br.com.mobicare.cielo.minhasVendas.fragments.online.MinhasVendasOnlineContract
import br.com.mobicare.cielo.minhasVendas.fragments.online.MinhasVendasOnlinePresenter
import br.com.mobicare.cielo.minhasVendas.fragments.trasacoes.MinhasVendasTransacoesContract
import br.com.mobicare.cielo.minhasVendas.fragments.trasacoes.MinhasVendasTransacoesPresenter
import br.com.mobicare.cielo.minhasVendas.repository.MinhasVendasRepository
import br.com.mobicare.cielo.mySales.analytics.MySalesGA4
import br.com.mobicare.cielo.newLogin.LoginDataSource
import br.com.mobicare.cielo.newLogin.NewLoginContract
import br.com.mobicare.cielo.newLogin.NewLoginPresenter
import br.com.mobicare.cielo.notification.NotificationDatasource
import br.com.mobicare.cielo.notification.NotificationRepository
import br.com.mobicare.cielo.notification.list.ListNotificationPresenter
import br.com.mobicare.cielo.notification.presenter.WelcomeNotificationContract
import br.com.mobicare.cielo.notification.presenter.WelcomeNotificationPresenter
import br.com.mobicare.cielo.orders.OrdersDataSource
import br.com.mobicare.cielo.orders.OrdersRepository
import br.com.mobicare.cielo.pagamentoLink.PagamentoLinkDataSource
import br.com.mobicare.cielo.pagamentoLink.PagamentoLinkRespository
import br.com.mobicare.cielo.pagamentoLink.clients.api.LinkApiDataSource
import br.com.mobicare.cielo.pagamentoLink.domains.PgLinkDataDataSource
import br.com.mobicare.cielo.pagamentoLink.managers.LinkRepository
import br.com.mobicare.cielo.pagamentoLink.orders.LinkOrdersPresenterImpl
import br.com.mobicare.cielo.pagamentoLink.orders.LinkOrdersView
import br.com.mobicare.cielo.pagamentoLink.orders.orderdetail.LinkOrderDetailPresenterImpl
import br.com.mobicare.cielo.pagamentoLink.orders.orderdetail.LinkOrderDetailView
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersInteractor
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersInteractorImpl
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersRepository
import br.com.mobicare.cielo.pagamentoLink.presentation.presenter.CreateLinkPaymentPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.presenter.LinkContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.deliveryLoggi.DeliveryLoggiConfigurationContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.deliveryLoggi.DeliveryLoggiConfigurationPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio.DeliveryContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio.DeliveryPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio.FormaEnvioContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.formaEnvio.FormaEnvioPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.tipoVenda.TipoVendaPagamentoPorLinkContract
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.engine.tipoVenda.TipoVendaPagamentoPorLinkPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog.PgLinkDetailPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.fragment.dialog.PgLinkDetailReporitory
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.linkPaymentCreated.LinkPaymentCreatedPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.listAtivos.PagamentoLinkListAtivosPresenter
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.SolicitationMotoboyPresenterImpl
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.SolicitationMotoboyView
import br.com.mobicare.cielo.pedidos.api.OrderMachineDataSource
import br.com.mobicare.cielo.pedidos.managers.OrderMachineRepository
import br.com.mobicare.cielo.pedidos.managers.OrderMachineServiceRepository
import br.com.mobicare.cielo.pedidos.orderdetail.OrderDetailsMachinesTrackingContract
import br.com.mobicare.cielo.pedidos.orderdetail.OrderDetailsMachinesTrackingPresenter
import br.com.mobicare.cielo.pedidos.presentation.presenter.OrderMachineContract
import br.com.mobicare.cielo.pedidos.presentation.presenter.OrderMachinePresenter
import br.com.mobicare.cielo.pix.api.PixAPI
import br.com.mobicare.cielo.pix.api.account.PixAccountDataSource
import br.com.mobicare.cielo.pix.api.account.PixAccountRepository
import br.com.mobicare.cielo.pix.api.claim.PixClaimDataSource
import br.com.mobicare.cielo.pix.api.claim.PixClaimRepository
import br.com.mobicare.cielo.pix.api.extract.PixExtractDataSource
import br.com.mobicare.cielo.pix.api.extract.PixExtractRepository
import br.com.mobicare.cielo.pix.api.extract.reversal.PixReversalDataSource
import br.com.mobicare.cielo.pix.api.extract.reversal.PixReversalRepository
import br.com.mobicare.cielo.pix.api.keys.PixKeysDataSource
import br.com.mobicare.cielo.pix.api.keys.PixKeysRepository
import br.com.mobicare.cielo.pix.api.myLimits.PixMyLimitsDataSource
import br.com.mobicare.cielo.pix.api.myLimits.PixMyLimitsRepository
import br.com.mobicare.cielo.pix.api.myLimits.timeManagement.PixTimeManagementDataSource
import br.com.mobicare.cielo.pix.api.myLimits.timeManagement.PixTimeManagementRepository
import br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations.PixTrustedDestinationsDataSource
import br.com.mobicare.cielo.pix.api.myLimits.trustedDestinations.PixTrustedDestinationsRepository
import br.com.mobicare.cielo.pix.api.onboarding.PixRepository
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeDataSource
import br.com.mobicare.cielo.pix.api.qrCode.PixQRCodeRepository
import br.com.mobicare.cielo.pix.api.transfer.PixTransferDataSource
import br.com.mobicare.cielo.pix.api.transfer.PixTransferRepository
import br.com.mobicare.cielo.pix.ui.extract.account.management.PixTransitoryAccountManagementContract
import br.com.mobicare.cielo.pix.ui.extract.account.management.PixTransitoryAccountManagementPresenter
import br.com.mobicare.cielo.pix.ui.extract.adapter.PixExtractAdapter
import br.com.mobicare.cielo.pix.ui.extract.detail.PixExtractDetailContract
import br.com.mobicare.cielo.pix.ui.extract.detail.PixExtractDetailPresenter
import br.com.mobicare.cielo.pix.ui.extract.filter.PixExtractFilterBottomSheetContract
import br.com.mobicare.cielo.pix.ui.extract.filter.PixExtractFilterBottomSheetPresenter
import br.com.mobicare.cielo.pix.ui.extract.home.PixExtractContract
import br.com.mobicare.cielo.pix.ui.extract.home.PixExtractPresenter
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsContract
import br.com.mobicare.cielo.pix.ui.extract.home.tabs.PixExtractTabsPresenter
import br.com.mobicare.cielo.pix.ui.extract.onboarding.PixOnboardingExtractContract
import br.com.mobicare.cielo.pix.ui.extract.onboarding.PixOnboardingExtractPresenter
import br.com.mobicare.cielo.pix.ui.extract.receipt.PixScheduledTransactionReceiptContract
import br.com.mobicare.cielo.pix.ui.extract.receipt.PixScheduledTransactionReceiptPresenter
import br.com.mobicare.cielo.pix.ui.extract.reversal.PixReversalContract
import br.com.mobicare.cielo.pix.ui.extract.reversal.PixReversalPresenter
import br.com.mobicare.cielo.pix.ui.extract.reversal.receipt.PixReversalReceiptContract
import br.com.mobicare.cielo.pix.ui.extract.reversal.receipt.PixReversalReceiptPresenter
import br.com.mobicare.cielo.pix.ui.extract.router.PixExtractRouterContract
import br.com.mobicare.cielo.pix.ui.extract.router.PixExtractRouterPresenter
import br.com.mobicare.cielo.pix.ui.home.account.PixFreeMovementAccountManagementContract
import br.com.mobicare.cielo.pix.ui.home.account.PixFreeMovementAccountManagementPresenter
import br.com.mobicare.cielo.pix.ui.home.onboarding.PixOnboardingHomeContract
import br.com.mobicare.cielo.pix.ui.home.onboarding.PixOnboardingHomePresenter
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.PixMyKeysContract
import br.com.mobicare.cielo.pix.ui.keys.myKeys.home.PixMyKeysPresenter
import br.com.mobicare.cielo.pix.ui.keys.myKeys.register.insert.PixInsertKeyToRegisterContract
import br.com.mobicare.cielo.pix.ui.keys.myKeys.register.insert.PixInsertKeyToRegisterPresenter
import br.com.mobicare.cielo.pix.ui.keys.myKeys.register.registration.PixKeyRegistrationContract
import br.com.mobicare.cielo.pix.ui.keys.myKeys.register.registration.PixKeyRegistrationPresenter
import br.com.mobicare.cielo.pix.ui.keys.myKeys.register.validation.PixValidationCodeContract
import br.com.mobicare.cielo.pix.ui.keys.myKeys.register.validation.PixValidationCodePresenter
import br.com.mobicare.cielo.pix.ui.keys.onboarding.PixKeysOnboardingContract
import br.com.mobicare.cielo.pix.ui.keys.onboarding.PixKeysOnboardingPresenter
import br.com.mobicare.cielo.pix.ui.mylimits.timemanagement.PixMyLimitsTimeManagementContract
import br.com.mobicare.cielo.pix.ui.mylimits.timemanagement.PixMyLimitsTimeManagementPresenter
import br.com.mobicare.cielo.pix.ui.mylimits.transactions.PixMyLimitsTransactionsContract
import br.com.mobicare.cielo.pix.ui.mylimits.transactions.PixMyLimitsTransactionsPresenter
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.PixMyLimitsTrustedDestinationsContract
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.PixMyLimitsTrustedDestinationsPresenter
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.add.PixMyLimitsAddNewTrustedDestinationContract
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.add.PixMyLimitsAddNewTrustedDestinationPresenter
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.detail.PixMyLimitsTrustedDestinationsDetailContract
import br.com.mobicare.cielo.pix.ui.mylimits.trusteddestinations.detail.PixMyLimitsTrustedDestinationsDetailPresenter
import br.com.mobicare.cielo.pix.ui.mylimits.withdrawandcharge.PixMyLimitsWithdrawAndChargeContract
import br.com.mobicare.cielo.pix.ui.mylimits.withdrawandcharge.PixMyLimitsWithdrawAndChargePresenter
import br.com.mobicare.cielo.pix.ui.qrCode.charge.generate.PixGenerateQRCodeContract
import br.com.mobicare.cielo.pix.ui.qrCode.charge.generate.PixGenerateQRCodePresenter
import br.com.mobicare.cielo.pix.ui.qrCode.decode.copyPaste.PixCopyPasteQRCodeContract
import br.com.mobicare.cielo.pix.ui.qrCode.decode.copyPaste.PixCopyPasteQRCodePresenter
import br.com.mobicare.cielo.pix.ui.qrCode.decode.read.PixQrCodeAnalyzer
import br.com.mobicare.cielo.pix.ui.qrCode.decode.read.PixReadQRCodeContract
import br.com.mobicare.cielo.pix.ui.qrCode.decode.read.PixReadQRCodePresenter
import br.com.mobicare.cielo.pix.ui.qrCode.decode.receipt.PixBillingReceiptContract
import br.com.mobicare.cielo.pix.ui.qrCode.decode.receipt.PixBillingReceiptPresenter
import br.com.mobicare.cielo.pix.ui.qrCode.decode.summary.PixDecodeQRCodeSummaryContract
import br.com.mobicare.cielo.pix.ui.qrCode.decode.summary.PixDecodeQRCodeSummaryPresenter
import br.com.mobicare.cielo.pix.ui.terms.PixTermContract
import br.com.mobicare.cielo.pix.ui.terms.PixTermPresenter
import br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.PixSelectBankContract
import br.com.mobicare.cielo.pix.ui.transfer.agency.selectBank.PixSelectBankPresenter
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountContract
import br.com.mobicare.cielo.pix.ui.transfer.amount.PixEnterTransferAmountPresenter
import br.com.mobicare.cielo.pix.ui.transfer.key.PixInsertKeyContract
import br.com.mobicare.cielo.pix.ui.transfer.key.PixInsertKeyPresenter
import br.com.mobicare.cielo.pix.ui.transfer.receipt.PixTransferReceiptContract
import br.com.mobicare.cielo.pix.ui.transfer.receipt.PixTransferReceiptPresenter
import br.com.mobicare.cielo.pix.ui.transfer.summary.PixTransferSummaryContract
import br.com.mobicare.cielo.pix.ui.transfer.summary.PixTransferSummaryPresenter
import br.com.mobicare.cielo.recebaMais.api.RecebaMaisApiDataSource
import br.com.mobicare.cielo.recebaMais.managers.MyResumeRepository
import br.com.mobicare.cielo.recebaMais.managers.RecebaMaisHelpRepository
import br.com.mobicare.cielo.recebaMais.managers.RecebaMaisRepository
import br.com.mobicare.cielo.recebaMais.managers.UserLoanRepository
import br.com.mobicare.cielo.recebaMais.presentation.presenter.MyResumeContract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.MyResumePresenter
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanContract
import br.com.mobicare.cielo.recebaMais.presentation.presenter.UserLoanPresenter
import br.com.mobicare.cielo.recebaMais.presentation.ui.presenter.UserLoanDataPresenter
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoDataSource
import br.com.mobicare.cielo.recebaRapido.api.RecebaRapidoRepository
import br.com.mobicare.cielo.recebaRapido.cancellation.selectreason.RecebaRapidoCancellationReasonPresenterImpl
import br.com.mobicare.cielo.recebaRapido.cancellation.selectreason.RecebaRapidoCancellationReasonView
import br.com.mobicare.cielo.research.ResearchContract
import br.com.mobicare.cielo.research.ResearchPresenter
import br.com.mobicare.cielo.security.presentation.presenter.BottomSheetPresenter
import br.com.mobicare.cielo.security.presentation.presenter.SecurityPresenter
import br.com.mobicare.cielo.security.presentation.ui.BottomSheetSecurityContract
import br.com.mobicare.cielo.security.presentation.ui.SecurityContract
import br.com.mobicare.cielo.selfRegistration.register.SelfRegistrationRepository
import br.com.mobicare.cielo.selfRegistration.register.SelfRegistrationSource
import br.com.mobicare.cielo.home.utils.BrandsRepository
import br.com.mobicare.cielo.solesp.api.SolespAPI
import br.com.mobicare.cielo.solesp.api.SolespDataSource
import br.com.mobicare.cielo.solesp.api.SolespRepository
import br.com.mobicare.cielo.solesp.ui.infoSend.SolespInfoSendContract
import br.com.mobicare.cielo.solesp.ui.infoSend.SolespInfoSendPresenter
import br.com.mobicare.cielo.solesp.ui.start.SolespStartContract
import br.com.mobicare.cielo.solesp.ui.start.SolespStartPresenter
import br.com.mobicare.cielo.splash.data.clients.api.SplashAPIDataSource
import br.com.mobicare.cielo.splash.data.clients.local.ConfigurationPreference
import br.com.mobicare.cielo.splash.data.managers.SplashRepository
import br.com.mobicare.cielo.splash.presentation.presenter.SplashPresenter
import br.com.mobicare.cielo.splash.presentation.ui.SplashContract
import br.com.mobicare.cielo.superlink.analytics.SuperLinkAnalytics
import br.com.mobicare.cielo.suporteTecnico.TechnicalSupportContract
import br.com.mobicare.cielo.suporteTecnico.data.managers.TechnicalSupportRemoteRepository
import br.com.mobicare.cielo.suporteTecnico.ui.presenter.TechnicalSupportPresenter
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneAnalytics
import br.com.mobicare.cielo.tapOnPhone.analytics.TapOnPhoneGA4
import br.com.mobicare.cielo.tapOnPhone.data.api.TapOnPhoneAPI
import br.com.mobicare.cielo.tapOnPhone.data.repository.TapOnPhoneAccreditationRepositoryImpl
import br.com.mobicare.cielo.tapOnPhone.data.repository.TapOnPhoneEligibilityRepositoryImpl
import br.com.mobicare.cielo.tapOnPhone.data.repository.TapOnPhoneTerminalRepositoryImpl
import br.com.mobicare.cielo.tapOnPhone.data.source.RemoteTapOnPhoneAccreditationDataSource
import br.com.mobicare.cielo.tapOnPhone.data.source.RemoteTapOnPhoneEligibilityDataSource
import br.com.mobicare.cielo.tapOnPhone.data.source.RemoteTapOnPhoneTerminalDataSource
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.TapOnPhoneAccreditationOfferContract
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.offer.TapOnPhoneAccreditationOfferPresenter
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term.TapOnPhoneTermAndConditionContract
import br.com.mobicare.cielo.tapOnPhone.presentation.accreditation.term.TapOnPhoneTermAndConditionPresenter
import br.com.mobicare.cielo.tapOnPhone.presentation.impersonate.TapOnPhoneImpersonateContract
import br.com.mobicare.cielo.tapOnPhone.presentation.impersonate.TapOnPhoneImpersonatePresenter
import br.com.mobicare.cielo.tapOnPhone.presentation.router.TapOnPhoneContract
import br.com.mobicare.cielo.tapOnPhone.presentation.router.TapOnPhonePresenter
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.ready.TapOnPhoneTerminalReadyPresenter
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminal
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminalContract
import br.com.mobicare.cielo.tapOnPhone.presentation.terminal.setup.TapOnPhoneSetupTerminalPresenter
import br.com.mobicare.cielo.taxaPlanos.FeeAndPlansMainFragment
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoDataSource
import br.com.mobicare.cielo.taxaPlanos.TaxaPlanoRepository
import br.com.mobicare.cielo.taxaPlanos.analytics.FeesPlansGA4
import br.com.mobicare.cielo.taxaPlanos.doSeuJeito.DoSeuJeitoTaxasPlanosPresenter
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.api.PostecipadoRepository
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre.PlanFreeTaxesPresenterContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre.TaxasPlanosCieloContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.cielolivre.TaxasPlanosCieloPresenter
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.controle.TaxasPlanosCieloControleContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.controle.TaxasPlanosCieloControlePresenter
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.fragment.doSeuJeito.taxas.DoSeuJeitoTaxasPlanosContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.main.TaxaPlanosMainPresenter
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.myPlan.TaxaPlanosPlanContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.myPlan.TaxaPlanosPlanPresenter
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoMeuAluguelContract
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PostecipadoMeuAluguelPresenter
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.presenter.PlanFreeTaxesPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import org.koin.dsl.module.module

val presenterModule: Module = module {

    single("cieloApiServices") {
        CieloAPIServices.getInstance(
            androidContext(),
            BuildConfig.HOST_API
        )
    }

    single("cieloAPI") {
        CieloAPIServices
            .getInstance(androidContext(), BuildConfig.HOST_API)
            .createAPI(CieloAPI::class.java) as CieloAPI
    }

    single {
        TaxaPlanoRepository(TaxaPlanoDataSource(androidContext()))
    }

    factory("bankTransferPresenter") { (view: CreditCardsContract.BankAccountView) ->

        BankAccountPresenter(
            view,
            BankTransactionRepository(BankDatasource(androidContext())), get(),
            AndroidSchedulers.mainThread(), Schedulers.io()
        )

    }

    factory(name = "prepaidPaymentPresenter") { (view: PaymentContract.View) ->
        PrepaidPaymentPresenter(
            view,
            PrepaidRepository(PrepaidDataSource(androidContext())),
            AndroidSchedulers.mainThread(), Schedulers.io()
        )
    }

    factory(name = "ListNotificationPresenter") {
        ListNotificationPresenter(
            NotificationRepository(NotificationDatasource(androidContext())),
            AndroidSchedulers.mainThread(), Schedulers.io()
        )
    }

    factory(name = "welcomeInfoNotificationPresenter") { (view: WelcomeNotificationContract.View) ->
        WelcomeNotificationPresenter(
            view, UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(), Schedulers.io()
        )
    }

    factory(name = "userLoanPresenter") { (view: UserLoanContract.View) ->
        UserLoanPresenter(
            UserLoanRepository(get()),
            view
        )
    }

    factory(name = "myResumePresenter") { (view: MyResumeContract.View) ->
        MyResumePresenter(
            view,
            MyResumeRepository(get()),
            UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "myDataPresenter") {
        UserLoanDataPresenter(RecebaMaisRepository(RecebaMaisApiDataSource(androidContext())))
    }

    factory(name = "helpMainPresenter") {
        HelpMainPresenter(RecebaMaisHelpRepository(RecebaMaisApiDataSource(androidContext())))
    }

    factory(name = "createLinkPaymentPresenter") { (linkView: LinkContract.CreateLinkView) ->
        CreateLinkPaymentPresenter(
            linkView, AndroidSchedulers.mainThread(), Schedulers.io(),
            LinkRepository(LinkApiDataSource(androidContext()))
        )
    }

    factory(name = "formaEnvioPresenter") { (view: FormaEnvioContract.View) ->
        FormaEnvioPresenter(
            view, AndroidSchedulers.mainThread(), Schedulers.io(),
            LinkRepository(LinkApiDataSource(androidContext()))
        )
    }

    factory(name = "tipoVendaPagamentoPorLinkPresenter") { (view: TipoVendaPagamentoPorLinkContract.View) ->
        TipoVendaPagamentoPorLinkPresenter(
            view, FeatureTogglePreference.instance
        )
    }

    factory(name = "linkPaymentCreatedPresenter") {
        LinkPaymentCreatedPresenter()
    }

    factory(name = "deliveryCorreiosCustomPresenter") { (view: DeliveryContract.View) ->
        DeliveryPresenter(view, LinkRepository(LinkApiDataSource(androidContext())))
    }

    factory(name = "SuperLinkAnalytics") {
        SuperLinkAnalytics()
    }

    factory(name = "") { (view: DeliveryLoggiConfigurationContract.View) ->
        DeliveryLoggiConfigurationPresenter(
            view,
            LinkRepository(LinkApiDataSource(androidContext()))
        )
    }

    factory(name = "addressPresenter") { (view: UserAddressContract.UserEditAddressView) ->
        UserEditAddressPresenterImpl(
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            MeuCadastroNovoRepository(MeuCadastroNovoDataSource(androidContext())),
            UserPreferences.getInstance()
        )
    }

    factory(name = "technicalSupportPresenter") { (view: TechnicalSupportContract.View) ->
        TechnicalSupportPresenter(
            TechnicalSupportRemoteRepository.createTechnicalSupportRemote(androidContext()),
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
        )
    }

    factory(name = "statementCancelPresenter") { (view: ExtratoContract.View) ->
        ExtratoPresenter(
            view,
            androidContext(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "homePresenter") { (view: BaseView, menuView: MenuContract.View) ->
        HomePresenter(
            view as BannersContract.View, menuView,
            AndroidSchedulers.mainThread(), Schedulers.io(),
            PrepaidRepository(PrepaidDataSource(androidContext())),
            MigrationRepository(MigrationDataSource(androidContext())),
            NotificationRepository(NotificationDatasource(androidContext())),
            AppMenuRepository(AppMenuRemoteDataSource(androidContext()), AppMenuLocalDataSource()),
            MfaRepository(MfaApiDataSource(get()), get()),
            IDOnboardingRepository(get()),
            UserInformationRepository(UserInformationRemoteDataSource(androidContext())),
            FeatureTogglePreference.instance,
            UserPreferences.getInstance(),
            MenuPreference.instance
        )
    }

    factory(name = "HomeAnalytics") {
        HomeAnalytics()
    }

    factory(name = "homeGA4") {
        HomeGA4()
    }

    factory(name = "feesPlansGA4") {
        FeesPlansGA4()
    }

    factory(name = "InteractBannersAnalytics") {
        InteractBannersAnalytics()
    }

    factory(name = "feeAndPlansHomePresenter") { (view: FeeAndPlansHomeContract.View) ->
        FeeAndPlansHomePresenter(
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            BrandsRepository(get()),
            UserPreferences.getInstance(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "mainBottomNavigationPresenter") { (view: MainBottomNavigationContract.View) ->
        MainBottomNavigationPresenter(
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            UserInformationRepository(UserInformationRemoteDataSource(androidContext())),
            PosVirtualWhiteListRepositoryImpl(PosVirtualWhiteListDataSource(get())),
            AppMenuRepository(
                AppMenuRemoteDataSource(androidContext()),
                AppMenuLocalDataSource()
            ), get(),
            LgpdRepositoryImpl(LgpdRemoteDataSourceImpl(get())),
            MerchantRepositoryImpl(
                MerchantRemoteDataSourceImpl(
                    CieloAPIServices
                        .getCieloBackInstance(androidContext())
                )
            ),
            UserPreferences.getInstance(),
            FeatureTogglePreference.instance,
            MenuPreference.instance
        )
    }

    factory(name = "lightHousePresenter") { (view: LightHouseContract.View) ->
        LightHousePresenter(view, AndroidSchedulers.mainThread(), Schedulers.io())
    }

    factory(name = "othersMenuPresenter") { (view: OthersMenuContract.View) ->
        OthersMenuPresenter(
            view,
            AppMenuRepository(
                AppMenuRemoteDataSource(androidContext()),
                AppMenuLocalDataSource()
            ),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "servicesPresenter") { (view: ServicesContract.View) ->

        ServicesPresenter(
            view, AndroidSchedulers.mainThread(), Schedulers.io(),
            AppMenuRepository(
                AppMenuRemoteDataSource(androidContext()),
                AppMenuLocalDataSource()
            ), UserPreferences.getInstance(),
            FeatureTogglePreference.instance
        )

    }

    factory(name = "servicesGA4") {
        ServicesGA4()
    }

    factory(name = "pixExtractFilterPresenter") { (view: PixExtractFilterBottomSheetContract.View) ->
        PixExtractFilterBottomSheetPresenter(
            view
        )
    }


    factory(name = "presenterResearch") { (view: ResearchContract.ResearchView) ->
        ResearchPresenter(
            CieloAPIServices.getCieloBackInstance(androidContext()),
            view,
            FeatureTogglePreference.instance
        )
    }

    factory(name = "otpPresenter") { (view: OtpContract.View) ->

        val maxOtpSeconds = 30L

        OtpPresenter(
            cieloMfaTokenGenerator = get(),
            view = view,
            totpClock = TotpClock(androidContext(), SystemWallClock()),
            totpCounter = TotpCounter(maxOtpSeconds),
            mfaUserInformation = get(),
            userPreferences = UserPreferences.getInstance()
        )
    }

    factory { (view: OrderMachineContract.View) ->
        OrderMachinePresenter(view, AndroidSchedulers.mainThread(), Schedulers.io(), get())
    }

    factory { (view: PlanFreeTaxesPresenterContract.View) ->
        PlanFreeTaxesPresenter(
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            RecebaRapidoRepository(RecebaRapidoDataSource(get())),
            UserPreferences.getInstance()
        )
    }

    factory(name = "interactBannerPresenter") { (view: InteractBannersView) ->
        InteractBannersPresenterImpl(
            view,
            InteractBannerRepository(get(), UserPreferences.getInstance(), FeatureTogglePreference.instance)
        )
    }

    factory(name = "termoAceitePresenter") { (view: TermoAceiteContract.View) ->
        TermoAceitePresenter(
            view,
            InteractBannerRepository(get(), UserPreferences.getInstance(), FeatureTogglePreference.instance),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appIDOnboarding = module {
    single("idOnboardingApi") {
        CieloAPIServices
            .getInstance(androidContext(), BuildConfig.HOST_API)
            .createAPI(IDOnboardingApi::class.java) as IDOnboardingApi
    }

    factory(name = "idOnboardingRouterPresenter") { (view: IDOnboardingRouterContract.View) ->
        IDOnboardingRouterPresenter(
            IDOnboardingRepository(get()),
            view,
            FeatureTogglePreference.instance
        )
    }

    single(name = "idOnboardingUpdateUserPresenter") {
        IDOnboardingUpdateUserPresenter(
            IDOnboardingRepository(get())
        )
    }

    factory(name = "idOnboardingValidateP1PolicyPresenter") { (view: IDOnboardingValidateP1PolicyContract.View) ->
        IDOnboardingValidateP1PolicyPresenter(
            IDOnboardingRepository(get()),
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "idOnboardingBenefitsInfoP1PolicyPresenter") {
        IDOnboardingBenefitsInfoP1PolicyPresenter(
            MenuPreference.instance,
            UserPreferences.getInstance()
        )
    }

    factory(name = "idOnboardingUploadDocumentsPresenter") { (view: IDOnboardingUploadDocumentContract.View) ->
        IDOnboardingUploadDocumentPresenter(
            IDOnboardingRepository(get()),
            view
        )
    }

    factory(name = "idOnboardingDigitalDocumentPresenter") { (view: IDOnboardingDigitalDocumentContract.View) ->
        IDOnboardingDigitalDocumentPresenter(
            IDOnboardingRepository(get()),
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    single(name = "idOnboardingUploadSelfiePresenter") { (view: IDOnboardingUploadSelfieContract.View) ->
        IDOnboardingUploadSelfiePresenter(
            IDOnboardingRepository(get()),
            view
        )
    }

    factory(name = "idOnboardingValidateP2PolicyPresenter") { (view: IDOnboardingValidateP2PolicyContract.View) ->
        IDOnboardingValidateP2PolicyPresenter(
            IDOnboardingRepository(get()),
            view,
            UserPreferences.getInstance(),
            get()
        )
    }

    factory(name = "idOnboardingPresenter") { (view: IDOnboardingContract.View) ->
        IDOnboardingPresenter(
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "iDOnboardingUpdateForeignPhonePresenter") { (view: IDOnboardingUpdateForeignPhoneContract.View) ->
        IDOnboardingUpdateForeignPhonePresenter(
            view,
            IDOnboardingRepository(get())
        )
    }

    factory(name = "idOnboardingP1CompletionStatusPresenter") { (view: IDOnboardingP1CompletionStatusContract.View) ->
        IDOnboardingP1CompletionStatusPresenter(
            view
        )
    }

    factory(name = "idOnboardingP1Analytics") {
        IDOnboardingP1Analytics()
    }

    factory(name = "idOnboardingP1AnalyticsGA") {
        IDOnboardingP1AnalyticsGA()
    }

    factory(name = "idOnboardingP2Analytics") {
        IDOnboardingP2Analytics()
    }

    factory(name = "idOnboardingP2AnalyticsGA") {
        IDOnboardingP2AnalyticsGA()
    }
}

val appPedidosModule = module {
    factory(name = "orderMachineRepository") {
        OrderMachineServiceRepository(
            OrderMachineDataSource(
                CieloAPIServices
                    .getCieloBackInstance(androidContext())
            )
        ) as OrderMachineRepository
    }
}

val appSelfRegistration = module {
    factory(name = "selfRegistrationRepository") {
        SelfRegistrationRepository(SelfRegistrationSource(androidContext()))
    }
}

val appMigration = module {
    factory(name = "migrationRepository") {
        MigrationRepository(MigrationDataSource(androidContext()))
    }
}

val appMeuEstabelecimento = module {
    factory(name = "meuEstabelecimentoPresenter") { (view: MeuCadastroContract.DadosEstabelecimentoView) ->
        DadosEstabelecimentoPresenter(
            view,
            MeuCadastroRepository(CieloAPIServices.getCieloBackInstance(androidContext()))
        )
    }

    factory(name = "dadosProprietarioPresenter") { (view: AlertaCadastralContract.View) ->
        DadosProprietarioPresenter(
            UserPreferences.getInstance(),
            view,
            MeuCadastroNovoRepository(MeuCadastroNovoDataSource(androidContext()))
        )
    }

    factory(name = "MeuCadastroAnalytics") {
        MeuCadastroAnalytics()
    }
}

val appMeuUser = module {
    factory(name = "meuUserPresenter") { (view: MeuCadastroContract.DadosUsuarioView) ->
        DadosUsuarioPresenter(
            view,
            MeuCadastroRepository(CieloAPIServices.getCieloBackInstance(androidContext())),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appAddAccount = module {
    factory(name = "addAccountPresenter") { (listener: AddAccountContract.View) ->
        AddAccountPresenter(
            listener,
            MeuCadastroDomicilioRespository(MeuCadastroDomicilioDataSource(androidContext())),
            UserPreferences.getInstance()
        )
    }
}

val appMinhaConta = module {
    factory(name = "minhaContaPresenter") { (dadosContaView: MeuCadastroContract.DadosContaView) ->
        DadosContaPresenter(
            dadosContaView,
            MeuCadastroRepository(CieloAPIServices.getCieloBackInstance(androidContext()))
        )
    }

    factory(name = "editarDadosNomeFantasiaPresenter") { (view: EditarDadosNomeFantasiaFragment) ->
        EditarDadosNomeFantasiaPresenter(
            MeuCadastroNovoRepository(
                MeuCadastroNovoDataSource(
                    androidContext()
                )
            ), view
        )
    }

    factory(name = "trocaDomicilioSolicitacoesPresenter") { (view: TrocaDomicilioSolicitacoesView) ->
        TrocaDomicilioSolicitacoesPresenterImpl(
            view,
            MeuCadastroRepository(CieloAPIServices.getCieloBackInstance(androidContext()))
        )
    }

}

val appEditarDadosProprietario = module {
    factory(name = "editarDadosProprietarioPresenter") { (view: EditarDadosProprietarioContract.View) ->
        EditarDadosProprietarioPresenter(
            MeuCadastroNovoRepository(
                MeuCadastroNovoDataSource(
                    androidContext()
                )
            ),
            view,
            UserPreferences.getInstance()
        )
    }
}

val appEditarDadosContato = module {
    factory(name = "editarDadosContatoPresenter") { (view: EditarDadosContatoFragment) ->
        EditarDadosContatoPresenter(
            MeuCadastroNovoRepository(
                MeuCadastroNovoDataSource(
                    androidContext()
                )
            ),
            view,
            UserPreferences.getInstance()
        )
    }
}

val appTaxaPlanos = module {

    factory(name = "taxaPlanosFeeAndPlansActivityMainPresenter") { (view: FeeAndPlansMainFragment) ->
        TaxaPlanosMainPresenter(
            TaxaPlanoRepository(TaxaPlanoDataSource(androidContext())),
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "taxaPlanosPlanPresenter") { (view: TaxaPlanosPlanContract.View) ->
        TaxaPlanosPlanPresenter(
            TaxaPlanoRepository(TaxaPlanoDataSource(androidContext())),
            view,
            UserPreferences.getInstance(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "taxaPlanosCieloLivrePresenter") { (view: TaxasPlanosCieloContract.View) ->
        TaxasPlanosCieloPresenter(
            TaxaPlanoRepository(TaxaPlanoDataSource(androidContext())),
            view
        )
    }

    factory(name = "taxaPlanosCieloControlePresenter") { (view: TaxasPlanosCieloControleContract.View) ->
        TaxasPlanosCieloControlePresenter(
            TaxaPlanoRepository(TaxaPlanoDataSource(androidContext())),
            view
        )
    }

    factory(name = "doSeuJeitoTaxasPlanosPresenter") { (view: DoSeuJeitoTaxasPlanosContract.View) ->
        DoSeuJeitoTaxasPlanosPresenter(
            view,
            TaxaPlanoRepository(TaxaPlanoDataSource(androidContext())),
            RecebaRapidoRepository(RecebaRapidoDataSource(get())),
            UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "postecipadoMeuAluguelPresenter") { (view: PostecipadoMeuAluguelContract.View) ->
        PostecipadoMeuAluguelPresenter(
            view,
            PostecipadoRepository(get()),
            FeatureTogglePreference.instance,
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appAutoAtendimento = module {

    factory(name = "autoAtendimentoAnalytics") {
        AutoAtendimentoAnalytics()
    }

    factory(name = "autoAtendimentoPresenter") {
        AutoAtendimentoPresenter(AutoAtendimentoRepository(AutoAtendimentoDataSource(androidContext())))
    }

    factory(name = "materiaFragment") {
        AutoAtendimentoMateriasFragment()
    }

    factory(name = "paraVenderMaisFragment") {
        AutoAtendimentoVenderMaisFragment()
    }

    factory(name = "selfServiceSupplyPresenter") { (view: SelfServiceSupplyContract.View) ->
        SelfServiceSupplyPresenter(
            view,
            AutoAtendimentoRepository(AutoAtendimentoDataSource(androidContext()))
        )
    }

    factory(name = "requestMachinePresenter") { (view: RequestMachineContract.View) ->
        RequestMachinePresenter(view, MachineRepository(MachineDataSource(get())))
    }

    factory(name = "") { (view: InstalacaoMaquinaAdicionalQuantidadeContract.View) ->
        InstalacaoMaquinaAdicionalQuantidadePresenter(view)
    }

    factory(name = "") { (view: InstalacaoMaquinaAdicionalContatoContract.View) ->
        InstalacaoMaquinaAdicionalContatoPresenter(view)
    }

    factory(name = "instalacaoMaquinaChooseAddress") { (view: InstalacaoMaquinaChooseAddressContract.View) ->
        InstalacaoMaquinaChooseAddressPresenter(view, MachineRepository(MachineDataSource(get())))
    }

    factory(name = "instalacaoMaquinaAdicionalHorarioPresenter") { (view: InstalacaoMaquinaAdicionalHorarioContract.View) ->
        InstalacaoMaquinaAdicionalHorarioPresenter(
            view,
            OrdersRepository(OrdersDataSource(androidContext()))
        )
    }

    factory(name = "instalacaoMaquinaChooseaddressNewPresenter") { (view: InstalacaoMaquinaChooseAddressNewContract.View) ->
        InstalacaoMaquinaChooseaddressNewPresenter(
            view,
            MachineRepository(MachineDataSource(get()))
        )
    }

    factory(name = "instalacaoMaquinaAdicionalResumoEfetivacaoPresenter") { (view: InstalacaoMaquinaAdicionalResumoEfetivacaoContract.View) ->
        InstalacaoMaquinaAdicionalResumoEfetivacaoPresenter(
            view,
            OrdersRepository(OrdersDataSource(androidContext()))
        )
    }

    factory(name = "openRequestPresenter") { (view: OpenRequestContract.View) ->
        OpenRequestPresenter(view)
    }

    factory(name = "openRequestMachinePresenter") { (view: OpenRequestMachinesContract.View) ->
        OpenRequestMachinesPresenter(view, MachineRepository(MachineDataSource(get())))
    }

    factory(name = "openRequestResumePresenter") { (view: OpenRequestResumeContract.View) ->
        OpenRequestResumePresenter(view, OrdersRepository(OrdersDataSource(androidContext())))
    }

    factory(name = "filterPresenter") { (view: FilterContract.View) ->
        FilterReceivablesPresenter(
            view, AndroidSchedulers.mainThread(), Schedulers.io(),
            FilterRepositoryNetwork(CieloAPIServices.getCieloBackInstance(androidContext()))
        ) as FilterContract.Presenter
    }

    factory(name = "selfServiceAnalytics"){
        SelfServiceAnalytics()
    }

}

val appFaq = module {
    factory(name = "centraAjudaCategories") {
        CentralAjudaCategoriesPresenter(
            CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(
                    androidContext()
                ),
                ConfigurationPreference.instance
            )
        )
    }
}

val appDeepLink = module {
    factory(name = "deepLink") { (view: DeepLinkContract.View) ->
        DeepLinkPresenter(
            view,
            DeepLinkInteractor(DeepLinkDataSource(get()))
        )
    }
}

val appFaqSubCategories = module {
    factory(name = "centralAjudaSubCategoriasPresenter") { (view: CentralAjudaSubCatregoriasContract.View) ->
        CentralAjudaSubCategoriasPresenter(
            view,
            CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(
                    androidContext()
                ),
                ConfigurationPreference.instance
            ),
            UserPreferences.getInstance()
        )
    }
}

val appFaqQuestions = module {
    factory(name = "centralAjudaSubCategoriasPresenter") {
        CentralAjudaPerguntasPresenter(
            CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(
                    androidContext()
                ),
                ConfigurationPreference.instance
            )
        )
    }
}

val appLinkOrders = module {

    factory(name = "appLinkOrders") { (view: LinkOrdersView) ->
        val interactor: LinkOrdersInteractor = LinkOrdersInteractorImpl(
            LinkOrdersRepository(get()),
            get()
        )
        LinkOrdersPresenterImpl(view, interactor)
    }
    factory(name = "appLinkOrderDetail") { (view: LinkOrderDetailView) ->
        val interactor: LinkOrdersInteractor = LinkOrdersInteractorImpl(
            LinkOrdersRepository(get()),
            get()
        )
        LinkOrderDetailPresenterImpl(view, interactor)
    }

    factory(name = "solicitationMotoboy") { (view: SolicitationMotoboyView) ->
        val interactor: LinkOrdersInteractor = LinkOrdersInteractorImpl(
            LinkOrdersRepository(get()),
            get()
        )
        SolicitationMotoboyPresenterImpl(view, interactor)
    }
}

val appFaqContacts = module {
    factory(name = "CentralAjudaContatosPresenter") {
        CentralAjudaContatosPresenter(
            CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(
                    androidContext()
                ),
                ConfigurationPreference.instance
            )
        )
    }

    factory(name = "OmbudsmanPersonalDataPresenter") { (view: OmbudsmanPersonalDataContract.View) ->
        OmbudsmanPersonalDataPresenter(
            view,
            UserPreferences.getInstance(),
            MenuPreference.instance
        )
    }

    factory(name = "OmbudsmanMessagePresenter") { (view: OmbudsmanMessageContract.View) ->
        OmbudsmanMessagePresenter(
            view,
            CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(
                    androidContext()
                ),
                ConfigurationPreference.instance
            )
        )
    }
}

val appCoil = module {
    factory(name = "coilChoose") {
        CoilChoosePresenter(androidContext(), CoilRepository(CoilDataDataSource(androidContext())))
    }
    factory(name = "serviceAddress") {
        ServiceAddressPresenter(CoilRepository(CoilDataDataSource(androidContext())))
    }
}

val appChangeEc = module {
    factory(name = "changeEcRepository") {
        ChangeEcRepository(ChangeEcDataSource(androidContext()))
    }
    factory(name = "internalUserPresenter") { (view: InternalUserView) ->
        InternalUserPresenterImpl(
            view,
            UserPreferences.getInstance(),
            ChangeEcRepository(ChangeEcDataSource(androidContext()))
        )
    }
}

val appAddEc = module {
    factory(name = "addEcPresenter") { (view: AddEcContract.View) ->
        AddEcPresenter(
            view, AddEcRepository(get()),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appPagamentoPorLink = module {
    factory(name = "pagamentoLinkRepository") {
        PagamentoLinkListAtivosPresenter(
            PagamentoLinkRespository(
                PagamentoLinkDataSource(
                    androidContext()
                )
            )
        )
    }
}

val appPgLinkDetail = module {
    factory(name = "pgLinkDetailPresenter") {
        PgLinkDetailPresenter(PgLinkDetailReporitory(PgLinkDataDataSource(androidContext())))
    }
}

val appContaDigital = module {
    factory(name = "contaDigital") { (view: CardNewPasswordContract.View) ->
        CardNewPasswordPresenter(
            view,
            CreditCardsNewRespository(CreditCardsNewDataSource(androidContext()))
        )
    }

    factory(name = "creditCardsPresenter") { (view: CreditCardsContract.CreditCardsView) ->
        CreditCardsPresenter(
            view,
            CreditCardsRepository(CreditCardsDataSource(get())),
            PrepaidRepository(PrepaidDataSource(get())),
            UserPreferences.getInstance(),
            FeatureTogglePreference.instance,
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "extratoTimeLinePresenter") { (view: ExtratoTimeLineContract.View) ->
        ExtratoTimeLinePresenter(
            view,
            CreditCardStatementRepository.getInstance(
                StatementApiDataSource.getInstance(
                    androidContext()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "LastTransactionsPresenter") { (view: LastTransactionsContract.View) ->
        LastTransactionsPresenter(
            view,
            CreditCardStatementRepository.getInstance(
                StatementApiDataSource.getInstance(
                    androidContext()
                )
            ),
            MenuPreference.instance,
            UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appCancel = module {
    factory(name = "cancelamento") { (view: OnCancelamentoContract.View) ->
        CancelamentoPresenter(
            view,
            CancelamentoInteractor(get())
        )
    }
}

val debitAccountGA4 = module {
    factory(name = "debitAccountGA4") {
        DebitAccountGA4()
    }
}

val appMinhasVendas = module {
    factory(name = "minhasVendasOnline") { (view: MinhasVendasOnlineContract.View) ->
        MinhasVendasOnlinePresenter(
            view,
            MinhasVendasRepository(MinhasVendasDataSource(androidContext()))
        )
    }
    factory(name = "minhasVendasConsolidado") { (view: MinhasVendasConsolidadoContract.View) ->
        MinhasVendasConsolidadoPresenter(
            view,
            MinhasVendasRepository(MinhasVendasDataSource(androidContext()))
        )
    }
    factory(name = "minhasVendasFilterBottomSheetFragment") { (view: MinhasVendasFilterBottomSheetContract.View) ->
        MinhasVendasFilterBottomSheetPresenter(
            view,
            MinhasVendasRepository(MinhasVendasDataSource(androidContext())),
            UserPreferences.getInstance()
        )
    }
    factory(name = "minhasVendasTransacoes") { (view: MinhasVendasTransacoesContract.View) ->
        MinhasVendasTransacoesPresenter(
            view,
            MinhasVendasRepository(MinhasVendasDataSource(androidContext())),
            UserPreferences.getInstance()
        )
    }
    factory(name = "minhasVendasDetalhes") { (view: MinhasVendasDetalhesContract.View) ->
        MinhasVendasDetalhesPresenter(
            view,
            ChangeEcRepository(ChangeEcDataSource(androidContext()))
        )
    }
    factory(name = "mySalesGA4") {
        MySalesGA4()
    }
}

val appNewLogin = module {
    factory(name = "newLogin") { (view: NewLoginContract.View) ->
        NewLoginPresenter(
            view,
            br.com.mobicare.cielo.newLogin.LoginRepository(LoginDataSource(get()), get()),
            PosVirtualWhiteListRepositoryImpl(PosVirtualWhiteListDataSource(get())),
            FeatureToggleRepository(FeatureToggleAPIDataSource(androidContext())),
            UserInformationRepository(UserInformationRemoteDataSource(androidContext())),
            LgpdRepositoryImpl(LgpdRemoteDataSourceImpl(get())),
            AccessManagerRepository(get()),
            FeatureTogglePreference.instance,
            UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            get()
        )
    }

    factory(name = "featureTogglePresenter") { (view: FeatureToggleContract.View) ->
        FeatureTogglePresenter(
            view,
            FeatureToggleRepository(FeatureToggleAPIDataSource(androidContext()))
        )
    }

    factory(name = "splashPresenter") { (view: SplashContract.View) ->
        SplashPresenter(
            view,
            SplashRepository(SplashAPIDataSource(get())),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            ConfigurationPreference.instance,
            FeatureTogglePreference.instance
        )
    }
}

val allowMe = module {
    factory(name = "allowMe") { (view: AllowMeContract.View) ->
        AllowMePresenter(
            view, UserPreferences.getInstance()
        )
    }
}

val appMeusRecebimentos = module {
    factory(name = "visaoSumarizadaMeusRecebimentosPresenter") { (view: VisaoSumarizadaMeusRecebimentosContract.View) ->
        VisaoSumarizadaMeusRecebimentosPresenter(view, MeusRecebimentosInteractorImpl())
    }
    factory(name = "visaoDetalhadaMeusRecebimentosPresenter") { (view: VisaoDetalhadaMeusRecebimentosContract.View) ->
        VisaoDetalhadaMeusRecebimentosPresenter(
            view, MeusRecebimentosInteractorImpl(), AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "myReceiptsHomePresenter") { (view: MeusRecebimentosHomeContract.View) ->
        MyReceiptsHomePresenter(
            view,
            MeusRecebimentosGraficoRepository(),
            FeatureTogglePreference.instance,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            Handler()
        )
    }
}

val appMfa = module {
    factory {
        CieloMfaTokenGenerator(
            userPreferences = UserPreferences.getInstance(),
            featureTogglePreference = FeatureTogglePreference.instance,
            MfaRepository(MfaApiDataSource(get()), get())
        )
    }

    single {
        MfaRepository(MfaApiDataSource(get()), get())
    }

    factory {
        PutValueRepository(get())
    }

    factory(name = "mfaRouter") { (view: MfaRouterContract.View) ->
        MfaRouterPresenter(
            view,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            get(),
            UserInformationRepository(UserInformationRemoteDataSource(androidContext())),
            UserPreferences.getInstance(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "mfaTokenConfigurationPresenter") { (view: MfaTokenConfigurationContract.View) ->
        MfaTokenConfigurationPresenter(
            view,
            get(),
            PutValueInteractorImpl(
                PutValueRepository(get()), get()
            ),
            UserPreferences.getInstance(),
            get()
        )
    }

    factory(name = "mfaRouteHandler") { (activity: FragmentActivity) ->
        MfaRouteHandler(activity)
    }

    factory(name = "selecioneBancoMfa") { (view: SelecioneBancoMfaContract.View) ->
        SelecioneBancoMfaPresenter(view, get())
    }

    factory(name = "activationToken") { (view: PutValueView) ->
        val interactor: PutValueInteractor =
            PutValueInteractorImpl(
                PutValueRepository(get()), get()
            )
        PutValuePresenterImpl(view, interactor)
    }
    factory(name = "validationPreviousToken") { (view: ValidationPreviousTokenContract.View) ->
        ValidationPreviousTokenPresenter(
            view,
            get(),
            PutValueInteractorImpl(PutValueRepository(get()), get())
        )
    }

    factory(name = "bankChallengeMFA") { (view: MerchantValidateChallengeView) ->
        MerchantValidateChallengePresenterImpl(view, get())
    }
    factory(name = "merchantChallengerActivation") { (view: MerchantChallengerActivationContract.View) ->
        MerchantChallengerActivationPresenter(
            view,
            MerchantRepositoryImpl((MerchantRemoteDataSourceImpl(get())))
        )
    }

    factory(name = "resumeBankAndCNPJ") { (view: ResumeBankAndCNPJContract.View) ->
        ResumeBankAndCNPJPresenter(view, get())
    }

    factory(name = "MfaAnalytics") {
        MfaAnalytics()
    }
}

val appRecebaRapido = module {

    factory(name = "recebaRapidoDelete") { (view: RecebaRapidoCancellationReasonView) ->
        RecebaRapidoCancellationReasonPresenterImpl(
            view,
            RecebaRapidoRepository(RecebaRapidoDataSource(get()))
        )
    }
}

val appMachinesTracking = module {
    factory(name = "OrderDetailsMachinesTracking") { (view: OrderDetailsMachinesTrackingContract.View) ->
        OrderDetailsMachinesTrackingPresenter(view, MachineRepository(MachineDataSource(get())))
    }
}

val appPixModule = module {

    single("pixAPI") {
        CieloAPIServices
            .getInstance(androidContext(), BuildConfig.HOST_API)
            .createAPI(PixAPI::class.java) as PixAPI
    }

    factory(name = "pixTerm") { (view: PixTermContract.View) ->
        PixTermPresenter(
            view,
            PixRepository(
                CieloAPIServices.getInstance(
                    androidContext(),
                    BuildConfig.HOST_API
                )
            )
        )
    }

    factory(name = "pixKeysOnboardingPresenter") { (view: PixKeysOnboardingContract.View) ->
        PixKeysOnboardingPresenter(
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "pixOnboardingHomePresenter") { (view: PixOnboardingHomeContract.View) ->
        PixOnboardingHomePresenter(
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "pixInsertKeyPresenter") { (view: PixInsertKeyContract.View) ->
        PixInsertKeyPresenter(
            view,
            PixKeysRepository(
                PixKeysDataSource(get())
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixEnterTransferAmountPresenter") { (view: PixEnterTransferAmountContract.View) ->
        PixEnterTransferAmountPresenter(
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "pixTransferSummaryPresenter") { (view: PixTransferSummaryContract.View) ->
        PixTransferSummaryPresenter(
            view,
            UserPreferences.getInstance(),
            PixTransferRepository(
                PixTransferDataSource(
                    get()
                )
            ),
            PixExtractRepository(
                PixExtractDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixTransferReceiptPresenter") { (view: PixTransferReceiptContract.View) ->
        PixTransferReceiptPresenter(
            view,
            PixTransferRepository(
                PixTransferDataSource(get())
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixSelectBankPresenter") { (view: PixSelectBankContract.View) ->
        PixSelectBankPresenter(
            view,
            PixTransferRepository(
                PixTransferDataSource(get())
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixKeyRegistrationPresenter") { (view: PixKeyRegistrationContract.View) ->
        PixKeyRegistrationPresenter(
            view,
            MenuPreference.instance,
            UserPreferences.getInstance(),
            PixKeysRepository(
                PixKeysDataSource(
                    get()
                )
            ),
            PixClaimRepository(
                PixClaimDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixMyKeysPresenter") { (view: PixMyKeysContract.View) ->
        PixMyKeysPresenter(
            view,
            PixKeysRepository(
                PixKeysDataSource(
                    get()
                )
            ),
            PixClaimRepository(
                PixClaimDataSource(
                    get()
                )
            ),
            UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixInsertKeyToRegisterPresenter") { (view: PixInsertKeyToRegisterContract.View) ->
        PixInsertKeyToRegisterPresenter(
            view,
            PixKeysRepository(
                PixKeysDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixValidationCodePresenter") { (view: PixValidationCodeContract.View) ->
        PixValidationCodePresenter(
            view,
            PixKeysRepository(
                PixKeysDataSource(
                    get()
                )
            ),
            PixClaimRepository(
                PixClaimDataSource(
                    get()
                )
            ),
            UserPreferences.getInstance(),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixGenerateQRCodePresenter") { (view: PixGenerateQRCodeContract.View) ->
        PixGenerateQRCodePresenter(
            view,
            UserPreferences.getInstance(),
            PixQRCodeRepository(
                PixQRCodeDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixQrCodeAnalyzer") { (view: PixReadQRCodeContract.View) ->
        PixQrCodeAnalyzer(
            view
        )
    }

    factory(name = "pixReadQRCodePresenter") { (view: PixReadQRCodeContract.View) ->
        PixReadQRCodePresenter(
            view,
            UserPreferences.getInstance(),
            PixQRCodeRepository(
                PixQRCodeDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixDecodeQRCodeSummaryPresenter") { (view: PixDecodeQRCodeSummaryContract.View) ->
        PixDecodeQRCodeSummaryPresenter(
            view,
            UserPreferences.getInstance(),
            PixTransferRepository(
                PixTransferDataSource(
                    get()
                )
            ),
            PixQRCodeRepository(
                PixQRCodeDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixCopyPasteQRCodePresenter") { (view: PixCopyPasteQRCodeContract.View) ->
        PixCopyPasteQRCodePresenter(
            view,
            UserPreferences.getInstance(),
            PixQRCodeRepository(
                PixQRCodeDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixExtractRouterPresenter") { (view: PixExtractRouterContract.View) ->
        PixExtractRouterPresenter(
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "pixOnboardingExtractPresenter") { (view: PixOnboardingExtractContract.View) ->
        PixOnboardingExtractPresenter(
            view,
            UserPreferences.getInstance()
        )
    }

    factory(name = "pixExtractDetailPresenter") { (view: PixExtractDetailContract.View) ->
        PixExtractDetailPresenter(
            view,
            UserPreferences.getInstance(),
            PixTransferRepository(
                PixTransferDataSource(
                    get()
                )
            ),
            PixExtractRepository(
                PixExtractDataSource(
                    get()
                )
            ),
            PixReversalRepository(
                PixReversalDataSource(
                    get()
                )
            ),
            FeatureTogglePreference.instance,
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
        )
    }

    factory(name = "pixReversalPresenter") { (view: PixReversalContract.View) ->
        PixReversalPresenter(
            view,
            UserPreferences.getInstance(),
            PixReversalRepository(
                PixReversalDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            Handler(Looper.getMainLooper())
        )
    }

    factory(name = "pixReversalReceiptPresenter") { (view: PixReversalReceiptContract.View) ->
        PixReversalReceiptPresenter(
            view,
            PixReversalRepository(
                PixReversalDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixExtractPresenter") { (view: PixExtractContract.View) ->
        PixExtractPresenter(
            view,
            CreditCardsRepository(CreditCardsDataSource(get())),
            PixRepository(
                CieloAPIServices.getInstance(
                    androidContext(),
                    BuildConfig.HOST_API
                )
            ),
            UserPreferences.getInstance(),
            MenuPreference.instance,
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixExtractAdapter") { (view: PixExtractTabsContract.View) ->
        PixExtractAdapter(view)
    }

    factory(name = "pixExtractTabsPresenter") { (view: PixExtractTabsContract.View) ->
        PixExtractTabsPresenter(
            view,
            PixExtractRepository(
                PixExtractDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixTransitoryAccountManagementPresenter") { (view: PixTransitoryAccountManagementContract.View) ->
        PixTransitoryAccountManagementPresenter(
            view,
            UserPreferences.getInstance(),
            UserInformationRepository(UserInformationRemoteDataSource(androidContext())),
            PixAccountRepository(
                PixAccountDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixBillingReceiptPresenter") { (view: PixBillingReceiptContract.View) ->
        PixBillingReceiptPresenter(
            view,
            PixTransferRepository(
                PixTransferDataSource(get())
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixScheduledTransactionReceiptPresenter") { (view: PixScheduledTransactionReceiptContract.View) ->
        PixScheduledTransactionReceiptPresenter(
            view,
            PixTransferRepository(
                PixTransferDataSource(get())
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixMyLimitsTrustedDestinationsPresenter") { (view: PixMyLimitsTrustedDestinationsContract.View) ->
        PixMyLimitsTrustedDestinationsPresenter(
            view,
            PixTrustedDestinationsRepository(
                PixTrustedDestinationsDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixMyLimitsAddNewTrustedDestinationPresenter") { (view: PixMyLimitsAddNewTrustedDestinationContract.View) ->
        PixMyLimitsAddNewTrustedDestinationPresenter(
            view,
            UserPreferences.getInstance(),
            PixTrustedDestinationsRepository(
                PixTrustedDestinationsDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixMyLimitsTrustedDestinationsDetailPresenter") { (view: PixMyLimitsTrustedDestinationsDetailContract.View) ->
        PixMyLimitsTrustedDestinationsDetailPresenter(
            view,
            UserPreferences.getInstance(),
            PixTrustedDestinationsRepository(
                PixTrustedDestinationsDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixMyLimitsTransactionsPresenter") { (view: PixMyLimitsTransactionsContract.View) ->
        PixMyLimitsTransactionsPresenter(
            view,
            UserPreferences.getInstance(),
            PixMyLimitsRepository(
                PixMyLimitsDataSource(
                    get()
                )
            ),
            PixTimeManagementRepository(
                PixTimeManagementDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixMyLimitsWithdrawAndChargePresenter") { (view: PixMyLimitsWithdrawAndChargeContract.View) ->
        PixMyLimitsWithdrawAndChargePresenter(
            view,
            UserPreferences.getInstance(),
            PixMyLimitsRepository(
                PixMyLimitsDataSource(
                    get()
                )
            ),
            PixTimeManagementRepository(
                PixTimeManagementDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixFreeMovementAccountManagementPresenter") { (view: PixFreeMovementAccountManagementContract.View) ->
        PixFreeMovementAccountManagementPresenter(
            view,
            UserPreferences.getInstance(),
            PixAccountRepository(
                PixAccountDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "pixMyLimitsTimeManagementPresenter") { (view: PixMyLimitsTimeManagementContract.View) ->
        PixMyLimitsTimeManagementPresenter(
            view,
            UserPreferences.getInstance(),
            PixTimeManagementRepository(
                PixTimeManagementDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appDirfModule = module {
    factory(name = "Dirf") { (view: DirfContract.View) ->
        DirfPresenter(
            view, DirfInteractor(get())
        )
    }

    factory(name = "dirfAnalytics") {
        DirfAnalytics()
    }

    factory(name = "dirfGA4") {
        DirfGA4()
    }

}

val appBalcaoRecebiveisExtratoModule = module {
    factory(name = "appBalcaoRecebíveisExtratoModule") { (view: BalcaoRecebiveisExtratoContract.View) ->
        BalcaoRecebiveisExtratoPresenter(
            view, BalcaoRecebiveisExtratoInteractor(get())
        )
    }

    factory(name = "appBalcaoRecebíveisExtratoDetailNegotiationsModule") { (view: BalcaoRecebiveisExtratoContract.View) ->
        BalcaoRecebiveisExtratoDetailsNegotiationsPresenter(view)
    }


    factory(name = "appBalcaoRecebiveisExtratoUnitSales") { (view: ExtratoRecebiveisVendasUnitariasView) ->
        ExtratoRecebiveisVendasUnitariasPresenterImpl(
            get(),
            view,
            BalcaoRecebiveisExtratoInteractor(get())
        )
    }

    factory(name = "appBalcaoRecebíveisExtratoBanksModule") { (view: BalcaoRecebiveisExtratoBanksContract.View) ->
        BalcaoRecebiveisExtratoBanksPresenter(
            view, BalcaoRecebiveisExtratoBanksInteractor(get())
        )
    }

    single(name = "myReceivablesGA4"){
        MyReceivablesGA4()
    }

    factory(name = "appFiltroVendasUnitariasModule") { (view: FiltroVendasUnitariasContract.View) ->
        FiltroVendasUtitariasPresenter(
            view, FiltroVendasUnitariasInteractor(get())
        )
    }

    single(name = "ReceivablesAnalyticsGA4") {
        ReceivablesAnalyticsGA4()
    }
}

val appLgpdModule = module {
    factory(name = "LgpdPresenter") { (view: LgpdContract.View) ->
        LgpdPresenter(
            view,
            LgpdRepositoryImpl(LgpdRemoteDataSourceImpl(get())),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appBankAccountTransfer = module {

    // Simple Presenter Factory
    factory { (view: BankAccountToTransferInputContract.View) ->

        BankAccountToTransferInputPresenter(
            view,
            BankTransactionRepository(BankDatasource(androidContext())), AndroidSchedulers
                .mainThread(), Schedulers.io()
        )
    }
}

val appWarningModule = module {
    factory(name = "warningModalPresenter") {
        WarningModalPresenter(
            FeatureTogglePreference.instance,
            MenuPreference.instance
        )
    }

    factory(name = "warningAnalytics") {
        WarningAnalytics()
    }
}

val appHelpCenterModule = module {
    factory(name = "frequentQuestionsHelpCenterPresenter") { (view: LoggedHelpCenterContract.FrequentQuestionsView) ->
        FrequentQuestionHelpCenterPresenter(
            view, AndroidSchedulers.mainThread(), Schedulers.io(),
            repository = CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(androidContext()),
                ConfigurationPreference.instance
            )
        )
    }

    factory(name = "questionAndAnswerPresenter") { (view: LoggedHelpCenterContract.QuestionAndAnswerView) ->
        QuestionAndAnswerPresenter(
            view, AndroidSchedulers.mainThread(), Schedulers.io(),
            repository = CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(androidContext()),
                ConfigurationPreference.instance
            )
        )
    }

    factory(name = "helpCenterSearchPresenter") { (view: HelpCenterSearchContract.View) ->
        HelpCenterSearchPresenter(
            view, AndroidSchedulers.mainThread(), Schedulers.io(),
            repository = CentralAjudaLogadoRepository(
                CentralAjudaLogadoDataSource(androidContext()),
                ConfigurationPreference.instance
            )
        )
    }

    factory<CentralAjudaContract.Presenter>(name = "CentralDeAjudaPresenterPresenter") { (view: CentralAjudaContract.View) ->
        CentralAjudaPresenter(
            view, CentralDeAjudaRepository(
                CentralDeAjudaAPIDataSource(get())
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }
}

val appSecurityMenu = module {
    factory(name = "securityAuth") { (view: BottomSheetSecurityContract.View) ->
        BottomSheetPresenter(
            view, br.com.mobicare.cielo.newLogin.LoginRepository(LoginDataSource(get()), get())
        )
    }

    factory(name = "securityPresenter") { (view: SecurityContract.View) ->
        SecurityPresenter(
            view
        )
    }
}


val appRouter = module {
    factory(name = "DeeplinkRouter") {
        DeeplinkRouter()
    }
}

val appSolesp = module {
    single("SolespAPI") {
        CieloAPIServices
            .getInstance(androidContext(), BuildConfig.HOST_API)
            .createAPI(SolespAPI::class.java) as SolespAPI
    }

    factory(name = "solespStartPresenter") { (view: SolespStartContract.View) ->
        SolespStartPresenter(
            view,
            FeatureTogglePreference.instance
        )
    }

    factory(name = "solespInfoSendPresenter") { (view: SolespInfoSendContract.View) ->
        SolespInfoSendPresenter(
            view,
            UserPreferences.getInstance(),
            SolespRepository(
                SolespDataSource(get())
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
        )
    }
}

val appTapOnPhone = module {
    single("tapOnPhoneAPI") {
        CieloAPIServices
            .getInstance(androidContext(), BuildConfig.HOST_API)
            .createAPI(TapOnPhoneAPI::class.java) as TapOnPhoneAPI
    }

    factory(name = "tapOnPhonePresenter") { (view: TapOnPhoneContract.View) ->
        TapOnPhonePresenter(
            view,
            TapOnPhoneEligibilityRepositoryImpl(
                RemoteTapOnPhoneEligibilityDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            UserPreferences.getInstance()
        )
    }

    factory(name = "tapOnPhoneSetupTerminal") { (result: TapOnPhoneSetupTerminalContract.Result) ->
        TapOnPhoneSetupTerminal(
            result,
            DatadogEvent(androidContext(), UserPreferences.getInstance())
        )
    }

    factory(name = "tapOnPhoneSetupTerminalPresenter") { (view: TapOnPhoneSetupTerminalContract.View) ->
        TapOnPhoneSetupTerminalPresenter(
            view,
            UserPreferences.getInstance(),
            TapOnPhoneTerminalRepositoryImpl(
                RemoteTapOnPhoneTerminalDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io()
        )
    }

    factory(name = "tapOnPhoneTermAndConditionPresenter") { (view: TapOnPhoneTermAndConditionContract.View) ->
        TapOnPhoneTermAndConditionPresenter(
            view,
            TapOnPhoneAccreditationRepositoryImpl(
                RemoteTapOnPhoneAccreditationDataSource(
                    get()
                )
            ),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "tapOnPhoneAccreditationOfferPresenter") { (view: TapOnPhoneAccreditationOfferContract.View) ->
        TapOnPhoneAccreditationOfferPresenter(
            view,
            TapOnPhoneAccreditationRepositoryImpl(
                RemoteTapOnPhoneAccreditationDataSource(get())
            ),
            get(),
            AndroidSchedulers.mainThread(),
            Schedulers.io(),
            FeatureTogglePreference.instance
        )
    }

    factory(name = "tapOnPhoneImpersonatePresenter") { (view: TapOnPhoneImpersonateContract.View) ->
        TapOnPhoneImpersonatePresenter(
            view,
            ChangeEcRepository(ChangeEcDataSource(androidContext())),
            UserPreferences.getInstance()
        )
    }

    factory(name = "tapOnPhoneTerminalReadyPresenter") {
        TapOnPhoneTerminalReadyPresenter(
            UserPreferences.getInstance()
        )
    }

    factory(name = "tapOnPhoneAnalytics") {
        TapOnPhoneAnalytics()
    }

    factory(name = "tapOnPhoneGA4") {
        TapOnPhoneGA4()
    }
}

object AppModule {

    fun getLoginModule(): Module = applicationContext {
        bean { LoginRepository(get(), LoginAPIDataSource.getInstance(get())) }
    }

    fun getFeatureToggleModule(): Module = applicationContext {
        bean { FeatureToggleRepository(FeatureToggleAPIDataSource.getInstance(get())) }
    }
}