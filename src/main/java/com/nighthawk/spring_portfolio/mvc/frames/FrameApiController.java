package com.nighthawk.spring_portfolio.mvc.frames;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

@RestController
public class FrameApiController {

    @Autowired
    private FrameJpaRepository frameJpaRepository;

    private final RestTemplate restTemplate;

    public FrameApiController() {
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/image")
    public String processImage(@RequestBody ImageData imageData) {
        try {
            List<List<List<Integer>>> mnistDataList = generateAugmentedFrames(imageData.getImage());
            if (mnistDataList != null) {
                createAugmentedImages(ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(imageData.getImage()))));
                for (List<List<Integer>> mnistData : mnistDataList) {
                    postMnistData(mnistData);

                    // Store MNIST data in the database
                    Frame frame = new Frame();
                    frame.setMnistData(mnistData.toString()); // Converting list of lists to string for database storage
                    frameJpaRepository.save(frame);
                }

                return "{\"message\": \"Image processed and posted successfully!\"}";
            } else {
                return "{\"error\": \"Failed to process image\"}";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to process image due to an internal error\"}";
        }
    }

    @GetMapping("/mnist")
    public List<Frame> getMnistData() {
        return frameJpaRepository.findAll();
    }
    
    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImageById(@PathVariable("id") Long id) {
        try {
            // Retrieve the saved image file based on the provided ID
            String filename = "augmented_image_" + id + ".png"; // Adjusting to match the new naming convention
            Path imagePath = Paths.get(filename);
    
            if (!Files.exists(imagePath)) {
                return ResponseEntity.notFound().build();
            }
    
            // Read the image file
            File file = imagePath.toFile();
            FileInputStream fis = new FileInputStream(file);
            byte[] imageData = new byte[(int) file.length()];
            fis.read(imageData);
            fis.close();
    
            // Return the image data in the response
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(imageData);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }    
    
    private List<List<List<Integer>>> generateAugmentedFrames(String imageData) throws IOException {
        byte[] imageBytes = Base64.getDecoder().decode(imageData);
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage originalImage = ImageIO.read(bis);

        List<List<List<Integer>>> mnistDataList = new ArrayList<>();
        BufferedImage[] augmentedImages = createAugmentedImages(originalImage);

        for (BufferedImage image : augmentedImages) {
            mnistDataList.add(convertToMNIST(image));
        }

        return mnistDataList;
    }

    private BufferedImage[] createAugmentedImages(BufferedImage originalImage) throws IOException {
        BufferedImage[] augmentedImages = new BufferedImage[9]; // Array size remains 9 for 9 variations
        augmentedImages[0] = originalImage;
        augmentedImages[1] = changeBrightness(originalImage, 0.8f);
        augmentedImages[2] = changeBrightness(originalImage, 1.2f);
        augmentedImages[3] = addNoise(originalImage, 0.1);
        augmentedImages[4] = addNoise(originalImage, 0.2);
        augmentedImages[5] = adjustHue(originalImage);
        augmentedImages[6] = rotateImage(originalImage, 30); // Rotate image by 30 degrees
        augmentedImages[7] = flipImage(originalImage, true); // Flip image horizontally
        augmentedImages[8] = flipImage(originalImage, false); // Flip image vertically
    
        // Save each augmented image to a file using i+1
        for (int i = 0; i < augmentedImages.length; i++) {
            saveImage(augmentedImages[i], "augmented_image_" + (i + 1) + ".png");
        }
    
        return augmentedImages;
    }    

    private BufferedImage changeBrightness(BufferedImage image, float factor) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        for (int y = 0; y < newImage.getHeight(); y++) {
            for (int x = 0; x < newImage.getWidth(); x++) {
                int rgba = newImage.getRGB(x, y);
                int alpha = (rgba >> 24) & 0xff;
                int red = (int)Math.min(255, ((rgba >> 16) & 0xff) * factor);
                int green = (int)Math.min(255, ((rgba >> 8) & 0xff) * factor);
                int blue = (int)Math.min(255, (rgba & 0xff) * factor);
                newImage.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
            }
        }
        return newImage;
    }

