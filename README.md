# Spendee

Spendee is an Android application that helps users manage their finances efficiently. Built with a focus on user experience, the app allows for setting balance, managing expenses, budget and financial goals with an intuitive and responsive design.

## Presentation

![Demo](demo/Navigation.gif)

## Features
- Set and update current balance
- Add and edit expenses
- Add and edit budget
- Add and edit financial goals
- Automatic budget renewal
- Notifications for exceeding the budget and reaching the goals

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/PiotrDynia/Spendee.git
   ```
2. Open the project in Android Studio.
3. Build the project and run on an emulator or connected device.

## Technologies Used

- Kotlin
- Jetpack Compose
- Room Database
- Dagger/Hilt
- JUnit 4
- Mockito

## Architecture

The app follows the MVVM (Model-View-ViewModel) architecture to ensure separation of concerns and facilitate testability and scalability.

## How to Use

### Home screen
1. Click on 'Set balance' button to update balance
2. Click on an expense to show details and edit it
3. Click on 'Show more' to go to expenses screen

### Expenses screen
1. Click on an expense to show details and edit it
2. Click on floating plus button to add a new expense
3. Swipe expense to the right to delete it
4. Swipe expense to the left to edit it
5. Long press on an expense to show options and edit or delete expense

### Budget screen
1. When no budget is set, click on 'Set budget' to create one
2. When budget is set, click on three dots button in the upper right corner to edit or delete budget

### Goals screen
1. When no goals are set, click on 'Set a financial goal' to create a goal
2. When at least one goal is set, click on a goal to show details and edit it
3. Click on floating plus button to add a new goal
4. Swipe goal to the right to delete it
5. Swipe goal to the left to edit it
6. Long press on a goal to show options and edit or delete goal