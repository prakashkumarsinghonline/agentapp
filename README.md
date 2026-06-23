# Banking Baba 🏦

**Personal Finance Education App** — Get the latest videos from Banking Baba (Rakesh) in a clean, monochrome Android app.

## 📱 About

Banking Baba is an Android app that provides easy access to personal finance education videos by **Rakesh** (MBA, B.Com, ex-Bank Manager). The app fetches the latest uploads directly from the Banking Baba YouTube channel and presents them in a minimal, distraction-free interface.

**Channel:** [Banking Baba](https://www.youtube.com/@bankingbaba) — 481K+ subscribers, 360+ videos  
**Content:** ITR Filing, GST, PF, TDS, Post Office Schemes, FD, Loans, and more — all in Hindi.

## ✨ Features

- **Live YouTube RSS Feed** — Automatically fetches the latest videos
- **Video Thumbnails** — Loaded with Coil, smoothly cached
- **Tap to Watch** — Opens any video directly in YouTube
- **Clean Monochrome UI** — Pure black & white with gray accents, no gradients
- **Channel Header** — Brand bar showing Banking Baba by Rakesh
- **Subscribe Prompt** — Channel info bar with subscriber count
- **Loading & Error States** — Spinner, error messages, and empty-state handling

## 🖼️ Screenshots

*(Insert screenshots here)*

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | **Kotlin** |
| UI Framework | **Jetpack Compose** (Material3) |
| Image Loading | **Coil** |
| Architecture | Single-Activity, Composable-based |
| Data Source | YouTube RSS Feed (XML Pull Parser) |
| Build System | Gradle (Kotlin DSL) |

## 📂 Project Structure

```
app/
├── src/main/java/com/example/myapp/
│   └── MainActivity.kt          # Full app — UI, RSS parser, models
├── src/main/res/
│   ├── values/strings.xml
│   ├── values/themes.xml
│   └── AndroidManifest.xml
└── build.gradle.kts
```

## 🔧 Building

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.

## 📄 License

This project is created for educational/demonstration purposes.

---

*Built with ❤️ using Compose & Coil*