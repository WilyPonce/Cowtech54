package com.sourcey.cowtech54;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Wily on 09/03/2018.
 */

/*public class MySingletonClass {

    private static MySingletonClass instance;

    public static MySingletonClass getInstance() {
        if (instance == null)
            instance = new MySingletonClass();
        return instance;
    }

    private MySingletonClass() {
    }

    private int intValue;

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}*/

public class BtMessageManager {

    public static String Message;
    public static Set<String> IDsListSet;
    public static ArrayList<String> IndivIDsArrayListStr;
    public static ArrayList<IDObject> MatIDs;
    public int lostIDs=0;
    public int recIDs=0;
    public static String MessagePurged = "";
    public static String MessagePurgedCopy = "";


    // -- Constructors
    public BtMessageManager(){ //Default constructor
        Message = "";
        IDsListSet = new HashSet<String>();
        IndivIDsArrayListStr = new ArrayList<String>();
        if(MatIDs == null){
            initMatIDs();
        }
    }
    public BtMessageManager(String strMessage){ //c1
        Message = strMessage;
        IDsListSet = new HashSet<String>();
        IndivIDsArrayListStr = new ArrayList<String>();
        if(MatIDs == null){
            initMatIDs();
        }

        updateAll();
    }

    // -- Methods
    public void initMatIDs(){
        int maxID = 2300;
        MatIDs = new ArrayList<IDObject>();
        for(int i = 0 ; i<maxID ; i++){
            MatIDs.add(new IDObject(i));
        }
    }

    public void addNewMessage(String msgStr) {
        Message=msgStr;
        updateAll();
    }

    //Getters


    public Set<String> getIDsListSet() {
        return IDsListSet;
    }

    public String getMessagePurged() {
        return MessagePurged;
    }

    public String getMessagePurgedCopy() {
        return MessagePurgedCopy;
    }

    public int getLostIDs() {
        return lostIDs;
    }

    public int getRecIDs() {
        return recIDs;
    }

    public void setIDsListSet(Set<String> IDsListSet) {
        this.IDsListSet = IDsListSet;
    }

    public ArrayList<String> getIndivIDsArrayListStr() {
        return IndivIDsArrayListStr;
    }

    public void setIndivIDsArrayListStr(ArrayList<String> indivIDsArrayListStr) {
        IndivIDsArrayListStr = indivIDsArrayListStr;
    }


    public void updateAll(){
        String[] messageSplitStr;
        messageSplitStr = Message.split("\n");
        MessagePurged = "";

        int mLength = messageSplitStr.length;
        for(int i = 0; i < mLength ; i++){
            //Check that contains [ & ]
            if( isACompleteID(messageSplitStr[i]) ){
                //Creating clean message with only good IDs and with out [ ]
                MessagePurged += "\n" + messageSplitStr[i];
                MessagePurged = MessagePurged.replaceAll("\\[" ,"");
                MessagePurged = MessagePurged.replaceAll("]","");


                //Splitting the message
                Number[] IDtram = Splitter2Num(messageSplitStr[i]);

                // [1] is the CAN ID positi
                Number currentIDNum = IDtram[1];
                String currentIDStr = IDtram[1].toString();

                //Add the ID to the Set List
                IDsListSet.add( currentIDStr );
                //Add Tram to the ID in mat
                MatIDs.get(currentIDNum.intValue()).addTram(IDtram);

                //Delete
                Number timePeriod = MatIDs.get(currentIDNum.intValue()).getTimePeriod();

                //Log.d("MessageMannager: ", "Time period " + timePeriod);
                Number tP = 180.0;
                Number offset = 60.0; //Seconds to delete
                if((double)timePeriod>(double)tP){
                    while((double)timePeriod>(double)tP - (double)offset) {//Delete first Offset sec
                        //Log.d("MessageMannager: ", "Removing items");
                        MatIDs.get(currentIDNum.intValue()).removeFirstItemInLists();
                        timePeriod = MatIDs.get(currentIDNum.intValue()).getTimePeriod();
                    }
                }

                recIDs++;
            }else{
                if(messageSplitStr[i].length()>1)   lostIDs++;
            }
        }
        //Delete first enter from message
        MessagePurged = MessagePurged.replaceFirst("\n","");
        //Coping message purged for future handle to avoid collision in above loop
        MessagePurgedCopy = MessagePurged;

    }

