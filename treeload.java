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
import java.text.SimpleDateFormat;

//Class to hold the tree nodes
class Tree implements Serializable {
	public List<String> key; 
	public List<Tree> ptr;
	public List<Long> offsetvalue;
	public List<Integer> dataLength; 
	public Tree parent;
	public Tree rightpointer; 
	public Tree leftpointer; 
	public boolean isLeaf;
	
	public Tree() {
		this.key = new ArrayList<String>();
		this.ptr = new ArrayList<Tree>();
		this.offsetvalue = new ArrayList<Long>();
		this.dataLength = new ArrayList<Integer>();
		this.parent = null;
		this.rightpointer = null;
		this.leftpointer = null;
		this.isLeaf = false;
	}

}

public class treeload {

     	/*
	 * static variables of the Class Tree and size of node which 
	 * will be used throughout the program.
	 */
    //static final int SDTName_SIZE = 25;
    //static final int RECORD_SIZE = 161;
	static final int SDTName_SIZE = 24;
	static final int RECORD_SIZE = 111;
  	static Tree root;
	static int Nodesize = 0; 
  
     // Initialize the tree
   public void initialiseTree() {
        root = new Tree();
    }

	  // Function to read the each page into each record and extract the SDT_Name portion of
   // the record. Then create BPlusTree Index on the field and write to an index file.
@SuppressWarnings("null")
public static void populateTree(int pageSize)  {

	   String IndexFileName = "treeIndex." + pageSize;
	   String datafile = "heap." + pageSize;
	   Path path = Paths.get("heap." + pageSize);
        long startTime = 0;
        long finishTime = 0;
        int numBytesInOneRecord = constants.TOTAL_SIZE;
        int numBytesInSdtnameField = constants.STD_NAME_SIZE;
        int numBytesIntField = 4;
        int numRecordsPerPage = pageSize/numBytesInOneRecord;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        byte[] page = new byte[pageSize];
        FileInputStream inStream = null;

        try {
            inStream = new FileInputStream(datafile);
            int numBytesRead = 0;
            byte[] sdtnameBytes = new byte[numBytesInSdtnameField];
			int position=0;
            // until the end of the binary file is reached
            while ((numBytesRead = inStream.read(page)) != -1) {
              
                for (int i = 0; i < numRecordsPerPage; i++) {

                    // Copy record's SdtName (field is located at multiples of the total record byte length)
                    System.arraycopy(page, (i*numBytesInOneRecord), sdtnameBytes, 0, numBytesInSdtnameField);

                    // Check if field is empty; if so, end of all records found (packed organisation)
                    if (sdtnameBytes[0] == 0) {
                        // can stop checking records
                        break;
                    }

                    // Check for match to "text"
                    String sdtNameString = new String(sdtnameBytes);
                
					//System.out.println("name " + sdtNameString + "at line:" + numBytesInOneRecord);

					//insert(root,sdtNameString, (i*numBytesInOneRecord), numBytesInOneRecord);
					// insert(root,sdtNameString, position, numBytesInOneRecord);
					 insert(root,sdtNameString, position, numBytesInOneRecord);
				
					position += numBytesRead;		
			//	position += 1;									
                }
				
            }
			
			//writeIndexfile("6", path.toString(), IndexFileName);

            }
        catch (Exception e) {
            System.err.println("Error occcured " + e.getMessage());
        }

		
 }

public static void populateTree_old(int pageSize) {

	try {

	RandomAccessFile objFile = new RandomAccessFile("heap." + pageSize, "r");
		//  RandomAccessFile objTree = new RandomAccessFile("treeIndex." + pageSize, "rw");
   	 String IndexFileName = "treeIndex." + pageSize;
	  
	Path path = Paths.get("heap." + pageSize);
	byte[] bytes = Files.readAllBytes(path);
	int count = bytes.length;
			
	objFile.seek(RECORD_SIZE);
	byte[] objs = new byte[RECORD_SIZE];
	objFile.read(objs);


//	File fout = new File("out.txt");
//	FileOutputStream fos = new FileOutputStream(fout);
 
//	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
 
    int offset = 0;
	for (int i = 0; i < count; i += RECORD_SIZE) {
	  
	   objFile.seek(i);
	  byte[] objRecordByte = new byte[RECORD_SIZE];
	   objFile.read(objRecordByte);
	 
	   byte[] SDT_Name = Arrays.copyOfRange(objRecordByte, 0, SDTName_SIZE);
	   String strSDTname = new String(SDT_Name);
  
	//   System.out.println("SDTName: " + strSDTname);
	  
	//   bw.write(strSDTname);
	//   bw.newLine();

	  // int keyLength = Integer.parseInt(SDT_Name.length);
	   // int keyLength = SDT_Name.length;
		//  Nodesize = (1024 - keyLength) / keyLength + 8 ;
		Nodesize = (1024 - SDTName_SIZE) / SDTName_SIZE + 8 ;
			
		insert(root,strSDTname, i, 161);
		 // insert(root,strSDTname, offset, i);
		 // offset += (i + 2);
		   
	   writeIndexfile("6", path.toString(), IndexFileName);

    }
	//bw.close();

    objFile.close();
  } catch (FileNotFoundException e) {
	e.printStackTrace();
  } catch (IOException e) {
	e.printStackTrace();
  }
  
 }

