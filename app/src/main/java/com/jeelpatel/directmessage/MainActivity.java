package com.jeelpatel.directmessage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jeelpatel.directmessage.Helper.BannerAdHelper;
import com.jeelpatel.directmessage.Helper.CheckForUpdateHelper;
import com.jeelpatel.directmessage.Helper.InterstitialAdHelper;
import com.jeelpatel.directmessage.databinding.ActivityMainBinding;

import java.util.Objects;

// VERSION 1(1.1)
public class MainActivity extends AppCompatActivity {

    private static final String privacyPolicy = "https://ellipsetech.blogspot.com/2023/04/direct-messaging-app-privacy-policy.html";
    private static final String termsAndCondition = "https://ellipsetech.blogspot.com/2023/04/direct-messaging-app-terms-conditions.html";
    private static final String AD_UNIT_ID = "ca-app-pub-6349327669214019/9363166692";
    private static final String AD_UNIT_ID_interstitial = "ca-app-pub-6349327669214019/2152597377";
    ActivityMainBinding binding;
    GoogleSignInClient gsc;
    InterstitialAdHelper interstitialAdHelper;
    CheckForUpdateHelper checkForUpdateHelper;
    BannerAdHelper bannerAdHelper;
    String scannedData;
    private String number;
    private String message;
    private String mainURL;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());  // set Content View

        bannerAdHelper = new BannerAdHelper();
        bannerAdHelper.loadBanner(MainActivity.this, AD_UNIT_ID, binding.bannerAdContainer, getWindowManager());// INITIALIZE ADMOB BANNER LOAD METHOD

        interstitialAdHelper = new InterstitialAdHelper(MainActivity.this, AD_UNIT_ID_interstitial);
        interstitialAdHelper.loadInterstitialAd(); // INITIALIZE ADMOB INTERSTITIAL AD LOAD METHOD

        checkForUpdateHelper = new CheckForUpdateHelper(MainActivity.this);
        checkForUpdateHelper.checkForUpdate();// INITIALIZE CHECK FOR UPDATE METHOD

        initializeAuthAndGoogleSignIn1();   // INITIALIZE GOOGLE AUTHENTICATION METHOD

        binding.buttonSendToWhats.setOnClickListener(v -> {
            number = Objects.requireNonNull(binding.editTextNumber.getText()).toString();
            message = Objects.requireNonNull(binding.editTextMessage.getText()).toString();
            mainURL = "https://wa.me/91" + number + "?text=" + message;

            if (number.isEmpty()) {
                binding.editTextNumberBody.setError("Enter Valid Number");
            } else if (number.length() < 10) {
                binding.editTextNumberBody.setError("Enter minimum 10 numbers");
            } else {
                //Start WhatsApp
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mainURL));
                startActivity(intent);
                // Clear EditText fields
                binding.editTextNumber.setText("");
                binding.editTextMessage.setText("");
            }
        }); // PERFORM ONCLICK EVENT FOR SEND MESSAGE TO WHATS APP BUTTON

        binding.buttonSMS.setOnClickListener(v -> {
            number = Objects.requireNonNull(binding.editTextNumber.getText()).toString();

            if (number.isEmpty()) {
                binding.editTextNumberBody.setError("Enter Valid Number");
            } else if (number.length() < 10) {
                binding.editTextNumberBody.setError("Enter minimum 10 numbers");
            } else {
                Uri uri = Uri.parse("smsto:" + number);
                Intent i = new Intent(Intent.ACTION_SENDTO, uri);
                startActivity(Intent.createChooser(i, "Select Medium"));
            }
        }); // PERFORM ONCLICK EVENT FOR SMS BUTTON

        binding.topAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.gift:
                    interstitialAdHelper.showAd();
                    break;

                case R.id.menuPrivacyPolicy:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicy)));
                    break;

                case R.id.menuTermsAndConditions:
                    interstitialAdHelper.showAd();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(termsAndCondition)));
                    break;

                case R.id.shareButton:
                    interstitialAdHelper.showAd();
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "*Download Now* https://play.google.com/store/apps/details?id=com.jeelpatel.directmessage \nSay goodbye to the hassle of saving phone numbers just to send a message on whats ap! With our direct messaging app, you can easily message anyone on what ap without having to add them to your contacts first.";
                    String shareSubject = "Direct WhatsApp Message";
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                    startActivity(Intent.createChooser(sharingIntent, "Share DWM"));
                    break;
            }
            return false;
        }); // PERFORM ONCLICK EVENT FOR TOP APPBAR MENU
        binding.scanner.setOnClickListener(v -> scanner()); // CALL OR SCANNER
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Snackbar.make(this, binding.scanner, "SCANNER CANCELED SUCCESS", Snackbar.LENGTH_LONG)
                        .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                        .setAction("SCAN AGAIN", v -> scanner()).show();

            } else {
                scannedData = result.getContents();
                if (scannedData.startsWith("http://wa.me/") || scannedData.startsWith("https://wa.me/")) {
                    // The scanned data is a valid WhatsApp URL
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(scannedData));
                    startActivity(intent);

                } else {
                    // The scanned data is not a valid WhatsApp URL
                    Snackbar.make(this, binding.scanner, "QR NOT VALID", Snackbar.LENGTH_LONG)
                            .setAnimationMode(BaseTransientBottomBar.ANIMATION_MODE_SLIDE)
                            .setAction("SCAN AGAIN", v -> scanner()).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void scanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(activity_custom_scanner.class);
        integrator.setPrompt("PLACE A QR CODE INSIDE THE VIEWFINDER TO SCAN IT.");
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();

    }

    private void initializeAuthAndGoogleSignIn1() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
    }

}