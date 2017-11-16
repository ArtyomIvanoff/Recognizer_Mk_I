package recognizer1;

/**
 * Created by 122 on 11.02.2017.
 */
public interface Neuronable {
    int getSum(); //sum the multiplications of input values and weights
    void increaseWeights(); //increase the weights by input values
    void decreaseWeights(); //decrease the weights by input values
}
