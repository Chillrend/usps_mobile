package org.chillrend.brieftrager.sapot;

public class KeyValPair {
    public String valuez;
    public int key;

    public KeyValPair(String value, int tag){
        valuez = value;
        key = tag;
    }

    @Override
    public String toString(){
        return valuez;
    }
}
