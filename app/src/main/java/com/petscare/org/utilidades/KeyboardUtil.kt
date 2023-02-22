package com.petscare.org.utilidades

import android.R
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import java.util.HashMap

class KeyboardUtil private constructor(act: Activity, listener: SoftKeyboardToggleListener) :
    OnGlobalLayoutListener {
    private var mCallback: SoftKeyboardToggleListener?
    private val mRootView: View
    private var prevValue: Boolean? = null
    private val mScreenDensity: Float

    interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }

    override fun onGlobalLayout() {
        val r = Rect()
        mRootView.getWindowVisibleDisplayFrame(r)
        val heightDiff = mRootView.rootView.height - (r.bottom - r.top)
        val dp = heightDiff / mScreenDensity
        val isVisible = dp > MAGIC_NUMBER
        if (mCallback != null && (prevValue == null || isVisible != prevValue)) {
            prevValue = isVisible
            mCallback!!.onToggleSoftKeyboard(isVisible)
        }
    }

    private fun removeListener() {
        mCallback = null
        mRootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    companion object {
        private const val MAGIC_NUMBER = 200
        private val sListenerMap = HashMap<SoftKeyboardToggleListener, KeyboardUtil>()
        fun addKeyboardToggleListener(act: Activity, listener: SoftKeyboardToggleListener) {
            removeKeyboardToggleListener(listener)
            sListenerMap[listener] = KeyboardUtil(act, listener)
        }

        private fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
            if (sListenerMap.containsKey(listener)) {
                val k = sListenerMap[listener]
                k!!.removeListener()
                sListenerMap.remove(listener)
            }
        }

        fun removeAllKeyboardToggleListeners() {
            for (l in sListenerMap.keys) sListenerMap[l]!!
                .removeListener()
            sListenerMap.clear()
        }

        fun cerrarTeclado(activeView: View) {
            val inputMethodManager = activeView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activeView.windowToken, 0)
        }
    }

    init {
        mCallback = listener
        mRootView = (act.findViewById<View>(R.id.content) as ViewGroup).getChildAt(0)
        mRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        mScreenDensity = act.resources.displayMetrics.density
    }
}