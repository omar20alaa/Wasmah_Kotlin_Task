package app.task.ui.activity

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import app.task.R
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeActivityTest {
    @Rule
    var homeActivityTestTestRule = ActivityTestRule(HomeActivity::class.java)
    private var homeActivity: HomeActivity? = null
    var monitor = InstrumentationRegistry.getInstrumentation().addMonitor(FavActivity::class.java.name, null, false)
    @Before
    @Throws(Exception::class)
    fun setUp() {
        homeActivity = homeActivityTestTestRule.activity
    }

    @Test
    fun testIntentNewActivity() {
        assert(homeActivity!!.findViewById<View?>(R.id.fab) != null)
        Espresso.onView(ViewMatchers.withId(R.id.fab)).perform(ViewActions.click())
        val second_activity = InstrumentationRegistry.getInstrumentation().waitForMonitorWithTimeout(monitor, 5000)!!
        second_activity.finish()
    }

    @Test
    fun testFindIdLaunch() {
        val view = homeActivity!!.findViewById<View>(R.id.title)!!
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        homeActivity = null
    }
}