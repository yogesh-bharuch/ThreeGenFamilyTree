package com.example.threegenfamilytree.repository

import android.util.Log
import com.example.threegenfamilytree.data.ThreeGen
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ThreeGenFirestoreRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("ThreeGenMembers")

    // Add new member to Firestore
    suspend fun addMemberToFirestore(member: ThreeGen): Result<Unit> {
        return try {
            collectionRef.document(member.id).set(member).await()
            Log.d("Firestore", "Member added: ${member.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firestore", "Error adding member", e)
            Result.failure(e)
        }
    }

    // Update specific fields of a member (prevents overwriting the entire document)
    suspend fun updateMemberInFirestore(memberId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            collectionRef.document(memberId).update(updates).await()
            Log.d("Firestore", "Member updated: $memberId with $updates")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating member", e)
            Result.failure(e)
        }
    }

    // Delete a member from Firestore
    suspend fun deleteMemberFromFirestore(memberId: String): Result<Unit> {
        return try {
            collectionRef.document(memberId).delete().await()
            Log.d("Firestore", "Member deleted: $memberId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting member", e)
            Result.failure(e)
        }
    }
}
