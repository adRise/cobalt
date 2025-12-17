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

    // Set the button layout as the main content view
    setContentView(buttonSelectionLayout);
  }

  private void loadUrl(String url) {
    Log.i("MainActivity", "loadUrl() called with URL: " + url);
    
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
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
    finish();
  }

}
