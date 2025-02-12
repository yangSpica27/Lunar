package me.spica.lunar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.nlf.calendar.Lunar
import kotlinx.coroutines.delay
import me.spica.lunar.ui.theme.LunarTheme
import me.spica.lunar.ui.theme.Text333
import me.spica.lunar.ui.theme.Text666
import java.util.Calendar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        setContent {
            LunarTheme {
                Main()
            }
        }
    }
}

/**
 * 主页容器
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Main() {
    // 阴历信息
    val lunar = remember { mutableStateOf(Lunar.fromDate(Calendar.getInstance().time)) }

    // 是否展示额外信息面板
    val showExtraPanel = remember { mutableStateOf(false) }

    // 一分钟左右更新面板信息
    LaunchedEffect(lunar.value) {
        delay(1000 * 60)
        lunar.value = Lunar.fromDate(Calendar.getInstance().time)
    }

    // 上层模糊
    val backgroundBlurRadius = animateDpAsState(
        targetValue = if (showExtraPanel.value) 18.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 450
        )
    )

    // 左边文字（月）的测量器
    val monthTextMeasurer = rememberTextMeasurer()

    // 右边文字（日）的测量器
    val dayTextMeasurer = rememberTextMeasurer()

    // 返回键关闭面板
    BackHandler(
        showExtraPanel.value
    ) {
        showExtraPanel.value = false
    }

    // 面板透明度
    val panelAlpha = animateFloatAsState(
        if (showExtraPanel.value) 1f else 0f,
        animationSpec = tween(350, 150)
    )

    Background {
        Scaffold(
            modifier = Modifier.clickable {
                showExtraPanel.value = true
            },
            containerColor = Color.Transparent,
            contentColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .matchParentSize()
                        .blur(
                            backgroundBlurRadius.value,
                            edgeTreatment = BlurredEdgeTreatment.Unbounded
                        )
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 上层布局
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    lunar.value.hou, style = TextStyle(
                                        fontFamily = FontFamily(Font(R.font.lxgw)),
                                        fontSize = 40.sp,
                                        color = Color.White
                                    )
                                )
                                Spacer(
                                    modifier = Modifier
                                        .height(8.dp)
                                )
                                Text(
                                    "${lunar.value.wuHou} ${lunar.value.monthShengXiao}月${lunar.value.timeShengXiao}时",
                                    fontSize = 24.sp,
                                    color = Color.White,
                                    fontFamily = FontFamily(Font(R.font.lxgw))
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))

                            val rightTopTextStyle = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.W500,
                                color = Color.White
                            )

                            androidx.compose.foundation.Canvas(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(80.dp)
                                    .drawWithCache {

                                        val month = (Calendar.getInstance()
                                            .get(Calendar.DAY_OF_MONTH)).toString()
                                        val measureRes =
                                            dayTextMeasurer.measure(
                                                month,
                                                style = rightTopTextStyle
                                            )
                                        onDrawBehind {
                                            drawText(
                                                measureRes,
                                                topLeft = Offset(
                                                    size.width - measureRes.size.width - 2.dp.toPx(),
                                                    size.height - measureRes.size.height
                                                ),
                                            )
                                        }
                                    }
                            ) {

                                drawText(
                                    monthTextMeasurer,
                                    Calendar.getInstance().get(Calendar.MONTH).toString(),
                                    style = rightTopTextStyle,
                                    topLeft = Offset(2.dp.toPx(), 0f),
                                )

                                drawLine(
                                    color = Color.White,
                                    start = Offset(x = 0f, y = size.height),
                                    end = Offset(x = size.width, y = 0f),
                                    strokeWidth = 2.dp.toPx()
                                )

                            }
                        }

                    }

                    // 中层布局
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            FlowColumn(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                // 获取当前的节日
                                val festivals = lunar.value.festivals
                                //有节日
                                if (festivals.isNotEmpty()) {
                                    (lunar.value.festivals).toList().joinToString(",")
                                        .map {
                                            Text(
                                                "$it",
                                                fontSize = 40.sp,
                                                fontWeight = FontWeight.W500,
                                                lineHeight = 3.sp
                                            )
                                        }
                                } else {
                                    ("今日无事发生。")
                                        .map {
                                            Text(
                                                "$it",
                                                fontSize = 40.sp,
                                                fontWeight = FontWeight.W500,
                                                lineHeight = 3.sp
                                            )
                                        }
                                }


                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "${lunar.value.yearInChinese}年 " +
                                        "${lunar.value.monthInChinese}月" +
                                        lunar.value.dayInChinese,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Justify
                            )
                        }
                    }

                    // 下层布局
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    ) {

                        // 暂时没想好放什么

                    }

                }
                if (panelAlpha.value != 0f) {
                    ExtraPanel(
                        lunar = lunar.value,
                        modifier = Modifier
                            .clickable {
                                showExtraPanel.value = false
                            }
                            .fillMaxSize()
                            .alpha(panelAlpha.value)
                    )
                }
            }
        }
    }
}

// 更多信息面板
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtraPanel(modifier: Modifier = Modifier, lunar: Lunar) {
    val show = remember { mutableStateOf(false) }

    SideEffect {
        show.value = true
    }

    Box(
        modifier = modifier.padding(
            vertical = 40.dp,
            horizontal = 24.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                "今日宜",
                style = TextStyle(fontSize = 20.sp, color = Text333, letterSpacing = 2.sp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lunar.dayYi.mapIndexed { index, str ->

                    AnimatedVisibility(
                        show.value,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 400 + index * 50,
                                delayMillis = index * 150
                            )
                        ) + slideIn(
                            animationSpec = tween(
                                durationMillis = 400 + index * 50,
                                delayMillis = index * 150
                            )
                        ) { _ ->
                            IntOffset(0, 40)
                        }
                    ) {
                        Text(str, style = TextStyle(fontSize = 18.sp, color = Text666))
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            Text(
                "今日忌",
                style = TextStyle(fontSize = 20.sp, color = Text333, letterSpacing = 2.sp)
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                lunar.dayJi.mapIndexed { index, str ->
                    AnimatedVisibility(
                        show.value,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 400 + index * 50,
                                delayMillis = index * 150
                            )
                        ) + slideIn(
                            animationSpec = tween(
                                durationMillis = 400 + index * 50,
                                delayMillis = index * 150
                            )
                        ) { size ->
                            IntOffset(0, size.height)
                        }
                    ) {
                        Text(str, style = TextStyle(fontSize = 18.sp, color = Text666))
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                lunar.solar.toFullString().toList().mapIndexed { index, str ->
                    AnimatedVisibility(
                        show.value,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 400 + index * 50,
                                delayMillis = index * 150
                            )
                        ) + slideIn(
                            animationSpec = tween(
                                durationMillis = 400 + index * 50,
                                delayMillis = index * 150
                            )
                        ) { size ->
                            IntOffset(0, size.height)
                        }
                    ) {
                        Text(str.toString(), style = TextStyle(fontSize = 18.sp, color = Text333))
                    }
                }
            }
        }

    }
}


/**
 * 动态背景组件
 */
