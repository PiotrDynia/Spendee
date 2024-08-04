package com.example.spendee.data.entities

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.spendee.R


sealed class ExpenseCategory(
    val id: Int,
    @StringRes val name: Int,
    @DrawableRes val iconResource: Int
) {
    data object Entertainment : ExpenseCategory(1, R.string.entertainment, R.drawable.ic_entertainment)
    data object Payments : ExpenseCategory(2, R.string.payments, R.drawable.ic_payments)
    data object Transport : ExpenseCategory(3, R.string.transport, R.drawable.ic_transport)
    data object Personal: ExpenseCategory(4, R.string.personal, R.drawable.ic_personal)
    data object House: ExpenseCategory(5, R.string.house, R.drawable.ic_house)
    data object Everyday : ExpenseCategory(6, R.string.everyday, R.drawable.ic_everyday)
    data object Health : ExpenseCategory(7, R.string.health, R.drawable.ic_health)
    data object Uncategorized : ExpenseCategory(8, R.string.uncategorized, R.drawable.ic_uncategorized)

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