package com.example.lahcomprahv2.analytics

import android.os.Bundle
import com.example.lahcomprahv2.models.Product
import com.google.firebase.analytics.FirebaseAnalytics

class ProductAnalytics(
    private val firebaseAnalytics: FirebaseAnalytics
) {

    fun logProductCreated(product: Product) {
        firebaseAnalytics.logEvent(EVENT_PRODUCT_CREATED, product.toAnalyticsParams())
    }

    fun logProductUpdated(product: Product) {
        firebaseAnalytics.logEvent(EVENT_PRODUCT_UPDATED, product.toAnalyticsParams())
    }

    fun logProductDeleted(product: Product) {
        firebaseAnalytics.logEvent(EVENT_PRODUCT_DELETED, product.toAnalyticsParams())
    }

    private fun Product.toAnalyticsParams(): Bundle {
        return Bundle().apply {
            putString(PARAM_PRODUCT_ID, id)
            putString(PARAM_PRODUCT_NAME, nombre)
            putLong(PARAM_QUANTITY, cantidad.toLong())
        }
    }

    private companion object {
        const val EVENT_PRODUCT_CREATED = "product_created"
        const val EVENT_PRODUCT_UPDATED = "product_updated"
        const val EVENT_PRODUCT_DELETED = "product_deleted"

        const val PARAM_PRODUCT_ID = "product_id"
        const val PARAM_PRODUCT_NAME = "product_name"
        const val PARAM_QUANTITY = "quantity"
    }
}
