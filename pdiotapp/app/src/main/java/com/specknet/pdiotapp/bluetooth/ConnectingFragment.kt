package com.specknet.pdiotapp.bluetooth

import android.app.Activity
import android.content.*
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.exceptions.BleException
import com.specknet.pdiotapp.R
import com.specknet.pdiotapp.barcode.BarcodeActivity
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.Utils
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.android.synthetic.main.activity_connecting.*

class ConnectingFragment : Fragment() {
    // https://stackoverflow.com/a/18144296/9184658
    // keep the fragment alive
    override fun setRetainInstance(retain: Boolean) {
        super.setRetainInstance(true)
    }

    lateinit var ctx: Context

    val REQUEST_CODE_SCAN_RESPECK = 0

    private lateinit var scanButton: Button
    private lateinit var qrCode: EditText
    private lateinit var connectButton: Button
    private lateinit var disconnectButton: Button
    private lateinit var respeckStatus: TextView
    private lateinit var respeckCode: EditText

    // might need to be public
    lateinit var rxBleClient: RxBleClient

    private var respeckMAC = ""

    lateinit var sharedPreferences: SharedPreferences
    val filter = IntentFilter()
    lateinit var respeckStatusReceiver: BroadcastReceiver

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        val view: View = inflater.inflate(R.layout.activity_connecting, container, false)

        scanButton = view.findViewById(R.id.scan_respeck)
        qrCode = view.findViewById(R.id.respeck_code)
        connectButton = view.findViewById(R.id.connect_button)
        disconnectButton = view.findViewById(R.id.disconnect_button)
        respeckStatus = view.findViewById(R.id.respeck_status_connection)
        respeckCode = view.findViewById(R.id.respeck_code)

        scanButton.setOnClickListener {
            val barcodeScanner = Intent(ctx, BarcodeActivity::class.java)
            startActivityForResult(barcodeScanner, REQUEST_CODE_SCAN_RESPECK)
        }

        connectButton.setOnClickListener {
            // start the bluetooth service

            sharedPreferences.edit().putString(
                Constants.RESPECK_MAC_ADDRESS_PREF,
                qrCode.text.toString()
            ).apply()
            sharedPreferences.edit().putInt(Constants.RESPECK_VERSION, 6).apply()

            Log.i("service", "Starting BLT service")
            val simpleIntent = Intent(ctx, BluetoothService::class.java)
            ctx.startService(simpleIntent)
        }

        disconnectButton.setOnClickListener {
            Log.i("service", "Tearing down BLT service")
            val simpleIntent = Intent(ctx, BluetoothService::class.java)
            ctx.stopService(simpleIntent)
        }

        // first read shared preferences to see if there was a respeck there already
        sharedPreferences = ctx.getSharedPreferences(Constants.PREFERENCES_FILE, Context.MODE_PRIVATE)
        if (sharedPreferences.contains(Constants.RESPECK_MAC_ADDRESS_PREF)) {
            Log.i("sharedpref", "Already saw a respeckID")
            respeckCode.setText(
                sharedPreferences.getString(Constants.RESPECK_MAC_ADDRESS_PREF,"")
            )
        } else {
            Log.i("sharedpref", "No respeck seen before")
            connectButton.isEnabled = false
            connectButton.isClickable = false
        }

        qrCode.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, start: Int, before: Int, count: Int) {
                if (cs.toString().trim().length != 17) {
                    connectButton.isEnabled = false
                    connectButton.isClickable = false
                } else {
                    connectButton.isEnabled = true
                    connectButton.isClickable = true
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
        })

        qrCode.filters = arrayOf<InputFilter>(AllCaps())

        // necessary workaround for weird errors
        // https://github.com/Polidea/RxAndroidBle/wiki/FAQ:-UndeliverableException
        RxJavaPlugins.setErrorHandler { throwable ->
            if (throwable is UndeliverableException && throwable.cause is BleException) {
                return@setErrorHandler // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable)
        }

        rxBleClient = RxBleClient.create(ctx)

        // register a broadcast receiver for the respeck status
        filter.addAction(Constants.ACTION_RESPECK_CONNECTED)
        filter.addAction(Constants.ACTION_RESPECK_DISCONNECTED)

        setupRespeckStatus()

        respeckStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when(intent.action) {
                    Constants.ACTION_RESPECK_CONNECTED -> respeckStatus.text =
                        "Respeck status: Connected"
                    Constants.ACTION_RESPECK_DISCONNECTED -> respeckStatus.text =
                        "Respeck status: Disconnected"
                    else -> respeckStatus.text = "Error"
                }
            }
        }

        ctx.registerReceiver(respeckStatusReceiver, filter)

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK) {
            var scanResult = data?.extras?.getString("ScanResult")

            if(scanResult != null) {
                Log.i("ble", "Scan result=" + scanResult)

                if(scanResult.contains(":")) {
                    // this is a respeck V6 and we should store its MAC address
                    respeckCode.setText(scanResult)
                    sharedPreferences.edit().putString(
                        Constants.RESPECK_MAC_ADDRESS_PREF,
                        scanResult.toString()
                    ).apply()
                    sharedPreferences.edit().putInt(Constants.RESPECK_VERSION, 6).apply()
                }
                if (!scanResult.contains(":") && !scanResult.contains("-")) {
                    val sb = StringBuilder(scanResult)
                    if (scanResult.length == 20)
                        sb.insert(4, "-")
                    else if (scanResult.length == 16)
                        sb.insert(0, "0105-")
                    scanResult = sb.toString()

                    Log.i("Debug", "Scan result = " + scanResult)
                    respeckCode.setText(scanResult)
                    sharedPreferences.edit().putString(
                        Constants.RESPECK_MAC_ADDRESS_PREF,
                        scanResult
                    ).apply()
                    sharedPreferences.edit().putInt(Constants.RESPECK_VERSION, 5).apply()
                }
                connectButton.isEnabled = true
                connectButton.isClickable = true
            } else {
                respeckCode.setText("No respeck found :(")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ctx.unregisterReceiver(respeckStatusReceiver)
    }

    private fun setupRespeckStatus() {
        val isServiceRunning = Utils.isServiceRunning(BluetoothService::class.java, ctx)
        Log.i("debug", "isServiceRunning = " + isServiceRunning)

        // check sharedPreferences for an existing Respeck id
        val sharedPreferences = ctx.getSharedPreferences(
            Constants.PREFERENCES_FILE,
            Context.MODE_PRIVATE
        )
        if (sharedPreferences.contains(Constants.RESPECK_MAC_ADDRESS_PREF)) {
            Log.i(
                "sharedpref",
                "Already saw a respeckID, starting service and attempting to reconnect"
            )
            respeckStatus.text = "Respeck status: Connecting..."

            // launch service to reconnect
            // start the bluetooth service if it's not already running
            if(!isServiceRunning) {
                Log.i("service", "Starting BLT service")
                val simpleIntent = Intent(ctx, BluetoothService::class.java)
                ctx.startService(simpleIntent)
            }
        }
        else {
            Log.i("sharedpref", "No Respeck seen before, must pair first")
            respeckStatus.text = "Respeck status: Unpaired"
        }
    }
}
