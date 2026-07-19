{
  description = "CodeMeter — Measure your code. Physically.";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { self, nixpkgs, flake-utils }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = nixpkgs.legacyPackages.${system};

        codemeter = pkgs.stdenv.mkDerivation {
          pname = "codemeter";
          version = "2.5.1";

          src = self;

          nativeBuildInputs = with pkgs; [
            jdk21
            gradle
          ];

          buildPhase = ''
            export GRADLE_USER_HOME=$(mktemp -d)
            gradle shadowJar --no-daemon
          '';

          installPhase = ''
            mkdir -p $out/bin $out/lib
            cp build/libs/codemeter.jar $out/lib/codemeter.jar
            cat > $out/bin/codemeter << EOF
            #!/usr/bin/env bash
            exec ${pkgs.jdk21}/bin/java -jar $out/lib/codemeter.jar "\$@"
            EOF
            chmod +x $out/bin/codemeter
          '';

          meta = with pkgs.lib; {
            description = "Measure your code. Physically.";
            homepage = "https://github.com/Shashwat-Gupta57/CodeMeter";
            license = licenses.mit;
            mainProgram = "codemeter";
          };
        };
      in {
        packages = {
          default = codemeter;
          codemeter = codemeter;
        };

        apps.default = flake-utils.lib.mkApp {
          drv = codemeter;
        };

        devShells.default = pkgs.mkShell {
          buildInputs = [ codemeter ];
        };
      }
    );
}
