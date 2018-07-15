package theawesomebox.com.app.awesomebox.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import theawesomebox.com.app.awesomebox.R;
import theawesomebox.com.app.awesomebox.common.utils.AppUtils;

/**
 * Contains {@link WebView} for showing web links in application.
 */
public class WebViewFragment extends BaseFragment {

    private WebView webView;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (WebView) view.findViewById(R.id.web_view);
        progressBar = (ProgressBar) view.findViewById(R.id.web_progress_bar);
        webView.setWebChromeClient(new WebViewClient());
        Bundle extras = this.getArguments();
        if (extras == null) return;
        String url = extras.getString("url");
        if (AppUtils.ifNotNullEmpty(url)) {
            webView.setInitialScale(1);
            WebSettings webSettings = webView.getSettings();
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setJavaScriptEnabled(false);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);
            webSettings.setDomStorageEnabled(true);
            webView.setScrollbarFadingEnabled(false);
            webView.setWebViewClient(new android.webkit.WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
            });
            webView.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        WebView webView = (WebView) v;
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_BACK:
                                if (webView.canGoBack()) {
                                    webView.goBack();
                                    return true;
                                }
                                break;
                        }
                    }
                    return false;
                }
            });
            webView.loadUrl(url);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null)
            webView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null)
            webView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.setOnLongClickListener(null);
            webView.setWebViewClient(null);
            webView.clearHistory();
            webView.clearCache(true);
            webView.pauseTimers();
        }
    }

    @Override
    public void onDestroy() {
        if (webView != null)
            webView.destroy();
        webView = null;
        super.onDestroy();
    }

    @Override
    public String getTitle() {
        Bundle bundle = getArguments();
        return bundle != null && bundle.containsKey("title") ? bundle.getString("title") : getString(R.string.app_name);
    }

    private class WebViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int progress) {
            if (progress == 100) {
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(progress);
            }
        }
    }
}
