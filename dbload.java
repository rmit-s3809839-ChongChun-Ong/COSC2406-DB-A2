
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

//dbload class
public class dbload {

   static final int SDTNameMaxLength = 25;   
   static final int IDMaxLength = 8;
   static final int DTMaxLength = 22;
   static final int yearMaxLength = 4;
   static final int monthMaxLength = 9;
   static final int mDateMaxLength = 4;
   static final int dayMaxLength = 9;
   static final int timeMaxLength = 4;
   static final int sIDMaxLength = 4;
   static final int sNameMaxLength = 50;
   static final int hourlyCountsMaxLength = 4;

   //page class to hold pages
    static class page {
       ArrayList<ArrayList<rec>> lstPage = new ArrayList<ArrayList<rec>>();
   }

   //record class to hold records
   static class rec {
    int ID;
    String Date_Time;
    int Year;
    String Month;
    int mDate;
    String Day;
    int Time;
    int SensorID;
    String SensorName;
    String SDT_Name;
    int HourlyCounts;
 }

//InitaliseOutputStream. File created is heap.(pagesize).
public static DataOutputStream GenerateOutputStream(int pageSize) throws FileNotFoundException {
    DataOutputStream oStream = new DataOutputStream(new FileOutputStream("heap." + pageSize));
    return oStream;
 }

