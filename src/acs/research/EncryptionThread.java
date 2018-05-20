package acs.research;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.*;

public class EncryptionThread implements Runnable {

    BouncyCastleAPI_AES_CBC bc;
    FileInputStream fis;
    FileOutputStream fos;

    EncryptionThread(int index) {
        this.bc = new BouncyCastleAPI_AES_CBC();
        bc.InitCiphers();
        try {
            this.fis = new FileInputStream(new File("fragment" + index + ".txt"));
            this.fos = new FileOutputStream(new File("encrypted" + index + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName());
        try {
            bc.CBCEncrypt(fis, fos);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
