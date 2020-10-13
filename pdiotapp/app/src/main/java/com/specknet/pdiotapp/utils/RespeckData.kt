package com.specknet.pdiotapp.utils

val zeroRespeckData = RespeckData(
    0,
    0f,
    0f,
    0f,
    0f,
    0f
)

data class RespeckData(val timestamp: Long, val accel_x: Float, val accel_y: Float,
                       val accel_z: Float, val accel_mag: Float, val breathingSignal: Float) {
    override fun toString(): String {
        return "${this.javaClass.name}($timestamp, $accel_x, $accel_y, $accel_z, ...)"
    }
}