# GoTech Media — Android Architecture & Foundation

Production-oriented, scalable Android application foundation engineered for **GoTech Media**, a high-performance digital agency. Built with Kotlin, Jetpack Compose, Material 3, Clean Architecture + MVVM, StateFlow, and an Apple-inspired minimalist dark aesthetic.

---

## 1. Project Structure

```
app/src/main/java/com/gotechmedia/app/
├── GoTechApplication.kt                     # Application class initializing the DI container
├── MainActivity.kt                          # Single-activity entry point with edge-to-edge system insets
│
├── core/                                    # Cross-cutting foundational modules
│   ├── config/                              # Global configuration & environment constants
│   │   └── AppConfig.kt
│   └── utils/                               # Shared utilities & functional wrappers
│       ├── Constants.kt                     # UI durations, layout standards, test tags
│       └── Resource.kt                      # Sealed Result/Resource wrapper (Success, Error, Loading)
│
├── di/                                      # Dependency Injection Foundation
│   ├── AppContainer.kt                      # AppContainer interface & DefaultAppContainer implementation
│   └── InjectionProvider.kt                 # CompositionLocal provider & ViewModel factory helpers
│
├── data/                                    # Data Layer
│   ├── datasource/                          # Remote & Local data sources
│   │   └── AgencyDataSource.kt              # Interface + InMemory baseline ready for Firebase Firestore
│   ├── model/                               # Data transfer objects (DTOs) & entities
│   │   └── AgencyEntity.kt                  # Serialization entities with toDomain() mappers
│   └── repository/                          # Concrete repository implementations
│       └── AgencyRepositoryImpl.kt          # Maps entities to domain with Coroutine Dispatchers
│
├── domain/                                  # Domain Layer (Pure Business Logic)
│   ├── model/                               # Framework-independent domain entities
│   │   ├── AgencyProfile.kt
│   │   └── AgencyService.kt
│   ├── repository/                          # Domain repository interfaces (inversion of control)
│   │   └── AgencyRepository.kt
│   └── usecase/                             # Single-responsibility interactors
│       ├── GetAgencyProfileUseCase.kt
│       └── GetAgencyServicesUseCase.kt
│
├── navigation/                              # Navigation Foundation
│   ├── Screen.kt                            # Type-safe Screen routes
│   ├── NavGraph.kt                          # Animated NavHost with Apple-inspired slide/fade transitions
│   └── NavigationAction.kt                  # Decoupled navigation controller callbacks
│
├── presentation/                            # Presentation Layer (Jetpack Compose + MVVM)
│   ├── base/                                # Base contracts for state management
│   │   └── UiState.kt                       # Immutable UI state marker
│   ├── common/                              # Reusable Apple-inspired minimal UI components
│   │   ├── AgencyTopBar.kt                  # Minimal brand top bar with safe-area handling
│   │   ├── GlassmorphicCard.kt              # Subtle 3D, dual gradient, frosted glass surface
│   │   └── TechBadge.kt                     # Clean status badges and category pills
│   └── foundation/                          # Foundation architectural dashboard
│       ├── FoundationScreen.kt              # Agency profile, architecture verification & status
│       ├── FoundationViewModel.kt           # MVVM ViewModel with StateFlow
│       └── FoundationUiState.kt             # Immutable state definition
│
└── ui/theme/                                # Apple-Inspired Minimal Theme (Dark Mode Only)
    ├── Color.kt                             # Obsidian canvas, electric cyan accents, glass borders
    ├── Type.kt                              # High-precision typography hierarchy with custom tracking
    └── Theme.kt                             # Enforced dark theme with WindowInsets & status bar control
```

---

## 2. Why Each Layer Exists

