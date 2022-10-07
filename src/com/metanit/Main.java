package com.metanit;
import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    private static final String IN_FILE_TXT = "inFile.txt";
    private static final String OUT_FILE_TXT = "outFile.txt";
    private static final String PATH_TO_MUSIC = "music\\music";

    private static final String PILE_FOR_PICTURE = "inPicture.txt";
    private static final String OUT_FILE_PICTURE = "outPicture.txt";
    private static final String PATH_TO_PCTURE = "D:/Java/Musik//picture/image";

    public static void main(String[] args) {
        downloadPicture();
        downloadMusic();
    }

    private static void downloadUsingNIO(String strUrl, String file) throws IOException {
        URL url = new URL(strUrl);
        ReadableByteChannel byteChannel = Channels.newChannel(url.openStream()); // Создает байтовый канал для чтения сайта
        FileOutputStream stream = new FileOutputStream(file); // Создает поток для записи
        stream.getChannel().transferFrom(byteChannel, 0, Long.MAX_VALUE);//Записывает в поток данные
        stream.close();
        byteChannel.close();
    }

public static void downloadMusic() {
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
        int count = 1;
        try {
            while ((music = musicFile.readLine()) != null) {
                downloadUsingNIO(music, PATH_TO_MUSIC + String.valueOf(count) + ".mp3");
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

}
    public static void downloadPicture() {
        String Url;
        try (BufferedReader inFile = new BufferedReader(new FileReader(IN_FILE_TXT));
             BufferedWriter outFile = new BufferedWriter(new FileWriter(OUT_FILE_PICTURE))) {
            while ((Url = inFile.readLine()) != null) {
                URL url = new URL(Url);

                String result;
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                    result = bufferedReader.lines().collect(Collectors.joining("\n"));
                }


                Pattern email_pattern = Pattern.compile("\\/covers\\/(.+).jpg");
                Matcher matcher = email_pattern.matcher(result);
                while (matcher.find()) {
                    outFile.write("https://ru.hitmotop.com/" + matcher.group() + "\r\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader pictureFile = new BufferedReader(new FileReader(OUT_FILE_PICTURE))) {
            String picture;
            int count = 1;
            try {
                while ((picture = pictureFile.readLine()) != null) {
                    try {
                        InputStream in = new URL(picture).openStream();
                        Files.copy(in, Paths.get(PATH_TO_PCTURE + count + ".jpg"));
                        count++;
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
    }}
