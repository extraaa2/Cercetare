package acs.research;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.*;

public class DecryptionThread extends Thread {

    BouncyCastleAPI_AES_CBC bc;
    FileInputStream fis;
    FileOutputStream fos;

    DecryptionThread(int index) {
        this.bc = new BouncyCastleAPI_AES_CBC();
        bc.InitCiphers();
        try {
            this.fis = new FileInputStream(new File("encrypted" + index + ".txt"));
            this.fos = new FileOutputStream(new File("decrypted" + index + ".txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        //decryption
        try {
            bc.CBCDecrypt(fis, fos);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
