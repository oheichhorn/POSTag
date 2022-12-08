public class Branch {

    private float prob;
    private String posTrain;

    public Branch(float p, String pos) {
        this.prob = p;
        this.posTrain = pos;
    }

    public Branch(Branch b) {
        this.prob = b.getProb();
        this.posTrain = b.getPosTrain();
    }

    public float getProb() {
        return prob;
    }

    public String getPosTrain() {
        return posTrain;
    }

    public void setPosTrain(String posTrain) {
        this.posTrain = posTrain;
    }

    public void addStep(float p, String pos){
        prob = prob * p;
        posTrain = posTrain + pos;
    }
}
