package id.app.ddwancan.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import id.app.ddwancan.data.repository.NewsRepository

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            val repo = NewsRepository(applicationContext)
            // userId could be passed via inputData if needed; keep null for now
            repo.syncPending(null)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
