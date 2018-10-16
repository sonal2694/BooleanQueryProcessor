package com.company;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class InvertedIndex {

    public String INDEX_DIRECTORY;
    public final int NO_MORE_DOCS = 2147483647;
    public DirectoryReader directoryReader;
    public HashMap<String, LinkedList<Integer>> invertedIndexMap;

    public InvertedIndex (String path) {
        INDEX_DIRECTORY = path;
        invertedIndexMap = new HashMap<>();
    }

    public void readIndex() {

        Path path = Paths.get(INDEX_DIRECTORY);

        try {
            Directory directory = FSDirectory.open(path);
            directoryReader = DirectoryReader.open(directory);
            //System.out.println("Documents in index: " + directoryReader.numDocs());
        }
        catch (Exception e ){
            System.out.println("Exception occurred in readIndex");
            System.out.println(e);
        }
    }

    public HashMap<String, LinkedList<Integer>> constructInvertedIndex() {

        try {
            // fields contain id, text_en, text_es, text_fr
            Fields fields = MultiFields.getFields(directoryReader);

            for(String field: fields) {

                // if statement for finding terms of text_xx fields only
                if (!field.equals("id")) {

                    Terms terms = fields.terms(field);
                    TermsEnum termsEnum = terms.iterator();
                    PostingsEnum postingsEnum = null;

                    while(termsEnum.next() != null) {

                        postingsEnum = MultiFields.getTermDocsEnum(directoryReader, field, termsEnum.term());
                        String stringTerm = termsEnum.term().utf8ToString();
                        //System.out.println(stringTerm);
                        LinkedList<Integer> postingsList;

                        while(postingsEnum.nextDoc() != NO_MORE_DOCS) {

                            int docID = postingsEnum.docID();

                            if(invertedIndexMap.containsKey(stringTerm))
                                postingsList = invertedIndexMap.get(stringTerm);
                            else
                                postingsList = new LinkedList<>();

                            postingsList.add(docID);
                            Collections.sort(postingsList);
                            invertedIndexMap.put(stringTerm, postingsList);
                            //System.out.println(docID);

                        }
                    } // end of while
                } // end of if
            } // end of for

        }
        catch (Exception e ){
            System.out.println("Exception occurred in constructInvertedIndex");
            System.out.println(e);
        }

        return invertedIndexMap;
    }

    public void printInvertedIndexMap(HashMap<String,LinkedList<Integer>> invertedIndexMap) {

        for(String key : invertedIndexMap.keySet()) {
            LinkedList<Integer> value = invertedIndexMap.get(key);
            System.out.println(key + " : " + value);
        }
    }

    public HashMap<String, LinkedList<Integer>> getMap() {
        return this.invertedIndexMap;
    }
}
