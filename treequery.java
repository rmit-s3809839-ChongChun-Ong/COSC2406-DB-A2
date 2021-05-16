import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
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
//Class to hold the tree nodes
//class Tree implements Serializable {
//	public List<String> key; 
//	public List<Tree> ptr;
//	public List<Long> offsetvalue;
//	public List<Integer> dataLength; 
//	public Tree parent;
//	public Tree rightpointer; 
//	public Tree leftpointer; 
//	public boolean isLeaf;
	
//	public Tree() {
//		this.key = new ArrayList<String>();
//		this.ptr = new ArrayList<Tree>();
//		this.offsetvalue = new ArrayList<Long>();
//		this.dataLength = new ArrayList<Integer>();
//		this.parent = null;
//		this.rightpointer = null;
//		this.leftpointer = null;
//		this.isLeaf = false;
//	}
//}

public class treequery {

     	/*
	 * static variables of the Class Tree and size of node which 
	 * will be used throughout the program.
	 */
    static final int SDTName_SIZE = 25;
    static final int RECORD_SIZE = 161;
  	static Tree root;
	static int Nodesize = 0; 
  
     // Initialize the tree
   public void initialiseTree() {
        root = new Tree();
    }

	  // Function to read the each page into each record and extract the SDT_Name portion of
   // the record. Then create BPlusTree Index on the field and write to an index file.
@SuppressWarnings("null")

