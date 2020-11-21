package com.specknet.pdiotapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.specknet.pdiotapp.bluetooth.BluetoothService
import com.specknet.pdiotapp.bluetooth.ConnectingActivity
import com.specknet.pdiotapp.onboarding.OnBoardingActivity
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.Utils

class MainActivity : AppCompatActivity() {

    // buttons and textviews
//    lateinit var liveProcessingButton: Button
    lateinit var pairingButton: Button
    lateinit var recordButton: Button
    lateinit var testButton: Button
    lateinit var respeckStatus: TextView


    // broadcast receiver
    lateinit var respeckStatusReceiver: BroadcastReceiver
    val filter = IntentFilter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



//        liveProcessingButton = findViewById(R.id.live_button)
        pairingButton = findViewById(R.id.ble_button)
        recordButton = findViewById(R.id.record_button)
        testButton = findViewById(R.id.test_button)
        respeckStatus = findViewById(R.id.respeck_status_welcome)


        setupClickListeners()


        setupRespeckStatus()

        // register a broadcast receiver for respeck status
        filter.addAction(Constants.ACTION_RESPECK_CONNECTED)
        filter.addAction(Constants.ACTION_RESPECK_DISCONNECTED)

        respeckStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = intent.action

                when (action) {
                    Constants.ACTION_RESPECK_CONNECTED -> respeckStatus.text =
                        "Respeck status: Connected"
                    Constants.ACTION_RESPECK_DISCONNECTED -> respeckStatus.text =
                        "Respeck status: Disconnected"
                    else -> respeckStatus.text = "Error"
                }
            }
        }

        this.registerReceiver(respeckStatusReceiver, filter)
    }

    fun setupClickListeners() {
//        liveProcessingButton.setOnClickListener {
//            val intent = Intent(this, LiveDataFragment::class.java)
//            startActivity(intent)
//        }

        pairingButton.setOnClickListener {
            val intent = Intent(this, ConnectingActivity::class.java)
            startActivity(intent)
        }

        recordButton.setOnClickListener {
            val intent = Intent(this, RecordingActivity::class.java)
            startActivity(intent)
        }

//        testButton.setOnClickListener {
//            val i = Intent(this, BottomNavActivity::class.java)
//            startActivity(i)
//        }
    }


    fun setupRespeckStatus() {
        val isServiceRunning =
            Utils.isServiceRunning(BluetoothService::class.java, applicationContext)
        Log.i("debug", "isServiceRunning = " + isServiceRunning)

        // check sharedPreferences for an existing Respeck id
        val sharedPreferences =
            getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(Constants.RESPECK_MAC_ADDRESS_PREF)) {
            Log.i(
                "sharedpref",
                "Already saw a respeckID, starting service and attempting to reconnect"
            )
            respeckStatus.text = "Respeck status: Connecting..."

            // launch service to reconnect
            // start the bluetooth service if it's not already running
            if (!isServiceRunning) {
                Log.i("service", "Starting BLT service")
                val simpleIntent = Intent(this, BluetoothService::class.java)
                this.startService(simpleIntent)
            }
        } else {
            Log.i("sharedpref", "No Respeck seen before, must pair first")
            respeckStatus.text = "Respeck status: Unpaired"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(respeckStatusReceiver)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.show_tutorial) {
            val introIntent = Intent(this, OnBoardingActivity::class.java)
            startActivity(introIntent)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}