package com.company;

import java.io.*;
import java.util.*;

public class QueryProcessor {

    PrintWriter outputFile;

    public static void main(String[] args) throws IOException {

        String filename = "input.txt";
        String workingDirectory = System.getProperty("user.dir");
        File inputFile = new File(workingDirectory, filename);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        ArrayList<String> listOfLines = new ArrayList<>();
        String line = null;
        while ( (line = br.readLine()) != null ) {
            listOfLines.add(line);
        }

        for (int i = 0; i < listOfLines.size(); i++) {

            QueryProcessor obj = new QueryProcessor();
            obj.getPostings(listOfLines.get(i));
//            obj.taatOr(listOfLines.get(i));
//            obj.taatOr(listOfLines.get(i));
//            obj.daatAnd(listOfLines.get(i));
//            obj.daatOr(listOfLines.get(i));
        }

//        outputFile.close();

    } // end of main()

    public void getPostings(String line) {

        try {
            outputFile = new PrintWriter("output.txt", "UTF-8");
        }
        catch (Exception e) {
            System.out.println("Error occurred in getPostings()");
            System.out.println(e);
        }

        String terms[] = line.split(" ");
        InvertedIndex invertedIndex = new InvertedIndex();
        HashMap<String, LinkedList<Integer>> invertedIndexMap = invertedIndex.invertedIndexMap;

        System.out.println(invertedIndexMap);

        LinkedList<Integer> postingsList = null;

        for(int i = 0; i <terms.length; i++) {
            outputFile.println("GetPostings");
            outputFile.println(terms[i]);
            outputFile.print("PostingsList: ");

            if( invertedIndexMap.containsKey(terms[i]) )
                postingsList = invertedIndexMap.get(terms[i]);

            if( postingsList != null )
                outputFile.println(postingsList);
            else
                outputFile.println("empty");

            System.out.println(postingsList);
        }

    }

}
