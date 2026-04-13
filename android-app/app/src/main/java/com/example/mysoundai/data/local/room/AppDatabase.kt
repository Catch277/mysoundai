package com.example.mysoundai.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [
    DownloadedSong::class,
    FavoriteSongEntity::class,
    PlaylistEntity::class,
    PlaylistSongCrossRef::class
                     ],
          version = 5,
          exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun favoriteDao(): FavoriteDao

    abstract fun playlistDao(): PlaylistDao
    // Tuyệt đối CHƯA viết hàm getDatabase() hay khởi tạo singleton ở đây.
    // Chúng ta sẽ giao việc đó cho AppContainer ở Giai đoạn 3 để kiểm soát luồng.
}