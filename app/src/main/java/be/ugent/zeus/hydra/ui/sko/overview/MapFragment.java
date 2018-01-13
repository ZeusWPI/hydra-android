package be.ugent.zeus.hydra.ui.sko.overview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import be.ugent.zeus.hydra.R;

/**
 * Display a map.
 *
 * @author Niko Strijbol
 */
public class MapFragment extends Fragment {

    private static final String URL = "https://zeus.ugent.be/hydra/api/1.0/grondplan-android.html";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sko_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        WebView webView = view.findViewById(R.id.web_view);
        final ProgressBar progressBar = view.findViewById(R.id.progress_bar);

        webView.setScrollContainer(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(1);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(URL);
    }
}