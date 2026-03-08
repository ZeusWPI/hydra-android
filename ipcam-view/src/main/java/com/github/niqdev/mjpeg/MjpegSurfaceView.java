package com.github.niqdev.mjpeg;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

public class MjpegSurfaceView extends SurfaceView implements SurfaceHolder.Callback, MjpegView {

    private final AbstractMjpegView mMjpegView;

    public MjpegSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMjpegView = new MjpegViewDefault(this, this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mMjpegView.onSurfaceCreated(holder);
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mMjpegView.onSurfaceChanged(holder, format, width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mMjpegView.onSurfaceDestroyed(holder);
    }

    @Override
    public void setSource(@NonNull MjpegInputStream stream) {
        mMjpegView.setSource(stream);
    }

    @Override
    public void showFps(boolean show) {
        mMjpegView.showFps(show);
    }

    @Override
    public void flipSource(boolean flip) {
        mMjpegView.flipSource(flip);
    }

    @Override
    public void flipHorizontal(boolean flip) {
        mMjpegView.flipHorizontal(flip);
    }

    @Override
    public void flipVertical(boolean flip) {
        mMjpegView.flipVertical(flip);
    }

    @Override
    public void setRotate(float degrees) {
        mMjpegView.setRotate(degrees);
    }

    @Override
    public void stopPlayback() {
        mMjpegView.stopPlayback();
    }

    @Override
    public boolean isStreaming() {
        return mMjpegView.isStreaming();
    }

    @NonNull
    @Override
    public SurfaceView getSurfaceView() {
        return this;
    }
}
