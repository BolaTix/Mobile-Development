package com.example.bolatix.data.firebase

import com.google.firebase.firestore.DocumentSnapshot

class FirebaseService(private val authHelper: FirebaseAuthHelper, private val firestoreHelper: FirestoreHelper) {

    fun addTicketToPurchaseHistory(userId: String, ticketData: Map<String, Any>, onComplete: (Boolean, String?) -> Unit) {
        firestoreHelper.addTicketToPurchaseHistory(userId, ticketData, onComplete)
    }

    fun getUserData(userId: String, onComplete: (DocumentSnapshot?) -> Unit) {
        firestoreHelper.getUserData(userId, onComplete)
    }

}