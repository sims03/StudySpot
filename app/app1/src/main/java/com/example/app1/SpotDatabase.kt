package com.example.app1

import android.content.Context
import androidx.room.*

@Entity(tableName = "spots")
data class SpotEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val email: String,
    val phone: String,
    val website: String,
    val categories: String,
    val noise: String = "Средно",
    val hasWifi: Boolean = false,
    val hasOutlets: Boolean = false,
    val hasCoffee: Boolean = false,
    val hasFood: Boolean = false,
    val hasParking: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0
)

@Dao
interface SpotDao {
    @Query("SELECT * FROM spots")
    fun getAll(): List<SpotEntity>

    @Insert
    fun insert(spot: SpotEntity)

    @Query("UPDATE spots SET rating = :rating, reviewCount = :reviewCount WHERE id = :id")
    fun updateRating(id: Int, rating: Float, reviewCount: Int)
}

@Database(entities = [SpotEntity::class], version = 2)
abstract class SpotDatabase : RoomDatabase() {
    abstract fun spotDao(): SpotDao

    companion object {
        @Volatile private var INSTANCE: SpotDatabase? = null

        fun get(context: Context): SpotDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, SpotDatabase::class.java, "spots_db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
        }
    }
}