package com.sourcey.cowtech54;





import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

    private JSONArray JsonArrayCompleto = new JSONArray();
    public JSONObject JsonCompleto = new JSONObject();
    //region Temporizador de Requests

    MainActivity mActivity= new MainActivity();
    private boolean contadorCorriendo = false;
    private int contadorLimite = 5;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {

            ///modificar valores y enviarlos al metodo desde aqui?
            contadorCorriendo = true;
            contadorLimite = contadorLimite - 1;
            if (contadorLimite > 0) {
                timerHandler.postDelayed(this, 1000);
            }
            else {
                contadorLimite = 5;
                contadorCorriendo = false;
                timerHandler.removeCallbacks(timerRunnable);
            }
        }
    };


    public void ActivarContador() {
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    //endregion



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
        int maxID = 11000;//2300;//  Number max of IDs possible
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



////////////////////////
// For Windows shortcut expand: ctrl + '+' (collapse: ctrl + '-') shortcut expand all: shift + ctrl + '+' (collapse all: shift + ctrl + '-')
////////////////////////


                //region REQUEST POST TO SERVER

                JSONObject JsonSingular = new JSONObject();

                try {
                    JsonSingular.put("IdCan",IDtram[1]);
                    JsonSingular.put("Length",IDtram[2]);
                    JsonSingular.put("B1",IDtram[3]);
                    JsonSingular.put("B2",IDtram[4]);
                    JsonSingular.put("B3",IDtram[5]);
                    JsonSingular.put("B4",IDtram[6]);
                    JsonSingular.put("B5",IDtram[7]);
                    JsonSingular.put("B6",IDtram[8]);
                    JsonSingular.put("B7",IDtram[9]);
                    JsonSingular.put("B8",IDtram[10]);
                    JsonSingular.put("IdCamion",1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonArrayCompleto.put(JsonSingular);



                if (!contadorCorriendo)
                {
                    ActivarContador();

                    try {
                        JsonCompleto.put("data" , JsonArrayCompleto);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    mActivity.MainActivityPOSTRequest(JsonCompleto);
                    JsonCompleto.remove("data");
                    JsonArrayCompleto = new JSONArray();


                }
                //endregion



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
                Number tP = 60.0;
                Number offset = 30.0; //Seconds to delete
                if((double)timePeriod>(double)tP){
                    while((double)timePeriod>(double)tP - (double)offset) {//Delete first Offset sec
                        //Log.d("MessageMannager: ", "Removing items");
                        MatIDs.get(currentIDNum.intValue()).removeFirstItemInLists();
                        timePeriod = MatIDs.get(currentIDNum.intValue()).getTimePeriod();
                    }
                }

                recIDs++;
            }else{
                if(messageSplitStr[i].length()>1)lostIDs++;
            }
        }
        //Delete first enter from message
        MessagePurged = MessagePurged.replaceFirst("\n","");
        //Coping message purged for future handle to avoid collision in above loop
        MessagePurgedCopy = MessagePurged;

    }

    //========


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
                try {
                    if (arr[i].length() > 0 && arr[i].length() < 20) {
                        //numArr[i] = Integer.parseInt(arr[i]);

                        numArr[i] = Double.parseDouble(arr[i]);
                    } else numArr[i] = 0;
                }catch(NumberFormatException e){
                    e.printStackTrace();
                    return new Number[3];
                }

            }
        }catch(NumberFormatException e){
            e.printStackTrace();
            return new Number[3];
        }
        return numArr;
    }


    //region Metodo armar JSONs



    //endregion

    /*==============================================================*/



}
