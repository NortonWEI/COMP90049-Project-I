import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class NGramDistance {
    private int N = 2;

    private NGramDistance(int N) {
        this.N = N;
    }

    private int getDistance(String s0, String s1) {
        int joint = 0;
        ArrayList<String> listS0 = new ArrayList<>();
        ArrayList<String> listS1 = new ArrayList<>();

        if (s0 == null || s1 == null) {
            throw new NullPointerException("The strings to be compared should not be null!");
        }

        s0 = "#" + s0 + "#";
        s1 = "#" + s1 + "#";

        int lengthS0 = s0.length();
        int lengthS1 = s1.length();

        if (lengthS0 < N) {
            for (int i=0; i<N-lengthS0; i++) {
                s0 += "#";
            }
        }

        if (lengthS1 < N) {
            for (int i=0; i<N-lengthS1; i++) {
                s1 += "#";
            }
        }

        lengthS0 = s0.length();
        lengthS1 = s1.length();

        for (int i=0; i < lengthS0-N+1; i++) {
            listS0.add(s0.substring(i, i+N));
        }

        for (int i=0; i < lengthS1-N+1; i++) {
            listS1.add(s1.substring(i, i+N));
        }

        int lengthListS0 = listS0.size();
        int lengthListS1 = listS1.size();

        for (int i=0; i<listS0.size(); i++) {
            for (int j=0; j<listS1.size(); j++) {
                if (listS0.get(i).equals(listS1.get(j))) {
                    joint++;
                    listS0.remove(i);
                    listS1.remove(j);
                    i--;
                    break;
                }
            }
        }

        return lengthListS0 + lengthListS1 - 2 * joint;
    }

    private String getDictWord(NGramDistance distance, String mistakeWord, ArrayList<String> listDict) {
        BidiMap<String, Integer> dicMap = new DualHashBidiMap<>();

        for (String aListDict : listDict) {
            int v = distance.getDistance(mistakeWord, aListDict);
            dicMap.put(aListDict, v);
        }

        int min = Collections.min(dicMap.values());
        String result = dicMap.getKey(min);

        return result;
    }

    private ArrayList<String> getDictWords(NGramDistance distance, String mistakeWord, ArrayList<String> listDict) {
        HashMap<String, Integer> dicMap = new HashMap<>();
        ArrayList<String> resultList = new ArrayList<>();

        for (String aListDict : listDict) {
            int v = distance.getDistance(mistakeWord, aListDict);
            dicMap.put(aListDict, v);
        }

        int min = Collections.min(dicMap.values());

        for(String result: dicMap.keySet()){
            if(dicMap.get(result).equals(min)) {
                resultList.add(result);
            }
        }

        return resultList;
    }

    public static void main(String args[]) throws IOException {
        NGramDistance distance = new NGramDistance(2);

        ArrayList<String> listMistake = new ArrayList<>();
        ArrayList<String> listDict = new ArrayList<>();
        ArrayList<ArrayList<String>> listResult = new ArrayList<>();
        ArrayList<String> listCorrect = new ArrayList<>();

        int firstMatch = 0;
        int allMatch = 0;

        int listResultTotalSize = 0;

        Scanner mistakeScanner = new Scanner(new File("./2018S2-90049P1-data/wiki_misspell.txt"), "UTF-8");
        while (mistakeScanner.hasNextLine()) {
            listMistake.add(mistakeScanner.nextLine());
        }

        Scanner correctScanner = new Scanner(new File("./2018S2-90049P1-data/wiki_correct.txt"), "UTF-8");
        while (correctScanner.hasNextLine()) {
            listCorrect.add(correctScanner.nextLine());
        }

        Scanner dictScanner = new Scanner(new File("./2018S2-90049P1-data/dict.txt"), "UTF-8");
        while (dictScanner.hasNextLine()) {
            listDict.add(dictScanner.nextLine());
        }

        for (int i=0; i<listMistake.size(); i++) {
            ArrayList<String> listDictWord = new ArrayList<>();
            listDictWord = distance.getDictWords(distance, listMistake.get(i), listDict);
            System.out.println(i);
            System.out.println("Mistake Word: " + listMistake.get(i));
            for (int j=0; j<listDictWord.size(); j++) {
                System.out.println("Dictionary Word: " + listDictWord.get(j));
            }
            System.out.println();
            listResult.add(listDictWord);
        }

        System.out.println("Algorithm finished!!!");

        for (int i=0; i<listResult.size(); i++) {
            if (listResult.get(i).get(0).equals(listCorrect.get(i))) {
                firstMatch++;
            }
        }

        for (int i=0; i<listResult.size(); i++) {
            for (int j=0; j<listResult.get(i).size(); j++) {
                listResultTotalSize++;
                if (listResult.get(i).get(j).equals(listCorrect.get(i))) {
                    allMatch++;
                }
            }
        }

        System.out.println("First Match Number: " + firstMatch);
        System.out.println("All Match Number: " + allMatch);
        System.out.println("All Possible Result Number: " + listResultTotalSize);

        float accuracy = (float) firstMatch/listResult.size();
        System.out.println("Accuracy: " + accuracy);

        float precision = (float) allMatch/listResultTotalSize;
        System.out.println("Precision: " + precision);

        float recall = (float) allMatch/listResult.size();
        System.out.println("Recall: " + recall);
    }

}