@Composable
fun Background(
    modifier: Modifier = Modifier,
    duration: Int = 5000,
    content: @Composable BoxScope.() -> Unit,
) {
    // 上层颜色
    val topColors = listOf(Color(0xff36cfc9), Color(0xff4096ff), Color(0xffffa940))
    // 中层颜色
    val bottomCenterColors = listOf(Color(0xFFB3E5FC), Color(0xFFBBDEFB), Color(0xFFFFE0B2))
    // 下层颜色
    val bottomColors = listOf(Color(0xFFC8E6C9), Color(0xFFB2EBF2), Color(0xFFFFECB3))
    // 用于循环的下标
    val indexState = remember { mutableIntStateOf(0) }
    // 当前的上班层颜色状态
    val topColorState = animateColorAsState(
        topColors[indexState.intValue], animationSpec = tween(
            durationMillis = duration, easing = LinearEasing
        )
    )
    // 中层的颜色状态
    val centerColorState = animateColorAsState(
        bottomCenterColors[indexState.intValue], animationSpec = tween(
            durationMillis = duration, easing = LinearEasing
        )
    )
    // 下层的颜色状态
    val bottomCenterColor = animateColorAsState(
        bottomColors[indexState.intValue], animationSpec = tween(
            durationMillis = duration, easing = LinearEasing
        )
    )

    // 协程中更新下标用于轮训
    LaunchedEffect(indexState.intValue) {
        delay(duration * 1L)
        indexState.intValue = if (indexState.intValue == topColors.size - 1) {
            0
        } else {
            indexState.intValue + 1
        }
    }

    Box(
        modifier = modifier
            .background(
                // 渐变色的绘制视线
                brush = Brush.linearGradient(
                    colors = listOf(
                        topColorState.value,
                        topColorState.value,
                        centerColorState.value,
                        bottomCenterColor.value
                    )
                )
            )
            .fillMaxSize(), content = content
    )

}

