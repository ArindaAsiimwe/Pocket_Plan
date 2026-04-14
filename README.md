# Pocket Plan 
A student budgeting and expense tracking Android app that helps university students allocate semester funds, plan goals, track daily expenses, and visualize spending patterns.

---

## Team — Group E (Mobile Programming)

| Name | Student No. |
|---|---|
| Lutalo Allan | 22/U/3330/PS |
| Tukwasiibwe Martin | 22/U/21816/eve |
| Arinda Asiimwe Atweta | 22/U/5799 |
| Nyonyozi Maria Lisa Loyse | 23/U/16424/eve |
| Beguya Melissa Deborah | 20/U/7756/PS |

---

## Features
- **Semester Budget Setup** : Allocate total semester funds into categories (rent, tuition, food, etc.) across selected months.
- **Goal-Based Expense Planning** : Protect and track important future expenses with due dates and progress indicators.
- **Daily Expense Tracking** : Log everyday spending by category with optional receipt/photo proof via camera.
- **Spending Insights** : Donut and bar charts showing spending by category and monthly trends.

---

## Tech Stack
Kotlin · Jetpack Compose · MVVM · Clean Architecture · Room · Hilt · Navigation Compose · StateFlow · CameraX

---

## Project Structure

```
com.example.pocketplan/
├── data/
│   ├── local/              # AppDatabase, Converters, DAOs
│   ├── model/              # Models (User, Budget, Category, Goal, Expense)
│   └── repository/         # Repository interfaces and implementations
├── di/                     # Hilt modules (AppModule, RepositoryModule)
├── domain/
│   └── usecase/            # AddExpenseUseCase, CreateGoalUseCase,
│                             GetBudgetSummaryUseCase, LoginUseCase
├── ui/
│   ├── auth/               # LoginScreen, RegisterScreen, AuthViewModel
│   ├── budget/             # BudgetSetupScreen, BudgetViewModel
│   ├── goals/              # GoalsScreen, GoalsViewModel
│   ├── insights/           # InsightsScreen, InsightsViewModel
│   ├── tracking/           # ExpenseTrackingScreen, ExpenseTrackingViewModel
│   ├── navigation/         # AppNavGraph, Screen (sealed class)
│   └── theme/
└── utils/                  # Constants, CurrencyUtils (UGX), DateUtils
```

---

## Getting Started

**Prerequisites:** Android Studio Hedgehog+, JDK 17, min SDK 24

```bash
git clone https://github.com/ArindaAsiimwe/Pocket_Plan.git
```
Open in Android Studio, let Gradle sync, then run on an emulator or device (API 24+).

---

## Git Workflow

```bash
# Create a feature branch — never commit directly to master
git checkout -b your-name/feature-name

# Push and open a Pull Request
git push origin your-name/feature-name
```

**Commit prefixes:** `feat:` `fix:` `ui:` `refactor:` `docs:`

---

*Makerere University — Mobile Programming Coursework*
