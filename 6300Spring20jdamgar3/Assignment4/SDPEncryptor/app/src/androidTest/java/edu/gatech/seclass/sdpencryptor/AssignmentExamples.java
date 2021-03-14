package edu.gatech.seclass.sdpencryptor;

import androidx.test.espresso.Espresso;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.*;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.AllOf.allOf;

/**
 * This is a Georgia Tech provided code example for use in assigned private GT repositories. Students and other users of this template
 * code are advised not to share it with other students or to make it available on publicly viewable websites including
 * repositories such as github and gitlab.  Such sharing may be investigated as a GT honor code violation. Created for CS6300.
 */


@RunWith(AndroidJUnit4.class)
public class AssignmentExamples {

    @Rule
    public ActivityTestRule<SDPEncryptorActivity> tActivityRule = new ActivityTestRule<>(
            SDPEncryptorActivity.class);


    @Test
    public void Text1() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("Cat & Dog"));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("2"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Reverse"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("Reverse"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.cipherText)).check(matches(withText("Vxe & Ujr")));
    }

    @Test
    public void Screenshot1() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("Up with the White And Gold!"));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("8"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("QWERTY"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("QWERTY"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.cipherText)).check(matches(withText("Eb tjwh whd Thjwd Ocs Gvzs!")));
    }

    @Test
    public void Screenshot2() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("abcdefg"));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("4"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Reverse"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("Reverse"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.cipherText)).check(matches(withText("vutsrqp")));
    }

    @Test
    public void Screenshot3() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("XYZabcABCxyz"));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("3"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Normal"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("Normal"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.cipherText)).check(matches(withText("ABCdefDEFabc")));
    }

    @Test
    public void ScreenshotErrors1() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("12345"));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("2"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("QWERTY"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("QWERTY"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.messageInput)).check(matches(hasErrorText("Invalid Message")));
    }

    @Test
    public void ScreenshotErrors2() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("12345"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("QWERTY"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("QWERTY"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.shiftNumber)).check(matches(hasErrorText("Invalid Shift Number")));
    }

    @Test
    public void ScreenshotErrors3() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("12345"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("QWERTY"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("QWERTY"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.messageInput)).check(matches(hasErrorText("Invalid Message")));
    }

    @Test
    public void ScreenshotErrors4() {
        onView(withId(R.id.messageInput)).perform(clearText());
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("QWERTY"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("QWERTY"))));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("5"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.messageInput)).check(matches(hasErrorText("Missing Message")));
    }


    @Test
    public void ScreenshotLabel() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("Test Example!!."));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("20"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Normal"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("Normal"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.cipherText)).check(matches(withText("Nymn Yrugjfy!!.")));
    }

    @Test
    public void ExtraTest1() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("Panda eats shoots and leaves."));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("5"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("QWERTY"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("QWERTY"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.cipherText)).check(matches(withText("Xyloy pynb bdzznb ylo jpyqpb.")));
    }

    @Test
    public void ExtraTest2() {
        onView(withId(R.id.messageInput)).perform(clearText(), replaceText("aaaaaa"));
        onView(withId(R.id.shiftNumber)).perform(clearText(), replaceText("1"));
        onView(withId(R.id.targetAlphabet)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Reverse"))).perform(click());
        onView(withId(R.id.targetAlphabet)).check(matches(withSpinnerText(containsString("Reverse"))));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.encryptButton)).perform(click());
        onView(withId(R.id.cipherText)).check(matches(withText("yyyyyy")));
    }

}