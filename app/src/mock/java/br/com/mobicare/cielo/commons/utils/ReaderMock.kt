package br.com.mobicare.cielo.commons.utils

import android.content.Context
import br.com.mobicare.cielo.R
import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoAvulsaObj
import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoContratoObj
import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoProgramadaObj
import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoSuccessObj
import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaObj
import br.com.mobicare.cielo.commons.domains.entities.ErrorMessage
import br.com.mobicare.cielo.commons.domains.entities.Message
import br.com.mobicare.cielo.esqueciSenha.domains.entities.BankListResponse
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciEstabelecimentoObj
import br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.domains.entities.EsqueciUsuarioObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoListaAcumulada.ExtratoListaObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoRecibo.ExtratoReciboObj
import br.com.mobicare.cielo.extrato.domains.entities.extratoTimeLine.ExtratoTimeLineObj
import br.com.mobicare.cielo.featureToggle.domain.FeatureToggleObj
import br.com.mobicare.cielo.fidelidade.domains.ProdutoFidelidadeObjList
import br.com.mobicare.cielo.login.domains.entities.LoginObj
import br.com.mobicare.cielo.massiva.domain.entities.MassivaStatusObj
import br.com.mobicare.cielo.meuCadastro.domains.entities.CardBrandFees
import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.MeusRecebimentosObj
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.PostingObject
import br.com.mobicare.cielo.meusRecebimentos.domains.entities.PostingOfDetailObject
import br.com.mobicare.cielo.postecipado.domain.PostecipadoObj
import br.com.mobicare.cielo.splash.domain.entities.Configuration
import br.com.mobicare.cielo.suporteTecnico.domain.entities.Support
import com.google.gson.GsonBuilder
import java.lang.reflect.Modifier

/**
 * Created by benhur.souza on 31/03/2017.
 */

