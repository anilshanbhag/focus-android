/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import org.mozilla.focus.R;
import org.mozilla.focus.fragment.InfoFragment;
import org.mozilla.focus.locale.Locales;
import org.mozilla.focus.utils.SupportUtils;
import org.mozilla.focus.web.IWebView;
import org.mozilla.focus.web.WebViewProvider;

/**
 * A generic activity that supports showing additional information in a WebView. This is useful
 * for showing any web based content, including About/Help/Rights, and also SUMO pages.
 */
public class InfoActivity extends AppCompatActivity {
    private static final String EXTRA_URL = "extra_url";
    private static final String EXTRA_TITLE = "extra_title";

    public static final Intent getIntentFor(final Context context, final String url, final String title) {
        final Intent intent = new Intent(context, InfoActivity.class);

        intent.putExtra(EXTRA_URL, url);
        intent.putExtra(EXTRA_TITLE, title);

        return intent;
    }

    public static final Intent getAboutIntent(final Context context) {
        final Resources resources = Locales.getLocalizedResources(context);

        // We can't use "about:" because webview silently swallows about: pages, hence we use
        // a custom scheme.
        return getIntentFor(context, "focusabout:", resources.getString(R.string.menu_about));
    }

    public static final Intent getRightsIntent(final Context context) {
        final Resources resources = Locales.getLocalizedResources(context);
        return getIntentFor(context, "file:///android_asset/rights-focus.html", resources.getString(R.string.menu_rights));
    }

    public static final Intent getHelpIntent(final Context context) {
        final Resources resources = Locales.getLocalizedResources(context);
        return getIntentFor(context, SupportUtils.HELP_URL, resources.getString(R.string.menu_help));
    }

    public static final Intent getTrackerHelpIntent(final Context context) {
        final Resources resources = Locales.getLocalizedResources(context);
        return getIntentFor(context, SupportUtils.getSumoURLForTopic(context, "trackers"), resources.getString(R.string.menu_help));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info);

        final String url = getIntent().getStringExtra(EXTRA_URL);
        final String title = getIntent().getStringExtra(EXTRA_TITLE);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.infofragment, InfoFragment.create(url))
                .commit();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(title);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        if (name.equals(IWebView.class.getName())) {
            final View view = WebViewProvider.create(this, attrs);

            final IWebView webView = (IWebView) view;
            webView.setBlockingEnabled(false);

            return view;
        }

        return super.onCreateView(name, context, attrs);
    }
}
