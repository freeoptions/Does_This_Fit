package com.example.zheheshima

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.zheheshima.data.AppDatabase
import com.example.zheheshima.data.MoveProgress
import com.example.zheheshima.data.ScanProgress
import com.example.zheheshima.data.SourceFolder
import com.example.zheheshima.data.WallpaperImage
import com.example.zheheshima.storage.StorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

enum class PreviewMode(val value: String, val title: String) {
    STANDARD("standard", "普通模式"),
    IMMERSIVE("immersive", "沉浸式全屏")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.get(application)
    private val dao = database.wallpaperDao()
    private val repository = StorageRepository(dao)
    private val preferences = application.getSharedPreferences(PREFS_NAME, 0)

    val folders: StateFlow<List<SourceFolder>> = dao.observeFolders().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val images: StateFlow<List<WallpaperImage>> = dao.observeActiveImages().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val markedImages: StateFlow<List<WallpaperImage>> = dao.observeMarkedImages().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    val markedCount: StateFlow<Int> = dao.observeMarkedCount().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        0
    )

    private val _scanProgress = MutableStateFlow(ScanProgress())
    val scanProgress: StateFlow<ScanProgress> = _scanProgress.asStateFlow()

    private val _moveProgress = MutableStateFlow(MoveProgress())
    val moveProgress: StateFlow<MoveProgress> = _moveProgress.asStateFlow()

    private val _includeSubfolders = MutableStateFlow(
        preferences.getBoolean(KEY_INCLUDE_SUBFOLDERS, true)
    )
    val includeSubfolders: StateFlow<Boolean> = _includeSubfolders.asStateFlow()

    private val _previewMode = MutableStateFlow(
        PreviewMode.values().firstOrNull {
            it.value == preferences.getString(KEY_PREVIEW_MODE, PreviewMode.IMMERSIVE.value)
        } ?: PreviewMode.IMMERSIVE
    )
    val previewMode: StateFlow<PreviewMode> = _previewMode.asStateFlow()

    private val _moveTargetPath = MutableStateFlow(
        preferences.getString(KEY_MOVE_TARGET_PATH, null)
    )
    val moveTargetPath: StateFlow<String?> = _moveTargetPath.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private var scanRunning = false
    private var moveRunning = false

    fun addFolder(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val canonical = runCatching { File(path).canonicalPath }.getOrNull()
            if (canonical.isNullOrBlank() || !File(canonical).isDirectory) {
                showMessage("文件夹不存在或不可访问")
                return@launch
            }
            if (dao.getFolderByPath(canonical) != null) {
                showMessage("这个文件夹已经固定")
                return@launch
            }
            val displayName = File(canonical).name.ifBlank { canonical }
            val sortOrder = dao.getFoldersOnce().size
            dao.insertFolder(
                SourceFolder(
                    path = canonical,
                    displayName = displayName,
                    sortOrder = sortOrder
                )
            )
            showMessage("文件夹已固定")
        }
    }

    fun removeFolder(folder: SourceFolder) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteFolder(folder)
            showMessage("已移除固定文件夹，图片文件未被删除")
        }
    }

    fun setFolderEnabled(folder: SourceFolder, enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateFolder(folder.copy(enabled = enabled))
        }
    }

    fun setIncludeSubfolders(enabled: Boolean) {
        _includeSubfolders.value = enabled
        preferences.edit().putBoolean(KEY_INCLUDE_SUBFOLDERS, enabled).apply()
    }

    fun setPreviewMode(mode: PreviewMode) {
        _previewMode.value = mode
        preferences.edit().putString(KEY_PREVIEW_MODE, mode.value).apply()
    }

    fun setMoveTargetPath(path: String) {
        val canonical = runCatching { File(path).canonicalPath }.getOrDefault(path)
        _moveTargetPath.value = canonical
        preferences.edit().putString(KEY_MOVE_TARGET_PATH, canonical).apply()
        showMessage("待删文件夹已设置")
    }

    fun scan() {
        if (scanRunning) return
        scanRunning = true
        viewModelScope.launch {
            try {
                val enabledFolders = withContext(Dispatchers.IO) { dao.getEnabledFolders() }
                if (enabledFolders.isEmpty()) {
                    showMessage("请先固定并启用至少一个图片文件夹")
                    _scanProgress.value = ScanProgress()
                    return@launch
                }
                repository.scan(
                    folders = enabledFolders,
                    targetPath = _moveTargetPath.value,
                    includeSubfolders = _includeSubfolders.value,
                    onProgress = { _scanProgress.value = it }
                )
                showMessage("扫描完成")
            } catch (error: Exception) {
                _scanProgress.value = ScanProgress()
                showMessage("扫描失败：${error.message ?: "未知错误"}")
            } finally {
                scanRunning = false
            }
        }
    }

    fun mark(image: WallpaperImage) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.setMarked(image.path, !image.marked)
        }
    }

    fun setMarked(path: String, marked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.setMarked(path, marked)
        }
    }

    fun markAll(marked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val folderIds = dao.getEnabledFolders().map { it.id }
            if (folderIds.isNotEmpty()) dao.setMarkedForFolders(folderIds, marked)
        }
    }

    fun moveMarked() {
        if (moveRunning) return
        val target = _moveTargetPath.value
        if (target.isNullOrBlank()) {
            showMessage("请先配置待删文件夹")
            return
        }

        moveRunning = true
        viewModelScope.launch {
            try {
                val marked = withContext(Dispatchers.IO) { dao.getAllImagesOnce().filter { it.marked } }
                if (marked.isEmpty()) {
                    showMessage("当前没有标记为待删的图片")
                    return@launch
                }
                val failed = repository.move(
                    images = marked,
                    targetPath = target,
                    onProgress = { _moveProgress.value = it }
                )
                val success = marked.size - failed
                showMessage("批量移动完成：成功 $success 张${if (failed > 0) "，失败 $failed 张" else ""}")
            } catch (error: Exception) {
                _moveProgress.value = MoveProgress()
                showMessage("移动失败：${error.message ?: "未知错误"}")
            } finally {
                moveRunning = false
            }
        }
    }

    fun exportConfig(uri: Uri, resolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("schemaVersion", 1)
                    put("includeSubfolders", _includeSubfolders.value)
                    put("previewMode", _previewMode.value.value)
                    put("moveTargetPath", _moveTargetPath.value)
                    put("exportedAt", System.currentTimeMillis())
                    put("folders", JSONArray().apply {
                        dao.getFoldersOnce().forEach { folder ->
                            put(JSONObject().apply {
                                put("path", folder.path)
                                put("displayName", folder.displayName)
                                put("enabled", folder.enabled)
                                put("sortOrder", folder.sortOrder)
                            })
                        }
                    })
                }
                resolver.openOutputStream(uri)?.use { output ->
                    output.write(json.toString(2).toByteArray(Charsets.UTF_8))
                } ?: error("无法打开导出文件")
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "配置已导出到指定位置", Toast.LENGTH_SHORT).show()
                }
            } catch (error: Exception) {
                showMessage("导出失败：${error.message ?: "未知错误"}")
            }
        }
    }

    fun importConfig(uri: Uri, resolver: ContentResolver) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val content = resolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    ?: error("无法打开配置文件")
                val json = JSONObject(content)
                val importedFolders = buildList {
                    val foldersJson = json.optJSONArray("folders") ?: JSONArray()
                    for (index in 0 until foldersJson.length()) {
                        val item = foldersJson.optJSONObject(index) ?: continue
                        val path = item.optString("path").trim()
                        if (path.isBlank()) continue
                        add(
                            SourceFolder(
                                path = path,
                                displayName = item.optString("displayName", File(path).name),
                                enabled = item.optBoolean("enabled", true),
                                sortOrder = item.optInt("sortOrder", index)
                            )
                        )
                    }
                }.distinctBy { it.path }

                dao.deleteAllImages()
                dao.deleteAllFolders()
                if (importedFolders.isNotEmpty()) dao.insertFolders(importedFolders)

                val include = json.optBoolean("includeSubfolders", true)
                val mode = PreviewMode.values().firstOrNull {
                    it.value == json.optString("previewMode", PreviewMode.IMMERSIVE.value)
                } ?: PreviewMode.IMMERSIVE
                val target = json.optString("moveTargetPath", "").takeIf { it.isNotBlank() }

                _includeSubfolders.value = include
                _previewMode.value = mode
                _moveTargetPath.value = target
                preferences.edit()
                    .putBoolean(KEY_INCLUDE_SUBFOLDERS, include)
                    .putString(KEY_PREVIEW_MODE, mode.value)
                    .putString(KEY_MOVE_TARGET_PATH, target)
                    .apply()
                showMessage("配置已导入，请检查文件夹路径后重新扫描")
            } catch (error: Exception) {
                showMessage("导入失败：${error.message ?: "配置格式不正确"}")
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun showMessage(value: String) {
        _message.value = value
    }

    companion object {
        private const val PREFS_NAME = "zheheshima_settings"
        private const val KEY_INCLUDE_SUBFOLDERS = "include_subfolders"
        private const val KEY_PREVIEW_MODE = "preview_mode"
        private const val KEY_MOVE_TARGET_PATH = "move_target_path"
    }
}
