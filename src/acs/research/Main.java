package acs.research;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Main {

    private static final int BLOCK_SIZE = 256 * 2 * 1024;
    private static final String ENCRYPT_FILENAME = "resources/" + "example.pdf";
    private static final String DECRYPT_FILENAME = "encrypted.txt";
    private static int noTasks = 0;

    private static int splitFile(InputStream fis, String outFileName, int size) throws IOException {
        byte[] buf = new byte[size];
        int noBytesRead;
        int index = 0;
        while((noBytesRead = fis.read(buf)) >= 0) {
            FileOutputStream fos = new FileOutputStream(outFileName + index + ".txt");
            fos.write(buf, 0, noBytesRead);
            fos.flush();
            fos.close();
            ++index;
        }
        fis.close();
        return index;
    }

    private static void mergeFile(OutputStream fos, String inFileName, int size) throws IOException {
        for (int index = 0; index < noTasks; ++index) {
            File file = new File(inFileName + index + ".txt");
            FileInputStream fis = new FileInputStream(file);
            byte[] buf = new byte[size];
            int noBytesRead = 0;
            long noBytesWritten = 0;
            long noBytesTotal = file.length();
            while(noBytesWritten < noBytesTotal) {
                noBytesRead = fis.read(buf);
                fos.write(buf, 0, noBytesRead);
                noBytesWritten += noBytesRead;
            }
            fis.close();
            file.delete();
        }
        fos.close();
    }

    public static void encrypt() throws IOException, InvalidCipherTextException {
        System.out.println("Encryption Started");

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        noTasks     = splitFile(new FileInputStream(new File(ENCRYPT_FILENAME)), "fragment", BLOCK_SIZE);

        for (int index = 0; index < noTasks; ++index) {

            Runnable task = new EncryptionThread(index);
            executorService.submit(task);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int index = 0; index < noTasks; ++index) {
            new File("fragment" + index + ".txt").delete();
        }

        FileOutputStream fos = new FileOutputStream(new File("encrypted.txt"));
        mergeFile(fos, "encrypted", BLOCK_SIZE + 16);
    }

    public static void decrypt() throws IOException {
        System.out.println("Decryption started.");
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        noTasks = splitFile(new FileInputStream(new File(DECRYPT_FILENAME)), "encrypted", BLOCK_SIZE + 16);

        for (int index = 0; index < noTasks; ++index) {

            Runnable task = new DecryptionThread(index);
            executorService.submit(task);
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int index = 0; index < noTasks; ++index) {
            new File("encrypted" + index + ".txt").delete();
        }

        FileOutputStream fos = new FileOutputStream(new File("decrypted.pdf"));

        mergeFile(fos, "decrypted", BLOCK_SIZE);

        System.out.println("Merged file.");
    }

    public static void main(String[] args) throws IOException, InvalidCipherTextException {

        System.out.println("Starting");
        encrypt();
        decrypt();
    }
}
