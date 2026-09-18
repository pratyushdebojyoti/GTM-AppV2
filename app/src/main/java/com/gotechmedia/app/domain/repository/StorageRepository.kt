package com.gotechmedia.app.domain.repository

import android.net.Uri
import com.gotechmedia.app.core.utils.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Firebase Storage Repository abstraction.
 * Encapsulates secure binary and asset uploads/downloads for project documents,
 * client attachments, and brand media.
 */
interface StorageRepository {

    /**
     * Upload an asset from a content URI to Firebase Storage and return its public download URL.
     */
    fun uploadFile(storagePath: String, fileUri: Uri): Flow<Resource<String>>

    /**
     * Upload in-memory byte array (e.g. generated reports, logs) and return download URL.
     */
    fun uploadBytes(storagePath: String, bytes: ByteArray, mimeType: String): Flow<Resource<String>>

    /**
     * Remove an asset located at the specified storage path.
     */
    fun deleteFile(storagePath: String): Flow<Resource<Unit>>
}
