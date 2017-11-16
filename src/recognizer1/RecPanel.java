package recognizer1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by 122 on 11.02.2017.
 */
public class RecPanel extends JPanel {
    private JButton butLoad; //кнопка выбора изображения для распознавания
    private JFileChooser jfc;
    private ImageIcon img; //для отображения выбранного изображения
    private File imgFile;
    private JLabel jlbAns; //метка ответа нейросети
    private String answer; //строка для ответа по распознаванию нейросетью
    private JLabel jlb1; //для показа картинки открываемого изображения и надписи "Right answer"
    private JTextField tfAnsw; //текстовое поле для ввода правильного ответа
    private JButton butQT; //кнопка для быстрого самообучения
    private JButton butDef; //кнопка для сброса весов
    private RecognizerNum rn;

    public RecPanel() {
        setLayout(new FlowLayout(SwingConstants.CENTER));
        butLoad = new JButton("Load image");
        butLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jfc = new JFileChooser("D:\\Recognizer_Mk_I\\pics");
                int ret =jfc.showDialog(null, "Открыть изображение");

                if(ret == JFileChooser.APPROVE_OPTION) {
                    imgFile = jfc.getSelectedFile();
                    img = new ImageIcon(imgFile.getAbsolutePath());
                    jlb1.setIcon(img);
                    try {
                        rn.setInput(imgFile.getAbsolutePath());
                        for(int i = 0; i < rn.neurons.length; i++)
                            rn.neurons[i].setInput(rn.input);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    answer = rn.getAnswer();
                    jlbAns.setText("It's " + answer);
                    tfAnsw.setText("");
                }
            }
        });
        this.add(butLoad);

        String[] weightStr = new String[10];
        for(int i = 0; i < weightStr.length; i++)
            weightStr[i] = "weights//" + i + ".txt";

        try {
            rn = new RecognizerNum("pics//5.png", weightStr);
        } catch (IOException e) {
            System.out.println("Exception while read from files: " + e);
        }

        answer = rn.getAnswer();
        jlbAns = new JLabel("It's " + answer, SwingConstants.CENTER);
        this.add(jlbAns);

        img = new ImageIcon("pics//5.png");
        jlb1 = new JLabel("Right answer", img, SwingConstants.CENTER);
        this.add(jlb1);

        tfAnsw = new JTextField(5);
        tfAnsw.setActionCommand("tfAnswer");
        tfAnsw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rn.teachNeurons(answer, tfAnsw.getText());
            }
        });
        this.add(tfAnsw);

        butQT = new JButton("Quick teach");
        butQT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] inpTrain = new String[10]; //массив строк путей до файлов тренировочных изображений
                for(int i = 0; i < inpTrain.length; i++)
                    inpTrain[i] = "pics//" + String.valueOf(i) + ".png";

                rn.quickTeach(inpTrain);
                butQT.setEnabled(false); //убережем нейросеть от лишнего обучения
            }
        });
        this.add(butQT);

        butDef = new JButton("Default"); //сбрасываем веса к исходным нулевым
        butDef.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rn.setDefaultWeights();
                butQT.setEnabled(true); //а теперь можно по-быстрому обучить сеть
            }
        });
        this.add(butDef);

        setBorder(BorderFactory.createEmptyBorder(20, 75, 10, 75));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 200);
    }
}
