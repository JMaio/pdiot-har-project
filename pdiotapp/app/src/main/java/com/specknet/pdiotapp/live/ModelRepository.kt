package com.specknet.pdiotapp.live

import com.specknet.pdiotapp.utils.Constants

//data class ModelTransforms(val fft: Boolean = false)

class Model(
    val name: String,
    val ws: Int = 100,
    val outputClasses: List<String>,
    val fft: Boolean = false,
) {
//    fun <T> applyTransforms(data: T) {
//
//    }
}

object ModelRepository {
    val modelList = listOf<Model>(
        Model("cnn_model_fft4_Chest_Right.tflite", outputClasses = Constants.ACTIVITY_CATEGORIES)
    )
    val modelsMap: Map<String, Model> = modelList.map { it.name to it }.toMap()

    operator fun get(name: String) = modelsMap[name]
}