    //========



    public static void main(String []args){
        System.out.println("Hello World1");

        String packetIds1 = "[1\t33\t2\t3\t4\t5\t6\t7\t8\t99\t100]\n" +
                "[2\t76\t2\t3\t4\t5\t6\t7\t8\t98\t101]\n" +
                "[3\t33\t2\t3\t4\t5\t6\t7\t8\t97\t102]\n" +
                "[4\t155\t2\t3\t4\t5\t6\t7\t8\t96\t103]\n" +
                "[5\t33\t2\t3\t4\t5\t6\t7\t8\t95\t104]\n" +
                "[6\t155\t2\t3\t4\t5\t6\t7\t8\t94\t105]\n" +
                "[7\t111\t2\t3\t4\t5\t6\t7\t8\t93\t106]\n" +
                "[8\t76\t2\t3\t4\t5\t6\t7\t8\t92\t107]\n" +
                "[9\t33\t2\t3\t4\t5\t6\t7\t8\t91\t108]\n" +
                "[10\t33\t2\t3\t4\t5\t6\t7\t80\t90\t109]\n" +
                "[11\t155\t2\t3\t4\t5\t6\t7\t81\t92\t1010]\n" +
                "[12\t76\t2\t3\t4\t5\t6\t7\t82\t91\t10]\n";

        String packetIds2 = "\t5\t6\t7\t8\t9\t10]\n" +
                "[2\t76\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[3\t33\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[4\t88\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[5\t99\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[6\t101\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[7\t2222\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[8\t33\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[9\t133\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[10\t76\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[11\t33\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[12\t76\t2\t3\t4";

        //runMet();

        BtMessageManager btMessage = new BtMessageManager(packetIds1);

        btMessage.addNewMessage(packetIds2);

        System.out.println("ID List Set:");
        System.out.println( IDsListSet );

        System.out.println("ID 33 Time:");
        System.out.println( MatIDs.get(33).getTime() );

        System.out.println("ID 155 Time:");
        System.out.println( MatIDs.get(155).getTime() );

        System.out.println("ID 76 Time:");
        System.out.println( MatIDs.get(76).getTime() );




    }



