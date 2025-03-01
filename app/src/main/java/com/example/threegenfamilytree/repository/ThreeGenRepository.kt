package com.example.threegenfamilytree.repository

import com.example.threegenfamilytree.data.ThreeGen
import com.example.threegenfamilytree.data.ThreeGenDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * Repository to manage data from Room and Firestore.
 */
class ThreeGenRepository(private val dao: ThreeGenDao) {

    val allMembers: Flow<List<ThreeGen>> = dao.getAllMembers()

    suspend fun addMember(member: ThreeGen) {
        dao.insertMember(member)
    }

    suspend fun updateMember(member: ThreeGen) {
        dao.updateMember(member)
    }

    suspend fun deleteMember(member: ThreeGen) {
        dao.deleteMember(member)
    }

    /**
     * Use Flow for continuous observation.
     */
    fun getMemberById(memberId: String): Flow<ThreeGen?> {
        return dao.getMemberFlowById(memberId)  // ✅ Correct method for Flow-based retrieval
    }

    /**
     * Fetch a single member using suspend function (for one-time retrieval).
     */
    suspend fun fetchMemberById(id: String): ThreeGen? {
        return dao.getMemberById(id)  // ✅ Correct method for single fetch
    }
}
