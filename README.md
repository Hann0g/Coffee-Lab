# Coffee Log ☕

**Coffee Log** is a modern, minimalist coffee brew tracker, recipe manager, and bean inventory application built for Android using Jetpack Compose and Material Design 3.

---

## ✨ Features

- **☕ Daily Brew Logger**
  - Record coffee dose, water yield, brew ratio, grind size, brew time, water temperature, roaster/origin, and tasting notes.
  - 5-star rating system to remember your best extractions.
  - 1-tap Quick Brew shortcuts for your most frequent recipes.
  - Filter brew logs by star rating (★ 5, ★ 4+, etc.) and sort by date, rating, or dose weight.

- **📖 Coffee Recipes & Brew Ratio Calculator**
  - Pre-loaded specialty brewing methods: *V60 Pour Over, AeroPress, French Press, Espresso, Chemex, and Cold Brew*.
  - Create and customize your own brewing recipes with target ratios, water temperature, grind recommendations, and step-by-step guides.
  - Integrated interactive Brew Ratio Calculator (1:15 Strong, 1:16.7 Golden Ratio, 1:18 Light).
  - Average recipe ratings computed automatically from your brew history.

- **⏱️ Interactive Brew Timer**
  - Integrated timer tailored to target brew durations.
  - Live progress ring and pause/resume controls.
  - Seamless "Finish & Log" flow directly into your history.

- **🫘 Bean Bag Inventory Management**
  - Track active coffee bean bags, roasters, roast dates, and remaining weights in grams.
  - Visual remaining weight progress bar.
  - Automatic deduction from your active bag whenever a cup is logged.
  - Quick weight adjust dialog with presets (`-20g`, `-15g`, `+15g`, `+50g`) and manual precision entry.

- **📊 Brew Analytics & Stats**
  - Total brews logged and weekly volume counters.
  - Total bean weight consumed (with 250g bag equivalent metrics).
  - Average dose per cup calculation and top preferred brew method.

- **🎨 5 Dynamic Theme Palettes**
  - **Milky Coffee:** Warm latte cream & mocha browns
  - **Matcha Green:** Fresh botanical olive & forest tones
  - **Espresso Amber:** Deep roast caramel & amber accents
  - **Berry Roaster:** Vibrant fruity hibiscus & washed berry notes
  - **Nordic Slate:** Clean cold brew slate blue & charcoal neutrals
  - Persistent theme selection across app restarts.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3 (M3)
- **Language:** Kotlin 2.x
- **Architecture:** MVVM (Model-View-ViewModel) with Kotlin Coroutines and StateFlow
- **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room) with KSP
- **Asynchronous Flow:** Kotlin Coroutines & `collectAsStateWithLifecycle`

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio:** Hedgehog (2023.1.1) or newer
- **JDK:** OpenJDK 17 or 21
- **Minimum SDK:** Android 8.0 (API Level 26)
- **Target SDK:** Android 15 (API Level 35)

### Building and Running

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/coffee-log.git
   cd coffee-log
   ```

2. **Open the project in Android Studio:**
   - Select **Open an existing project** and navigate to the cloned directory.
   - Allow Gradle to sync dependencies automatically.

3. **Build the Debug APK via Gradle:**
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run on Device or Emulator:**
   - Select your target device or emulator in Android Studio and press **Run** (Shift+F10).

---

## 📂 Project Structure

```
├── app
│   ├── src
│   │   ├── main
│   │   │   ├── java/com/example
│   │   │   │   ├── data/              # Room entities, DAOs, and CoffeeDatabase
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/    # Reusable Compose dialogs & bottom sheets
│   │   │   │   │   ├── screens/       # LogScreen, RecipesScreen, StatsScreen
│   │   │   │   │   ├── theme/         # Material 3 ColorScheme & Typography
│   │   │   │   │   └── CoffeeViewModel.kt
│   │   │   │   └── MainActivity.kt
│   │   │   └── res/                   # Drawables, strings, and app launcher icons
│   │   └── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/
│   └── libs.versions.toml             # Gradle Version Catalog
└── README.md
```

---

## 📄 License

This project is open source and available under the [MIT License](LICENSE).
