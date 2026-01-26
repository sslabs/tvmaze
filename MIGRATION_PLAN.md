# TVmaze Android Modernization Plan

## Progress

- [x] **Phase 1:** Build System Foundation
- [x] **Phase 2:** KAPT to KSP Migration
- [ ] **Phase 3:** Kotlin 2.0 Upgrade
- [ ] **Phase 4:** LiveData to StateFlow Migration
- [ ] **Phase 5:** Jetpack Compose Setup
- [ ] **Phase 6:** Compose UI Migration
- [ ] **Phase 7:** Navigation 3 Migration
- [ ] **Phase 8:** Final Cleanup

---

## Target State

| Component | Before | After |
|-----------|--------|-------|
| Gradle/AGP | 8.0.0-rc01 | 8.7+ |
| Kotlin | 1.8.10 | 2.0+ |
| SDK | 33 | 35 |
| Annotation Processing | KAPT | KSP |
| Dependency Management | build.gradle | Version Catalogs |
| UI | XML + View Binding | Jetpack Compose |
| Navigation | Navigation 2.5.3 | Navigation 3 |
| State Management | LiveData | StateFlow |
| Image Loading | Glide | Coil |
| Material Design | Material 2 | Material 3 |

---

## Phase 1: Build System Foundation

Convert build files to Kotlin DSL, implement Version Catalogs, and update SDK versions.

**Changes:**
- Convert `settings.gradle` → `settings.gradle.kts`
- Convert `build.gradle` → `build.gradle.kts`
- Convert `app/build.gradle` → `app/build.gradle.kts`
- Create `gradle/libs.versions.toml`
- Update `compileSdk`/`targetSdk` to 35

---

## Phase 2: KAPT to KSP Migration

Replace KAPT with KSP for faster builds and better Kotlin support.

**Changes:**
- Add KSP plugin
- Migrate Hilt compiler to KSP
- Migrate Room compiler to KSP
- Remove Glide KAPT
- Remove KAPT plugin

---

## Phase 3: Kotlin 2.0 Upgrade

Upgrade to Kotlin 2.0+ and add serialization support for Navigation 3.

**Changes:**
- Upgrade Kotlin to 2.0+
- Add Kotlin Serialization plugin
- Fix deprecation warnings

---

## Phase 4: LiveData to StateFlow Migration

Modernize state management in all ViewModels.

**Changes:**
- Migrate `CatalogViewModel` to StateFlow
- Migrate `EpisodesViewModel` to StateFlow
- Migrate `EpisodeDetailsViewModel` to StateFlow
- Migrate `MainViewModel` to StateFlow
- Update all Fragment observers to use `collectAsStateWithLifecycle`
- Update ViewModel tests

---

## Phase 5: Jetpack Compose Setup

Add Compose infrastructure alongside existing Views.

**Changes:**
- Enable Compose build features
- Add Compose BOM and dependencies
- Add Coil for image loading
- Create Material 3 theme (`ui/theme/`)

---

## Phase 6: Compose UI Migration

Migrate screens from XML to Compose (simplest to most complex).

**Migration Order:**
1. EpisodeDetailsFragment → `EpisodeDetailsScreen.kt`
2. ShowDetailsFragment → `ShowDetailsScreen.kt`
3. SeasonEpisodesFragment → `SeasonEpisodesScreen.kt`
4. EpisodesFragment → `EpisodesScreen.kt`
5. ShowFragment → `ShowScreen.kt`
6. CatalogFragment → `CatalogScreen.kt`
7. SettingsFragment → `SettingsScreen.kt`

**Cleanup:**
- Remove Glide
- Remove XML layouts
- Remove RecyclerView adapters

---

## Phase 7: Navigation 3 Migration

Replace Fragment-based navigation with Navigation 3.

**Changes:**
- Add Navigation 3 dependencies
- Add `@Serializable` to data models
- Create type-safe navigation routes
- Implement Compose `NavHost`
- Update `MainActivity`
- Remove Navigation 2.x, Safe Args, and all Fragments

---

## Phase 8: Final Cleanup

Remove all legacy code and dependencies.

**Changes:**
- Remove deprecated dependencies (Glide, Fragment KTX, View Binding)
- Delete unused DI modules
- Add Compose UI tests
- Add Turbine for StateFlow testing

---

## References

- [Navigation 3 Guide](https://developer.android.com/guide/navigation/navigation-3)
- [Nav3 Recipes](https://github.com/android/nav3-recipes)
- [Compose Migration](https://developer.android.com/develop/ui/compose/migrate)
