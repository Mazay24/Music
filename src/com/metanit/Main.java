package com.metanit;

import javax.sound.sampled.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.DoubleBinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class Main{

    public static void main(String[] args) throws IOException, InterruptedException {
        Download download = new Download();
        Picture picture = new Picture(download);
        Music music = new Music(download);
        new Thread(picture).start();
        new Thread(music).start();

        Thread.sleep(15000);
        download.goPlay();
    }
}

class Download extends Thread{
    public static final String IN_FILE_TXT = "inFile.txt";
    public static final String OUT_FILE_TXT = "outFile.txt";
    public static final String PATH_TO_MUSIC = "D:/Java/Money/music/music";
    public static final String RESULT = "pictureandmusic.txt";

    public static final String OUT_FILE_PICTURE = "outPicture.txt";
    public static final String PATH_TO_PCTURE = "D:/Java/Money//picture/image";
    public static int pictureCounter;
    BufferedWriter resultAll = new BufferedWriter(new FileWriter(RESULT));

    Download() throws IOException {
    }

    public static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream()); // Создает байтовый канал для чтения сайта
        FileOutputStream stream = new FileOutputStream(file); // Создает поток для записи
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);//Записывает в поток данные
        stream.close();
        byteChannel.close();
    }

        public synchronized void downloadPicture() throws InterruptedException {
        int kkk;
            String Url;
            try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
                 BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_PICTURE))) {
                while ((Url = inFile.readLine()) != null) {
                    URL url = new URL(Url);

                    String result;
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        result = bufferedReader.lines().collect(Collectors.joining("\n"));
                    }
                    pictureCounter = result.split("\n").length;

                    Pattern email_pattern = Pattern.compile("\\/covers\\/(.+).jpg");
                    Matcher matcher = email_pattern.matcher(result);
                    while (matcher.find()) {
                        outFile.write("https://ru.hitmotop.com/" + matcher.group() + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedReader pictureFile = new BufferedReader(new FileReader(OUT_FILE_PICTURE))) {
                String picture;
                int count = 0;
                try{
                    while ((picture = pictureFile.readLine()) != null) {
                        try {
                            InputStream in = new URL(picture).openStream();
                            Files.copy(in, Paths.get(PATH_TO_PCTURE + count + ".jpg"));
                            resultAll.write(findString("outPicture.txt", count) + " " + PATH_TO_PCTURE + "\n");
                            resultAll.flush();
                            count++;
                            System.out.println("Картинка скачана.");
                            notify();
                            wait();
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized void downloadMusic() throws InterruptedException {
        String Url;
            try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
                 BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_TXT))) {
                while ((Url = inFile.readLine()) != null) {
                    URL url = new URL(Url);

                    String result;
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        result = bufferedReader.lines().collect(Collectors.joining("\n"));
                    }
                    Pattern email_pattern = Pattern.compile("https:\\/\\/ru.hitmotop.com\\/get\\/music(.+).mp3");
                    Matcher matcher = email_pattern.matcher(result);
                    while (matcher.find()) {
                        outFile.write(matcher.group() + "\r\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedReader musicFile = new BufferedReader(new FileReader(OUT_FILE_TXT))) {
                String music;
                int count = 0;
                boolean aboba = true;
                try {
                    while ((music = musicFile.readLine()) != null) {
                        downloadUsingNIO(music, PATH_TO_MUSIC + String.valueOf(count) + ".mp3");
                        resultAll.write(findString("outFile.txt", count ) + " " + PATH_TO_MUSIC + "\n");
                        resultAll.flush();
                        count++;
                        Pattern pattern = Pattern.compile("\\d\\/(\\w).+_");
                        Matcher matcher = pattern.matcher(music);
                        while(matcher.find()){
                            String pp = matcher.group().replaceAll("(1|2|3|4|5|6|7|8|9|0)\\/", "");
                            String pp1 = pp.replaceAll("-", " ");
                            String pp2 = pp1.replaceAll("_", " ");
                            String pp3 = pp2.replaceAll("  ", " ");
                            System.out.printf("Музыка %s скачана." + "\n", pp3);
                            notify();
                            wait();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void goPlay(){
            try (FileInputStream inputStream = new FileInputStream("music/music0.mp3")) {
                try {
                    Player player = new Player(inputStream);
                    player.play();
                } catch (Exception e) {
                    e.getMessage();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String findString(String path, int counter){
            try(FileReader fileReader = new FileReader(path)){
                String text = "";
                int k = 0;
                while ((k = fileReader.read()) != -1){
                    text += (char)k;
                }
                String[] array = text.split("\n");
                String out = array[counter];
                return out;
            }
            catch (Exception exception){

            }
            return "error";
        }
}

class Picture extends Thread{

    Download download;
    Picture(Download download) {
        this.download = download;
    }
    public void run(){
        try {
            download.downloadPicture();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
class Music extends Thread{
    Download download;
    Music(Download download){
        this.download = download;
    }
    public void run(){
        try {
            download.downloadMusic();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
