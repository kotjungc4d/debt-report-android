package com.fintech.debtreport.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 100% FinTech Dark Slate & Sage Green Color Palette (Zero Orange / Zero Cyberpunk)
object FinTechColors {
    val PitchBlack = Color(0xFF000000)
    val SlateCard = Color(0xFF0F1216)
    val SlateCardElevated = Color(0xFF15191E)
    val SlateBorder = Color(0xFF232931)
    val SageGreen = Color(0xFF70B286)
    val SageLight = Color(0xFF88D49E)
    val SageDim = Color(0xFF1E3326)
    val CrimsonExpense = Color(0xFFD9534F)
    val CrimsonDim = Color(0xFF2B1416)
    val TextPrimary = Color(0xFFF3F5F8)
    val TextMuted = Color(0xFF8A94A6)
}

// Luxury 420ms Cubic-Bezier Easing for Page Transitions
val LuxurySlideEasing = CubicBezierEasing(0.22f, 1.0f, 0.36f, 1.0f)

/**
 * FinTechInputField
 * กล่องโค้งมน 12dp พื้นหลังเทาสเลทเข้ม มีไอคอนนำหน้าและสัญลักษณ์ ฿
 * เส้นขอบเรืองแสงสีเขียวเซจเมื่อแตะพิมพ์ (ไม่ใช้ OutlinedTextField เดิม)
 */
@Composable
fun FinTechInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: String,
    showCurrencySymbol: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) FinTechColors.SageGreen else FinTechColors.SlateBorder,
        animationSpec = tween(durationMillis = 180),
        label = "FinTechInputBorder"
    )

    val containerBg by animateColorAsState(
        targetValue = if (isFocused) FinTechColors.SlateCardElevated else FinTechColors.SlateCard,
        animationSpec = tween(durationMillis = 180),
        label = "FinTechInputBg"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(containerBg)
            .border(
                width = if (isFocused) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leadingIcon,
            fontSize = 16.sp,
            modifier = Modifier.padding(end = 10.dp)
        )

        if (showCurrencySymbol) {
            Text(
                text = "฿",
                color = if (isFocused) FinTechColors.SageLight else FinTechColors.TextMuted,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 6.dp)
            )
        }

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = FinTechColors.TextMuted.copy(alpha = 0.65f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                interactionSource = interactionSource,
                keyboardOptions = keyboardOptions,
                cursorBrush = SolidColor(FinTechColors.SageLight),
                textStyle = TextStyle(
                    color = FinTechColors.TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

enum class DebtReportTab(val title: String, val icon: ImageVector) {
    DEBT("หนี้สิน", Icons.AutoMirrored.Filled.ReceiptLong),
    CASHFLOW("รายรับ-จ่าย", Icons.Default.CreditCard),
    HEALTH("สุขภาพเงิน", Icons.Default.AutoAwesome),
    HISTORY("ประวัติ", Icons.Default.History)
}

/**
 * Floating Bottom Navigation Dock (ลอยเหนือขอบล่างพร้อม Frosted Glass & Active Sage Pill)
 */
@Composable
fun FinTechBottomDock(
    selectedTab: DebtReportTab,
    onSelectTab: (DebtReportTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, FinTechColors.PitchBlack.copy(alpha = 0.85f))
                )
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(FinTechColors.SlateCard.copy(alpha = 0.92f))
                .border(width = 1.dp, color = FinTechColors.SlateBorder, shape = RoundedCornerShape(22.dp))
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DebtReportTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) FinTechColors.SageDim else Color.Transparent
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) FinTechColors.SageGreen.copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectTab(tab) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = if (isSelected) FinTechColors.SageLight else FinTechColors.TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        color = if (isSelected) FinTechColors.SageLight else FinTechColors.TextMuted
                    )
                }
            }
        }
    }
}