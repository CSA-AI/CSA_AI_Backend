package com.nighthawk.spring_portfolio.mvc.datalogin;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataFrame {

    private static final String VIDEO_DIR = "src/main/java/com/nighthawk/spring_portfolio/mvc/datalogin/videos/";
    private static final String CSV_DIR = "src/main/java/com/nighthawk/spring_portfolio/mvc/datalogin/csv/";
    private static final String PROCESSED_VIDEOS_FILE = "src/main/java/com/nighthawk/spring_portfolio/mvc/datalogin/processed_videos.txt";

    public static void processVideosInFolder() {
        Set<String> processedVideos = loadProcessedVideos();
        File folder = new File(VIDEO_DIR);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp4"));

        if (listOfFiles != null) {
            for (int videoId = 0; videoId < listOfFiles.length; videoId++) {
                File file = listOfFiles[videoId];
                if (!processedVideos.contains(file.getName())) {
                    processVideoFile(file.getAbsolutePath(), file.getName());
                    processedVideos.add(file.getName());
                    saveProcessedVideo(file.getName());
                }
            }
        }
    }

    private static void processVideoFile(String videoFilePath, String videoFileName) {
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoFilePath);

        try {
            grabber.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame frame;
            int frameNumber = 0;

            List<List<List<Integer>>> allFramesData = new ArrayList<>();

            while ((frame = grabber.grabFrame()) != null) {
                BufferedImage bi = converter.convert(frame);
                if (bi != null) {
                    List<List<Integer>> mnistData = convertToMNIST(bi);
                    allFramesData.add(mnistData);

                    System.out.println("Processed MNIST data for frame: " + frameNumber + " with data size: " + mnistData.size());
                    frameNumber++;
                }
            }

            grabber.stop();
            appendToCSV(allFramesData, videoFileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<List<Integer>> convertToMNIST(BufferedImage originalImage) {
        BufferedImage resizedImage = new BufferedImage(256, 256, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 256, 256, null);
        g.dispose();

        List<List<Integer>> mnistData = new ArrayList<>();
        for (int y = 0; y < 256; y++) {
            List<Integer> row = new ArrayList<>();
            for (int x = 0; x < 256; x++) {
                int pixel = resizedImage.getRGB(x, y) & 0xFF;
                row.add(pixel);
            }
            mnistData.add(row);
        }
        return mnistData;
    }

    private static void appendToCSV(List<List<List<Integer>>> allFramesData, String videoFileName) throws IOException {
        String csvFileName = videoFileName.replace(".mp4", ".csv");
        File csvFile = new File(CSV_DIR + csvFileName);

        // Check if the CSV file already exists
        boolean fileExists = csvFile.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
            // If the file doesn't exist, write a header or an initial message
            if (!fileExists) {
                writer.write("frame_number,data");
                writer.newLine();
            }
    
            int frameNumber = 0;
            for (List<List<Integer>> frameData : allFramesData) {
                // Flattening all rows of the frame into one line and adding a label
                String line = frameNumber + "," + frameData.stream()
                        .flatMap(List::stream) // Flatten the lists of rows into a single list
                        .map(Object::toString)
                        .collect(Collectors.joining(","));
                writer.write(line);
                writer.newLine();
                frameNumber++;
            }
        }
    }

    private static Set<String> loadProcessedVideos() {
        Set<String> processedVideos = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PROCESSED_VIDEOS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processedVideos.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processedVideos;
    }

    private static void saveProcessedVideo(String videoName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PROCESSED_VIDEOS_FILE, true))) {
            writer.write(videoName);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        processVideosInFolder();
    }
}