 /*
	 * Insert function which inserts the record into the tree. It validates 
	 * whether a record is already present in the index file and skips it.
	 * When the nodes inserted equals the node size of the tree, the split function 
	 * is called and the tree is balanced.
	 */
private static void insert(Tree node, String key,long offset, int reclength) throws IOException {
	
	

		if ((node == null || node.key.isEmpty()) && node == root) {
			node.key.add(key);
			node.offsetvalue.add((Long) offset);
			node.dataLength.add(reclength);
			node.isLeaf = true;
			root = node;
			return;
		}
		else if (node != null || !node.key.isEmpty()) {
			for (int i = 0; i < node.key.size(); i++) {
				
			//	if (key.compareTo(node.key.get(i)) == 0) {
				//	System.out.println("Duplicate Record " + key + "at line:" + offset);
				//	return;
			//	 continue;
			//	}
			if (key.compareTo(node.key.get(i)) < 0) {
			//	else if (key.compareTo(node.key.get(i)) < 0) {
			//	if (key.compareTo(node.key.get(i)) < 0 || key.compareTo(node.key.get(i)) == 0) {
					if (!node.isLeaf && node.ptr.get(i) != null) {
						insert((Tree) node.ptr.get(i), key, offset, reclength);
						return;
					} 
					else if (node.isLeaf) {
						node.key.add("");
						node.offsetvalue.add(0l);
						node.dataLength.add(0);
						for (int j = node.key.size() - 2; j >= i; j--) {
							node.key.set(j + 1, node.key.get(j));
							node.offsetvalue.set(j + 1, node.offsetvalue.get(j));
							node.dataLength.set(j + 1, node.dataLength.get(j));
						}
						node.key.set(i, key);
						node.offsetvalue.set(i, offset);
						node.dataLength.set(i, reclength);
						if (node.key.size() == Nodesize) {
							split(node);
							return;
						} 
						else 
							return;
					}
				}
				else if (key.compareTo(node.key.get(i)) > 0) {
					if (i < node.key.size() - 1) {
						continue;
					}
					else if (i == node.key.size() - 1) {
						if (!node.isLeaf && node.ptr.get(i + 1) != null) {
							insert((Tree) node.ptr.get(i + 1),key, offset, reclength);
							return;
						}

						else if (node.isLeaf) {
							node.key.add("");
							node.offsetvalue.add(0l);
							node.dataLength.add(0);
							node.key.set(i + 1, key);
							node.offsetvalue.set(i + 1, offset);
							node.dataLength.set(i + 1, reclength);
						}
						
						if (node.key.size() == Nodesize) {
							split(node);
							return;
						} 
						else
							return;
					}
				}
			}
		}
	}

