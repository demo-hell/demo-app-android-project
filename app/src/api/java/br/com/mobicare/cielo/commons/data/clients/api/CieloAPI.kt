package br.com.mobicare.cielo.commons.data.clients.api

import br.com.mobicare.cielo.BuildConfig
import br.com.mobicare.cielo.adicaoEc.domain.model.BankAccountObj
import br.com.mobicare.cielo.autoAtendimento.domain.model.SupliesResponse
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.ExtratoRecebiveisVendasUnitariasResponse
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.Negotiations
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.NegotiationsBanks
import br.com.mobicare.cielo.balcaoRecebiveisExtrato.data.VendasUnitariasFilterBrands
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.HelpCategory
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionDataResponse
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domain.QuestionReactionResponse
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.Contact
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanRequest
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.OmbudsmanResponse
import br.com.mobicare.cielo.centralDeAjuda.data.clients.domains.SubCategorie
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.changeEc.domain.HierachyResponse
import br.com.mobicare.cielo.changeEc.domain.Impersonate
import br.com.mobicare.cielo.changeEc.domain.ImpersonateRequest
import br.com.mobicare.cielo.coil.domain.MerchantAddressResponse
import br.com.mobicare.cielo.coil.domain.MerchantBuySupplyChosenResponse
import br.com.mobicare.cielo.coil.domain.MerchantSuppliesResponde
import br.com.mobicare.cielo.coil.domain.MerchantSupplyChosen
import br.com.mobicare.cielo.commons.constants.FOUR_HUNDRED
import br.com.mobicare.cielo.commons.constants.TEN
import br.com.mobicare.cielo.commons.constants.Text.ACTIVATED_STATUS
import br.com.mobicare.cielo.commons.constants.ZERO
import br.com.mobicare.cielo.commons.data.clients.local.UserPreferences
import br.com.mobicare.cielo.commons.data.domain.MultichannelUserTokenResponse
import br.com.mobicare.cielo.commons.domains.entities.MessagePhoto
import br.com.mobicare.cielo.commons.presentation.filter.model.FilterReceivableResponse
import br.com.mobicare.cielo.dirf.DirfResponse
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPassword
import br.com.mobicare.cielo.esqueciSenha.domains.entities.RecoveryPasswordResponse
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleResponse
import br.com.mobicare.cielo.home.presentation.incomingfast.model.EligibleOffer
import br.com.mobicare.cielo.interactbannersoffers.model.HiringOffers
import br.com.mobicare.cielo.lgpd.domain.entity.LgpdElegibilityEntity
import br.com.mobicare.cielo.login.domain.LoginMultichannelRequest
import br.com.mobicare.cielo.login.domain.SendDeviceTokenResponse
import br.com.mobicare.cielo.login.domain.TokenFCM
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.machine.domain.MachineListOffersResponse
import br.com.mobicare.cielo.machine.domain.OrdersAvailabilityResponse
import br.com.mobicare.cielo.main.domain.AppMenuResponse
import br.com.mobicare.cielo.me.MeResponse
import br.com.mobicare.cielo.merchant.data.entity.MerchantChallengerActivateRequest
import br.com.mobicare.cielo.merchant.domain.entity.MerchantPermissionsEligible
import br.com.mobicare.cielo.merchant.domain.entity.MerchantResponseRegisterGet
import br.com.mobicare.cielo.merchant.domain.entity.ResponseDebitoContaEligible
import br.com.mobicare.cielo.merchants.MerchantsResponse
import br.com.mobicare.cielo.meuCadastro.domains.entities.CepAddressResponse
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroEndereco
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.AccountTransferRequest
import br.com.mobicare.cielo.meuCadastroDomicilio.domain.FlagTransferRequest
import br.com.mobicare.cielo.meuCadastroNovo.data.model.response.GetUserAdditionalInfo
import br.com.mobicare.cielo.meuCadastroNovo.domain.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.*
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentRequest
import br.com.mobicare.cielo.meusCartoes.clients.api.domain.payment.PrepaidPaymentResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.CreditCardStatement
import br.com.mobicare.cielo.meusCartoes.domains.entities.ImageDocument
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidBalanceResponse
import br.com.mobicare.cielo.meusCartoes.domains.entities.PrepaidResponse
import br.com.mobicare.cielo.meusrecebimentosnew.calculationview.graph.repository.PostingsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.models.detailsummaryview.DetailSummaryViewResponse
import br.com.mobicare.cielo.meusrecebimentosnew.models.summaryview.SummaryViewResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.AlertsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.FileResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.ReceivablesBankAccountsResponse
import br.com.mobicare.cielo.meusrecebimentosnew.repository.SummaryResponse
import br.com.mobicare.cielo.mfa.*
import br.com.mobicare.cielo.mfa.activation.repository.PutValueResponse
import br.com.mobicare.cielo.mfa.api.EnrollmentBankResponse
import br.com.mobicare.cielo.mfa.api.MfaEnrollmentRequest
import br.com.mobicare.cielo.migration.domain.MigrationRequest
import br.com.mobicare.cielo.minhasVendas.domain.*
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.RequestCancelApi
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseBanlanceInquiry
import br.com.mobicare.cielo.minhasVendas.fragments.cancelamento.ResponseCancelVenda
import br.com.mobicare.cielo.mySales.data.model.responses.ResultCardBrands
import br.com.mobicare.cielo.mySales.data.model.responses.ResultPaymentTypes
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummaryCanceledSales
import br.com.mobicare.cielo.mySales.data.model.responses.ResultSummarySalesHistory
import br.com.mobicare.cielo.newLogin.domain.LoginRequest
import br.com.mobicare.cielo.newLogin.domain.LoginResponse
import br.com.mobicare.cielo.newLogin.domain.PostRegisterDeviceRequest
import br.com.mobicare.cielo.newLogin.domain.PostRegisterDeviceResponse
import br.com.mobicare.cielo.notification.domain.NotificationCountResponse
import br.com.mobicare.cielo.notification.domain.NotificationResponse
import br.com.mobicare.cielo.orders.domain.OrderReplacementRequest
import br.com.mobicare.cielo.orders.domain.OrderReplacementResponse
import br.com.mobicare.cielo.orders.domain.OrderRequest
import br.com.mobicare.cielo.orders.domain.OrdersResponse
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyRequest
import br.com.mobicare.cielo.pagamentoLink.domain.CreateLinkBodyResponse
import br.com.mobicare.cielo.pagamentoLink.domain.PaymentLinkResponse
import br.com.mobicare.cielo.pagamentoLink.domains.DeleteLink
import br.com.mobicare.cielo.pagamentoLink.orders.model.Order
import br.com.mobicare.cielo.pagamentoLink.orders.repository.LinkOrdersResponse
import br.com.mobicare.cielo.pagamentoLink.presentation.ui.solicitationMotoboy.ResponseMotoboy
import br.com.mobicare.cielo.pedidos.domain.OrderMachineResponse
import br.com.mobicare.cielo.pedidos.tracking.model.OrderAffiliationDetail
import br.com.mobicare.cielo.pix.domain.ResponseEligibilityPix
import br.com.mobicare.cielo.pix.domain.ResponsePixDataQuery
import br.com.mobicare.cielo.recebaMais.domain.*
import br.com.mobicare.cielo.recebaMais.domains.entities.ContractDetailsResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratacaoResponse
import br.com.mobicare.cielo.recebaMais.domains.entities.ContratarEmprestimoRecebaMaisRequest
import br.com.mobicare.cielo.recebaMais.domains.entities.ResumoResponse
import br.com.mobicare.cielo.research.domains.entities.ResearchRating
import br.com.mobicare.cielo.research.domains.entities.ResearchResponse
import br.com.mobicare.cielo.selfRegistration.domains.AccountRegistrationPayLoadRequest
import br.com.mobicare.cielo.selfRegistration.domains.SelfRegistrationResponse
import br.com.mobicare.cielo.splash.domain.entities.Configuration
import br.com.mobicare.cielo.suporteTecnico.domain.entities.SupportItem
import br.com.mobicare.cielo.taxaPlanos.domain.*
import br.com.mobicare.cielo.taxaPlanos.presentation.ui.postecipado.meuAluguel.PlanInformationResponse
import br.com.mobicare.cielo.turboRegistration.data.model.response.AddressResponse
import io.reactivex.Completable
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.math.BigDecimal

