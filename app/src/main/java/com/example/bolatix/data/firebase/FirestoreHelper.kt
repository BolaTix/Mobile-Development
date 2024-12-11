package com.example.bolatix.data.firebase

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirestoreHelper {

    private val db = FirebaseFirestore.getInstance()

    fun addTicketToPurchaseHistory(userId: String, ticketData: Map<String, Any>, onComplete: (Boolean, String?) -> Unit) {
        val userDocRef = db.collection("users").document(userId)

        userDocRef.update("purchase_history", FieldValue.arrayUnion(ticketData))
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    fun getUserData(userId: String, onComplete: (DocumentSnapshot?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                onComplete(document)
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
}