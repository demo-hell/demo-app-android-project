package br.com.mobicare.cielo.centralDeAjuda.presentation.ui.fragments;

/**
 * Created by benhur.souza on 24/04/2017.
 */
//
//@LargeTest
//@RunWith(AndroidJUnit4.class)
//public class CentralAjudaFragmentTest {
//
//    private CentralAjudaViewRobot robot;
//    private CentralAjudaResult result;
//    private CentralAjudaObj mockObj;
//
//    @Before
//    public void setup() throws Exception {
//        robot = new CentralAjudaViewRobot();
//        robot.setup();
//
//        mockObj = ReaderMock.Companion.getRegistrationData(robot.getContext());
//    }
//
//    @Test
//    /**
//     * Verifica se o loading aparece assim que a tela é carregada
//     */
//    public void testProgressIsVisible() throws Exception {
//        CentralAjudaResult result = new CentralAjudaResult();
//        result.loadingIsVisible();
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testa as chamadas de Show e Hide do progress
//     */
//    public void testProgressMethod() throws Exception {
//        result = robot.showLoading()
//                .start();
//
//        //Verifica se o progress está parecendo
//        result.loadingIsVisible();
//
//
//        result = robot.hideLoading()
//                .start();
//
//        //Verifica se o progress não está parecendo
//        result.loadingIsInvisible();
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testa as chamadas de Error
//     */
//    public void testErrorMethod() throws Exception {
//
//        final String error = "ERROR TEST";
//        result = robot.showError(error).start();
//
//        //Verifica se o loading não está sendo exibido e a mensagem de erro está correta
////        result.loadingIsInvisible().errorIsVisible(error);
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar as chamadas de Show e Hide do content
//     */
//    public void testContentMethod() throws Exception {
//        result = robot.showContent().start();
//
//        //Verifica se o content está parecendo
//        result.contentIsVisible();
//
//        result = robot.hideContent().start();
//
//        //Verifica se o content não está parecendo
//        result.contentIsInvisible();
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar as chamadas de Show e Hide do content Gestor
//     */
//    public void testContentGestorMethod() throws Exception {
//        result = robot.showGestor(mockObj.manager).start();
//
//        //Verifica se o content está parecendo
//        result.gestorComercialIsVisible();
//
//        result = robot.hideGestor().start();
//
//        //Verifica se o content não está parecendo
//        result.gestorComercialIsInvisible();
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar os valores do card Gestor
//     */
//    public void testContentGestor() throws Exception {
//        result = robot.showGestor(mockObj.manager).start();
//
//        result.gestorComercialIsVisible().  //Verifica se o content está parecendo
//                checkGestorName(mockObj.manager.name).  //Verificar nome do gestor comercial
//                checkGestorEmail(mockObj.manager.emailFormatted()). //Verificar email do gestor comercial
//                checkGestorTelefone(mockObj.manager.phoneFormatted()); //Verificar telefone do gestor comercial
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar os valores do header
//     */
//    public void testContentHeader() throws Exception {
//        result = robot.loadHeader(mockObj).start();
//
//        result.headerIsVisible() //Verifica se o content está parecendo
//                .checkHeaderChatOnline(mockObj.onlineConsultant.title)  //Verificar chat online
//                .checkHeaderDuvidasGerais(mockObj.faq.title) //Verificar duvidas gerais
//                .checkHeaderDuvidasTecnicas(mockObj.technicalQuestions.title); //Verificar duvidas tecnicas
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar os valores do Telefones Uteis
//     */
//    public void testContentPhoneSupport() throws Exception {
//        PhoneSupport phoneMock = mockObj.phonesSupport[0];
//
//        result = robot.loadPhoneSupport(phoneMock).start();
//
//        result.phoneSupportIsVisible() //Verifica se o content está parecendo
//                .checkPhoneSupportDescription(phoneMock.description) //Verificar descrição
//                .checkPhoneSupportDescriptionTime(phoneMock.time_description) //Verificar horário de atendimento
//                .checkPhoneSupportItem(0, phoneMock.phones); // Verificar valores dos numeros de telefone do primeiro elemento
//    }
//
//}
