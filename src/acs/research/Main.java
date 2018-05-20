package acs.research;

import java.io.*;
import java.security.Security;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.ShortBufferException;

public class Main {

    private static final int MYTHREADS = 30;
    private static final String FILENAME = "/Users/ella/IdeaProjects/Cercetare/resources/" + "example.pdf";

    public static void main(String[] args) throws IOException, ShortBufferException, InvalidCipherTextException {

        Security.addProvider(new BouncyCastleProvider());

        FileInputStream fis =
                new FileInputStream(new File(FILENAME));
        FileOutputStream fos =
                new FileOutputStream(new File("encrypted.txt"));

        BouncyCastleAPI_AES_CBC bc = new BouncyCastleAPI_AES_CBC();
        bc.InitCiphers();

        //encryption
        bc.CBCEncrypt(fis, fos);

        fis = new FileInputStream(new File("encrypted.txt"));
        fos = new FileOutputStream(new File("clear_test.pdf"));

        //decryption
        bc.CBCDecrypt(fis, fos);
    }
}
