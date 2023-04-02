package android.portfolio.stopwatch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.portfolio.stopwatch.databinding.ActivityMainBinding
import android.portfolio.stopwatch.databinding.DialogCountdownSettingBinding
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.min

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var countDownSecond = 10
    private var currentCountdownDeciSecond = countDownSecond * 10
    private var currentDeciSecond = 0
    private var timer: Timer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.countDownTextView.setOnClickListener {
            showCountdownSettingDialog()
        }

        binding.startButton.setOnClickListener {
            start()
            binding.startButton.isVisible = false
            binding.pauseButton.isVisible = true
            binding.stopButton.isVisible = false
            binding.checkButton.isVisible = true
        }

        binding.pauseButton.setOnClickListener {
            pause()
            binding.startButton.isVisible = true
            binding.pauseButton.isVisible = false
            binding.stopButton.isVisible = true
            binding.checkButton.isVisible = false
        }

        binding.stopButton.setOnClickListener {
            showDialog()
        }

        binding.checkButton.setOnClickListener {
            check()
        }
        initCountdownViews()
    }

    private fun initCountdownViews() {
        binding.countDownTextView.text = String.format("%02d", countDownSecond)
        binding.countDownProgressBar.progress = 100
    }

    private fun start() {
        timer = timer(initialDelay = 0, period = 100) {
            if (currentCountdownDeciSecond == 0) {
                currentDeciSecond += 1
                val minutes = (currentDeciSecond / 10) / 60
                val seconds = (currentDeciSecond / 10) % 60
                val deciSeconds = currentDeciSecond % 10
                runOnUiThread {
                    binding.timeTextView.text = String.format("%02d:%02d", minutes, seconds)
                    binding.tickTextView.text = deciSeconds.toString()

                    binding.coundDownGroup.isVisible = false
                }
            } else {
                currentCountdownDeciSecond -= 1
                val curSecond = currentCountdownDeciSecond / 10

                binding.root.post {
                    binding.countDownTextView.text = String.format("%02d", curSecond)
                    var progress = (currentCountdownDeciSecond / (countDownSecond * 10f)) * 100
                    binding.countDownProgressBar.progress = progress.toInt()
                }
            }
        }
    }

    private fun pause() {
        timer?.cancel()
        timer = null
    }

    private fun stop() {
        binding.startButton.isVisible = true
        binding.pauseButton.isVisible = false
        binding.stopButton.isVisible = true
        binding.checkButton.isVisible = false
        currentDeciSecond = 0
        binding.timeTextView.text = "00:00"
        binding.tickTextView.text = "0"

        binding.coundDownGroup.isVisible = true
        initCountdownViews()
    }

    private fun check() {

    }

    private fun showCountdownSettingDialog() {
        AlertDialog.Builder(this).apply {
            val dialogBinding = DialogCountdownSettingBinding.inflate(layoutInflater)
            with(dialogBinding.countDownSecondPicker) {
                maxValue = 30
                minValue = 0
                value = countDownSecond
            }
            setTitle("카운트다운 설정")
            setView(dialogBinding.root)
            setPositiveButton("확인") { _, _ ->
                countDownSecond = dialogBinding.countDownSecondPicker.value
                currentCountdownDeciSecond = countDownSecond * 10
                binding.countDownTextView.text = String.format("%02d", countDownSecond)
            }
        }.show()
    }

    private fun showDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("종료하시겠습니까?")
            setPositiveButton("네") { _, _ ->
                stop()
            }
            setNegativeButton("아니오", null)
        }.show()
    }
}