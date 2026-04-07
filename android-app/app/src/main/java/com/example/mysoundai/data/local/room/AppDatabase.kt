package com.example.mysoundai.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DownloadedSong::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao

    // Tuyệt đối CHƯA viết hàm getDatabase() hay khởi tạo singleton ở đây.
    // Chúng ta sẽ giao việc đó cho AppContainer ở Giai đoạn 3 để kiểm soát luồng.
}