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

import com.google.android.gms.wallet.WalletConstants

object GooglePayConstant {

    /**
    * ENVIRONMENT_PRODUCTION will make the API return chargeable card information.
    */
    const val PAYMENTS_ENVIRONMENT = WalletConstants.ENVIRONMENT_TEST

    const val PAYMENTS_GATEWAY_KEY = "gateway"
    const val PAYMENTS_GATEWAY_VAL = "stripe"
    const val STRIPE_KEY_PUBLISHABLE_KEY = "stripe:publishableKey"
    const val STRIPE_VAL_PUBLISHABLE_VAL = "pk_test_TYooMQauvdEDq54NiTphI7jx"
    const val STRIPE_VERSION_KEY = "stripe:version"
    const val STRIPE_VERSION_VAL = "2018-11-08"

    const val CURRENCY_CODE = "USD"
}