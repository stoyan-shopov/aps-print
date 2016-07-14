package com.example.shopov.aps_print;

import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MSUser on 14.7.2016 г..
 */

public class APSPrinterDiscoverySession extends PrinterDiscoverySession {
    private APSPrintService printService;
    private static final String PRINTER = "super shopov printer";

    public APSPrinterDiscoverySession(APSPrintService printService) {
        Log.e("shopov", "shopov constructor");
        this.printService = printService;
    }

    @Override
    public void onStartPrinterDiscovery(List<PrinterId> printerList) {
        Log.e("shopov", "shopov onStartPrinterDiscovery");
        PrinterId id = printService.generatePrinterId(PRINTER);
        PrinterInfo.Builder builder =
                new PrinterInfo.Builder(id, PRINTER, PrinterInfo.STATUS_IDLE);
        PrinterInfo info = builder.build();
        List<PrinterInfo> infos = new ArrayList<>();
        infos.add(info);
        addPrinters(infos);
    }

    @Override
    public void onStopPrinterDiscovery() {
        Log.e("shopov", "shopov onStopPrinterDiscovery");
    }

    @Override
    public void onValidatePrinters(List<PrinterId> printerIds) {
        Log.e("shopov", "shopov onValidatePrinters");
    }

    @Override
    public void onStartPrinterStateTracking(PrinterId printerId) {
        Log.e("shopov", "shopov onStartPrinterStateTracking");
        PrinterInfo.Builder builder = new PrinterInfo.Builder(printerId,
                PRINTER, PrinterInfo.STATUS_IDLE);
        PrinterCapabilitiesInfo.Builder capBuilder =
                new PrinterCapabilitiesInfo.Builder(printerId);

        capBuilder.addMediaSize(PrintAttributes.MediaSize.ISO_A4, true);
        capBuilder.addResolution(new PrintAttributes.Resolution(
                "Default", "Default", 360, 360), true);
        capBuilder.setColorModes(PrintAttributes.COLOR_MODE_COLOR
                        + PrintAttributes.COLOR_MODE_MONOCHROME,
                PrintAttributes.COLOR_MODE_COLOR);
        capBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

        PrinterCapabilitiesInfo caps = capBuilder.build();
        builder.setCapabilities(caps);
        PrinterInfo info = builder.build();
        List<PrinterInfo> infos = new ArrayList<PrinterInfo>();
        infos.add(info);
        addPrinters(infos);
    }

    @Override
    public void onStopPrinterStateTracking(PrinterId printerId) {
        Log.e("shopov", "shopov onStopPrinterStateTracking");
    }

    @Override
    public void onDestroy() {
        Log.e("shopov", "shopov onDestroy");
    }
}