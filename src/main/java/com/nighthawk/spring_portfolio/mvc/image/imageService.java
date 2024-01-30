package com.nighthawk.spring_portfolio.mvc.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class imageService {
	

    @Autowired
    private imageRepository uploadFileRepository;

	public void save(image person) {
        uploadFileRepository.save(person);
    }
    
}