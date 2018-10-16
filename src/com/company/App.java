package com.company;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class App {

    public static void main(String[] args) throws IOException {

        String path = "/Users/sonalsingh/index";
        InvertedIndex obj = new InvertedIndex(path);
        obj.readIndex();
        HashMap<String, LinkedList<Integer>> invertedIndexMap = obj.constructInvertedIndex();
        //obj.printInvertedIndexMap(invertedIndexMap);

        String input = "input.txt";
        String output = "output.txt";

        QueryProcessor queryProcessor = new QueryProcessor(input, output, invertedIndexMap);
        queryProcessor.process();
    }

}
