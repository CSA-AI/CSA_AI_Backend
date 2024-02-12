package com.nighthawk.spring_portfolio.mvc.profile;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class ImageService {
	

    @Autowired
    private UploadFileRepository uploadFileRepository;

	public void save(UploadFile person) {
        uploadFileRepository.save(person);
    }
    
}