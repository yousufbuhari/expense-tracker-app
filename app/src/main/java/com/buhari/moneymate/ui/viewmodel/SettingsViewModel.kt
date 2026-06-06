package com.buhari.moneymate.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buhari.moneymate.data.entity.Transaction
import com.buhari.moneymate.data.entity.UserProfile
import com.buhari.moneymate.data.local.AppDatabase
import com.buhari.moneymate.data.repository.TransactionRepository
import com.buhari.moneymate.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository by lazy {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        TransactionRepository(dao)
    }

    private val userProfileRepository: UserProfileRepository by lazy {
        val dao = AppDatabase.getDatabase(application).userProfileDao()
        UserProfileRepository(dao)
    }

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            userProfileRepository.userProfile.collect { profile ->
                if (profile != null) {
                    _userProfile.value = profile
                } else {
                    // Initial insert if not exists
                    userProfileRepository.updateProfile(UserProfile())
                }
            }
        }
    }

    fun updateProfile(name: String, profileImageUri: String?) {
        viewModelScope.launch {
            var finalImagePath = profileImageUri
            
            // If it's a new URI (not already in our internal storage), copy it
            if (profileImageUri != null && !profileImageUri.contains(getApplication<Application>().filesDir.path)) {
                finalImagePath = saveImageToInternalStorage(profileImageUri)
            }

            val updatedProfile = _userProfile.value.copy(name = name, profileImage = finalImagePath)
            userProfileRepository.updateProfile(updatedProfile)
        }
    }

    private suspend fun saveImageToInternalStorage(uriString: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val uri = Uri.parse(uriString)
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.filesDir, "profile_image_${System.currentTimeMillis()}.jpg")
                
                // Delete old profile images if they exist to save space
                context.filesDir.listFiles { _, name -> name.startsWith("profile_image_") }?.forEach { it.delete() }

                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                Uri.fromFile(file).toString()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            val updatedProfile = _userProfile.value.copy(theme = theme)
            userProfileRepository.updateProfile(updatedProfile)
        }
    }

    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.deleteAllTransactions()
            onComplete()
        }
    }

    fun exportDataToCsv(context: Context, uri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val transactions = repository.allTransactions.first()
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        OutputStreamWriter(outputStream).use { writer ->
                            // Header
                            writer.write("uuid,title,amount,date,category,isExpense,icon,description\n")
                            transactions.forEach { t ->
                                val line =
                                    "${t.uuid},${escapeCsv(t.title)},${t.amount},${t.date},${escapeCsv(t.category)},${t.isExpense},${t.icon},${
                                        escapeCsv(t.description ?: "")
                                    }\n"
                                writer.write(line)
                            }
                        }
                    }
                }
                onComplete(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

    fun importDataFromCsv(context: Context, uri: Uri, onComplete: (Boolean, Int, Int) -> Unit) {
        viewModelScope.launch {
            try {
                var addedCount = 0
                var duplicateCount = 0
                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        BufferedReader(InputStreamReader(inputStream)).use { reader ->
                            val header = reader.readLine() // Skip header
                            var line: String? = reader.readLine()
                            while (line != null) {
                                val parts = parseCsvLine(line)
                                if (parts.size >= 7) {
                                    val hasUuid = parts.size >= 8
                                    val transaction = Transaction(
                                        uuid = if (hasUuid) parts[0] else java.util.UUID.randomUUID().toString(),
                                        title = if (hasUuid) parts[1] else parts[0],
                                        amount = (if (hasUuid) parts[2] else parts[1]).toDoubleOrNull() ?: 0.0,
                                        date = (if (hasUuid) parts[3] else parts[2]).toLongOrNull()
                                            ?: System.currentTimeMillis(),
                                        category = if (hasUuid) parts[4] else parts[3],
                                        isExpense = (if (hasUuid) parts[5] else parts[4]).toBoolean(),
                                        icon = (if (hasUuid) parts[6] else parts[5]).toIntOrNull() ?: 0,
                                        description = if (hasUuid) {
                                            if (parts.size > 7) parts[7] else null
                                        } else {
                                            if (parts.size > 6) parts[6] else null
                                        }
                                    )
                                    val id = repository.insertTransaction(transaction)
                                    if (id == -1L) {
                                        duplicateCount++
                                    } else {
                                        addedCount++
                                    }
                                }
                                line = reader.readLine()
                            }
                        }
                    }
                }
                onComplete(true, addedCount, duplicateCount)
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, 0, 0)
            }
        }
    }

    private fun escapeCsv(value: String): String {
        return if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            "\"" + value.replace("\"", "\"\"") + "\""
        } else {
            value
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (inQuotes) {
                if (c == '\"') {
                    if (i + 1 < line.length && line[i + 1] == '\"') {
                        current.append('\"')
                        i++
                    } else {
                        inQuotes = false
                    }
                } else {
                    current.append(c)
                }
            } else {
                if (c == '\"') {
                    inQuotes = true
                } else if (c == ',') {
                    result.add(current.toString())
                    current = StringBuilder()
                } else {
                    current.append(c)
                }
            }
            i++
        }
        result.add(current.toString())
        return result
    }
}