package com.jeelpatel.directmessage.Helper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.card.MaterialCardView;

public class BannerAdHelper {
    Context context;
    WindowManager windowManager;
    MaterialCardView bannerAdContainer;


    public void loadBanner(Context context, String AD_UNIT_ID, MaterialCardView bannerAdContainer, WindowManager windowManager) {
        this.bannerAdContainer = bannerAdContainer;
        this.windowManager = windowManager;
        this.context = context;
        // Create an ad request.
        AdView adView = new AdView(context);
        adView.setAdUnitId(AD_UNIT_ID);
        bannerAdContainer.removeAllViews();
        bannerAdContainer.addView(adView);

        AdSize adSize = getAdSize();
        adView.setAdSize(adSize);

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private AdSize getAdSize() {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = bannerAdContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }
}
