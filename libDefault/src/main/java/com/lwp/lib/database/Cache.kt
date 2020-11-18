package com.lwp.lib.database

import androidx.room.*
import com.lwp.lib.APP

@Database(entities = [Cache::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun data(): CacheDao
}

@Entity
data class Cache(
    @PrimaryKey
    var key: String,

    var json: String
)

@Dao
interface CacheDao {
    //查询user表中所有数据
    @get:Query("SELECT * FROM cache")
    val all: List<Cache?>?

    @Query("SELECT * FROM cache WHERE `key` = (:key)")
    fun findData(key: String): Cache?

    @Update
    fun update(data: Cache)

    @Insert
    fun insert(data: Cache)

    @Query("DELETE FROM cache WHERE `key` = (:key)")
    fun delete(key: String)

    @Query("DELETE FROM cache")
    fun deleteAll()
}

val cacheDao: CacheDao by lazy {
    Room.databaseBuilder(
        APP.context,
        AppDatabase::class.java, "cache.db"
    )
        .allowMainThreadQueries() //允许在主线程中查询
        .build().data()
}