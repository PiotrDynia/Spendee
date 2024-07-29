package com.example.spendee.data.entities


sealed class ExpenseCategory(
    val id: Int,
    val name: String,
    val iconResource: String
) {
    data object Entertainment : ExpenseCategory(1, "Entertainment", "ic_entertainment")
    data object Payments : ExpenseCategory(2, "Payments", "ic_payments")
    data object Transport : ExpenseCategory(3, "Transport", "ic_transport")
    data object Personal: ExpenseCategory(4, "Personal", "ic_personal")
    data object House: ExpenseCategory(5, "House", "ic_house")
    data object Everyday : ExpenseCategory(6, "Everyday", "ic_everyday")
    data object Health : ExpenseCategory(7, "Health", "ic_health")
    data object Uncategorized : ExpenseCategory(8, "Uncategorized", "ic_uncategorized")

    companion object {
        fun fromId(id: Int): ExpenseCategory? {
            return when (id) {
                1 -> Entertainment
                2 -> Payments
                3 -> Transport
                4 -> Personal
                5 -> House
                6 -> Everyday
                7 -> Health
                8 -> Uncategorized
                else -> null
            }
        }

        fun getAllCategories(): List<ExpenseCategory> {
            return listOf(
                Entertainment,
                Payments,
                Transport,
                Personal,
                House,
                Everyday,
                Health,
                Uncategorized
            )
        }
    }
}