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
    int printCount = 0;

    public QueryProcessor(String input, String output, HashMap<String,LinkedList<Integer>> map) {

        inputFileName = input;
        outputFileName = output;
        invertedIndexMap = map;

    }

    public void process() throws IOException {

        outputFile = new PrintWriter(outputFileName, "UTF-8");
        BufferedReader br = new BufferedReader(new FileReader(inputFileName));

        ArrayList<String> listOfLines = new ArrayList<>();
        String line = null;
        while ( (line = br.readLine()) != null ) {
            listOfLines.add(line);
        }

        for (int i = 0; i < listOfLines.size(); i++) {

            printCount++;
            getPostings(listOfLines.get(i));
            taatAnd(listOfLines.get(i));
            taatOr(listOfLines.get(i));
            daatAnd(listOfLines.get(i));
            daatOr(listOfLines.get(i));
        }

        outputFile.close();

    } // end of process()


    public void getPostings(String line) {

        String terms[] = line.split(" ");
        LinkedList<Integer> postingsList = new LinkedList<>();

        if(printCount != 1)
            outputFile.println();

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

        if(resultPostingsList.size() == 0)
            outputFile.println("empty");
        else
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

        if(resultPostingsList.size() == 0)
            outputFile.println("empty");
        else
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

        LinkedList<Integer> resultPostingsList = intersectDaat(postingsListArray);

        if(resultPostingsList.size() == 0)
            outputFile.println("empty");
        else
            outputFile.println(listToString(resultPostingsList));

        outputFile.println("Number of documents in the result: " + resultPostingsList.size());
        outputFile.println("Number of comparisons: " + noOfComparisons);

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

        if(resultPostingsList.size() == 0)
            outputFile.println("empty");
        else
            outputFile.println(listToString(resultPostingsList));

        outputFile.println("Number of documents in the result: " + resultPostingsList.size());
        outputFile.print("Number of comparisons: " + noOfComparisons);

    }


    // INTERSECT AND UNION FUNCTIONS
    // STARTS HERE

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
            else if ( p1.getFirst() < p2.getFirst() ) {
                if ( hasSkip(p1) && !skip(p1).isEmpty() && (skip(p1).getFirst() <= p2.getFirst()) ) {
                    while (hasSkip(p1) && !skip(p1).isEmpty() && (skip(p1).getFirst() <= p2.getFirst())) {
                        p1 = skip(p1);
                    }
                }
                else
                    p1.removeFirst();
            }
            else {

                if ( hasSkip(p2) && !skip(p2).isEmpty() && (skip(p2).getFirst() <= p1.getFirst()) ) {
                    while (hasSkip(p2) && !skip(p2).isEmpty() && (skip(p2).getFirst() <= p1.getFirst())) {
                        p2 = skip(p2);
                    }
                }
                else
                    p2.removeFirst();
            }
            noOfComparisons ++;
        }

        return result;
    }


    public boolean hasSkip(LinkedList<Integer> p) {

        if(p.size() <= 2)
            return false;

        boolean result = false;
        int sizeP = p.size();
        int noOfSkips = (int)Math.sqrt(sizeP);
        int skipRange = sizeP/noOfSkips;

        if ( p.get(skipRange-1) != null)
            return true;

        return result;
    }


    public LinkedList<Integer> skip(LinkedList<Integer> origP) {

        LinkedList<Integer> p = new LinkedList<>(origP);

        if(p.size() <= 2)
            return p;
        int sizeP = p.size();
        int noOfSkips = (int)Math.sqrt(sizeP);
        int skipRange = sizeP/noOfSkips;

        if ( p.get(skipRange-1) != null) {
            p.subList(0, skipRange).clear();
        }

        return p;

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


    public LinkedList<Integer> intersectDaat(ArrayList<LinkedList<Integer>> postingsListArray) {

        noOfComparisons = 0;
        LinkedList<Integer> resultPostingsList = new LinkedList<>();
        HashSet<Integer> hashSet = new HashSet<>();
        int originalSize = postingsListArray.size();

        while ( postingsListArray.size() == originalSize ) {
            for(int i = 0; i < postingsListArray.size(); i++ ) {
                LinkedList<Integer> l = postingsListArray.get(i);
                if( !l.isEmpty())
                    hashSet.add(l.get(0));
                else
                    return resultPostingsList;
            }

            if ( hashSet.size() == 1 ) {

                resultPostingsList.add(hashSet.iterator().next());
                for ( int i =0; i < postingsListArray.size(); i++) {
                    LinkedList<Integer> l = postingsListArray.get(i);
                    l.removeFirst();

                    if (l.isEmpty())
                        return resultPostingsList;
                }
            }

            else {
                //finding maximum of all first values
                int maxDocID = Integer.MIN_VALUE;

                for ( int i = 0; i < postingsListArray.size(); i++) {

                    LinkedList<Integer> l = postingsListArray.get(i);
                    if(!l.isEmpty()) {
                        if(l.get(0) > maxDocID) {
                            maxDocID = l.get(0);
                            noOfComparisons ++;
                        }

                    }
                    else
                        System.out.println("List " + i + "is empty. Cannot get max doc id.");
                } // end for

                // removing all elements less than max doc id
                for( int i = 0; i < postingsListArray.size(); i++) {
                    LinkedList<Integer> l = postingsListArray.get(i);

                    if(!l.isEmpty()) {
                        if(l.get(0) < maxDocID) {
                            noOfComparisons ++;
                            l.removeFirst();
                        }
                    }
                    else
                        System.out.println("List " + i + "is empty. Cannot remove element.");
                }

            }

            postingsListArray.removeIf(p -> p.isEmpty());
            hashSet.clear();

        }
        return resultPostingsList;
    }


    public LinkedList<Integer> unionDaat(ArrayList<LinkedList<Integer>> postingsListArray) {

        noOfComparisons = 0;
        LinkedList<Integer> resultPostingsList = new LinkedList<>();

        while (!postingsListArray.isEmpty()) {

            int minDocId = Integer.MAX_VALUE;
            for(int i = 0; i < postingsListArray.size(); i++) {
                LinkedList<Integer> l = postingsListArray.get(i);
                if ( l.get(0) < minDocId ) {
                    minDocId = l.get(0);
                    noOfComparisons ++;
                }
            }

            for(int i = 0; i < postingsListArray.size(); i++) {
                LinkedList<Integer> l = postingsListArray.get(i);
                if(l.get(0) == minDocId)
                    l.removeFirst();
            }
            resultPostingsList.add(minDocId);
            postingsListArray.removeIf(p -> p.isEmpty());

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