/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kuanhsien.app.sample.google_pay_with_stripe_demo.utils

import android.app.Activity
import com.kuanhsien.app.sample.google_pay_with_stripe_demo.model.PriceInfoModel
import com.google.android.gms.wallet.*
import java.util.*

object GooglePayUtil {

    /**
     * Creates an instance of [PaymentsClient] for use in an [Activity]
     * using the environment and theme set in [Constants].
     */
    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
            .setEnvironment(GooglePayConstant.PAYMENTS_ENVIRONMENT)
            .build()

        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    /**
     * Check if user's device could support google pay
     */
    fun isReadyToPayRequest(): IsReadyToPayRequest =
        IsReadyToPayRequest.newBuilder()
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
            .build()

    /**
     * Create a PaymentMethodTokenizationParameters object with your credentials for using Stripe as gateway
     */
    private fun createTokenizationParameters(): PaymentMethodTokenizationParameters {
        return PaymentMethodTokenizationParameters.newBuilder()
            .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
            .addParameter(GooglePayConstant.PAYMENTS_GATEWAY_KEY, GooglePayConstant.PAYMENTS_GATEWAY_VAL)
            .addParameter(GooglePayConstant.STRIPE_KEY_PUBLISHABLE_KEY, GooglePayConstant.STRIPE_VAL_PUBLISHABLE_VAL)
            .addParameter(GooglePayConstant.STRIPE_VERSION_KEY, GooglePayConstant.STRIPE_VERSION_VAL)
            .build()
    }

    /**
     * Create the PaymentDataRequest object with the information relevant to the purchase.
     * Including price, allowable payment methods and tokenizationParameters (created by createTokenizationParameters())
     */
    fun createPaymentDataRequest(priceInfo: PriceInfoModel): PaymentDataRequest {
        return PaymentDataRequest.newBuilder()

            // Create transaction info (including price and currency)
            .setTransactionInfo(
                TransactionInfo.newBuilder()
                    .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                    .setTotalPrice(priceInfo.price)   // set price
                    .setCurrencyCode(priceInfo.currency)
                    .build()
            )

            // Set allowed payment method
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
            .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)

            // Define supported card networks
            .setCardRequirements(
                CardRequirements.newBuilder()
                    .addAllowedCardNetworks(
                        Arrays.asList(
                            WalletConstants.CARD_NETWORK_AMEX,
                            WalletConstants.CARD_NETWORK_DISCOVER,
                            WalletConstants.CARD_NETWORK_VISA,
                            WalletConstants.CARD_NETWORK_MASTERCARD))
                    .build()
            )

            // Choose Stripe as a gateway (Could choose DIRECT or GATEWAY as payment tokenization method
            .setPaymentMethodTokenizationParameters(createTokenizationParameters())
            .build()
    }

}