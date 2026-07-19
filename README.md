# CodeMeter 📏

**Measure your code. Physically.**

![CodeMeter v1.0.0](https://img.shields.io/github/v/release/Shashwat-Gupta57/CodeMeter?color=blue&label=v1.0.0)
![License](https://img.shields.io/github/license/Shashwat-Gupta57/CodeMeter)
![Build](https://img.shields.io/github/actions/workflow/status/Shashwat-Gupta57/CodeMeter/ci.yml?branch=main)

CodeMeter is a modern, blazing-fast CLI and Terminal UI (TUI) application that doesn't just tell you how many lines of code you have—it tells you what that means in the real world. 

How many **trees** did your codebase cost? How many **Burj Khalifas** tall is it? Find out in seconds.

## ✨ Features

- 🏎️ **Blazing Fast**: Powered by `scc` and `cloc` under the hood for lightning-fast analysis.
- 🖥️ **Premium TUI**: A beautiful, mouse-friendly terminal interface built with Lanterna.
- 📊 **Rich Metrics**: Converts Lines of Code (LOC) into physical dimensions (meters, kilograms, Eiffel Towers).
- 🏆 **Achievements**: Gamify your coding with unlockable milestones.
- 📈 **History & Growth**: Track your project's evolution over time.
- 💾 **Export Anywhere**: Generate gorgeous PDF, SVG, PNG, JSON, CSV, and Markdown reports.
- 🚀 **Zero Dependencies**: Available as a native GraalVM executable for instant startup (no JVM required!).

## 🚀 Installation

### macOS
```bash
brew install codemeter
```

### Windows
```powershell
scoop install codemeter
# OR
winget install CodeMeter
```

### Linux / Manual
Head over to the [Releases](https://github.com/Shashwat-Gupta57/CodeMeter/releases) page and download the native binary for your OS. 

Alternatively, download the Fat JAR (`codemeter.jar`) and run it with:
```bash
java -jar codemeter.jar
```

## 🎮 Usage

Launch the interactive Terminal UI by simply running:
```bash
codemeter
```

### CLI Commands

Export metrics directly from the command line:
```bash
# Export as JSON
codemeter export -f json -o report.json

# Export as a beautiful PDF
codemeter export -f pdf -o report.pdf

# Export as a shareable Image Card
codemeter export -f png -o report.png
```

## 🏗️ Architecture

CodeMeter is built on **Java 21** using **Picocli** for argument parsing and **Lanterna** for the rich terminal UI. Data is serialized efficiently using **Gson** and **Toml4j**, while rendering pipelines leverage **Apache PDFBox** and **Batik** for pixel-perfect exports. The CI/CD pipeline compiles everything via **GraalVM** into native AOT executables.

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to get started.

## 🗺️ Roadmap

Check out [ROADMAP.md](ROADMAP.md) for upcoming features and planned improvements.

## 📄 License

CodeMeter is released under the MIT License. See [LICENSE](LICENSE) for more details.
