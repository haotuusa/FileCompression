<snippet>
  <content>
# Huffman_Compression

## Purpose: 
Using Huffman algorithm to compress different file type depending on option flag (txt, jpg, png, mp3, mp4) in Java. Implementing Huffman library for building tree and common-codec additional library for converting all file type to base 64 string.

## Result:
* txt: 50 to 60 compression rate depending on the file size
* jpg: In progress
* png: In progress
* mp3: In progress
* mp4: In progress

## Algorithm:
### Compress mode (without -d flag)
1. Use file name extention to ackowledge file type to compress (.txt, .jpg, .png, .mp3, .mp4)
2. For text file we read in all the original string, for other file type we first convert to base 64 string.
3. Count all the character's frequency throughout the string
4. Build Huffman Tree base on our character's frequency
5. write in our tree shape in .cod file
6. Read in all the string again to find all binary path for each character in the Huffman Tree we build.
7. Write all the binary path into .huf file

### decompress mode (with -d flag)
1. Read in the .cod file to build back the tree.
2. Read in the .huf file and get the binary string of path
3. Recover our string by following the binary path in the tree we build on
4. Use first 4 substring to determine file type to decompress (.txt, .jpg, .png, .mp3, .mp4)
5. For text file, write back the string to text file with an additional x at the end of file name (etc. test.txt -> testx.txt)
6. For other file type, we convert base 64 string back to byte array and write to the file with original extention and additional x at the end of file name (etc. test.mp4 ->testx.mp4)

</content>
  <tabTrigger></tabTrigger>
</snippet>
