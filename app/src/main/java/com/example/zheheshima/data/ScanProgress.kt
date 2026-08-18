package com.example.zheheshima.data

data class ScanProgress(
    val running: Boolean = false,
    val stage: String = "",
    val folderName: String = "",
    val currentPath: String = "",
    val scanned: Int = 0,
    val candidates: Int = 0,
    val completed: Int = 0
)

data class MoveProgress(
    val running: Boolean = false,
    val completed: Int = 0,
    val total: Int = 0,
    val currentName: String = "",
    val failed: Int = 0
)