 //This method will read data from the .csv file using a buffered reader.
 //All strings will be converted to bytes and written to file through DataOutputStream
 //Record size = Total size of all the fields.
 //All records are stored in a page until no more record can fit into the page.
 public static void CreateHeapFile(int pagesize, String datafile){
   File file = new File("heap" + "." + pagesize);
   int pageNum = 1;

   String line = "";  
   String splitBy = ",";  
   try   
   {  
   //parsing a CSV file into BufferedReader class constructor  
   BufferedReader br = new BufferedReader(new FileReader(datafile));  
   DataOutputStream objStream = GenerateOutputStream(pagesize);

   String headerLine = br.readLine();
   page objPage = new page();
   objPage.lstPage.add(new ArrayList<rec>());
   rec objRow = null;
   int intTotalRecordSize= 0;
   int intTotalRowCount = 0;
   int intPageNo = 0;

   while ((line = br.readLine()) != null)   //returns a Boolean value  
   {  
   
   objRow = new rec();

   int SDTLength = 0;
          int IDLength = 0;
          int DTLength = 0;
          int yearLength = 0;
          int monLength = 0;
          int mDtLength = 0;
          int dayLength = 0;
          int timeLength = 0;
          int sIDLength = 0;
          int sNameLength = 0;
          int hcLength = 0;
          int tmpRecordSize = 0;
          int maxLength = 0;
          String strDateTime="";
          String strSensorID="";
          String strSDTNameValue="";

   String[] ArrPC = line.split(splitBy);    // use comma as separator  
   
      //Add new field SDT_Name.
       strSDTNameValue = ArrPC[7] + "_" + ArrPC[1];
       byte[] objtmpByte = strSDTNameValue.getBytes("UTF-8");

       byte[] tmpSDTByte = Arrays.copyOf(objtmpByte, SDTNameMaxLength);
       objStream.write(tmpSDTByte);

       objRow.SDT_Name = strSDTNameValue;
       tmpRecordSize += tmpSDTByte.length;
    
     for (int x=0; x < ArrPC.length; x++){
                       
                if (x == 0) {
                  int idValue = Integer.parseInt(ArrPC[x]);
                  byte[] objByte =  intToByteArray(idValue);

                  byte[] tmpByte = Arrays.copyOf(objByte, IDMaxLength);
                    objStream.write(tmpByte);
                     IDLength = tmpByte.length;
                    tmpRecordSize += IDLength;
                    objRow.ID = Integer.parseInt(ArrPC[x]);
                }
                else if (x == 1) {
                  byte[] objByte = ArrPC[x].getBytes("UTF-8");

                  byte[] tmpByte = Arrays.copyOf(objByte, DTMaxLength);
                     objStream.write(tmpByte);
                     DTLength = tmpByte.length;
                   tmpRecordSize += DTLength;
                  objRow.Date_Time = ArrPC[x];
               }
                else if (x == 2) {
                  int yearValue = Integer.parseInt(ArrPC[x]);
                  byte[] objByte =  intToByteArray(yearValue);
                  byte[] tmpByte = Arrays.copyOf(objByte, DTMaxLength);
                  objStream.write(tmpByte);
                  yearLength = tmpByte.length;
                  tmpRecordSize += yearLength;
                 objRow.Year = Integer.parseInt(ArrPC[x]);
               }
               else if (x == 3) {
                  byte[] objByte = ArrPC[x].getBytes("UTF-8");
                  byte[] tmpByte = Arrays.copyOf(objByte, monthMaxLength);
                  objStream.write(tmpByte);
                  monLength = tmpByte.length;             
                 tmpRecordSize += monLength;
                 objRow.Month = ArrPC[x];
      
               }
               else if (x == 4) {
                  int mDateValue = Integer.parseInt(ArrPC[x]);
                  byte[] objByte =  intToByteArray(mDateValue);
                  byte[] tmpByte = Arrays.copyOf(objByte, mDateMaxLength);
                  objStream.write(tmpByte);
                  mDtLength = tmpByte.length;                     
                 tmpRecordSize += mDtLength;
                 objRow.mDate = Integer.parseInt(ArrPC[x]);
              }
               else if (x == 5) {
                  byte[] objByte = ArrPC[x].getBytes("UTF-8");
                 byte[] tmpByte = Arrays.copyOf(objByte, dayMaxLength);
                  objStream.write(tmpByte);
                  dayLength = tmpByte.length;        
                  tmpRecordSize += dayLength;
                 objRow.Day = ArrPC[x];
               }
               else if (x == 6) {
                  int timeValue = Integer.parseInt(ArrPC[x]);
                  byte[] objByte =  intToByteArray(timeValue);
                  byte[] tmpByte = Arrays.copyOf(objByte, timeMaxLength);
                  objStream.write(tmpByte);
                  timeLength = tmpByte.length;
                  tmpRecordSize += timeLength;
                  objRow.Time = Integer.parseInt(ArrPC[x]);
               }
               else if (x == 7) {
                  int sensorIDValue = Integer.parseInt(ArrPC[x]);
                  byte[] objByte =  intToByteArray(sensorIDValue);
                  byte[] tmpByte = Arrays.copyOf(objByte, sIDMaxLength);
                  objStream.write(tmpByte);
                  sIDLength = tmpByte.length;  
                 tmpRecordSize += sIDLength;
                 objRow.SensorID =  Integer.parseInt(ArrPC[x]);
               }
               else if (x == 8) {
                  byte[] objByte = ArrPC[x].getBytes("UTF-8");
                  byte[] tmpByte = Arrays.copyOf(objByte, sNameMaxLength);
                  objStream.write(tmpByte);
                  sNameLength = tmpByte.length;   
                 tmpRecordSize += sNameLength;
                objRow.SensorName = ArrPC[x];
              
               }
               else if (x == 9) {
                  int hourlyCountsValue = Integer.parseInt(ArrPC[x]);
                  byte[] objByte =  intToByteArray(hourlyCountsValue);
                  byte[] tmpByte = Arrays.copyOf(objByte, hourlyCountsMaxLength);
                  objStream.write(tmpByte);
                  hcLength = tmpByte.length;            
                  tmpRecordSize += hcLength;
                 objRow.HourlyCounts = Integer.parseInt(ArrPC[x]);
             }
                   
             }
              
                 if((tmpRecordSize + intTotalRecordSize) < pagesize){
                    intTotalRowCount++;
                    intTotalRecordSize += tmpRecordSize;
                    objPage.lstPage.get(intPageNo).add(objRow);
                                    
                  }
                else{
                   intPageNo++;
                    objPage.lstPage.add(new ArrayList<rec>());
                    objPage.lstPage.get(intPageNo).add(objRow);                   
                    intTotalRecordSize = tmpRecordSize;
                  }
  
   }  

       //Print out the total no of pages and total no of records
    System.out.println("Total number of pages: " + objPage.lstPage.size());
    int intTotalRows=0;
    for(int i=0; i<objPage.lstPage.size(); i++) {
       for(int j=0; j<objPage.lstPage.get(i).size(); j++) {
         intTotalRows++;
       }
    }
    System.out.println("Total number of records: " + intTotalRows);

   }   
   catch (IOException e)   
   {  
   e.printStackTrace();  
   }  
 }
 
private static byte[] intToByteArray(final int i ) throws IOException {      
   byte[] bytes = java.nio.ByteBuffer.allocate(4).putInt(i).array();

   return bytes;
}

 //Main method
 //Calculates the time taken to create heap file in ms.
 public static void main(String[] args) {
    long intStartTime = System.nanoTime();
    try {
   
        int pagesize = 0;
        String datafile = null;
        try{
            pagesize = Integer.parseInt(args[1]);
            datafile = args[2];    
        }
        catch(Exception e){
            System.out.println("Invalid arguments. Please try again");
            System.exit(0);
        }
       
        CreateHeapFile(pagesize,datafile);
        long intEndTime = System.nanoTime();
        long intTotalTime = (intEndTime - intStartTime) / 1000000;
        System.out.println("Heap file creation completed in: " + intTotalTime + "ms");

    } catch (Exception e) {
       System.err.println(e.getMessage());
    }
   
 }

 
}