/**
 * Created by benhur.souza on 31/03/2017.
 */

interface CieloAPI {

    @GET("maps/api/geocode/json")
    fun googleMaps(@Query("address") address: String?): Observable<MeuCadastroEndereco>

    @GET("/site-cielo/v1/configurations")
    fun getConfiguration(): Observable<List<Configuration>>

    @GET("/api/customer/" + BuildConfig.API_VERSION + "/recoveryEc/{doc}")
    fun recoveryEstablishment(
        @Path("doc") doc: String
    ): Observable<EsqueciEstabelecimentoObj>

    @GET("/api/customer/" + BuildConfig.API_VERSION + "/recoveryLogin/{doc}")
    fun recoveryUser(
        @Path("doc") doc: String
    ): Observable<EsqueciUsuarioObj>

    @POST("/api/customer/" + BuildConfig.API_VERSION + "/sendLoginEmail/{ec}/{userId}")
    fun sendEmail(
        @Path("userId") userId: String?,
        @Path("ec") ec: String?
    ): Observable<EsqueciUsuarioObj>

    @GET("/site-cielo/v1/configurations/help")
    fun registrationData(@Header("access_token") accessToken: String): Observable<CentralAjudaObj>

    @GET("/site-cielo/v1/configurations/help")
    fun unloggedRegistrationData(): Observable<CentralAjudaObj>

    @GET("/site-cielo/v1/sales/receivables/balance/postings/summary")
    fun getCaculationVision(
        @Header("Authorization") bearerToken: String,
        @Header("access_token") accessToken: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Query("initialDate") initialDate: String? = null,
        @Query("finalDate") finalDate: String? = null
    ): Observable<SummaryResponse>

    @GET("/site-cielo/v1/sales/receivables/balance/postings")
    fun getCaculationVisionGraph(
        @Header("Authorization") bearerToken: String,
        @Header("access_token") accessToken: String,
        @Header("client_id") clientId: String,
        @Query("initialDate") initialDate: String? = null,
        @Query("finalDate") finalDate: String? = null
    ): Observable<PostingsResponse>

    @GET
    fun getSummaryView(
        @Url url: String,
        @Header("Authorization") bearerToken: String,
        @Header("access_token") accessToken: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Query("initialDate") initialDate: String? = null,
        @Query("finalDate") finalDate: String? = null,
        @Query("cardBrands") cardBrands: List<Int>? = null,
        @Query("paymentTypes") paymentTypes: List<Int>? = null,
        @Query("roNumber") roNumber: String? = null,
        @Query("page") page: Int? = 1,
        @Query("pageSize") pageSize: Int? = 25
    ): Observable<SummaryViewResponse>

    @GET
    @Headers(value = ["auth: required", "appToken: required"])
    fun getDetailSummaryView(
        @Url url: String,
        @Query("customId") customId: String? = null,
        @Query("initialDate") initialDate: String? = null,
        @Query("finalDate") finalDate: String? = null,
        @Query("paymentTypeCode") paymentTypeCode: List<Int>? = null,
        @Query("cardBrandCode") cardBrandCode: List<Int>? = null,
        @Query("authorizationCode") authorizationCode: String? = null,
        @Query("nsu") nsu: Int? = null,
        @Query("operationNumber") operationNumber: String? = null,
        @Query("roNumber") roNumber: String? = null,
        @Query("initialAmount") initialAmount: Double? = null,
        @Query("finalAmount") finalAmount: Double? = null,
        @Query("saleCode") saleCode: String? = null,
        @Query("transactionId") transactionId: String? = null,
        @Query("truncatedCardNumber") truncatedCardNumber: String? = null,
        @Query("terminal") terminal: String? = null,
        @Query("transactionTypeCode") transactionTypeCode: Int? = null,
        @Query("merchantId") merchantId: String? = null,
        @Query("page") page: Int? = 1,
        @Query("pageSize") pageSize: Int? = 25
    ): Observable<DetailSummaryViewResponse>

