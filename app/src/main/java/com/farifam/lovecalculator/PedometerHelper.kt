package com.farifam.lovecalculator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.samsung.android.sdk.SsdkUnsupportedException
import com.samsung.android.sdk.motion.Smotion
import com.samsung.android.sdk.motion.SmotionPedometer
import java.util.*

/**
 * Created by sabithuraira on 9/12/17.
 */

class PedometerHelper(private val context: Context) {

    private var mMode = MODE_PEDOMETER_REALTIME

    private var mMotion: Smotion? = null
    private var mPedometer: SmotionPedometer? = null
    var isStarted = false
    private var isPedometerUpDownAvailable: Boolean = false

    internal var pedometerCallback: PedometerCallback? = null

    private val TAG = "PEDOMETER"

    private var mTimer: Timer? = null
    private val mInterval: Long = 10000
    internal var mInfo: SmotionPedometer.Info? = null

    @Throws(IllegalArgumentException::class, SsdkUnsupportedException::class)
    fun initialize() {
        mMotion = Smotion()

        mMotion!!.initialize(context)
        mPedometer = SmotionPedometer(Looper.getMainLooper(), mMotion!!)

        isPedometerUpDownAvailable = mMotion!!.isFeatureEnabled(Smotion.TYPE_PEDOMETER_WITH_UPDOWN_STEP)
    }

    fun setPedometerCallback(pedometerCallback: PedometerCallback) {
        this.pedometerCallback = pedometerCallback
    }

    fun setModePedometer(mode: Int) {

        this.mMode = mode

    }

    fun start() {
        if (!isStarted) {
            isStarted = true
            mPedometer!!.start(changeListener)
            //            mPedometer.updateInfo();

            if (mMode == MODE_PEDOMETER_PERIODIC) {
                startTimer()
            }

            pedometerCallback?.motionStarted()
        }
    }

    fun stop() {
        if (isStarted == true) {
            isStarted = false
            mPedometer!!.stop()
            if (mMode == MODE_PEDOMETER_PERIODIC) {
                stopTimer()
            }

            pedometerCallback?.motionStopped()
        }
    }

    private fun startTimer() {
        if (mTimer == null) {
            mTimer = Timer()
            mTimer!!.schedule(MyTimer(), 0, mInterval)
        }
    }

    private fun stopTimer() {
        if (mTimer != null) {
            mTimer!!.cancel()
            mTimer = null
        }
    }

    internal val changeListener: SmotionPedometer.ChangeListener = SmotionPedometer.ChangeListener { info ->
        // TODO Auto-generated method stub
        if (mMode == MODE_PEDOMETER_REALTIME) {

            pedometerCallback?.updateInfo(info)
        }
    }


    internal inner class MyTimer : TimerTask() {

        override fun run() {
            // TODO Auto-generated method stub
            mInfo = mPedometer!!.info

            handler.sendEmptyMessage(0)
        }
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            // TODO Auto-generated method stub
            if (mInfo != null) {
                pedometerCallback?.updateInfo(mInfo!!)
            }
        }
    }

    companion object {

        internal val MODE_PEDOMETER_REALTIME = 0
        internal val MODE_PEDOMETER_PERIODIC = 1
    }

}

interface PedometerCallback {
    fun motionStarted()
    fun motionStopped()
    fun updateInfo(info: SmotionPedometer.Info)

}