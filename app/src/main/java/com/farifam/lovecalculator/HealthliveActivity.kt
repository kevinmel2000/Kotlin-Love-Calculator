package com.farifam.lovecalculator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.motion.SmotionPedometer

import kotlinx.android.synthetic.main.activity_healthlive.*

class HealthliveActivity : AppCompatActivity(), PedometerCallback  {

    internal var pedometerHelper: PedometerHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_healthlive)
        pedometerHelper = PedometerHelper(this)

        try {
            pedometerHelper!!.initialize()
            pedometerHelper!!.setPedometerCallback(this)

        } catch (e: IllegalArgumentException) {
            showErrorDialog("Something went wrong", e.message.toString())
            return
        } catch (e: SsdkUnsupportedException) {
            showErrorDialog("SDK Not Supported", e.message.toString())
            return
        }

        pedometerHelper?.setModePedometer(PedometerHelper.MODE_PEDOMETER_REALTIME)

        res.visibility = View.GONE
//        res = findViewById(R.id.res) as LinearLayout
//        res.visibility = View.GONE
//        res_title = findViewById(R.id.res_title) as TextView
//        res_result = findViewById(R.id.res_result) as TextView
//        res_residu = findViewById(R.id.res_residu) as TextView
//        btnStart = findViewById(R.id.btn_start) as Button

        btn_start.setOnClickListener {
            if (pedometerHelper?.isStarted === false) {
                pedometerHelper?.start()
            } else {
                pedometerHelper?.stop()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.home) {
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop pedometer
        pedometerHelper?.stop()
    }

    private fun getStatus(status: Int): String {
        var str: String? = null
        when (status) {
            SmotionPedometer.Info.STATUS_WALK_UP -> str = "Walk Up"
            SmotionPedometer.Info.STATUS_WALK_DOWN -> str = "Walk Down"
            SmotionPedometer.Info.STATUS_WALK_FLAT -> str = "Walk"
            SmotionPedometer.Info.STATUS_RUN_DOWN -> str = "Run Down"
            SmotionPedometer.Info.STATUS_RUN_UP -> str = "Run Up"
            SmotionPedometer.Info.STATUS_RUN_FLAT -> str = "Run"
            SmotionPedometer.Info.STATUS_STOP -> str = "Stop"
            SmotionPedometer.Info.STATUS_UNKNOWN -> str = "Unknown"
            else -> {
            }
        }

        return str.toString()
    }

    override fun motionStarted() {
        res.visibility = View.VISIBLE
        btn_start.setText(R.string.stop)
    }

    override fun motionStopped() {
        res.visibility = View.GONE
        btn_start.setText(R.string.start)
    }

    override fun updateInfo(info: SmotionPedometer.Info) {
        val pedometerInfo = info
        //        System.out.println("HelloMotion PedometerHelper");
        //        double calorie = info.getCalorie();
        //        double distance = info.getDistance();
        //        double speed = info.getSpeed();
        val count = info.getCount(SmotionPedometer.Info.COUNT_TOTAL)
        //        int status = info.getStatus();
        if (count >= 10000) {
            res_title.text = "Hooray !"
            res_residu.text = "Congratulations, you have been passed 10.000 step"
        } else {

            res_residu.text = "you need " + java.lang.Long.toString(10000 - count) + " step to aim 10.000 step!"
        }

        res_result.text = java.lang.Long.toString(count)
    }

    internal fun showErrorDialog(title: String, message: String) {
        AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok) { dialog, which -> }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
    }
}