package com.kuanhsien.app.sample.google_pay_with_stripe_demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.AutoResolveHelper
import kotlinx.android.synthetic.main.activity_main.*
import android.app.Activity
import com.google.android.gms.wallet.PaymentData
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import com.kuanhsien.app.sample.google_pay_with_stripe_demo.model.PriceInfoModel
import com.kuanhsien.app.sample.google_pay_with_stripe_demo.utils.GooglePayConstant
import com.kuanhsien.app.sample.google_pay_with_stripe_demo.utils.GooglePayUtil
import com.google.android.material.snackbar.Snackbar
import com.stripe.android.model.Token

private const val LOAD_PAYMENT_DATA_REQUEST_CODE = 100
private const val TAG = "GOOGLE_PAY_DEMO"


class MainActivity : AppCompatActivity() {

    private lateinit var mPaymentsClient: PaymentsClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /**
         * Step 1:
         *      Create an instance of PaymentsClient to access the Payments APIs.
         */
        mPaymentsClient = GooglePayUtil.createPaymentsClient(this)

        /**
         * Step 2:
         *      Check if google pay supported on this device
         */
        isReadyToPay()

        /**
         * Step 3:
         *      Create a request of the transaction details
         *      While click the buy button, send the request to google pay
         */
        button_buy.setOnClickListener {

            val priceInfo = PriceInfoModel(textview_price.text.toString(), GooglePayConstant.CURRENCY_CODE)
            val request = GooglePayUtil.createPaymentDataRequest(priceInfo)

            button_buy.isClickable = false

            AutoResolveHelper.resolveTask(
                GooglePayUtil.createPaymentsClient(this).loadPaymentData(request),
                this@MainActivity,
                LOAD_PAYMENT_DATA_REQUEST_CODE
            )
        }
    }

    /**
     * Step 4:
     *      Get stripe token from google pay
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult: requestCode = $requestCode, resultCode = $resultCode")

        when (requestCode) {

            LOAD_PAYMENT_DATA_REQUEST_CODE -> {

                when (resultCode) {
                    Activity.RESULT_OK -> {
                        if (data != null) {
                            val paymentData = PaymentData.getFromIntent(data)
                            // You can get some data on the user's card, such as the brand and last 4 digits
                            val info = paymentData!!.cardInfo
                            // You can also pull the user address from the PaymentData object.
                            val address = paymentData.shippingAddress
                            // This is the raw JSON string version of your Stripe token.
                            val rawToken = paymentData.paymentMethodToken?.token

                            // Now that you have a Stripe token object, charge that by using the id
                            val stripeToken = Token.fromString(rawToken)
                            if (stripeToken != null) {

                                // TODO:
                                // This chargeToken function is a call to your own server, which should then connect
                                // to Stripe's API to finish the charge.
                                // chargeToken(stripeToken.id)
                                textview_stripe_token.text = stripeToken.id
                                Snackbar.make(root_view, "stripeToken = ${stripeToken.id}", Snackbar.LENGTH_LONG).show()
                                Log.d(TAG, "stripeToken = ${stripeToken.id}")
                            } else {
                                Log.d(TAG, "stripeToken is null in Activity.RESULT_OK")
                            }
                        }
                        Log.d(TAG, "data is null in Activity.RESULT_OK")
                    }

                    Activity.RESULT_CANCELED -> {
                    }

                    AutoResolveHelper.RESULT_ERROR -> {
                        // Log the status for debugging
                        // Generally there is no need to show an error to
                        // the user as the Google Payment API will do that
                        val status = AutoResolveHelper.getStatusFromIntent(data)
                        Log.w("loadPaymentData failed", String.format("Error code: %d", status?.statusCode))
                    }
                }

                // Re-enables the Google Pay payment button.
                button_buy.isClickable = true
            }
        }
        // Handle any other startActivityForResult calls you may have made.
    }


    /**
     *  Create an instance of IsReadyToPayRequest to see whether or not to display Google Pay as an option.
     */
    private fun isReadyToPay() {
        val request = GooglePayUtil.isReadyToPayRequest()

        GooglePayUtil.createPaymentsClient(this).isReadyToPay(request)    // type: Task<Boolean>
            ?.addOnCompleteListener {
                try {
                    val isGooglePayEnable = it.getResult(ApiException::class.java)

                    if (isGooglePayEnable == true) {
                        //show Google as payment option
                        button_buy.isVisible = isGooglePayEnable

                    } else {
                        //hide Google as payment option
                        button_buy.isVisible = false

                        Toast.makeText(this, "Google Pay is not available on this device", Toast.LENGTH_LONG).show()
                        Log.d(TAG, "Google Pay is not available on this device")
                    }
                } catch (exception: ApiException) {
                }
            }
    }
}
