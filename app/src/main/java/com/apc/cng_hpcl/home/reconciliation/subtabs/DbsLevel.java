package com.apc.cng_hpcl.home.reconciliation.subtabs;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.apc.cng_hpcl.R;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static com.apc.cng_hpcl.util.Constant.BASE_URL;

public class DbsLevel extends AppCompatActivity {
    private WebView webView,webViewSummary;
    private LinearLayout linear_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbs_level);
        webView = (WebView) findViewById(R.id.webView1);
        webViewSummary = (WebView) findViewById(R.id.summarywebView);
        Button report = (Button) findViewById(R.id.report);
        Button summary = (Button) findViewById(R.id.summary);
        linear_edit = findViewById(R.id.linear_edit);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_edit.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                startWebView(BASE_URL+"Report/luage_dbs.php");
            }
        });

        summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_edit.setVisibility(View.GONE);
                webViewSummary.setVisibility(View.VISIBLE);
                startWebViewSummary(BASE_URL+"Report/summary_luag_dbs.php");
            }
        });

//        startWebView("https://www.cng-suvidha.in/npclnoida/index.php");
    }

    private void startWebViewSummary(String url) {
        webViewSummary.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(DbsLevel.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        webViewSummary.getSettings().setJavaScriptEnabled(true);


        webViewSummary.loadUrl(url);
    }

    private void startWebView(String url) {



        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;

            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //Show loader on url load
            public void onLoadResource(WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(DbsLevel.this);
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                }
            }

            public void onPageFinished(WebView view, String url) {
                try {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);


        webView.loadUrl(url);


    }



    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }


}