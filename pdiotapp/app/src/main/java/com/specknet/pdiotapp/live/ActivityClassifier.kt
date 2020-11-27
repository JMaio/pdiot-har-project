package com.specknet.pdiotapp.live

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
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

val defaultClassificationResult = Pair("Activity", 0f)

class ClassificationResults(val list: List<ClassificationResult>) {
    // unsafe if list is empty
    val maxI: Int = list.indices.maxByOrNull { i -> list.map { (_, c) -> c }[i] } ?: 0
    val max: ClassificationResult =
        list.getOrElse(maxI, defaultValue = { defaultClassificationResult })
}

class ActivityClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    var isInitialized = false
        private set

    /** Executor to run inference task in the background */
    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    var windowSize: Int = 0 // will be inferred from TF Lite model
    var modelInputSize: Int = 0 // will be inferred from TF Lite model

    fun initialize(modelFile: String): Task<Void> {
        val task = TaskCompletionSource<Void>()
        executorService.execute {
            try {
                initializeInterpreter(modelFile)
                task.setResult(null)
            } catch (e: IOException) {
                task.setException(e)
            }
        }
        return task.task
    }

    @Throws(IOException::class)
    private fun initializeInterpreter(modelFile: String) {
        // Load the TF Lite model
        val assetManager = context.assets
        val model = loadModelFile(assetManager, modelFile)

        // Initialize TF Lite Interpreter with NNAPI enabled
        val options = Interpreter.Options().apply {
            setUseNNAPI(true)
        }

        val interpreter = Interpreter(model, options)

        // Read input shape from model file
        val inputShape = interpreter.getInputTensor(0).shape()
        // looks like [1, 100, 3] - [1][window size][xyz]
        windowSize = inputShape[1]
        // modelInputSize = windowSize * 3 floats * 4 bytes / float
        modelInputSize = windowSize * XYZ_TYPE_SIZE * FLOAT_TYPE_SIZE

        // Finish interpreter initialization
        this.interpreter = interpreter
        isInitialized = true
        Log.i(
            TAG,
            "Initialized TFLite interpreter with model '$modelFile'; input shape = ${inputShape.map { i -> i.toString() }}"
        )
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelFile: String): ByteBuffer {
        val fileDescriptor = assetManager.openFd(MODEL_DIR + modelFile)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun classify(data: List<RespeckData>): ClassificationResults {
        if (!isInitialized) {
            throw IllegalStateException("TF Lite Interpreter is not initialized yet.")
        }
        Log.d(TAG, "Starting classification...")

        val byteBuffer = respeckDataToModelInput(data)

        var elapsedTime: Long
        var startTime = System.nanoTime()
        val result = Array(1) { FloatArray(OUTPUT_ACTIVITIES_COUNT) }
        // run interpreter, return results to array
        interpreter?.run(byteBuffer, result)
        elapsedTime = (System.nanoTime() - startTime) / 1000000
        Log.d(TAG, "Inference time = " + elapsedTime + "ms")

        return ClassificationResults(result[0].mapIndexed { i, f ->
            Log.d(TAG, "inference: ${Constants.AVAILABLE_ACTIVITIES[i]} => $f")
            ClassificationResult(
                Constants.AVAILABLE_ACTIVITIES[i],
//                Constants.ACTIVITY_CODE_TO_NAME_MAPPING
//                    .getOrDefault(Constants.TFCODE_TO_ACTIVITY_CODE[i], "Unknown"),
                f
            )
        })
    }

    fun respeckDataToModelInput(data: List<RespeckData>): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(modelInputSize)
        byteBuffer.order(ByteOrder.nativeOrder())
        byteBuffer.apply {
            data.map { d ->
                putFloat(d.accel_x)
                putFloat(d.accel_y)
                putFloat(d.accel_z)
            }
        }
        return byteBuffer
    }

    fun classifyAsync(data: List<RespeckData>): Task<ClassificationResults> {
        val task = TaskCompletionSource<ClassificationResults>()
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

    companion object {
        private const val TAG = "ActivityClassifier"



//        private const val FLOAT_TYPE_SIZE = 4
//        private const val PIXEL_SIZE = 1

        // floats are 4 bytes!
        private const val FLOAT_TYPE_SIZE = 4

        // TODO: update depending on time window duration?
        private const val XYZ_TYPE_SIZE = 3

        const val OUTPUT_ACTIVITIES_COUNT = 14
        val OUTPUT_CLASSES_COUNT = Constants.ACTIVITY_CATEGORIES.size

        const val MODEL_DIR = "models/"
//        private const val MODEL_FILE = MODEL_DIR + "cnn_model_grouped_step50_1_Chest_Right.tflite"
    }
}
