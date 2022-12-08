public class Word {

    private String wordName;
    private int wordCount;
    private String pos;

    public Word(String wordName, String pos) {
        this.wordName = wordName;
        this.pos = pos;
        this.wordCount = 1;
    }

    public void incrementCount(){
        this.wordCount++;
    }

    public int getWordCount() {
        return wordCount;
    }

    public String getWordName() {
        return wordName;
    }

    public String getPos() {
        return pos;
    }
}
