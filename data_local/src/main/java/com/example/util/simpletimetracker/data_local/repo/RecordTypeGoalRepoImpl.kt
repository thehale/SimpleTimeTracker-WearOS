package com.example.util.simpletimetracker.data_local.repo

import com.example.util.simpletimetracker.data_local.database.RecordTypeGoalDao
import com.example.util.simpletimetracker.data_local.mapper.RecordTypeGoalDataLocalMapper
import com.example.util.simpletimetracker.domain.model.RecordTypeGoal
import com.example.util.simpletimetracker.domain.repo.RecordTypeGoalRepo
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class RecordTypeGoalRepoImpl @Inject constructor(
    private val dao: RecordTypeGoalDao,
    private val mapper: RecordTypeGoalDataLocalMapper,
) : RecordTypeGoalRepo {

    override suspend fun getAll(): List<RecordTypeGoal> = withContext(Dispatchers.IO) {
        Timber.d("getAll")
        dao.getAll().map(mapper::map)
    }

    override suspend fun getByType(typeId: Long): List<RecordTypeGoal> = withContext(Dispatchers.IO) {
        Timber.d("getByType")
        dao.getByType(typeId)
            .map(mapper::map)
    }

    override suspend fun add(recordTypeGoal: RecordTypeGoal): Long = withContext(Dispatchers.IO) {
        Timber.d("add")
        return@withContext dao.insert(
            recordTypeGoal.let(mapper::map),
        )
    }

    override suspend fun remove(id: Long) = withContext(Dispatchers.IO) {
        Timber.d("remove")
        dao.delete(id)
    }

    override suspend fun removeByType(typeId: Long) = withContext(Dispatchers.IO) {
        Timber.d("removeByType")
        dao.deleteByType(typeId)
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        Timber.d("clear")
        dao.clear()
    }
}