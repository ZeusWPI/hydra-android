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
        androidVersions = builtins.fromTOML (builtins.readFile ./android-versions.toml);
        androidComposition = pkgs.androidenv.composeAndroidPackages {
            cmdLineToolsVersion = androidVersions.cmdLineToolsVersion;
            toolsVersion = null;
            platformToolsVersion = androidVersions.platformToolsVersion;
            buildToolsVersions = [ androidVersions.buildToolsVersions ];
            includeEmulator = false;
            platformVersions = [ androidVersions.platformVersions ];
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
