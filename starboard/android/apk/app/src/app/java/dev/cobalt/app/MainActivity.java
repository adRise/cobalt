// Copyright 2017 The Cobalt Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package dev.cobalt.app;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import dev.cobalt.coat.ArtworkDownloaderDefault;
import dev.cobalt.coat.CobaltActivity;
import dev.cobalt.coat.CobaltService;
import dev.cobalt.coat.R;
import dev.cobalt.coat.StarboardBridge;
import dev.cobalt.libraries.services.clientloginfo.ClientLogInfoModule;
import dev.cobalt.util.Holder;
import dev.cobalt.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main Activity for the "Cobalt on Android TV" app.
 *
 * <p>The real work is done in the abstract base class. This class is really just some factory
 * methods to "inject" things that can be customized.
 */
public class MainActivity extends CobaltActivity {

  private static final String YOUTUBE_URL = "https://www.youtube.com/tv";
  private static final String TUBITV_URL = "https://ott-firetv-hyb.tubitv.com/";
  private static final String SELECTED_URL_KEY = "selected_url";
  
  private FrameLayout buttonSelectionLayout;
  private boolean urlSelected = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i("MainActivity", "MainActivity.onCreate() - Cobalt test");
    
    // Check if URL was already selected in a previous instance
    if (savedInstanceState != null) {
      urlSelected = savedInstanceState.getBoolean(SELECTED_URL_KEY, false);
      Log.i("MainActivity", "onCreate: urlSelected from savedInstanceState = " + urlSelected);
    }
    
    // Check intent for URL (in case of deep link)
    Intent intent = getIntent();
    if (intent != null && intent.getData() != null) {
      urlSelected = true;
      Log.i("MainActivity", "onCreate: urlSelected from intent = true, URL = " + intent.getData().toString());
    }
    
    Log.i("MainActivity", "onCreate: urlSelected = " + urlSelected + " before super.onCreate()");

    // Always call super.onCreate() to initialize the activity properly
    super.onCreate(savedInstanceState);
    
    // Show button selection screen if URL not already selected
    if (!urlSelected) {
      Log.i("MainActivity", "onCreate: Showing button selection screen");
      showUrlSelectionButtons();
    } else {
      Log.i("MainActivity", "onCreate: URL already selected, skipping button screen");
    }
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(SELECTED_URL_KEY, urlSelected);
  }

  private void showUrlSelectionButtons() {
    // Inflate the button selection layout
    buttonSelectionLayout = (FrameLayout) getLayoutInflater().inflate(
        R.layout.url_selection, null);
    
    Button buttonYouTube = buttonSelectionLayout.findViewById(R.id.button_youtube);
    Button buttonTubiTV = buttonSelectionLayout.findViewById(R.id.button_tubitv);

    buttonYouTube.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadUrl(YOUTUBE_URL);
      }
    });

    buttonTubiTV.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        loadUrl(TUBITV_URL);
      }
    });

    // Add the button layout on top of existing content using addContentView
    // This ensures it appears above the video surface view
    addContentView(buttonSelectionLayout, 
        new android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT));
  }

  private void loadUrl(String url) {
    urlSelected = true;
    
    // Hide the button selection layout
    if (buttonSelectionLayout != null) {
      android.view.ViewGroup parent = (android.view.ViewGroup) buttonSelectionLayout.getParent();
      if (parent != null) {
        parent.removeView(buttonSelectionLayout);
      }
      buttonSelectionLayout = null;
    }

    // Restart the activity with the selected URL in the intent
    // This ensures the URL is properly loaded via startDeepLink
    Intent intent = new Intent(this, MainActivity.class);
    intent.setData(Uri.parse(url));
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    finish();
  }

  @Override
  protected String[] getArgs() {
    Log.i("MainActivity", "getArgs() called, urlSelected = " + urlSelected);
    
    // If URL is already selected (from intent or saved state), use parent's getArgs()
    // Otherwise, override to use about:blank as placeholder URL
    if (urlSelected) {
      String[] args = super.getArgs();
      Log.i("MainActivity", "getArgs() returning parent args (URL selected), count = " + args.length);
      return args;
    }
    
    // Get parent args but replace the URL argument with about:blank to prevent auto-loading
    String[] parentArgs = super.getArgs();
    List<String> args = new ArrayList<>(Arrays.asList(parentArgs));
    
    Log.i("MainActivity", "getArgs() parent args count = " + parentArgs.length);
    for (String arg : parentArgs) {
      Log.i("MainActivity", "getArgs() parent arg: " + arg);
    }
    
    // Replace the --url= argument with about:blank to prevent auto-loading
    String urlArgPrefix = "--url=";
    boolean foundUrlArg = false;
    for (int i = 0; i < args.size(); i++) {
      if (args.get(i).startsWith(urlArgPrefix)) {
        args.set(i, urlArgPrefix + "about:blank");
        foundUrlArg = true;
        Log.i("MainActivity", "getArgs() replaced URL arg with about:blank");
        break;
      }
    }
    
    // If no URL arg was found, add one with about:blank
    if (!foundUrlArg) {
      args.add(urlArgPrefix + "about:blank");
      Log.i("MainActivity", "getArgs() added about:blank URL arg");
    }
    
    Log.i("MainActivity", "getArgs() returning modified args, count = " + args.size());
    
    return args.toArray(new String[0]);
  }

  @Override
  protected StarboardBridge createStarboardBridge(String[] args, String startDeepLink) {
    Holder<Activity> activityHolder = new Holder<>();
    Holder<Service> serviceHolder = new Holder<>();
    Runnable stopRequester =
        new Runnable() {
          @Override
          public void run() {
            getStarboardBridge().requestStop(0);
          }
        };
    StarboardBridge bridge =
        new StarboardBridge(
            getApplicationContext(),
            activityHolder,
            serviceHolder,
            new ArtworkDownloaderDefault(),
            args,
            startDeepLink);

    CobaltService.Factory clientLogInfoFactory =
        new ClientLogInfoModule().provideFactory(getApplicationContext());
    bridge.registerCobaltService(clientLogInfoFactory);

    return bridge;
  }
}
