package com.sourcey.cowtech54;

/**
 * Created by Wily on 03/04/2018.
 */

public class JavaTest {

    public static void main(String[] args) {
        int bitmask = 0x0F;
        int val = 0x02;
        // prints "2"
        System.out.println("val & bitmask");
        System.out.println(val & bitmask);

        bitmask = 0x80;
        val = 0x20;
        // prints "2"
        System.out.println("0x80 | 0x20");
        System.out.println(val | bitmask);

        bitmask = 0x80;
        val = 0x40;
        // prints "2"
        System.out.println("0x80 | 0x40");
        System.out.println(val | bitmask);

    }

    public static void enabeler(){
        int[] enabler={0, 1,0,1,0,1,1,0,0}; //From Byte1 to Byte8

        for(int i : enabler){

        }

        if(enabler[1]==1){
            System.out.println("yVals1 = yVals1");
        }
        else{
            System.out.println("place empty LinkedList");
        }
    }
}


