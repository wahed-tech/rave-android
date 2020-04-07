package com.flutterwave.raveandroid.rave_presentation.banktransfer;

import android.support.annotation.Nullable;

public class NullBankTransferCallback implements BankTransferCallback {
    @Override
    public void showProgressIndicator(boolean active) {

    }


    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {

    }

    @Override
    public void onSuccessful(String flwRef) {

    }

    @Override
    public void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName) {

    }

    @Override
    public void onPollingTimeout(String flwRef) {

    }
}