    public void testIDObject(){
        ArrayList<IDObject> ArrayIDs = new ArrayList<IDObject>();
        for(int i = 0 ; i<6 ; i++){
            ArrayIDs.add(new IDObject(i));
        }
        Number[] IDnum1 = {2033.0, 33.0, 130.0, 3.0, 144.0, 155.0, 1.0, 75.0, 76.0, 10, 11};
        Number[] IDnum2 = {2034.0, 33.0, 133.0, 6.0, 144.0, 155.0, 1.0, 75.0, 76.0, 10, 11};
        Number[] IDnum3 = {2035.0, 33.0, 135.0, 9.0, 144.0, 155.0, 1.0, 75.0, 76.0, 10, 11};
        Number[] IDnum4 = {2036.0, 33.0, 137.0, 12.0, 144.0, 155.0, 1.0, 75.0, 76.0, 10, 11};

        ArrayIDs.get(1).addTram(IDnum1);
        ArrayIDs.get(1).addTram(IDnum2);
        ArrayIDs.get(1).addTram(IDnum3);
        ArrayIDs.get(1).addTram(IDnum4);

        System.out.println("ID 1 Time:");
        System.out.println( ArrayIDs.get(1).getTime() );
        System.out.println("ID 1 CanID:");
        System.out.println( ArrayIDs.get(1).getCanID() );
        System.out.println("ID 1 Len:");
        System.out.println( ArrayIDs.get(1).getLen() );
        System.out.println("ID 1 Byte 1:");
        System.out.println( ArrayIDs.get(1).getByte1() );
        System.out.println("ID 1 Byte 2:");
        System.out.println( ArrayIDs.get(1).getByte2() );
        System.out.println("ID 1 Byte 3:");
        System.out.println( ArrayIDs.get(1).getByte3() );
        System.out.println("ID 1 Byte 4:");
        System.out.println( ArrayIDs.get(1).getByte4() );
        System.out.println("ID 1 Byte 5:");
        System.out.println( ArrayIDs.get(1).getByte5() );
        System.out.println("ID 1 Byte 6:");
        System.out.println( ArrayIDs.get(1).getByte6() );
        System.out.println("ID 1 Byte 7:");
        System.out.println( ArrayIDs.get(1).getByte7() );
        System.out.println("ID 1 Byte 8:");
        System.out.println( ArrayIDs.get(1).getByte8() );

        System.out.println("ID 1 Time:");
        System.out.println( ArrayIDs.get(2).getTime() );

        ArrayIDs.get(1).removeFirstItemInLists();
        System.out.println("ID 1 Time:");
        System.out.println( ArrayIDs.get(1).getTime() );
        System.out.println("ID 1 CanID:");
        System.out.println( ArrayIDs.get(1).getCanID() );
        System.out.println("ID 1 Len:");
        System.out.println( ArrayIDs.get(1).getLen() );
        System.out.println("ID 1 Byte 1:");
        System.out.println( ArrayIDs.get(1).getByte1() );
        System.out.println("ID 1 Byte 2:");
        System.out.println( ArrayIDs.get(1).getByte2() );
        System.out.println("ID 1 Byte 3:");
        System.out.println( ArrayIDs.get(1).getByte3() );
        System.out.println("ID 1 Byte 4:");
        System.out.println( ArrayIDs.get(1).getByte4() );
        System.out.println("ID 1 Byte 5:");
        System.out.println( ArrayIDs.get(1).getByte5() );
        System.out.println("ID 1 Byte 6:");
        System.out.println( ArrayIDs.get(1).getByte6() );
        System.out.println("ID 1 Byte 7:");
        System.out.println( ArrayIDs.get(1).getByte7() );
        System.out.println("ID 1 Byte 8:");
        System.out.println( ArrayIDs.get(1).getByte8() );
    }
    public void runMet(){
        //String packetIds = "1,2,3;3,5,6;7,8,9";
        String packetIds1 = "[1\t33\t2\t3\t4\t5\t6\t7\t8\t99\t100]\n" +
                "[2\t76\t2\t3\t4\t5\t6\t7\t8\t98\t101]\n" +
                "[3\t33\t2\t3\t4\t5\t6\t7\t8\t97\t102]\n" +
                "[4\t88\t2\t3\t4\t5\t6\t7\t8\t96\t103]\n" +
                "[5\t33\t2\t3\t4\t5\t6\t7\t8\t95\t104]\n" +
                "[6\t101\t2\t3\t4\t5\t6\t7\t8\t94\t105]\n" +
                "[7\t111\t2\t3\t4\t5\t6\t7\t8\t93\t106]\n" +
                "[8\t122\t2\t3\t4\t5\t6\t7\t8\t92\t107]\n" +
                "[9\t33\t2\t3\t4\t5\t6\t7\t8\t91\t108]\n" +
                "[10\t144\t2\t3\t4\t5\t6\t7\t80\t90\t109]\n" +
                "[11\t155\t2\t3\t4\t5\t6\t7\t81\t92\t1010]\n" +
                "[12\t76\t2\t3\t4\t5\t6\t7\t82\t91\t10]\n";
        Set<Number> SetIDs = new HashSet<Number>();

        String packetIds2 = "\t5\t6\t7\t8\t9\t10]\n" +
                "[2\t76\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[3\t77\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[4\t88\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[5\t99\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[6\t101\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[7\t111\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[8\t122\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[9\t133\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[10\t144\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[11\t155\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n" +
                "[12\t76\t2\t3\t4";
        String packetIds3 = "[1\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n[2\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10]\n[3\t1\t2\t3\t4\t5\t6";
        String[] strArr1 = packetIds1.split("\n");
        String[] strArr2 = packetIds2.split("\n");
        String[] strArr3 = packetIds3                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                  .split("\n");

        for(int i = 0; i < strArr1.length; i++){
            // [1] is the CAN ID position
            Number IDtram = Splitter2Num(strArr1[i])[1];
            //System.out.println( IDtram );
            SetIDs.add(IDtram);
        }



        TreeSet sortedSet = new TreeSet<Number>(SetIDs);
        System.out.println("The sorted list is:");
        System.out.println(sortedSet);

        System.out.println("The First element of the set is: "+ (Number)sortedSet.first());
        System.out.println("The last element of the set is: "+ (Number)sortedSet.last());

        Set<Number> Set2 = new HashSet<>();
        Set2.add(1.0);
        Set2.add(2.0);
        Set2.add(33.0);
        Set2.add(75.0);
        Set2.add(76.0);

        System.out.println("The Set 1 is:");
        System.out.println(SetIDs);
        System.out.println("The Set 2 is:");
        System.out.println(Set2);
        System.out.println("The Set 1 + 2 is:");
        System.out.println(addTwoSets(sortedSet,Set2));

        System.out.println("--------------------");




        System.out.println(getIndivIDs2Str(packetIds1));
        System.out.println(getIndivIDs2Str(packetIds2));


        System.out.println("--------------------");

        /*
        ArrayList[] IDsMat = new ArrayList[2500];

        IDsMat[3] = new ArrayList<Number>();
        IDsMat[3].add("HI");
        System.out.println(IDsMat[3].get(0));*/

        ArrayList<Number[] >[] IDsMat = (ArrayList<Number[] >[])new ArrayList[2500];
        IDsMat[33] = new ArrayList<Number[] >();
        IDsMat[76] = new ArrayList<Number[] >();

        Number[] testID = {2033.0, 33.0, 133.0, 2.0, 144.0, 155.0, 1.0, 75.0, 76.0};
        IDsMat[33].add(testID);

        testID = new Number[]{3033.0, 33.0, 133.0, 2.0, 144.0, 155.0, 1.0, 75.0, 76.0};
        IDsMat[33].add(testID);

        testID = new Number[]{2076.0, 76.0, 133.0, 2.0, 144.0, 155.0, 1.0, 75.0, 76.0};
        IDsMat[76].add(testID);

        testID = new Number[]{3076.0, 76.0, 133.0, 2.0, 144.0, 155.0, 1.0, 75.0, 76.0};
        IDsMat[76].add(testID);

        System.out.println("ID 33 in IDsMat[33]:");
        System.out.println(IDsMat[33].get(1)[0]);

        System.out.println("ID 76 in IDsMat[33]:");
        System.out.println(IDsMat[76].get(0));

        System.out.println("--------------------");

        ArrayOfArrayOfNumbers();


        System.out.println("------------ OBJECT TEST --------");
        BtMessageManager MatMessage = new BtMessageManager( packetIds1);

    }

