package com.fintech.debtreport

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.util.lerp
import com.fintech.debtreport.audio.UiSoundEffects
import com.fintech.debtreport.ui.components.DebtReportTab
import com.fintech.debtreport.ui.components.FinTechBottomDock
import com.fintech.debtreport.ui.components.FinTechColors
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DebtReportAppScreen()
        }
    }
}

@Composable
fun DebtReportAppScreen() {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { DebtReportTab.entries.size }
    )
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    // ไม่มี Floating Header ด้านบน — ใช้ Floating Dock ลอยด้านล่าง + ปัดนิ้วเปลี่ยนหน้าได้
    Scaffold(
        containerColor = FinTechColors.PitchBlack,
        bottomBar = {
            FinTechBottomDock(
                selectedTab = DebtReportTab.entries[pagerState.currentPage],
                onSelectTab = { targetTab ->
                    UiSoundEffects.playTabSwitch(view)
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = targetTab.ordinal,
                            animationSpec = spring(
                                dampingRatio = 0.84f,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        // เปิด Gesture ปัดนิ้วสลับหน้า (userScrollEnabled = true) พร้อมแอนิเมชัน Spring Physics
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true,
            beyondViewportPageCount = 3,
            modifier = Modifier
                .fillMaxSize()
                .background(FinTechColors.PitchBlack)
                .padding(innerPadding)
        ) { pageIndex ->
            val pageOffset = (
                (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
            ).absoluteValue

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        alpha = lerp(start = 0.55f, stop = 1f, fraction = fraction)
                        scaleX = lerp(start = 0.98f, stop = 1f, fraction = fraction)
                        scaleY = lerp(start = 0.98f, stop = 1f, fraction = fraction)
                    }
            ) {
                // เนื้อหาของแต่ละแท็บ (Tab 1..4)
            }
        }
    }
}

/**
 * Pop-up Window ที่เลื่อนลงมาจากด้านบนด้วย Spring-Based Physics
 */
@Composable
fun TopDownSpringPopup(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = spring(stiffness = Spring.StiffnessMedium)
        ) + fadeOut()
    ) {
        content()
    }
}