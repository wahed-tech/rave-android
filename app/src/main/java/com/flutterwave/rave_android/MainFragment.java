package com.flutterwave.rave_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.RavePayManager;
import com.flutterwave.raveandroid.rave_presentation.card.Card;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager;
import com.flutterwave.raveandroid.rave_presentation.card.SavedCardsListener;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveutils.verification.AVSVBVFragment;
import com.flutterwave.raveutils.verification.OTPFragment;
import com.flutterwave.raveutils.verification.PinFragment;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;

import java.util.ArrayList;
import java.util.List;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_REQUEST_CODE;


public class MainFragment
        extends Fragment
        // Interfaces to implement for custom UI or no-UI usage
        implements
        FeeCheckListener, // Implement if you want to be able to check fees beforehand
        SavedCardsListener, // Implement if you want to be able to save cards and charge saved cards
        CardPaymentCallback {// Must be implemented to charge cards with custom UI or no-UI

    EditText emailEt;
    EditText amountEt;
    EditText publicKeyEt;
    EditText encryptionKeyEt;
    EditText txRefEt;
    EditText narrationEt;
    EditText currencyEt;
    EditText countryEt;
    EditText fNameEt;
    EditText lNameEt;
    EditText durationEt;
    EditText frequencyEt;
    EditText phoneNumberEt;
    Button startPayBtn;
    Button addVendorBtn;
    Button firstBtn;
    Button secondBtn;
    Button clearVendorBtn;
    SwitchCompat cardSwitch;
    SwitchCompat accountSwitch;
    SwitchCompat ghMobileMoneySwitch;
    SwitchCompat ugMobileMoneySwitch;
    SwitchCompat ukbankSwitch;
    SwitchCompat saBankSwitch;
    SwitchCompat francMobileMoneySwitch;
    SwitchCompat rwfMobileMoneySwitch;
    SwitchCompat zmMobileMoneySwitch;
    SwitchCompat bankTransferSwitch;
    SwitchCompat isPermanentAccountSwitch;
    SwitchCompat setExpirySwitch;
    SwitchCompat ussdSwitch;
    SwitchCompat barterSwitch;
    SwitchCompat isLiveSwitch;
    SwitchCompat isMpesaSwitch;
    SwitchCompat accountAchSwitch;
    SwitchCompat addSubAccountsSwitch;
    SwitchCompat useRaveUISwitch;
    SwitchCompat isPreAuthSwitch;
    SwitchCompat allowSavedCardsSwitch;
    SwitchCompat shouldDisplayFeeSwitch;
    SwitchCompat shouldShowStagingLabelSwitch;
    List<Meta> meta = new ArrayList<>();
    List<SubAccount> subAccounts = new ArrayList<>();
    LinearLayout addSubaccountsLayout;
    LinearLayout expiryDetailsLayout;
    TextView vendorListTXT;

    ProgressDialog progressDialog;
    private CardPaymentManager cardPayManager;
    private Card card;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        emailEt = rootView.findViewById(R.id.emailEt);
        amountEt = rootView.findViewById(R.id.amountEt);
        publicKeyEt = rootView.findViewById(R.id.publicKeyEt);
        encryptionKeyEt = rootView.findViewById(R.id.encryptionEt);
        txRefEt = rootView.findViewById(R.id.txRefEt);
        narrationEt = rootView.findViewById(R.id.narrationTV);
        currencyEt = rootView.findViewById(R.id.currencyEt);
        countryEt = rootView.findViewById(R.id.countryEt);
        fNameEt = rootView.findViewById(R.id.fNameEt);
        lNameEt = rootView.findViewById(R.id.lnameEt);
        phoneNumberEt = rootView.findViewById(R.id.phoneNumberEt);
        durationEt = rootView.findViewById(R.id.expiryDaysEt);
        frequencyEt = rootView.findViewById(R.id.frequencyEt);
        startPayBtn = rootView.findViewById(R.id.startPaymentBtn);
        cardSwitch = rootView.findViewById(R.id.cardPaymentSwitch);
        allowSavedCardsSwitch = rootView.findViewById(R.id.allowSavedCardsSwitch);
        accountSwitch = rootView.findViewById(R.id.accountPaymentSwitch);
        accountAchSwitch = rootView.findViewById(R.id.accountAchSwitch);
        isMpesaSwitch = rootView.findViewById(R.id.accountMpesaSwitch);
        isPreAuthSwitch = rootView.findViewById(R.id.isPreAuthSwitch);
        shouldDisplayFeeSwitch = rootView.findViewById(R.id.isDisplayFeeSwitch);
        ghMobileMoneySwitch = rootView.findViewById(R.id.accountGHMobileMoneySwitch);
        ugMobileMoneySwitch = rootView.findViewById(R.id.accountUgMobileMoneySwitch);
        ukbankSwitch = rootView.findViewById(R.id.accountUkbankSwitch);
        saBankSwitch = rootView.findViewById(R.id.accountSaBankSwitch);
        francMobileMoneySwitch = rootView.findViewById(R.id.accountfrancMobileMoneySwitch);
        zmMobileMoneySwitch = rootView.findViewById(R.id.accountZmMobileMoneySwitch);
        rwfMobileMoneySwitch = rootView.findViewById(R.id.accountRwfMobileMoneySwitch);
        bankTransferSwitch = rootView.findViewById(R.id.bankTransferSwitch);
        isPermanentAccountSwitch = rootView.findViewById(R.id.isPermanentSwitch);
        setExpirySwitch = rootView.findViewById(R.id.setExpirySwitch);
        bankTransferSwitch = rootView.findViewById(R.id.bankTransferSwitch);
        expiryDetailsLayout = rootView.findViewById(R.id.expiry_layout);
        ussdSwitch = rootView.findViewById(R.id.ussd_switch);
        barterSwitch = rootView.findViewById(R.id.barter_switch);
        isLiveSwitch = rootView.findViewById(R.id.isLiveSwitch);
        addSubAccountsSwitch = rootView.findViewById(R.id.addSubAccountsSwitch);
        useRaveUISwitch = rootView.findViewById(R.id.useRaveUISwitch);
        shouldShowStagingLabelSwitch = rootView.findViewById(R.id.shouldShowStagingLabelSwitch);
        addVendorBtn = rootView.findViewById(R.id.addVendorBtn);
        firstBtn = rootView.findViewById(R.id.first);
        secondBtn = rootView.findViewById(R.id.second);
        clearVendorBtn = rootView.findViewById(R.id.clearVendorsBtn);
        vendorListTXT = rootView.findViewById(R.id.refIdsTV);
        vendorListTXT.setText("Your current vendor refs are: ");

        bankTransferSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isPermanentAccountSwitch.setVisibility(View.VISIBLE);
                    setExpirySwitch.setVisibility(View.VISIBLE);
                } else {
                    isPermanentAccountSwitch.setVisibility(View.GONE);
                    setExpirySwitch.setVisibility(View.GONE);
                    expiryDetailsLayout.setVisibility(View.GONE);
                }
            }
        });

        cardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isPreAuthSwitch.setVisibility(View.VISIBLE);
                    allowSavedCardsSwitch.setVisibility(View.VISIBLE);
                } else {
                    isPreAuthSwitch.setVisibility(View.GONE);
                    allowSavedCardsSwitch.setVisibility(View.GONE);
                }
            }
        });

        isPermanentAccountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setExpirySwitch.setVisibility(View.GONE);
                    expiryDetailsLayout.setVisibility(View.GONE);
                } else setExpirySwitch.setVisibility(View.VISIBLE);
            }
        });
        setExpirySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    expiryDetailsLayout.setVisibility(View.VISIBLE);
                    isPermanentAccountSwitch.setVisibility(View.GONE);
                } else {
                    expiryDetailsLayout.setVisibility(View.GONE);
                    isPermanentAccountSwitch.setVisibility(View.VISIBLE);
                }
            }
        });

        publicKeyEt.setText(RaveConstants.PUBLIC_KEY);
        encryptionKeyEt.setText(RaveConstants.ENCRYPTION_KEY);

        addSubaccountsLayout = rootView.findViewById(R.id.addSubAccountsLayout);

        meta.add(new Meta("test key 1", "test value 1"));
        meta.add(new Meta("test key 2", "test value 2"));

        startPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEntries(amountEt.getText().toString());
            }
        });

        addSubAccountsSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    addSubaccountsLayout.setVisibility(View.VISIBLE);
                } else {
                    clear();
                }
            }
        });

        addVendorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVendorDialog();
            }
        });
        clearVendorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

        firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEntries("3.0");
            }
        });

        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateEntries("");
            }
        });

        getParentFragmentManager().setFragmentResultListener(RAVE_REQUEST_CODE + "", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                Log.d("okh", "i am listening");
                String message = result.getString("response");
                int resultCode = result.getInt("resultCode");

                if (message != null) {
                    Log.d("rave response", message);
                }

                if (getActivity() != null) {
                    if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                        Toast.makeText(requireContext(), "SUCCESS " + message, Toast.LENGTH_SHORT).show();
                    } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                        Toast.makeText(requireContext(), "ERROR " + message, Toast.LENGTH_SHORT).show();
                    } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                        Toast.makeText(requireContext(), "CANCELLED " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return rootView;
    }

    private void clear() {
        subAccounts.clear();
        vendorListTXT.setText("Your current vendor refs are: ");
        addSubaccountsLayout.setVisibility(View.GONE);
        addSubAccountsSwitch.setChecked(false);
    }

    private void validateEntries(String amounts) {
        clearErrors();
        String email = emailEt.getText().toString();
        String amount = amounts;
        String publicKey = publicKeyEt.getText().toString();
        String encryptionKey = encryptionKeyEt.getText().toString();
        String txRef = txRefEt.getText().toString();
        String narration = narrationEt.getText().toString();
        String currency = currencyEt.getText().toString();
        String fName = fNameEt.getText().toString();
        String lName = lNameEt.getText().toString();
        String phoneNumber = phoneNumberEt.getText().toString();
        String accountDuration = durationEt.getText().toString();
        String accountPaymentFrequency = frequencyEt.getText().toString();

        boolean valid = true;

        if (amount.length() == 0) {
            amount = "0";
        }

        //isAmountValid for compulsory fields
        if (!Utils.isEmailValid(email)) {
            valid = false;
            emailEt.setError("A valid email is required");
        }

        if (publicKey.length() < 1) {
            valid = false;
            publicKeyEt.setError("A valid public key is required");
        }

        if (phoneNumber.length() < 7) {
            valid = false;
            phoneNumberEt.setError("A valid phone number is required");
        }

        if (encryptionKey.length() < 1) {
            valid = false;
            encryptionKeyEt.setError("A valid encryption key is required");
        }

        if (txRef.length() < 1) {
            valid = false;
            txRefEt.setError("A valid txRef key is required");
        }

        if (currency.length() < 1) {
            valid = false;
            currencyEt.setError("A valid currency code is required");
        }

        if (setExpirySwitch.isChecked()) {
            if (accountDuration.isEmpty()) {
                valid = false;
                durationEt.setError("Please enter expiry duration (in days)");
            }
            if (accountPaymentFrequency.isEmpty()) {
                valid = false;
                frequencyEt.setError("Please enter payment frequency");
            }
        }

        if (valid) {
            RavePayManager raveManager;

            boolean shouldUseRaveUi = useRaveUISwitch.isChecked();

            if (shouldUseRaveUi) {

                raveManager = new RaveUiManager(this)
                        .acceptMpesaPayments(isMpesaSwitch.isChecked())
                        .acceptAccountPayments(accountSwitch.isChecked())
                        .acceptCardPayments(cardSwitch.isChecked())
                        .allowSaveCardFeature(allowSavedCardsSwitch.isChecked(), true)
                        .acceptAchPayments(accountAchSwitch.isChecked())
                        .acceptGHMobileMoneyPayments(ghMobileMoneySwitch.isChecked())
                        .acceptUgMobileMoneyPayments(ugMobileMoneySwitch.isChecked())
                        .acceptZmMobileMoneyPayments(zmMobileMoneySwitch.isChecked())
                        .acceptRwfMobileMoneyPayments(rwfMobileMoneySwitch.isChecked())
                        .acceptUkPayments(ukbankSwitch.isChecked())
                        .acceptSaBankPayments(saBankSwitch.isChecked())
                        .acceptFrancMobileMoneyPayments(francMobileMoneySwitch.isChecked())
                        .acceptBankTransferPayments(bankTransferSwitch.isChecked())
                        .acceptUssdPayments(ussdSwitch.isChecked())
                        .acceptBarterPayments(barterSwitch.isChecked())
                        //                    .withTheme(R.style.TestNewTheme)
                        .showStagingLabel(shouldShowStagingLabelSwitch.isChecked())
                        .setAmount(Double.parseDouble(amount))
                        .setCurrency(currency)
                        .setEmail(email)
                        .setfName(fName)
                        .setlName(lName)
                        .setPhoneNumber(phoneNumber, false)
                        .setNarration(narration)
                        .setPublicKey(publicKey)
                        .setEncryptionKey(encryptionKey)
                        .setTxRef(txRef)
                        .onStagingEnv(!isLiveSwitch.isChecked())
                        .setSubAccounts(subAccounts)
                        .isPreAuth(isPreAuthSwitch.isChecked())
                        .setMeta(meta)
                        .shouldDisplayFee(shouldDisplayFeeSwitch.isChecked());

                if (amount.equals("0")) {
                    ((RaveUiManager) raveManager).acceptZmMobileMoneyPayments(true);
                    ((RaveUiManager) raveManager).acceptGHMobileMoneyPayments(true);
                    ((RaveUiManager) raveManager).showStagingLabel(true);
                    ((RaveUiManager) raveManager).onStagingEnv(true);
                }

                ((RaveUiManager) raveManager).embedFragment(R.id.container, (AppCompatActivity) getActivity());

                // Customize pay with bank transfer options (optional)
                if (isPermanentAccountSwitch.isChecked())
                    ((RaveUiManager) raveManager).acceptBankTransferPayments(true, true);
                else {
                    if (setExpirySwitch.isChecked()) {
                        int duration = 0, frequency = 0;
                        try {
                            duration = Integer.parseInt(durationEt.getText().toString());
                            frequency = Integer.parseInt(frequencyEt.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        ((RaveUiManager) raveManager).acceptBankTransferPayments(true, duration, frequency);
                    }
                }

                raveManager.initialize();

            } else {
                raveManager = new RaveNonUIManager().setAmount(Double.parseDouble(amount))
                        .setCurrency(currency)
                        .setEmail(email)
                        .setfName(fName)
                        .setlName(lName)
                        .setPhoneNumber(phoneNumber)
                        .setNarration(narration)
                        .setPublicKey(publicKey)
                        .setEncryptionKey(encryptionKey)
                        .setTxRef(txRef)
                        .onStagingEnv(!isLiveSwitch.isChecked())
                        .setSubAccounts(subAccounts)
                        .setMeta(meta)
                        .setUniqueDeviceId("1")
                        .isPreAuth(isPreAuthSwitch.isChecked())
                        .initialize();

                cardPayManager = new CardPaymentManager(((RaveNonUIManager) raveManager), this, this);
                card = new Card(
                        "5531886652142950", // Test MasterCard PIN authentication
//                        "4242424242424242", // Test VisaCard 3D-Secure Authentication
//                        "4556052704172643", // Test VisaCard (Address Verification)
                        "12",
                        "30",
                        "123"
                );

//                cardPayManager.fetchSavedCards();
//                cardPayManager.fetchTransactionFee(card,this);
                cardPayManager.chargeCard(card);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RaveConstants.RESULT_SUCCESS) {
            switch (requestCode) {
                case RaveConstants.PIN_REQUEST_CODE:
                    String pin = data.getStringExtra(PinFragment.EXTRA_PIN);
                    // Use the collected PIN
                    cardPayManager.submitPin(pin);
                    break;
                case RaveConstants.ADDRESS_DETAILS_REQUEST_CODE:
                    String streetAddress = data.getStringExtra(AVSVBVFragment.EXTRA_ADDRESS);
                    String state = data.getStringExtra(AVSVBVFragment.EXTRA_STATE);
                    String city = data.getStringExtra(AVSVBVFragment.EXTRA_CITY);
                    String zipCode = data.getStringExtra(AVSVBVFragment.EXTRA_ZIPCODE);
                    String country = data.getStringExtra(AVSVBVFragment.EXTRA_COUNTRY);
                    AddressDetails address = new AddressDetails(streetAddress, city, state, zipCode, country);

                    // Use the address details
                    cardPayManager.submitAddress(address);
                    break;
                case RaveConstants.WEB_VERIFICATION_REQUEST_CODE:
                    // Web authentication complete, proceed
                    cardPayManager.onWebpageAuthenticationComplete();
                    break;
                case RaveConstants.OTP_REQUEST_CODE:
                    String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                    // Use OTP
                    cardPayManager.submitOtp(otp);
                    break;
            }
        }

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {

            String message = data.getStringExtra("response");

            if (message != null) {
                Log.d("rave response", message);
            }

            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(requireContext(), "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(requireContext(), "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(requireContext(), "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == RaveConstants.WEB_VERIFICATION_REQUEST_CODE) {
            cardPayManager.onWebpageAuthenticationComplete();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void clearErrors() {
        emailEt.setError(null);
        amountEt.setError(null);
        publicKeyEt.setError(null);
        encryptionKeyEt.setError(null);
        txRefEt.setError(null);
        narrationEt.setError(null);
        currencyEt.setError(null);
        countryEt.setError(null);
        fNameEt.setError(null);
        lNameEt.setError(null);
        durationEt.setError(null);
        frequencyEt.setError(null);
    }

    private void addVendorDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rave_sdk_add_vendor_layout, null);
        dialogBuilder.setView(dialogView);
        final EditText vendorReferenceET = dialogView.findViewById(R.id.vendorReferecnceET);
        final EditText vendorRatioET = dialogView.findViewById(R.id.vendorRatioET);
        Button addVendorBtn = dialogView.findViewById(R.id.doneDialogBtn);
        Button cancelDialogBtn = dialogView.findViewById(R.id.cancelDialogBtn);
        final AlertDialog alertDialog = dialogBuilder.create();
        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        addVendorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                String vendorRef = vendorReferenceET.getText().toString().trim();
                String vendorRatio = vendorRatioET.getText().toString().trim();

                if (vendorRef.length() < 1) {
                    vendorReferenceET.setError("Vendor reference is required");
                    valid = false;
                }
                if (vendorRatioET.length() < 1) {
                    vendorRatioET.setError("Vendor ratio is required");
                    valid = false;
                }
                if (!valid) {
                    return;
                }
                if (subAccounts.size() != 0) {
                    vendorListTXT.setText(vendorListTXT.getText().toString() + ", " + vendorRef + "(" + vendorRatio + ")");
                } else {
                    vendorListTXT.setText(vendorListTXT.getText().toString() + vendorRef + "(" + vendorRatio + ")");
                }
                subAccounts.add(new SubAccount(vendorRef, vendorRatio));
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        try {
            if (getActivity().isFinishing()) {
                return;
            }

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(requireContext());
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");
            }

            if (active && !progressDialog.isShowing()) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectCardPin() {
        new RaveVerificationUtils(this, isLiveSwitch.isChecked(), publicKeyEt.getText().toString())
                .showPinScreen();
    }

    @Override
    public void collectOtp(String message) {
        new RaveVerificationUtils(this, isLiveSwitch.isChecked(), publicKeyEt.getText().toString())
                .showOtpScreen(message);
    }

    @Override
    public void collectAddress() {
        Toast.makeText(requireContext(), "Submitting address details", Toast.LENGTH_SHORT).show();
        new RaveVerificationUtils(this, isLiveSwitch.isChecked(), publicKeyEt.getText().toString())
                .showAddressScreen();
    }

    @Override
    public void showAuthenticationWebPage(String authenticationUrl) {
        Toast.makeText(requireContext(), "Loading auth web page", Toast.LENGTH_SHORT).show();

        // Load webpage
        new RaveVerificationUtils(this, isLiveSwitch.isChecked(), publicKeyEt.getText().toString())
                .showWebpageVerificationScreen(authenticationUrl);
    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {
        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessful(String flwRef) {
        Toast.makeText(requireContext(), "Transaction Successful", Toast.LENGTH_LONG).show();
//        cardPayManager.saveCard(); // Save card if needed
    }

    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {
        // Check that the list is not empty, show the user to select which they'd like to charge, then proceed to chargeSavedCard()
        if (cards.size() != 0) cardPayManager.chargeSavedCard(cards.get(0));
        else
            Toast.makeText(requireContext(), "No saved cards found for " + phoneNumber, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSavedCardsLookupFailed(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteSavedCardRequestSuccessful() {

    }

    @Override
    public void onDeleteSavedCardRequestFailed(String message) {

    }

    @Override
    public void collectOtpForSaveCardCharge() {
        collectOtp("Otp for saved card");
    }

    @Override
    public void onCardSaveSuccessful(String phoneNumber) {

    }

    @Override
    public void onCardSaveFailed(String message) {

    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, String fee) {
        // Display the fee to the customer
        Toast.makeText(requireContext(), "The transaction fee is " + fee, Toast.LENGTH_SHORT).show();
//        cardPayManager.chargeCard(card);
    }

    @Override
    public void onFetchFeeError(String errorMessage) {

    }
}
