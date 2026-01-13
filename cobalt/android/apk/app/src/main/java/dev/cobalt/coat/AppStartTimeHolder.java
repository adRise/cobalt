// Copyright 2026 The Cobalt Authors. All Rights Reserved.
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

package dev.cobalt.coat;

/**
 * A holder class for the application start timestamp.
 * This is used to measure app startup time from the very beginning.
 */
public class AppStartTimeHolder {
  private static long sAppStartTimeNanos = 0;

  /**
   * Set the application start timestamp.
   * This should be called as early as possible in the app lifecycle,
   * typically from Application.onCreate().
   */
  public static void setAppStartTimeNanos(long timeNanos) {
    if (sAppStartTimeNanos == 0) {
      sAppStartTimeNanos = timeNanos;
    }
  }

  /**
   * Get the application start timestamp in nanoseconds.
   * Returns 0 if not set yet.
   */
  public static long getAppStartTimeNanos() {
    return sAppStartTimeNanos;
  }
}
