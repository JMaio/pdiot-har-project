package com.specknet.pdiotapp.live

import android.util.Log
import com.paramsen.noise.Noise
import com.specknet.pdiotapp.utils.RespeckDataRaw
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sqrt

object DataProcessor {
    private const val TAG = "DataProcessor"

    // NATIVE_NOISE: kissfft require input length to be even
//    const val fftWs = 25
    private var fftWs = 24

    // use Noise (https://github.com/paramsen/noise) to perform FFT
    // make this before? yes! otherwise, if it's re-instantiated every time, it starts to give NaN values
    private var noise = Noise.real(fftWs)


    // frequencies above this limit are removed?
    private const val filterLowFreq = 0.1 * 12.5 / 2

    fun fft(model: InferenceModel, data: List<RespeckDataRaw>): RespeckDataFFTOutput {
        model.fftWs?.let {
            // check if the input data is the same as noise's
            if (it != fftWs) {
                fftWs = model.fftWs
                // make a new noise object for fft
                noise = Noise.real(it)
            }
        }

        val fftStep = model.fftStep ?: 15

        val dataAxes = mutableListOf<MutableList<Float>>()
        // create n lists, one for each parameter
        data[0].asList().map { dataAxes.add(mutableListOf()) }

        data.map { element ->
            // add each axis' data from Respeck(x, y, z) to [[xs], [ys], [zs]]
            element.mapIndexed { i, a -> dataAxes[i].add(a) }
        }
        Log.d(TAG, "FFT data => $dataAxes")

        // 3 lists (x, y, z) of 2D FFT <List<List<Float>> output (ws * resolution * sample)
        val fftOutput = mutableListOf<List<List<Float>>>()

        dataAxes.map { ax ->
            val axMags = mutableListOf<List<Float>>()

            // iterate over the full window in smaller steps
            // only take as long as there are full windows
            for (start in 0..(model.ws - fftWs) step fftStep) {
                // start taking samples at the correct start position
                // last element must be duplicated if window size is odd
                val subWindow = ax.drop(start).take(fftWs).toMutableList()
                Log.d("$TAG/forloop", "[w: ${data.size}][s: $start] (${subWindow.size}) $ax")

                if (subWindow.size % 2 != 0) {
                    // input must be even - if not, duplicate last sample
                    Log.d(TAG, "FFT input must be even - duplicating last sample")
                    subWindow.add(subWindow.last())
                }

                // apply hamming window
                val ham = hamming(subWindow)
//                val dst = FloatArray(subWindow.size + 2) //real output length equals src+2
                // The result array has the pairs of real+imaginary floats in a one dimensional array; even indices
                // are real, odd indices are imaginary. DC bin is located at index 0, 1, nyquist at index n-2, n-1
//                val fft = noise.fft(ham.toFloatArray(), FloatArray(subWindow.size + 2))
                val fft = noise.fft(ham.toFloatArray(), FloatArray(subWindow.size + 2))

                // for pairs below. only magnitude matters to us so transform it here
                // https://www.baeldung.com/kotlin-split-list-into-parts#4-using-chunked
//                val mag = emptyList<Float>()
                val mag = fft.toList().chunked(2).map { (a, b) ->
                    sqrt(a.pow(2) + b.pow(2))
                }.map { m ->
                    when (m > filterLowFreq) {
                        true -> 0
                        false -> m
                    }.toFloat()
                }

                Log.d(
                    TAG,
                    listOf(
                        "  ",
                        "  subWindow (size ${subWindow.size}) => $subWindow",
//                        "        ham (size ${ham.size}) => $ham",
                        " FFT result (size ${fft.size}) => ${fft.toList()}",
                        "        mag (size ${mag.size}) => $mag"
                    ).joinToString("\n")
                )
                if (fft.any { f -> f.isNaN() }) {
                    Log.w(TAG, "FFT results are NaN!")
                    throw Exception("NaN FFT results")
                }
                // add to this axis
                axMags.add(mag)
            }
            fftOutput.add(axMags)
        }
        return fftOutput.toList()
    }

    // https://github.com/NeuroTechX/eeg-101/blob/9a349ec6c87e1416826a7527d0d7da87fba4eb36/EEG101/android/app/src/main/java/com/eeg_project/components/signal/FFT.java#L193
    private fun hamming(win: List<Float>): List<Float> =
        win.mapIndexed { n, f -> f * (0.54 - 0.46 * cos(2 * PI * n / (win.size - 1))) }
            .map { it.toFloat() }
    // Compute Hamming window coefficients.
    //
    // See [http://www.mathworks.com/help/signal/ref/hamming.html]


}