	/*
	 * This function retrieves the data file path from the index file 
	 * and calls the corresponding functions for searching a record or 
	 * listing records.
	 */
	private static void searchTree(String[] args) {

     String[] SDTNames = Arrays.copyOfRange(args, 0, args.length - 1);

   	String spageSize = args[args.length - 1];
      String SDTName = "";
	  String indexFileName = "treeIndex." + spageSize;
     
	  try{
      // If argument given is only 1 then first element in array is the name
      // otherwise join all the elements with space as a delimiter
      if(SDTNames.length == 1) {
		SDTName = SDTNames[0];
      } else {
	 //  	SDTName = String.join(" ", SDTNames);
	 StringBuilder builder = new StringBuilder();
    for(String s : SDTNames) {
        builder.append(s + " ");
        }
        SDTName = builder.toString();

      }
		//searchForInput(hashIndex, name, heapFile, hashFile);
		     
		FileInputStream fin = new FileInputStream(indexFileName);
		FileChannel fc = fin.getChannel();
		fc.position(1025l);

		ObjectInputStream ois = new ObjectInputStream(fin);
        Tree newRoot = (Tree) ois.readObject();
		ois.close();
		
		//FileOutputStream fout = new FileOutputStream("object.txt");		
	//	ObjectOutputStream oos = new ObjectOutputStream(fout);
	//	oos.writeObject(newRoot);
	//	oos.close();
		//System.out.println(newRoot);

		//if(fnchoice.equals(" "))
	 	searchData(newRoot, indexFileName, SDTName, spageSize);
	//	else
	//		ListData(newRoot, indexFile, SDTName, Integer.parseInt(fnchoice));
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
	//	System.out.println(key);
	//	System.out.println(node.key.size());
		//for (int a = 0; a < node.key.size(); a++) {
		//	System.out.println(node.key.get(a));
		//}
			
		int indexfilekeylen = Integer.parseInt(getmetadata(indexFile,"key"));
		if(key.length() > indexfilekeylen) {
			key = key.substring(0, indexfilekeylen);
		//	System.out.println("key a");
		}
		//else if(key.length() < indexfilekeylen) {
		//	System.out.println("key b:" + key.length() + "~" + indexfilekeylen);
		//	for(int i = key.length();i < indexfilekeylen; i++)
		//		key = key + " ";
				
		//}
	//	for (int a = 0; a < node.key.size(); a++) {
	//		System.out.println("node:" + node.offsetvalue);
	//	}

		for (int b = 0; b < node.ptr.size(); b++) {
			Tree tmpNode = node.ptr.get(b);

			for (int c = 0; c < tmpNode.key.size(); c++) {
				boolean tmpKeyIndex = tmpNode.key.get(c).contains(key);
				
				int keyIndex = c;
				
				if (tmpKeyIndex == true) {
				//	System.out.println(tmpNode.offsetvalue.get(c) + "~" + tmpNode.key.get(c));

					//long offsetvalue = tmpNode.offsetvalue.get(c);
					int offsetvalue = tmpNode.offsetvalue.get(c).intValue();
					int dataLength = tmpNode.dataLength.get(c);
				 	DisplayRecord(indexFile, offsetvalue, dataLength, pageSize,key);
				//	return;
				}

			//	System.out.println(tmpNode.key.get(c));
			}
			
		}

		for (int i = 0; i < node.key.size(); i++) {

			//System.out.println(node.key.get(i));

			if (node.isLeaf) {
				//int keyIndex = node.key.indexOf(key);
				boolean tmpKeyIndex = node.key.get(i).contains(key);
				int keyIndex = -1;
				
				if (tmpKeyIndex == true) {
					keyIndex = i;
				}
				
				//System.out.println(tmpKeyIndex);

				if (keyIndex == -1) {
				//	System.out.println("A");
				
				//	System.out.println("Data not found");
				//	return;
					//if (i < node.key.size() - 1) {
						continue;
					//}
					
	
				} else if (keyIndex != -1) { 
				//	System.out.println("A2");
					long offsetvalue = node.offsetvalue.get(keyIndex);
					int dataLength = node.dataLength.get(keyIndex);
				//	DisplayRecord(indexFile, offsetvalue, dataLength, pageSize);
				//	return;
				}
			}
			else if (key.compareTo(node.key.get(i)) < 0) {
			//	System.out.println("B");
			//System.out.println("ptr1:" + node.ptr.get(i));
			//	if (!node.isLeaf && node.ptr.get(i) != null) {
				if (node.ptr.get(i) != null) {
					searchData(node.ptr.get(i), indexFile, key, pageSize);
					//searchData(node.ptr.get(i+1), indexFile, key, pageSize);
					return;
				}
			}
			else if (key.compareTo(node.key.get(i)) >= 0) {
			//	System.out.println("c");
   		     if (i < node.key.size() - 1) {
					continue;
		     }
			else if (i == node.key.size() - 1) {
			
				if (!node.isLeaf && node.ptr.get(i + 1) != null) {
			//			System.out.println("ptr2:" + node.key.get(i+1));
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

	 
 	/*
	 * This function retrieves the data from the data file and prints it 
	 * along with the line number of the record in the data file.
	 */
	private static void DisplayRecord(String indexFile, int offset, int dataLength, String pageSize, String text) {	
		
		String datafile = "heap." + pageSize;
        long startTime = 0;
        long finishTime = 0;
        int numBytesInOneRecord = constants.TOTAL_SIZE;
        int numBytesInSdtnameField = constants.STD_NAME_SIZE;
        int numBytesIntField = 4;
        int numRecordsPerPage = Integer.parseInt(pageSize)/numBytesInOneRecord;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        byte[] page = new byte[Integer.parseInt(pageSize)];
        FileInputStream inStream = null;

        try {
            inStream = new FileInputStream(datafile);
            int numBytesRead = 0;
            startTime = System.nanoTime();
            // Create byte arrays for each field
            byte[] sdtnameBytes = new byte[numBytesInSdtnameField];
            byte[] idBytes = new byte[constants.ID_SIZE];
            byte[] dateBytes = new byte[constants.DATE_SIZE];
            byte[] yearBytes = new byte[constants.YEAR_SIZE];
            byte[] monthBytes = new byte[constants.MONTH_SIZE];
            byte[] mdateBytes = new byte[constants.MDATE_SIZE];
            byte[] dayBytes = new byte[constants.DAY_SIZE];
            byte[] timeBytes = new byte[constants.TIME_SIZE];
            byte[] sensorIdBytes = new byte[constants.SENSORID_SIZE];
            byte[] sensorNameBytes = new byte[constants.SENSORNAME_SIZE];
            byte[] countsBytes = new byte[constants.COUNTS_SIZE];

			while ((numBytesRead = inStream.read(page)) != -1) {
                // Process each record in page
               // for (int i = 0; i < numRecordsPerPage; i++) {
                
					System.arraycopy(page, (offset*numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);
		
					String sdtNameString = new String(sdtnameBytes);

					String sFormat = String.format("(.*)%s(.*)", text);

				   if (sdtNameString.matches(sFormat)) {
		
				//	System.out.println("SDTName:" + offset + "~" + sdtNameString);
		 
					System.arraycopy(page, ((offset*numBytesInOneRecord) + constants.ID_OFFSET), idBytes, 0, numBytesIntField);
					System.arraycopy(page, ((offset*numBytesInOneRecord) + constants.DATE_OFFSET), dateBytes, 0, constants.DATE_SIZE);
					System.arraycopy(page, ((offset*numBytesInOneRecord) + constants.YEAR_OFFSET), yearBytes, 0, numBytesIntField);
					System.arraycopy(page, ((offset*numBytesInOneRecord)  + constants.MONTH_OFFSET), monthBytes, 0, constants.MONTH_SIZE);
					System.arraycopy(page, ((offset*numBytesInOneRecord)  + constants.MDATE_OFFSET), mdateBytes, 0, numBytesIntField);
					System.arraycopy(page, ((offset*numBytesInOneRecord)  + constants.DAY_OFFSET), dayBytes, 0, constants.DAY_SIZE);
					System.arraycopy(page, ((offset*numBytesInOneRecord)  + constants.TIME_OFFSET), timeBytes, 0, numBytesIntField);
					System.arraycopy(page, ((offset*numBytesInOneRecord)  + constants.SENSORID_OFFSET), sensorIdBytes, 0, numBytesIntField);
					System.arraycopy(page, ((offset*numBytesInOneRecord)  + constants.SENSORNAME_OFFSET), sensorNameBytes, 0, constants.SENSORNAME_SIZE);
					System.arraycopy(page, ((offset*numBytesInOneRecord)  + constants.COUNTS_OFFSET), countsBytes, 0, numBytesIntField);
		
					// Convert long data into Date object
					Date date = new Date(ByteBuffer.wrap(dateBytes).getLong());
		
					// Get a string representation of the record for printing to stdout
					String record = sdtNameString.trim() + "," + ByteBuffer.wrap(idBytes).getInt()
							+ "," + dateFormat.format(date) + "," + ByteBuffer.wrap(yearBytes).getInt() +
							"," + new String(monthBytes).trim() + "," + ByteBuffer.wrap(mdateBytes).getInt()
							+ "," + new String(dayBytes).trim() + "," + ByteBuffer.wrap(timeBytes).getInt()
							+ "," + ByteBuffer.wrap(sensorIdBytes).getInt() + "," +
							new String(sensorNameBytes).trim() + "," + ByteBuffer.wrap(countsBytes).getInt();
					System.out.println(record);
		

					
				}
			}

		

            // until the end of the binary file is reached
            // while ((numBytesRead = inStream.read(page)) != -1) {
            //     // Process each record in page
            //     for (int i = 0; i < numRecordsPerPage; i++) {

            //         // Copy record's SdtName (field is located at multiples of the total record byte length)
            //         System.arraycopy(page, (i*numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);

            //         // Check if field is empty; if so, end of all records found (packed organisation)
            //         if (sdtnameBytes[0] == 0) {
            //             // can stop checking records
            //             break;
            //         }

            //         // Check for match to "text"
            //         String sdtNameString = new String(sdtnameBytes);
            //        // String sFormat = String.format("(.*)%s(.*)", text);
            //         // if match is found, copy bytes of other fields and print out the record
            //        // if (sdtNameString.matches(sFormat)) {
            //             /*
            //              * Fixed Length Records (total size = 112 bytes):
            //              * SDT_NAME field = 24 bytes, offset = 0
            //              * id field = 4 bytes, offset = 24
            //              * date field = 8 bytes, offset = 28
            //              * year field = 4 bytes, offset = 36
            //              * month field = 9 bytes, offset = 40
            //              * mdate field = 4 bytes, offset = 49
            //              * day field = 9 bytes, offset = 53
            //              * time field = 4 bytes, offset = 62
            //              * sensorid field = 4 bytes, offset = 66
            //              * sensorname field = 38 bytes, offset = 70
            //              * counts field = 4 bytes, offset = 108
            //              *
            //              * Copy the corresponding sections of "page" to the individual field byte arrays
            //              */
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.ID_OFFSET), idBytes, 0, numBytesIntField);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.DATE_OFFSET), dateBytes, 0, constants.DATE_SIZE);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.YEAR_OFFSET), yearBytes, 0, numBytesIntField);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.MONTH_OFFSET), monthBytes, 0, constants.MONTH_SIZE);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.MDATE_OFFSET), mdateBytes, 0, numBytesIntField);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.DAY_OFFSET), dayBytes, 0, constants.DAY_SIZE);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.TIME_OFFSET), timeBytes, 0, numBytesIntField);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.SENSORID_OFFSET), sensorIdBytes, 0, numBytesIntField);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.SENSORNAME_OFFSET), sensorNameBytes, 0, constants.SENSORNAME_SIZE);
            //             System.arraycopy(page, ((i*numBytesInOneRecord) + constants.COUNTS_OFFSET), countsBytes, 0, numBytesIntField);

            //             // Convert long data into Date object
            //             Date date = new Date(ByteBuffer.wrap(dateBytes).getLong());

            //             // Get a string representation of the record for printing to stdout
            //             String record = sdtNameString.trim() + "," + ByteBuffer.wrap(idBytes).getInt()
            //                     + "," + dateFormat.format(date) + "," + ByteBuffer.wrap(yearBytes).getInt() +
            //                     "," + new String(monthBytes).trim() + "," + ByteBuffer.wrap(mdateBytes).getInt()
            //                     + "," + new String(dayBytes).trim() + "," + ByteBuffer.wrap(timeBytes).getInt()
            //                     + "," + ByteBuffer.wrap(sensorIdBytes).getInt() + "," +
            //                     new String(sensorNameBytes).trim() + "," + ByteBuffer.wrap(countsBytes).getInt();
            //             System.out.println(record);
            //       //  }
            //     }
            // }
			
            finishTime = System.nanoTime();
        }
		catch (IOException e) {
			System.err.println("IO Exception " + e.getMessage());
		}
	
		long timeInMilliseconds = (finishTime - startTime)/constants.MILLISECONDS_PER_SECOND;
        System.out.println("Time taken: " + timeInMilliseconds + " ms");
	}
    

	private static void DisplayRecord_old(String indexFile, long offset, int dataLength, String pageSize) {		
		//String inputFileName = getmetadata(indexFile, "file");
	//	RandomAccessFile objFile = new RandomAccessFile(inputFileName, "r");

	try{
	 RandomAccessFile objFile = new RandomAccessFile("heap." + pageSize, "r");
	      
	 //	file.seek(offset);
	//	byte buffer[] = new byte[dataLength + 1];
	//	file.read(buffer);
	//	String str = new String(buffer);
	//	str = str.replace("\n", "");
	//	System.out.println("At " + offset);
	//	file.close();

		
			objFile.seek(offset);
			byte[] record = new byte[RECORD_SIZE];
			//byte[] record = new byte[161];
			objFile.read(record, 0, RECORD_SIZE);

			 byte[] SDT_Name = Arrays.copyOfRange(record, 0, 25);
		    byte[] id = Arrays.copyOfRange(record, 25, 32);
			byte[] date_time = Arrays.copyOfRange(record, 32, 55);
			byte[] year = Arrays.copyOfRange(record, 55, 77);
			byte[] month = Arrays.copyOfRange(record, 77, 85);
			byte[] mDate = Arrays.copyOfRange(record, 86, 90);
			byte[] day = Arrays.copyOfRange(record, 90, 99);
			byte[] time = Arrays.copyOfRange(record, 99, 103);
			byte[] sensorID = Arrays.copyOfRange(record, 103, 107);
			byte[] sensorName = Arrays.copyOfRange(record, 107, 157);
			byte[] hourlycounts = Arrays.copyOfRange(record, 157, 161);
		
			int idValue = java.nio.ByteBuffer.wrap(id).getInt();
			int yearValue = java.nio.ByteBuffer.wrap(year).getInt();
			int mDateValue = java.nio.ByteBuffer.wrap(mDate).getInt();
			int timeValue = java.nio.ByteBuffer.wrap(time).getInt();
			int sensorIDValue = java.nio.ByteBuffer.wrap(sensorID).getInt();
			int hourlycountsValue = java.nio.ByteBuffer.wrap(hourlycounts).getInt();
	
			   String strSDTname = new String(SDT_Name);
			
			//	 if(strSDTname.toLowerCase().contains(text.toLowerCase())) {
					System.out.println("At Byte Position: " + offset);
					System.out.println("SDT_Name: " + strSDTname);
					System.out.println("ID: " + idValue);
					System.out.println("Date_Time: " + new String(date_time));
					System.out.println("Year: " + yearValue);
					 System.out.println("Month: " + new String(month));
					 System.out.println("mDate: " + mDateValue);
					System.out.println("Day: " + new String(day));
					System.out.println("Time: " + timeValue);
					System.out.println("Sensor_ID: " + sensorIDValue);
					System.out.println("SensorName: " + new String(sensorName));
					System.out.println("HourlyCounts: " + hourlycountsValue);
					System.out.println();
			//	 }
			//	 else {
			//		recordCount++;
			//		if(recordCount == tmpTotalRecordCountPerPage) {
			//		   pageNum++;
			//		   recordCount=0;
			//		}
			//	 }
						
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
			//byte[] key = new byte[25];
			file.seek(257l);
			file.read(key);
			String keyLength = new String(key);

		//	System.out.println(keyLength);

			file.close();
			return keyLength.trim();
		}
	}

      // Main Method
   public static void main(String[] args) {
	treequery objTree = new treequery();
     
      try {
       
       long startTime = System.currentTimeMillis();
       
	  //objTree.initialiseTree();
       objTree.searchTree(args);
       
       long stopTime = System.currentTimeMillis();
       
       // System.out.println((stopTime - startTime) * 0.001 + " sec");

       System.out.println(stopTime - startTime + " ms");
       
    } catch(ArrayIndexOutOfBoundsException e) {
		System.out.println(e);
		//  System.out.println("Invalid pagesize");
    }
 }
}