package com.example.util.simpletimetracker.domain.resolver

interface BackupRepo {

    suspend fun saveBackupFile(uriString: String): ResultCode

    suspend fun restoreBackupFile(uriString: String): ResultCode
}