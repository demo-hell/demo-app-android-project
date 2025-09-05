//package br.com.mobicare.cielo.meuCadastro.presentation.robot;
//
//import br.com.mobicare.cielo.R;
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroObj;
//import br.com.mobicare.cielo.meuCadastro.domains.entities.MeuCadastroSolucoesContratadas;
//
//import static android.support.test.espresso.Espresso.onView;
//import static android.support.test.espresso.assertion.ViewAssertions.matches;
//import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
//import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
//import static android.support.test.espresso.matcher.ViewMatchers.withId;
//import static android.support.test.espresso.matcher.ViewMatchers.withText;
//import static br.com.mobicare.cielo.centralDeAjuda.presentation.robot.CentralAjudaResult.withRecyclerView;
//import static org.hamcrest.Matchers.not;
//
///**
// * Created by benhur.souza on 27/04/2017.
// */
//
//public class MeuCadastroResult {
//
//    public MeuCadastroResult contentIsVisible() {
//        onView(withId(R.id.layout_central_ajuda))
//                .check(matches(isDisplayed()));
//        return this;
//    }
//
//    public MeuCadastroResult contentIsInvisible() {
//        onView(withId(R.id.layout_central_ajuda))
//                .check(matches(not(isDisplayed())));
//        return this;
//    }
//
//    public MeuCadastroResult loadingIsVisible() {
//        onView(withId(R.id.progress_meu_cadastro_loading))
//                .check(matches(isDisplayed()));
//        return this;
//    }
//
//    public MeuCadastroResult loadingIsInvisible() {
//        onView(withId(R.id.progress_meu_cadastro_loading))
//                .check(matches(not(isDisplayed())));
//        return this;
//    }
//
//    public MeuCadastroResult errorIsVisible(String error){
////        onView(withText(error))
////                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
//        return this;
//    }
//
//    public MeuCadastroResult EstabelecimentoIsVisible() {
//        onView(withId(R.id.layout_meu_cadastro_dados_estabelecimento))
//                .check(matches(isDisplayed()));
//        return this;
//    }
//
//    // DADOS DO ESTABELECIMENTO
//
//    public MeuCadastroResult checkCamposMeuEstabelecimento(MeuCadastroObj obj){
//        onView(withId(R.id.textview_meu_cadastro_estabelecimento_name)).check(matches(withText(obj.getName())));
//        onView(withId(R.id.textview_meu_cadastro_numero_estabelecimento)).check(matches(withText(obj.getEc())));
//        onView(withId(R.id.textview_meu_cadastro_cnpj)).check(matches(withText(obj.getDocumentNumber())));
//        onView(withId(R.id.textview_meu_cadastro_razao_social)).check(matches(withText(obj.getCorporateName())));
//        onView(withId(R.id.textview_meu_cadastro_ramo_da_atividade)).check(matches(withText(obj.getBusinessSegment())));
//        onView(withId(R.id.textview_meu_cadastro_data_de_abertura)).check(matches(withText(obj.getOpeningDate())));
//        onView(withId(R.id.textview_meu_cadastro_proprietario)).check(matches(withText(obj.getOwner())));
//        onView(withId(R.id.textview_meu_cadastro_telefone)).check(matches(withText(obj.getPhone())));
//        return this;
//    }
//
//    // ENREDECO
//    public MeuCadastroResult checkEndereco(String endereco){
//        onView(withId(R.id.textview_meu_cadastro_endereco)).check(matches(withText(endereco)));
//        return this;
//    }
//
//
//    public MeuCadastroResult validateMapIsVisible() {
//        onView(withId(R.id.imageview_meu_cadastro_maps))
//                .check(matches(isDisplayed()));
//        return this;
//    }
//
//    public MeuCadastroResult validateButtonsIsVisible(){
//        onView(withId(R.id.imageview_meu_cadastro_maps)).check(matches(isDisplayed()));
//        onView(withId(R.id.btn_endereco_contato)).check(matches(isDisplayed()));
//        onView(withId(R.id.btn_endereco_fisico)).check(matches(isDisplayed()));
//        return this;
//    }
//
//    // TODO FAZER O TESTE DO DOMICIIO BANCARIO PESQUISAR TEST CARDVIEW
//
//
//    // SOlUCOES CONTRATADAS
//    public MeuCadastroResult checkSoluoesContratadas(int position, MeuCadastroSolucoesContratadas[] solucoes){
//        onView(withRecyclerView(R.id.recycler_view_meu_cadastro_solucoes_contratadas).atPosition(position))
//                .check(matches(hasDescendant(withText(solucoes[position].getPrice()))))
//                .check(matches(hasDescendant(withText(solucoes[position].getQuantity()))))
//                .check(matches(hasDescendant(withText(solucoes[position].quantidadeItem()))))
//                .check(matches(hasDescendant(withText(solucoes[position].getDescription()))));
//        return this;
//    }
//
//    // SOlUCOES BANDEIRAS HABILITADAS
////    public MeuCadastroResult checkBandeirasHabilitadas(int position, MeuCadastroBandeirasHabilitadas[] enabledBrands){
////        onView(withRecyclerView(R.id.recycler_view_item_domicilio_bancario).atPosition(position))
////                .check(matches(hasDescendant(withText(enabledBrands[position].name))))
////                .check(matches(hasDescendant(withText(enabledBrands[position].imgUrl))));
////        return this;
////    }
//
//    // TODO FAZER O TESTE DO TAXAS BANDEIRAS
//
//
//
//}
