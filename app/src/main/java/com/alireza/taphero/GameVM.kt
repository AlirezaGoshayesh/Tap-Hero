package com.alireza.taphero

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameVM: ViewModel() {
    var isGameRunning by mutableStateOf(false)
        private set
    var topScore by mutableIntStateOf(0)
        private set
    var background by mutableStateOf(Color.Red)
        private set
    var isColorClicked by mutableStateOf(false)
        private set
    var score by mutableIntStateOf(0)
        private set
    var timeLeft by mutableIntStateOf(30)
        private set

    private var timerJob: Job? = null
    private var colorJob: Job? = null
    private val vmScope = viewModelScope

    val speedLevel: Int
        get() = (score / 5) + 1

    fun startGame() {
        isGameRunning = true
        score = 0
        timeLeft = 30
        background = Color.Red
        isColorClicked = false
        startTimer()
        startColorChange()
    }

    fun quitGame() {
        stopJobs()
        isGameRunning = false
        if (topScore < score) topScore = score
    }

    fun tap() {
        if (!isColorClicked) {
            score++
            isColorClicked = true
        } else {
            score--
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = vmScope.launch {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            if (timeLeft == 0) {
                quitGame()
            }
        }
    }

    private fun startColorChange() {
        colorJob?.cancel()
        colorJob = vmScope.launch {
            while (isGameRunning && timeLeft > 0) {
                val minDelay = maxOf(200, 700 - score * 40)
                val maxDelay = maxOf(350, 1200 - score * 60)
                val delayMillis = Random.nextLong(minDelay.toLong(), maxDelay.toLong())
                delay(delayMillis)
                isColorClicked = false
                background = randomColor()
            }
        }
    }

    private fun stopJobs() {
        timerJob?.cancel()
        colorJob?.cancel()
    }

    private fun randomColor(): Color {
        return Color(
            red = Random.nextFloat(),
            green = Random.nextFloat(),
            blue = Random.nextFloat(),
            alpha = 1f
        )
    }

    override fun onCleared() {
        super.onCleared()
        stopJobs()
    }
}