    public static void ArrayOfArrayOfNumbers(){
        LinkedList<LinkedList<Number>[]>[] IDsMat2 = (LinkedList<LinkedList<Number>[] >[])new LinkedList[2500];
        //IDsMat2[33] = new LinkedList<LinkedList<Number[] >>();
        for(int i = 1; i<2500; i++){
            IDsMat2[i] = new LinkedList<LinkedList<Number>[] >();
        }


        //if(IDsMat2[33].get(0).length<1){
        LinkedList< Number>[] IDsubByte = (LinkedList<Number >[])new LinkedList[11];
        for(int i = 0; i<11; i++){
            IDsubByte[i] = new LinkedList<Number>();
        }

        Number[] testID2 = {2033.0, 33.0, 203.0, 2.0, 144.0, 155.0, 1.0, 75.0, 76.0, 10.0, 11.0 };
        IDsubByte[0].add(testID2[0]);
        IDsubByte[1].add(testID2[1]);
        IDsubByte[2].add(testID2[2]);
        IDsubByte[3].add(testID2[3]);
        IDsubByte[4].add(testID2[4]);
        IDsubByte[5].add(testID2[5]);
        IDsubByte[6].add(testID2[6]);
        IDsubByte[7].add(testID2[7]);
        IDsubByte[8].add(testID2[8]);
        IDsubByte[9].add(testID2[9]);
        IDsubByte[10].add(testID2[10]);


        Number idTest = 33;


        IDsMat2[idTest.intValue()].add(IDsubByte);

        testID2 = new Number[]{3033.0, 33.0, 303.0, 2.0, 144.0, 155.0, 1.0, 75.0, 76.0, 10.0, 11.0 };
        IDsubByte[0].add(testID2[0]);
        IDsubByte[1].add(testID2[1]);
        IDsubByte[2].add(testID2[2]);
        IDsubByte[3].add(testID2[3]);
        IDsubByte[4].add(testID2[4]);
        IDsubByte[5].add(testID2[5]);
        IDsubByte[6].add(testID2[6]);
        IDsubByte[7].add(testID2[7]);
        IDsubByte[8].add(testID2[8]);
        IDsubByte[9].add(testID2[9]);
        IDsubByte[10].add(testID2[10]);

        IDsMat2[33].add(IDsubByte);

        System.out.println("ID 33 in IDsMat2[33]: 0 0");
        System.out.println(IDsMat2[33].get(0)[0]);

        System.out.println("ID 33 in IDsMat2[33]: 0 2");
        System.out.println(IDsMat2[33].get(0)[2]);

        System.out.println("ID 33 in IDsMat2[33]: 1 0");
        System.out.println(IDsMat2[33].get(1)[0]);

        System.out.println("ID 33 in IDsMat2[33]: 1 2");
        System.out.println(IDsMat2[33].get(1)[2]);

        System.out.println("ID 33 in IDsMat2[33]: (1) [2] (1)");
        Number lolxd = IDsMat2[33].get(0)[2].get(1);
        System.out.println(lolxd);
    }

