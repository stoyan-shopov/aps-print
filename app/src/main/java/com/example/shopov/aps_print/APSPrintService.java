package com.example.shopov.aps_print;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
        Log.e("shopov", "shopov trying to print file " + printJob.getDocument().getInfo().getName());
        ParcelFileDescriptor fd = printJob.getDocument().getData();

        int pageNum = 0;

        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {

            PdfDocument pdfDocument = pdfiumCore.newDocument(printJob.getDocument().getData());

            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);

            pdfiumCore.closeDocument(pdfDocument); // important!
            Log.e("shopov", "shopov bitmap rendered");
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream instream = new FileInputStream(fd.getFileDescriptor());
        byte[] buf = new byte[128];


        try {
            instream.skip(1);
            instream.read(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            instream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fd.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("shopov", "shopov read data: " + new String(buf));

        printJob.start();
        printJob.complete();
    }
}
