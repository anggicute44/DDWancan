package id.app.ddwancan.worker

import android.content.Context

import androidx.work.*
import androidx.work.NetworkType
import java.util.concurrent.TimeUnit

object SyncScheduler {
    private const val UNIQUE_WORK_NAME = "ddwancan_periodic_sync"

    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
    }
}
