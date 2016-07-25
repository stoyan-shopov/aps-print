package com.example.shopov.aps_print;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        byte[] buf = new byte[128];

        Log.e("shopov", "shopov print job queued");
        Log.e("shopov", "shopov trying to print file " + printJob.getDocument().getInfo().getName());
        ParcelFileDescriptor fd = printJob.getDocument().getData();
        Log.e("shopov", "shopov fd size " + fd.getStatSize());

        InputStream instream = new FileInputStream(fd.getFileDescriptor());

        File outputDir = getCacheDir(); // context being the Activity pointer
        File outputFile;
        ParcelFileDescriptor fd_tmp_pdf = new ParcelFileDescriptor(fd);

        try {
            byte[] tmpbuf = new byte[1024];
            int len;
            outputFile = File.createTempFile("shopov-temp", "pdf", outputDir);
            Log.e("shopov", "shopov temp pdf file created");

            Log.e("shopov", "shopov free space remaining " + outputFile.getFreeSpace());
            fd_tmp_pdf = ParcelFileDescriptor.open(outputFile, ParcelFileDescriptor.MODE_READ_WRITE);
            OutputStream outstream = new FileOutputStream(fd_tmp_pdf.getFileDescriptor());

            while ((len = instream.read(tmpbuf)) != -1)
                outstream.write(tmpbuf, 0, len);
            outstream.close();
            Log.e("shopov", "shopov temp fd size " + fd_tmp_pdf.getStatSize());

        } catch (IOException e) {
            e.printStackTrace();
        }

        int pageNum = 0;

        PdfiumCore pdfiumCore = new PdfiumCore(this);

        try {

            Log.e("shopov", "shopov trying to render pdf document");
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd_tmp_pdf);

            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = 576;//pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);

            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width, height);

            pdfiumCore.closeDocument(pdfDocument); // important!
            Log.e("shopov", "shopov bitmap rendered");
            Log.e("shopov", "shopov bitmap dimensions: " + width + "x" + height);

            Bitmap pdfbimtmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(pdfbimtmap);
            ColorMatrix ma = new ColorMatrix();
            ma.setSaturation(0);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(ma));
            canvas.drawBitmap(bitmap, 0, 0, paint);
            int x, y;
            for (y = 0; y < height; y ++)
            {
                String s = new String();
                for (x = 0; x < width; x ++)
                {
                    if ((pdfbimtmap.getPixel(x, y) & 0xff) >= 0x80)
                        s += '.';
                    else
                        s += ' ';
                }
                Log.e("shopov", s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            instream.reset();
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
