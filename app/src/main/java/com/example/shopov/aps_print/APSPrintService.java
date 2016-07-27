package com.example.shopov.aps_print;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
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
import java.util.HashMap;
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

    private static final int  TPH_DOT_WIDTH = 672;
    private static final int  TPH_BYTE_WIDTH = TPH_DOT_WIDTH / 8;

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {
        Log.e("shopov", "shopov print job cancelled");

    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {

        Log.e("shopov", "shopov print job queued");
        UsbManager mUsbManager;
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        if (deviceList.isEmpty())
            ;//enumerate_button.setText("failed");
        else
        {
            //enumerate_button.setText("enumeration successful" + deviceList.values().toArray()[0]);
            UsbDevice device = deviceList.values().toArray(new UsbDevice[0])[0];

            byte[] bytes = new byte[4];
            int TIMEOUT = 0;
            boolean forceClaim = true;

            bytes[0] = 'A';
            bytes[1] = 'P';
            bytes[2] = 'S';
            bytes[3] = '\n';

            UsbInterface intf = device.getInterface(0);
            UsbEndpoint endpoint = intf.getEndpoint(0);
            UsbDeviceConnection connection = mUsbManager.openDevice(device);
            connection.claimInterface(intf, forceClaim);
            connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT); //do in another thread

            Log.e("shopov", "shopov print job queue - wrote to printer");

            //mUsbManager.requestPermission(device, mPermissionIntent);
        }



        byte[] buf = new byte[128];


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

            int width = TPH_DOT_WIDTH;//pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
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
            int x, y, z;
            if (deviceList.isEmpty())
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
            else
            {
                //enumerate_button.setText("enumeration successful" + deviceList.values().toArray()[0]);
                UsbDevice device = deviceList.values().toArray(new UsbDevice[0])[0];

                byte[] bytes = new byte[4];
                int TIMEOUT = 0;
                boolean forceClaim = true;

                bytes[0] = 'S';
                bytes[1] = 'G';
                bytes[2] = 'S';
                bytes[3] = '\n';

                UsbInterface intf = device.getInterface(0);
                UsbEndpoint endpoint = intf.getEndpoint(0);
                UsbDeviceConnection connection = mUsbManager.openDevice(device);
                connection.claimInterface(intf, forceClaim);
                connection.bulkTransfer(endpoint, bytes, bytes.length, TIMEOUT); //do in another thread

                Log.e("shopov", "shopov print job queue - wrote to printer");
                byte[] graphics_line = new byte[128];
                byte dotx;
                graphics_line[0] = 27 /* ESC */;
                graphics_line[1] = 'V';
                graphics_line[2] = 0 /* attributes */;
                graphics_line[3] = TPH_BYTE_WIDTH /* byte count low byte */;
                graphics_line[4] = 0 /* byte count high byte */;
                for (y = 0; y < height; y ++)
                {
                    for (x = 0; x < TPH_BYTE_WIDTH; x ++)
                    {
                        for (z = 0, dotx = 0; z < 8; z ++)
                        {
                            dotx <<= 1;
                            if ((pdfbimtmap.getPixel(x * 8 + z, y) & 0xff) < 0x80)
                                dotx |= 1;
                        }
                        graphics_line[x + 5] = dotx;
                    }
                    connection.bulkTransfer(endpoint, graphics_line, 5 + TPH_BYTE_WIDTH, TIMEOUT); //do in another thread
                }

                //mUsbManager.requestPermission(device, mPermissionIntent);
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