    @POST("site-cielo/v1/accounts/forgot-password")
    @Headers("ec: no-required")
    fun recoveryPassword(
        @Body data: RecoveryPassword,
        @Header("X-acf-sensor-data") akamaiSensorData: String?
    ): Observable<RecoveryPasswordResponse>

    @GET("site-cielo/v1/configurations/support")
    fun fetchTechnicalSupport(): Observable<List<SupportItem>>

    @GET("/site-cielo/v1/configurations/featuretoggle")
    fun getFeatureToggle(
        @Query("system") system: String?,
        @Query("version") version: String?,
        @Query("platform") platform: String?,
        @Query("page") page: Int? = ZERO,
        @Query("size") pageSize: Int? = FOUR_HUNDRED,
        @Query("status") status: String? = ACTIVATED_STATUS
    ): Observable<FeatureToggleResponse>

    @GET("site-cielo/v1/prepaid")
    @Headers("auth: no-required")
    fun getUserPrepaidInformation(@Header("access_token") accessToken: String): Observable<PrepaidResponse>

    @PUT("accounts/v1/password")
    fun getChangePassword(
        @Header("access_token") accessToken: String,
        @Header("authorization") authorization: String,
        @Body body: BodyChangePassword,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<Response<Void>>

    @POST("appcielo/v1/push/user/device")
    fun sendTokenFCM(@Body tokenFCM: TokenFCM): Observable<SendDeviceTokenResponse>

    @GET("/site-cielo/v1/configurations/research")
    @Headers("auth: required")
    fun getResearch(
        @Header("username") username: String,
        @Header("merchantId") merchantId: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<ResearchResponse?>

    @POST("/site-cielo/v1/configurations/research")
    @Headers("auth: required")
    fun saveResearch(
        @Header("username") username: String,
        @Header("merchantId") merchantId: String,
        @Body params: ResearchRating,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Completable

    @GET("appcielo/v1/merchant/user/research")
    @Headers("auth: required")
    fun getResearchOld(
        @Header("username") username: String,
        @Header("merchantId") merchantId: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<ResearchResponse?>

    @POST("appcielo/v1/merchant/user/research")
    @Headers("auth: required")
    fun saveResearchOld(
        @Header("username") username: String,
        @Header("merchantId") merchantId: String,
        @Body params: ResearchRating,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Completable

    @GET("site-cielo/v1/prepaid/cards/{proxy}/balance")
    fun fetchUserCardBalance(
        @Path("proxy") cardProxy: String,
        @Header("access_token") accessToken: String
    ): Observable<PrepaidBalanceResponse>

    @GET("site-cielo/v1/prepaid/cards/{cardProxy}/statement")
    fun getCreditCardsStatement(
        @Path("cardProxy")
        cardProxy: String,
        @Query("initialDate")
        initialDate: String,
        @Query("finalDate")
        finalDate: String,
        @Query("pageSize")
        pageSize: Int,
        @Query("page")
        page: Int,
        @Header("merchantId") merchantId: String?,
        @Header("access_token") accessToken: String,
    ): Observable<CreditCardStatement>

    @POST("site-cielo/v1/prepaid/cards/{proxy}/activate")
    fun activateCard(
        @Header("merchantId") merchantId: String,
        @Header("access_token") accessToken: String,
        @Path("proxy") proxy: String
    ): Observable<Response<Void>>

    @POST("site-cielo/v1/prepaid/cards/{proxy}/password")
    fun activateCatenoCard(
        @Path("proxy") proxy: String,
        @Body cardActivation: CardActivationCatenoRequest,
        @Header("access_token") accessToken: String,
        @Header("x-authorization") xAuthorization: String
    ): Observable<Response<Void>>


    @POST("site-cielo/v1/prepaid")
    fun sendDocumentCreate(
        @Header("merchantId") merchantId: String,
        @Header("access_token") accessToken: String,
        @Body imageDocument: ImageDocument
    ): Observable<MessagePhoto>

    @PATCH("site-cielo/v1/prepaid")
    fun sendDocumentUpdate(
        @Header("merchantId") merchantId: String,
        @Header("access_token") accessToken: String,
        @Body imageDocument: ImageDocument
    ): Observable<MessagePhoto>


    @GET("site-cielo/v1/accounts/banks")
    fun allBanks(@Header("client_id") clientId: String = BuildConfig.CLIENT_ID):
            Observable<BanksSet>

    @POST("site-cielo/v1/prepaid/cards/{cardProxy}/transfer/ted")
    fun beginTransfer(
        @Path("cardProxy") cardProxy: String,
        @Header("access_token") accessToken: String,
        @Header("client_id")
        clientId: String = BuildConfig.CLIENT_ID,
        @Body bankTransferRequest: BankTransferRequest
    ): Observable<TransferResponse>

    @POST("site-cielo/v1/prepaid/cards/{proxy}/transfer/{transferId}")
    fun confirmTransfer(
        @Path("proxy") cardProxy: String,
        @Header("access_token") accessToken: String,
        @Path("transferId") transferId: String,
        @Header("x-authorization") xAuthorization: String,
        @Header("client_id")
        clientId: String = BuildConfig.CLIENT_ID
    ): Observable<TransferConfirmationResponse>

    @POST("site-cielo/v1/prepaid/cards/{proxy}/payment")
    fun createPayment(
        @Path("proxy") cardProxy: String,
        @Header("access_token") accessToken: String,
        @Header("client_id")
        clientId: String = BuildConfig.CLIENT_ID,
        @Body paymentRequest: PrepaidPaymentRequest
    ): Observable<PrepaidPaymentResponse>

    @POST("site-cielo/v1/prepaid/cards/{proxy}/payment/{paymentId}")
    fun confirmPayment(
        @Path("proxy") cardProxy: String,
        @Path("paymentId") paymentId: String,
        @Header("access_token") accessToken: String,
        @Header("x-authorization") xAuthorization: String,
        @Header("client_id")
        clientId: String = BuildConfig.CLIENT_ID
    ): Observable<PrepaidPaymentResponse>

    @GET("site-cielo/v1/notifications")
    @Headers("auth: required")
    fun getAllNotification(
        @Query("quantity") quantity: Int? = TEN
    ): Observable<NotificationResponse>

    @GET("site-cielo/v1/notifications/new")
    @Headers("auth: required")
    fun getNotificationCount(): Observable<NotificationCountResponse>

    @POST("site-cielo/v1/accounts")
    @Headers("auth: required")
    fun registrationAccount(
        @Header("inviteToken") inviteToken: String?,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Header("X-acf-sensor-data") akamaiSensorData: String?,
        @Body accountRegistrationPayLoadRequest: AccountRegistrationPayLoadRequest
    ): Observable<SelfRegistrationResponse>

    //TODO confirmar que realmente não é usada
    @POST("appcielo/v1/authentication/v2")
    fun loginMultichannel(@Body multichannelRequest: LoginMultichannelRequest): Observable<LoginObj>

    @GET("site-cielo/v1/merchant")
    fun getMerchant(
        @Header("authorization") authorization: String,
        @Header("access_token") accessToken: String
    ): Observable<UserOwnerResponse>

    //TODO confirmar que realmente não é usada
    @GET("appcielo/v1/user/banks")
    fun getBanks(): Observable<BanksResponse>

    //TODO confirmar que realmente não é usada
    @GET("appcielo/v1/help/help-center")
    fun getHelpCenter(): Observable<HelpCenterResponse>

    @POST("site-cielo/v1/eco/{token}")
    fun borrow(
        @Path("token") token: String,
        @Body contratarEmprestimo: ContratarEmprestimoRecebaMaisRequest,
        @Header("access_token") accessToken: String,
        @Header("client-id") clientId: String = "APP",
        @Header("client_id") clientId2: String = BuildConfig.CLIENT_ID

    ): Observable<ContratacaoResponse>

    @GET("site-cielo/v1/eco/contract")
    fun summary(
        @Header("access_token") accessToken: String,
        @Header("client-id") clientId: String = "APP",
        @Header("client_id") clientId2: String = BuildConfig.CLIENT_ID
    ): Observable<ResumoResponse>

    @POST("site-cielo/v1/merchant/offers/eco/{offerId}/interest")
    fun keepInterestOffer(
        @Path("offerId") offerId: String,
        @Header("access_token") accessToken: String,
        @Header("authorization") authorization: String,
        @Header("client-id") clientId: String = "APP",
        @Header("client_id") clientId2: String = BuildConfig.CLIENT_ID
    ): Observable<ContratacaoResponse>

    @POST("site-cielo/v1/accounts/migration")
    fun migrationUser(
        @Body migrationRequest: MigrationRequest,
        @Header("access_token") accessToken: String,
        @Header("authorization") authorization: String
    ):
            Observable<MultichannelUserTokenResponse>

    @GET("accounts/v1/migration")
    @Headers("auth: required")
    fun getUserMigration(@Header("access_token") accessToken: String):
            Observable<Response<Unit>>

    @POST("site-cielo/v1/merchant/impersonate/{ec}")
    @Headers(value = ["appToken: required", "accessToken: no-required"])
    fun impersonate(
        @Path("ec") ec: String,
        @Header("access_token") token: String,
        @Query("type") type: String,
        @Body fingerprint: ImpersonateRequest,
    ): Observable<Impersonate>

    @GET("site-cielo/v1/merchant/children")
    @Headers(value = ["accessToken: no-required"])
    fun children(
        @Header("access_token") token: String,
        @Query("pageSize") pageSize: Int?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("searchCriteria") searchCriteria: String? = null
    ): Observable<HierachyResponse>


    @GET("site-cielo/v1/merchant/supplies")
    fun merchantSupplies(
        @Header("access_token") access_token: String
    ): Observable<MerchantSuppliesResponde>

    @GET("site-cielo/v1/merchant/supplies")
    fun loadSuplies(
        @Header("access_token") accessToken: String,
        @Header("authorization") authorization: String
    ): Observable<SupliesResponse>

    @GET("site-cielo/v1/merchant/supplies/shipment/address")
    fun merchantAddress(@Header("access_token") accessToken: String): Observable<MerchantAddressResponse>

    @POST("site-cielo/v1/merchant/supplies")
    fun merchantBuySupply(
        @Header("access_token") token: String,
        @Body supplies: ArrayList<MerchantSupplyChosen>
    ): Observable<MerchantBuySupplyChosenResponse>

    @GET("site-cielo/v1/ecommerce/payment/link")
    fun paymentLinkActive(
        @Header("access_token") token: String,
        @Query("size") size: Int,
        @Query("page") page: Int
    ): Observable<PaymentLinkResponse>

    @HTTP(method = "DELETE", path = "site-cielo/v1/ecommerce/payment/link", hasBody = true)
    fun deleteLink(
        @Header("access_token") token: String?,
        @Body linkId: DeleteLink
    ): Completable


    @POST("site-cielo/v1/ecommerce/payment/link")
    fun generateLink(
        @Header("access_token") token: String,
        @Body createLinkBodyRequest: CreateLinkBodyRequest
    ): Observable<CreateLinkBodyResponse>

    @GET("site-cielo/v1/ecommerce/payment/link/{linkId}/orders")
    fun getLinkOrders(
        @Header("access_token") token: String,
        @Path("linkId") linkId: String
    ): Observable<LinkOrdersResponse>

    @GET("site-cielo/v1/ecommerce/payment/link/orders/{orderId}")
    fun getLinkOrder(
        @Header("access_token") token: String,
        @Path("orderId") orderId: String
    ): Observable<Order>

    @POST("site-cielo/v1/mfa/enrollment/activate")
    @Headers("auth: no-required", "accessToken: required")
    fun activationCode(
        @Body activationCode: MfaEnrollmentRequest
    ): Observable<PutValueResponse>

    @POST("site-cielo/v1/mfa/challenge/activate")
    @Headers("auth: no-required", "accessToken: required")
    fun postMerchantChallengeActivate(@Body request: MerchantChallengerActivateRequest): Observable<Response<Void>>

    @GET("site-cielo/v1/faqs/{faqId}/subcategories")
    fun faqSubCategories(
        @Header("access_token") token: String,
        @Path("faqId") faqId: String
    ): Observable<List<SubCategorie>>

    @GET("site-cielo/v1/faqs")
    fun faqCategories(
        @Query("imageType") imageType: String,
        @Header("access_token") accessToken: String
    ): Observable<List<HelpCategory>>


    @GET("site-cielo/v1/faqs/subcategories/questions")
    fun getFrequentQuestions(
        @Header("access_token") accessToken: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ):
            Observable<List<QuestionDataResponse>>

    @GET("site-cielo/v1/me")
    @Headers("appToken: no-required")
    fun loadMe(
        @Header("access_token") token: String,
        @Header("token") appToken: String = token
    ): Observable<MeResponse>

    @GET("site-cielo/v1/merchant")
    fun loadMerchant(@Header("access_token") token: String): Observable<MCMerchantResponse>

    @GET("site-cielo/v1/merchant/solutions/brands")
    fun loadBrands(@Header("access_token") token: String): Observable<List<Solution>>

    @GET("site-cielo/v1/merchant/payment-accounts")
    @Headers(value = ["auth: required"])
    fun getDomiciles(
        @Query("protocol") protocol: String?,
        @Query("status") status: String? = "ALL",
        @Query("page") page: Int? = 1,
        @Query("pageSize") pageSize: Int? = 25
    ): Observable<PaymentAccountsDomicile>

    @GET("site-cielo/v1/faqs/{faqId}/subcategories/{subcategoryId}/questions")
    fun faqQuestions(
        @Header("access_token") token: String,
        @Path("faqId") faqId: String,
        @Path("subcategoryId") subcategoryId: String
    ): Observable<List<QuestionDataResponse>>

    @GET("site-cielo/v1/faqs/subcategories/questions")
    fun getFaqQuestionsByName(
        @Header("access_token") token: String,
        @Query("tag") tag: String
    ): Observable<List<QuestionDataResponse>>

    @GET("site-cielo/v1/faqs/{faqId}/subcategories/{subcategoryId}/questions/{questionId}")
    fun getQuestionDetails(
        @Header("access_token") token: String,
        @Path("faqId") faqId: String,
        @Path("subcategoryId") subcategoryId: String,
        @Path("questionId") questionId: String
    ):
            Observable<QuestionDataResponse>

    @PUT("site-cielo/v1/faqs/{faqId}/subcategories/{subcategoryId}/questions/{questionId}/likes")
    fun likeQuestion(
        @Header("access_token") token: String,
        @Path("faqId") faqId: String,
        @Path("subcategoryId") subcategoryId: String,
        @Path("questionId") questionId: String
    ): Observable<QuestionReactionResponse>


    @PUT("site-cielo/v1/faqs/{faqId}/subcategories/{subcategoryId}/questions/{questionId}/dislikes")
    fun dislikeQuestion(
        @Header("access_token") token: String,
        @Path("faqId") faqId: String,
        @Path("subcategoryId") subcategoryId: String,
        @Path("questionId") questionId: String
    ): Observable<QuestionReactionResponse>

    @GET("site-cielo/v1/faqs/contacts")
    fun getFaqContacts(
        @Header("access_token") token: String
    ): Observable<List<Contact>>

    @POST("site-cielo/v1/customer/care/ombudsman")
    @Headers(value = ["authorization: required"])
    fun sendProtocol(@Body params: OmbudsmanRequest): Observable<OmbudsmanResponse>

    @GET("/site-cielo/v1/merchant/solutions/plans/status")
    fun loadStatusPlan(
        @Header("access_token") token: String
    ): Observable<TaxaPlanosStatusPlanResponse>

    @GET("/site-cielo/v1/merchant/solutions/plans/overview/{type}")
    fun loadOverview(
        @Header("access_token") token: String,
        @Path("type") type: String
    ): Observable<TaxaPlanosOverviewResponse>

    @GET("site-cielo/v1/receba-rapido")
    @Headers(value = ["accessToken: required"])
    fun getOfferIncomingFastDetail(
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<OfferIncomingFastDetailResponse>

    @GET("/site-cielo/v1/merchant/solutions")
    fun loadMarchine(
        @Header("access_token") token: String
    ): Observable<TaxaPlanosSolutionResponse>

    @GET("site-cielo/v1/merchant/solutions/equipments")
    fun loadMerchantSolutionsEquipments(
        @Header("access_token") token: String
    ): Observable<TerminalsResponse>

    @GET("/site-cielo/v1/merchant/solutions/plans/details/{planName}")
    @Headers(value = ["accessToken: required"])
    fun loadPlanDetais(
        @Path("planName") planName: String
    ): Observable<TaxaPlanosDetailsResponse>

    @GET("/site-cielo/v1/merchant/solutions/plans/{planName}")
    @Headers(value = ["accessToken: required"])
    fun getPlanInformation(
        @Path("planName") planName: String
    ): Observable<PlanInformationResponse>

    @PUT("site-cielo/v1/merchant/owners/{cpf}")
    fun putMerchantOwner(
        @Header("access_token") token: String,
        @Header("otpCode") otpCode: String,
        @Path("cpf") cpf: String,
        @Body owner: PutEditOwnerRequest
    ): Observable<Response<Void>>

    @PUT("site-cielo/v1/merchant/contacts/{contactId}")
    fun putMerchantContact(
        @Header("access_token") token: String,
        @Header("otpCode") otpCode: String,
        @Path("contactId") contactId: Int?,
        @Body contact: PutEditContactRequest
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/merchant/government")
    fun loadReceitaFederal(@Header("access_token") accessToken: String)
            : Observable<ReceitaFederalResponse>


    @PUT("site-cielo/v1/merchant/government")
    fun saveReceitaFederal(@Header("access_token") accessToken: String)
            : Observable<ReceitaFederalResponse>


    @PUT("site-cielo/v1/merchant/addresses/{addressId}")
    fun updateUserAddress(
        @Header("access_token") accessToken: String,
        @Header("otpCode") otpCode: String,
        @Path("addressId") addressId: String,
        @Body updateAddressRequest: AddressUpdateRequest
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/merchant/addresses/cep/{cep}")
    @Headers("auth: required")
    fun getAddressByCep(
        @Header("access_token") accessToken: String,
        @Path("cep") cep: String
    ): Observable<AddressResponse>


    @PUT("site-cielo/v1/merchant/payment-accounts/card-brands")
    fun transferOfBrands(
        @Header("access_token") token: String,
        @Header("otpCode") otpCode: String,
        @Body transferFlag: FlagTransferRequest
    ): Observable<Response<Void>>

    @POST("site-cielo/v1/merchant/payment-accounts")
    fun domicilioTransferAccount(
        @Header("access_token") token: String,
        @Header("otpCode") otpGenerated: String? = null,
        @Body addFlag: AccountTransferRequest
    ): Observable<Response<Void>>

    @GET("site-cielo/v1/merchant/solutions/offers")
    fun loadSolutionsOffers(
        @Query("imageType") imageType: String,
        @Header("access_token") token: String
    ): Observable<MachineListOffersResponse>

    //TODO CONFIRMAR QUE REALMENTE NÃO É USADA
    @GET("appcielo/v1/merchants/addresses")
    fun fetchAddressByCep(
        @Header("access_token") accessToken: String,
        @Query("addressCode") cep: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Header("Content-Type") contentType: String = "text/xml"
    ): Observable<CepAddressResponse>

    @GET("site-cielo/v1/merchant/solutions/orders/availability")
    fun loadOrdersAvailability(
        @Header("access_token") token: String
    ): Observable<OrdersAvailabilityResponse>

    @POST("site-cielo/v1/merchant/solutions/orders")
    fun postOrders(
        @Header("access_token") token: String,
        @Body orderResquest: OrderRequest
    ): Observable<OrdersResponse>

    @POST("site-cielo/v1/merchant/solutions/orders/replacements")
    fun postOrdersReplacements(
        @Header("access_token") token: String,
        @Body orderResquest: OrderReplacementRequest
    ): Observable<OrderReplacementResponse>

    @PUT("site-cielo/v1/accounts/email-confirmation/{token}")
    @Headers("auth: required")
    fun verificationEmailConfirmation(
        @Path("token") token: String?
    ): Completable

    @POST("site-cielo/v1/accounts/resend/{token}")
    @Headers("auth: required")
    fun resendEmail(
        @Path("token") token: String?,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID,
        @Query("type") type: String = "EMAIL_CONFIRMATION"
    ): Observable<MultichannelUserTokenResponse>

    @GET("site-cielo/v1/sales/postings")
    fun getSummarySalesOnline(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?,
        @Query("cardBrand") cardBrand: List<Int>?,
        @Query("paymentType") paymentType: List<Int>?,
        @Query("terminal") terminal: List<String>?,
        @Query("status") status: List<Int>?,
        @Query("cardNumber") cardNumber: Int?,
        @Query("nsu") nsu: String?,
        @Query("authorizationCode") authorizationCode: String?,
        @Query("id") page: String?,
        @Query("pageSize") pageSize: Int?
    ): Observable<ResultSummarySales>

    //TODO API USADA
    @POST("appcielo/v1/user-login/token")
    @Headers(value = ["appToken: no-required", "accessToken: no-required"])
    fun login(
        @Body request: LoginRequest,
        @Header("ignoreSessionExpired") ignoreSessionExpired: String,
        @Header("X-acf-sensor-data") akamaiSensorData: String?
    ): Observable<LoginResponse>

    //TODO API USADA
    @POST("appcielo/v1/user-login/token")
    @Headers("auth: required")
    fun refreshToken(
        @Header("access_token") accessToken: String?,
        @Header("refresh_token") refreshToken: String?,
        @Header("X-acf-sensor-data") akamaiSensorData: String?,
    ): Observable<LoginResponse>

    //TODO API USADA
    @POST("appcielo/v1/user-login/token")
    @Headers("auth: required")
    fun callRefreshToken(
        @Header("access_token") accessToken: String?,
        @Header("refresh_token") refreshToken: String?,
        @Header("X-acf-sensor-data") akamaiSensorData: String?,
    ): Call<LoginResponse>

    @GET("/site-cielo/v1/menu/app")
    @Headers("auth: required")
    fun getOthersMenu(
        @Header("access_token") accessToken: String,
        @Query("platform") platform: String = "ANDROID"
    ): Observable<AppMenuResponse?>

    @GET("site-cielo/v1/sales/history")
    fun getSummarySalesHistory(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Query("type") type: String,

        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?,
        @Query("cardBrands") cardBrand: List<Int>?,
        @Query("paymentTypes") paymentType: List<Int>?
    ): Observable<ResultSummarySalesHistory>

    @GET("site-cielo/v1/statement/card-brands")
    fun getCardBrands(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String
    ): Observable<ResultCardBrands>

    @GET("site-cielo/v1/sales/filters")
    fun getPaymentTypes(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String
    ): Observable<ResultPaymentTypes>

    @GET("site-cielo/v1/sales")
    fun getSummarySales(
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,

        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String,

        @Query("initialAmount") initialAmount: Double?,
        @Query("finalAmount") finalAmount: Double?,
        @Query("customId") customId: String?,
        @Query("saleCode") saleCode: String?,
        @Query("truncatedCardNumber") truncatedCardNumber: String?,

        @Query("cardBrands") cardBrands: List<Int>?,
        @Query("paymentTypes") paymentTypes: List<Int>?,
        @Query("terminal") terminal: List<String>?,
        @Query("status") status: List<Int>?,
        @Query("cardNumber") cardNumber: Int?,
        @Query("nsu") nsu: String?,
        @Query("authorizationCode") authorizationCode: String?,
        @Query("page") page: Int?,
        @Query("pageSize") pageSize: Int?
    ): Observable<ResultSummarySales>

    /**
     * método para consultar o saldo da venda que será cancelada
     * */
    @GET("site-cielo/v1/sales/refunds/eligibles")
    fun balanceInquiry(
        @Query("cardBrandCode") cardBrandCode: String,
        @Query("authorizationCode") authorizationCode: String,
        @Query("nsu") nsu: String,
        @Query("logicalNumber") logicalNumber: String,
        @Query("truncatedCardNumber") truncatedCardNumber: String,
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String,
        @Query("paymentType") paymentType: String,
        @Query("grossAmount") grossAmount: String,
        @Query("page") page: Int?,
        @Query("pageSize") pageSize: Int?,
        @Header("Authorization") authorization: String,
        @Header("access_token") accessToken: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<ResponseBanlanceInquiry>

    /**
     * método para cancelar uma venda na api
     * */
    @POST("site-cielo/v1/sales/refunds")
    fun sendSaleToCancel(
        @Body sales: ArrayList<RequestCancelApi>,
        @Header("otpCode") otpGenerated: String? = null,
        @Header("access_token") accessToken: String,
        @Header("Authorization") authorization: String,
        @Header("client-id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<ResponseCancelVenda>


    @GET("/site-cielo/v1/sales/refunds/lifecycle")
    @Headers("auth: required")
    fun getCanceledSells(
        @Header("access_token") accessToken: String,
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String,
        @Query("page") page: Long?,
        @Query("pageSize") pageSize: Int?,
        @Query("nsu") nsu: String?,
        @Query("saleAmount") saleAmount: Double?,
        @Query("refundAmount") refundAmount: Double?,
        @Query("paymentTypes") paymentTypes: List<Int>?,
        @Query("cardBrands") cardBrands: List<Int>?,
        @Query("authorizationCode") authorizationCode: String?,
        @Query("tid") tid: String?
    ): Observable<ResultSummaryCanceledSales>


    @GET("/site-cielo/v1/sales/refunds/filters")
    @Headers("auth: required")
    fun filterCanceledSells(
        @Header("access_token") accessToken: String,
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String
    ): Observable<ResultPaymentTypes>

    @GET("site-cielo/v1/accounts/merchants")
    @Headers("auth: required")
    fun getMerchants(
        @Header("access_token") accessToken: String,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<MerchantsResponse>

    @POST("site-cielo/v1/accounts/merchants")
    @Headers("auth: required")
    fun addNewEc(
        @Body request: BankAccountObj, @Header("otpCode") otpCode: String
    ): Observable<Response<Void>>

    @POST("site-cielo/v1/ecommerce/payment/link/orders/{orderId}/delivery")
    @Headers("auth: required")
    fun callMotoboy(
        @Path("orderId") orderId: String,
        @Header("access_token") token: String = UserPreferences.getInstance().token,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID

    ): Observable<ResponseMotoboy>

    @GET("site-cielo/v1/ecommerce/payment/link/orders/{orderId}/delivery")
    @Headers("auth: required")
    fun resendCallMotoboy(
        @Path("orderId") orderId: String,
        @Header("access_token") token: String = UserPreferences.getInstance().token,
        @Header("client_id") clientId: String = BuildConfig.CLIENT_ID
    ): Observable<ResponseMotoboy>

    @GET("site-cielo/v1/mfa/enrollment")
    @Headers("auth: required")
    fun checkEnrollment(
        @Header("access_token") token: String = UserPreferences.getInstance().token
    ): Observable<EnrollmentResponse>

    @GET("site-cielo/v1/mfa/eligibility")
    @Headers("auth: required")
    fun checkMfaEligibility(
        @Header("access_token") token: String = UserPreferences.getInstance().token
    ): Observable<MfaEligibilityResponse>

    @GET("site-cielo/v1/mfa/banks")
    @Headers("auth: required")
    fun getMfaBanks(
        @Header("access_token") token: String = UserPreferences.getInstance().token
    ): Observable<ArrayList<MfaAccount>>

    @POST("site-cielo/v1/mfa/challenge")
    @Headers("auth: required")
    fun sendMFABankChallenge(@Body request: MfaAccount): Observable<EnrollmentResponse>

    @POST("site-cielo/v1/mfa/enrollment")
    @Headers("auth: required")
    fun postBankEnrollment(
        @Header("access_token") token: String = UserPreferences.getInstance().token,
        @Body request: MfaAccount
    ): Observable<BankEnrollmentResponse>

    @GET("site-cielo/v1/merchant/offers/eco")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun offers(@Header("access_token") token: String): Observable<OfferSet>

    @GET("site-cielo/v1/merchant/offers")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getHiringOffers(): Observable<List<HiringOffers>>

    @POST("site-cielo/v1/merchant/offers/{bannerId}/report/accept/BANNER")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun postTermoAceite(@Path("bannerId") bannerId: Int): Observable<Response<Void>>

    @GET("/site-cielo/v1/merchant/offers/eligible/RECEBA_RAPIDO")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getEligibleToOffer(@Header("client_id") clientId: String = BuildConfig.CLIENT_ID): Observable<EligibleOffer>

    @GET("site-cielo/v1/eco")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun simulation(
        @Header("access_token") token: String,
        @Query("offerId") offerId: String,
        @Query("loanAmount") loanAmount: BigDecimal,
        @Query("firstInstallmentDate") firstInstallmentDt: String
    ):
            Observable<LoanSimulationResponse>

    @GET("site-cielo/v1/eco/contract")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun fetchContracts(@Header("access_token") token: String): Observable<ResumoResponse>

    @GET("site-cielo/v1/eco/contract/detail")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getContractDetails(@Header("access_token") token: String): Observable<ContractDetailsResponse>

    @GET("site-cielo/v1/sales/receivables/filters")
    @Headers(value = ["auth: required, accessToken: required"])
    fun avaiableReceivableFilters(
        @Query("initialDate") initialDate: String?,
        @Query("finalDate") finalDate: String?
    ):
            Observable<FilterReceivableResponse>

    @GET("site-cielo/v1/sales/receivables/balance/bank-accounts/summary")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getReceivablesBankAccounts(
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String
    ): Observable<ReceivablesBankAccountsResponse>

    @GET("/site-cielo/v1/sales/receivables/notification")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun onLoadAlerts(): Observable<AlertsResponse>

    @GET("site-cielo/v1/sales/receivables/notification/pdf")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun onGeneratePdfAlerts(): Observable<FileResponse>

    @GET("site-cielo/v1/merchant/orders/affiliation/{orderId}")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getOrderAffiliationDetail(
        @Path("orderId") orderId: Int
    ): Observable<OrderAffiliationDetail>

    @GET("site-cielo/v1/merchant/orders/affiliation")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun fetchMachineOpenedOrders(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int = 10
    ):
            Observable<OrderMachineResponse>

    @DELETE("site-cielo/v1/receba-rapido")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun callDeleteRecebaRapido(): Observable<Response<Void>>

    @POST("site-cielo/v1/mfa/enrollment/activate")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun postEnrollmentActivate(@Header("otpCode") code: String): Observable<PutValueResponse>

    @GET("site-cielo/v1/mfa/enrollment/bank")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun fetchEnrollmentActiveBank(): Observable<EnrollmentBankResponse>

    @GET("site-cielo/v1/authorities/lgpd/eligibility")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun getLgpdElegibility(): Observable<LgpdElegibilityEntity>

    @POST("site-cielo/v1/authorities/lgpd")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun postLgpdAgreement(): Observable<Response<Void>>

    @GET("site-cielo/v1/merchant/permissions/eligible")
    @Headers(value = ["auth: required"])
    fun getMerchantPermissionsEligible(): Observable<MerchantPermissionsEligible>

    @GET("site-cielo/v1/merchant/permissions/registration")
    @Headers(value = ["auth: required"])
    fun balcaoRecebiveisPermissionRegister(): Observable<MerchantResponseRegisterGet>

    @POST("site-cielo/v1/merchant/permissions/register")
    @Headers(value = ["auth: required"])
    fun sendPermisionRegister(): Observable<Response<Void>>

    @GET("site-cielo/v1/merchant/solutions/account-debit/agreement")
    @Headers(value = ["auth: required"])
    fun getDebitoContaPermissionsEligible(): Observable<ResponseDebitoContaEligible>

    @POST("site-cielo/v1/merchant/solutions/account-debit/agreement_term/{operation}")
    @Headers(value = ["auth: required"])
    fun sendDebitoContaPermission(
        @Path("operation") operation: String
    ): Observable<Response<Void>>

    @GET("/site-cielo/v1/merchant/solutions/pix/eligibility")
    @Headers(value = ["auth: required"])
    fun pixElegibility(): Observable<ResponseEligibilityPix>

    @POST("site-cielo/v1/merchant/solutions/pix")
    @Headers(value = ["auth: required"])
    fun sendTerm(): Observable<Response<Void>>

    @POST("site-cielo/v1/merchant/solutions/pix/partner")
    @Headers(value = ["auth: required"])
    fun sendTermPixPartner(): Observable<Response<Void>>

    @GET("site-cielo/v1/merchant/solutions/pix")
    @Headers(value = ["auth: required"])
    fun pixDataQuery(): Observable<ResponsePixDataQuery>

    @GET("/site-cielo/v1/dirf/{type}")
    @Headers(value = ["auth: required"])
    fun callDirf(
        @Path("type") type: String,
        @Query("year") year: Int,
        @Query("cnpj") cnpj: String,
        @Query("companyName") companyName: String,
        @Query("owner") owner: String,
        @Query("cpf") cpf: String
    ): Observable<DirfResponse>

    @GET("/site-cielo/v1/dirf/{type}")
    @Headers(value = ["auth: required"])
    fun callDirfPDFOrExcel(
        @Path("type") type: String?,
        @Query("year") year: Int

    ): Observable<DirfResponse>

    @GET("/site-cielo/v1/sales/receivables/negotiations")
    @Headers(value = ["auth: required"])
    fun loadNegotiations(
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String
    ): Observable<Negotiations>

    @GET("/site-cielo/v1/sales/receivables/negotiations")
    @Headers(value = ["auth: required"])
    fun loadNegotiationsByFilter(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String,
        @Query("negotiationType") type: String,
        @Query("operationNumber") operationNumber: String?,
        @Query("initialAmount") initialAmount: Double?,
        @Query("finalAmount") finalAmount: Double?,
        @Query("identificationNumber") identificationNumber: String?
    ): Observable<Negotiations>

    @GET("/site-cielo/v1/sales/receivables/negotiations/bank-accounts")
    @Headers(value = ["auth: required"])
    fun loadNegotiationsBanks(
        @Query("initialDate") initialDate: String,
        @Query("finalDate") finalDate: String,
        @Query("negotiationType") type: String
    ): Observable<NegotiationsBanks>

    @GET("/site-cielo/v1/sales/receivables/negotiations/receivable-units")
    @Headers(value = ["auth: required"])
    fun getUnitReceivable(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("negotiationDate") negotiationDate: String,
        @Query("operationNumber") operationNumber: String,
        @Query("initialReceivableDate") initialReceivableDate: String?,
        @Query("finalReceivableDate") finalReceivableDate: String?,
        @Query("identificationNumber") identificationNumber: String?,
        @Query("cardBrands") cardBrands: List<Int>? = null
    ): Observable<ExtratoRecebiveisVendasUnitariasResponse>


    @GET("/site-cielo/v1/sales/receivables/negotiations/receivable-units/filters")
    @Headers(value = ["auth: required"])
    fun loadFiltroVendasUnitariasBrands(
        @Query("negotiationDate") date: String,
        @Query("operationNumber") identificationNumber: String
    ): Observable<VendasUnitariasFilterBrands>

    @POST("site-cielo/v1/mfa/enrollment/resend")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun resendMfa(
        @Body request: MfaResendRequest?
    ): Observable<Response<Void>>

    @POST("site-cielo/v1/accounts/device/register")
    @Headers(value = ["auth: required", "accessToken: required"])
    fun postRegisterDevice(
        @Header("faceid-token") faceIdToken: String,
        @Body body: PostRegisterDeviceRequest
    ): Observable<PostRegisterDeviceResponse>

    @GET("site-cielo/v1/accounts/contact-info")
    fun getUserAdditionalInfo() : Observable<GetUserAdditionalInfo>

}