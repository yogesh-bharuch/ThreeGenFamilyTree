package com.example.threegenfamilytree.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import com.example.threegenfamilytree.data.ThreeGen
import com.example.threegenfamilytree.repository.ThreeGenFirestoreRepository
import com.example.threegenfamilytree.repository.ThreeGenRepository
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ThreeGenViewModel(
    application: Application,
    private val repository: ThreeGenRepository,
    private val firestoreRepository: ThreeGenFirestoreRepository
) : AndroidViewModel(application) {

    val allMembers: Flow<List<ThreeGen>> = repository.allMembers
    val allMembersLiveData: LiveData<List<ThreeGen>> = allMembers.asLiveData()

    fun updateMember(member: ThreeGen) {
        viewModelScope.launch {
            repository.updateMember(member) // Update locally in Room

            // Ensure all fields conform to Firestore's `Map<String, Any>` requirement
            val updates: Map<String, Any> = mapOf(
                "firstName" to member.firstName,
                "middleName" to member.middleName,
                "lastName" to member.lastName,
                "town" to member.town,
                "parentID" to (member.parentID ?: "") as Any,  // Cast explicitly to Any
                "spouseID" to (member.spouseID ?: "") as Any,  // Cast explicitly to Any
                "childNumber" to member.childNumber,
                "shortName" to member.shortName,
                "imageUri" to (member.imageUri ?: "") as Any  // Ensure null safety
            )

            firestoreRepository.updateMemberInFirestore(member.id, updates)
        }
    }


    fun deleteMember(member: ThreeGen) {
        viewModelScope.launch {
            repository.deleteMember(member)
            firestoreRepository.deleteMemberFromFirestore(member.id)
        }
    }

    fun getMemberById(memberId: String): Flow<ThreeGen?> {
        return repository.getMemberById(memberId)
    }

    private val _members = mutableStateListOf<ThreeGen>()
    val members: List<ThreeGen> get() = _members

    fun addMember(
        firstName: String,
        middleName: String,
        lastName: String,
        town: String,
        parentID: String? = null,
        spouseID: String? = null
    ) {
        if (firstName.isBlank() || middleName.isBlank() || lastName.isBlank() || town.isBlank()) {
            return
        }

        val existingCount = getMemberCountByName(firstName, middleName, lastName, town)
        val shortName = generateShortName(firstName, middleName, lastName, town, existingCount + 1)

        val newMember = ThreeGen(
            id = UUID.randomUUID().toString(),
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            town = town,
            parentID = parentID,
            spouseID = spouseID,
            childNumber = 0,
            shortName = shortName,
            imageUri = null // Initialize image as null
        )

        viewModelScope.launch {
            repository.addMember(newMember)
            firestoreRepository.addMemberToFirestore(newMember)
        }

        _members.add(newMember)
    }

    private fun getMemberCountByName(
        firstName: String,
        middleName: String,
        lastName: String,
        town: String
    ): Int {
        return _members.count {
            it.firstName == firstName && it.middleName == middleName &&
                    it.lastName == lastName && it.town == town
        }
    }

    private fun generateShortName(
        firstName: String,
        middleName: String,
        lastName: String,
        town: String,
        count: Int
    ): String {
        return "${firstName.first().uppercase()}${middleName.first().uppercase()}${
            lastName.first().uppercase()
        }${town.first().uppercase()}$count"
    }

    fun uploadImageAndSaveUri(context: Context, member: ThreeGen, imageUri: Uri) {
        val storageRef = Firebase.storage.reference.child("profile_images/${member.id}.jpg")
        Log.d("FirebaseStorage", "Uploading to: ${storageRef.path}")
        Log.d("RoomDB", "Image URI: $imageUri")
        viewModelScope.launch {
            val updatedMember = member.copy(imageUri = imageUri.toString())
            Log.d("RoomDB", "Updated image URI: $imageUri")

            try {
                storageRef.putFile(imageUri).await()
                val downloadUri = storageRef.downloadUrl.await().toString()
                val updatedMember = member.copy(imageUri = downloadUri)
                Log.d("RoomDB", "Updated image URI: ${updatedMember.imageUri}")

                // ✅ Update in Room
                repository.updateMember(updatedMember)
                val updated = repository.getMemberById(member.id).firstOrNull()
                Log.d("RoomDB", "Updated image URI: ${updated?.imageUri}") // ✅ Debug


                // ✅ Update in Firestore
                val updates = mapOf("imageUri" to updatedMember.imageUri as Any)
                firestoreRepository.updateMemberInFirestore(updatedMember.id, updates)

                Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            /*try {
                // Upload the image and ensure it completes
                val uploadTaskSnapshot = storageRef.putFile(imageUri).await()
                Log.d("FirebaseStorage", "Upload successful for: ${storageRef.path}")

                // Fetch download URL after successful upload
                val downloadUri = storageRef.downloadUrl.await()
                Log.d("FirebaseStorage", "Download URL retrieved: $downloadUri")

                // Update local database and Firestore
                val updatedMember = member.copy(imageUri = downloadUri.toString())
                repository.updateMember(updatedMember)

                val updates = mapOf("imageUri" to (updatedMember.imageUri ?: "") as Any)
                firestoreRepository.updateMemberInFirestore(updatedMember.id, updates)

                Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("FirebaseStorage", "Image upload failed: ${e.message}")
                Toast.makeText(context, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } */
        }
    }
}


