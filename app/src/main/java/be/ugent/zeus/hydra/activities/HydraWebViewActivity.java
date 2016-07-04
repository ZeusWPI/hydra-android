package be.ugent.zeus.hydra.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.R;

public class HydraWebViewActivity extends AppCompatActivity {

    public static final String URL = "be.ugent.zeus.hydra.url";
    public static final String TITLE = "be.ugent.zeus.hydra.title";

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hydra_web_view);


        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
        String title = intent.getStringExtra(TITLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(title);
        }
        setTitle(title);
        webView.loadUrl(url);
    }

    @Override
    protected void onResume() {
        super.onResume();
        HydraApplication app = (HydraApplication) getApplication();
        app.sendScreenName("Webview > " + getTitle());
    }
}
