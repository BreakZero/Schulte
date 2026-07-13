#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
XCODE_PROJECT="$PROJECT_ROOT/iosApp/iosApp.xcodeproj"
SCHEME="${SCHEME:-iosApp}"
CONFIGURATION="${CONFIGURATION:-Debug}"
DERIVED_DATA_PATH="${DERIVED_DATA_PATH:-${TMPDIR:-/tmp}/SchulteDerivedData}"

if [[ -z "${SIMULATOR_UDID:-}" ]]; then
  SIMULATOR_UDID="$(
    xcrun simctl list devices booted |
      sed -E -n 's/.*\(([0-9A-Fa-f-]{36})\) \(Booted\).*/\1/p' |
      head -n 1
  )"
fi

if [[ -z "$SIMULATOR_UDID" ]]; then
  echo "No booted iOS Simulator found."
  echo "Open Simulator first, or run with SIMULATOR_UDID=<device-uuid>."
  exit 1
fi

echo "Using iOS Simulator: $SIMULATOR_UDID"

xcrun simctl boot "$SIMULATOR_UDID" 2>/dev/null || true
xcrun simctl bootstatus "$SIMULATOR_UDID" -b

echo "Building $SCHEME ($CONFIGURATION)..."
xcodebuild \
  -project "$XCODE_PROJECT" \
  -scheme "$SCHEME" \
  -configuration "$CONFIGURATION" \
  -destination "platform=iOS Simulator,id=$SIMULATOR_UDID" \
  -derivedDataPath "$DERIVED_DATA_PATH" \
  CODE_SIGNING_ALLOWED=NO \
  build

APP_PATH="$DERIVED_DATA_PATH/Build/Products/${CONFIGURATION}-iphonesimulator/Schulte.app"

if [[ ! -d "$APP_PATH" ]]; then
  echo "Built app not found at: $APP_PATH"
  exit 1
fi

BUNDLE_ID="${BUNDLE_ID:-$(/usr/libexec/PlistBuddy -c 'Print :CFBundleIdentifier' "$APP_PATH/Info.plist")}"

echo "Installing $APP_PATH..."
xcrun simctl install "$SIMULATOR_UDID" "$APP_PATH"

xcrun simctl terminate "$SIMULATOR_UDID" "$BUNDLE_ID" >/dev/null 2>&1 || true

echo "Launching $BUNDLE_ID..."
xcrun simctl launch "$SIMULATOR_UDID" "$BUNDLE_ID"

echo "Schulte is running on Simulator $SIMULATOR_UDID."
