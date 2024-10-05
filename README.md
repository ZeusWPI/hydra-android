# Hydra (Android) [![CI](https://github.com/ZeusWPI/hydra-android/actions/workflows/ci.yml/badge.svg)](https://github.com/ZeusWPI/hydra-android/actions/workflows/ci.yml)

<a href='https://play.google.com/store/apps/details?id=be.ugent.zeus.hydra'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height="80px"/></a>
<a href='https://f-droid.org/packages/be.ugent.zeus.hydra.open/'><img alt='Get it on F-Droid' src='https://fdroid.gitlab.io/artwork/badge/get-it-on.png' height="80px"/></a>

The Android version of the Hydra app, available for Android 5.0 (Lollipop) and up. Available on [Google Play](https://play.google.com/store/apps/details?id=be.ugent.zeus.hydra), on [F-Droid](https://f-droid.org/packages/be.ugent.zeus.hydra.open/) or as a download in the [release section](https://github.com/ZeusWPI/hydra-android/releases) (as an APK file).

## Contributing

### Quickstart
If you don't have Android Studio or Intellij already, [download and install it](https://developer.android.com/studio/index.html).

* Clone the repo, i.e. `git clone git@github.com:ZeusWPI/hydra-android.git`
* Choose 'Import Project (gradle etc.)'
* Choose the folder you just cloned
* Have fun :)

Alternatively, you can [checkout](https://www.jetbrains.com/help/idea/set-up-a-git-repository.html#clone-repo) the repo directly from within Android Studio or Intellij.

People who use Nix(OS) can use the `flake.nix` file in the repo for a dev shell.
Note that we don't build the app using flakes; it's only used for a dev shell.
Additionally, the flake is very flaky, since Android development in Nixpkgs currently isn't the best.

### Keys
If you want to use the Google Maps integration, you will need the API keys. You can contact us for more information and to obtain the keys. _This is not required to compile and build the app._

After you've obtained the keys, you will need to copy the file `app/secrets.properties.example` to `app/secrets.properties` and insert the correct keys.

### Build variants

Hydra comes in two build variants: `store` and `open`.
The `store` variant is the main variant, and used for the Play Store and is the recommended version for most people. The `open` variant only uses open-source software (e.g. OpenStreetMaps instead of Google Maps). Since the open variant contains no crash reporting functionality, crashes from that version not accompanied by a stack trace will not be considered.

### Useful links
- [How to Write a Git Commit Message](https://chris.beams.io/posts/git-commit/)
- [Android Developer Guides](https://developer.android.com/guide/)
- [Android API Reference](https://developer.android.com/reference/)
- [Gradle User Manual](https://docs.gradle.org/current/userguide/userguide.html) (probably not needed for normal work on the app)
- [Hydra](https://mattermost.zeus.gent/zeus/channels/hydra) on Mattermost

## Other repos

* [Main repo](https://github.com/ZeusWPI/hydra) - contains the scrapers and APIs for the data and the shared resources
* [iOS](https://github.com/ZeusWPI/hydra-iOS) - iOS app

## Credits

 * [Sandwich](https://thenounproject.com/term/sandwich/222438/) by icon 54 from the Noun Project
 * [Megaphone](http://www.flaticon.com/free-icon/megaphone_3911) by Daniel Bruce from [flaticon](http://www.flaticon.com)
 
 Google Play and the Google Play logo are trademarks of Google Inc.


## License

All code is licensed under the MIT license, unless otherwise noted.
