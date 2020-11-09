package com.flutterwave.raveutils.verification;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_logger.events.StartTypingEvent;
import com.flutterwave.raveutils.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.EMBED_FRAGMENT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.OTP_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RESULT_SUCCESS;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VERIFICATION_REQUEST_KEY;
import static com.flutterwave.raveutils.verification.Utils.REQUEST_CODE;
import static com.flutterwave.raveutils.verification.Utils.RESULT_CODE;
import static com.flutterwave.raveutils.verification.VerificationActivity.PUBLIC_KEY_EXTRA;

/**
 * A simple {@link Fragment} subclass.
 */
public class OTPFragment extends Fragment implements View.OnClickListener {

    public static final String EXTRA_OTP = "extraOTP";
    public static final String IS_SAVED_CARD_CHARGE = "is_saved_card_charge";
    private Boolean isSavedCardCharge = false;
    TextInputEditText otpEt;
    TextInputLayout otpTil;
    TextView chargeMessage;
    Button otpButton;

    @Inject
    EventLogger logger;

    public static final String EXTRA_CHARGE_MESSAGE = "extraChargeMessage";
    View v;
    String otp;

    public OTPFragment() {
        // Required empty public constructor
    }

    private boolean embedFragment = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.rave_sdk_fragment_ot, container, false);

        if (getArguments() != null) {
            embedFragment = getArguments().getBoolean(EMBED_FRAGMENT);
            if (getArguments().getInt("theme", 0) != 0) {
                getActivity().setTheme(getArguments().getInt("theme", 0));
            }
        }

        injectComponents(embedFragment);

        logEvent(new ScreenLaunchEvent("OTP Fragment").getEvent());

        initializeViews();

        setChargeMessage();

        setIsSavedCardCharge();

        setListeners();

        Utils.onBackPressed(embedFragment, this, (AppCompatActivity) getActivity());

        return v;
    }

    private void setIsSavedCardCharge() {
        if (getArguments() != null) {
            if (getArguments().containsKey(IS_SAVED_CARD_CHARGE)) {
                isSavedCardCharge = getArguments().getBoolean(IS_SAVED_CARD_CHARGE);
            }
        }
    }

    private void setListeners() {
        otpButton.setOnClickListener(this);

        otpEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    logEvent(new StartTypingEvent("OTP").getEvent());
                }
            }
        });
    }

    private void initializeViews() {
        otpTil = v.findViewById(R.id.otpTil);
        otpEt = v.findViewById(R.id.otpEv);
        otpButton = v.findViewById(R.id.otpButton);
        chargeMessage = v.findViewById(R.id.otpChargeMessage);
    }

    private void injectComponents(boolean embedFragment) {
        if (getActivity() != null) {
            if (embedFragment) {
                VerificationFragment.getVerificationComponent()
                        .inject(this);
            } else {
                ((VerificationActivity) getActivity()).getVerificationComponent()
                        .inject(this);
            }
        }
    }

    private void logEvent(Event event) {
        if (getArguments() != null
                & getArguments().getString(PUBLIC_KEY_EXTRA) != null
                & logger != null) {
            String publicKey = getArguments().getString(PUBLIC_KEY_EXTRA);
            event.setPublicKey(publicKey);
            logger.logEvent(event);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.otpButton) {
            clearErrors();
            otp = otpEt.getText().toString();
            if (otp.length() < 1) {
                otpTil.setError("Enter a valid one time password");
            } else {
                if (getActivity() !=null){
                    Utils.hideKeyboard(getActivity());
                }
                goBack();
            }
        }
    }

    private void clearErrors() {
        otpTil.setErrorEnabled(false);
        otpTil.setError(null);
    }

    private void setChargeMessage() {
        if (getArguments() != null) {
            if (getArguments().containsKey(EXTRA_CHARGE_MESSAGE)) {
                chargeMessage.setText(getArguments().getString(EXTRA_CHARGE_MESSAGE));
            }
        }
    }

    public void goBack() {

        if (embedFragment) {
            Bundle bundle = new Bundle();
            bundle.putInt(REQUEST_CODE, OTP_REQUEST_CODE);
            bundle.putString(EXTRA_OTP, otp);
            bundle.putInt(RESULT_CODE, RESULT_SUCCESS);
            bundle.putBoolean(IS_SAVED_CARD_CHARGE, isSavedCardCharge);
            getParentFragmentManager().setFragmentResult(VERIFICATION_REQUEST_KEY, bundle);

        } else {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_OTP, otp);
            //inform onActivityResult of if this is a saved card charge and how to handle.
            intent.putExtra(IS_SAVED_CARD_CHARGE, isSavedCardCharge);

            if (getActivity() != null) {
                getActivity().setResult(RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        }

    }

}
