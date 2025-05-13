package com.alireza.taphero

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GameScreen(modifier: Modifier = Modifier, gameVM: GameVM = viewModel()) {
    if (!gameVM.isGameRunning)
        GameStartScreen(
            modifier = modifier, onStartGame = { gameVM.startGame() }, topScore = gameVM.topScore
        )
    else
        GamePlayScreen(
            onGameOver = {
                gameVM.quitGame()
            },
            modifier = modifier,
            gameVM = gameVM
        )
}

@Composable
fun GameStartScreen(
    modifier: Modifier = Modifier, onStartGame: () -> Unit = {}, topScore: Int = 0
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            "Tap Hero",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(red = 242, green = 140, blue = 40)
        )
        Button(onClick = onStartGame) {
            Text("Start Game", fontSize = 24.sp)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Top Score: $topScore", fontSize = 24.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = "Info")
                Text(
                    text = "Tap the screen as many as you can\nwhen background color is changed in 30 seconds !",
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp,
                )
            }

        }
    }
}

@Composable
fun GamePlayScreen(
    modifier: Modifier = Modifier,
    onGameOver: () -> Unit = { },
    gameVM: GameVM
) {
    val background = gameVM.background
    val score = gameVM.score
    val timeLeft = gameVM.timeLeft
    val speedLevel = gameVM.speedLevel
    val intensity = 0.7f + (speedLevel * 0.03f).coerceAtMost(0.25f)
    val intenseBackground = background.copy(
        red = (background.red * intensity).coerceIn(0f, 1f),
        green = (background.green * intensity).coerceIn(0f, 1f),
        blue = (background.blue * intensity).coerceIn(0f, 1f)
    )
    val textColor = if (intenseBackground.luminance() < 0.5f) Color.White else Color.Black
    val maxSpeedLevel = 10
    val barFill = (speedLevel.coerceAtMost(maxSpeedLevel)).toFloat() / maxSpeedLevel
    val barBgColor = textColor.copy(alpha = 0.2f)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(intenseBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                gameVM.tap()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = "Time Left: $timeLeft",
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = textColor
        )
        Text("Score: $score", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = textColor)
        Column {
            Text(
                "Speed",
                fontSize = 14.sp,
                color = textColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(120.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(barBgColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width((120 * barFill).dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(textColor)
                )
            }
        }
        Button(
            onClick = {
                onGameOver()
            },
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Quit Game", fontSize = 16.sp, color = textColor)
        }
    }
}

@Preview
@Composable
fun GameStartScreenPreview() {
    Surface {
        GameStartScreen()
    }
}

@Preview
@Composable
fun GamePlayScreenPreview() {
    Surface {
        GamePlayScreen(gameVM = viewModel())
    }
}