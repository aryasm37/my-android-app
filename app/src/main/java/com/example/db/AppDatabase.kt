package com.example.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "history_items")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // e.g. "URL", "Text", "Wifi"
    val content: String,
    val format: String, // e.g. "QR_CODE", "UPC_A"
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_items ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Insert
    suspend fun insert(item: HistoryItem)

    @Query("DELETE FROM history_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM history_items")
    suspend fun clearAll()
}

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
