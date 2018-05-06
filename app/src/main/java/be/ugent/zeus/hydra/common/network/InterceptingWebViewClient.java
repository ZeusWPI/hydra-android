package be.ugent.zeus.hydra.common.network;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URL;

/**
 * WebView client that intercepts the requests and executes it with OkHTTP to enable TLSv1.2.
 *
 * On API > 20, this does nothing.
 *
 * @author Niko Strijbol
 */
public class InterceptingWebViewClient extends WebViewClient {

    private static final String TAG = "InterceptingWVClient";

    private final Context context;

    public InterceptingWebViewClient(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    @SuppressWarnings("deprecation")
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            return super.shouldInterceptRequest(view, url);
        }

        Log.i(TAG, "Intercepting request...");
        Request okHttpRequest = new Request.Builder().url(url).build();
        OkHttpClient client = InstanceProvider.getClient(context);
        try {
            Response response = client.newCall(okHttpRequest).execute();
            if (response.body() == null) {
                return super.shouldInterceptRequest(view, url);
            }
            return new WebResourceResponse(
                    response.header("Content-Type", "plain/text"),
                    response.header("Content-Encoding", "deflate"),
                    response.body().byteStream()
            );
        } catch (IOException e) {
            return super.shouldInterceptRequest(view, url);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            return super.shouldInterceptRequest(view, request);
        }

        Log.i(TAG, "Intercepting request...");
        try {
            Request okHttpRequest = new Request.Builder().url(new URL(request.getUrl().toString())).build();
            OkHttpClient client = InstanceProvider.getClient(context);

            Response response = client.newCall(okHttpRequest).execute();
            if (response.body() == null) {
                return super.shouldInterceptRequest(view, request);
            }
            return new WebResourceResponse(
                    response.header("Content-Type", "plain/text"),
                    response.header("Content-Encoding", "deflate"),
                    response.body().byteStream()
            );
        } catch (IOException e) {
            return super.shouldInterceptRequest(view, request);
        }
    }
}
