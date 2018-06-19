package net.alexandroid.utils.workmanagertest

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.work.*
import net.alexandroid.utils.mylog.MyLog
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    // https://developer.android.com/topic/libraries/architecture/workmanager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyLog.init(this, "ZAQ")

        //createOneTimeWork()
        //createPeriodicWork()
        //createChainedTasks()
        //createSeparateChains()
        //createUniqueWorkChain()
        //createTaggedWork()
        //inputAndReturnedValues()
    }

    private fun createOneTimeWork() {
        MyLog.d("")

        // Create a Constraints that defines when the task should run
        val myConstraints = Constraints.Builder()
                //.setRequiresDeviceIdle(true) // Api 23
                .setRequiresCharging(true)
                .setRequiresBatteryNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .build()

        val someWork = OneTimeWorkRequestBuilder<OneTimeWorker>()
                .setConstraints(myConstraints)
                .build()

        WorkManager.getInstance().getStatusById(someWork.id)
                .observe(this, Observer { workStatus ->
                    MyLog.d("One time work status: " + workStatus?.state)
                    if (workStatus != null && workStatus.state.isFinished) {
                        MyLog.d("One time work is finished")
                    }
                })

        WorkManager.getInstance().enqueue(someWork)


        // Canceling work code:
        //WorkManager.getInstance().cancelWorkById(someWork.id)
    }

    private fun createPeriodicWork() {
        val periodicWork =
                PeriodicWorkRequestBuilder<PeriodicWorker>(1, TimeUnit.MINUTES).build()
        WorkManager.getInstance().enqueue(periodicWork)

        WorkManager.getInstance().getStatusById(periodicWork.id)
                .observe(this, Observer { workStatus ->
                    MyLog.d("Periodic work status: " + workStatus?.state)
                    if (workStatus != null && workStatus.state.isFinished) {
                        MyLog.d("Periodic work is finished")
                    }
                })
    }

    private fun createChainedTasks() {

        val workA = OneTimeWorkRequestBuilder<WorkA>().build()
        val workB = OneTimeWorkRequestBuilder<WorkB>().build()
        val workC = OneTimeWorkRequestBuilder<WorkC>().build()

        WorkManager.getInstance()
                .beginWith(workA)
                .then(workB)
                .then(workC)
                .enqueue()
    }

    private fun createSeparateChains() {
        val workA = OneTimeWorkRequestBuilder<WorkA>().build()
        val workB = OneTimeWorkRequestBuilder<WorkB>().build()
        val workC = OneTimeWorkRequestBuilder<WorkC>().build()
        val workD = OneTimeWorkRequestBuilder<WorkD>().build()
        val workE = OneTimeWorkRequestBuilder<WorkE>().build()
        val chain1 = WorkManager.getInstance()
                .beginWith(workA)
                .then(workB)
        val chain2 = WorkManager.getInstance()
                .beginWith(workC)
                .then(workD)
        val chain3 = WorkContinuation
                .combine(chain1, chain2)
                .then(workE)
        chain3.enqueue()
    }

    private fun createUniqueWorkChain() {
        val workA = OneTimeWorkRequestBuilder<WorkA>().build()
        val workB = OneTimeWorkRequestBuilder<WorkB>().build()
        val workC = OneTimeWorkRequestBuilder<WorkC>().build()

        WorkManager.getInstance()
                .beginUniqueWork("UniqueWorkName", ExistingWorkPolicy.REPLACE, workA)
                .then(workB)
                .then(workC)
                .enqueue()

        // Cancel the existing sequence and replace it with the new one
        // Keep the existing sequence and ignore your new request
        // Append your new sequence to the existing one, running the new sequence's first task after
        // the existing sequence's last task finish
    }

    private fun createTaggedWork() {
        val someWork = OneTimeWorkRequestBuilder<OneTimeWorker>()
                .addTag("someTag")
                .build()
        WorkManager.getInstance().enqueue(someWork)

        //Cancel all work by tag
        //WorkManager.getInstance().cancelAllWorkByTag("someTag")

        WorkManager.getInstance().getStatusesByTag("someTag").observe(this, Observer { list ->
            list?.forEach {
                MyLog.d("One time work status: " + it?.state)
            }
        })

    }

    private fun inputAndReturnedValues() {
        val myData: Data = mapOf(KEY_X_ARG to 1, KEY_Y_ARG to 2, KEY_Z_ARG to 3).toWorkData()

        val mathWork = OneTimeWorkRequestBuilder<MathWorker>()
                .setInputData(myData)
                .build()

        WorkManager.getInstance().getStatusById(mathWork.id)
                .observe(this, Observer { status ->
                    if (status != null && status.state.isFinished) {
                        val myResult = status.outputData.getInt(KEY_RESULT, -1)
                        MyLog.d("Result: $myResult")
                    }
                })

        WorkManager.getInstance().enqueue(mathWork)
        /*
        If you chain tasks, the outputs from one task are available as inputs to the next task in the chain. I
        f it's a simple chain, with a single OneTimeWorkRequest followed by another single OneTimeWorkRequest,
        the first task returns its result by calling setOutputData(), and the next task fetches that result
        by calling getInputData().
        If the chain is more complicated—for example, because several tasks all send output to a single
        following task—you can define an InputMerger on the OneTimeWorkRequest.Builder to specify what
        should happen if different tasks return an output with the same key.
         */
    }
}