	/*
	 * This function splits the tree and balances the node in it. 
	 * When the nodes inserted exceeds the node size this function is called 
	 * After split it creates the pointer and stores the pointer of the 
	 * parent, right node and left node to it if it is an internal node and 
	 * if it is a leaf node stores the right pointer to the node. Before splitting
	 * the function sorts the key in ascending order and perform the split.
	 */
	private static void split(Tree node) throws IOException {
		Tree leftnode = new Tree();
		Tree rightnode = new Tree(); 
		Tree tempparent = new Tree(); 
		Tree parent;
		int newPosKey = 0, split = 0;
		
		if (node.isLeaf) {
			if (node.key.size() % 2 == 0)
				split = (node.key.size() / 2) - 1;
			else
				split = node.key.size() / 2;

			rightnode.isLeaf = true;
			for (int i = split; i < node.key.size(); i++) {
				rightnode.key.add(node.key.get(i));
				rightnode.offsetvalue.add(node.offsetvalue.get(i));
				rightnode.dataLength.add(node.dataLength.get(i));
			}
			
			leftnode.isLeaf = true;
			for (int i = 0; i < split; i++) {
				leftnode.key.add(node.key.get(i));
				leftnode.offsetvalue.add(node.offsetvalue.get(i));
				leftnode.dataLength.add(node.dataLength.get(i));
			}
			
			if (node.rightpointer != null)
				rightnode.rightpointer = node.rightpointer;
			else
				rightnode.rightpointer = null;
			if (node.leftpointer != null)
				leftnode.leftpointer = node.leftpointer;
			else
				leftnode.leftpointer = null;

			leftnode.rightpointer = rightnode;
			rightnode.leftpointer = leftnode;

			if (node.parent == null) {
				tempparent.isLeaf = false;
				tempparent.key.add(rightnode.key.get(0));
				tempparent.ptr.add(leftnode);
				tempparent.ptr.add(rightnode);
				leftnode.parent = tempparent;
				rightnode.parent = tempparent;
				root = tempparent;
				node = tempparent;
			}
			else if (node.parent != null) {
				parent = node.parent;				
				parent.key.add(rightnode.key.get(0));
				Collections.sort(parent.key);
				leftnode.parent = parent;
				rightnode.parent = parent;
				newPosKey = parent.key.indexOf(rightnode.key.get(0));

				if (newPosKey < parent.key.size() - 1) {
					parent.ptr.add(null);

					for (int i = parent.key.size() - 1; i > newPosKey; i--) {
						parent.ptr.set(i + 1, parent.ptr.get(i));
					}

					parent.ptr.set(newPosKey + 1, rightnode);
					parent.ptr.set(newPosKey, leftnode);
				}

				else if (newPosKey == parent.key.size() - 1) {
					parent.ptr.set(newPosKey, leftnode);
					parent.ptr.add(rightnode);
				}
				if (node.leftpointer != null) {
					node.leftpointer.rightpointer = leftnode;
					leftnode.leftpointer = node.leftpointer;
				}
				if (node.rightpointer != null) {
					node.rightpointer.leftpointer = rightnode;
					rightnode.rightpointer = node.rightpointer;
				}
				if (parent.key.size() == Nodesize) {
					split(parent);
					return;
				} else
					return;
			}
		}
		else if (!node.isLeaf) {
			rightnode.isLeaf = false;
			if (node.key.size() % 2 == 0)
				split = (node.key.size() / 2) - 1;
			else
				split = node.key.size() / 2;

			String popKey = node.key.get(split);
			int k = 0, p = 0;
			for (int i = split + 1; i < node.key.size(); i++) {
				rightnode.key.add(node.key.get(i));
			}
			for (int i = split + 1; i < node.ptr.size(); i++) {
				rightnode.ptr.add(node.ptr.get(i));
				rightnode.ptr.get(k++).parent = rightnode;
			}
			k = 0;
			for (int i = 0; i < split; i++) {
				leftnode.key.add(node.key.get(i));
			}
			for (int i = 0; i < split + 1; i++) {
				leftnode.ptr.add(node.ptr.get(i));
				leftnode.ptr.get(p++).parent = leftnode;
			}
			p = 0;
			if (node.parent == null) {
				tempparent.isLeaf = false;
				tempparent.key.add(popKey);
				tempparent.ptr.add(leftnode);
				tempparent.ptr.add(rightnode);
				leftnode.parent = tempparent;
				rightnode.parent = tempparent;
				node = tempparent;
				root = tempparent;
				return;
			}
			else if (node.parent != null) {
				parent = node.parent;
				parent.key.add(popKey);
				Collections.sort(parent.key);
				newPosKey = parent.key.indexOf(popKey);

				if (newPosKey == parent.key.size() - 1) {
					parent.ptr.set(newPosKey, leftnode);
					parent.ptr.add(rightnode);
					rightnode.parent = parent;
					leftnode.parent = parent;
				}
				else if (newPosKey < parent.key.size() - 1) {
					int ptrSize = parent.ptr.size();
					parent.ptr.add(null);
					for (int i = ptrSize - 1; i > newPosKey; i--) {
						parent.ptr.set(i + 1, parent.ptr.get(i));
					}

					parent.ptr.set(newPosKey, leftnode);
					parent.ptr.set(newPosKey + 1, rightnode);
					leftnode.parent = parent;
					rightnode.parent = parent;
				}
				
				if (parent.key.size() == Nodesize) {
					split(parent);
					return;
				} else
					return;
			}
		}
	}


