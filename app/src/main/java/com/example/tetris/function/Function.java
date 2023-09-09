package com.example.tetris.function;

import android.util.Log;

import java.util.ArrayList;
import java.util.Base64;

public class Function {
    public static String listToString(ArrayList<byte[]>list){
        // 将ArrayList<byte[]>转换为Base64编码的String
        StringBuilder stringBuilder = new StringBuilder();
        for (byte[] byteArray : list) {
            String base64EncodedString = Base64.getEncoder().encodeToString(byteArray);
            stringBuilder.append(base64EncodedString).append(",");
        }

        // 移除最后的逗号
        if (stringBuilder.length() > 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }

        // 将StringBuilder转换为String
        String encodedArrayListString = stringBuilder.toString();

        Log.d("decode", "listToString: "+encodedArrayListString);
        return encodedArrayListString;
    }
    public static ArrayList<byte[]> StringToList(String str){
        ArrayList<byte[]> decodedArrayList = new ArrayList<>();
        String[] encodedStringArray = str.split(",");
        for (String encodedString : encodedStringArray) {
            byte[] decodedByteArray = Base64.getDecoder().decode(encodedString);
            decodedArrayList.add(decodedByteArray);
        }
        Log.d("decode", "listToString: "+decodedArrayList);
        return decodedArrayList;
    }
}
