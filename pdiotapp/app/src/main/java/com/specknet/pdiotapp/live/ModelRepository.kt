package com.specknet.pdiotapp.live

import com.specknet.pdiotapp.utils.Constants
import com.specknet.pdiotapp.utils.RespeckData
import com.specknet.pdiotapp.utils.RespeckDataRaw
import com.specknet.pdiotapp.utils.toRespeckDataRaw

interface ModelDataTransform<T, out R> {
    // apply the transformation with input T, output R
    fun apply(model: InferenceModel, data: T): R
}

object RespeckDataToFloat : ModelDataTransform<List<RespeckData>, List<RespeckDataRaw>> {
    override fun apply(model: InferenceModel, data: List<RespeckData>): List<RespeckDataRaw> =
        data.map { d -> d.toRespeckDataRaw() }
}

// 3 lists (x, y, z) of 2D FFT <List<List<Float>> output (ws * resolution * sample)
typealias RespeckDataFFTOutput = List<List<List<Float>>>

object FFTTransform : ModelDataTransform<List<RespeckDataRaw>, RespeckDataFFTOutput> {
    override fun apply(model: InferenceModel, data: List<RespeckDataRaw>): RespeckDataFFTOutput =
//        runBlocking {
        // fft already performed in a task
        DataProcessor.fft(model, data)
//        return DataProcessor.fft(model, data)
//        GlobalScope.launch {
//        }.
//        return awaitAll(DataProcessor.fftAsync(model, data))
//        }
}

//data class ModelTransforms(val fft: Boolean = false)

abstract class InferenceModel(
    val filename: String,
    val ws: Int = 100,
//    val transforms: List<ModelDataTransform<*, *>>
    val fftWs: Int? = 25,
    val fftStep: Int? = 15,
    val outputClasses: List<String>,
    // (0,25) -> (15, 40) -> (30, 55),
    // (45,70) -> (60, 85) -> (75, 100)
    //
    // (0,24) -> (15, 39) -> (30, 54),
    // (45,69) -> (60, 84) -> (75, 99)
) {
    // inference models return 1D samples after transforming data
    abstract fun applyTransforms(data: List<RespeckData>): List<Float>
}

object ModelRepository {
    // manually input the available models
    private val inferenceModelsList = listOf<InferenceModel>(
        object : InferenceModel(
            "cnn_model_fft4_Chest_Right.tflite",
            fftWs = 25,
            fftStep = 15, // interval = 10
            outputClasses = Constants.ACTIVITY_CATEGORIES,
        ) {

            override fun applyTransforms(data: List<RespeckData>): List<Float> = data.let {
                RespeckDataToFloat.apply(this, it)
            }.let {
                FFTTransform.apply(this, it)
            }.flatten().flatten()
        },

        object : InferenceModel(
            "cnn_model_window24_2_Chest_Right.tflite",
            ws = 100, // data window size
            fftWs = 24, // input window size after fft
            fftStep = 15, // interval = 10
            outputClasses = Constants.ACTIVITY_CATEGORIES,
        ) {

            override fun applyTransforms(data: List<RespeckData>): List<Float> = data.let {
                RespeckDataToFloat.apply(this, it)
            }.let {
                FFTTransform.apply(this, it)
            }.flatten().flatten()
        },

        )

    val inferenceModelsMap: Map<String, InferenceModel> =
        inferenceModelsList.map { it.filename to it }.toMap()

    val defaultModel =
        inferenceModelsMap["cnn_model_window24_2_Chest_Right.tflite"] ?: inferenceModelsList.first()

    operator fun get(name: String) = inferenceModelsMap[name]
}