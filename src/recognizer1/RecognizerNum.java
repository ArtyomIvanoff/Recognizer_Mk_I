package recognizer1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by 122 on 11.02.2017.
 */
public class RecognizerNum {
    NeuronNum[] neurons;
    int[][] input;

    public RecognizerNum() {
        int k = 10; //десять нейронов для каждой цифры
        neurons = new NeuronNum[k];

        for(int i = 0; i < neurons.length; i++) {
            neurons[i] = new NeuronNum();
            neurons[i].setName(String.valueOf(i));
        }
    }

    public RecognizerNum(String[] pathesW) throws IOException {
        this();

        int[][] wgh;
        for(int i = 0; i < neurons.length; i++) {
            wgh = readMtxFrom(pathesW[i]);
            neurons[i].setWeights(wgh);
        }
    }

    public RecognizerNum(String pathInp, String[] pathesW) throws IOException {
        this(pathesW);

        try {
            setInput(pathInp);
        } catch (Exception e) {
            System.out.println("Exception while reading input from file, " + e);
            throw new IOException(e);
        }

        for(int i = 0; i < neurons.length; i++) {
            neurons[i].setInput(this.input);
        }
    }

    public void setInput(String inpPath) throws Exception{
        BufferedImage bi = null;

        try {
            bi = ImageIO.read(new File(inpPath));
        } catch (Exception e) {
            System.out.println("Exception while reading image from file!");
            throw new Exception();
        }

        Color c;
        input = new int[bi.getHeight()][];
        for (int i = 0; i < input.length; i++)
            input[i] = new int[bi.getWidth()];

        for(int i = 0; i < input.length; i++) {
            for(int j = 0; j < input[i].length; j++) {
                try{
                    c = new Color(bi.getRGB(j, i));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Array of bound " + j + " " + i);
                    c = Color.white;
                }

                if(c.getRed() < 10 && c.getGreen() < 10 && c.getBlue() < 10)
                    input[i][j] = 1; //черный цвет
                else
                    input[i][j] = 0; //другие цвета
            }
        }
    }

    public int[][] readMtxFrom(String fullpath) throws IOException {
        String str;
        StringTokenizer st;
        int cap = 2; //начальная емкость
        ArrayList<ArrayList<Integer>> tmpMtx = new ArrayList<>(cap);

        FileReader fr = new FileReader(fullpath);
        try(BufferedReader br = new BufferedReader(fr)){
            int i = 0;
            while((str = br.readLine()) != null) {
               tmpMtx.add(i, new ArrayList<Integer>());
               st = new StringTokenizer(str);
               int j = 0;
               while(st.hasMoreTokens()){
                  Integer elem = Integer.parseInt(st.nextToken());
                  tmpMtx.get(i).add(j, elem);
                  j++;
               }
               i++;
            }
        }

        Integer[][] mtx = new Integer[tmpMtx.size()][tmpMtx.get(0).size()];
        for(int i = 0; i < mtx.length; i++)
            mtx[i] = tmpMtx.get(i).toArray(mtx[i]);

        int[][] mtxRes = new int[mtx.length][];
        for(int i = 0; i < mtxRes.length; i++)
            mtxRes[i] = new int[mtx[i].length];

        for(int i = 0; i < mtxRes.length; i++)
            for(int j = 0; j < mtxRes[i].length; j++)
                mtxRes[i][j] = mtx[i][j];

        return mtxRes;
    }

    public void writeMtx(int[][] mtx, String fullpath) throws IOException {
        FileWriter fw = new FileWriter(fullpath);
        String str = "";
        try(BufferedWriter bw = new BufferedWriter(fw)){
            for(int i = 0; i < mtx.length; i++){
                for(int j = 0; j < mtx[i].length; j++) {
                    str += mtx[i][j]+" ";
                }
                bw.write(str);
                bw.newLine();
                str = "";
            }
        }
    }

    public String getAnswer(){
        int max = neurons[0].getThreshold();
        int sum;
        String answer = "nothing";

        for(int i = 0; i < neurons.length; i++){
            sum = neurons[i].getSum();
            if(sum > max) {
                max = sum;
                answer = neurons[i].getName();
            }
        }

        return answer;
    }

    public void teachNeurons(String wrong, String correct) {
        for(int i = 0; i < neurons.length; i++) {
            if(neurons[i].getName().equals(correct)) //повысим веса для правильного нейрона
                neurons[i].increaseWeights();
            if(neurons[i].getName().equals(wrong)) //понизим веса для неправильного нейрона
                neurons[i].decreaseWeights();
        }

        //запишем обновлённые веса
        for(int i = 0; i < neurons.length; i++) {
            String weightStr = "weights//" + i + ".txt";
            try {
                this.writeMtx(neurons[i].getWeights(), weightStr);
            } catch (IOException e) {
               System.out.println("Exception while writing to files: " + e);
            }
        }
    }

    //алгоритм последовательного обучения по известным образацам
    public void quickTeach(String[] inputStr) {
        int[][][] inpCollection = new int[inputStr.length][][];
        for(int i = 0; i < inputStr.length; i++){
            try {
                setInput(inputStr[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            inpCollection[i] = this.input;
        }

        int maxEpoch = 20; // I think that it's enough
        for(int i = 0; i < maxEpoch; i++) {
            for(int j = 0; j < inputStr.length; j++) {
                for(int k = 0; k < neurons.length; k++) {
                    neurons[k].setInput(inpCollection[j]);
                }
                this.teachNeurons(this.getAnswer(), String.valueOf(j));
            }
        }
    }

    //сбросить веса всех нейронов
    public void setDefaultWeights() {
        int[][] dflt = new int[5][3];
        for(int i = 0; i < neurons.length; i++) {
            String weightStr = "weights//" + i + ".txt";
            try {
                this.writeMtx(dflt, weightStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
            neurons[i].setWeights(dflt);
        }
    }
}
