package recognizer1;

import java.util.Arrays;

/**
 * Created by 122 on 11.02.2017.
 */
public class NeuronNum implements Neuronable {
    private int threshold = 15; //пороговое значение
    private int[][] weights; //матрица весов для каждого пикселя
    private int[][] input;//матрица исходной информации(содержит значения пикселей входной картинки)
    private int sum; //сумма произведений вход.сигналов и весов
    private String name;//имя данного нейрона цифры

    public NeuronNum() {}

    public NeuronNum(int[][] wgArr, int[][] inArr, String nm) {
        setWeights(wgArr);
        setInput(inArr);
        setName(nm);
    }

    public int getThreshold() { return  threshold; }

    public void setThreshold(int newT) throws IndexOutOfBoundsException{
        if(newT <= 0)
            throw new IndexOutOfBoundsException();

        threshold = newT;
    }

    public int[][] getWeights() { return weights; }

    public void setWeights(int[][] wgArr) throws NullPointerException {
        if(wgArr == null)
            throw new NullPointerException();

        int w = wgArr.length;
        int h = wgArr[0].length;

        weights = new int[w][h];
        for(int i = 0; i < w; i++)
            weights[i] = Arrays.copyOf(wgArr[i], h);
    }

    public int[][] getInput() { return  input; }

    public void setInput(int[][] inArr) throws NullPointerException {
        if(inArr == null)
            throw new NullPointerException();

        int w = inArr.length;
        int h = inArr[0].length;

        input = new int[w][h];
        for(int i = 0; i < w; i++)
            input[i] = Arrays.copyOf(inArr[i], h);
    }

    public String getName() { return  name; }

    public void setName(String nameNew) throws NullPointerException {
        if(nameNew == null)
            throw new NullPointerException();

        name = nameNew;
    }

    @Override
    public int getSum() {
        sum = 0;
        for(int i = 0; i < input.length; i++)
            for(int j = 0; j < input[i].length; j++)
                sum += input[i][j]*weights[i][j];

        return sum;
    }

    @Override
    public void increaseWeights() {
        for(int i = 0; i < weights.length; i++)
            for(int j = 0; j < weights[i].length; j++)
                weights[i][j] += input[i][j];
    }

    @Override
    public void decreaseWeights() {
        for(int i = 0; i < weights.length; i++)
            for(int j = 0; j < weights[i].length; j++)
                weights[i][j] -= input[i][j];
    }
}
