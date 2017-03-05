# DoomWadEx
Extracts all of the files from a DOOM WAD file. Not very usefull, it is very basic and there are better tools for this. This project was just to show some basic Java binary IO.

## Compile 
javac DoomWad.java

## Run
java DoomWad [WAD File Name]

## WAD Format
More information can be found here: http://doom.wikia.com/wiki/WAD

### Header
A WAD file always starts with a 12-byte header. It contains three values:

Position | Length | Name | Description
---------|--------|------|------------
0x00 | 4 | identification | The ASCII characters "IWAD" or "PWAD". Defines whether the WAD is an IWAD or a PWAD.
0x04 | 4 | numlumps | An integer specifying the number of lumps in the WAD.
0x08 | 4 | infotableofs | An integer holding a pointer to the location of the directory. 

All integers are 4 bytes long in x86-style little-endian order. Their values can never exceed 231-1, since Doom reads them as signed ints. 

### Directory
The directory associates names of lumps with the data that belong to them. It consists of a number of entries, each with a length of 16 bytes. The length of the directory is determined by the number given in the WAD header. The structure of each entry is as follows: 

Offset | Length | Name | Content
---------|--------|------|------------
0x00 | 4 | filepos | An integer holding a pointer to the start of the lump's data in the file.
0x04 | 4 | size | An integer representing the size of the lump in bytes.
0x08 | 8 | name | An ASCII string defining the lump's name. Only the characters 'A-Z' (uppercase), '0-9', and '[ ]' - _ should be used in lump names (an exception has to be made for some of the Arch-Vile sprites, which use "\\"). When a string is less than 8 bytes long, it should be null-padded to the tight byte. 

"Virtual" lumps (such as F_START) only exist in the directory, having a size of 0. Their offset value therefore is nonsensical (often 0).
It is possible for more than one lump to have the same offset value, aswell as having offsets that overlap other lump data. 
