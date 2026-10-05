package com.example.zheheshima

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.zheheshima.data.ScanProgress
import com.example.zheheshima.data.SourceFolder
import com.example.zheheshima.data.WallpaperImage
import com.example.zheheshima.ui.ZheHeShiMaTheme
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZheHeShiMaTheme { ZheHeShiMaApp() } }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZheHeShiMaApp(viewModel: MainViewModel = viewModel()) {
    val folders by viewModel.folders.collectAsStateWithLifecycleCompat()
    val images by viewModel.images.collectAsStateWithLifecycleCompat()
    val markedImages by viewModel.markedImages.collectAsStateWithLifecycleCompat()
    val markedCount by viewModel.markedCount.collectAsStateWithLifecycleCompat()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycleCompat()
    val moveProgress by viewModel.moveProgress.collectAsStateWithLifecycleCompat()
    val previewMode by viewModel.previewMode.collectAsStateWithLifecycleCompat()
    val message by viewModel.message.collectAsStateWithLifecycleCompat()

    var selectedTab by remember { mutableIntStateOf(0) }
    var previewItems by remember { mutableStateOf<List<WallpaperImage>?>(null) }
    var previewIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    BackHandler(enabled = previewItems == null && selectedTab != 0) {
        selectedTab = 0
    }

    LaunchedEffect(message) {
        message?.let { notice ->
            snackbarHostState.showSnackbar(notice)
            viewModel.clearMessage()
        }
    }

    if (previewItems != null) {
        PreviewScreen(
            items = previewItems.orEmpty(),
            initialIndex = previewIndex,
            mode = previewMode,
            onModeChange = viewModel::setPreviewMode,
            onBack = { previewItems = null },
            onMark = { item -> viewModel.setMarked(item.path, true) },
            onUnmark = { item -> viewModel.setMarked(item.path, false) },
            onIndexChange = { previewIndex = it }
        )
        return
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.GridView, null) },
                    label = { Text("图片") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Folder, null) },
                    label = { Text("文件夹") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Star, null) },
                    label = { Text(if (markedCount == 0) "待删" else "待删 $markedCount") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Settings, null) },
                    label = { Text("设置") }
                )
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> ImageBrowserScreen(
                padding = padding,
                images = images,
                markedCount = markedCount,
                scanProgress = scanProgress,
                onScan = viewModel::scan,
                onOpen = { index ->
                    previewItems = images
                    previewIndex = index
                }
            )
            1 -> FolderScreen(
                padding = padding,
                folders = folders,
                scanProgress = scanProgress,
                onAdd = viewModel::addFolder,
                onRemove = viewModel::removeFolder,
                onToggle = viewModel::setFolderEnabled,
                onScan = viewModel::scan
            )
            2 -> MarkedScreen(
                padding = padding,
                images = markedImages,
                moveProgress = moveProgress,
                targetPath = viewModel.moveTargetPath.collectAsStateWithLifecycleCompat().value,
                onOpen = { index ->
                    previewItems = markedImages
                    previewIndex = index
                },
                onMove = viewModel::moveMarked,
                onGoSettings = { selectedTab = 3 }
            )
            else -> SettingsScreen(
                padding = padding,
                includeSubfolders = viewModel.includeSubfolders.collectAsStateWithLifecycleCompat().value,
                previewMode = previewMode,
                moveTargetPath = viewModel.moveTargetPath.collectAsStateWithLifecycleCompat().value,
                onIncludeSubfoldersChange = viewModel::setIncludeSubfolders,
                onPreviewModeChange = viewModel::setPreviewMode,
                onMoveTargetChange = viewModel::setMoveTargetPath,
                onExport = { uri, resolver -> viewModel.exportConfig(uri, resolver) },
                onImport = { uri, resolver -> viewModel.importConfig(uri, resolver) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun ImageBrowserScreen(
    padding: PaddingValues,
    images: List<WallpaperImage>,
    markedCount: Int,
    scanProgress: ScanProgress,
    onScan: () -> Unit,
    onOpen: (Int) -> Unit
) {
    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = { Text("这合适吗") },
                actions = {
                    IconButton(onClick = onScan, enabled = !scanProgress.running) {
                        Icon(Icons.Default.Refresh, contentDescription = "扫描")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("图片 ${images.size} 张", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.weight(1f))
                Text("待删 $markedCount", color = MaterialTheme.colorScheme.primary)
            }

            if (scanProgress.running) ScanProgressCard(scanProgress)

            if (images.isEmpty() && !scanProgress.running) {
                EmptyState(
                    icon = Icons.Default.Image,
                    title = "还没有图片",
                    description = "先固定图片文件夹，然后扫描图片。",
                    actionText = "开始扫描",
                    onAction = onScan
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    gridItems(images, key = { it.path }) { image ->
                        ImageThumbnail(
                            image = image,
                            onClick = { onOpen(images.indexOf(image)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnail(image: WallpaperImage, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = File(image.path),
            contentDescription = image.displayName,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (image.marked) {
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(6.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "已标记",
                    tint = Color.White,
                    modifier = Modifier.padding(4.dp).size(18.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FolderScreen(
    padding: PaddingValues,
    folders: List<SourceFolder>,
    scanProgress: ScanProgress,
    onAdd: (String) -> Unit,
    onRemove: (SourceFolder) -> Unit,
    onToggle: (SourceFolder, Boolean) -> Unit,
    onScan: () -> Unit
) {
    val context = LocalContext.current
    val storageGranted = rememberAllFilesAccess()
    var showBrowser by remember { mutableStateOf(false) }
    var removeTarget by remember { mutableStateOf<SourceFolder?>(null) }

    Scaffold(
        modifier = Modifier.padding(padding),
        topBar = {
            TopAppBar(
                title = { Text("固定文件夹") },
                actions = {
                    IconButton(onClick = onScan, enabled = !scanProgress.running) {
                        Icon(Icons.Default.Refresh, contentDescription = "扫描")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            StoragePermissionCard(context)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { showBrowser = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = storageGranted
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("添加图片文件夹")
            }
            Spacer(Modifier.height(12.dp))
            Text("已固定 ${folders.size} 个文件夹", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (folders.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Folder,
                    title = "还没有固定文件夹",
                    description = "添加一个或多个图片目录后开始扫描。"
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(folders, key = { it.id }) { folder ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = folder.enabled,
                                    onCheckedChange = { onToggle(folder, it) }
                                )
                                Column(Modifier.weight(1f)) {
                                    Text(folder.displayName, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        folder.path,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                IconButton(onClick = { removeTarget = folder }) {
                                    Icon(Icons.Default.Delete, contentDescription = "移除")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showBrowser) {
        FolderBrowserDialog(
            title = "选择图片文件夹",
            onDismiss = { showBrowser = false },
            onSelect = {
                onAdd(it)
                showBrowser = false
            }
        )
    }

    removeTarget?.let { folder ->
        AlertDialog(
            onDismissRequest = { removeTarget = null },
            title = { Text("移除固定文件夹") },
            text = { Text("只从应用中移除“${folder.displayName}”，不会删除里面的图片。") },
            confirmButton = {
                TextButton(onClick = {
                    onRemove(folder)
                    removeTarget = null
                }) { Text("移除") }
            },
            dismissButton = { TextButton(onClick = { removeTarget = null }) { Text("取消") } }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MarkedScreen(
    padding: PaddingValues,
    images: List<WallpaperImage>,
    moveProgress: com.example.zheheshima.data.MoveProgress,
    targetPath: String?,
    onOpen: (Int) -> Unit,
    onMove: () -> Unit,
    onGoSettings: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(padding)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("待删列表", style = MaterialTheme.typography.headlineSmall)
                Text("先标记，确认后统一移动", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onMove, enabled = images.isNotEmpty() && !moveProgress.running) {
                Icon(Icons.Default.Save, contentDescription = "批量移动")
            }
        }

        if (moveProgress.running) {
            LinearProgressIndicator(
                progress = if (moveProgress.total == 0) 0f else moveProgress.completed.toFloat() / moveProgress.total,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            Text(
                "正在移动 ${moveProgress.completed}/${moveProgress.total}：${moveProgress.currentName}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (targetPath.isNullOrBlank()) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clickable(onClick = onGoSettings)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("还没有配置待删文件夹，点击去设置")
                }
            }
        } else {
            Text(
                "目标：$targetPath",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (images.isEmpty()) {
            EmptyState(
                icon = Icons.Default.CheckCircle,
                title = "暂时没有待删图片",
                description = "在预览中点击“标记待删”，图片会出现在这里。"
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                gridItems(images, key = { it.path }) { image ->
                    ImageThumbnail(image, onClick = { onOpen(images.indexOf(image)) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    padding: PaddingValues,
    includeSubfolders: Boolean,
    previewMode: PreviewMode,
    moveTargetPath: String?,
    onIncludeSubfoldersChange: (Boolean) -> Unit,
    onPreviewModeChange: (PreviewMode) -> Unit,
    onMoveTargetChange: (String) -> Unit,
    onExport: (Uri, android.content.ContentResolver) -> Unit,
    onImport: (Uri, android.content.ContentResolver) -> Unit
) {
    val context = LocalContext.current
    val storageGranted = rememberAllFilesAccess()
    var showTargetBrowser by remember { mutableStateOf(false) }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let { onExport(it, context.contentResolver) } }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { onImport(it, context.contentResolver) } }

    Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
        Text("设置", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 18.dp))
        Spacer(Modifier.height(12.dp))
        StoragePermissionCard(context)

        Spacer(Modifier.height(16.dp))
        Text("扫描", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("扫描子文件夹")
                Text("开启后会递归读取固定文件夹中的图片", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = includeSubfolders, onCheckedChange = onIncludeSubfoldersChange)
        }

        Divider()
        Spacer(Modifier.height(14.dp))
        Text("待删文件夹", style = MaterialTheme.typography.titleMedium)
        OutlinedButton(
            onClick = { showTargetBrowser = true },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            enabled = storageGranted
        ) {
            Icon(Icons.Default.Folder, null)
            Spacer(Modifier.width(8.dp))
            Text(if (moveTargetPath.isNullOrBlank()) "选择待删文件夹" else "重新选择待删文件夹")
        }
        if (!moveTargetPath.isNullOrBlank()) {
            Text(
                moveTargetPath,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(14.dp))
        Text("预览模式", style = MaterialTheme.typography.titleMedium)
        PreviewMode.values().forEach { mode ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onPreviewModeChange(mode) }.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = previewMode == mode, onClick = { onPreviewModeChange(mode) })
                Text(mode.title)
            }
        }

        Spacer(Modifier.height(16.dp))
        Divider()
        Spacer(Modifier.height(14.dp))
        Text("配置", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Button(
                onClick = { exportLauncher.launch("这合适吗_exportConfig_${timestampForFile()}.json") },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Share, null)
                Spacer(Modifier.width(6.dp))
                Text("导出配置")
            }
            OutlinedButton(
                onClick = { importLauncher.launch(arrayOf("application/json", "text/*")) },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(6.dp))
                Text("导入配置")
            }
        }
    }

    if (showTargetBrowser) {
        FolderBrowserDialog(
            title = "选择待删文件夹",
            onDismiss = { showTargetBrowser = false },
            onSelect = {
                onMoveTargetChange(it)
                showTargetBrowser = false
            }
        )
    }
}

@Composable
private fun StoragePermissionCard(context: Context) {
    val granted = rememberAllFilesAccess()
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (granted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(if (granted) Icons.Default.CheckCircle else Icons.Default.Lock, null)
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(if (granted) "存储权限已就绪" else "需要存储权限", fontWeight = FontWeight.SemiBold)
                Text(
                    if (granted) "可以扫描和移动固定文件夹中的图片。"
                    else "请在系统设置中允许管理所有文件。",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!granted) {
                TextButton(onClick = { openAllFilesSettings(context) }) { Text("去授权") }
            }
        }
    }
}

@Composable
private fun rememberAllFilesAccess(): Boolean {
    return hasAllFilesAccess()
}

@Composable
private fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))
        Text(description, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (actionText != null && onAction != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onAction) { Text(actionText) }
        }
    }
}

@Composable
private fun ScanProgressCard(progress: ScanProgress) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(progress.stage.ifBlank { "正在扫描" }, fontWeight = FontWeight.SemiBold)
            Text(
                "已读取 ${progress.scanned} 项 · 图片候选 ${progress.candidates} 张 · 完成 ${progress.completed} 张",
                style = MaterialTheme.typography.bodySmall
            )
            if (progress.folderName.isNotBlank()) {
                Text(
                    "当前：${progress.folderName} · ${progress.currentPath}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun FolderBrowserDialog(
    title: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    var current by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }
    val directories = remember(current.path) {
        current.listFiles()
            ?.filter { it.isDirectory && !it.name.startsWith(".") }
            ?.sortedBy { it.name.lowercase() }
            .orEmpty()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                Text(
                    current.path,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { current.parentFile?.let { current = it } },
                        enabled = current.parentFile != null
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, null)
                        Text("上一级")
                    }
                    Button(onClick = { onSelect(current.canonicalPath) }) { Text("选择当前目录") }
                }
                Spacer(Modifier.height(8.dp))
                LazyColumn(Modifier.height(300.dp)) {
                    items(directories, key = { it.path }) { directory ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { current = directory }.padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Folder, null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(10.dp))
                            Text(directory.name, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.weight(1f))
                            Icon(Icons.Default.ArrowForward, null, tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("取消") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewScreen(
    items: List<WallpaperImage>,
    initialIndex: Int,
    mode: PreviewMode,
    onModeChange: (PreviewMode) -> Unit,
    onBack: () -> Unit,
    onMark: (WallpaperImage) -> Unit,
    onUnmark: (WallpaperImage) -> Unit,
    onIndexChange: (Int) -> Unit
) {
    if (items.isEmpty()) {
        onBack()
        return
    }

    var index by remember(items) { mutableIntStateOf(initialIndex.coerceIn(0, items.lastIndex)) }
    var dragDistance by remember { mutableFloatStateOf(0f) }
    val immersive = mode == PreviewMode.IMMERSIVE

    SystemBarsForPreview(immersive)
    BackHandler(onBack = onBack)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(items.size) {
                detectHorizontalDragGestures(
                    onHorizontalDrag = { _, amount -> dragDistance += amount },
                    onDragEnd = {
                        when {
                            dragDistance < -80f -> index = (index + 1).coerceAtMost(items.lastIndex)
                            dragDistance > 80f -> index = (index - 1).coerceAtLeast(0)
                        }
                        dragDistance = 0f
                        onIndexChange(index)
                    }
                )
            }
    ) {
        DesktopMock(items[index], Modifier.fillMaxSize())

        Surface(
            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().padding(12.dp),
            color = Color.Black.copy(alpha = 0.45f),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "返回", tint = Color.White) }
                Column(Modifier.weight(1f)) {
                    Text("${index + 1}/${items.size}", color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text(
                        items[index].displayName,
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                FilterChip(
                    selected = immersive,
                    onClick = { onModeChange(if (immersive) PreviewMode.STANDARD else PreviewMode.IMMERSIVE) },
                    label = { Text(if (immersive) "全屏" else "普通") }
                )
            }
        }

        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(12.dp),
            color = Color.Black.copy(alpha = 0.5f),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        onMark(items[index])
                        index = (index + 1).coerceAtMost(items.lastIndex)
                        onIndexChange(index)
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("标记待删", color = Color.White) }
                Button(
                    onClick = {
                        onUnmark(items[index])
                        index = (index + 1).coerceAtMost(items.lastIndex)
                        onIndexChange(index)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, null)
                    Spacer(Modifier.width(4.dp))
                    Text("保留")
                }
            }
        }
    }
}

@Composable
private fun DesktopMock(image: WallpaperImage, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.background(Color.Black), contentAlignment = Alignment.Center) {
        // The reference desktop in image 1 is 1080 x 2296. Keep this canvas fixed so
        // every wallpaper is judged against the same real launcher geometry.
        val designHeight = 2296f
        val designScale = minOf(maxWidth / 1080f, maxHeight / designHeight)
        val canvasWidth = 1080f * designScale.value
        val canvasHeight = designHeight * designScale.value
        var displayedPath by remember { mutableStateOf(image.path) }
        var loadingPath by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(image.path) {
            loadingPath = image.path.takeUnless { it == displayedPath }
        }

        // Paint the wallpaper across the complete preview viewport. Only the launcher
        // overlay keeps the reference canvas size; this removes immersive-mode gutters
        // without stretching or moving the real icon grid.
        AsyncImage(
            model = File(displayedPath),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            onSuccess = {
                if (loadingPath == displayedPath) loadingPath = null
            }
        )
        loadingPath?.let { targetPath ->
            AsyncImage(
                model = File(targetPath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    if (loadingPath == targetPath) displayedPath = targetPath
                },
                onError = {
                    if (loadingPath == targetPath) loadingPath = null
                }
            )
        }
        Box(Modifier.size(canvasWidth.dp, canvasHeight.dp)) {
            DesktopOverlay(scale = designScale.value)
        }
    }
}

@Composable
private fun DesktopOverlay(scale: Float) {
    fun px(value: Float): Dp = (value * scale).dp

    Box(Modifier.fillMaxSize()) {
        Text(
            "20:15",
            modifier = Modifier.offset(px(86f), px(145f)),
            color = Color.Black,
            fontSize = (130f * scale).sp,
            fontWeight = FontWeight.Light
        )
        Text(
            "9月9日周三 · 七月廿五",
            modifier = Modifier.offset(px(104f), px(382f)),
            color = Color.Black,
            fontSize = (36f * scale).sp
        )
        Text(
            "当前位置 25°C",
            modifier = Modifier.offset(px(690f), px(382f)),
            color = Color.Black,
            fontSize = (36f * scale).sp
        )
        Text(
            "今日 30°/22°",
            modifier = Modifier.offset(px(104f), px(460f)),
            color = Color.Black,
            fontSize = (36f * scale).sp
        )
        Text(
            "体感 28°C",
            modifier = Modifier.offset(px(774f), px(460f)),
            color = Color.Black,
            fontSize = (36f * scale).sp
        )
        Text(
            "空气质量 111 (不健康)",
            modifier = Modifier.offset(px(104f), px(538f)),
            color = Color.Black,
            fontSize = (36f * scale).sp
        )
        Text(
            "↓ 2.3 米/秒",
            modifier = Modifier.offset(px(756f), px(538f)),
            color = Color.Black,
            fontSize = (36f * scale).sp
        )

        WeatherMoon(
            modifier = Modifier.offset(px(824f), px(168f)).size(px(158f))
        )

        DesktopRasterIcon(::px, 48f, 700f, "Authenticator", DesktopAsset.AUTHENTICATOR, imageOffsetY = 3f)
        DesktopRasterFolder(::px, 252f, 700f, "B站", DesktopAsset.B_STATION_FOLDER, imageOffsetY = 3f)
        DesktopRasterIcon(::px, 456f, 700f, "baby", DesktopAsset.BABY, imageOffsetY = 3f)
        DesktopRasterFolder(::px, 660f, 700f, "don't skip", DesktopAsset.DONT_SKIP_FOLDER, imageOffsetY = 3f)
        DesktopRasterFolder(::px, 864f, 700f, "小工具", DesktopAsset.SMALL_TOOLS_FOLDER, imageOffsetY = 3f)

        DesktopRasterIcon(::px, 48f, 1015f, "FlashWall", DesktopAsset.FLASHWALL)
        DesktopRasterIcon(::px, 252f, 1015f, "录音机", DesktopAsset.RECORDER)
        DesktopRasterIcon(::px, 456f, 1015f, "微信", DesktopAsset.WECHAT_TOP)
        DesktopRasterIcon(::px, 864f, 1015f, "Share", DesktopAsset.SHARE)

        DesktopRasterIcon(::px, 48f, 1328f, "微信", DesktopAsset.WECHAT_THIRD)
        DesktopRasterIcon(::px, 252f, 1328f, "TODO", DesktopAsset.TODO)
        DesktopRasterIcon(::px, 456f, 1328f, "设置", DesktopAsset.SETTINGS)
        DesktopRasterIcon(::px, 660f, 1328f, "BotFather", DesktopAsset.BOTFATHER)
        DesktopRasterIcon(::px, 864f, 1328f, "企业微信", DesktopAsset.WEWORK)

        DesktopRasterIcon(::px, 48f, 1640f, "质感文件", DesktopAsset.FILES)
        DesktopRasterIcon(::px, 456f, 1640f, "雪豹速清", DesktopAsset.CLEANER)
        DesktopRasterIcon(::px, 660f, 1640f, "Komi Store", DesktopAsset.KOMI)
        DesktopRasterIcon(::px, 864f, 1640f, "MT管理器", DesktopAsset.MT)

        DesktopRasterDockIcon(::px, 48f, 2032f, DesktopAsset.CAMERA)
        DesktopRasterDockIcon(::px, 252f, 2032f, DesktopAsset.PHONE)
        DesktopRasterDockIcon(::px, 456f, 2032f, DesktopAsset.GALLERY)
        DesktopRasterDockIcon(::px, 660f, 2032f, DesktopAsset.MARKET)
        DesktopRasterDockIcon(::px, 864f, 2032f, DesktopAsset.TELEGRAM_PORTRAIT)

        Row(
            modifier = Modifier.offset(px(405f), px(1915f)).width(px(290f)),
            horizontalArrangement = Arrangement.spacedBy(px(22f))
        ) {
            repeat(7) { index ->
                Box(
                    Modifier.size(px(if (index == 1) 16f else 12f)).clip(CircleShape)
                        .background(if (index == 1) Color.White else Color.White.copy(alpha = 0.5f))
                )
            }
        }
        Box(
            modifier = Modifier.offset(px(337f), px(2255f)).size(px(400f), px(13f)).clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.92f))
        )
    }
}

private enum class DesktopAsset(@DrawableRes val resourceId: Int) {
    AUTHENTICATOR(R.drawable.desktop_authenticator),
    B_STATION_FOLDER(R.drawable.desktop_b_station_folder),
    BABY(R.drawable.desktop_baby),
    DONT_SKIP_FOLDER(R.drawable.desktop_dont_skip_folder),
    SMALL_TOOLS_FOLDER(R.drawable.desktop_small_tools_folder),
    FLASHWALL(R.drawable.desktop_flashwall),
    RECORDER(R.drawable.desktop_recorder),
    WECHAT_TOP(R.drawable.desktop_wechat),
    SHARE(R.drawable.desktop_share),
    WECHAT_THIRD(R.drawable.desktop_wechat_2),
    TODO(R.drawable.desktop_todo),
    SETTINGS(R.drawable.desktop_settings),
    BOTFATHER(R.drawable.desktop_botfather),
    WEWORK(R.drawable.desktop_wework),
    FILES(R.drawable.desktop_files),
    CLEANER(R.drawable.desktop_cleaner),
    KOMI(R.drawable.desktop_komi),
    MT(R.drawable.desktop_mt),
    CAMERA(R.drawable.desktop_camera),
    PHONE(R.drawable.desktop_phone),
    GALLERY(R.drawable.desktop_gallery),
    MARKET(R.drawable.desktop_market),
    TELEGRAM_PORTRAIT(R.drawable.desktop_telegram_portrait)
}

@Composable
private fun DesktopRasterIcon(
    px: (Float) -> Dp,
    x: Float,
    y: Float,
    label: String,
    asset: DesktopAsset,
    imageOffsetY: Float = 0f
) {
    Column(
        modifier = Modifier.offset(px(x), px(y)).width(px(180f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(asset.resourceId),
            contentDescription = label,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                // The bitmap is already cropped to the icon bounds. The Column centers
                // a 144px image inside a 180px slot, so nudge it 4px left to match
                // the reference launcher grid rather than adding a second margin.
                .offset(x = px(-4f), y = px(imageOffsetY))
                .size(px(144f))
                .clip(RoundedCornerShape(px(31f)))
        )
        Spacer(Modifier.height(px(12f)))
        Text(
            label,
            color = Color.White,
            fontSize = (30f * px(1f).value).sp,
            lineHeight = (35f * px(1f).value).sp,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DesktopRasterFolder(
    px: (Float) -> Dp,
    x: Float,
    y: Float,
    label: String,
    asset: DesktopAsset,
    imageOffsetY: Float = 0f
) {
    Column(
        modifier = Modifier.offset(px(x), px(y)).width(px(180f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(asset.resourceId),
            contentDescription = label,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .offset(x = px(-4f), y = px(imageOffsetY))
                .size(px(144f))
                .clip(RoundedCornerShape(px(31f)))
        )
        Spacer(Modifier.height(px(12f)))
        Text(
            label,
            color = Color.White,
            fontSize = (30f * px(1f).value).sp,
            lineHeight = (34f * px(1f).value).sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DesktopRasterDockIcon(px: (Float) -> Dp, x: Float, y: Float, asset: DesktopAsset) {
    Image(
        painter = painterResource(asset.resourceId),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier
            .offset(px(x), px(y))
            .then(Modifier.offset(x = px(14f)))
            .size(px(144f))
            .clip(RoundedCornerShape(px(31f)))
    )
}

@Composable
private fun WeatherMoon(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = size.minDimension
        val center = Offset(size.width * 0.52f, size.height * 0.50f)
        val moon = Path().apply {
            moveTo(center.x + unit * 0.18f, center.y - unit * 0.38f)
            cubicTo(
                center.x - unit * 0.08f, center.y - unit * 0.35f,
                center.x - unit * 0.29f, center.y - unit * 0.14f,
                center.x - unit * 0.27f, center.y + unit * 0.12f
            )
            cubicTo(
                center.x - unit * 0.25f, center.y + unit * 0.38f,
                center.x + unit * 0.03f, center.y + unit * 0.43f,
                center.x + unit * 0.24f, center.y + unit * 0.29f
            )
            cubicTo(
                center.x + unit * 0.02f, center.y + unit * 0.23f,
                center.x - unit * 0.01f, center.y + unit * 0.02f,
                center.x + unit * 0.03f, center.y - unit * 0.12f
            )
            cubicTo(
                center.x + unit * 0.06f, center.y - unit * 0.25f,
                center.x + unit * 0.12f, center.y - unit * 0.33f,
                center.x + unit * 0.18f, center.y - unit * 0.38f
            )
            close()
        }
        drawPath(moon, Color(0xFFB7CDFF))
    }
}

private enum class DesktopAppStyle {
    AUTHENTICATOR, PORTRAIT, BABY, RECORDER, WECHAT, SHARE, TODO, SETTINGS, BOT, WEWORK,
    FILES, PLANET, CLEANER, KOMI, MT, CAMERA, PHONE, GALLERY, MARKET, TELEGRAM_PORTRAIT,
    NOTE, DESCRIPTION, TREE, PAW, VIDEO, SHIELD
}

@Composable
private fun WeatherSun(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        repeat(8) { index ->
            val angle = index * PI.toFloat() / 4f
            val inner = size.minDimension * 0.32f
            val outer = size.minDimension * 0.49f
            drawLine(
                color = Color(0xFFFFB74D),
                start = Offset(center.x + cos(angle) * inner, center.y + sin(angle) * inner),
                end = Offset(center.x + cos(angle) * outer, center.y + sin(angle) * outer),
                strokeWidth = size.minDimension * 0.18f,
                cap = StrokeCap.Square
            )
        }
        drawCircle(Color(0xFFFFD86A), radius = size.minDimension * 0.34f, center = center)
    }
}

@Composable
private fun DesktopFolder(
    x: Dp,
    y: Dp,
    px: (Float) -> Dp,
    label: String,
    icons: List<DesktopAppStyle>
) {
    Column(
        modifier = Modifier.offset(x, y).width(px(180f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(px(144f)),
            shape = RoundedCornerShape(px(31f)),
            color = Color(0xFF6F6F73).copy(alpha = 0.34f)
        ) {
            Column(
                modifier = Modifier.padding(px(14f)),
                verticalArrangement = Arrangement.spacedBy(px(7f))
            ) {
                icons.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(px(7f))) {
                        row.forEach { style ->
                            DesktopMiniIcon(style = style, px = px)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(px(12f)))
        Text(
            label,
            color = Color.White,
            fontSize = (30f * px(1f).value).sp,
            lineHeight = (34f * px(1f).value).sp,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DesktopMiniIcon(style: DesktopAppStyle, px: (Float) -> Dp) {
    Surface(
        modifier = Modifier.size(px(34f)),
        shape = RoundedCornerShape(px(8f)),
        color = desktopTileColor(style)
    ) {
        DesktopAppArtwork(style, px(1f).value, Modifier.fillMaxSize())
    }
}

@Composable
private fun DesktopIcon(
    px: (Float) -> Dp,
    x: Float,
    y: Float,
    label: String,
    style: DesktopAppStyle
) {
    Column(
        modifier = Modifier.offset(px(x), px(y)).width(px(180f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(px(144f)),
            shape = RoundedCornerShape(px(31f)),
            color = desktopTileColor(style),
            shadowElevation = px(3f)
        ) {
            DesktopAppArtwork(style, px(1f).value, Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(px(12f)))
        Text(
            label,
            color = Color.White,
            fontSize = (30f * px(1f).value).sp,
            lineHeight = (35f * px(1f).value).sp,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DockIcon(px: (Float) -> Dp, x: Float, y: Float, style: DesktopAppStyle) {
    Surface(
        modifier = Modifier.offset(px(x), px(y)).size(px(144f)),
        shape = RoundedCornerShape(px(31f)),
        color = desktopTileColor(style),
        shadowElevation = px(3f)
    ) {
        DesktopAppArtwork(style, px(1f).value, Modifier.fillMaxSize())
    }
}

private fun desktopTileColor(style: DesktopAppStyle): Color = when (style) {
    DesktopAppStyle.WECHAT, DesktopAppStyle.PHONE -> Color(0xFF08D568)
    DesktopAppStyle.SETTINGS -> Color(0xFFA9BBC6)
    DesktopAppStyle.FILES -> Color(0xFF1677ED)
    DesktopAppStyle.CLEANER -> Color(0xFF3BA8F5)
    DesktopAppStyle.KOMI -> Color(0xFF11131A)
    DesktopAppStyle.GALLERY -> Color(0xFFFF7A00)
    DesktopAppStyle.NOTE -> Color(0xFFFFB300)
    DesktopAppStyle.PAW -> Color(0xFF20242B)
    DesktopAppStyle.VIDEO -> Color(0xFF2196E8)
    DesktopAppStyle.SHIELD -> Color(0xFF1677C8)
    DesktopAppStyle.TREE -> Color(0xFFE8FFF1)
    else -> Color(0xFFF8F8F8)
}

@Composable
private fun DesktopAppArtwork(style: DesktopAppStyle, scale: Float, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val unit = size.minDimension
            val center = Offset(size.width / 2f, size.height / 2f)
            fun point(degrees: Float, radius: Float): Offset {
                val angle = degrees * PI.toFloat() / 180f
                return Offset(center.x + cos(angle) * radius, center.y + sin(angle) * radius)
            }
            fun portrait(bot: Boolean = false, badge: Boolean = true) {
                drawCircle(if (bot) Color(0xFF74C7EB) else Color(0xFFFFE3D3), unit * 0.39f, center)
                drawCircle(Color(0xFFFFD0B5), unit * 0.15f, Offset(center.x, center.y - unit * 0.06f))
                drawArc(
                    color = if (bot) Color(0xFF453228) else Color(0xFFE3B766),
                    startAngle = 185f,
                    sweepAngle = 170f,
                    useCenter = true,
                    topLeft = Offset(unit * 0.28f, unit * 0.22f),
                    size = Size(unit * 0.44f, unit * 0.43f)
                )
                drawPath(
                    Path().apply {
                        moveTo(unit * 0.28f, unit * 0.82f)
                        lineTo(unit * 0.42f, unit * 0.58f)
                        lineTo(unit * 0.58f, unit * 0.58f)
                        lineTo(unit * 0.74f, unit * 0.82f)
                        close()
                    },
                    color = if (bot) Color(0xFF25333D) else Color(0xFFB7A0E8)
                )
                if (bot) {
                    drawPath(
                        Path().apply {
                            moveTo(unit * 0.44f, unit * 0.61f)
                            lineTo(unit * 0.50f, unit * 0.69f)
                            lineTo(unit * 0.56f, unit * 0.61f)
                            lineTo(unit * 0.50f, unit * 0.76f)
                            close()
                        }, Color.White
                    )
                }
                if (badge) {
                    val badgeCenter = Offset(unit * 0.78f, unit * 0.78f)
                    drawCircle(Color(0xFF39A9E8), unit * 0.105f, badgeCenter)
                    drawPath(
                        Path().apply {
                            moveTo(unit * 0.72f, unit * 0.76f)
                            lineTo(unit * 0.84f, unit * 0.72f)
                            lineTo(unit * 0.79f, unit * 0.84f)
                            close()
                        }, Color.White
                    )
                }
            }

            when (style) {
                DesktopAppStyle.AUTHENTICATOR -> {
                    val spokeColors = listOf(
                        Color(0xFF2E7DE9), Color(0xFFFFC107), Color(0xFFFF4F3E),
                        Color(0xFF34A853), Color(0xFF1A73E8), Color(0xFFFFC107)
                    )
                    repeat(6) { index ->
                        drawLine(
                            color = spokeColors[index],
                            start = point(index * 60f, unit * 0.06f),
                            end = point(index * 60f, unit * 0.34f),
                            strokeWidth = unit * 0.105f,
                            cap = StrokeCap.Round
                        )
                    }
                    drawCircle(Color.White, unit * 0.075f, center)
                }
                DesktopAppStyle.PORTRAIT, DesktopAppStyle.BABY, DesktopAppStyle.TODO,
                DesktopAppStyle.TELEGRAM_PORTRAIT -> portrait()
                DesktopAppStyle.BOT -> portrait(bot = true)
                DesktopAppStyle.RECORDER -> {
                    drawCircle(Color(0xFFFF4754), unit * 0.34f, center)
                    drawCircle(Color(0xFFF9F9F9), unit * 0.13f, center)
                }
                DesktopAppStyle.WECHAT -> {
                    drawCircle(Color.White, unit * 0.25f, Offset(unit * 0.43f, unit * 0.43f))
                    drawCircle(Color.White, unit * 0.21f, Offset(unit * 0.62f, unit * 0.59f))
                    drawCircle(Color(0xFF08D568), unit * 0.025f, Offset(unit * 0.35f, unit * 0.39f))
                    drawCircle(Color(0xFF08D568), unit * 0.025f, Offset(unit * 0.48f, unit * 0.39f))
                    drawCircle(Color(0xFF08D568), unit * 0.022f, Offset(unit * 0.57f, unit * 0.56f))
                    drawCircle(Color(0xFF08D568), unit * 0.022f, Offset(unit * 0.67f, unit * 0.56f))
                }
                DesktopAppStyle.SHARE -> {
                    val dark = Color(0xFF151A20)
                    val cyan = Color(0xFF55D4D0)
                    drawPath(Path().apply { moveTo(unit*.28f,unit*.25f); lineTo(unit*.5f,unit*.12f); lineTo(unit*.5f,unit*.44f); close() }, dark)
                    drawPath(Path().apply { moveTo(unit*.5f,unit*.12f); lineTo(unit*.76f,unit*.28f); lineTo(unit*.5f,unit*.44f); close() }, Color(0xFF3A4650))
                    drawPath(Path().apply { moveTo(unit*.28f,unit*.25f); lineTo(unit*.5f,unit*.44f); lineTo(unit*.28f,unit*.57f); close() }, Color(0xFF8D969B))
                    drawPath(Path().apply { moveTo(unit*.5f,unit*.44f); lineTo(unit*.76f,unit*.57f); lineTo(unit*.5f,unit*.76f); close() }, cyan)
                    drawPath(Path().apply { moveTo(unit*.28f,unit*.57f); lineTo(unit*.5f,unit*.76f); lineTo(unit*.28f,unit*.88f); close() }, Color(0xFF9AA0A4))
                    drawPath(Path().apply { moveTo(unit*.5f,unit*.76f); lineTo(unit*.76f,unit*.57f); lineTo(unit*.76f,unit*.78f); close() }, dark)
                }
                DesktopAppStyle.SETTINGS -> {
                    repeat(8) { index ->
                        drawLine(Color.White, point(index * 45f, unit*.23f), point(index * 45f, unit*.34f), unit*.09f, StrokeCap.Square)
                    }
                    drawCircle(Color.White, unit*.25f, center)
                    drawCircle(Color(0xFFA9BBC6), unit*.105f, center)
                }
                DesktopAppStyle.WEWORK -> {
                    val blue = Color(0xFF1489E8)
                    drawCircle(blue, unit*.25f, Offset(unit*.42f, unit*.47f), style = Stroke(unit*.055f))
                    drawCircle(blue, unit*.19f, Offset(unit*.62f, unit*.56f), style = Stroke(unit*.05f))
                    listOf(Color(0xFFFFA000), Color(0xFF19BD5A), Color(0xFFFF5A4C)).forEachIndexed { i, color ->
                        drawCircle(color, unit*.045f, Offset(unit*(.68f+i*.07f), unit*(.72f-i*.04f)))
                    }
                }
                DesktopAppStyle.FILES -> {
                    drawPath(Path().apply {
                        moveTo(unit*.20f,unit*.34f); lineTo(unit*.42f,unit*.34f); lineTo(unit*.49f,unit*.41f)
                        lineTo(unit*.80f,unit*.41f); lineTo(unit*.80f,unit*.73f); lineTo(unit*.20f,unit*.73f); close()
                    }, Color.White)
                    drawRoundRect(Color(0xFFDCEBFF), Offset(unit*.23f,unit*.45f), Size(unit*.54f,unit*.06f), CornerRadius(unit*.025f))
                }
                DesktopAppStyle.PLANET -> {
                    val teal = Color(0xFF27B59C)
                    drawCircle(teal, unit*.26f, center, style = Stroke(unit*.055f))
                    drawArc(teal, 205f, 230f, false, Offset(unit*.20f,unit*.20f), Size(unit*.60f,unit*.60f), style = Stroke(unit*.055f, cap = StrokeCap.Round))
                    drawCircle(teal, unit*.07f, Offset(unit*.69f,unit*.31f))
                }
                DesktopAppStyle.CLEANER -> {
                    drawLine(Color.White, Offset(unit*.65f,unit*.22f), Offset(unit*.38f,unit*.67f), unit*.085f, StrokeCap.Round)
                    drawPath(Path().apply { moveTo(unit*.25f,unit*.64f); lineTo(unit*.46f,unit*.56f); lineTo(unit*.62f,unit*.78f); lineTo(unit*.27f,unit*.78f); close() }, Color.White)
                    drawPath(Path().apply { moveTo(unit*.35f,unit*.64f); lineTo(unit*.42f,unit*.73f); lineTo(unit*.49f,unit*.63f) }, Color(0xFF3BA8F5), style = Stroke(unit*.025f))
                }
                DesktopAppStyle.KOMI -> {
                    drawCircle(Color(0xFFD8E1E8), unit*.24f, Offset(unit*.43f,unit*.52f))
                    drawPath(Path().apply { moveTo(unit*.25f,unit*.38f); lineTo(unit*.31f,unit*.22f); lineTo(unit*.41f,unit*.39f); close() }, Color(0xFFD8E1E8))
                    drawPath(Path().apply { moveTo(unit*.45f,unit*.39f); lineTo(unit*.57f,unit*.23f); lineTo(unit*.62f,unit*.45f); close() }, Color(0xFFD8E1E8))
                    drawPath(Path().apply { moveTo(unit*.59f,unit*.35f); lineTo(unit*.81f,unit*.50f); lineTo(unit*.59f,unit*.65f); close() }, Color(0xFF3E4650))
                    drawCircle(Color(0xFF11131A), unit*.02f, Offset(unit*.36f,unit*.49f))
                }
                DesktopAppStyle.MT -> {
                    drawRoundRect(Color(0xFF363A3E), Offset(unit*.20f,unit*.30f), Size(unit*.60f,unit*.50f), CornerRadius(unit*.06f))
                    drawRoundRect(Color(0xFFBFC6CC), Offset(unit*.28f,unit*.19f), Size(unit*.28f,unit*.17f), CornerRadius(unit*.04f))
                    drawCircle(Color(0xFF4E67BD), unit*.04f, Offset(unit*.41f,unit*.23f))
                }
                DesktopAppStyle.CAMERA -> {
                    drawCircle(Color(0xFF17191C), unit*.29f, center)
                    drawCircle(Color(0xFF34373B), unit*.21f, center)
                    drawCircle(Color.Black, unit*.145f, center)
                    drawCircle(Color(0xFF737981), unit*.065f, Offset(unit*.47f,unit*.46f))
                    drawCircle(Color.Red, unit*.045f, Offset(unit*.77f,unit*.23f))
                }
                DesktopAppStyle.PHONE -> {
                    drawPath(
                        Path().apply { moveTo(unit*.32f,unit*.27f); quadraticBezierTo(unit*.30f,unit*.66f,unit*.70f,unit*.74f) },
                        Color.White, style = Stroke(unit*.13f, cap = StrokeCap.Round)
                    )
                }
                DesktopAppStyle.GALLERY -> {
                    drawCircle(Color.White, unit*.07f, Offset(unit*.68f,unit*.29f))
                    drawPath(Path().apply { moveTo(unit*.20f,unit*.74f); lineTo(unit*.43f,unit*.43f); lineTo(unit*.57f,unit*.59f); lineTo(unit*.68f,unit*.48f); lineTo(unit*.83f,unit*.74f); close() }, Color.White)
                }
                DesktopAppStyle.MARKET -> {
                    drawPath(Path().apply { moveTo(unit*.20f,unit*.68f); lineTo(unit*.39f,unit*.28f); lineTo(unit*.49f,unit*.40f); lineTo(unit*.34f,unit*.75f); close() }, Color(0xFF13A5DF))
                    drawPath(Path().apply { moveTo(unit*.39f,unit*.28f); lineTo(unit*.57f,unit*.43f); lineTo(unit*.49f,unit*.72f); lineTo(unit*.34f,unit*.75f); close() }, Color(0xFFFF3D20))
                    drawPath(Path().apply { moveTo(unit*.57f,unit*.43f); lineTo(unit*.75f,unit*.28f); lineTo(unit*.80f,unit*.72f); lineTo(unit*.49f,unit*.72f); close() }, Color(0xFFFFA600))
                }
                DesktopAppStyle.NOTE, DesktopAppStyle.DESCRIPTION -> {
                    drawRoundRect(Color.White, Offset(unit*.25f,unit*.18f), Size(unit*.50f,unit*.64f), CornerRadius(unit*.08f))
                    repeat(3) { i -> drawLine(Color(0xFF3989C9), Offset(unit*.34f,unit*(.36f+i*.13f)), Offset(unit*.66f,unit*(.36f+i*.13f)), unit*.045f, StrokeCap.Round) }
                }
                DesktopAppStyle.TREE -> {
                    drawPath(Path().apply { moveTo(unit*.50f,unit*.18f); lineTo(unit*.26f,unit*.55f); lineTo(unit*.39f,unit*.55f); lineTo(unit*.22f,unit*.75f); lineTo(unit*.78f,unit*.75f); lineTo(unit*.61f,unit*.55f); lineTo(unit*.74f,unit*.55f); close() }, Color(0xFF38C995))
                }
                DesktopAppStyle.PAW -> {
                    drawCircle(Color.White, unit*.14f, Offset(unit*.50f,unit*.61f))
                    repeat(4) { i -> drawCircle(Color.White, unit*.07f, Offset(unit*(.32f+i*.12f), unit*(.34f+(i%2)*.05f))) }
                }
                DesktopAppStyle.VIDEO -> drawPath(Path().apply { moveTo(unit*.34f,unit*.25f); lineTo(unit*.78f,unit*.50f); lineTo(unit*.34f,unit*.75f); close() }, Color.White)
                DesktopAppStyle.SHIELD -> drawPath(Path().apply { moveTo(unit*.50f,unit*.15f); lineTo(unit*.76f,unit*.27f); lineTo(unit*.70f,unit*.68f); lineTo(unit*.50f,unit*.84f); lineTo(unit*.30f,unit*.68f); lineTo(unit*.24f,unit*.27f); close() }, Color.White)
            }
        }
        if (style == DesktopAppStyle.MT) {
            Text("MT", color = Color.White, fontSize = (30f * scale).sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SystemBarsForPreview(immersive: Boolean) {
    val context = LocalContext.current
    val activity = context as? Activity ?: return
    DisposableEffect(immersive) {
        val window = activity.window
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        if (immersive) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
        onDispose {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }
}

private fun hasAllFilesAccess(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()

private fun openAllFilesSettings(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
    val packageUri = Uri.parse("package:${context.packageName}")
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
        data = packageUri
    }
    runCatching { context.startActivity(intent) }.getOrElse {
        context.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
    }
}

private fun timestampForFile(): String =
    java.text.SimpleDateFormat("yyyy-MM-dd HH_mm_ss", java.util.Locale.getDefault()).format(java.util.Date())

@Composable
private fun <T> kotlinx.coroutines.flow.StateFlow<T>.collectAsStateWithLifecycleCompat() =
    collectAsState()
