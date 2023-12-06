package br.com.mvbos.lgj.base;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class TocarSom {

    public void play(String path) {
        try {
            File arquivoSom = new File(path);

            if (arquivoSom.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(arquivoSom);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.start();
            } else {
                System.out.println("no path");
            }
        } catch (Exception e){
            System.out.println(e);
        }
    }


}
