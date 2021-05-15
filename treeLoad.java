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
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

public class treeLoad {

     	/*
	 * static variables of the Class Tree and size of node which 
	 * will be used throughout the program.
	 */
    static final int SDTName_SIZE = 25;
    static final int RECORD_SIZE = 161;
  	static Tree root;
	static int Nodesize = 0; 
  
     // Initialize root of the tree
   public void initialiseTree() {
        root = new Tree();
    }
   
/*
	 * Insert function which inserts the record into the tree. It validates 
	 * whether a record is already present in the index file and skips it.
	 * When the nodes inserted equals the node size of the tree, the split function 
	 * is called and the tree is balanced.
	 */
	@SuppressWarnings("null")
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
				
				if (key.compareTo(node.key.get(i)) == 0) {
					System.out.println("Duplicate Record " + key + "at line:" + offset);
					return;
				}
				else if (key.compareTo(node.key.get(i)) < 0) {
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


     // Function to read the each page into each record and extract the SDT_Name portion of
   // the record. Then create BPlusTree Index on the field and write to an index file.
public static void populateTree(int pageSize) throws IOException {
      RandomAccessFile objFile = new RandomAccessFile("heap." + pageSize, "r");
    //  RandomAccessFile objTree = new RandomAccessFile("treeIndex." + pageSize, "rw");
      String IndexFileName = "treeIndex." + pageSize;

      Path path = Paths.get("heap." + pageSize);
      byte[] bytes = Files.readAllBytes(path);
      int count = bytes.length;
              
      objFile.seek(RECORD_SIZE);
      byte[] objs = new byte[RECORD_SIZE];
      objFile.read(objs);
    
      for (int i = 0; i < count; i += RECORD_SIZE) {
        
         objFile.seek(i);
        byte[] objRecordByte = new byte[RECORD_SIZE];
         objFile.read(objRecordByte);
       
         byte[] SDT_Name = Arrays.copyOfRange(objRecordByte, 0, SDTName_SIZE);
         String strSDTname = new String(SDT_Name);
    
         int keyLength = Integer.parseInt(SDT_Name.length);
		    Nodesize = (1024 - keyLength) / keyLength + 8 ;
	   	int offset = 0;	
	    	insert(root,strSDTname, offset, i);
			offset += (i + 2);
	
         writeIndexfile(strSDTname, path.toString(), IndexFileName);

      //   WriteIndex(SDT_Name,path,IndexFileName);
    }
    objFile.close();
   }



   public void treeFunction_old(int pageSize) {
      
    File heapFile = new File("heap." + pageSize);
    File treeFile = new File("tree." + pageSize);
    
    // Used to store the empty space at the end of each page.
    trailingWhiteSpace = pageSize - (13 * RECORD_SIZE);
    
    boolean isNextPage = true;
    boolean isNextRecord = true;
    int counter = 1;
    
    try {
       FileInputStream objStream = new FileInputStream(heapFile);
       RandomAccessFile treeRAF = new RandomAccessFile(treeFile, "rw");
       
       while(isNextPage) {
          byte[] page = new byte[pageSize];
          objStream.read(page, 0, pageSize);
          
          // Check if the end of the file has been reached.
          // Done so by coverting byte to string of the page.
          // Trim that string and if the length is 0 then we 
          // know we are at the end.
          String pageCheck = new String(page);
          if(pageCheck.trim().length() == 0) {
             isNextPage = false;
             break;
          }
          
          isNextRecord = true;
          while(isNextRecord) {
             for(int i=0; i<page.length; i+=RECORD_SIZE) {
                
                // when i = 3564 then we know its the amout of max
                // record that can be stored in that page and it is the last
                // record.
                if(i == 3564) {
                   lastRecordFlag = true;
                }
                
                // when i = 3861 then we know no more records can be in this page
                // thus it is the end of the page.
                if(i == 3861) {
                   isNextRecord = false;
                   break;
                }
                byte[] SDTName = new byte[SDTName_SIZE];
                System.arraycopy(page, i, SDTName, 0, 50);
                String name = new String(SDTName).trim();
                
                // used to see how many records read
                System.out.println(counter);
                counter++; 
                
              //  int hashIndex = Math.abs(name.hashCode()) % HASH_MOD * INTSIZE;
                
                treeWrite(hashIndex, pageSize, hashRaf);
             }
             
             
          }
       }
       
       objStream.close();
       hashRaf.close();
       
       System.out.println("Completed");
     //  System.out.println("Insertions: " + insertionCounter);
     //  System.out.println("Collisions: " + collisionCounter);
    } catch (FileNotFoundException e) {
       e.printStackTrace();
    } catch (IOException e) {
       e.printStackTrace();
    }
 }

 	/*
	 * This function determines the size of the node by the formulae
	 *     (Block Size - key length)/(key length + Block Pointer)
	 * It calls the insert function for the index file to be created.Once the index 
	 * file is created it calls the write file function to insert into the file path.
	 */
	private static void WriteIndex(String key, String heapfilepath, String indexfilepath) throws IOException {
		
		int keyLength = Integer.parseInt(key);
		Nodesize = (1024 - keyLength) / keyLength + 8 ;
		int offset = 0;	
		String s;
		BufferedReader br = new BufferedReader(new FileReader(datafilepath));
		while ((s = br.readLine()) != null) {
			insert(root,(String) s.subSequence(0, keyLength), offset, s.length());
			offset += s.length() + 2;
		}
		br.close();
		writefile(key, heapfilepath, indexfilepath);
	}

    	/*
	 * This function writes the Tree into the index file in the form of bytes. 
	 * The first 1k block of data is allocated for the meta data and the next
	 * following 1k blocks contains the key,offset values and data length of 
	 * the record from the file.
	 */
	private static void writeIndexfile(String key, String heapfilepath, String indexfilename) throws IOException {
		FileOutputStream fout = new FileOutputStream(indexfilename);
		byte[] heapFileName = heapfilepath.getBytes();
		byte[] keyLength = key.getBytes();
		byte[] rootOffset = (" " + root.key.get(0)).getBytes();
		FileChannel fc = fout.getChannel();
		fc.write(ByteBuffer.wrap(heapFileName));
		fc.write(ByteBuffer.wrap(keyLength), 257l);
		fc.write(ByteBuffer.wrap(rootOffset), 260l);
		fc.position(1025l);
		ObjectOutputStream oos = new ObjectOutputStream(fout);
		oos.writeObject(root);
		oos.close();
	}

      // Main Method
   public static void main(String[] args) {
     treeLoad objTree = new treeLoad();
     
      try {
       String input = args[0];
       int pageSize = Integer.parseInt(input);
       
       long startTime = System.currentTimeMillis();
       
       objTree.populateTree(pageSize);
       
       long stopTime = System.currentTimeMillis();
       
       System.out.println(stopTime - startTime + " ms");
       System.out.println((stopTime - startTime) * 0.001 + " sec");
       System.out.println("Index file created successfully");
       
    } catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
       System.out.println("Invalid pagesize");
    }
 }
}