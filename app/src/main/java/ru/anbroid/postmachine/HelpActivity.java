package ru.anbroid.postmachine;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

/**
 * @author AnBro-ID, 2018
 * Активность для вывода справочного руководства
 */

public class HelpActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final WebView webView;
        setContentView(R.layout.activity_help);

        Toolbar toolbar = findViewById(R.id.toolbar_help);

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

        String prefix = Locale.getDefault().getLanguage().substring(0, 2).toLowerCase();
        String path = "file:///android_asset/help-" + prefix + ".htm";
        String defaultPath = "file:///android_asset/help-en.htm";

        webView = findViewById(R.id.help_page);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        int nightModeFlag = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url)
            {
                if (nightModeFlag == Configuration.UI_MODE_NIGHT_YES)
                    webView.loadUrl("javascript:(function(){document.getElementById('dark').media = 'all';})()");
                view.clearCache(true);
            }
        });

        try
        {
            if (Arrays.asList(getResources().getAssets().list("")).contains("help-" + prefix + ".htm"))
                webView.loadUrl(path);
            else webView.loadUrl(defaultPath);
        }
        catch (IOException e)
        {
            webView.loadUrl(defaultPath);
        }
    }
}
