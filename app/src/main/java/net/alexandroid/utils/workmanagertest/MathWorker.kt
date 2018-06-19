package net.alexandroid.utils.workmanagertest

import androidx.work.Data
import androidx.work.Worker
import androidx.work.toWorkData
import net.alexandroid.utils.mylog.MyLog

// Define the parameter keys:
const val KEY_X_ARG = "X"
const val KEY_Y_ARG = "Y"
const val KEY_Z_ARG = "Z"

// ...and the result key:
const val KEY_RESULT = "result"

// Define the Worker class:
class MathWorker : Worker()  {

    override fun doWork(): WorkerResult {
        val x = inputData.getInt(KEY_X_ARG, 0)
        val y = inputData.getInt(KEY_Y_ARG, 0)
        val z = inputData.getInt(KEY_Z_ARG, 0)

        MyLog.d("Input: $x / $y / $z")

        // ...do the math...
        val result = myCrazyMathFunction(x, y, z)

        //...set the output, and we're done!
        val output: Data = mapOf(KEY_RESULT to result).toWorkData()
        outputData = output
        return WorkerResult.SUCCESS
    }

    private fun myCrazyMathFunction(x: Int, y: Int, z: Int): Int {
        return x + y + z;
    }
}