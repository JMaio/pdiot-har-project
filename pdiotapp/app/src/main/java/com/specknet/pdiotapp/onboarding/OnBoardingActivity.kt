package com.specknet.pdiotapp.onboarding

import android.Manifest
import android.animation.ArgbEvaluator
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.ui.main.SectionsPagerAdapter
import com.specknet.pdiotapp.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*

class OnBoardingActivity : AppCompatActivity() {

    lateinit var mSectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var mViewPager: ViewPager
    private lateinit var mNextBtn: ImageButton
    private lateinit var mSkipBtn: Button
    private lateinit var mFinishBtn: Button

    private lateinit var zero: ImageView
    private lateinit var one: ImageView
    private lateinit var two: ImageView
    private lateinit var indicators: Array<ImageView>

    private var lastLeftValue = 0

    private lateinit var mCoordinator: CoordinatorLayout
    private val TAG = "PagerActivity"

    private var page = 0 // to track page position

    // permissions
    lateinit var permissionAlertDialog: AlertDialog.Builder

    val permissionsForRequest = arrayListOf<String>()

    var locationPermissionGranted = false
    var cameraPermissionGranted = false
    var readStoragePermissionGranted = false
    var writeStoragePermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        mNextBtn = findViewById(R.id.intro_btn_next)
        mNextBtn.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.ic_chevron_right)
        )

        mSkipBtn = findViewById(R.id.intro_btn_skip)
        mFinishBtn = findViewById(R.id.intro_btn_finish)

        zero = findViewById(R.id.intro_indicator_0)
        one = findViewById(R.id.intro_indicator_1)
        two = findViewById(R.id.intro_indicator_2)

        mCoordinator = findViewById(R.id.main_content)

        indicators = arrayOf(zero, one, two)

        // Set up the ViewPages with the sections adapter
        mViewPager = findViewById(R.id.container)
        mViewPager.adapter = mSectionsPagerAdapter
        mViewPager.setCurrentItem(page)
        updateIndicators(page)

        val color1 = ContextCompat.getColor(this, R.color.cyan)
        val color2 = ContextCompat.getColor(this, R.color.orange)
        val color3 = ContextCompat.getColor(this, R.color.green)

        val colorList = arrayOf(color1, color2, color3)
        val evaluator = ArgbEvaluator()

        permissionAlertDialog = AlertDialog.Builder(this)

        setupPermissions()

        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                /*
                color update
                 */
                val colorUpdate = evaluator.evaluate(
                    positionOffset,
                    colorList[position],
                    colorList[if (position === 2) position else position + 1]
                ) as Int
                mViewPager.setBackgroundColor(colorUpdate)
            }

            override fun onPageSelected(position: Int) {
                page = position
                updateIndicators(page)

                when (position) {
                    0 -> mViewPager.setBackgroundColor(color1)
                    1 -> mViewPager.setBackgroundColor(color2)
                    2 -> mViewPager.setBackgroundColor(color3)
                }

                mNextBtn.visibility = if (position === 2) View.GONE else View.VISIBLE
                mFinishBtn.visibility = if (position === 2) View.VISIBLE else View.GONE

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        mNextBtn.setOnClickListener {
            page++
            mViewPager.setCurrentItem(page, true)
        }

        mSkipBtn.setOnClickListener {
            finish()
        }

        mFinishBtn.setOnClickListener {
            finish()
            // TODO only here should you save the shared preference
        }
    }

    fun updateIndicators(position: Int) {
        for (i in indicators.indices) {
            if (i == position) {
                indicators[i].setBackgroundResource(R.drawable.indicator_selected)
            } else indicators[i].setBackgroundResource(R.drawable.indicator_unselected)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_pager, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    fun setupPermissions() {
        // request permissions

        // location permission
        Log.i("Permissions", "Location permission = " + locationPermissionGranted)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsForRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
            permissionsForRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            locationPermissionGranted = true
        }

        // camera permission
        Log.i("Permissions", "Camera permission = " + cameraPermissionGranted)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("Permissions", "Camera permission = " + cameraPermissionGranted)
            permissionsForRequest.add(Manifest.permission.CAMERA)
        } else {
            cameraPermissionGranted = true
        }

        // read storage permission
        Log.i("Permissions", "Read st permission = " + readStoragePermissionGranted)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("Permissions", "Read st permission = " + readStoragePermissionGranted)
            permissionsForRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            readStoragePermissionGranted = true
        }

        // write storage permission
        Log.i("Permissions", "Write storage permission = " + writeStoragePermissionGranted)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i("Permissions", "Write storage permission = " + writeStoragePermissionGranted)
            permissionsForRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            writeStoragePermissionGranted = true
        }

        if (permissionsForRequest.size >= 1) {
            ActivityCompat.requestPermissions(
                this,
                permissionsForRequest.toTypedArray(),
                Constants.REQUEST_CODE_PERMISSIONS
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Constants.REQUEST_CODE_PERMISSIONS) {
            if (grantResults.isNotEmpty()) {
                for (i in grantResults.indices) {
                    when (permissionsForRequest[i]) {
                        Manifest.permission.ACCESS_COARSE_LOCATION -> locationPermissionGranted =
                            true
                        Manifest.permission.ACCESS_FINE_LOCATION -> locationPermissionGranted = true
                        Manifest.permission.CAMERA -> cameraPermissionGranted = true
                        Manifest.permission.READ_EXTERNAL_STORAGE -> readStoragePermissionGranted =
                            true
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> writeStoragePermissionGranted =
                            true
                    }

                }
            }
        }

        // count how many permissions need granting
        var numberOfPermissionsUngranted = 0
        if (!locationPermissionGranted) numberOfPermissionsUngranted++
        if (!cameraPermissionGranted) numberOfPermissionsUngranted++
        if (!readStoragePermissionGranted) numberOfPermissionsUngranted++
        if (!writeStoragePermissionGranted) numberOfPermissionsUngranted++

        // show a general message if we need multiple permissions
        if (numberOfPermissionsUngranted > 1) {
            val generalSnackbar = Snackbar
                .make(
                    coordinatorLayout,
                    "Several permissions are needed for correct app functioning",
                    Snackbar.LENGTH_LONG
                )
                .setAction("SETTINGS") {
                    startActivity(Intent(Settings.ACTION_SETTINGS))
                }
                .show()
        } else if (numberOfPermissionsUngranted == 1) {
            var snackbar: Snackbar = Snackbar.make(coordinatorLayout, "", Snackbar.LENGTH_LONG)
            if (!locationPermissionGranted) {
                snackbar = Snackbar
                    .make(
                        coordinatorLayout,
                        "Location permission needed for Bluetooth to work.",
                        Snackbar.LENGTH_LONG
                    )
            }

            if (!cameraPermissionGranted) {
                snackbar = Snackbar
                    .make(
                        coordinatorLayout,
                        "Camera permission needed for QR code scanning to work.",
                        Snackbar.LENGTH_LONG
                    )
            }

            if (!readStoragePermissionGranted || !writeStoragePermissionGranted) {
                snackbar = Snackbar
                    .make(
                        coordinatorLayout,
                        "Storage permission needed to record sensor.",
                        Snackbar.LENGTH_LONG
                    )
            }

            snackbar.setAction("SETTINGS") {
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
                .show()
        }

    }
}