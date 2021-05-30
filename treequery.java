import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;
import java.nio.*;

//treequery class
public class treequery {

     /*
	 * static variables of the will be used throughout the program.
	 */
    static final int SDTName_SIZE = 25;
    static final int RECORD_SIZE = 112;
  	static Tree root;
	static int Nodesize = 0; 
	private static final int ID_POS = 0;
    private static final int DATETIME_POS = 1;
    private static final int YEAR_POS = 2;
    private static final int MONTH_POS = 3;
    private static final int MDATE_POS = 4;
    private static final int DAY_POS = 5;
    private static final int TIME_POS = 6;
    private static final int SENSORID_POS = 7;
    private static final int SENSORNAME_POS = 8;
    private static final int COUNTS_POS = 9;
	private static final int STD_NAME_SIZE = 24;
    private static final int ID_SIZE = 4;
    private static final int DATE_SIZE = 8;
    private static final int YEAR_SIZE = 4;
    private static final int MONTH_SIZE = 9;
    private static final int MDATE_SIZE = 4;
    private static final int DAY_SIZE = 9;
    private static final int TIME_SIZE = 4;
    private static final int SENSORID_SIZE = 4;
    private static final int SENSORNAME_SIZE = 38;
    private static final int COUNTS_SIZE = 4;
    private static final int TOTAL_SIZE =    STD_NAME_SIZE + 
                                            ID_SIZE + 
                                            DATE_SIZE + 
                                            YEAR_SIZE + 
                                            MONTH_SIZE + 
                                            MDATE_SIZE + 
                                            DAY_SIZE + 
                                            TIME_SIZE + 
                                            SENSORID_SIZE + 
                                            SENSORNAME_SIZE + 
                                            COUNTS_SIZE;
											public static final int ID_OFFSET =   STD_NAME_SIZE;

    private static final int DATE_OFFSET =   STD_NAME_SIZE +
                                            ID_SIZE;

    private static final int YEAR_OFFSET =  STD_NAME_SIZE +
                                            ID_SIZE +
                                            DATE_SIZE;

    private static final int MONTH_OFFSET =  STD_NAME_SIZE +
                                            ID_SIZE +
                                            DATE_SIZE +
                                            YEAR_SIZE;

    private static final int MDATE_OFFSET =  STD_NAME_SIZE +
                                            ID_SIZE +
                                            DATE_SIZE +
                                            YEAR_SIZE +
                                            MONTH_SIZE;

    private static final int DAY_OFFSET =   STD_NAME_SIZE +
                                            ID_SIZE +
                                            DATE_SIZE +
                                            YEAR_SIZE +
                                            MONTH_SIZE +
                                            MDATE_SIZE;

    private static final int TIME_OFFSET =   STD_NAME_SIZE + 
                                            ID_SIZE + 
                                            DATE_SIZE +
                                            YEAR_SIZE +
                                            MONTH_SIZE +
                                            MDATE_SIZE +
                                            DAY_SIZE;

    private static final int SENSORID_OFFSET =   STD_NAME_SIZE + 
                                                ID_SIZE + 
                                                DATE_SIZE +
                                                YEAR_SIZE +
                                                MONTH_SIZE +
                                                MDATE_SIZE +
                                                DAY_SIZE +
                                                TIME_SIZE;

    private static final int SENSORNAME_OFFSET = STD_NAME_SIZE + 
                                                ID_SIZE + 
                                                DATE_SIZE + 
                                                YEAR_SIZE + 
                                                MONTH_SIZE + 
                                                MDATE_SIZE + 
                                                DAY_SIZE + 
                                                TIME_SIZE + 
                                                SENSORID_SIZE; 

    private static final int COUNTS_OFFSET = STD_NAME_SIZE + 
                                            ID_SIZE + 
                                            DATE_SIZE + 
                                            YEAR_SIZE + 
                                            MONTH_SIZE + 
                                            MDATE_SIZE + 
                                            DAY_SIZE + 
                                            TIME_SIZE + 
                                            SENSORID_SIZE + 
                                            SENSORNAME_SIZE;  
  
