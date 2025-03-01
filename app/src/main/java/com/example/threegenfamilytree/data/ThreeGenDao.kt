package com.example.threegenfamilytree.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ThreeGenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: ThreeGen)

    @Update
    suspend fun updateMember(member: ThreeGen)

    @Delete
    suspend fun deleteMember(member: ThreeGen)

    @Query("SELECT * FROM three_gen_table ORDER BY childNumber")
    fun getAllMembers(): Flow<List<ThreeGen>>

    @Query("SELECT * FROM three_gen_table WHERE id = :id LIMIT 1")
    suspend fun getMemberById(id: String): ThreeGen?  // ✅ Suspend function for single fetch


    @Query("SELECT * FROM three_gen_table WHERE id = :memberId LIMIT 1")
    fun getMemberFlowById(memberId: String): Flow<ThreeGen?>
}
