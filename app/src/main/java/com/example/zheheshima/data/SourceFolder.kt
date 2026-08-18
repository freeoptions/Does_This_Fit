package com.example.zheheshima.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "source_folders",
    indices = [Index(value = ["path"], unique = true)]
)
data class SourceFolder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val displayName: String,
    val enabled: Boolean = true,
    val sortOrder: Int = 0
)
