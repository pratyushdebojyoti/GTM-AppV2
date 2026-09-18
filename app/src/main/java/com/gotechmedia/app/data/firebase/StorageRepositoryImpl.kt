package com.gotechmedia.app.data.firebase

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.gotechmedia.app.core.utils.Resource
import com.gotechmedia.app.domain.repository.StorageRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

/**
 * Production implementation of StorageRepository backed by Firebase Cloud Storage.
 */
class StorageRepositoryImpl(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : StorageRepository {

    override fun uploadFile(storagePath: String, fileUri: Uri): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val storageRef = storage.reference.child(storagePath)
            val uploadTask = storageRef.putFile(fileUri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            emit(Resource.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to upload file to storage."))
        }
    }.flowOn(ioDispatcher)

    override fun uploadBytes(storagePath: String, bytes: ByteArray, mimeType: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading)
        try {
            val storageRef = storage.reference.child(storagePath)
            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            val uploadTask = storageRef.putBytes(bytes, metadata).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await().toString()
            emit(Resource.Success(downloadUrl))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to upload document bytes."))
        }
    }.flowOn(ioDispatcher)

    override fun deleteFile(storagePath: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading)
        try {
            val storageRef = storage.reference.child(storagePath)
            storageRef.delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Failed to delete storage asset."))
        }
    }.flowOn(ioDispatcher)
}
