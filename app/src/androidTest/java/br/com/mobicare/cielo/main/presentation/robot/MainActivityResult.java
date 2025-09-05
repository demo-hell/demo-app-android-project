package br.com.mobicare.cielo.main.presentation.robot;

public class MainActivityResult {

    public MainActivityResult menuDeslogadoIsVisible() {
//        onView(withId(R.id.layout_menu))
//                .check(matches(isDisplayed()));
        return this;
    }

    public MainActivityResult menuDeslogadoIsInvisible() {
//        onView(withId(R.id.layout_menu))
//                .check(matches(not(isDisplayed())));
        return this;
    }


}
