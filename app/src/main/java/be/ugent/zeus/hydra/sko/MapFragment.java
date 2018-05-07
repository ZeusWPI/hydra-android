package be.ugent.zeus.hydra.sko;

import android.content.Context;
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
import be.ugent.zeus.hydra.common.network.Endpoints;
import be.ugent.zeus.hydra.common.network.InterceptingWebViewClient;

/**
 * Display a map of the Student Village.
 *
 * @author Niko Strijbol
 */
public class MapFragment extends Fragment {

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
        webView.setWebViewClient(new ProgressClient(requireContext(), progressBar));
        webView.loadUrl(Endpoints.SKO_VILLAGE_MAP);
    }

    private static class ProgressClient extends InterceptingWebViewClient {
        private final ProgressBar progressBar;

        ProgressClient(Context context, ProgressBar progressBar) {
            super(context);
            this.progressBar = progressBar;
        }

        @Override
        public void onPageFinished(WebView webView1, String url) {
            progressBar.setVisibility(View.GONE);
        }
    }
}