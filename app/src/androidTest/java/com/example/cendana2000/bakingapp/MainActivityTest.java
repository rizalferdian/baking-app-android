package com.example.cendana2000.bakingapp;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() throws InterruptedException {
        CountingIdlingResource mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

        onView(withText("Nutella Pie")).check(matches(isDisplayed()));
        onView(withId(R.id.recycler_recipe)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText("Nutella Pie's Recipes")).check(matches(isDisplayed()));

        onView(withText("Recipe Introduction")).check(matches(isDisplayed()));
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));
        onView(withId(R.id.recipe_detail)).check(matches(withText("Recipe Introduction")));

        Thread.sleep(5000);

        onView(withId(R.id.exo_pause)).check(matches(isDisplayed()));
        onView(withId(R.id.exo_pause)).perform(click());

        Espresso.unregisterIdlingResources(mIdlingResource);
    }
}
