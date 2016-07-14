package com.example.shopov.aps_print;

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
            Thread.sleep(10000, 0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new PrinterDiscoverySession() {
            @Override
            public void onStartPrinterDiscovery(List<PrinterId> priorityList) {
                Log.e("shopov", "shopov 1");
                addPrinters(new ArrayList<PrinterInfo>());
            }

            @Override
            public void onStopPrinterDiscovery() {
                Log.e("shopov", "shopov 2");
            }

            @Override
            public void onValidatePrinters(List<PrinterId> printerIds) {
                Log.e("shopov", "shopov 3");
            }

            @Override
            public void onStartPrinterStateTracking(PrinterId printerId) {
                Log.e("shopov", "shopov 4");
            }

            @Override
            public void onStopPrinterStateTracking(PrinterId printerId) {
                Log.e("shopov", "shopov 5");
            }

            @Override
            public void onDestroy() {
                Log.e("shopov", "shopov 6");
            }
        };
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {

    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {

    }
}