class ThreeGenViewModelFactory(
    private val application: Application,
    private val repository: ThreeGenRepository,
    private val firestoreRepository: ThreeGenFirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThreeGenViewModel::class.java)) {
            return ThreeGenViewModel(application, repository, firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



/*

package com.example.threegenfamilytree.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.*
import com.example.threegenfamilytree.data.ThreeGen
import com.example.threegenfamilytree.repository.ThreeGenFirestoreRepository
import com.example.threegenfamilytree.repository.ThreeGenRepository
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class ThreeGenViewModel(
    application: Application,
    private val repository: ThreeGenRepository,
    private val firestoreRepository: ThreeGenFirestoreRepository // Accept Firestore Repository
) : AndroidViewModel(application) {

    val allMembers: Flow<List<ThreeGen>> = repository.allMembers
    // Convert Flow to LiveData for Compose UI
    val allMembersLiveData: LiveData<List<ThreeGen>> = allMembers.asLiveData()

    fun updateMember(member: ThreeGen) {
        viewModelScope.launch {
            repository.updateMember(member)
            firestoreRepository.updateMemberInFirestore(member) // Sync to Firestore
        }
    }

    fun deleteMember(member: ThreeGen) {
        viewModelScope.launch {
            repository.deleteMember(member)
            firestoreRepository.deleteMemberFromFirestore(member.id) // Delete from Firestore
        }
    }

    fun getMemberById(memberId: String): Flow<ThreeGen?> {
        return repository.getMemberById(memberId)
    }

    private val _members = mutableStateListOf<ThreeGen>()
    val members: List<ThreeGen> get() = _members

    fun addMember(
        firstName: String,
        middleName: String,
        lastName: String,
        town: String,
        parentID: String? = null,  // Allow optional parent ID
        spouseID: String? = null    // Allow optional spouse ID
    ) {
        if (firstName.isBlank() || middleName.isBlank() || lastName.isBlank() || town.isBlank()) {
            // Validate mandatory fields
            return
        }

        val existingCount = getMemberCountByName(firstName, middleName, lastName, town)
        val shortName = generateShortName(firstName, middleName, lastName, town, existingCount + 1)

        val newMember = ThreeGen(
            id = UUID.randomUUID().toString(),
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            town = town,
            parentID = parentID,
            spouseID = spouseID,
            childNumber = 0,
            shortName = shortName
        )

        viewModelScope.launch {
            repository.addMember(newMember)  // Save to Room
            firestoreRepository.addMemberToFirestore(newMember) // Sync with Firestore
        }

        _members.add(newMember) // Add to list
    }

    private fun getMemberCountByName(
        firstName: String,
        middleName: String,
        lastName: String,
        town: String
    ): Int {
        return _members.count {
            it.firstName == firstName && it.middleName == middleName &&
                    it.lastName == lastName && it.town == town
        }
    }

    private fun generateShortName(
        firstName: String,
        middleName: String,
        lastName: String,
        town: String,
        count: Int
    ): String {
        return "${firstName.first().uppercase()}${middleName.first().uppercase()}${
            lastName.first().uppercase()
        }${town.first().uppercase()}$count"
    }

    fun uploadImageAndSaveUri(context: Context, member: ThreeGen, imageUri: Uri) {
        val storageRef = Firebase.storage.reference.child("profile_images/${member.id}.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val updatedMember = member.copy(imageUri = downloadUri.toString())

                    // Save image URI to Room (Local Database)
                    viewModelScope.launch { repository.updateMember(updatedMember) }

                    // Save image URI to Firestore (Cloud Database)
                    firestoreRepository.updateMemberInFirestore(updatedMember)
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

}


class ThreeGenViewModelFactory(
    private val application: Application,
    private val repository: ThreeGenRepository,
    private val firestoreRepository: ThreeGenFirestoreRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ThreeGenViewModel::class.java)) {
            return ThreeGenViewModel(application, repository, firestoreRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


 */