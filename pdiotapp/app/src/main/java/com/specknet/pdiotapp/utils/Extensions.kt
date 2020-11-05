package com.specknet.pdiotapp.utils

import android.view.View
import androidx.fragment.app.Fragment

// https://stackoverflow.com/a/59655582/9184658
// Use a Kotlin extension function
fun Fragment?.runOnUiThread(action: () -> Unit) {
    this ?: return
    if (!isAdded) return // Fragment not attached to an Activity
    activity?.runOnUiThread(action)
}

//fun <T: View> Fragment?.findViewById(r: Int): T? {
//    this ?: return null
////    if (!isAdded) return null
//    return view?.findViewById<T>(r)
//}