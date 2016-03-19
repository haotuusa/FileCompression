import java.io.*;
 
import org.apache.commons.codec.binary.Base64;
 
/**
 * @desc Image manipulation - Conversion
 * 
 * @filename ImageManipulation.java
 * @author Jeevanandam M. (jeeva@myjeeva.com)
 * @copyright myjeeva.com
 */
public class ImageManipulation {
 
    // String fileName;
    String imageDataString;

    public ImageManipulation(){}

    public void convertToString(String fileName) {
 
        File file = new File(fileName);
 
        try {            
            // Reading a Image file from file system
            FileInputStream imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);
 
            // Converting Image byte array into Base64 String
            imageDataString = encodeImage(imageData);
 
            /* TEST ------------------------------- */
            FileOutputStream txtOutFile = new FileOutputStream(
                    "convert.txt");
            OutputStreamWriter fwriter = new OutputStreamWriter(txtOutFile);
            // InputStreamReader freader = new InputStreamReader(stream, "UTF-16");
            BufferedWriter outputfile = new BufferedWriter(fwriter);
            outputfile.write(imageDataString);
            txtOutFile.close();
            /* ------------------------------- */

            imageInFile.close();



 
            System.out.println("Image Successfully Manipulated!");
        } 
        catch (FileNotFoundException e) {
            System.out.println("Image not found" + e);
        } 
        catch (IOException ioe) {
            System.out.println("Exception while reading the Image " + ioe);
        }
    }

    public void convertToImage(String imageDataString) {

        // Converting a Base64 String into Image byte array
        byte[] imageByteArray = decodeImage(imageDataString);

        // Write a image byte array into file system
        FileOutputStream imageOutFile = new FileOutputStream(
            "convert.jpg");

        imageOutFile.write(imageByteArray);


        imageOutFile.close();
    }

    public String getEncodeString() {
        return 
    }

 
    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    private String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64URLSafeString(imageByteArray);
    }
 
    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    private byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
}