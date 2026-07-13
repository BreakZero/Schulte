# Schulte Material 3 UI Redesign — Phase 0: Information Architecture

**Status:** Approved baseline for the `design/material3-ui-redesign` branch

**Scope:** Navigation and page hierarchy decisions only. This document makes no UI, state, navigation-code, persistence, security, or behavior changes.

## Goal

Establish a stable information architecture before introducing a Material 3 app shell, top app bars, or navigation bar. The design must keep the training journey focused while making records and account entry points discoverable.

## Product navigation model

### Primary destinations

The final Material 3 navigation shell will expose these three primary destinations:

| Destination | Route | Purpose | Shell treatment |
|---|---|---|---|
| **Train** | `ConfigRoute` | Configure a training session, start training, and view a compact recent-training summary. | Primary destination; selected by default. |
| **Records** | `TrainingRecordsRoute` | Browse statistics, filters, and individual training records. | Primary destination. |
| **Profile** | `ProfileRoute` | Guest/authenticated profile entry, account actions, and access to settings. | Primary destination. |

**Settings decision:** `SettingsRoute` remains a secondary route reached from Profile. It is not a primary navigation destination. This keeps the first-level navigation focused on the user’s daily goals: train, review progress, and manage identity.

### Immersive task-flow routes

The routes below are intentionally excluded from the persistent navigation bar because they represent a focused task, result, or transactional subflow:

| Route | Role | Required navigation behavior |
|---|---|---|
| `TrainingRoute` | Active timed exercise | No persistent primary navigation; exit/back resolves through the current training exit policy. |
| `ReportRoute` | Immediate result after training | No persistent primary navigation; system back resets to Train/`ConfigRoute`. |
| `AiAdviceRoute` | Contextual advice from a report | Secondary route; return to the report context. |
| `SettingsRoute` | Preferences and sensitive configuration | Secondary route under Profile. |
| `LoginRoute`, `RegisterRoute` | Authentication | Transactional subflow from Profile or contextual sign-in prompts. |
| `EditProfileRoute`, `AccountSettingsRoute` | Account maintenance | Secondary routes under Profile. |
| `PkSoonRoute` | Future feature placeholder | Secondary route; visually marked unavailable/coming soon. |

## Journey map

```text
Train (ConfigRoute)
  ├─ start training → TrainingRoute
  │                    ├─ finish → ReportRoute
  │                    │            ├─ AI advice → AiAdviceRoute → ReportRoute
  │                    │            ├─ records → TrainingRecordsRoute
  │                    │            ├─ sign in → LoginRoute / RegisterRoute
  │                    │            └─ train again → TrainingRoute
  │                    └─ exit / system back → Train (ConfigRoute)
  ├─ records summary → TrainingRecordsRoute
  ├─ profile/sign-in → ProfileRoute / LoginRoute / RegisterRoute
  └─ settings shortcut (if retained) → SettingsRoute

Records (TrainingRecordsRoute)
  └─ primary navigation → Train / Profile

Profile (ProfileRoute)
  ├─ guest → LoginRoute / RegisterRoute
  ├─ authenticated → EditProfileRoute / AccountSettingsRoute
  ├─ settings → SettingsRoute
  ├─ records → TrainingRecordsRoute
  └─ PK placeholder → PkSoonRoute
```

## Back-stack contract

Existing behavior in `AppNavigator` remains the baseline for the Material 3 redesign:

1. The app starts at `ConfigRoute`.
2. System back from `TrainingRoute` resets the stack to `ConfigRoute`.
3. System back from `ReportRoute` resets the stack to `ConfigRoute`.
4. Other secondary routes navigate up one entry when there is a previous entry.
5. Login/register success may continue using the existing route replacement/reset behavior; the redesign must not change account session semantics.
6. Selecting a primary destination must not duplicate the same route on the back stack.

The persistent navigation shell will be introduced only after its selected-state, reselect, and per-destination state-retention behavior have been implemented against this contract.

## UI-state inventory for Material 3 design work

Each screen redesign must include an intentional presentation for the following states. The state/action/viewmodel contracts remain unchanged unless a separate functional task is approved.

| Module | Required visual states |
|---|---|
| Train / Config | Ready, option selected, invalid/disabled start, recent-training empty, recent-training available. |
| Training | Active timer, correct feedback, incorrect feedback, assisted-mode explanation, exiting confirmation, completion transition. |
| Report | Report ready, record save/loading state, guest sign-in prompt, AI unconfigured, AI analysis in progress, AI failure, no historical comparison. |
| Records | Initial empty, populated list, filter controls, filtered-empty result, summary visible. |
| AI Advice | Unconfigured, loading, successful recommendation, failure/retry. |
| Settings | Guest/authenticated header, form value/error/saving state, API key masked, AI connection feedback, destructive record-clear confirmation. |
| Profile / Account | Guest, authenticated, login/register submit/error/loading, profile update, local-record association, logout confirmation, unavailable PK feature. |

## Material 3 shell decisions for the next phase

1. Use a shared Material 3 `Scaffold` in `commonMain`.
2. Show a `NavigationBar` only on Train, Records, and Profile primary destinations.
3. Use top app bars with navigation-up affordances on secondary routes.
4. Training and Report use task-focused app bars; do not show persistent `NavigationBar` there.
5. Use `SnackbarHost` for transient non-blocking feedback and `AlertDialog` for destructive/decision-required actions.
6. Treat edge-to-edge and safe areas as shared Compose Insets responsibilities; Android `enableEdgeToEdge()` and iOS full-screen hosting must remain compatible.

## Non-negotiable invariants

- UI redesign must not change training scoring, randomization, timer behavior, record persistence, account association, AI request behavior, or secure secret storage.
- Compose UI, navigation, and the Material 3 theme stay in `shared/src/commonMain`.
- `commonMain` must not import Android SDK, UIKit, SwiftUI, Foundation, Android `R`, or iOS Asset Catalog APIs.
- Settings/API-key rendering must preserve masking and must not log or persist secret values in UI state.
- Existing `State → Action → ViewModel → Event` boundaries remain in force; Composables must not acquire business decisions.

## Phase 0 acceptance checklist

- [x] Primary destinations are Train, Records, and Profile.
- [x] Settings is defined as a Profile secondary destination.
- [x] Training, Report, AI Advice, authentication, and account maintenance are defined as task/subflow routes.
- [x] Current training/report system-back behavior is explicitly preserved.
- [x] Required visual state matrix is defined for every feature module.
- [x] The next phase has a clear Material 3 shell boundary without premature UI implementation.

## Explicitly deferred

- Material 3 component implementation, app-shell code, theme/token code, screen redesign, resource migration, visual assets, and navigation-code changes.
- Automated test additions or test refactors, per the current delivery-efficiency preference.
- Full iOS Simulator runtime validation until complete Xcode is available.
