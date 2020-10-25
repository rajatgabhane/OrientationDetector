package com.tesseract.android.orientationdetector

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tesseract.android.sdk.connector.OrientationServiceConnector
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var dataChangeSubscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        OrientationServiceConnector.connect(this)
        dataChangeSubscription = Observable.interval(DATA_REFRESH_INTERVAL, TimeUnit.MILLISECONDS)
            .onBackpressureLatest()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                OrientationServiceConnector.getOrientationData()?.let {
                    azimuth_value.text = it.azimuth.toString()
                    pitch_value.text = it.pitch.toString()
                    roll_value.text = it.roll.toString()
                }
            }) {
                Log.e(TAG, "error occurred: $it")
            }
    }

    override fun onStop() {
        OrientationServiceConnector.disconnect(this)
        super.onStop()
    }

    companion object {
        private const val DATA_REFRESH_INTERVAL = 8L
        private const val TAG = "MainActivity"
    }
}
