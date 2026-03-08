package com.github.niqdev.mjpeg;

import android.graphics.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/*
 * I don't really understand and want to know what the hell it does!
 * Maybe one day I will refactor it ;-)
 * <p/>
 * https://code.google.com/archive/p/android-camera-axis
 */
public class MjpegViewDefault extends AbstractMjpegView {
    private static final String TAG = MjpegViewDefault.class.getSimpleName();

    private final SurfaceHolder.Callback mSurfaceHolderCallback;
    private final SurfaceView mSurfaceView;
    private MjpegViewThread thread;
    private MjpegInputStream mIn = null;
    private boolean showFps = false;
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;
    private float rotateDegrees = 0;
    private volatile boolean mRun = false;
    private volatile boolean surfaceDone = false;
    private Paint overlayPaint;
    private int ovlPos;
    private int dispWidth;
    private int dispHeight;
    private boolean resume = false;

    MjpegViewDefault(SurfaceView surfaceView, SurfaceHolder.Callback callback) {
        this.mSurfaceView = surfaceView;
        this.mSurfaceHolderCallback = callback;
        init();
    }

    Bitmap flip(Bitmap src) {
        Matrix m = new Matrix();
        float sx = flipHorizontal ? -1 : 1;
        float sy = flipVertical ? -1 : 1;
        m.preScale(sx, sy);
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }

    Bitmap rotate(Bitmap src, float degrees) {
        Matrix m = new Matrix();
        m.setRotate(degrees);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
    }

    private void init() {

        SurfaceHolder holder = mSurfaceView.getHolder();
        holder.addCallback(mSurfaceHolderCallback);
        thread = new MjpegViewThread(holder);
        mSurfaceView.setFocusable(true);
        if (!resume) {
            resume = true;
            overlayPaint = new Paint();
            overlayPaint.setTextAlign(Paint.Align.LEFT);
            overlayPaint.setTextSize(12);
            overlayPaint.setTypeface(Typeface.DEFAULT);
            ovlPos = MjpegViewDefault.POSITION_LOWER_RIGHT;
            dispWidth = mSurfaceView.getWidth();
            dispHeight = mSurfaceView.getHeight();
        }
    }

    /* all methods/constructors below are no more accessible */

    void _startPlayback() {
        if (mIn != null && thread != null) {
            mRun = true;
            /*
             * clear canvas cache
             * @see https://github.com/niqdev/ipcam-view/issues/14
             */
            //noinspection deprecation
            mSurfaceView.destroyDrawingCache();
            thread.start();
        }
    }

    void _resumePlayback() {
        mRun = true;
        init();
        thread.start();
    }

    @Override
    public void onSurfaceCreated(@NonNull SurfaceHolder holder) {
        surfaceDone = true;
    }

