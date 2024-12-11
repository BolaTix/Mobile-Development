package com.example.bolatix.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName


data class User(
    var id: String? = null,
    var name: String? = null,
    var email: String? = null,

    @get:PropertyName("profile_picture")
    @set:PropertyName("profile_picture")
    var profilePicture: String? = null,

    @get:PropertyName("favorite_team")
    @set:PropertyName("favorite_team")
    var favoriteTeam: String? = null,

    @get:PropertyName("birth_date")
    @set:PropertyName("birth_date")
    var birthday: String? = null,

    var gender: String? = null,

    @get:PropertyName("phone_number")
    @set:PropertyName("phone_number")
    var phoneNumber: String? = null,

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Timestamp = Timestamp.now(),

    @get:PropertyName("purchase_history")
    @set:PropertyName("purchase_history")
    var purchaseHistory: List<PurchaseHistory> = emptyList()
)

@IgnoreExtraProperties
data class PurchaseHistory(
    @get:PropertyName("order_id")
    val orderId: String = "",

    @get:PropertyName("match_id")
    val matchId: String = "",

    @get:PropertyName("away_team")
    val awayTeam: String = "",

    @get:PropertyName("home_team")
    val homeTeam: String = "",

    @get:PropertyName("purchase_date")
    val purchaseDate: Timestamp = Timestamp.now(),

    val stadium: String = "",

    @get:PropertyName("ticket_quantity")
    val ticketQuantity: Int = 0,

    @get:PropertyName("total_price")
    val totalPrice: Double = 0.0,

    @get:PropertyName("notification")
    var notification: Boolean = false,

    @get:PropertyName("match_date")
    val matchDate: String = "",

    val gate: List<Int> = emptyList()
)