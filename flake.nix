{
  description = "Hydra app for Android";

  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
    android-nixpkgs = {
      # url = "github:tadfisher/android-nixpkgs";

      # The main branch follows the "canary" channel of the Android SDK
      # repository. Use another android-nixpkgs branch to explicitly
      # track an SDK release channel.
      #
      # url = "github:tadfisher/android-nixpkgs/stable";
      url = "github:tadfisher/android-nixpkgs/beta";
      # url = "github:tadfisher/android-nixpkgs/preview";
      # url = "github:tadfisher/android-nixpkgs/canary";

      # If you have nixpkgs as an input, this will replace the "nixpkgs" input
      # for the "android" flake.
      #
      inputs.nixpkgs.follows = "nixpkgs";
      inputs.flake-utils.follows = "flake-utils";
    };
    devshell = {
      url = "github:numtide/devshell";
      inputs = {
        flake-utils.follows = "flake-utils";
        nixpkgs.follows = "nixpkgs";
      };
    };
  };

  outputs = { self, nixpkgs, devshell, flake-utils, android-nixpkgs }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { 
          inherit system;
          overlays = [ devshell.overlay ];
          config.allowUnfree = true;
        };
        android-sdk = android-nixpkgs.sdk.${system} (sdkPkgs: with sdkPkgs; [
          cmdline-tools-latest
          build-tools-32-0-0
          platform-tools
          platforms-android-32
          sources-android-32
          emulator
          system-images-android-21-google-apis-x86
        ]);
      in
      {
        devShells = rec {
          default = hydra-android;
          hydra-android = pkgs.devshell.mkShell {
            name = "hydra-android";
            packages = [
              android-sdk pkgs.jdk11 pkgs.git pkgs.androidStudioPackages.beta
            ];
            env = [
              {
                name = "JAVA_HOME";
                eval = pkgs.jdk11.home;
              }
              {
                name = "ANDROID_SDK_ROOT";
                eval = "${android-sdk}/share/android-sdk";
              }
            ];
            commands = [
            ];
          };
        };
      }
    );
}
