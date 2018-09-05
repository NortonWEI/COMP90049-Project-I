import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class GlobalEditDistance {
    private int insert, delete, replace = -1;
    private int match = 1;

    public GlobalEditDistance(int match, int insert, int delete, int replace) {
        this.match = match;
        this.insert = insert;
        this.delete = delete;
        this.replace = replace;
    }

    private int getDistance(String s0, String s1) {

        if (s0 == null || s1 == null) {
            throw new NullPointerException("The strings to be compared should not be null!");
        }

        int lengthS0 = s0.length();
        int lengthS1 = s1.length();

        int[][] table = new int[lengthS1+1][lengthS0+1];

        for (int i=1; i<=lengthS1; i++) {
            for (int j=1; j<=lengthS0; j++) {
                table[i][j] = Math.min(Math.min(table[i][j-1] + delete,
                        table[i-1][j] + insert),
                        table[i-1][j-1] + checkEqual(s0.substring(j-1, j), s1.substring(i-1, i)));
            }
        }

        return table[lengthS1][lengthS0];
    }

    private int checkEqual(String char0, String char1) {
        if (char0.equals(char1)) {
            return match;
        } else {
            return replace;
        }
    }

    private String getDictWord(GlobalEditDistance distance, String mistakeWord, ArrayList<String> listDict) {
        BidiMap<String, Integer> dicMap = new DualHashBidiMap<>();

        for (String aListDict : listDict) {
            int v = distance.getDistance(mistakeWord, aListDict);
            dicMap.put(aListDict, v);
        }

        int max = Collections.max(dicMap.values());
        String result = dicMap.getKey(max);

        return result;
    }

    private ArrayList<String> getDictWords(GlobalEditDistance distance, String mistakeWord, ArrayList<String> listDict) {
        HashMap<String, Integer> dicMap = new HashMap<>();
        ArrayList<String> resultList = new ArrayList<>();

        for (String aListDict : listDict) {
            int v = distance.getDistance(mistakeWord, aListDict);
            dicMap.put(aListDict, v);
        }

        int min = Collections.min(dicMap.values());
        System.out.println("Distance: " + min);

        for(String result: dicMap.keySet()){
            if(dicMap.get(result).equals(min)) {
                resultList.add(result);
            }
        }

        return resultList;
    }

    public static void main(String args[]) throws FileNotFoundException {
        GlobalEditDistance distance = new GlobalEditDistance(0, 1, 1, 1);

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

        System.out.println("Algorithm finish!!!");

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
