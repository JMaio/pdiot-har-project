package com.specknet.pdiotapp.live

import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RespeckData
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// using tensorflow demo as reference:
// https://github.com/tensorflow/examples/blob/master/lite/examples/digit_classifier/android/app/src/main/java/org/tensorflow/lite/examples/digitclassifier/DigitClassifier.kt

typealias ClassificationResult = Pair<String, Float>

class ActivityClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    var isInitialized = false
        private set

    /** Executor to run inference task in the background */
    private val executorService: ExecutorService = Executors.newCachedThreadPool()

//    private var x: Float = 0f // will be inferred from TF Lite model
//    private var y: Float = 0f // will be inferred from TF Lite model
//    private var z: Float = 0f // will be inferred from TF Lite model

    fun initialize(): Task<Void> {
        val task = TaskCompletionSource<Void>()
        executorService.execute {
            try {
                initializeInterpreter()
                task.setResult(null)
            } catch (e: IOException) {
                task.setException(e)
            }
        }
        return task.task
    }

    @Throws(IOException::class)
    private fun initializeInterpreter() {
        // Load the TF Lite model
        val assetManager = context.assets
        val model = loadModelFile(assetManager)

        // Initialize TF Lite Interpreter with NNAPI enabled
        val options = Interpreter.Options().apply {
            setUseNNAPI(true)
        }

        val interpreter = Interpreter(model, options)

        // Read input shape from model file
//        val inputShape = interpreter.getInputTensor(0).shape()

        // Finish interpreter initialization
        this.interpreter = interpreter
        isInitialized = true
        Log.d(TAG, "Initialized TFLite interpreter.")
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager): ByteBuffer {
        val fileDescriptor = assetManager.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun classify(data: RespeckData): ClassificationResult {
        if (!isInitialized) {
            throw IllegalStateException("TF Lite Interpreter is not initialized yet.")
        }
        Log.i(TAG, "Starting classification...")

        val byteBuffer = respeckDataToModelInput(data)

        var startTime: Long
        var elapsedTime: Long
//
//        // Preprocessing: resize the input
//        startTime = System.nanoTime()
//        val resizedImage = Bitmap.createScaledBitmap(bitmap, inputImageWidth, inputImageHeight, true)
//        val byteBuffer = convertBitmapToByteBuffer(resizedImage)
//        elapsedTime = (System.nanoTime() - startTime) / 1000000
//        Log.d(TAG, "Preprocessing time = " + elapsedTime + "ms")

        startTime = System.nanoTime()
        val result = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }
        // run interpreter, return results to array
        interpreter?.run(byteBuffer, result)
        elapsedTime = (System.nanoTime() - startTime) / 1000000
        Log.d(TAG, "Inference time = " + elapsedTime + "ms")
//
        val output = result[0]
        val maxIndex = output.indices.maxBy { output[it] } ?: -1
        val activityCode: Int? = Constants.TFCODE_TO_ACTIVITY_CODE[maxIndex]
        val activityName: String = Constants.ACTIVITY_CODE_TO_NAME_MAPPING.getOrDefault(activityCode, "Unknown")
        return Pair(
            activityName,
            output[maxIndex]
        )
//        return getOutputString(result[0])
    }

    fun respeckDataToModelInput(data: RespeckData): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(FLOAT_TYPE_SIZE * MODEL_INPUT_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())
        // acceleration data might be out of bounds? not the right format?
        byteBuffer.apply {
            putFloat(data.accel_x)
            putFloat(data.accel_y)
            putFloat(data.accel_z)
        }
        return byteBuffer
    }

    fun classifyAsync(data: RespeckData): Task<ClassificationResult> {
        val task = TaskCompletionSource<ClassificationResult>()
        executorService.execute {
            val result = classify(data)
            task.setResult(result)
        }
        return task.task
    }

    fun close() {
        executorService.execute {
            interpreter?.close()
            Log.d(TAG, "Closed TFLite interpreter.")
        }
    }

    private fun getOutputString(output: FloatArray): String {
        val maxIndex = output.indices.maxBy { output[it] } ?: -1
        return "%s\nConfidence: %2f".format(maxIndex, output[maxIndex])
    }

    companion object {
        private const val TAG = "ActivityClassifier"

        private const val MODEL_FILE = "har_v0.0.tflite"


//        private const val FLOAT_TYPE_SIZE = 4
//        private const val PIXEL_SIZE = 1

        // floats are 4 bytes!
        private const val FLOAT_TYPE_SIZE = 4
        // TODO: update depending on time window duration
        private const val MODEL_INPUT_SIZE = 3

        private const val OUTPUT_CLASSES_COUNT = 14
    }
}