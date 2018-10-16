package com.company;

import java.io.*;
import java.util.*;

public class QueryProcessor {

    PrintWriter outputFile;
    public String inputFileName;
    public String outputFileName;
    public HashMap<String, LinkedList<Integer>> invertedIndexMap;

    public QueryProcessor(String input, String output, HashMap<String,LinkedList<Integer>> map) {

        inputFileName = input;
        outputFileName = output;
        invertedIndexMap = map;

    }

    public void process() throws IOException {

        outputFile = new PrintWriter(outputFileName, "UTF-8");

        String workingDirectory = System.getProperty("user.dir");
        File inputFile = new File(workingDirectory, inputFileName);
        BufferedReader br = new BufferedReader(new FileReader(inputFile));

        ArrayList<String> listOfLines = new ArrayList<>();
        String line = null;
        while ( (line = br.readLine()) != null ) {
            listOfLines.add(line);
        }

        for (int i = 0; i < listOfLines.size(); i++) {

            getPostings(listOfLines.get(i));
//            taatAnd(listOfLines.get(i));
//            taatOr(listOfLines.get(i));
//            daatAnd(listOfLines.get(i));
//            daatOr(listOfLines.get(i));
        }

        outputFile.close();

    } // end of main()

    public void getPostings(String line) {

        String terms[] = line.split(" ");
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
