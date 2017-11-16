package recognizer1;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 122 on 11.02.2017.
 */
public class Main {
    static void createAndShowGUI(){
        JFrame frame1 = new JFrame("Recognizer Mark I");
        frame1.setLayout(new FlowLayout());
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(300, 200);
        RecPanel panel1 = new RecPanel();
        frame1.add(panel1);

        frame1.pack();
        frame1.setVisible(true);
        frame1.setResizable(true);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
