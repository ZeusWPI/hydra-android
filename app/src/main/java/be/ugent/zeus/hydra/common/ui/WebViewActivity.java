package be.ugent.zeus.hydra.common.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.databinding.ActivityWebviewBinding;

/**
 * Displays a web view.
 *
 * @author Niko Strijbol
 */
public class WebViewActivity extends BaseActivity<ActivityWebviewBinding> {

    public static final String URL = "be.ugent.zeus.hydra.url";
    public static final String TITLE = "be.ugent.zeus.hydra.title";

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ActivityWebviewBinding::inflate);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.setWebViewClient(new ProgressClient(binding.progressBar.progressBar));

        Intent intent = getIntent();
        String url = intent.getStringExtra(URL);
        String title = intent.getStringExtra(TITLE);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        binding.webView.loadUrl(url);
    }

    private static class ProgressClient extends WebViewClient {
        private final ProgressBar progressBar;

        ProgressClient(ProgressBar progressBar) {
            super();
            this.progressBar = progressBar;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
