package me.spica.lunar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nlf.calendar.Lunar
import kotlinx.coroutines.delay
import me.spica.lunar.ui.theme.LunarTheme
import java.util.Calendar
import kotlin.time.Duration

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
    val lunar = remember { mutableStateOf(Lunar.fromDate(Calendar.getInstance().time)) }


    LaunchedEffect(lunar.value) {
        delay(1000 * 60)
        lunar.value = Lunar.fromDate(Calendar.getInstance().time)
    }

    val monthTextMeasurer = rememberTextMeasurer()

    val dayTextMeasurer = rememberTextMeasurer()

    Background {
        Scaffold(
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
                                        fontSize = 48.sp,
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
                                            .get(Calendar.DAY_OF_MONTH) + 1).toString()
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
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                ("玻璃晴朗，橘子辉煌。").toList()
                                    .map {
                                        Text("$it", fontSize = 40.sp, fontWeight = FontWeight.W500)
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

