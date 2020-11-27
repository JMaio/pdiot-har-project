package com.specknet.pdiotapp.live

import android.util.Log
import com.paramsen.noise.Noise
import com.specknet.pdiotapp.utils.RespeckData
import com.specknet.pdiotapp.utils.RespeckDataRaw
import com.specknet.pdiotapp.utils.toRespeckDataRaw
import kotlin.math.pow
import kotlin.math.sqrt

class DataProcessor {
    companion object {
        const val TAG = "DataProcessor"
    }

    @JvmName("fft1")
    fun fft(data: List<RespeckData>) = fft(data.map { d -> d.toRespeckDataRaw() })

    fun fft(data: List<RespeckDataRaw>): List<List<Float>> {
        // use Noise (https://github.com/paramsen/noise) to perform FFT
        val noise = Noise.real(data.size)
        val dataAxes = mutableListOf<MutableList<Float>>()
        // create n lists, one for each parameter
        data[0].asList().map { dataAxes.add(mutableListOf()) }

        data.map { e ->
            e.mapIndexed { i, a -> dataAxes[i].add(a) }
        }
        Log.d(TAG, "FFT data => $dataAxes")

        val fftOutput = mutableListOf<List<Float>>()

        dataAxes.map { ax ->
//            val dst = FloatArray(ax.size + 2) //real output length equals src+2
            val fft = noise.fft(
                ax.toFloatArray(),
                FloatArray(ax.size + 2)
            ) //real output length equals src+2
            // The result array has the pairs of real+imaginary floats in a one dimensional array; even indices
            // are real, odd indices are imaginary. DC bin is located at index 0, 1, nyquist at index n-2, n-1

            Log.d(TAG, "FFT result => $fft")
            // for pairs below. only magnitude matters to us so transform it here
//            val pairs = mutableListOf<Pair<Float, Float>>() // = arrayOfNulls(fft.size / 2)
//            for (i in 0 until fft.size / 2) {
//                val real = fft[i * 2]
//                val imaginary = fft[i * 2 + 1]
//                pairs.add(Pair(real, imaginary))
//            }
//            fftOutput.add(pairs)
            // magnitudes
            val mags = mutableListOf<Float>() // = arrayOfNulls(fft.size / 2)
            for (i in fft.indices step 2) {
                val a = fft[i]
                val b = fft[i + 1]
                mags.add(sqrt(a.pow(2) + b.pow(2)))
            }
            fftOutput.add(mags)
        }

        return fftOutput
    }

//    fun fft(data: List<T>)  {
//        when { typeOf(T) ->
//
//        }
//    }
}