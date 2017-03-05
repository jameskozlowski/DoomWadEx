import java.io.*;
import java.util.ArrayList;

/******************************************************************************************
* This class holds a DOOM Wad wadFile
* Wad structure from http://doom.wikia.com/wiki/WAD
*
*Author James Kozlowski
******************************************************************************************/

public class DoomWad
{

    //IOStream
    String fileName;

    //Header Data
    private String WADType = null;
    private int directoryLength = 0;
    private int directoryPointer = 0;

    //a list of files in the wad
    ArrayList<WadDirectoryFile> wadFiles;

    //wad file ioStream
    RandomAccessFile ioStream = null;

    //entery point for the simple extractor
    public static void main(String[] args)
    {
        if (args.length != 1)
        {
            System.out.println("Please specify a wad file to open");
            System.exit(0);
        }

        DoomWad wad = new DoomWad(args[0]);
    }

    public DoomWad(String fileName)
    {
        //set the file name`
        this.fileName = fileName;

        try
        {
            //open up the IO Stream
            ioStream = new RandomAccessFile(fileName, "r");

            //read the heder of the file
            ReadHeader();

            //read the wad file directory
            ReadDirectory();

            //extract the files from the WAD file
            extractAllFiles();
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("File Not Found");;
        }
        catch (Exception e) 
        {
            System.err.println("Error extracting files");
            System.err.println(e);
        }
        finally 
        {
            try 
            {
                //close the stream
                ioStream.close();
            } 
            catch (Exception e) 
            {
                System.err.println("Error closing file");
            } 
        }
    }

    /************************************************************************************************************
    *   A WAD file always starts with a 12-byte header. It contains three values:
    *    
    *   Bytes 	    Content
    *   0x00-0x03 	string "PWAD" or "IWAD", defines whether the WAD is a PWAD or an IWAD
    *   0x04-0x07 	An integer specifying the number of entries in the directory
    *   0x08-0x0b 	An integer holding a pointer to the location of the directory 
    **************************************************************************************************************/
    private void ReadHeader() throws IOException
    {
        //all data in the header are 4 bytes long
        byte[] headerData = new byte[4];

        //read in the WAD type
        ioStream.read(headerData);
        WADType = new String(headerData, "UTF-8");

        //Read in the number of files in the wad directory
        ioStream.read(headerData);
        directoryLength = java.nio.ByteBuffer.wrap(headerData).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
        
        //get the pointer to the directory 
        ioStream.read(headerData);
        directoryPointer = java.nio.ByteBuffer.wrap(headerData).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
    }


    /************************************************************************************************************
    *   The directory associates names of lumps with the data that belong to them. It consists 
    *   of a number of entries, each with a length of 16 bytes. The length of the directory is 
    *   determined by the number given in the WAD header. The structure of each entry is as follows:
    *
    *   Bytes 	    Content
    *   0x00-0x03 	An integer holding a pointer to the start of the lump's data in the file
    *   0x04-0x07 	An integer representing the size of the lump in bytes
    *   0x08-0x0f 	ASCII string defining the lump's name. Only the characters A-Z (uppercase), 0-9 and []-_ 
    *               should be used in Lump names (an exception has to be made for some of the ArchVile 
    *               sprites, which use "\"). The string must be null-terminated if shorter than 8 bytes. 
    **************************************************************************************************************/
    private void ReadDirectory() throws IOException
    {

        //create an array list for the files in the directory
        wadFiles = new ArrayList<WadDirectoryFile>();

        //go to the start of the directory
        ioStream.seek(directoryPointer);
        
        //Read the directory information for each item
        for (int i = 0; i < directoryLength; i++)
        {

            byte[] dirData = new byte[4];

            //read in the pointer to the file
            ioStream.read(dirData);
            int Pointer = java.nio.ByteBuffer.wrap(dirData).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
            
            //read in the size of the file
            ioStream.read(dirData);
            int Size = java.nio.ByteBuffer.wrap(dirData).order(java.nio.ByteOrder.LITTLE_ENDIAN).getInt();
            
            //read in the name of the file
            dirData = new byte[8];
            ioStream.read(dirData);
            String Name = new String(dirData, "UTF-8");
            
            //add the file information to the ArrayList
            wadFiles.add(new WadDirectoryFile(Pointer, Size, Name));
        }     
    }

    //Extract all files from the doom WAD file
    private void extractAllFiles() throws IOException
    {
        //create a directory for the extracted files
        File dir = new File(fileName +  " Files");

        if (!dir.exists())
            dir.mkdir();
        
        //loop over each item in the directory list
        for (int i = 0; i < directoryLength; i++)
        {
            WadDirectoryFile file = wadFiles.get(i);
            
            //only if it is a real file and not a marker(0 bytes)
            if (file.getLength() > 0)
            {

                //create file
                RandomAccessFile fileOutStream = new RandomAccessFile("./" + dir + "/" + file.getName(), "rw");
                
                //create an byte buffer for the file
                //as WAD files are not very large we dont need to wory about the buffer being too large
                byte[] data = new byte[file.getLength()];
                
                //go to the start of the file in the WAD
                ioStream.seek(file.getStartPointer());

                //read in the data to the buffer
                ioStream.read(data);

                //save the buffer to the new file
                fileOutStream.write(data);

                //close out the new file stream
                fileOutStream.close();

                //print out the name of the new file created
                System.out.println(dir + "/" + file.getName());
            }
        }
    }

    //to string override
    public String toString()
    {
        String returnString =   "Wad Enterys : " + directoryLength +
                                "\n--------------------------------------------------\n";
        for (int i = 0; i < directoryLength; i++)
            returnString += wadFiles.get(i) + "\n";
        return returnString;
    }
}