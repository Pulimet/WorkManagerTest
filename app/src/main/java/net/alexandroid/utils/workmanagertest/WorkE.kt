package net.alexandroid.utils.workmanagertest

import androidx.work.Worker
import net.alexandroid.utils.mylog.MyLog

class WorkE : Worker()  {

    override fun doWork(): WorkerResult {
        MyLog.d("E")

        doTask()

        return WorkerResult.SUCCESS // Indicate success or failure with your return value:
        // (Returning RETRY tells WorkManager to try this task again later; FAILURE says not to try again.)
    }

    private fun doTask() {

    }

}