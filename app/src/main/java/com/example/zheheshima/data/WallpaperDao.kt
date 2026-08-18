package com.example.zheheshima.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM source_folders ORDER BY sortOrder ASC, id ASC")
    fun observeFolders(): Flow<List<SourceFolder>>

    @Query("SELECT * FROM source_folders ORDER BY sortOrder ASC, id ASC")
    suspend fun getFoldersOnce(): List<SourceFolder>

    @Query("SELECT * FROM source_folders WHERE enabled = 1 ORDER BY sortOrder ASC, id ASC")
    suspend fun getEnabledFolders(): List<SourceFolder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: SourceFolder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolders(folders: List<SourceFolder>)

    @Query("SELECT * FROM source_folders WHERE path = :path LIMIT 1")
    suspend fun getFolderByPath(path: String): SourceFolder?

    @Update
    suspend fun updateFolder(folder: SourceFolder)

    @Delete
    suspend fun deleteFolder(folder: SourceFolder)

    @Query("DELETE FROM source_folders")
    suspend fun deleteAllFolders()

    @Query("SELECT * FROM wallpaper_images WHERE marked = 1 ORDER BY path ASC")
    fun observeMarkedImages(): Flow<List<WallpaperImage>>

    @Query("SELECT COUNT(*) FROM wallpaper_images WHERE marked = 1")
    fun observeMarkedCount(): Flow<Int>

    @Query(
        """
        SELECT wallpaper_images.* FROM wallpaper_images
        INNER JOIN source_folders ON source_folders.id = wallpaper_images.sourceFolderId
        WHERE source_folders.enabled = 1
        ORDER BY wallpaper_images.path ASC
        """
    )
    fun observeActiveImages(): Flow<List<WallpaperImage>>

    @Query("SELECT * FROM wallpaper_images")
    suspend fun getAllImagesOnce(): List<WallpaperImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImages(images: List<WallpaperImage>)

    @Query("UPDATE wallpaper_images SET marked = :marked WHERE path = :path")
    suspend fun setMarked(path: String, marked: Boolean)

    @Query(
        "UPDATE wallpaper_images SET marked = :marked WHERE sourceFolderId IN (:folderIds)"
    )
    suspend fun setMarkedForFolders(folderIds: List<Long>, marked: Boolean)

    @Query("DELETE FROM wallpaper_images WHERE path = :path")
    suspend fun deleteImage(path: String)

    @Query("DELETE FROM wallpaper_images WHERE sourceFolderId IN (:folderIds) AND lastSeen < :scanStarted")
    suspend fun deleteStaleImages(folderIds: List<Long>, scanStarted: Long)

    @Query("DELETE FROM wallpaper_images")
    suspend fun deleteAllImages()
}