    @Override
    public void onSurfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        if (thread != null) {
            thread.setSurfaceSize(width, height);
        }
    }

    @Override
    public void onSurfaceDestroyed(@NonNull SurfaceHolder holder) {
        surfaceDone = false;
        stopPlayback();
        if (thread != null) {
            thread = null;
        }
    }

    @Override
    public void setSource(@NonNull MjpegInputStream stream) {
        // see https://github.com/niqdev/ipcam-view/issues/14
        mIn = stream;
        // make sure resume is calling _resumePlayback()
        if (!resume) {
            _startPlayback();
        } else {
            _resumePlayback();
        }
    }

    @Override
    public void showFps(boolean show) {
        showFps = show;
    }

    @Override
    public void flipSource(boolean flip) {
        flipHorizontal(flip);
    }

    @Override
    public void flipHorizontal(boolean flip) {
        this.flipHorizontal = flip;
    }

    @Override
    public void flipVertical(boolean flip) {
        this.flipVertical = flip;
    }

    @Override
    public void setRotate(float degrees) {
        rotateDegrees = degrees;
    }

    @Override
    public synchronized void stopPlayback() {
        mRun = false;
        boolean retry = true;
        while (retry) {
            try {
                // make sure the thread is not null
                if (thread != null) {
                    thread.join(500);
                }
                retry = false;
            } catch (InterruptedException e) {
                Log.e(TAG, "error stopping playback thread", e);
            }
        }

        // close the connection
        if (mIn != null) {
            try {
                mIn.close();
            } catch (IOException e) {
                Log.e(TAG, "error closing input stream", e);
            }
            mIn = null;
        }
    }

    @Override
    public boolean isStreaming() {
        return mRun;
    }

    @NonNull
    @Override
    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    class MjpegViewThread extends Thread {
        private final SurfaceHolder mSurfaceHolder;
        private int frameCounter = 0;
        private Bitmap ovl;

        MjpegViewThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        private Rect destRect(int bmw, int bmh) {
            float bmasp = (float) bmw / (float) bmh;
            bmw = dispWidth;
            bmh = (int) (dispWidth / bmasp);
            if (bmh > dispHeight) {
                bmh = dispHeight;
                bmw = (int) (dispHeight * bmasp);
            }
            int tempx = (dispWidth / 2) - (bmw / 2);
            int tempy = (dispHeight / 2) - (bmh / 2);
            return new Rect(tempx, tempy, bmw + tempx, bmh + tempy);
        }

        void setSurfaceSize(int width, int height) {
            synchronized (mSurfaceHolder) {
                dispWidth = width;
                dispHeight = height;
            }
        }

        private Bitmap makeFpsOverlay(Paint p, String text) {
            Rect b = new Rect();
            p.getTextBounds(text, 0, text.length(), b);
            int bwidth = b.width() + 2;
            int bheight = b.height() + 2;
            Bitmap bm = Bitmap.createBitmap(bwidth, bheight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bm);
            p.setColor(Color.BLACK);
            c.drawRect(0, 0, bwidth, bheight, p);
            p.setColor(Color.WHITE);
            c.drawText(text, -b.left + 1, (bheight / 2) - ((p.ascent() + p.descent()) / 2) + 1, p);
            return bm;
        }

        public void run() {
            long start = System.currentTimeMillis();
            PorterDuffXfermode mode = new PorterDuffXfermode(
                    PorterDuff.Mode.DST_OVER);
            Bitmap bm;
            int width;
            int height;
            Rect destRect;
            Canvas c = null;
            Paint p = new Paint();
            String fps;
            while (mRun) {
                if (surfaceDone) {
                    try {
                        c = mSurfaceHolder.lockCanvas();

                        if (c == null) {
                            Log.w(TAG, "null canvas, skipping render");
                            continue;
                        }
                        synchronized (mSurfaceHolder) {
                            try {
                                byte[] header = mIn.readHeader();
                                byte[] imageData = mIn.readMjpegFrame(header);
                                bm = BitmapFactory.decodeStream(new ByteArrayInputStream(imageData));
                                if (flipHorizontal || flipVertical) {
                                    bm = flip(bm);
                                }
                                if (rotateDegrees != 0) {
                                    bm = rotate(bm, rotateDegrees);
                                }

                                destRect = destRect(bm.getWidth(), bm.getHeight());

                                c.drawColor(Color.BLACK);

                                c.drawBitmap(bm, null, destRect, p);

                                if (showFps) {
                                    p.setXfermode(mode);
                                    if (ovl != null) {
                                        height = ((ovlPos & 1) == 1) ? destRect.top : destRect.bottom - ovl.getHeight();
                                        width = ((ovlPos & 8) == 8) ? destRect.left : destRect.right - ovl.getWidth();
                                        c.drawBitmap(ovl, width, height, null);
                                    }
                                    p.setXfermode(null);
                                    frameCounter++;
                                    if ((System.currentTimeMillis() - start) >= 1000) {
                                        fps = frameCounter + "fps";
                                        frameCounter = 0;
                                        start = System.currentTimeMillis();
                                        ovl = makeFpsOverlay(overlayPaint, fps);
                                    }
                                }
                            } catch (IOException e) {
                                Log.e(TAG, "encountered exception during render", e);
                            }
                        }
                    } finally {
                        if (c != null) {
                            mSurfaceHolder.unlockCanvasAndPost(c);
                        } else {
                            Log.w(TAG, "couldn't unlock surface canvas");
                        }
                    }
                }
            }
        }
    }
}
