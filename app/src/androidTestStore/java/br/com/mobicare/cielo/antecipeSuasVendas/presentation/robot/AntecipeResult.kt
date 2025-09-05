//package br.com.mobicare.cielo.antecipeSuasVendas.presentation.robot
//
//import android.support.test.espresso.Espresso.onData
//import android.support.test.espresso.Espresso.onView
//import android.support.test.espresso.assertion.ViewAssertions.matches
//import android.support.test.espresso.matcher.ViewMatchers.*
//import br.com.mobicare.cielo.R
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoAvulsaObj
//import br.com.mobicare.cielo.antecipeSuasVendas.domains.entities.AntecipacaoProgramadaObj
//import br.com.mobicare.cielo.commons.utils.Utils
//import kotlinx.android.synthetic.main.card_error.view.*
//import kotlinx.android.synthetic.main.card_view_programada.*
//import org.hamcrest.Matchers.allOf
//import org.hamcrest.Matchers.not
//
///**
// * Created by benhur.souza on 20/07/2017.
// */
//class AntecipeResult {
//
//    /**
//     * Verifica se o loading esta sendo exibido
//     * @return
//     */
//    fun loadingIsVisible(): AntecipeResult {
//        onView(withId(R.id.progress_antecipe))
//                .check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o loading esta sendo exibido
//     * @return
//     */
//    fun loadingProgramadaIsVisible(): AntecipeResult {
//        onView(withId(R.id.progress_antecipacao_programada))
//                .check(matches((isDisplayed())))
//        onView(withId(R.id.cardview_antecipe_programada_message))
//                .check(matches((isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o loading esta sendo exibido
//     * @return
//     */
//    fun loadingAvulsaIsVisible(): AntecipeResult {
//        onView(withId(R.id.progress_antecipacao_avulsa))
//                .check(matches((isDisplayed())))
//        onView(withId(R.id.cardview_antecipe_avulsa_msg))
//                .check(matches((isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o loading não esta sendo exibido
//     * @return
//     */
//    fun loadingIsHide(): AntecipeResult {
//        onView(withId(R.id.progress_antecipe))
//                .check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o loading avulsa não esta sendo exibido
//     * @return
//     */
//    fun loadingAvulsaIsHide(): AntecipeResult {
//        onView(withId(R.id.progress_antecipe))
//                .check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o loading não esta sendo exibido
//     * @return
//     */
//    fun loadingProgramadaIsHide(): AntecipeResult {
//        onView(withId(R.id.progress_antecipacao_programada))
//                .check(matches(not(isDisplayed())))
//        onView(withId(R.id.cardview_antecipe_programada_message))
//                .check(matches(not(isDisplayed())))
//        return this
//    }
//
//
//
//    /**
//     * Verifica se o card avulsa esta selecionado
//     */
//    fun cardAvulsaIsCheck() : AntecipeResult {
//        onView(withId(R.id.checkbox_meus_recebimentos_card_avulsa)).check(matches(isSelected()))
//        onView(withId(R.id.framelayout_antecipe_avulsa_selected)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o card avulsa nao esta selecionado
//     */
//    fun cardAvulsaIsNotCheck() : AntecipeResult {
//        onView(withId(R.id.checkbox_meus_recebimentos_card_avulsa)).check(matches(not(isSelected())))
//        onView(withId(R.id.framelayout_antecipe_avulsa_selected)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o card avulsa esta selecionado
//     */
//    fun cardProgramadaIsCheck() : AntecipeResult {
//        onView(withId(R.id.checkbox_meus_recebimentos_card_programada)).check(matches(isSelected()))
//        onView(withId(R.id.framelayout_antecipe_programada_selected)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o card avulsa nao esta selecionado
//     */
//    fun cardProgramdaIsNotCheck() : AntecipeResult {
//        onView(withId(R.id.checkbox_meus_recebimentos_card_programada)).check(matches(not(isSelected())))
//        onView(withId(R.id.framelayout_antecipe_programada_selected)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o contrato esta marcado
//     */
//    fun contratoIsCheck() : AntecipeResult {
//        onView(withId(R.id.imageview_antecipe_contrato_checked)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o contrato nao esta marcado
//     */
//    fun contratoIsNotCheck() : AntecipeResult {
//        onView(withId(R.id.imageview_antecipe_contrato_checked)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o card de aviso de horario esta sendo exibido
//     */
//    fun avisoIsVisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_avulsa_closed)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o card de aviso de horario nao esta sendo exibido
//     */
//    fun avisoIsInvisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_avulsa_closed)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o card de avulsa  esta sendo exibido
//     */
//    fun cardAvulsaIsVisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_avulsa)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o card de avulsa nao esta sendo exibido
//     */
//    fun cardAvulsaIsInvisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_avulsa)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o card de programada  esta sendo exibido
//     */
//    fun cardProgramadaIsVisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_programada)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o card de programada nao esta sendo exibido
//     */
//    fun cardProgramadaIsInvisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_programada)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se o card de avulsa  esta sendo exibido
//     */
//    fun cardAvulsaMessageIsVisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_avulsa_msg)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o card de avulsa nao esta sendo exibido
//     */
//    fun cardAvulsaMessageIsInvisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_avulsa_msg)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica se a mensagem de erro do avulsa esta sendo exibida
//     */
//    fun cardAvulsaErroIsVisible() : AntecipeResult{
//        return cardAvulsaMessageIsVisible()
//    }
//
//    /**
//     * Verifica se a mensagem de erro do avulsa nao esta sendo exibida
//     */
//    fun cardAvulsaErroIsInvisible() : AntecipeResult{
//        return cardAvulsaMessageIsInvisible()
//    }
//
//    /**
//     * Verifica se a mensagem de erro do programada nao esta sendo exibida
//     */
//    fun cardProgramadaErroIsInvisible() : AntecipeResult{
//        return cardProgramadaMessageIsInvisible()
//    }
//
//    /**
//     * Verifica se a mensagem de erro do programada esta sendo exibida
//     */
//    fun cardProgramadaErroIsVisible() : AntecipeResult{
//        return cardProgramadaMessageIsVisible()
//    }
//
//
//    /**
//     * Verifica se o card de programada  esta sendo exibido
//     */
//    fun cardProgramadaMessageIsVisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_programada_message)).check(matches(isDisplayed()))
//        return this
//    }
//
//    /**
//     * Verifica se o card de programada nao esta sendo exibido
//     */
//    fun cardProgramadaMessageIsInvisible() : AntecipeResult {
//        onView(withId(R.id.cardview_antecipe_programada_message)).check(matches(not(isDisplayed())))
//        return this
//    }
//
//    /**
//     * Verifica mensagem exibida no card programada
//     */
//    fun checkCardProgramadaMessage(message: String): AntecipeResult{
//        onView(allOf(withId(R.id.text_view_card_error_msg), isDescendantOfA(withId(R.id.card_programada_error))))
//                .check(matches(withText(message)))
//
//        return this
//    }
//
//    /**
//     * Verifica mensagem exibida no card avulsa
//     */
//    fun checkCardAvulsaMessage(message: String): AntecipeResult{
////        onView(withId(R.id.text_view_card_error_msg)).check(matches(withText(message)))
//        onView(allOf(withId(R.id.text_view_card_error_msg), isDescendantOfA(withId(R.id.card_avulsa_error))))
//                .check(matches(withText(message)))
//        return this
//    }
//
//    /**
//     * Verifica se a mensagem de erro do avulsa esta sendo exibida com o texto correto
//     */
//    fun checkAvulsaErroMessage(message: String): AntecipeResult{
//        return checkCardAvulsaMessage(message)
//    }
//
//    /**
//     * Verifica se a mensagem de erro do programada esta sendo exibida com o texto correto
//     */
//    fun checkProgramadaErroMessage(message: String): AntecipeResult{
//        return checkCardProgramadaMessage(message)
//    }
//
//    /**
//     * Verifica taxas no card avulsa
//     */
//    fun checkCardAvulsaTaxas(taxas: AntecipacaoAvulsaObj): AntecipeResult{
//        onView(withId(R.id.textview_card_avulsa_valor_disponivel)).check(matches(withText(Utils.formatValue(taxas.anticipationAmount))))
//        onView(withId(R.id.textview_card_avulsa_valor_operacao)).check(matches(withText(Utils.convertToPercent(taxas.operationTax))))
//        onView(withId(R.id.textview_card_avulsa_valor_depositado)).check(matches(withText(Utils.formatValue(taxas.amountToReceive))))
//        return this
//    }
//
//    /**
//     * Verifica taxas no card programada
//     */
//    fun checkCardProgramadaTaxas(taxas: AntecipacaoProgramadaObj): AntecipeResult{
//        onView(withId(R.id.textview_card_programada_promocao)).check(matches(withText(Utils.convertToPercent(taxas.promotionalTax))))
//        onView(withId(R.id.textview_card_programada_preco_padrao)).check(matches(withText(Utils.convertToPercent(taxas.defaultTax))))
//        return this
//    }
//
//    /**
//     * Verifica mensagem do contrato contem o toque aqui
//     */
//    fun checkContratoMessageClickHere(): AntecipeResult{
//        onView(withId(R.id.textview_antecipe_contrato)).check(matches(withText("toque aqui para ler")))
//        return this
//    }
//
//
//    /**
//     * Verifica mensagem do contrato nao contem o toque aqui
//     */
//    fun checkContratoMessageNotClickHere(): AntecipeResult{
//        onView(withId(R.id.textview_antecipe_contrato)).check(matches(not(withText("toque aqui para ler"))))
//        return this
//    }
//
//}