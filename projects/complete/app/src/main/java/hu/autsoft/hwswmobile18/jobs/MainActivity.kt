package hu.autsoft.hwswmobile18.jobs

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import hu.autsoft.hwswmobile18.jobs.arch.Navigator
import hu.autsoft.hwswmobile18.jobs.ui.joblist.JobListFragment

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            add(JobListFragment())
        }
    }

    override fun add(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.mainContent, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun pop() {
        supportFragmentManager.popBackStack()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            pop()
        } else {
            finish()
        }
    }

}
