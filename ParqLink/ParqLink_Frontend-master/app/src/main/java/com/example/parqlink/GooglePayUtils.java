package com.example.parqlink;



import com.google.android.gms.wallet.PaymentDataRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GooglePayUtils {

    public static PaymentDataRequest createPaymentDataRequest(double montoTotal) {
        try {
            JSONObject paymentDataRequestJson = getBaseRequest();
            paymentDataRequestJson.put("allowedPaymentMethods", getAllowedPaymentMethods());
            paymentDataRequestJson.put("transactionInfo", getTransactionInfo(montoTotal));
            paymentDataRequestJson.put("merchantInfo", getMerchantInfo());

            return PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject getBaseRequest() throws JSONException {
        JSONObject baseRequest = new JSONObject();
        baseRequest.put("apiVersion", 2);
        baseRequest.put("apiVersionMinor", 0);
        return baseRequest;
    }

    private static JSONArray getAllowedPaymentMethods() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");

        JSONObject parameters = new JSONObject();
        parameters.put("allowedAuthMethods", new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS"));
        parameters.put("allowedCardNetworks", new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA"));

        cardPaymentMethod.put("parameters", parameters);
        cardPaymentMethod.put("tokenizationSpecification", getTokenizationSpecification());

        JSONArray paymentMethods = new JSONArray();
        paymentMethods.put(cardPaymentMethod);

        return paymentMethods;
    }

    private static JSONObject getTokenizationSpecification() throws JSONException {
        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");

        JSONObject parameters = new JSONObject();
        parameters.put("gateway", "example");
        parameters.put("gatewayMerchantId", "exampleMerchantId");

        tokenizationSpecification.put("parameters", parameters);
        return tokenizationSpecification;
    }

    private static JSONObject getTransactionInfo(double montoTotal) throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", String.format("%.2f", montoTotal));
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("currencyCode", "EUR");
        return transactionInfo;
    }

    private static JSONObject getMerchantInfo() throws JSONException {
        JSONObject merchantInfo = new JSONObject();
        merchantInfo.put("merchantName", "ParqLink");
        return merchantInfo;
    }
}

