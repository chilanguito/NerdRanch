package com.android.nerdlauncher

import android.support.v4.app.Fragment

class NerdLauncherActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        return NerdLauncherFragment.newInstance()
    }

}
