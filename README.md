<p align="center">
  <h1 align="center">⬡ CodeMeter</h1>
  <p align="center"><strong>Measure your code. Physically.</strong></p>
</p>

<p align="center">
  <a href="#installation">Installation</a> •
  <a href="#features">Features</a> •
  <a href="#usage">Usage</a> •
  <a href="#screenshots">Screenshots</a> •
  <a href="#configuration">Configuration</a> •
  <a href="#contributing">Contributing</a>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/version-1.0.0-8B5CF6?style=flat-square" alt="version">
  <img src="https://img.shields.io/badge/java-21-ED8B00?style=flat-square" alt="java">
  <img src="https://img.shields.io/badge/license-MIT-green?style=flat-square" alt="license">
  <img src="https://img.shields.io/badge/TUI-Lanterna-4FC08D?style=flat-square" alt="tui">
</p>

---

Traditional LOC counters tell you how much code exists.
**CodeMeter tells you what that code looks like in the real world.**

- 📏 **Kilometers** of characters
- 📚 **Stack height** if printed
- ⚽ **Football fields** of code
- 🏗️ **Burj Khalifas** tall
- 🏔️ **Mount Everests** high
- 🌲 **Trees** required to print
- ⌨️ **Typing time** to recreate
- 📖 **Reading time** from start to finish

## Screenshots

```
╭──────────────────────────────────────────────────╮
│ ◆ CodeMeter          │  ◈ OVERVIEW               │
│   my-project         │                            │
│ ─────────────────    │  CODE STATISTICS           │
│                      │  Files .............. 1,247│
│ █ ◈ Overview         │  Languages ............ 12 │
│   📐 Physical        │  Code Lines ...... 184,291 │
│   🖨 Printed         │  Comments ........ 42,103  │
│   📈 Growth          │  Blank Lines ..... 28,447  │
│   📅 History         │  Characters ... 8,291,044  │
│   🏆 Achievements    │                            │
│   ⚖ Comparisons     │  LANGUAGE BREAKDOWN        │
│   ⚙ Settings        │  ● Java      98,241  53.3% │
│   ℹ About           │  ████████████████           │
│                      │  ● Python    42,103  22.8% │
│                      │  ████████████               │
│                      │  ● JS        28,447  15.4% │
│                      │  █████████                  │
╰──────────────────────────────────────────────────╯
```

## Installation

### Prerequisites

