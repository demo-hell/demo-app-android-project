package br.com.mobicare.cielo.meuCadastro.presentation.ui.fragments;

/**
 * Created by silvia.miranda on 28/04/2017.
 */
//
//@RunWith(AndroidJUnit4.class)
//public class MeuCadastroFragmentTest {
//
//    private MeuCadastroViewRobot robot;
//    private MeuCadastroResult result;
//    private MeuCadastroObj mockObj;
//
//    @Before
//    public void setup() throws Exception {
//        robot = new MeuCadastroViewRobot();
//        robot.setup();
//
//        mockObj = ReaderMock.Companion.getMeuCadastro(robot.getContext());
//    }
//
//    @Test
//    /**
//     * Verifica se o loading aparece assim que a tela é carregada
//     */
//    public void testProgressIsVisible() throws Exception {
//        MeuCadastroResult result = new MeuCadastroResult();
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
//
//    }

//    @Test
//    @UiThreadTest
//    /**
//     * Testar carregamento dados do estabelecimento
//     */
//    public void testLoadDadosEstabelecimento() throws Exception {
//
//        result = robot.loadDadosEstabelecimento(mockObj).start();
//
//        result.checkCamposMeuEstabelecimento(mockObj);
//
//        result.EstabelecimentoIsVisible();
//    }

//    @Test
//    @UiThreadTest
//    /**
//     * Testar carregamento enderecos do estabelecimento
//     */
//    public void testLoadEnderecos() throws Exception {
//
//        MeuCadastroEndereco enderecoMock = mockObj.getUserAddresses()[0];
//
//        result = robot.loadEndederecos(enderecoMock).start();
//
//        result.checkEndereco(enderecoMock.getAddressConcatenado());
//
//        result.validateMapIsVisible();
//
//        result.validateButtonsIsVisible();
//
//        result = robot.selectedEnderecoContato().start();
//
//        result = robot.selectedEnderecoFisico().start();
//
//        result = robot.hideButtonEnderecos().start();
//
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar carregar/esconder de solucoes contratadas
//     */
//    public void testLoadSolucoesContratadas() throws Exception {
//
//        MeuCadastroSolucoesContratadas solucaoMock = mockObj.getHiredSolutions()[0];
//
//        result = robot.loadSolucoesContratadas(solucaoMock).start();
//
//        result = robot.hideSolucoesContratadas().start();
//
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar carregar/esconder de domicilio bancario
//     */
//    public void testLoadDomicilioBancario() throws Exception {
//
//        MeuCadastroDomicilioBancario bankMock = mockObj.getBankDatas()[0];
//
//        result = robot.loadDomicilioBancario(bankMock).start();
//
//        result = robot.loadUniqueDomicilioBancario(bankMock).start();
//
//        result = robot.hideDomicilioBancario().start();
//
//    }
//
//    @Test
//    @UiThreadTest
//    /**
//     * Testar carregar/esconder de bandeiras
//     */
//    public void testLoadBandeirasHabilitadas() throws Exception {
//
//        MeuCadastroBandeirasHabilitadas bandeiraMock = mockObj.getEnabledBrands()[0];
//
//        result = robot.loadBandeirasHabilitadas(bandeiraMock).start();
//
//        result = robot.loadUniqueBandeiraHabilitada(bandeiraMock).start();
//
//        result = robot.hideBandeirasHabilitadas().start();
//
//    }
//
//
//}
