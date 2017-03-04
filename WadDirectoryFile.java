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

public class WadDirectoryFile
{

    //An integer holding a pointer to the start of the lump's data in the file
    private int startPointer = 0;

    //An integer representing the size of the lump in bytes
    private int length = 0;

    //ASCII string defining the lump's name.
    private String name = null;

    WadDirectoryFile(int startPointer, int length, String name)
    {
        this.startPointer = startPointer;
        this.length = length;
        this.name = name.trim();
    }

    public int getStartPointer()
    {
        return startPointer;
    }

    public int getLength()
    {
        return length;
    }

    public String getName()
    {
        return name;
    }

    public String toString()
    {
        return "[Name: " + name + "\tPointer:" + startPointer + "\tLength: " + length + "]";
    }
}