- **Java 21+** ([Temurin](https://adoptium.net/) recommended)
- **scc** or **cloc** (code counter backend)

```bash
# Install scc (recommended — much faster)
# macOS
brew install scc

# Linux
snap install scc

# Windows (scoop)
scoop install scc

# Or install cloc as fallback
# macOS
brew install cloc

# Linux
apt install cloc

# Windows
scoop install cloc
```

### Build from source

```bash
git clone https://github.com/codemeter/codemeter.git
cd codemeter
./gradlew shadowJar

# Run directly
java -jar build/libs/codemeter.jar

# Or add to PATH
alias codemeter='java -jar /path/to/codemeter.jar'
```

### Download release

```bash
# Download the latest release JAR
curl -L -o codemeter.jar https://github.com/codemeter/codemeter/releases/latest/download/codemeter.jar

# Run
java -jar codemeter.jar
```

## Usage

### Interactive TUI (primary interface)

```bash
# Launch the fullscreen terminal UI
codemeter
```

The TUI is the **primary interface**. Navigate with keyboard:

| Key | Action |
|-----|--------|
| `↑` `↓` | Navigate menus |
| `Enter` | Select item |
| `j` `k` | Scroll content |
| `J` `K` | Navigate sidebar |
| `1-9` | Jump to tab |
| `/` | Search |
| `q` | Back / Quit |
| `Esc` | Back |
| `PgUp` `PgDn` | Page scroll |

### CLI Commands (automation)

```bash
# Headless scan
codemeter scan .
codemeter scan /path/to/project

# Export report
codemeter export . -f json -o report.json
codemeter export . -f markdown -o report.md
codemeter export . -f csv -o report.csv
```

## Features

### 🏠 Home Screen
- Quick-scan current directory
- Resume last scan
- Recent projects list
- Global statistics
- Wrapped experience

### 📊 Dashboard
Card-based layout with sidebar navigation:

| Tab | Description |
|-----|-------------|
| **Overview** | Project summary, language breakdown with colored bars |
| **Physical** | Real-world comparisons (football fields, Burj Khalifas, etc.) |
| **Printed** | Paper/ink analysis with configurable settings |
| **Growth** | Code growth trends with sparkline graphs |
| **History** | Timeline of all scans |
| **Achievements** | Gamified milestones with progress bars |
| **Comparisons** | Side-by-side diff with previous scan |
| **Settings** | All configuration options |
| **About** | Application information |

### 📐 Physical Metrics
Converts your code into tangible real-world measurements:

- **Distance**: Character length in km, marathons, Earth circumference %
- **Sports**: Football fields, cricket grounds, basketball courts, pools
- **Landmarks**: Burj Khalifa, Empire State, Eiffel Tower, Mount Everest
- **Paper**: Pages, trees, shelf width, weight, printer trays

### 🖨 Print Analysis
Fully configurable print simulation:

- Paper sizes: A4, Letter, Legal, A3
- Fonts: JetBrains Mono, Fira Code, Cascadia Code, Consolas, etc.
- Font sizes: 6-30pt
- Ink types: Laser, Inkjet, Draft
- Calculates pages, weight, ink, cost, print time

### 🎁 Wrapped
Spotify Wrapped-style year-in-review:

1. Total lines written
2. Physical equivalent (km)
3. Most productive month
4. Language of the year
5. Achievements unlocked
6. Growth graph
7. Share card

### 🏆 Achievements
25+ gamified milestones:

- **First Scan** → **Century** (100 scans)
- **100 Files** → **Metropolis** (100K files)
- **Getting Started** (1K LOC) → **Encyclopedia** (10M LOC)
- **First Kilometer** → **Planet Walker**
- **Polyglot** → **Tower of Babel**

### 📤 Export
Generate reports in multiple formats:
- JSON (with full metrics)
- CSV
- Markdown

## Configuration

### Global Settings

Settings are stored at platform-specific locations:

| Platform | Path |
|----------|------|
| Windows | `%APPDATA%/CodeMeter/config.toml` |
| macOS | `~/Library/Application Support/CodeMeter/config.toml` |
| Linux | `~/.config/codemeter/config.toml` |

### Config File

```toml
# CodeMeter Configuration

[theme]
mode = "dark"           # dark / light / system
animations = true

[measurement]
system = "metric"       # metric / imperial

[print]
paper_size = "A4"       # A4 / LETTER / LEGAL / A3
margin_type = "normal"  # normal / narrow / custom
font = "JetBrains Mono"
font_size = 10          # 6-30
line_spacing = 1.15
ink_type = "laser"      # laser / inkjet / draft
paper_thickness_mm = 0.1

[features]
comparison_objects = true
history = true
```

### Project Storage

Each scanned project stores local history in `.codemeter/`:

```
project-root/
└── .codemeter/
    ├── project.toml
    └── history.json
```

Add `.codemeter/` to your `.gitignore` if desired.

## Architecture

```
dev.codemeter
├── CodeMeter.java              # Entry point
├── cli/                        # Picocli commands
│   ├── CodeMeterCommand.java   # Main command (launches TUI)
│   ├── ScanCommand.java        # Headless scan
│   └── ExportCommand.java      # Report export
├── core/
│   ├── model/                  # Data models (records)
│   ├── scanner/                # scc/cloc integration
│   ├── metrics/                # Physical & print calculators
│   └── storage/                # Persistence layer
├── tui/                        # Terminal UI
│   ├── TuiApp.java             # Main TUI controller
│   ├── Theme.java              # Color palette & styling
│   ├── Renderer.java           # Drawing primitives
│   └── screens/                # All panel implementations
└── export/                     # Report generation
```

### Design Principles

- **Clean architecture** with clear separation of concerns
- **Records** for immutable data models
- **Async scanning** to never freeze the UI
- **ProcessBuilder** for scanner invocation (never reimplements LOC counting)
- **Platform-aware** storage paths

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 21 |
| Build | Gradle Kotlin DSL |
| CLI | Picocli |
| TUI | Lanterna |
| JSON | Gson |
| Config | TOML (toml4j) |
| Testing | JUnit 5 + AssertJ + Mockito |
| Packaging | Shadow JAR |

## Contributing

Contributions welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development

```bash
# Build
./gradlew build

# Run tests
./gradlew test

# Run the app
./gradlew run

# Build fat JAR
./gradlew shadowJar
```

## License

MIT — see [LICENSE](LICENSE) for details.

---

<p align="center">
  <strong>CodeMeter</strong> — Measure your code. Physically.<br>
  <sub>Built with ♥ for developers who appreciate the physical weight of their work.</sub>
</p>
