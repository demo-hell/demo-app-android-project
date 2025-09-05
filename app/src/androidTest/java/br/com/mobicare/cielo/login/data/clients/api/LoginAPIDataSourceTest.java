package br.com.mobicare.cielo.login.data.clients.api;

//import br.com.mobicare.cielo.commons.data.utils.APIUtils;

/**
 * Created by benhur.souza on 07/04/2017.
 */

//public class LoginAPIDataSourceTest {
    //./gradlew connectedStoreDevAndroidTest run this test

//    public static String EC_200 = "2000148870";
//    public static String EC_401 = "123";
//    public static String EC_401_processing_error = "2000148870000";
//    public static String USERNAME = "cmd03";
//    public static String PASSWORD = "cielo2212";
//
//    private LoginAPIDataSource mLoginAPIDataSource;
//    private Context appContext;
//
//    @Before
//    public void setup() {
//        appContext = InstrumentationRegistry.getTargetContext();
//        mLoginAPIDataSource = LoginAPIDataSource.getInstance(appContext);
//    }
//
//    @Test
//    public void testPreConditions() {
//        // check if LoginAPIDataSource is not null
//        assertNotNull(mLoginAPIDataSource);
//    }
//
//    @Test
//    public void callLoginAPI200() {
//        mLoginAPIDataSource.account(EC_200, USERNAME, PASSWORD).asObservable()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<CustumerVO>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        fail(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(CustumerVO custumerVO) {
//                        assertNotNull(custumerVO);
//                    }
//                });
//
//    }
//
//    @Test
//    public void callLoginAPI401() {
//        mLoginAPIDataSource.account(EC_401, USERNAME, PASSWORD)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<CustumerVO>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ErrorMessage errorMessage = APIUtils.convertToErro(e);
//                        assertEquals(errorMessage.code, "cielo.error.401");
//                    }
//
//                    @Override
//                    public void onNext(CustumerVO custumerVO) {
//                        fail();
//                    }
//                });
//    }
//
//    @Test
//    public void callLoginAPI401ProcessingError() {
//        mLoginAPIDataSource.account(EC_401_processing_error, USERNAME, PASSWORD)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<CustumerVO>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ErrorMessage errorMessage = APIUtils.convertToErro(e);
//                        assertEquals(errorMessage.code, "cielo.client.login.processing.error");
//                    }
//
//                    @Override
//                    public void onNext(CustumerVO custumerVO) {
//                        fail();
//                    }
//                });
//    }
//
//    @Test
//    public void callLoginAPI500() {
//        mLoginAPIDataSource.account(null, USERNAME, PASSWORD)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<CustumerVO>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        ErrorMessage errorMessage = APIUtils.convertToErro(e);
//                        assertEquals(errorMessage.code, "500");
//                    }
//
//                    @Override
//                    public void onNext(CustumerVO custumerVO) {
//                        fail();
//                    }
//                });
//    }
//
//    @Test
//    public void checkUserToken(){
//        TokenPreference.getInstance().saveUserToken(appContext, "");
//        mLoginAPIDataSource.account(EC_200, USERNAME, PASSWORD)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Observer<CustumerVO>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        fail(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(CustumerVO custumerVO) {
//                        assertNotSame(TokenPreference.getInstance().getUserToken(appContext), "");
//                    }
//                });
//    }
//}
