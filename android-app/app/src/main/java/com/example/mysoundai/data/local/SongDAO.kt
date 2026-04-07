//package com.example.mysoundai.data.local
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//
//@Dao
//interface SongDao {
//    @Query("SELECT * FROM songs")
//    fun getAllSongs(): List<SongEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSongs(songs: List<SongEntity>)
//}