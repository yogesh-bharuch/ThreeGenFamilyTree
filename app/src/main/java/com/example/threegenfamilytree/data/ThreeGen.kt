package com.example.threegenfamilytree.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * ThreeGen Entity - Represents a family tree member.
 *
 * - Stored locally in Room DB.
 * - Synced with Firestore for cloud backup.
 */
@Entity(tableName = "three_gen_table")
data class ThreeGen(
    @PrimaryKey val id: String = UUID.randomUUID().toString(), // Unique ID across Room & Firestore
    val parentID: String? = null, // Parent ID (null for root member)
    val spouseID: String? = null, // Spouse ID (null if unmarried)
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val town: String,
    val childNumber: Int, // Order of birth (used for sorting)
    val shortName: String, // Auto-generated short name for quick search
    val imageUri: String? = null, // Optional profile image
    val createdAt: Long = System.currentTimeMillis() // Timestamp when added
)