package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class App {

    public static void main(String[] args) throws IOException {

        String indexPath = args[0];
        InvertedIndex obj = new InvertedIndex(indexPath);
        obj.readIndex();
        HashMap<String, LinkedList<Integer>> invertedIndexMap = obj.constructInvertedIndex();
        //obj.printInvertedIndexMap(invertedIndexMap);

        String output = args[1];
        String input = args[2];

        QueryProcessor queryProcessor = new QueryProcessor(input, output, invertedIndexMap);
        queryProcessor.process();
        System.out.println("Find output in output.txt file");
    }

}