    // Initialize the tree
   public void initialiseTree() {
        root = new Tree();
    }

@SuppressWarnings("null")
	/*
	 * This function retrieves the data file path from the index file 
	 * and calls the corresponding functions for searching a record or 
	 */
	private static void searchTree(String[] args) {

	 //Set up the search text based on arguments passed in
     String[] SDTNames = Arrays.copyOfRange(args, 0, args.length - 1);

   	String spageSize = args[args.length - 1];
      String SDTName = "";
	  String indexFileName = "treeIndex." + spageSize;
     
	  try{
      if(SDTNames.length == 1) {
		SDTName = SDTNames[0];
      } else {
	   	SDTName = String.join(" ", SDTNames);
      }
		 
	    //Open channel for the index file
		FileInputStream fin = new FileInputStream(indexFileName);
		FileChannel fc = fin.getChannel();
		fc.position(1025l);
        
		//Load the index file to Tree object
		ObjectInputStream ois = new ObjectInputStream(fin);
        Tree newRoot = (Tree) ois.readObject();
		ois.close();
		
	    //Start searching the input text
	 	searchData(newRoot, indexFileName, SDTName, spageSize);
	
} catch (FileNotFoundException e) {
	e.printStackTrace();
 } catch (IOException e) {
	e.printStackTrace();
} catch (ClassNotFoundException e) {
	e.printStackTrace();
 }

}

