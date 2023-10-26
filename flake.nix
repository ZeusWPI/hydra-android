{
  description = "Hydra app for Android";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    devshell = {
      url = "github:numtide/devshell";
      inputs.nixpkgs.follows = "nixpkgs";
    };
  };

  outputs = { self, nixpkgs, devshell, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { 
          inherit system;
          overlays = [ devshell.overlays.default ];
          config.allowUnfree = true;
          config.android_sdk.accept_license = true;
        };
        androidComposition = pkgs.androidenv.composeAndroidPackages {
            cmdLineToolsVersion = "12.0-rc15";
            toolsVersion = null;
            platformToolsVersion = "34.0.4";
            buildToolsVersions = [ "34.0.0" ];
            includeEmulator = false;
            platformVersions = [ "34" ];
            # Enable once nixpkgs is fixed...
            includeSources = false;
            includeNDK = false;
            useGoogleAPIs = true;
            includeExtras = [
              "extras;google;gcm"
            ];
            extraLicenses = [
              "android-sdk-license"
            ];
        };
      in
      {
        devShells = rec {
          default = hydra-android;
          hydra-android = pkgs.devshell.mkShell {
            name = "hydra-android";
            packages = [
              androidComposition.androidsdk pkgs.jdk11 pkgs.git pkgs.androidStudioPackages.beta
            ];
            env = [
              {
                name = "JAVA_HOME";
                eval = pkgs.jdk17.home;
              }
              {
                name = "ANDROID_SDK_ROOT";
                eval = "${androidComposition.androidsdk}/libexec/android-sdk";
              }
              {
                name = "GRADLE_OPTS";
                eval = "-Dorg.gradle.project.android.aapt2FromMavenOverride=${androidComposition.androidsdk}/libexec/android-sdk/build-tools/34.0.0/aapt2";
              }
            ];
            commands = [
            ];
          };
        };
      }
    );
}