    public static ArrayList<String> getIndivIDs2Str(String StrMsg){
        String[] ArrOfIDs= StrMsg.split("\n");
        ArrayList<String> ArrayListOfIDs = new ArrayList<String>();

        int counter=0;
        for(int i = 0; i < ArrOfIDs.length; i++){

                if(isACompleteID(ArrOfIDs[i])){
                    ArrayListOfIDs.add( ArrOfIDs[i]);
                }
        }

        return ArrayListOfIDs;
    }



    public static Set<Number> getIDsListSet(String StrMsg){
        Set<Number> SetIDs = new HashSet<Number>();

        String[] ArrOfIDs= StrMsg.split("\n");
        for(int i = 0; i < ArrOfIDs.length; i++){
            // [1] is the CAN ID position
            Number IDtram = Splitter2Num(ArrOfIDs[i])[1];
            //System.out.println( IDtram );
            SetIDs.add(IDtram);
        }
        return SetIDs;
    }

    public static Set<Number> addTwoSets(Set<Number> one, Set<Number> two) {
        Set<Number> newSet = new HashSet<Number>(one);
        newSet.addAll(two);
        return newSet;
    }



    private static Boolean isACompleteID(String StrMsg){


        if( StrMsg.contains("[") && StrMsg.contains("]")
                && StrMsg.length()<100) {

            return true;
        }
        else //System.out.println("Message Incomplete: No beginning or ending brackets [ ]");
            return false;


    }

    /**
     * Split the message ID into individual numbers
     * @param str Array of Strings (IDs) given in a message
     */
    public static Number[] Splitter2Num(String str){
        Number[] NumArr={0,0,0,0,0,0,0,0,0,0,0};

        if(isACompleteID(str)){
            str=str.replace("[", "");
            str=str.replace("]", "");
            NumArr = StrSplitNum(str);
            return NumArr;
        }
        else return null;

    }

    /**
     * Converts a String of numbers into an Number Array
     * @param StrVar String with only numbers separated by TABs
     * @return Number[]
     */
    public static Number[] StrSplitNum(String StrVar){


        String[] arr = StrVar.split("\t");
        Number[] numArr = new Number[arr.length];
        try {
            for (int i = 0; i < numArr.length; i++) {
                //numArr[i] = Integer.parseInt(arr[i]);
                if (arr[i].length() > 0 && arr[i].length() < 20) {
                    //numArr[i] = Integer.parseInt(arr[i]);

                    numArr[i] = Double.parseDouble(arr[i]);
                } else numArr[i] = 0;

            }
        }catch(NumberFormatException e){
            e.printStackTrace();
            return new Number[arr.length];
        }
        return numArr;
    }

    /*==============================================================*/



}
