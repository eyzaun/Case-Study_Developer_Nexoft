# Phone Book (Jetpack Compose)

[Download APK](https://github.com/eyzaun/Case-Study_Developer_Nexoft/releases/latest/download/app-release.apk)

Not: Release henüz oluşmadıysa bu bağlantı tag push sonrası otomatik olarak aktif olacaktır.

Simple phone book app built with Kotlin + Jetpack Compose following Clean Architecture. It integrates with the given REST API to manage contacts and supports saving a contact to the device address book.

## Features
- Add, edit, delete contacts (first name, last name, phone, photo)
- Lottie success animation after adding
- Contacts screen with alphabetical grouping and search with history
- Swipe-to-reveal Edit/Delete actions
- Device-contact indicator for entries already in phone contacts
- Profile screen with Change Photo, Save to My Phone, and dominant color avatar glow
- Event-State ViewModel pattern, Room caching, Coil images, Hilt DI

## Tech stack
- Kotlin, Jetpack Compose (Material3), Navigation Compose
- Hilt (DI), Retrofit/OkHttp (API), Room (cache), Coil (images), Lottie

## Build & Run (Debug)
- Android Studio Giraffe+ or Arctic Fox+
- Minimum setup: open project and build
- A debug APK can be generated via Gradle task `:app:assembleDebug`

## Download (GitHub Releases)
- Son sürüm APK: https://github.com/eyzaun/Case-Study_Developer_Nexoft/releases/latest/download/app-release.apk
	- Bu link, GitHub Actions tarafından tag atıldığında otomatik yayınlanan release’e işaret eder.

## API configuration
- Base URL: `http://146.59.52.68:11235/`
- The app expects an `ApiKey` header per API spec. Update your key in `NetworkModule.kt` if needed.

## Notes
- Phone numbers are normalized to match device contacts; saving to device updates the local flag to keep UI consistent.
- Design accents use a blue brand color and follow the provided Figma screens.

## License
This repository is for case study purposes.
