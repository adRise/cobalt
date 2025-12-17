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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import dev.cobalt.coat.R;
import dev.cobalt.util.Log;

/**
 * Main Activity for the "Cobalt on Android TV" app.
 *
 * <p>This activity only displays URL selection buttons and doesn't load any Cobalt content.
 * The actual URL loading is handled by TestCobaltActivity.
 */
public class MainActivity extends Activity {

  private static final String YOUTUBE_URL = "https://www.youtube.com/tv";
  private static final String TUBITV_URL = "https://ott-firetv-hyb.tubitv.com/";
  
  private FrameLayout buttonSelectionLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    Log.i("MainActivity", "MainActivity.onCreate() - Cobalt test");
    
    super.onCreate(savedInstanceState);
    
    // Show button selection screen
    showUrlSelectionButtons();
  }

  private void showUrlSelectionButtons() {
    // Inflate the button selection layout
    buttonSelectionLayout = (FrameLayout) getLayoutInflater().inflate(
        R.layout.url_selection, null);
    
    Button buttonYouTube = buttonSelectionLayout.findViewById(R.id.button_youtube);
    Button buttonTubiTV = buttonSelectionLayout.findViewById(R.id.button_tubitv);

    if (buttonYouTube == null || buttonTubiTV == null) {
      Log.e("MainActivity", "Buttons not found in layout!");
      return;
    }

    Log.i("MainActivity", "Setting up button listeners");

    buttonYouTube.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i("MainActivity", "YouTube button clicked");
        loadUrl(YOUTUBE_URL);
      }
    });

    buttonTubiTV.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Log.i("MainActivity", "TubiTV button clicked");
        loadUrl(TUBITV_URL);
      }
    });

    // Ensure buttons are clickable
    buttonYouTube.setClickable(true);
    buttonTubiTV.setClickable(true);

    // Set the button layout as the main content view
    setContentView(buttonSelectionLayout);
    
    Log.i("MainActivity", "Button layout set, buttons should be clickable now");
  }

  private void loadUrl(String url) {
    Log.i("MainActivity", "loadUrl() called with URL: " + url);
    
    try {
      // Hide the button selection layout
      if (buttonSelectionLayout != null) {
        android.view.ViewGroup parent = (android.view.ViewGroup) buttonSelectionLayout.getParent();
        if (parent != null) {
          parent.removeView(buttonSelectionLayout);
        }
        buttonSelectionLayout = null;
      }

      // Start TestCobaltActivity with the selected URL in the intent
      Intent intent = new Intent(this, TestCobaltActivity.class);
      intent.setData(Uri.parse(url));
      Log.i("MainActivity", "Starting TestCobaltActivity with URL: " + url);
      Log.i("MainActivity", "Intent data: " + intent.getData());
      startActivity(intent);
      // Don't finish MainActivity so user can return to it with back button
    } catch (Exception e) {
      Log.e("MainActivity", "Error starting TestCobaltActivity", e);
    }
  }

}
