# CodeMeter Release Checklist

Use this checklist before every public release. Every item must be verified.

## Pre-Release

- [ ] Version bumped in `build.gradle.kts`
- [ ] Version bumped in `CodeMeter.java` (`VERSION` constant)
- [ ] `./gradlew clean test` passes locally
- [ ] `./gradlew shadowJar` produces `build/libs/codemeter.jar`
- [ ] All commands run without error: `scan`, `config`, `wrapped`, `compare`, `history`, `doctor`, `stats`, `benchmark`, `export`, `milestones`
- [ ] `codemeter --help` renders the custom help layout correctly
- [ ] `codemeter --version` prints the correct version
- [ ] `codemeter doctor` reports no critical issues
- [ ] README.md accurately describes all commands and installation methods
- [ ] No TODO/FIXME/placeholder comments remain in source code
- [ ] No binary files (`.exe`, `.jar`, `.class`) are tracked in Git

## Release

- [ ] Create and push a version tag: `git tag vX.Y.Z && git push origin vX.Y.Z`
- [ ] Verify GitHub Actions `Release` workflow completes on all 5 platforms
- [ ] Verify GitHub Release contains all expected assets:
  - [ ] `codemeter-linux-amd64`
  - [ ] `codemeter-linux-arm64`
  - [ ] `codemeter-macos-amd64`
  - [ ] `codemeter-macos-arm64`
  - [ ] `codemeter-windows-amd64.exe`
  - [ ] `codemeter.jar`
  - [ ] `codemeter-sbom.json`
  - [ ] `SHA256SUMS`
- [ ] Verify SHA256SUMS file contains hashes for every asset

## Distribution

- [ ] **Winget**: Verify `publish-winget` job succeeded (PR submitted to `microsoft/winget-pkgs`)
- [ ] **Scoop**: Verify `bucket/codemeter.json` was pushed to `Shashwat-Gupta57/scoop-bucket`
  - [ ] Verify the JSON is valid (`scoop cat shashwat-gupta57/codemeter` after adding the bucket)
  - [ ] Verify `scoop install shashwat-gupta57/codemeter` completes
- [ ] **Homebrew**: Verify `Formula/codemeter.rb` was pushed to `Shashwat-Gupta57/homebrew-tap`
  - [ ] Verify `brew install Shashwat-Gupta57/homebrew-tap/codemeter` completes on macOS
- [ ] **Nix**: Verify `nix run github:Shashwat-Gupta57/CodeMeter` executes

## Post-Release

- [ ] Download each native binary from the release and verify it runs
- [ ] Verify `codemeter scan .` produces a valid Story on at least one real repository
- [ ] Update any external documentation or blog posts
