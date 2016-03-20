import java.io.*;
import java.text.DecimalFormat;
import java.util.SortedMap;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;

/**
 * Class:           Huffman
 * File:            Huffman.java 
 * Description:     This class has the functionalities, to compress a text file 
 *                  and create a huf and cod file while encoding. During decode
 *                  mode, it loads the cod file and decompresses the huf file
 *                  to create an original text.
 * Date:            3/10/2016
 * Course:          CS 143
 * @author          San Min Liew, Hao Tu, Devin Stoen, Fnu Michael
 * @version         2.0
 * Environment:     PC, Windows 8, jdk 1.8.0_20, Netbeans 8.0.1
 * @see             javax.swing.JFrame
 */
public class Huffman
{   
    public static final int CHARMAX = 256;
    // public static final int UNIMAX = 65536;
    private HuffmanTree<Character> theTree;
    private byte[] saveDataArray;
    HuffmanChar[] charCountArray;
    private final char END_OF_FILE = '\u0003';
    private static File hufFile;
    private static File codFile;
    // for text file
    private static final int MAX_BYTE_SIZE_IN_BIT = 8;


    /**
     * Creates a new instance of Main
     */
    public Huffman() {}
    
    /**
     * main
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        // lets user to choose between typing in the argument or choose from
        // JFileChooser
        boolean decode = false;
        
        //factor
        final double NANO_TO_SEC = 1000000000;

        String textFileName = "";
        File file = new File("12.txt");
        if(args.length > 0)
        {
            if(args[0].substring(0,2).toLowerCase().equals("-d"))
            {
                decode = true;
                if(args.length > 1)
                {
                    textFileName = args[1];
                }
            }
            else
            {
                textFileName = args[0];
            }
            file = new File(textFileName);
        }
        
        Huffman coder = new Huffman();
        if (decode)
        {
            while (!file.exists() || !file.canRead())
            {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "huf files", "huf");
                chooser.setFileFilter(filter);
                chooser.setCurrentDirectory(file.getAbsoluteFile().
                                            getParentFile());

                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == chooser.APPROVE_OPTION) 
                {
                    file = chooser.getSelectedFile();
                    textFileName = file.getName();
                }
                else
                {
                    System.exit(0); // exit the program is user chooses cancel
                }
            }
            try 
            {
                /* checking efficiency */
                double start_decode = (double) System.nanoTime();

                coder.decode(textFileName);