	/*
	 * This function finds whether the record is present in the data file or not. 
	 * It opens the index file and obtains the offset value of the record and 
	 * calls the corresponding retrieve data function to display the record 
	 */
	private static void searchData(Tree node, String indexFile, String key, String pageSize) {
	
	  try{
			
		int indexfilekeylen = Integer.parseInt(getmetadata(indexFile,"key"));
		if(key.length() > indexfilekeylen) {
			key = key.substring(0, indexfilekeylen);
		}
	
		for (int i = 0; i < node.key.size(); i++) {
  	  	  if (node.isLeaf) {
				boolean tmpKeyIndex = node.key.get(i).contains(key);
				int keyIndex = -1;
				
				if (tmpKeyIndex == true) {
					keyIndex = i;
				}
				
			if (keyIndex == -1) {
					if (i < node.key.size() - 1) {
						continue;
					}
					
	
				} else if (keyIndex != -1) { 
					int offsetvalue = node.offsetvalue.get(keyIndex).intValue();
					int dataLength = node.dataLength.get(keyIndex);

					DisplayRecord(indexFile, offsetvalue, dataLength, pageSize);
				
				}
			}
			else if (key.compareTo(node.key.get(i)) < 0) {
				if (node.ptr.get(i) != null) {
					searchData(node.ptr.get(i), indexFile, key, pageSize);
					return;
				}
			}
			else if (key.compareTo(node.key.get(i)) >= 0) {
		     if (i < node.key.size() - 1) {
					continue;
		     }
			else if (i == node.key.size() - 1) {
			
				if (!node.isLeaf && node.ptr.get(i + 1) != null) {
						searchData((Tree) node.ptr.get(i + 1),indexFile, key, pageSize);
						return;
					}
				}

			
			}
		}

	} catch (FileNotFoundException e) {
		e.printStackTrace();
	 } catch (IOException e) {
		e.printStackTrace();
	} 
	}

	 
 	//This function will get the record details from the heap file using the offset position obtained from the index file
	private static void DisplayRecord(String indexFile, int offset, int dataLength, String pageSize) {		
	
	try{
   
	 RandomAccessFile objFile = new RandomAccessFile("heap." + pageSize, "r");
	
	int numBytesIntField = 4;
	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");

	        //Seek the position of the record based on offset position
			objFile.seek(offset);
			byte[] record = new byte[TOTAL_SIZE];
			objFile.read(record, 0, TOTAL_SIZE);

			//Starts getting the byte range of each field
			 byte[] SDT_Name = Arrays.copyOfRange(record, 0, STD_NAME_SIZE);
			byte[] id = Arrays.copyOfRange(record, ID_OFFSET, ID_OFFSET + numBytesIntField);
			byte[] date_time = Arrays.copyOfRange(record, DATE_OFFSET, DATE_OFFSET+ DATE_SIZE);
			byte[] year = Arrays.copyOfRange(record, YEAR_OFFSET,YEAR_OFFSET + numBytesIntField);
			byte[] month = Arrays.copyOfRange(record, MONTH_OFFSET,MONTH_OFFSET + MONTH_SIZE);
			byte[] mDate = Arrays.copyOfRange(record, MDATE_OFFSET, MDATE_OFFSET + numBytesIntField);
			byte[] day = Arrays.copyOfRange(record, DAY_OFFSET, DAY_OFFSET + DAY_SIZE);
			byte[] time = Arrays.copyOfRange(record, TIME_OFFSET,  TIME_OFFSET + numBytesIntField);
			byte[] sensorID = Arrays.copyOfRange(record, SENSORID_OFFSET,SENSORID_OFFSET + numBytesIntField);
			byte[] sensorName = Arrays.copyOfRange(record, SENSORNAME_OFFSET, SENSORNAME_OFFSET + SENSORNAME_SIZE);
			byte[] hourlycounts = Arrays.copyOfRange(record, COUNTS_OFFSET,COUNTS_OFFSET + numBytesIntField);
		
			//Get the value for the field bytes
			String strSDTname = new String(SDT_Name);
			int idValue = java.nio.ByteBuffer.wrap(id).getInt();
			int yearValue = java.nio.ByteBuffer.wrap(year).getInt();
			String dayValue = new String(day).trim();
			int mDateValue = java.nio.ByteBuffer.wrap(mDate).getInt();
			Date date = new Date(ByteBuffer.wrap(date_time).getLong());
		    String monthValue = new String(month).trim();
			int timeValue = java.nio.ByteBuffer.wrap(time).getInt();
			int sensorIDValue = java.nio.ByteBuffer.wrap(sensorID).getInt();
			String sensorNameValue = new String(sensorName).trim();
			int hourlycountsValue = java.nio.ByteBuffer.wrap(hourlycounts).getInt();

			// Get a string representation of the record for printing to stdout
			String recordString = strSDTname.trim() + "," + idValue
			+ "," + date + "," + yearValue +
			"," + monthValue + "," + mDateValue
			+ "," + dayValue + "," + timeValue
			+ "," + sensorIDValue + "," + sensorNameValue
			 + "," + hourlycountsValue;

	        System.out.println(recordString);

			
								
			objFile.close();
		} catch (IOException e) {
            e.printStackTrace();
         } 
	}

    	/*
	 * This function retrieves the data file name and the key value from the 
	 * index file.If the command is file it returns data file name and if the 
	 * command is key it returns key length.
	 */
	private static String getmetadata(String indexpath, String command) throws IOException {

		if(command == "file") {
			RandomAccessFile file = new RandomAccessFile(indexpath, "r");
			byte[] inputFileByte = new byte[256];
			file.read(inputFileByte);
			String inputFileName = new String(inputFileByte);
			file.close();
			return inputFileName.trim();
		}
		else {
			RandomAccessFile file = new RandomAccessFile(indexpath, "r");
			byte[] key = new byte[3];
			file.seek(257l);
			file.read(key);
			String keyLength = new String(key);

			file.close();
			return keyLength.trim();
		}
	}

      // Main Method
   public static void main(String[] args) {
	treequery objTree = new treequery();
     
      try {
       
       long startTime = System.currentTimeMillis();
	 
       objTree.searchTree(args);
       
       long stopTime = System.currentTimeMillis();
       
       System.out.println(stopTime - startTime + " ms");
       
    } catch(Exception e) {
		System.out.println(e);
    }
 }
}