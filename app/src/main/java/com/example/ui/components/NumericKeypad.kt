package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricYellow
import com.example.ui.theme.MidnightBlueCard
import com.example.ui.theme.MidnightBlueDark
import com.example.ui.theme.MidnightBlueSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextMuted

@Composable
fun NumericKeypad(
    onDigitClick: (String) -> Unit,
    onDecimalClick: () -> Unit,
    onBackspaceClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: 1, 2, 3, C
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "1", onClick = { onDigitClick("1") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "2", onClick = { onDigitClick("2") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "3", onClick = { onDigitClick("3") }, modifier = Modifier.weight(1f))
            KeypadButton(
                text = "C",
                onClick = onClearClick,
                isSpecial = true,
                specialTextColor = ElectricYellow,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: 4, 5, 6, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "4", onClick = { onDigitClick("4") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "5", onClick = { onDigitClick("5") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "6", onClick = { onDigitClick("6") }, modifier = Modifier.weight(1f))
            KeypadIconButton(
                onClick = onBackspaceClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3 & 4 combined: 7, 8, 9 and ., 0
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "7", onClick = { onDigitClick("7") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "8", onClick = { onDigitClick("8") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "9", onClick = { onDigitClick("9") }, modifier = Modifier.weight(1f))
            KeypadButton(text = ".", onClick = onDecimalClick, modifier = Modifier.weight(1f))
        }

        // Row 4: 0 spanning middle, and quick quick shortcuts
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            KeypadButton(text = "00", onClick = { onDigitClick("00") }, modifier = Modifier.weight(1f))
            KeypadButton(text = "0", onClick = { onDigitClick("0") }, modifier = Modifier.weight(2f))
            KeypadButton(
                text = "+5€",
                onClick = { onDigitClick("5") },
                isSpecial = true,
                specialTextColor = PureWhite,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSpecial: Boolean = false,
    specialTextColor: androidx.compose.ui.graphics.Color = PureWhite
) {
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSpecial) MidnightBlueSurface else MidnightBlueCard)
            .clickable(onClick = onClick)
            .testTag("keypad_btn_$text"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSpecial) specialTextColor else PureWhite,
            fontSize = if (text.length > 2) 16.sp else 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun KeypadIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MidnightBlueSurface)
            .clickable(onClick = onClick)
            .testTag("keypad_btn_backspace"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Backspace,
            contentDescription = "Effacer un chiffre",
            tint = PureWhite,
            modifier = Modifier.size(22.dp)
        )
    }
}
