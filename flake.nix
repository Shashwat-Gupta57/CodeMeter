{
  description = "Measure your code. Physically.";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};

        version = "2.3.5";

        hashes = {
          x86_64-linux = "sha256-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
          aarch64-linux = "sha256-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
          x86_64-darwin = "sha256-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
          aarch64-darwin = "sha256-AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
        };

        urls = {
          x86_64-linux = "https://github.com/Shashwat-Gupta57/CodeMeter/releases/download/v${version}/codemeter-linux-amd64";
          aarch64-linux = "https://github.com/Shashwat-Gupta57/CodeMeter/releases/download/v${version}/codemeter-linux-arm64";
          x86_64-darwin = "https://github.com/Shashwat-Gupta57/CodeMeter/releases/download/v${version}/codemeter-macos-amd64";
          aarch64-darwin = "https://github.com/Shashwat-Gupta57/CodeMeter/releases/download/v${version}/codemeter-macos-arm64";
        };

        binaryUrl = urls.${system} or (throw "Unsupported system: ${system}");
        binaryHash = hashes.${system} or (throw "Unsupported system: ${system}");

        codemeter = pkgs.stdenv.mkDerivation {
          pname = "codemeter";
          inherit version;

          src = pkgs.fetchurl {
            url = binaryUrl;
            hash = binaryHash;
          };

          dontUnpack = true;

          installPhase = ''
            mkdir -p $out/bin
            cp $src $out/bin/codemeter
            chmod +x $out/bin/codemeter
          '';

          meta = with pkgs.lib; {
            description = "Measure your code. Physically.";
            homepage = "https://github.com/Shashwat-Gupta57/CodeMeter";
            license = licenses.mit;
            platforms = [ "x86_64-linux" "aarch64-linux" "x86_64-darwin" "aarch64-darwin" ];
            mainProgram = "codemeter";
          };
        };
      in {
        packages = {
          default = codemeter;
          codemeter = codemeter;
        };

        apps = {
          default = flake-utils.lib.mkApp {
            drv = codemeter;
          };
        };

        devShells.default = pkgs.mkShell {
          buildInputs = [
            codemeter
          ];
        };
      }
    );
}
