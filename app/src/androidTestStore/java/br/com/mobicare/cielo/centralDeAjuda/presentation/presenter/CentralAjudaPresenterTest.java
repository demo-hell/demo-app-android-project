package br.com.mobicare.cielo.centralDeAjuda.presentation.presenter;

/**
 * Created by benhur.souza on 24/04/2017.
 */
//
//@RunWith(AndroidJUnit4.class)
//public class CentralAjudaPresenterTest {

//    private CentralAjudaObj mockObj;
//    private CentralAjudaObj mockSemGestor;
//
//    private CentralAjudaViewRobot robot;
//    private CentralAjudaPresenterRobot presenterRobot;
//    private CentralAjudaResult result;

//    @Before
//    public void setup() throws Exception {
//        robot = new CentralAjudaViewRobot();
//        robot.setup();
//
//        presenterRobot = new CentralAjudaPresenterRobot(robot);
//
//        mockObj = ReaderMock.Companion.getRegistrationData(robot.getActivity());
//        mockSemGestor = ReaderMock.Companion.getRegistrationDataWithoutManager(robot.getActivity());
//    }
//
//    @Test
//    /**
//     * Verificar se mostra o loading ao chamar a API
//     */
//    public void testCallAPI() {
//        result = presenterRobot.callAPI().start();
//        result.loadingIsVisible();
//    }
//
//    @Test
//    /**
//     * Chama o metodo para fazer ligações
//     */
//    public void testCallPhone() throws Exception {
//        presenterRobot.callPhone(null)
//                .callPhone("");
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Verifica ações no Start da API -
//     * Iniciar Loading e esconder content
//     */
//    public void testAPIStart() throws Exception {
//        result = robot.hideContent().hideLoading().start();
//        result.loadingIsInvisible() //Verifica se o loading está hideUserBonus
//                .contentIsInvisible(); //Verifica se o layout está hideUserBonus
//
//        result = presenterRobot.startAPI().start();
//        result.loadingIsVisible()  //Verificar se loading vai aparecer
//                .contentIsInvisible(); //Verifica se layout continua hideUserBonus
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Verifica ações no onAuthenticationSuccess da API
//     */
//    public void testAPISuccess() throws Exception {
//
//        result = robot.hideLoading() //Desabilitar loading
//                .hideContent().start(); //Esconde layout
//
//        result.loadingIsInvisible() //Verifica se o loading está hideUserBonus
//                .contentIsInvisible(); //Verifica se o layout está hideUserBonus
//
//
//        result = presenterRobot.onSuccessAPI(mockObj).start();
//        result.loadingIsInvisible() //Verificar se loading continua hideUserBonus
//                .contentIsVisible() //Verifica se layout é exibido
//                .gestorComercialIsVisible(); //Verifica se Gestor Comercial é exibido
//    }

//    @Test
//    @UiThreadTest
//    /**
//     * Verifica ações no onAuthenticationSuccess da API sem Gestor Comercial
//     */
//    public void testAPISuccessWitoutManager() throws Exception {
//
//        result = robot.hideLoading() //Desabilitar loading
//                .hideContent().start(); //Esconde layout
//
//        result.loadingIsInvisible() //Verifica se o loading está hideUserBonus
//                .contentIsInvisible(); //Verifica se o layout está hideUserBonus
//
//
//        result = presenterRobot.onSuccessAPI(mockSemGestor).start();
//        result.loadingIsInvisible() //Verificar se loading continua hideUserBonus
//                .contentIsVisible() //Verifica se layout é exibido
//                .gestorComercialIsInvisible(); //Verifica se Gestor Comercial não é exibido
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Verifica ações no onError da API
//     */
//    public void testAPIonError() throws Exception {
//
//        final String error = "ERROR TEST";
//
//        result = presenterRobot.onErrorAPI(error).start();
//
//        result.loadingIsInvisible() //Verificar se loading está hideUserBonus
//                .contentIsInvisible() //Verifica se layout está hideUserBonus
//                .errorIsVisible(error); //Verifica se Toast é exibido
//
////        onView(withText(error))
////                .inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView())))).check(matches(isDisplayed()));
//    }

//}
