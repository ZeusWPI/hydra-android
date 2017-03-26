package be.ugent.zeus.hydra.ui.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.common.plugins.ProgressBarPlugin;
import be.ugent.zeus.hydra.ui.common.plugins.common.Plugin;

import java.util.List;

/**
 * Displays a web view.
 *
 * @author Niko Strijbol
 */
public class WebViewActivity extends BaseActivity {

    public static final String URL = "be.ugent.zeus.hydra.url";
    public static final String TITLE = "be.ugent.zeus.hydra.title";

    private ProgressBarPlugin progressBarPlugin = new ProgressBarPlugin();

    @Override
    protected void onAddPlugins(List<Plugin> plugins) {
        super.onAddPlugins(plugins);
        plugins.add(progressBarPlugin);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = $(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBarPlugin.hideProgressBar();
            }
        });

        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
        String title = intent.getStringExtra(TITLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        webView.loadUrl(url);
    }

    @Override
    protected String getScreenName() {
        return "Webview > " + getTitle();
    }
}