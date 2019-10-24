package com.example.cendana2000.bakingapp;


import androidx.test.espresso.Espresso;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
