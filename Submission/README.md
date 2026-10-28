# BizPulse 2.0 submission — CT-MAD-213

Prepared from repository commit 85faca7bdc832460bfc27994fbfb8157cfa4311e, using the original BizPulse Submission structure.

| Section | Contents |
|---|---|
| 01-Application | Released 2.0.0 APK download, checksum, installation guide and download script |
| 02-Source-Code | Current Android Studio project snapshot, including build scripts and license notices |
| 03-Documentation | Current technical documentation; original research paper and diagrams linked as historical references |
| 04-UIUX | Wireframes, current UI/UX explanation and screenshot evidence status |
| 05-Testing | Current tester-reported device results, historical build verification and release CI link |
| 06-Presentation | Updated demonstration outline and user guide |
| 07-GitHub | Repository and release URLs |

## Get the APK into this folder
From PowerShell, run:
```powershell
& ".\01-Application\Download-APK.ps1"
```
The APK is 499,095,070 bytes and exceeds GitHub's normal Git file limit. It is hosted as a permanent release asset; the script downloads it into 01-Application and verifies SHA-256. It is not embedded in this Git folder.

## Evidence still needed for a fully self-contained final hand-in
Current 2.0 screenshots, demonstration video and a revised research paper PDF/DOCX are not stored in this repository. TESTING.md records that October 27 evidence was supplied previously, but those media files are not available here. The original app's media and report are linked as historical references only. Automated raw test reports are not checked in; use the linked CI run where available. Do not treat old media/reports as current 2.0 evidence.
