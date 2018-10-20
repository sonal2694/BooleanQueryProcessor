package com.company;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class QueryProcessor {

    PrintWriter outputFile;
    public String inputFileName;
    public String outputFileName;
    public HashMap<String, LinkedList<Integer>> invertedIndexMap;
    int noOfComparisons;

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
            taatAnd(listOfLines.get(i));
            taatOr(listOfLines.get(i));
            daatAnd(listOfLines.get(i));
            daatOr(listOfLines.get(i));
        }

        outputFile.close();

    } // end of main()


    public void getPostings(String line) {

        String terms[] = line.split(" ");
        LinkedList<Integer> postingsList = new LinkedList<>();

        for(int i = 0; i <terms.length; i++) {
            outputFile.println("GetPostings");
            outputFile.println(terms[i]);
            outputFile.print("Postings list: ");

            if( invertedIndexMap.containsKey(terms[i]) )
                postingsList = invertedIndexMap.get(terms[i]);

            if( !postingsList.isEmpty() )
                outputFile.println(listToString(postingsList));
            else
                outputFile.println("empty");
        }

    }

    // TAAT AND implementation
    public void taatAnd(String line) {

        outputFile.println("TaatAnd");
        outputFile.println(line);
        outputFile.print("Results: ");

        String terms[] = line.split(" ");
        int noOfTerms = terms.length;
        ArrayList<LinkedList<Integer>> postingsListArray = new ArrayList<>();

        for (int i = 0; i < noOfTerms; i++) {
            if ( invertedIndexMap.containsKey(terms[i]) ) {
                postingsListArray.add(invertedIndexMap.get(terms[i]));
            }
        }
        // if terms don't exist in dictionary
        if (postingsListArray.size() == 0) {
            outputFile.println("empty");
            outputFile.println("Number of documents in the result: " + 0);
            outputFile.println("Number of comparisons: " + 0);
            return;
        }

        // finding the postingsList of the smallest length
        LinkedList<Integer> currentPostingsList, smallestPostingsList, resultPostingsList;
        int minSize = postingsListArray.get(0).size();
        int positionOfSmallestList = 0;
        smallestPostingsList = postingsListArray.get(0);

        for (int i = 1; i < postingsListArray.size(); i++) {
            currentPostingsList = postingsListArray.get(i);
            if(postingsListArray.get(i).size() < minSize) {
                minSize = currentPostingsList.size();
                positionOfSmallestList = i;
                smallestPostingsList = currentPostingsList;
            }
        }

        postingsListArray.remove(positionOfSmallestList);
        resultPostingsList = smallestPostingsList;

        noOfComparisons = 0;
        while(postingsListArray.size() != 0) {
            resultPostingsList = intersectTaat(resultPostingsList, postingsListArray.get(0));
            postingsListArray.remove(0);
        }

        if(resultPostingsList.size() == 0) {
            outputFile.println("empty");
            return;
        }
        outputFile.println(listToString(resultPostingsList));
        outputFile.println("Number of documents in the result: " + resultPostingsList.size());
        outputFile.println("Number of comparisons: " + noOfComparisons);
    }

    // TAAT OR implementation
    public void taatOr(String line) {

        outputFile.println("TaatOr");
        outputFile.println(line);
        outputFile.print("Results: ");

        String terms[] = line.split(" ");
        int noOfTerms = terms.length;
        ArrayList<LinkedList<Integer>> postingsListArray = new ArrayList<>();

        for (int i = 0; i < noOfTerms; i++) {
            if ( invertedIndexMap.containsKey(terms[i]) ) {
                postingsListArray.add(invertedIndexMap.get(terms[i]));
            }
        }
        // if terms don't exist in dictionary
        if (postingsListArray.size() == 0) {
            outputFile.println("empty");
            outputFile.println("Number of documents in the result: " + 0);
            outputFile.println("Number of comparisons: " + 0);
            return;
        }

        LinkedList<Integer> resultPostingsList = postingsListArray.get(0);
        postingsListArray.remove(0);
        noOfComparisons = 0;

        while(postingsListArray.size() != 0) {
            resultPostingsList = unionTaat(resultPostingsList, postingsListArray.get(0));
            postingsListArray.remove(0);
        }

        if(resultPostingsList.size() == 0) {
            outputFile.println("empty");
            return;
        }
        outputFile.println(listToString(resultPostingsList));
        outputFile.println("Number of documents in the result: " + resultPostingsList.size());
        outputFile.println("Number of comparisons: " + noOfComparisons);

    }


    // DAAT AND implementation
    public void daatAnd(String line) {
        outputFile.println("DaatAnd");
        outputFile.println(line);
        outputFile.print("Results: ");

        String terms[] = line.split(" ");
        int noOfTerms = terms.length;
        ArrayList<LinkedList<Integer>> postingsListArray = new ArrayList<>();

        for (int i = 0; i < noOfTerms; i++) {
            if ( invertedIndexMap.containsKey(terms[i]) ) {
                LinkedList<Integer> individualList = new LinkedList<>(invertedIndexMap.get(terms[i]));
                postingsListArray.add(individualList);
            }
        }

        // if terms don't exist in dictionary
        if (postingsListArray.size() == 0) {
            outputFile.println("empty");
            outputFile.println("Number of documents in the result: " + 0);
            outputFile.println("Number of comparisons: " + 0);
            return;
        }

    }

    // DAAT OR implementation
    public void daatOr(String line) {

        outputFile.println("DaatOr");
        outputFile.println(line);
        outputFile.print("Results: ");

        String terms[] = line.split(" ");
        int noOfTerms = terms.length;
        ArrayList<LinkedList<Integer>> postingsListArray = new ArrayList<>();

        for (int i = 0; i < noOfTerms; i++) {
            if ( invertedIndexMap.containsKey(terms[i]) ) {
                LinkedList<Integer> individualList = new LinkedList<>(invertedIndexMap.get(terms[i]));
                postingsListArray.add(individualList);
            }
        }

        // if terms don't exist in dictionary
        if (postingsListArray.size() == 0) {
            outputFile.println("empty");
            outputFile.println("Number of documents in the result: " + 0);
            outputFile.println("Number of comparisons: " + 0);
            return;
        }

        LinkedList<Integer> resultPostingsList = unionDaat(postingsListArray);

        if(resultPostingsList.size() == 0) {
            outputFile.println("empty");
            return;
        }
        outputFile.println(listToString(resultPostingsList));
        outputFile.println("Number of documents in the result: " + resultPostingsList.size());
        outputFile.println("Number of comparisons: " + noOfComparisons);

    }


    public LinkedList<Integer> intersectTaat(LinkedList<Integer> origP1, LinkedList<Integer> origP2) {

        LinkedList<Integer> result = new LinkedList<>();
        LinkedList<Integer> p1 = new LinkedList<>(origP1);
        LinkedList<Integer> p2 = new LinkedList<>(origP2);

        while( (p1.size() != 0) && (p2.size() != 0) ) {

            if (p1.getFirst().equals(p2.getFirst())) {
                result.add(p1.getFirst());
                p1.removeFirst();
                p2.removeFirst();
            }
            else if (p1.getFirst() < p2.getFirst())
                p1.removeFirst();
            else
                p2.removeFirst();
            noOfComparisons ++;
        }

        return result;
    }

    public LinkedList<Integer> unionTaat(LinkedList<Integer> origP1, LinkedList<Integer> origP2) {

        LinkedList<Integer> result = new LinkedList<>();
        LinkedList<Integer> p1 = new LinkedList<>(origP1);
        LinkedList<Integer> p2 = new LinkedList<>(origP2);

        while( (p1.size() != 0) && (p2.size() != 0)) {
            if (p1.getFirst().equals(p2.getFirst())) {
                result.add(p1.getFirst());
                p1.removeFirst();
                p2.removeFirst();
            }
            else if (p1.getFirst() < p2.getFirst()) {
                result.add(p1.getFirst());
                p1.removeFirst();
            }
            else {
                result.add(p2.getFirst());
                p2.removeFirst();
            }
            noOfComparisons ++;
        }

        if(p1.size() != 0)
            result.addAll(p1);
        else if(p2.size() != 0)
            result.addAll(p2);

        return result;
    }


    public LinkedList<Integer> unionDaat(ArrayList<LinkedList<Integer>> postingsListArray) {

        noOfComparisons = 0;
        LinkedList<Integer> resultPostingsList = new LinkedList<>();
        int postingsListNumber = 0;

        while (!postingsListArray.isEmpty()) {

            int minDocId = Integer.MAX_VALUE;
            for(int i = 0; i < postingsListArray.size(); i++) {

                LinkedList<Integer> l = postingsListArray.get(i);
                if ( l.get(0) < minDocId ) {
                    minDocId = l.get(0);
                    postingsListNumber = i;
                    noOfComparisons ++;
                }
            }

            // if not already in result
//            if( !resultPostingsList.contains(minDocId))
//                resultPostingsList.add(minDocId);
            for(int i = 0; i < postingsListArray.size(); i++) {
                LinkedList<Integer> l = postingsListArray.get(i);
                if(l.get(0) == minDocId)
                    l.removeFirst();
            }
            resultPostingsList.add(minDocId);
            //postingsListArray.get(postingsListNumber).removeFirst();

            if (postingsListArray.get(postingsListNumber).size() == 0) {
                postingsListArray.remove(postingsListNumber);
            }
        }
        return resultPostingsList;
    }


    public String listToString(LinkedList<Integer> list) {

        String postingsListString = "";
        int i;
        for (i = 0; i < list.size()-1; i++) {
            postingsListString = postingsListString + list.get(i).toString() + " ";
        }
        postingsListString = postingsListString + list.get(i).toString();

        return postingsListString;
    }

}