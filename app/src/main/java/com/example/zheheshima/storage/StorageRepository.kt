package com.example.zheheshima.storage

import com.example.zheheshima.data.MoveProgress
import com.example.zheheshima.data.ScanProgress
import com.example.zheheshima.data.SourceFolder
import com.example.zheheshima.data.WallpaperDao
import com.example.zheheshima.data.WallpaperImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class StorageRepository(private val dao: WallpaperDao) {
    private val imageExtensions = setOf(
        "jpg", "jpeg", "png", "webp", "gif", "bmp", "heic", "heif"
    )

    suspend fun scan(
        folders: List<SourceFolder>,
        targetPath: String?,
        includeSubfolders: Boolean,
        onProgress: (ScanProgress) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        if (folders.isEmpty()) return@withContext 0

        val scanStarted = System.currentTimeMillis()
        val existingMarks = dao.getAllImagesOnce().associate { it.path to it.marked }
        val folderIds = folders.map { it.id }
        val targetCanonical = targetPath?.let { canonicalOrNull(File(it)) }
        var scanned = 0
        var candidates = 0
        var completed = 0

        onProgress(
            ScanProgress(
                running = true,
                stage = "准备扫描",
                scanned = 0,
                candidates = 0,
                completed = 0
            )
        )

        folders.forEach { folder ->
            val root = canonicalOrNull(File(folder.path))
            if (root == null || !root.isDirectory) return@forEach

            onProgress(
                ScanProgress(
                    running = true,
                    stage = "扫描文件夹",
                    folderName = folder.displayName,
                    currentPath = root.path,
                    scanned = scanned,
                    candidates = candidates,
                    completed = completed
                )
            )

            val files = if (includeSubfolders) {
                root.walkTopDown()
                    .onEnter { directory -> !isInside(directory, targetCanonical) }
                    .filter { it.isFile }
            } else {
                root.listFiles()?.asSequence()?.filter { it.isFile } ?: emptySequence()
            }

            val batch = ArrayList<WallpaperImage>(256)
            try {
                files.forEach fileLoop@{ file ->
                    scanned++
                    if (isInside(file, targetCanonical)) return@fileLoop
                    if (!isImageFile(file)) return@fileLoop

                    candidates++
                    val path = canonicalOrNull(file)?.path ?: return@fileLoop
                    val seenAt = System.currentTimeMillis().coerceAtLeast(scanStarted + 1)
                    batch += WallpaperImage(
                        path = path,
                        sourceFolderId = folder.id,
                        displayName = file.name,
                        size = file.length(),
                        lastModified = file.lastModified(),
                        marked = existingMarks[path] ?: false,
                        lastSeen = seenAt
                    )
                    completed++

                    if (batch.size >= 256) {
                        dao.insertImages(batch.toList())
                        batch.clear()
                    }

                    if (scanned % 50 == 0) {
                        onProgress(
                            ScanProgress(
                                running = true,
                                stage = "读取图片",
                                folderName = folder.displayName,
                                currentPath = path,
                                scanned = scanned,
                                candidates = candidates,
                                completed = completed
                            )
                        )
                    }
                }
            } catch (_: SecurityException) {
                onProgress(
                    ScanProgress(
                        running = true,
                        stage = "部分目录无权访问",
                        folderName = folder.displayName,
                        currentPath = root.path,
                        scanned = scanned,
                        candidates = candidates,
                        completed = completed
                    )
                )
            }

            if (batch.isNotEmpty()) dao.insertImages(batch)
        }

        dao.deleteStaleImages(folderIds, scanStarted)
        onProgress(
            ScanProgress(
                running = false,
                stage = "扫描完成",
                scanned = scanned,
                candidates = candidates,
                completed = completed
            )
        )
        completed
    }

    suspend fun move(
        images: List<WallpaperImage>,
        targetPath: String,
        onProgress: (MoveProgress) -> Unit
    ): Int = withContext(Dispatchers.IO) {
        val target = canonicalOrNull(File(targetPath))
        if (target == null || !target.isDirectory) {
            throw IOException("待删文件夹不存在或不可访问")
        }

        val targetPathWithSeparator = target.path + File.separator
        var completed = 0
        var failed = 0
        onProgress(MoveProgress(running = true, total = images.size))

        images.forEach { image ->
            val source = canonicalOrNull(File(image.path))
            onProgress(
                MoveProgress(
                    running = true,
                    completed = completed,
                    total = images.size,
                    currentName = image.displayName,
                    failed = failed
                )
            )

            if (source == null || !source.isFile || source.path.startsWith(targetPathWithSeparator)) {
                failed++
                return@forEach
            }

            val destination = uniqueDestination(target, source.name)
            val moved = moveOne(source, destination)
            if (moved) {
                completed++
                dao.deleteImage(image.path)
            } else {
                failed++
            }
        }

        onProgress(
            MoveProgress(
                running = false,
                completed = completed,
                total = images.size,
                failed = failed
            )
        )
        failed
    }

    private fun moveOne(source: File, destination: File): Boolean {
        if (source.renameTo(destination)) {
            return destination.isFile && !source.exists()
        }

        return try {
            source.inputStream().use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                    output.flush()
                }
            }

            val copied = destination.isFile && destination.length() == source.length()
            if (!copied) {
                destination.delete()
                false
            } else if (source.delete()) {
                true
            } else {
                destination.delete()
                false
            }
        } catch (_: IOException) {
            destination.delete()
            false
        } catch (_: SecurityException) {
            destination.delete()
            false
        }
    }

    private fun uniqueDestination(target: File, originalName: String): File {
        var candidate = File(target, originalName)
        if (!candidate.exists()) return candidate

        val extension = originalName.substringAfterLast('.', "").let { if (it.isEmpty()) "" else ".$it" }
        val base = originalName.removeSuffix(extension)
        var number = 1
        while (candidate.exists()) {
            candidate = File(target, "$base ($number)$extension")
            number++
        }
        return candidate
    }

    private fun isImageFile(file: File): Boolean =
        file.extension.lowercase() in imageExtensions

    private fun canonicalOrNull(file: File): File? =
        runCatching { file.canonicalFile }.getOrNull()

    private fun isInside(file: File, directory: File?): Boolean {
        if (directory == null) return false
        val filePath = canonicalOrNull(file)?.path ?: return false
        val directoryPath = directory.path
        return filePath == directoryPath || filePath.startsWith(directoryPath + File.separator)
    }
}
