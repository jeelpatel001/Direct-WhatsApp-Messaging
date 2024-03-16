package com.jeelpatel.directmessage;


import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

public class activity_custom_scanner extends CaptureActivity {
    @Override
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_custom_scanner);
        return (DecoratedBarcodeView) findViewById(R.id.scanner_view);


    }
}