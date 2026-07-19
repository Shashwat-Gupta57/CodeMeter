# CodeMeter 📏

**Measure your code. Physically.**

![Version](https://img.shields.io/github/v/release/Shashwat-Gupta57/CodeMeter?color=blue)
![License](https://img.shields.io/github/license/Shashwat-Gupta57/CodeMeter)
![Build](https://img.shields.io/github/actions/workflow/status/Shashwat-Gupta57/CodeMeter/ci.yml?branch=main)

CodeMeter is a modern, blazing-fast CLI application that doesn't just tell you how many lines of code you have—it tells you what that means in the real world. 

How many **trees** did your codebase cost? How many **Burj Khalifas** tall is it? Find out in seconds.

## ✨ Features

- 🏎️ **Blazing Fast**: Powered by `scc` and `cloc` under the hood for lightning-fast analysis.
- 🎨 **Premium Aesthetic**: A beautifully styled, editorial-grade command-line interface. No TUI required.
- 📊 **Rich Metrics**: Converts Lines of Code (LOC) into physical dimensions (meters, kilograms, Eiffel Towers).
- 🏆 **Achievements**: Gamify your coding with unlockable milestones and ranks.
- 📈 **History & Growth**: Track your project's evolution over time and compare against previous states.
- 💾 **Export Anywhere**: Generate gorgeous PDF, SVG, PNG, JSON, CSV, and Markdown reports.
- 🚀 **Zero Dependencies**: Available as a native GraalVM executable for instant startup (no JVM required!).

## 🚀 Installation

### macOS (Homebrew)
```bash
brew tap Shashwat-Gupta57/homebrew-tap
brew install codemeter
```

### Windows (Winget)
> **Note**: Do not use `winget install codemeter` as it points to an unrelated package. Use the exact identifier below:
```powershell
winget install ShashwatGupta.CodeMeter
```

### Windows (Scoop)
```powershell
scoop bucket add shashwat-gupta57 https://github.com/Shashwat-Gupta57/scoop-bucket
# Install explicitly from the bucket to avoid local folder name collisions
scoop install shashwat-gupta57/codemeter
```

### Nix / NixOS
```bash
nix run github:Shashwat-Gupta57/CodeMeter
```

### Linux / Manual Download
Head over to the [Releases](https://github.com/Shashwat-Gupta57/CodeMeter/releases) page and download the native binary for your OS (Windows, macOS Intel/ARM, Linux x64/ARM). 

Alternatively, download the Fat JAR (`codemeter.jar`) and run it with:
```bash
java -jar codemeter.jar
```

## 🎮 Commands

CodeMeter is a fully-featured command-line tool. You can view all commands at any time using `codemeter --help`.

### The Story Mode
The core experience of CodeMeter.
```bash
# Scan the current directory
codemeter scan .

# Scan a specific project
codemeter scan ../my-project

# View a beautifully crafted end-of-year summary
codemeter wrapped
```

### Tracking & Analysis
```bash
# View your chronological scan history across all projects
codemeter history

# Compare your current workspace against the last scan
codemeter compare

# Check your progress towards your next achievement rank
codemeter milestones

# Output raw script-friendly data metrics for CI/CD integrations
codemeter stats
```

### Utilities
```bash
# Export metrics directly to a file (json, pdf, png, svg, csv, md)
codemeter export -f png -o report.png

# Configure your physical assumptions (e.g. thickness of paper)
codemeter config

# Run system diagnostics
codemeter doctor

# Measure parsing performance
codemeter benchmark
```

## 🏗️ Architecture

CodeMeter is built on **Java 21** using **Picocli** for argument parsing. Data is serialized efficiently using **Gson** and **Toml4j**, while rendering pipelines leverage **Apache PDFBox** and **Batik** for pixel-perfect exports. The CI/CD pipeline compiles everything via **GraalVM** into native AOT executables.

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to get started.

## 📄 License

CodeMeter is released under the MIT License. See [LICENSE](LICENSE) for more details.
