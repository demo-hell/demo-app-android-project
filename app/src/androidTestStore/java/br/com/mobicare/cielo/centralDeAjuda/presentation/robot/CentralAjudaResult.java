//package br.com.mobicare.cielo.centralDeAjuda.presentation.robot;
//
//import br.com.mobicare.cielo.R;
//import br.com.mobicare.cielo.centralDeAjuda.domains.entities.CentralAjudaDefaultObj;
//import br.com.mobicare.cielo.commons.utils.RecyclerViewMatcher;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static org.hamcrest.Matchers.not;
//
///**
// * Created by benhur.souza on 27/04/2017.
// */
//
//public class CentralAjudaResult {
//
//    /**
//     * Verifica se o loading esta sendo exibido
//     * @return
//     */
//    public CentralAjudaResult loadingIsVisible() {
//        onView(withId(R.id.progress_central_ajuda_loading))
//                .check(matches(isDisplayed()));
//        return this;
//    }
//
//    /**
//     * Verifica se o loading não esta sendo exibido
//     * @return
//     */
//    public CentralAjudaResult loadingIsInvisible() {
//        onView(withId(R.id.progress_central_ajuda_loading))
//                .check(matches(not(isDisplayed())));
//        return this;
//    }
//
//    /**
//     * Verifica se o Gestor Comercial não esta sendo exibido''
//     * @return
//     */
//    public CentralAjudaResult gestorComercialIsInvisible() {
//        onView(withId(R.id.card_view_central_ajuda_gestor_comercial))
//                .check(matches(not(isDisplayed())));
//        return this;
//    }
//
//    /**
//     * Verifica se o Gestor Comercial esta sendo exibido
//     * @return
//     */
//    public CentralAjudaResult gestorComercialIsVisible() {
//        onView(withId(R.id.card_view_central_ajuda_gestor_comercial))
//                .check(matches(isDisplayed()));
//        return this;
//    }
//
//    /**
//     * Verifica se o Layout não esta sendo exibido
//     * @return
//     */
//    public CentralAjudaResult contentIsInvisible() {
//        onView(withId(R.id.layout_central_ajuda))
//                .check(matches(not(isDisplayed())));
//        return this;
//    }
//
//
//    /**
//     * Verifica se o Layout esta sendo exibido
//     * @return
//     */
//    public CentralAjudaResult contentIsVisible() {
//        onView(withId(R.id.layout_central_ajuda))
//                .check(matches(isDisplayed()));
//        return this;
//    }
//
//    /**
//     * Verifica se o error está sendo exibido corretamente
//     *
//     * @param error
//     * @return
//     */
//    public CentralAjudaResult errorIsVisible(String error){
////        onView(withText(error))
////                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
//        return this;
//    }
//
//    public CentralAjudaResult checkGestorName(String name){
//        onView(withId(R.id.textview_ajuda_gestor_nome)).check(matches(withText(name)));
//        return this;
//    }
//
//    public CentralAjudaResult checkGestorTelefone(String phone){
//        onView(withId(R.id.textview_ajuda_gestor_telefone)).check(matches(withText(phone)));
//        return this;
//    }
//
//    public CentralAjudaResult checkGestorEmail(String email){
//        onView(withId(R.id.textview_ajuda_gestor_email)).check(matches(withText(email)));
//        return this;
//    }
//
//    public CentralAjudaResult headerIsVisible(){
//        onView(withId(R.id.content_central_ajuda_header)).check(matches(isDisplayed()));
//        return this;
//    }
//
//    public CentralAjudaResult phoneSupportIsVisible(){
//        onView(withId(R.id.content_central_ajuda_telefones_uteis)).check(matches(isDisplayed()));
//        return this;
//    }
//
//    public CentralAjudaResult checkPhoneSupportDescription(String description){
//        onView(withId(R.id.textview_ajuda_telefones_description)).check(matches(withText(description)));
//        return this;
//    }
//
//    public CentralAjudaResult checkPhoneSupportDescriptionTime(String time_description){
//        onView(withId(R.id.textview_ajuda_telefones_description_hour)).check(matches(withText(time_description)));
//        return this;
//    }
//
//    public CentralAjudaResult checkPhoneSupportItem(int position, CentralAjudaDefaultObj[] phone){
//        onView(withRecyclerView(R.id.recycler_view_ajuda_telefones_uteis).atPosition(position))
//                .check(matches(hasDescendant(withText(phone[position].description))))
//                .check(matches(hasDescendant(withText(phone[position].value))));
//        return this;
//    }
//
//    public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
//        return new RecyclerViewMatcher(recyclerViewId);
//    }
//
//
//}
