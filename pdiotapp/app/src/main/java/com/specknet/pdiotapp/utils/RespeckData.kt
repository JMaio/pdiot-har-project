package com.specknet.pdiotapp.utils

val zeroRespeckData = RespeckData(
    0,
    0f,
    0f,
    0f,
    0f,
    0f
)

typealias RespeckDataRawFloat = List<List<Float>>

data class RespeckData(val timestamp: Long, val accel_x: Float, val accel_y: Float,
                       val accel_z: Float, val accel_mag: Float, val breathingSignal: Float) {
    override fun toString(): String {
        return "${this.javaClass.name}($timestamp, $accel_x, $accel_y, $accel_z, ...)"
    }
}

// extend RespeckData class without modification
fun RespeckData.toRespeckDataRaw() = RespeckDataRaw(accel_x, accel_y, accel_z)
//fun to(r: RespeckData): RespeckDataRaw = RespeckDataRaw(r.accel_x, r.accel_y, r.accel_z)

data class RespeckDataRaw(val x: Float, val y: Float, val z: Float): Iterable<Float> {
//    fun fromRespeckData(r: RespeckData) = r.toRespeckDataRaw()
    fun asList() = listOf(x, y, z)

    // an indexing-style way to access the values of this data
    operator fun get(i: Int): Float = when (i) {
        0 -> x
        1 -> y
        2 -> z
        else -> 0f
    }

    override fun iterator(): Iterator<Float> = asList().iterator()
}