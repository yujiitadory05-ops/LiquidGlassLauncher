package com.example.liquidglasslauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class LauncherApp(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LiquidLauncher(
                    appsProvider = { loadLaunchableApps() },
                    onLaunchApp = { app -> launchApp(app) }
                )
            }
        }
    }

    private fun loadLaunchableApps(): List<LauncherApp> {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .mapNotNull { info: ResolveInfo ->
                try {
                    LauncherApp(
                        label = info.loadLabel(packageManager).toString(),
                        packageName = info.activityInfo.packageName,
                        activityName = info.activityInfo.name,
                        icon = info.loadIcon(packageManager)
                    )
                } catch (_: Exception) {
                    null
                }
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase(Locale.getDefault()) }
    }

    private fun launchApp(app: LauncherApp) {
        val intent = packageManager.getLaunchIntentForPackage(app.packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}

@Composable
fun LiquidLauncher(
    appsProvider: () -> List<LauncherApp>,
    onLaunchApp: (LauncherApp) -> Unit
) {
    var apps by remember { mutableStateOf(emptyList<LauncherApp>()) }
    var showDrawer by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var usePurple by remember { mutableStateOf(false) }
    var clock by remember { mutableStateOf(currentTime()) }

    LaunchedEffect(Unit) {
        apps = appsProvider()
        while (true) {
            clock = currentTime()
            delay(1000L)
        }
    }

    val bgBrush = if (usePurple) {
        Brush.linearGradient(
            listOf(Color(0xFF241044), Color(0xFF492A78), Color(0xFF111A3A))
        )
    } else {
        Brush.linearGradient(
            listOf(Color(0xFF071B3B), Color(0xFF164A75), Color(0xFF27164D))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        // Soft color blooms behind the translucent panels.
        Box(
            Modifier
                .size(270.dp)
                .align(Alignment.TopStart)
                .padding(20.dp)
                .background(Color(0x553DCBFF), CircleShape)
        )
        Box(
            Modifier
                .size(250.dp)
                .align(Alignment.BottomEnd)
                .background(Color(0x558D58FF), CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "LIQUID",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                GlassCircleButton(
                    icon = { Icon(Icons.Default.Settings, null, tint = Color.White) },
                    onClick = { showSettings = !showSettings }
                )
            }

            Spacer(Modifier.weight(0.55f))

            Text(
                text = clock,
                color = Color.White,
                fontSize = 66.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = (-2).sp
            )
            Text(
                text = LocalDate.now().format(
                    DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())
                ),
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 16.sp
            )

            Spacer(Modifier.weight(0.65f))

            GlassPanel(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = Color.White.copy(alpha = 0.8f))
                    Spacer(Modifier.size(10.dp))
                    BasicTextField(
                        value = search,
                        onValueChange = {
                            search = it
                            showDrawer = true
                        },
                        singleLine = true,
                        cursorBrush = SolidColor(Color.White),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Color.White,
                            fontSize = 15.sp
                        ),
                        decorationBox = { inner ->
                            Box {
                                if (search.isEmpty()) {
                                    Text("Search apps", color = Color.White.copy(alpha = 0.65f))
                                }
                                inner()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // Favorite apps are chosen from the installed-app list.
            val favorites = apps.take(4)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                favorites.forEach { app ->
                    AppShortcut(app = app, onClick = { onLaunchApp(app) })
                }
                if (favorites.isEmpty()) {
                    Text("Your apps will appear here", color = Color.White.copy(alpha = 0.7f))
                }
            }

            Spacer(Modifier.weight(0.35f))

            GlassPanel(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 15.dp, horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DockButton("Apps", "▦") { showDrawer = !showDrawer }
                    DockButton("Search", "⌕") {
                        showDrawer = true
                    }
                    DockButton("Settings", "⚙") {
                        showSettings = !showSettings
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        AnimatedVisibility(
            visible = showDrawer,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AppDrawer(
                apps = apps.filter {
                    it.label.contains(search, ignoreCase = true)
                },
                onAppClick = {
                    showDrawer = false
                    search = ""
                    onLaunchApp(it)
                },
                onClose = {
                    showDrawer = false
                    search = ""
                }
            )
        }

        AnimatedVisibility(
            visible = showSettings,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            GlassPanel(modifier = Modifier.padding(24.dp)) {
                Column(Modifier.padding(22.dp)) {
                    Text(
                        "Launcher settings",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "Background theme",
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ThemeChoice("Ocean", !usePurple) { usePurple = false }
                        ThemeChoice("Violet", usePurple) { usePurple = true }
                    }
                    Spacer(Modifier.height(20.dp))
                    Text(
                        "Choose Apps in the dock to open your installed apps.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        "CLOSE",
                        color = Color(0xFFBCEBFF),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showSettings = false }
                            .padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.shadow(
            elevation = 18.dp,
            shape = RoundedCornerShape(28.dp),
            ambientColor = Color(0x5538CFFF),
            spotColor = Color(0x5538CFFF)
        ),
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = 0.13f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(
                listOf(
                    Color.White.copy(alpha = 0.65f),
                    Color.White.copy(alpha = 0.10f),
                    Color.White.copy(alpha = 0.35f)
                )
            )
        )
    ) {
        Box {
            Box(
                Modifier
                    .matchParentSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color.White.copy(alpha = 0.10f),
                                Color.Transparent,
                                Color(0x223DCCFF)
                            )
                        )
                    )
            )
            content()
        }
    }
}

@Composable
fun GlassCircleButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(44.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = 0.35f)
        )
    ) {
        Box(contentAlignment = Alignment.Center) { icon() }
    }
}

@Composable
fun AppShortcut(app: LauncherApp, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(6.dp)
    ) {
        GlassAppIcon(app.icon, Modifier.size(55.dp))
        Spacer(Modifier.height(7.dp))
        Text(
            app.label,
            color = Color.White,
            fontSize = 11.sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GlassAppIcon(drawable: Drawable, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(17.dp))
            .clip(RoundedCornerShape(17.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(7.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { context -> ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageDrawable(drawable)
            } },
            update = { it.setImageDrawable(drawable) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DockButton(label: String, symbol: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 3.dp)
    ) {
        Text(symbol, color = Color.White, fontSize = 25.sp)
        Text(label, color = Color.White.copy(alpha = 0.82f), fontSize = 11.sp)
    }
}

@Composable
fun ThemeChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(15.dp),
        color = if (selected) Color(0x665FCBFF) else Color.White.copy(alpha = 0.10f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Color.White.copy(alpha = if (selected) 0.7f else 0.25f)
        )
    ) {
        Text(
            label,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun AppDrawer(
    apps: List<LauncherApp>,
    onAppClick: (LauncherApp) -> Unit,
    onClose: () -> Unit
) {
    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .height(470.dp)
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "All Apps",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "CLOSE",
                    color = Color(0xFFBCEBFF),
                    modifier = Modifier.clickable { onClose() }.padding(8.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            if (apps.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No matching apps found", color = Color.White.copy(alpha = 0.75f))
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 76.dp),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(apps, key = { it.packageName }) { app ->
                        AppShortcut(app = app, onClick = { onAppClick(app) })
                    }
                }
            }
        }
    }
}

fun currentTime(): String =
    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
