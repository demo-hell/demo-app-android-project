//package br.com.mobicare.cielo.esqueciUsuarioAndEstabelecimento.presentation.robot
//
//import android.support.test.espresso.Espresso
//import android.support.test.espresso.assertion.ViewAssertions
//import android.support.test.espresso.matcher.ViewMatchers
//import br.com.mobicare.cielo.R
//import org.hamcrest.Matchers
//
///**
// * Created by Benhur on 04/09/17.
// */
//class EsqueciUsuarioResult {
//
///*    *//**
//    * Verifica se o loading esta sendo exibido
//    * @return
//    *//*
//    fun loadingIsVisible(): EsqueciUsuarioResult {
//        Espresso.onView(ViewMatchers.withId(R.id.progress_esqueci_usuario_loading))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
//        return this
//    }
//
//    *//**
//     * Verifica se o loading não esta sendo exibido
//     * @return
//     *//*
//    fun loadingIsHide(): EsqueciUsuarioResult {
//        Espresso.onView(ViewMatchers.withId(R.id.progress_esqueci_usuario_loading))
//                .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isDisplayed())))
//        return this
//    }
//
//    *//**
//     * Verifica se o campo está preenchido com o valor
//     *//*
//    fun compareFiled(msg: String): EsqueciUsuarioResult {
//        Espresso.onView(ViewMatchers.withId(R.id.edit_text_esqueci_field))
//                .check(ViewAssertions.matches(ViewMatchers.withText(msg)))
//        return this
//    }
//
//    *//**
//     * Verifica se o hint está preenchido com o valor
//     *//*
//    fun compareHint(msgId: Int): EsqueciUsuarioResult {
//        Espresso.onView(ViewMatchers.withId(R.id.edit_text_esqueci_field))
//                .check(ViewAssertions.matches(ViewMatchers.withHint(msgId)))
//        return this
//    }
//
//    *//**
//     * Verifica se o título está preenchido com o valor
//     *//*
//    fun checkTitle(msgId: Int): EsqueciUsuarioResult {
//        Espresso.onView(ViewMatchers.withId(R.id.textview_preciso_ajuda_title))
//                .check(ViewAssertions.matches(ViewMatchers.withText(msgId)))
//        return this
//    }
//
//    *//**
//     * Verifica se a descrição está preenchido com o valor
//     *//*
//    fun checkDescription(msgId: Int): EsqueciUsuarioResult {
//        Espresso.onView(ViewMatchers.withId(R.id.textview_preciso_ajuda_description))
//                .check(ViewAssertions.matches(ViewMatchers.withText(msgId)))
//        return this
//    }*/
//
//
//
//}