open class ReaderMock {
    companion object {
        val TIME_LOADING = 1000


        fun getConfiguration(context: Context): Configuration {
            return convertToObject(R.raw.configuration, Configuration::class.java, context)
        }

        fun getContratoAntecipacao(context: Context): AntecipacaoContratoObj{
            return convertToObject(R.raw.antecipacao_termo, AntecipacaoContratoObj::class.java, context)
        }

        fun getAntecipacaoAvulsa(context: Context): AntecipacaoAvulsaObj{
            return convertToObject(R.raw.antecipacao_avulsa, AntecipacaoAvulsaObj::class.java, context)
        }

        fun getAntecipacaoProgramada(context: Context): AntecipacaoProgramadaObj{
            return convertToObject(R.raw.antecipacao_programada, AntecipacaoProgramadaObj::class.java, context)
        }

        fun getAntecipacaoSuccess(context: Context): AntecipacaoSuccessObj{
            return convertToObject(R.raw.antecipacao_contratacao, AntecipacaoSuccessObj::class.java, context)
        }

        fun getMeusRecebimentos(context: Context?): MeusRecebimentosObj {
            return convertToObject(R.raw.meus_recebimentos, MeusRecebimentosObj::class.java, context)
        }

        fun getMeusRecebimentosFiltro(context: Context?): MeusRecebimentosObj {
            return convertToObject(R.raw.recebimentos_filtro, MeusRecebimentosObj::class.java, context)
        }

        fun getLancamentos(context: Context?): Array<Double> {
            return convertToObject(R.raw.meus_recebimentos_dialog_lancamentos, Array<Double>::class.java, context)
        }

        fun getResumoOperacoes(context: Context?): PostingObject {
            return convertToObject(R.raw.postings, PostingObject::class.java, context)
        }

        fun getResumoOperacoesDetalhes(context: Context?): PostingOfDetailObject {
            return convertToObject(R.raw.postings_details, PostingOfDetailObject::class.java, context)
        }

        fun getExtratoListaAcumulada(context: Context?): ExtratoListaObj {
            return convertToObject(R.raw.extrato_lista_acumulada, ExtratoListaObj::class.java, context)
        }

        fun getExtratoTimeLineHoje(context: Context?): ExtratoTimeLineObj {
            return convertToObject(R.raw.extrato_time_line, ExtratoTimeLineObj::class.java, context)
        }

        fun getExtratoTimeLineZero(context: Context?): ExtratoTimeLineObj {
            return convertToObject(R.raw.extrato_timeline_vazio, ExtratoTimeLineObj::class.java, context)
        }

        fun getExtratoRecibo(context: Context?): ExtratoReciboObj {
            return convertToObject(R.raw.extrato_recibo, ExtratoReciboObj::class.java, context)
        }

        fun getExtratoTimeLine(context: Context?): ExtratoTimeLineObj {
            return convertToObject(R.raw.extrato_time_line_data, ExtratoTimeLineObj::class.java, context)
        }

        fun getProdutosFidelidade(context: Context?): ProdutoFidelidadeObjList {
            return convertToObject(R.raw.fidelidade_produtos, ProdutoFidelidadeObjList::class.java, context)
        }

        fun getMassivaStatus(context: Context?): MassivaStatusObj? {
            return convertToObject(R.raw.massiva, MassivaStatusObj::class.java, context)
        }

        fun getMassivaStatusOK(context: Context?): MassivaStatusObj? {
            return convertToObject(R.raw.massiva_ok, MassivaStatusObj::class.java, context)
        }

        fun getAccount(context: Context?): LoginObj? {
            return convertToObject(R.raw.account, LoginObj::class.java, context)
        }

        fun getBanks(context: Context): BankListResponse {
            return convertToObject(R.raw.banks, BankListResponse::class.java, context)
        }

        fun getRecoveryPassword(context: Context): Message {
            return convertToObject(R.raw.recovery_password, Message::class.java, context)
        }

        fun getRecoveryPasswordMassiva(context: Context): Message {
            return convertToObject(R.raw.recovery_password_massiva, Message::class.java, context)
        }

        fun getRecoveryPasswordError(context: Context): ErrorMessage {
            return convertToObject(R.raw.recovery_password_erro, ErrorMessage::class.java, context)
        }

        fun getRecoveryUser(context: Context?): EsqueciUsuarioObj? {
            return convertToObject(R.raw.recovery_user,  EsqueciUsuarioObj::class.java, context)
        }

        fun getSendEmail(context: Context?): EsqueciUsuarioObj {
            return convertToObject(R.raw.send_email, EsqueciUsuarioObj::class.java, context)
        }

        fun getRecoveryEC(context: Context?): EsqueciEstabelecimentoObj? {
            return convertToObject(R.raw.recovery_ec, EsqueciEstabelecimentoObj::class.java, context)
        }

        fun getRecoveryUserError(context: Context): ErrorMessage {
            return convertToObject(R.raw.recovery_user_error, ErrorMessage::class.java, context)
        }

        fun getRegistrationData(context: Context): CentralAjudaObj {
            return convertToObject(R.raw.registration_data, CentralAjudaObj::class.java, context)
        }

        fun getTechnicalSupport(context: Context): Support =
                simpleConvert(R.raw.technical_support_problem,
                        Support::class.java, context)

        fun getRegistrationDataWithoutManager(context: Context): CentralAjudaObj {
            return convertToObject(R.raw.registration_data_without_manager, CentralAjudaObj::class.java, context)
        }

        fun getMeuCadastro(context: Context): MeuCadastroObj {
            return convertToObject(R.raw.meu_cadastro, MeuCadastroObj::class.java, context)
        }

        fun getBrands(context: Context): CardBrandFees {
            return convertToObject(R.raw.card_brand_fees, CardBrandFees::class.java, context)
        }

        fun getMeuCadastroUnico(context: Context): MeuCadastroObj {
            return convertToObject(R.raw.meu_cadastro_unico, MeuCadastroObj::class.java, context)
        }

        fun getFeatureTogle(context: Context): FeatureToggleObj {
            return convertToObject(R.raw.feature_togle, FeatureToggleObj::class.java, context)
        }

        fun getPostecipado(context: Context?): PostecipadoObj {
            return convertToObject(R.raw.postecipado, PostecipadoObj::class.java, context)
        }

        fun <T> simpleConvert(resourceId: Int, classFile: Class<T>, context: Context?): T =
                GsonBuilder().create().fromJson(getResourceAsString(context, resourceId), classFile)

        fun <T> convertToObject(resourceId: Int, classFile: Class<T>, context: Context?): T {
            return GsonBuilder().excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC).create().fromJson(getResourceAsString(context, resourceId), classFile)
        }

        fun getResourceAsString(context: Context?, resourceId: Int): String? {
            if(context == null){
                return null
            }

            try {
                val res = context?.resources
                val in_s = res?.openRawResource(resourceId)
                if(in_s != null){
                    val b = ByteArray(in_s!!.available())
                    in_s.read(b)

                    return String(b)
                }

                return null

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }
    }


}
