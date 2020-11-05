package com.specknet.pdiotapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.specknet.pdiotapp.utils.setupWithNavController

class BottomNavActivity : AppCompatActivity() {
    // https://github.com/elye/demo_android_fragments_swapping/blob/master/app/src/main/java/com/elyeproj/bottombarfragmentsswitching/MainActivity.kt
//    val layout = findViewById<>()
//    private var savedStateSparseArray = SparseArray<Fragment.SavedState>()
//    private var currentSelectItemId = R.id.navigation_live
//    companion object {
//        const val SAVED_STATE_CONTAINER_KEY = "ContainerKey"
//        const val SAVED_STATE_CURRENT_TAB_KEY = "CurrentTabKey"
//    }
//
//    val fragmentLive = LiveDataFragment()
//    val fragmentRecord = {}
//    val fragmentConnect = ConnectingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (savedInstanceState != null) {
//            savedStateSparseArray = savedInstanceState.getSparseParcelableArray(SAVED_STATE_CONTAINER_KEY)
//                ?: savedStateSparseArray
//            currentSelectItemId = savedInstanceState.getInt(SAVED_STATE_CURRENT_TAB_KEY)
//        }
        setContentView(R.layout.bottom_nav)

//        makeCurrentFragment(fragmentLive)

//        bottomNavigationView.setOnNavigationItemSelectedListener {
//            when (it.itemId) {
//                R.id.navigation_live -> swapFragments(it.itemId, fragmentLive, "Live")
//                R.id.navigation_record -> swapFragments(it.itemId, fragmentLive, "Record")
//                R.id.navigation_connect -> swapFragments(it.itemId, fragmentConnect, "Connect")
//            }
//            true
//        }

        // https://stackoverflow.com/a/58859118/9184658
//        val navHostFragment = supportFragmentManager
//            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//        val navController = navHostFragment.navController

        val navHostFragment = findViewById<FragmentContainerView>(R.id.nav_host_container)
        // https://www.youtube.com/watch?v=Chso6xrJ6aU&ab_channel=Stevdza-San
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val navController = findNavController(R.id.nav_host_container)
//
//        val appBarConfiguration = AppBarConfiguration(setOf(
//            R.id.navigation_live,
//            R.id.navigation_record,
//            R.id.navigation_connect,
//        ))
//        setupActionBarWithNavController(navController, appBarConfiguration)

        bottomNavigationView.setupWithNavController(navController)

        val fab = findViewById<FloatingActionButton>(R.id.bottom_nav_fab)
        fab.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putSparseParcelableArray(SAVED_STATE_CONTAINER_KEY, savedStateSparseArray)
//        outState.putInt(SAVED_STATE_CURRENT_TAB_KEY, currentSelectItemId)
//    }
//
//    private fun makeCurrentFragment(f: Fragment) =
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.nav_host_fragment, f)
//            commit()
//        }
//
//    // https://proandroiddev.com/fragments-swapping-with-bottom-bar-ffbd265bd742
//    private fun swapFragments(@IdRes actionId: Int, f: Fragment, key: String) {
//        if (supportFragmentManager.findFragmentByTag(key) == null) {
//            savedFragmentState(actionId)
//            createFragment(f, key, actionId)
//        }
//    }
//
//    private fun createFragment(f: Fragment, key: String, actionId: Int) {
//        f.setInitialSavedState(savedStateSparseArray[actionId])
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.nav_host_fragment, f, key)
//            .commit()
//    }
//
//    private fun savedFragmentState(actionId: Int) {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
//        if (currentFragment != null) {
//            savedStateSparseArray.put(currentSelectItemId,
//                supportFragmentManager.saveFragmentInstanceState(currentFragment)
//            )
//        }
//        currentSelectItemId = actionId
//    }
}