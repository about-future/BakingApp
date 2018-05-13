package com.aboutfuture.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class RecipesActivityTest {
    @Rule
    public ActivityTestRule<RecipesActivity> mActivityTestRule
            = new ActivityTestRule<>(RecipesActivity.class);

    @Test
    public void loadRecipesAndClickOnFirst() {
        // Open the first recipe
        Espresso.onView(withId(R.id.recipes_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Return
        Espresso.pressBack();
    }

    @Test
    public void clickOnLastRecipe_LoadFirstStep() {
        // Scroll to 4th recipe and click it
        Espresso.onView(withId(R.id.recipes_rv)).perform(RecyclerViewActions.scrollToPosition(3)).perform(click());

        // In the new activity, click on the first step of the recipe
        Espresso.onView(withId(R.id.recipe_steps_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // Check if video view is visible
        Espresso.onView(withId(R.id.playerView)).check(matches(isDisplayed()));
    }
}
