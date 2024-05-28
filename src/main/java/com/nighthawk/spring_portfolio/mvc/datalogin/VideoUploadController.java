package com.nighthawk.spring_portfolio.mvc.datalogin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/videos")
public class VideoUploadController {

    private static final String VIDEO_DIR = "src/main/java/com/nighthawk/spring_portfolio/mvc/datalogin/videos/";

    @PostMapping("/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("video") MultipartFile file) {
        System.out.println("Received file: " + file.getOriginalFilename());
        System.out.println("File size: " + file.getSize());
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
        }

        try {
            // Create directory if it doesn't exist
            File uploadDir = new File(VIDEO_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the file
            Path path = Paths.get(VIDEO_DIR + file.getOriginalFilename());
            Files.write(path, file.getBytes());

            // Process all videos in the folder
            DataFrame.processVideosInFolder();

            return ResponseEntity.status(HttpStatus.OK).body("File uploaded and processed successfully");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }
}
