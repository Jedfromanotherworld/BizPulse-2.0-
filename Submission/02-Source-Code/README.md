# Source code

BizPulse-2.0/ is the project snapshot at commit 85faca7bdc832460bfc27994fbfb8157cfa4311e. Open that directory in Android Studio. It contains the current source/documentation snapshot, not a recursively copied Submission folder.

The native AI dependency is a Git submodule. For a reliable full source checkout:
```powershell
git clone --recurse-submodules https://github.com/Jedfromanotherworld/BizPulse-2.0-.git
cd BizPulse-2.0-
git checkout 85faca7bdc832460bfc27994fbfb8157cfa4311e
git submodule update --init --recursive
python scripts/download_model.py
.\gradlew.bat assembleDebug testDebugUnitTest assembleDebugAndroidTest
```
Use JDK 17, Android SDK 34, Build Tools 34.0.0, NDK 27.2.12479018 and CMake 3.22.1. Configure local.properties for your SDK. GitHub ZIP downloads do not populate Git submodules; use the clone procedure above. The large model is fetched and checksum-verified by scripts/download_model.py. See the project README and licenses.
