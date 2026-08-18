package com.example.zheheshima.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "wallpaper_images",
    indices = [Index(value = ["marked"]), Index(value = ["sourceFolderId"])]
)
data class WallpaperImage(
    @PrimaryKey val path: String,
    val sourceFolderId: Long,
    val displayName: String,
    val size: Long,
    val lastModified: Long,
    val marked: Boolean = false,
    val lastSeen: Long = 0L
)
