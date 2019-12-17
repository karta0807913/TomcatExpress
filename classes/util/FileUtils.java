package util;

import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
public class FileUtils {

    public static String readFile(String path)
        throws IOException 
    {
        return readFile(path, StandardCharsets.UTF_8);
    }

    public static String readFile(String path, Charset encoding) 
        throws IOException 
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
