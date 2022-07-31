package com.aayushi.tortoise

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.aayushi.tortoise.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var gameString: String = ""
    var counter: Int = 0

    // just for first time
    // value not taken 0 as then there can be no high score as per given condition
    private var highScore = 10L

    private val mInterval = 1000
    private var mHandler: Handler? = null
    private var timeInSeconds = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState != null) {
            highScore = savedInstanceState.getLong("highScore")
        }

        binding.highScore.text = getString(R.string.hgh_score,highScore)

        binding.start.setOnClickListener {
            binding.random.setText(randomCharacter())
            gameString = binding.random.text.toString()
            counter++
            startTimer()
        }

        binding.type.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                counter++
                if (s != binding.random.text)
                    timeInSeconds += 5
                if (counter <= 20) {
                    binding.random.setText(randomCharacter())
                    gameString = gameString.plus(binding.random.text)
                }
            }
        })

        binding.result.setOnClickListener {
            stopTimer()
            if (timeInSeconds < highScore)
                highScore = timeInSeconds

            binding.highScore.text = getString(R.string.hgh_score,highScore)

            val type = binding.type.text.toString()
            if (gameString.compareTo(type, true) == 0)
                binding.random.setText(getString(R.string.success))
            else
                binding.random.setText(getString(R.string.failure))

            timeInSeconds = 0
            gameString = ""
        }

        binding.reset.setOnClickListener {
            stopTimer()
            binding.type.setText("")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(
            "highScore",
            highScore
        )
    }

    fun randomCharacter() = (65..90).random().toChar().toString()

    private fun startTimer() {
        mHandler = Handler(Looper.getMainLooper())
        mStatusChecker.run()

    }

    private fun stopTimer() {
        mHandler?.removeCallbacks(mStatusChecker)
    }

    private var mStatusChecker: Runnable = object : Runnable {
        override fun run() {
            try {
                timeInSeconds += 1
                updateStopWatchView(timeInSeconds)
            } finally {
                mHandler!!.postDelayed(this, mInterval.toLong())
            }
        }
    }

    private fun updateStopWatchView(timeInSeconds: Long) {
        val formattedTime = getFormattedStopWatch((timeInSeconds * 1000))
        binding.timer.text = getString(R.string.timer,formattedTime)
    }

    private fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }
}