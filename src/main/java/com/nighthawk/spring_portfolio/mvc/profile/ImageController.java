package com.nighthawk.spring_portfolio.mvc.profile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")

@Controller
public class ImageController {
    @Autowired
    private UploadFileRepository uploadFileRepository;

	@Autowired
	ImageService imageService;
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@PostMapping
public ResponseEntity<String> save(MultipartFile image, @RequestParam("username") String username) throws IOException {
    Optional<UploadFile> existingFileOptional = uploadFileRepository.findByUsername(username);

    if (existingFileOptional.isPresent()) {
        UploadFile existingFile = existingFileOptional.get();

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytearr = image.getBytes();
        String encodedString = encoder.encodeToString(bytearr);

        existingFile.setImageEncoder(encodedString);
        uploadFileRepository.save(existingFile);
    } else {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] bytearr = image.getBytes();
        String encodedString = encoder.encodeToString(bytearr);
        UploadFile file = new UploadFile(username, encodedString);
        uploadFileRepository.save(file);
    }
    return new ResponseEntity<>("It is created successfully", HttpStatus.CREATED);
}
    @GetMapping("/{username}")
    public ResponseEntity<?> downloadImage(@PathVariable String username) {
        Optional<UploadFile> optional = uploadFileRepository.findByUsername(username);
        UploadFile file = optional.get();
        String data = file.getImageEncoder();
        byte[] imageBytes = DatatypeConverter.parseBase64Binary(data);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageBytes);
    }
    
	

}