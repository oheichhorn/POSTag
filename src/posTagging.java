import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

public class posTagging {

    public static void main(String args[]) {
        String[] trigrams = new String[200];
        String[] bigrams = new String[200];
        String[] unigrams = new String[200];
        String[] starts = new String[200];
        String[][] wordList = new String[200][2];
        Word[] wordTracker = new Word[200];
        int triLength = 0;
        int biLength = 0;
        int uniLength = 0;
        int startLength = 0;
        int wordListLength = 0;
        int wordTrackLength = 0;

        try {
            File trainData = new File("src/trainData");
            Scanner myReader = new Scanner(trainData);
            while (myReader.hasNextLine()) {
                String wordData = myReader.nextLine();
                String[] words = wordData.split(" ");
                String tagData = myReader.nextLine();
                String[] tags = tagData.split(" ");

                for (int i = 0; i < words.length; i++) {
                    boolean inList = false;
                    for (int j = 0; j < wordListLength; j++) {
                        if (wordList[j][0].equals(words[i]) && wordList[j][1].equals(tags[i])) {
                            inList = true;
                            wordTracker[j].incrementCount();
                        }
                    }
                    if (!inList) {
                        wordList[wordListLength][0] = words[i];
                        wordList[wordListLength][1] = tags[i];
                        wordListLength++;
                        wordTracker[wordTrackLength] = new Word(words[i], tags[i]);
                        wordTrackLength++;
                    }
                }
                starts[startLength] = tags[0];
                startLength++;
                for (int i = 0; i < tags.length - 2; i++) {
                    String trigram = tags[i];
                    trigram += tags[i + 1];
                    trigram += tags[i + 2];
                    trigrams[triLength] = trigram;
                    triLength++;
                }
                for (int i = 0; i < tags.length - 1; i++) {
                    String bigram = tags[i];
                    bigram += tags[i + 1];
                    bigrams[biLength] = bigram;
                    biLength++;
                }
                for (int i = 0; i < tags.length; i++) {
                    String unigram = tags[i];
                    unigrams[uniLength] = unigram;
                    uniLength++;
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        Hashtable<String, String> startProbs = new Hashtable<String, String>();
        Hashtable<String, String> bigramProbs = new Hashtable<String, String>();
        Hashtable<String, String> trigramProbs = new Hashtable<String, String>();

        for (int i = 0; i < startLength; i++) {
            int posCount = 0;
            float posProb;
            if (!(startProbs.containsKey(starts[i]))) {
                String pos = starts[i];
                for (int j = 0; j < startLength; j++) {
                    if (starts[j].equals(pos)) {
                        posCount++;
                    }
                }
                posProb = ((float) posCount) / ((float) startLength);
                String posProbS = String.valueOf(posProb);
                startProbs.put(pos, posProbS);
            }
        }
        for (int i = 0; i < biLength; i++) {
            int biCount = 0;
            int uniCount = 0;
            float biProb;
            if (!(bigramProbs.containsKey(bigrams[i]))) {
                String bi = bigrams[i];
                char uni = bigrams[i].charAt(0);
                for (int j = 0; j < biLength; j++) {
                    if (bigrams[j].equals(bi)) {
                        biCount++;
                    }
                    if (bigrams[j].charAt(0) == uni) {
                        uniCount++;
                    }
                }
                biProb = ((float) biCount) / ((float) uniCount);
                String biProbS = String.valueOf(biProb);
                bigramProbs.put(bi, biProbS);
            }
        }
        for (int i = 0; i < triLength; i++) {
            int triCount = 0;
            int biCount = 0;
            float triProb;
            if (!(trigramProbs.containsKey(trigrams[i]))) {
                String tri = trigrams[i];
                String bi = String.valueOf(trigrams[i].charAt(0)) + (trigrams[i].charAt(1));
                for (int j = 0; j < triLength; j++) {
                    String newBi = String.valueOf(trigrams[j].charAt(0)) + (trigrams[j].charAt(1));
                    if (trigrams[j].equals(tri)) {
                        triCount++;
                    }
                    if (newBi.equals(bi)) {
                        biCount++;
                    }
                    triProb = ((float) triCount) / ((float) biCount);
                    String triProbS = String.valueOf(triProb);
                    trigramProbs.put(tri, triProbS);
                }
            }
        }
        System.out.println(startProbs);
        System.out.println(bigramProbs);
        System.out.println(trigramProbs);

        Hashtable<String, String> nProbs = new Hashtable<String, String>();
        Hashtable<String, String> aProbs = new Hashtable<String, String>();
        Hashtable<String, String> vProbs = new Hashtable<String, String>();
        Hashtable<String, String> dProbs = new Hashtable<String, String>();

        int nCount = 0;
        int aCount = 0;
        int vCount = 0;
        int dCount = 0;
        for (int i = 0; i < uniLength; i++) {
            if (unigrams[i].equals("N")) {
                nCount++;
            }
            if (unigrams[i].equals("V")) {
                vCount++;
            }
            if (unigrams[i].equals("A")) {
                aCount++;
            }
            if (unigrams[i].equals("D")) {
                dCount++;
            }
        }
        for (int i = 0; i < wordTrackLength; i++) {
            String word = wordTracker[i].getWordName();
            int wordCount = wordTracker[i].getWordCount();
            String pos = wordTracker[i].getPos();
            float wordProb;
            if (pos.equals("N")) {
                wordProb = ((float) wordCount) / ((float) nCount);
                String wordProbS = String.valueOf(wordProb);
                nProbs.put(word, wordProbS);
            } else if (pos.equals("V")) {
                wordProb = ((float) wordCount) / ((float) vCount);
                String wordProbS = String.valueOf(wordProb);
                vProbs.put(word, wordProbS);
            } else if (pos.equals("A")) {
                wordProb = ((float) wordCount) / ((float) aCount);
                String wordProbS = String.valueOf(wordProb);
                aProbs.put(word, wordProbS);
            } else if (pos.equals("D")) {
                wordProb = ((float) wordCount) / ((float) dCount);
                String wordProbS = String.valueOf(wordProb);
                dProbs.put(word, wordProbS);
            }
        }
        System.out.println(nProbs);
        System.out.println(vProbs);
        System.out.println(aProbs);
        System.out.println(dProbs);

        String[] answers = new String[20];
        String[] sentences = new String[20];
        int sentenceCount = 0;

        try {
            File testData = new File("src/testData");
            Scanner myReader = new Scanner(testData);
            while (myReader.hasNextLine()) {
                String sentence = myReader.nextLine();
                sentences[sentenceCount] = sentence;
                String answer = myReader.nextLine();
                answers[sentenceCount] = answer;
                sentenceCount++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            new FileWriter("src/trigramOut", false).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new FileWriter("src/bigramOut", false).close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < sentenceCount; i++) {
            String workingSentence = sentences[i];
            String[] words = workingSentence.split(" ");
            String curWord;

            Branch[] possibilities = new Branch[200];
            Branch[] prevPossibilities = new Branch[200];
            int possibilityCount = 0;
            int prevPossibilityCount = 0;

            for (int j = 0; j < words.length; j++) {
                float wordProbN = 0.0F, wordProbV = 0.0F, wordProbA = 0.0F, wordProbD = 0.0F;
                float gramProbN = 0.0F, gramProbV = 0.0F, gramProbA = 0.0F, gramProbD = 0.0F;
                curWord = words[j];
                if (nProbs.containsKey(curWord)) {
                    wordProbN = Float.parseFloat(nProbs.get(curWord));
                }
                if (vProbs.containsKey(curWord)) {
                    wordProbV = Float.parseFloat(vProbs.get(curWord));
                }
                if (aProbs.containsKey(curWord)) {
                    wordProbA = Float.parseFloat(aProbs.get(curWord));
                }
                if (dProbs.containsKey(curWord)) {
                    wordProbD = Float.parseFloat(dProbs.get(curWord));
                }
                if(j == 0){
                    if(wordProbN > 0){
                        gramProbN = Float.parseFloat(startProbs.get("N"));
                        Branch b = new Branch(gramProbN*wordProbN, "N");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    if(wordProbV > 0){
                        gramProbV = Float.parseFloat(startProbs.get("V"));
                        Branch b = new Branch(gramProbV*wordProbV, "V");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    if(wordProbA > 0){
                        gramProbA = Float.parseFloat(startProbs.get("A"));
                        Branch b = new Branch(gramProbA*wordProbA, "A");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    if(wordProbD > 0){
                        gramProbD = Float.parseFloat(startProbs.get("D"));
                        Branch b = new Branch(gramProbD*wordProbD, "D");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    prevPossibilityCount = possibilityCount;
                    prevPossibilities = possibilities;
                    possibilities = new Branch[200];
                    possibilityCount = 0;
                }
                else if(j == 1){
                    if(wordProbN > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = b.getPosTrain() +  "N";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbN = Float.parseFloat(bigramProbs.get(b.getPosTrain() + "N"));
                                b.addStep(gramProbN * wordProbN, "N");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbV > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = new Branch(prevPossibilities[k]);
                            String curPosTrain = b.getPosTrain() +  "V";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbV = Float.parseFloat(bigramProbs.get(b.getPosTrain() + "V"));
                                b.addStep(gramProbV * wordProbV, "V");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbA > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = b.getPosTrain() +  "A";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbA = Float.parseFloat(bigramProbs.get(b.getPosTrain() + "A"));
                                b.addStep(gramProbA * wordProbA, "A");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbD > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = b.getPosTrain() +  "D";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbD = Float.parseFloat(bigramProbs.get(b.getPosTrain() + "D"));
                                b.addStep(gramProbD * wordProbD, "D");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    prevPossibilityCount = possibilityCount;
                    prevPossibilities = possibilities;
                    possibilities = new Branch[200];
                    possibilityCount = 0;
                }
                else{
                    if(wordProbN > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-2) + b.getPosTrain().charAt(b.getPosTrain().length()-1) + "N";
                            if(trigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbN = Float.parseFloat(trigramProbs.get(curPosTrain));
                                b.addStep(gramProbN * wordProbN, "N");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbV > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = new Branch(prevPossibilities[k]);
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-2) + b.getPosTrain().charAt(b.getPosTrain().length()-1) + "V";
                            if(trigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbV = Float.parseFloat(trigramProbs.get(curPosTrain));
                                b.addStep(gramProbV * wordProbV, "V");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbA > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-2) + b.getPosTrain().charAt(b.getPosTrain().length()-1) + "A";
                            if(trigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbA = Float.parseFloat(trigramProbs.get(curPosTrain));
                                b.addStep(gramProbA * wordProbA, "A");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbD > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-2) + b.getPosTrain().charAt(b.getPosTrain().length()-1) + "D";
                            if(trigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbD = Float.parseFloat(trigramProbs.get(curPosTrain));
                                b.addStep(gramProbD * wordProbD, "D");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    prevPossibilityCount = possibilityCount;
                    prevPossibilities = possibilities;
                    possibilities = new Branch[200];
                    possibilityCount = 0;
                }
            }
            int maxPos = 0;
            for(int j = 0; j < prevPossibilityCount; j++){
                if(prevPossibilities[j].getProb() > prevPossibilities[maxPos].getProb()){
                    maxPos = j;
                }
            }
            String output = new String();
            for(int j = 0; j < prevPossibilities[maxPos].getPosTrain().length(); j++) {
                output = output + prevPossibilities[maxPos].getPosTrain().charAt(j) + " ";
            }
            try {
                FileWriter fw = new FileWriter("src/trigramOut", true);
                fw.append(output + "\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < sentenceCount; i++) {
            String workingSentence = sentences[i];
            String[] words = workingSentence.split(" ");
            String curWord;

            Branch[] possibilities = new Branch[200];
            Branch[] prevPossibilities = new Branch[200];
            int possibilityCount = 0;
            int prevPossibilityCount = 0;

            for (int j = 0; j < words.length; j++) {
                float wordProbN = 0.0F, wordProbV = 0.0F, wordProbA = 0.0F, wordProbD = 0.0F;
                float gramProbN = 0.0F, gramProbV = 0.0F, gramProbA = 0.0F, gramProbD = 0.0F;
                curWord = words[j];
                if (nProbs.containsKey(curWord)) {
                    wordProbN = Float.parseFloat(nProbs.get(curWord));
                }
                if (vProbs.containsKey(curWord)) {
                    wordProbV = Float.parseFloat(vProbs.get(curWord));
                }
                if (aProbs.containsKey(curWord)) {
                    wordProbA = Float.parseFloat(aProbs.get(curWord));
                }
                if (dProbs.containsKey(curWord)) {
                    wordProbD = Float.parseFloat(dProbs.get(curWord));
                }
                if(j == 0){
                    if(wordProbN > 0){
                        gramProbN = Float.parseFloat(startProbs.get("N"));
                        Branch b = new Branch(gramProbN*wordProbN, "N");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    if(wordProbV > 0){
                        gramProbV = Float.parseFloat(startProbs.get("V"));
                        Branch b = new Branch(gramProbV*wordProbV, "V");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    if(wordProbA > 0){
                        gramProbA = Float.parseFloat(startProbs.get("A"));
                        Branch b = new Branch(gramProbA*wordProbA, "A");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    if(wordProbD > 0){
                        gramProbD = Float.parseFloat(startProbs.get("D"));
                        Branch b = new Branch(gramProbD*wordProbD, "D");
                        possibilities[possibilityCount] = b;
                        possibilityCount++;
                    }
                    prevPossibilityCount = possibilityCount;
                    prevPossibilities = possibilities;
                    possibilities = new Branch[200];
                    possibilityCount = 0;
                }
                else {
                    if(wordProbN > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-1) +  "N";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbN = Float.parseFloat(bigramProbs.get(curPosTrain));
                                b.addStep(gramProbN * wordProbN, "N");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbV > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = new Branch(prevPossibilities[k]);
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-1) +  "V";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbV = Float.parseFloat(bigramProbs.get(curPosTrain));
                                b.addStep(gramProbV * wordProbV, "V");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbA > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-1) +  "A";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbA = Float.parseFloat(bigramProbs.get(curPosTrain));
                                b.addStep(gramProbA * wordProbA, "A");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    if(wordProbD > 0){
                        for(int k = 0; k < prevPossibilityCount; k++){
                            Branch b = prevPossibilities[k];
                            String curPosTrain = "" + b.getPosTrain().charAt(b.getPosTrain().length()-1) +  "D";
                            if(bigramProbs.containsKey(curPosTrain)) {
                                Branch bOld = new Branch(b);
                                gramProbD = Float.parseFloat(bigramProbs.get(curPosTrain));
                                b.addStep(gramProbD * wordProbD, "D");
                                possibilities[possibilityCount] = b;
                                possibilityCount++;
                                prevPossibilities[k] = bOld;
                            }
                        }
                    }
                    prevPossibilityCount = possibilityCount;
                    prevPossibilities = possibilities;
                    possibilities = new Branch[200];
                    possibilityCount = 0;
                }
            }
            int maxPos = 0;
            for(int j = 0; j < prevPossibilityCount; j++){
                if(prevPossibilities[j].getProb() > prevPossibilities[maxPos].getProb()){
                    maxPos = j;
                }
            }
            String output = new String();
            for(int j = 0; j < prevPossibilities[maxPos].getPosTrain().length(); j++) {
                output = output + prevPossibilities[maxPos].getPosTrain().charAt(j) + " ";
            }
            try {
                FileWriter fw = new FileWriter("src/bigramOut", true);
                fw.append(output + "\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}