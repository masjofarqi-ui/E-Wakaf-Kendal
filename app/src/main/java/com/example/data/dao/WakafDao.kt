package com.example.data.dao

import androidx.room.*
import com.example.data.model.Nazhir
import com.example.data.model.WakafLand
import kotlinx.coroutines.flow.Flow

@Dao
interface WakafDao {

    // Wakaf Land operations
    @Query("SELECT * FROM wakaf_land_table ORDER BY createdAt DESC")
    fun getAllWakafLands(): Flow<List<WakafLand>>

    @Query("SELECT * FROM wakaf_land_table WHERE id = :id LIMIT 1")
    fun getWakafLandById(id: Long): Flow<WakafLand?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWakafLand(wakafLand: WakafLand): Long

    @Query("DELETE FROM wakaf_land_table WHERE id = :id")
    suspend fun deleteWakafLandById(id: Long)

    // Nazhir operations
    @Query("SELECT * FROM nazhir_table ORDER BY name ASC")
    fun getAllNazhirs(): Flow<List<Nazhir>>

    @Query("SELECT * FROM nazhir_table WHERE kecamatan = :kecamatan ORDER BY name ASC")
    fun getNazhirsByKecamatan(kecamatan: String): Flow<List<Nazhir>>

    @Query("SELECT * FROM nazhir_table WHERE id IN (:ids)")
    suspend fun getNazhirsByIds(ids: List<Long>): List<Nazhir>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNazhir(nazhir: Nazhir): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNazhirs(nazhirs: List<Nazhir>)

    @Query("DELETE FROM nazhir_table WHERE id = :id")
    suspend fun deleteNazhirById(id: Long)
}