    private BufferedImage addNoise(BufferedImage image, double noiseLevel) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        for (int y = 0; y < newImage.getHeight(); y++) {
            for (int x = 0; x < newImage.getWidth(); x++) {
                int rgba = newImage.getRGB(x, y);
                int alpha = (rgba >> 24) & 0xff;
                int red = (int)Math.min(255, Math.max(0, (int)(((rgba >> 16) & 0xff) * (1 + (Math.random() - 0.5) * noiseLevel))));
                int green = (int)Math.min(255, Math.max(0, (int)(((rgba >> 8) & 0xff) * (1 + (Math.random() - 0.5) * noiseLevel))));
                int blue = (int)Math.min(255, Math.max(0, (int)((rgba & 0xff) * (1 + (Math.random() - 0.5) * noiseLevel))));
                newImage.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
            }
        }
        return newImage;
    }

    private BufferedImage adjustHue(BufferedImage image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        for (int y = 0; y < newImage.getHeight(); y++) {
            for (int x = 0; x < newImage.getWidth(); x++) {
                int rgba = newImage.getRGB(x, y);
                int alpha = (rgba >> 24) & 0xff;
                int red = Math.min(255, (int)(((rgba >> 16) & 0xff) * (0.8 + Math.random() * 0.4)));
                int green = Math.min(255, (int)(((rgba >> 8) & 0xff) * (0.8 + Math.random() * 0.4)));
                int blue = Math.min(255, (int)((rgba & 0xff) * (0.8 + Math.random() * 0.4)));
                newImage.setRGB(x, y, (alpha << 24) | (
                    red << 16) | (green << 8) | blue);
                }
            }
            return newImage;
        }
    
        private BufferedImage rotateImage(BufferedImage image, double angle) {
            // Create a transformation matrix for rotating the image
            AffineTransform tx = new AffineTransform();
            tx.rotate(Math.toRadians(angle), image.getWidth() / 2.0, image.getHeight() / 2.0);
    
            // Apply the transformation to the image
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
            return op.filter(image, null);
        }
    
        private BufferedImage flipImage(BufferedImage image, boolean horizontal) {
            // Create a transformation matrix for flipping the image
            AffineTransform tx = new AffineTransform();
            if (horizontal) {
                tx.scale(-1, 1); // Flip horizontally
                tx.translate(-image.getWidth(), 0);
            } else {
                tx.scale(1, -1); // Flip vertically
                tx.translate(0, -image.getHeight());
            }
    
            // Apply the transformation to the image
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            return op.filter(image, null);
        }
    
        private List<List<Integer>> convertToMNIST(BufferedImage image) {
            BufferedImage resizedImage = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, 28, 28, null);
            g.dispose();
    
            List<List<Integer>> mnistData = new ArrayList<>();
            for (int y = 0; y < 28; y++) {
                List<Integer> row = new ArrayList<>();
                for (int x = 0; x < 28; x++) {
                    int pixel = resizedImage.getRGB(x, y) & 0xFF;
                    row.add(pixel);
                }
                mnistData.add(row);
            }
            return mnistData;
        }
    
        private void postMnistData(List<List<Integer>> mnistData) {
            String url = "http://localhost:8017/smnist";
            restTemplate.postForObject(url, mnistData, String.class);
        }
    
        private void saveImage(BufferedImage image, String filename) throws IOException {
            File output = new File(filename);
            ImageIO.write(image, "png", output);
        }
    
        public static class ImageData {
            private String image;
            private int label;
    
            public String getImage() {
                return image;
            }
    
            public void setImage(String image) {
                this.image = image;
            }
    
            public int getLabel() {
                return label;
            }
    
            public void setLabel(int label) {
                this.label = label;
            }
        }
    }
    