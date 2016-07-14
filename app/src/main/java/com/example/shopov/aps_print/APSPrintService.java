package com.example.shopov.aps_print;

import android.os.Parcelable;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class APSPrintService extends PrintService {
    @Nullable
    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        Log.e("shopov", "shopov started");
        try {
            Thread.sleep(1000, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new APSPrinterDiscoverySession(this);
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {
        Log.e("shopov", "shopov print job cancelled");

    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {
        Log.e("shopov", "shopov print job queued");
        printJob.start();
        printJob.complete();

    }
}
