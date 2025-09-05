//package br.com.mobicare.cielo.splash.data.clients.api;
//
//
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import br.com.mobicare.cielo.splash.presentation.ui.activities.SplashActivity;
//
//
///**
// * Created by benhur.souza on 03/04/2017.
// */
//
//@RunWith(AndroidJUnit4.class)
//public class SplashAPIDataSourceTest {
//    @Rule
//    public ActivityTestRule<SplashActivity> mActivityTestRule = new ActivityTestRule<>(SplashActivity.class);
//
//    @Test
//    public void splashActivityTest() {
//    }
//
//    //./gradlew connectedStoreDevAndroidTest run this test
//
////    private SplashAPIDataSource mSplashAPIDataSource;
////
////    @Before
////    public void setup() {
////        Context appContext = InstrumentationRegistry.getTargetContext();
////        mSplashAPIDataSource = SplashAPIDataSource.getInstance(appContext);
////    }
////
////    @Test
////    public void testPreConditions() {
////        // check if MovieListLocalDataSource is not null
////        assertNotNull(mSplashAPIDataSource);
////    }
////
////    @Test
////    public void callConfigAPI() {
////        mSplashAPIDataSource.getConfig().asObservable()
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(new Observer<Configuration>() {
////                    @Override
////                    public void onCompleted() {
////                    }
////
////                    @Override
////                    public void onError(Throwable e) {
////                        fail(e.getMessage());
////                    }
////
////                    @Override
////                    public void onNext(Configuration config) {
////                        assertNotNull(config);
////                    }
////                });
////
////    }
//
//}