                double end_decode = (double) System.nanoTime();
                System.out.println("Decode time: " 
                    + (end_decode - start_decode)/ NANO_TO_SEC
                    + " seconds");
            } catch (FileNotFoundException ex) {}
        }
        else
        {
            while (!file.exists() || !file.canRead())
            {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "text files", "txt");
                chooser.setFileFilter(filter);
                chooser.setCurrentDirectory(file.getAbsoluteFile().
                                            getParentFile());

                if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
                {
                    file = chooser.getSelectedFile();
                    textFileName = file.getName();
                }
                else
                {
                    System.exit(0); // exit the program is user chooses cancel
                }
            }
            System.err.println("file can execute");
            if (!file.canRead())
                System.exit(0);

            System.err.println("file after execute");
             // for tracking efficiency           
            double start_encode = (double) System.nanoTime();
            coder.encode(textFileName);
            double end_encode = (double) System.nanoTime();
            System.out.println("Encode time: " 
                + (end_encode - start_encode)/ NANO_TO_SEC
                + " seconds");
            displayCompressionRate(file, textFileName);
        }
    }

    /*
     * Encodes the text file and create huf File and cod File
     * @param fileName -- the file to encode
     */
    public void encode(String fileName)
    {
        // System.err.println("encode start");
        int huffmanCharSize = 0;
        // int[] charFrequency = new int[UNIMAX];
        int[] charFrequency = new int[CHARMAX];
        try
        {
            // System.err.println("read file start");
            FileInputStream stream = new FileInputStream(fileName);
            InputStreamReader freader = new InputStreamReader(stream);
            // InputStreamReader freader = new InputStreamReader(stream, "UTF-16");
            BufferedReader inputFile = new BufferedReader(freader);
            
            
            String line = inputFile.readLine();
            for (char temp : line.toCharArray())
            {   
                // System.out.println("Char UTF is: " + temp);
                charFrequency[temp]++;
            }
            line = inputFile.readLine();
            while(line != null)
            {
                line = "\n" + line;
                for (char temp : line.toCharArray())
                {   
                    // System.out.println("Char UTF is: " + temp);
                    charFrequency[temp]++;
                }
                line = inputFile.readLine();
            }
            charFrequency[END_OF_FILE]++;

        }
        catch (IOException ex)
        {}

        // gets the size and save it to charCountArray later
        for (int i = 0; i < charFrequency.length; i++) 
        {
            if (charFrequency[i] > 0)
            {
                huffmanCharSize++;
            }

            // System.out.println("Adding frequencies");
        }
        // System.out.println("Done adding frequencies");

        charCountArray = new HuffmanChar[huffmanCharSize];
        int count = 0;
        for (int i = 0; i < charFrequency.length; i++) 
        {
            if (charFrequency[i] > 0)
            {
              charCountArray[count] = new HuffmanChar((char) i, 
                                      charFrequency[i]);

              // System.out.println("The current char is: "+ (char) i);
              count++;
            }
        }

        // System.out.println("Before BubbleSort");
        bubbleSort(charCountArray); // sorts HuffmanChar array
        // System.out.println("After BubbleSort, now Generate Tree");
        theTree = new HuffmanTree(charCountArray); // pass in to HuffmanTree
        // System.out.println("After generateing Tree, now create key");
        createKeyByteArray(fileName, charCountArray, huffmanCharSize); 
        // System.out.println("Before writeEncode");
        writeEncodedFile(saveDataArray, fileName); 
    }
 
    /*
     * Decodes the huf File and cod File, creating an text file identical to 
     * the original with the extension x.txt
     * @param inFileName the file to decode
     */   
    public void decode(String inFileName) throws FileNotFoundException
    { 
        // assigns the variable to .cod file
        codFile = new File(inFileName.replace(".huf", ".cod"));
        if (codFile.canRead())
            loadHuffmanChar(codFile); // need the tree to get key map
        
        readHufFile(inFileName);    
        ArrayList<String> binary_array = getDecodeBinary(saveDataArray);
        
        writeDecodedTextFile(binary_array, inFileName); // creates text file
    }
    
    /**
     * Decodes the huf File character by character by using keymap to get the
     * char from the binary text. And creates the text file with xtxt as an 
     * extension
     *
     * @param decodeBinary the full binary representation of original text
     * @param hufFileName the huf file chosen by user, we need this to get the
     *                    the title and change the extension from there
     */
    private void writeDecodedTextFile(ArrayList<String> binary_array, String hufFileName) 
    {

        String charInBinary = "";
        String currentLine = "";
        try 
        {
            FileOutputStream stream = new FileOutputStream(hufFileName.replace(".huf", "x.txt"));
            OutputStreamWriter fwriter = new  OutputStreamWriter(stream);
            // OutputStreamWriter fwriter = new  OutputStreamWriter(stream, "UTF-16");
            BufferedWriter pWriter = new BufferedWriter(fwriter);
            // pWriter = new PrintWriter(decodeTextFile);
            for(int j = 0; j < binary_array.size(); j++)
            {
                String decodeBinary = binary_array.get(j);
                for (int i = 0; i < decodeBinary.length(); i++)
                {
                    charInBinary += decodeBinary.charAt(i);
                    if (theTree.getKeyMap().get(charInBinary) != null)
                    {
                        // do the checking and adding
                        char leafChar = theTree.getKeyMap().get(charInBinary);
                        if(leafChar != END_OF_FILE)
                        {
                            if (leafChar == '\n') // adss line by line
                            {
                                // System.out.println(currentLine);

                                pWriter.write(currentLine);
                                pWriter.newLine();
                                currentLine = "";
                            }
                            else
                                currentLine += leafChar;
                        }
                        else
                        {
                            // pWriter.print(currentLine);
                            // System.out.println(currentLine);
                            pWriter.write(currentLine);
                            pWriter.close();
                        }

                        // refresh charInBinary
                        charInBinary = "";
                    }
                }
            }
        } catch (IOException ex)
        {}

    }
    
    /**
     * Going through the every element in byteArray and reverse the byte 
     * value to the exact binary as how it's being stored
     * @param byteArray - the binary byte read from the huff file
     * @return String - 
     */
    private ArrayList<String> getDecodeBinary(byte[] byteArray)
    {
        ArrayList<String> binary_array = new ArrayList<String>();

        for (int i = 0; i < byteArray.length; i++)
        {
            byte currentInt = byteArray[i];

            String eightBinary = Integer.toBinaryString(currentInt);

            // only when unicode

            if((int)currentInt >= 0)
            {
                int lengthDiff = MAX_BYTE_SIZE_IN_BIT - eightBinary.length();
                if( lengthDiff > 0)
                {
                    for(int j = 0; j < lengthDiff; j++)
                    {
                        eightBinary = "0" + eightBinary;
                    } 
                    
                }
            }
            else
                eightBinary = eightBinary.substring(24);

            // System.err.println(eightBinary);//test 

            binary_array.add(eightBinary);
        }
        return binary_array;
    }
    
    /**
     * read the hufFile completely and store the byte info into 
     * saveDataArray
     * @param inHuff - the hufFile directory
     */
    private void readHufFile(String inHuff)
    {
        hufFile = new File(inHuff);
        saveDataArray = new byte[(int) hufFile.length()];
        try
        {
            try (DataInputStream dis = new DataInputStream
            (new FileInputStream(hufFile))) 
            {
                dis.read(saveDataArray);
                dis.close();
            } 
        }
        catch (IOException ex) {}
    }
    
    /**
     * Loads the .cod File to get the original HuffmanChar array that we 
     * created during encoding mode
     * @param codFile the codFile to be loaded to pass the original HuffmanChar
     *                array to HuffmanTree class
     */
    private void loadHuffmanChar(File codFile)
    {
        byte[] decodeByte = new byte[ (int) codFile.length()];
        try (DataInputStream dis = new DataInputStream
                (new FileInputStream(codFile))) 
        {
            dis.readFully(decodeByte);
        }
        catch (IOException ex) 
        {}
        
        int ORIGINIAL_DIVIDER = 3;
        int HUFF_SIZE = decodeByte.length / ORIGINIAL_DIVIDER;
        HuffmanChar[] originalHuffman = new HuffmanChar[HUFF_SIZE];
        
        for (int i = 0; i < originalHuffman.length; i++)
        {
            byte[] currentByteArray = new byte[3];
            currentByteArray[0] = decodeByte[i * 3];
            currentByteArray[1] = decodeByte[(i * 3) + 1];
            currentByteArray[2] = decodeByte[(i * 3) + 2];
            originalHuffman[i] = new HuffmanChar(currentByteArray);
        }
        
        // sort the array and pass it to the tree again
        bubbleSort(originalHuffman);
        theTree = new HuffmanTree(originalHuffman);
    }
      
    /**
     * Get all the binary path for every single character and add them to a 
     * String, then add a artificial binary path to indicate the end of file,
     * then convert every 8 binary bits to an element of byte in byte array,
     * then save it into a new file with extend .huf
     * @param bytes bytes for file
     * @param fileName file directory
     */ 
    public void writeEncodedFile(byte[] bytes, String fileName)
    {

        /* initialize for put in length of 8 algorithm */
        ArrayList<String> binary_array = new ArrayList<String>();
        binary_array.add("");
        int current_length = 0;
        String add_string = "";

        SortedMap myMap = theTree.getCodeMap();
        try
        {
            // FileReader freader = new FileReader(fileName);
            // BufferedReader inputFile = new BufferedReader(freader);

            FileInputStream stream = new FileInputStream(fileName);
            InputStreamReader freader = new InputStreamReader(stream);
            // InputStreamReader freader = new InputStreamReader(stream, "UTF-16");
            BufferedReader inputFile = new BufferedReader(freader);
            

            String temp_line;
            String line = inputFile.readLine();

            while(line != null)
            {
                temp_line = line;
                line = inputFile.readLine();
                
                // for dealing with the last line in the file
                if(line == null)
                {
                    if(temp_line.equals(""))
                        temp_line += "\n" + END_OF_FILE;
                    else
                        temp_line += END_OF_FILE;
                }
                else
                    temp_line += "\n";

                for (char temp : temp_line.toCharArray())
                {   
                    // System.out.println("In writeEncode char is: " + temp);

                    add_string = myMap.get(temp).toString();

                    /* huffman algorithm start here */
                    while(add_string != "")
                    {

                        /* last time didn't fill up the element, set more string in there */
                        if(current_length < MAX_BYTE_SIZE_IN_BIT)
                        {
                            if(current_length + add_string.length() <= MAX_BYTE_SIZE_IN_BIT)
                            {
                                binary_array.set(binary_array.size()-1,
                                    binary_array.get(binary_array.size()-1) + add_string);
                                add_string = "";
                            }
                            else //case for length over
                            {
                                binary_array.set(binary_array.size()-1,
                                        binary_array.get(binary_array.size()-1) 
                                        + add_string.substring(0, 
                                        MAX_BYTE_SIZE_IN_BIT - current_length));

                                add_string = add_string.substring(MAX_BYTE_SIZE_IN_BIT 
                                            - current_length, add_string.length());
                            }
                        }
                        else //case for equal just add another element of arraylist
                        {
                            // case for add_string length > 8 distribute to next iteration
                            if(add_string.length() > MAX_BYTE_SIZE_IN_BIT)
                            {
                                binary_array.add(add_string.substring(0, MAX_BYTE_SIZE_IN_BIT));
                                add_string = add_string.substring(MAX_BYTE_SIZE_IN_BIT,
                                             add_string.length());
                            }
                            else // case for smaller than 8
                            {
                                binary_array.add(add_string);
                                add_string = "";
                            }
                        }
                        current_length = (binary_array.get(binary_array.size()-1)).length();    
                    }
                }
            }
            
        }
        catch (IOException ex)
        {}
        
        if(!((binary_array.size() == 1) && (binary_array.get(0) == "")))
        {
            bytes = new byte[binary_array.size()];
            
            int i;
            String eightBinary;
            //every eight bit of binary put in the byte array
            for( i = 0; i < bytes.length - 1 ; i++)
            {
                eightBinary = binary_array.get(i);  
                
                // System.err.println(eightBinary);//test

                bytes[i] = (byte) Integer.parseInt(eightBinary, 2);
            }
            // operation for last index add trailing zeros
            eightBinary = binary_array.get(i);
            int lengthDiff = MAX_BYTE_SIZE_IN_BIT - eightBinary.length();


            for(int z = 0; z < lengthDiff; z++)
                eightBinary += "0";

            // System.err.println(eightBinary);//test

            bytes[i] = (byte) Integer.parseInt(eightBinary, 2);

            try
            {
                //create the file with replace extention
                hufFile = new File(fileName.replace(".txt", ".huf"));
                hufFile.createNewFile();
                try 
                {
                    FileOutputStream f = new FileOutputStream(hufFile);
                    f.write(bytes);
                    f.close();
                }
                catch(IOException ex){}
            }
            catch(IOException ex){}
        }
    }
    
   /**
     * This method convert the HuffmanChar passed in and create the three-byte
     * array to be saved on the .cod file.
     * 
     * @param fileName The name of the file to write to
     * @param charCountArray The array of HuffmanChar to be converted to 
     *                       three-byte array
     * @param huffmanCharSize The size of the array of HuffmanChar
     */
    private void createKeyByteArray(String fileName, HuffmanChar[] 
                                    charCountArray, int huffmanCharSize) 
    {
        saveDataArray = new byte[3 * (huffmanCharSize)];
        
        int i = 0;
        for (HuffmanChar h: charCountArray)
        {
            byte[] temp = h.toThreeBytes();
            for (int j = 0; j < 3; i++, j++)
                saveDataArray[i] = temp[j];
        }
        writeKeyFile(fileName, saveDataArray);
    }
   
   /**
     * This method uses the byte array passed in to write the .cod file.
     * 
     * @param fileName the name of the file to write to
     * @param byteArray the array of byte to be written to the file
     */
    public void writeKeyFile(String fileName, byte[] byteArray)
    {
        codFile = new File(fileName.replace(".txt", ".cod"));
        try 
        {
            FileOutputStream stream = new FileOutputStream(codFile);
            
            stream.write(byteArray); // was using byteArray earlier
            stream.close();
            
        } catch (IOException ex) 
        {}
    }
    
    /**
     * Displays the compression rate
     * @param file the file for comparison.
     * @param textFileName the full name to get .huf by replacing the extension
     */
    private static void displayCompressionRate(File file, String textFileName)
    {
        DecimalFormat decimal = new DecimalFormat("#.##");
        // printing out the compression ratio:
        double compressionRate = ((double)hufFile.length()
                                 / file.length()) * 100;
        String hufFileName = textFileName.replace(".txt", ".huf");
        System.out.println(hufFileName + " : " + 
                           decimal.format(compressionRate) +
                           "% compression");

    }
    
    /**
     * A bubbleSort to sort the HuffmanChar array on their frequencies to the 
     * second parameter in HuffmanChar's constructor
     * @param charCountArray the HuffmanChar array to be sorted
     */
    private void bubbleSort(HuffmanChar[] charCountArray) // bubble sort
    {
        HuffmanChar temp;
        for (int i = 0; i < charCountArray.length - 1; i++)
        {
            for (int j = 1; j < charCountArray.length - i; j++)
            {
                if (charCountArray[j - 1].getOccurances() > 
                    charCountArray[j].getOccurances())
                {
                    temp = charCountArray[j - 1];
                    charCountArray[j - 1] = charCountArray[j];
                    charCountArray[j] = temp;
                }
            }
        }
    }
}