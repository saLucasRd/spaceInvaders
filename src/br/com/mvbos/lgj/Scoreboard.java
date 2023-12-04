package br.com.mvbos.lgj;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class Scoreboard {
    static JFrame frame = new JFrame();
    JTable tabela = new JTable();
    JLabel label = new JLabel("TOP 10");







    Scoreboard() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,800);
        //frame.setLayout();
        frame.setVisible(false);
        frame.setTitle("TOP 10 SCORE");



    }


    public static void showScore() {
        frame.setVisible(true);
    }



}
