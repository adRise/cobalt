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
import dev.cobalt.coat.ArtworkDownloaderDefault;
import dev.cobalt.coat.CobaltActivity;
import dev.cobalt.coat.CobaltService;
import dev.cobalt.coat.StarboardBridge;
import dev.cobalt.libraries.services.clientloginfo.ClientLogInfoModule;
import dev.cobalt.util.Holder;
import dev.cobalt.util.Log;

/**
 * Test Cobalt Activity that loads a URL from the Intent.
 * 
 * <p>This activity accepts a URL via Intent data and loads it using Cobalt.
 */
public class TestCobaltActivity extends CobaltActivity {

  @Override
  protected void onCreate(android.os.Bundle savedInstanceState) {
    Log.i("TestCobaltActivity", "TestCobaltActivity.onCreate()");
    Intent intent = getIntent();
    if (intent != null && intent.getData() != null) {
      Log.i("TestCobaltActivity", "onCreate() - Intent URL: " + intent.getData().toString());
    }
    super.onCreate(savedInstanceState);
  }

  @Override
  protected String[] getArgs() {
    // Get the URL from intent data
    Intent intent = getIntent();
    String urlFromIntent = getIntentUrlAsString(intent);
    
    Log.i("TestCobaltActivity", "getArgs() called, urlFromIntent: " + urlFromIntent);
    
    // Get parent args (which includes metadata from manifest)
    String[] parentArgs = super.getArgs();
    java.util.List<String> args = new java.util.ArrayList<>(java.util.Arrays.asList(parentArgs));
    
    // If we have a URL from intent, replace the --url= argument with it
    if (urlFromIntent != null && !urlFromIntent.isEmpty()) {
      String urlArgPrefix = "--url=";
      boolean found = false;
      for (int i = 0; i < args.size(); i++) {
        if (args.get(i).startsWith(urlArgPrefix)) {
          args.set(i, urlArgPrefix + urlFromIntent);
          found = true;
          Log.i("TestCobaltActivity", "getArgs() replaced URL with intent URL: " + urlFromIntent);
          break;
        }
      }
      if (!found) {
        args.add(urlArgPrefix + urlFromIntent);
        Log.i("TestCobaltActivity", "getArgs() added URL from intent: " + urlFromIntent);
      }
    } else {
      Log.i("TestCobaltActivity", "getArgs() no URL in intent, using manifest URL");
    }
    
    // Log all args for debugging
    Log.i("TestCobaltActivity", "getArgs() returning args count: " + args.size());
    for (String arg : args) {
      Log.i("TestCobaltActivity", "getArgs() arg: " + arg);
    }
    
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