   	/*
	 * This function determines the size of the node by the formulae
	 *     (Block Size - key length)/(key length + Block Pointer)
	 * It calls the insert function for the index file to be created.Once the index 
	 * file is created it calls the write file function to insert into the file path.
	 */
	// private static void WriteIndex_old(String key, String heapfilepath, String indexfilepath) throws IOException {
		
	// 	int keyLength = key;
	// 	Nodesize = (1024 - keyLength) / keyLength + 8 ;
	// 	int offset = 0;	
	// 	String s;
	// 	BufferedReader br = new BufferedReader(new FileReader(heapfilepath));
	// 	while ((s = br.readLine()) != null) {
	// 		insert(root,(String) s.subSequence(0, keyLength), offset, s.length());
	// 		offset += s.length() + 2;
	// 	}
	// 	br.close();
	// 	writeIndexfile(key, heapfilepath, indexfilepath);
	// }

	private static void CreateTree(int pageSize) throws IOException {
		try{
		String IndexFileName = "treeIndex." + pageSize;
		Path path = Paths.get("heap." + pageSize);

		populateTree(pageSize);
		writeIndexfile("6", path.toString(), IndexFileName);
	}
	catch (Exception e) {
		System.err.println("Error occcured " + e.getMessage());
	}

	}

    	/*
	 * This function writes the Tree into the index file in the form of bytes. 
	 * The first 1k block of data is allocated for the meta data and the next
	 * following 1k blocks contains the key,offset values and data length of 
	 * the record from the file.
	 */
	private static void writeIndexfile(String key, String heapfilepath, String indexfilename) throws IOException {
	
	//	for (int a = 0; a < root.key.size(); a++) {
	//		System.out.println("node:" + root.key.get(a));
	//	}
	
		FileOutputStream fout = new FileOutputStream(indexfilename);
		byte[] heapFileName = heapfilepath.getBytes();
		byte[] keyLength = key.getBytes();
		byte[] rootOffset = (" " + root.key.get(0)).getBytes();
		FileChannel fc = fout.getChannel();
		fc.write(ByteBuffer.wrap(heapFileName));
		fc.write(ByteBuffer.wrap(keyLength), 257l);
	//	fc.write(ByteBuffer.wrap(rootOffset), 285l);
		fc.write(ByteBuffer.wrap(rootOffset), 260l);
		fc.position(1025l);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(root);
		oos.close();
	}

      // Main Method
   public static void main(String[] args) {
     treeload objTree = new treeload();
     
      try {
       String input = args[0];
       int pageSize = Integer.parseInt(input);
       
       long startTime = System.currentTimeMillis();
       
	   objTree.initialiseTree();
      // objTree.populateTree(pageSize);
	  objTree.CreateTree(pageSize);
     
       long stopTime = System.currentTimeMillis();
       
       System.out.println(stopTime - startTime + " ms");
       System.out.println((stopTime - startTime) * 0.001 + " sec");
       System.out.println("Index file created successfully");
	   
	} catch (Exception e) {   
   // } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
       System.out.println("Invalid pagesize");
    }

 }
}