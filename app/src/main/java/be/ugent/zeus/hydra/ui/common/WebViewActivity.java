package be.ugent.zeus.hydra.ui.common;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import be.ugent.zeus.hydra.R;

/**
 * Displays a web view.
 *
 * @author Niko Strijbol
 */
public class WebViewActivity extends BaseActivity {

    public static final String URL = "be.ugent.zeus.hydra.url";
    public static final String TITLE = "be.ugent.zeus.hydra.title";

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = findViewById(R.id.web_view);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
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