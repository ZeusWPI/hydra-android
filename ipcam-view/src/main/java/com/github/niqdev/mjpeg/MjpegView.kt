package com.github.niqdev.mjpeg

import android.view.SurfaceView

interface MjpegView {
    fun setSource(stream: MjpegInputStream)
    fun showFps(show: Boolean)
    fun flipSource(flip: Boolean)
    fun flipHorizontal(flip: Boolean)
    fun flipVertical(flip: Boolean)
    fun setRotate(degrees: Float)
    fun stopPlayback()
    val isStreaming: Boolean
    val surfaceView: SurfaceView
}
