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
 
    public static void main(String[] args) {

        if(args[0].substring(0,2).toLowerCase().equals("-d"))
        {
            writeOriginalFile(args[1]);
        }
        else
        {
            writeTextFile(args[0]);
        }
    }
 
    private static void writeTextFile(String fileName)
    {
        try { 
            File file = new File(fileName);
            // Reading a Image file from file system
            FileInputStream imageInFile = new FileInputStream(file);
            byte imageData[] = new byte[(int) file.length()];
            imageInFile.read(imageData);

            // Converting Image byte array into Base64 String
            String imageDataString = encodeImage(imageData);

            FileOutputStream txtOutFile = new FileOutputStream(
                    "convert.txt");
            OutputStreamWriter fwriter = new OutputStreamWriter(txtOutFile);
            // InputStreamReader freader = new InputStreamReader(stream, "UTF-16");
            BufferedWriter outputfile = new BufferedWriter(fwriter);

            outputfile.write(imageDataString);
            imageInFile.close();

            System.out.println("File successfully convert to text file!");
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the file " + ioe);
        }
    }


    private static void writeOriginalFile(String fileName)
    {
        try { 
            File file = new File(fileName);
            FileInputStream txtInFile = new FileInputStream(file);
            InputStreamReader freader = new InputStreamReader(txtInFile);
            // InputStreamReader freader = new InputStreamReader(stream, "UTF-16");
            BufferedReader inputFile = new BufferedReader(freader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
                
            while((line = inputFile.readLine())!= null) {
            
                stringBuffer.append(line).append("\n");
            }
               
            String imageDataString = stringBuffer.toString();


            // Converting a Base64 String into Image byte array
            byte[] imageByteArray = decodeImage(imageDataString);

            // Write a image byte array into file system
            FileOutputStream imageOutFile = new FileOutputStream(
                    "convert.mp4");

            imageOutFile.write(imageByteArray);


            imageOutFile.close();

             System.out.println("File successfully recover!");
        } catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        } catch (IOException ioe) {
            System.out.println("Exception while reading the text file " + ioe);
        }
    }

    /**
     * Encodes the byte array into base64 string
     *
     * @param imageByteArray - byte array
     * @return String a {@link java.lang.String}
     */
    public static String encodeImage(byte[] imageByteArray) {
        return Base64.encodeBase64URLSafeString(imageByteArray);
    }
 
    /**
     * Decodes the base64 string into byte array
     *
     * @param imageDataString - a {@link java.lang.String}
     * @return byte array
     */
    public static byte[] decodeImage(String imageDataString) {
        return Base64.decodeBase64(imageDataString);
    }
}