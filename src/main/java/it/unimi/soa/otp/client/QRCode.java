package it.unimi.soa.otp.client;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static com.google.zxing.BarcodeFormat.QR_CODE;


public class QRCode {

    private String totpConf;
    private int size; //Pixels of main.java.totp.QRCode

    public QRCode(String totpConf, int size) {
        this.totpConf = totpConf;
        this.size = size;
    }

    private BitMatrix generateQR() {
        BitMatrix bitMatrix = null;
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            bitMatrix = qrCodeWriter.encode(totpConf, QR_CODE, this.getSize(), this.getSize());
        } catch (Exception e) {
            System.err.println("Error creating qrcode");
        }
        return bitMatrix;
    }

    public void saveImage(String path) {
        try {
            BufferedImage image = MatrixToImageWriter.toBufferedImage(generateQR());
            File outputfile = new File(path + "qrcode.png");
            ImageIO.write(image, "png", outputfile);
        } catch (Exception e) {
            System.err.println("Cannot save file");
        }
    }

    public void show() {
        try {
            BufferedImage image = MatrixToImageWriter.toBufferedImage(generateQR());

            JFrame frame = new JFrame();
            frame.getContentPane().setLayout(new GridLayout(1, 1));
            frame.getContentPane().add(new JLabel(new ImageIcon(image)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getSize() {
        return size;
    }

    public String getTotpConf() {
        return totpConf;
    }
}