- **`domain`**: The core of the system. Contains zero Android SDK dependencies, no third-party database imports, and no UI logic. It defines pure business models (`AgencyProfile`, `AgencyService`), repository interfaces, and specific use cases (`GetAgencyProfileUseCase`). This makes business logic 100% testable on the JVM without mocks and resilient to future framework changes.
- **`data`**: Responsible for data storage, synchronization, and communication with internal/external systems. Encapsulates `AgencyDataSource`, data transfer models (`AgencyProfileEntity`), and `AgencyRepositoryImpl`. Changes to backend APIs or databases (such as swapping in Firebase Firestore) are fully isolated to this layer.
- **`presentation`**: Houses the user interface and state observation. Follows the MVVM pattern with Jetpack Compose. ViewModels expose immutable `StateFlow<UiState>` instances consumed using `collectAsStateWithLifecycle()`. Composable functions are stateless or state-hoisted, adhering to unidirectional data flow (UDF).
- **`di`**: Decouples component construction from usage. Implements the Dependency Inversion Principle using `AppContainer`. Provides singleton scopes for dispatchers, data sources, repositories, and use cases, with seamless transition to Dagger Hilt annotations when needed.
- **`navigation`**: Centralizes navigation routing, arguments, backstack management, and transitions. Uses type-safe sealed destinations (`Screen`) and custom fluid motion transitions.
- **`core`**: Contains cross-cutting utilities (`Resource<T>`, `Constants`, `AppConfig`) needed by multiple layers without creating circular dependencies.
- **`ui/theme`**: Establishes the Apple-inspired minimal design system. Strictly enforces **Dark Mode Only** across all screens, with an obsidian color palette, titanium accents, and modern typography.

---

## 3. How Navigation is Organized

1. **Destinations (`Screen.kt`)**: Routes are modeled as a sealed class (`Screen.Foundation`, `Screen.Services`, `Screen.Portfolio`, `Screen.Contact`). Each destination provides a unique route key and title.
2. **Fluid Motion (`NavGraph.kt`)**: Built on Jetpack Navigation Compose (`androidx.navigation.compose`). Includes Apple-inspired 200ms-350ms slide and fade transitions for enters, exits, pop-enters, and pop-exits.
3. **Decoupled Controller (`NavigationActions.kt`)**: Screens do not directly interact with `NavController`. Instead, events flow up to parent actions that manage backstack pop behavior and single-top navigation.

---

## 4. How Firebase Will Later Be Integrated

The architecture is prepared for Firebase integration without touching existing UI or domain logic:

1. **Data Source Integration**:
   - Create `FirestoreAgencyDataSource : AgencyDataSource` inside `data/datasource/`.
   - Implement `streamProfile()` and `streamServices()` using `FirebaseFirestore.collection(...).snapshots()`.
2. **Authentication**:
   - Add `FirebaseAuthDataSource` in the data layer.
   - Introduce an `AuthRepository` interface in `domain/repository/` and an `AuthRepositoryImpl` in `data/repository/`.
   - Create use cases: `SignInWithGoogleUseCase`, `GetCurrentUserUseCase`, `SignOutUseCase`.
3. **Dependency Injection Binding**:
   - In `AppContainer` (or Hilt `@Provides` module), swap `InMemoryAgencyDataSource` with `FirestoreAgencyDataSource`.
   - Domain and presentation layers remain 100% unchanged, ensuring zero regression.

---

## 5. How the Application Can Scale

- **Feature Modularization**: The existing package structure (`presentation`, `domain`, `data`) can be split into Gradle subprojects (e.g., `:feature:services`, `:feature:portfolio`, `:core:data`, `:core:domain`) as the team and codebase grow.
- **Use Case Granularity**: Business rules are separated into discrete use case classes, preventing bloated ViewModels and allowing cross-feature reusability.
- **Unidirectional Data Flow (UDF)**: UI states are single immutable models (`UiState`), eliminating race conditions and inconsistent UI states.
- **Dependency Inversion**: High-level modules do not depend on low-level modules; both depend on abstractions (`AgencyRepository`), making unit testing and mocking trivial.
- **Design System Tokens**: Colors, typography, spacing, and glassmorphic cards are tokenized in `ui/theme/` and `presentation/common/`, ensuring visual consistency across all newly added agency screens.
#   G T M - A p p  
 #   G T M - A p p V 2  
 