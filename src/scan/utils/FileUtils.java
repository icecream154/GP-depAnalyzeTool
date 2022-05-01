package scan.utils;

import java.io.*;

public class FileUtils {
    public static String readToString(String fileName) {
        String encoding = "UTF-8";
        File file = new File(fileName);
        long filelength = file.length();
        byte[] filecontent = new byte[(int) filelength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return new String(filecontent, encoding);
        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
            return null;
        }
    }
}
