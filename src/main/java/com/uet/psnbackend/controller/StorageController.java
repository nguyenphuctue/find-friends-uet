package com.uet.psnbackend.controller;

import com.uet.psnbackend.entity.ImageDataEntity;
import com.uet.psnbackend.service.ResponseObjectService;
import com.uet.psnbackend.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/findfriend.com/image")
@Slf4j
public class StorageController {

    @Autowired
    private StorageService storageService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image")MultipartFile file) throws IOException {
//        String uploadImage = storageService.uploadImage(file);
        return new ResponseEntity<ResponseObjectService>(storageService.uploadImage(file), HttpStatus.OK);
    }

//    @GetMapping("/{fileName}")
//    public ResponseEntity<?> downloadImage(@PathVariable String fileName){
//        log.info(fileName);
//        byte[] imageData=storageService.downloadImage(fileName);
//        return ResponseEntity.status(HttpStatus.OK)
//                .contentType(MediaType.valueOf("image/png"))
//                .body(imageData);
//
//    }

    @GetMapping("/{idImage}")
    public ResponseEntity<?> downloadImage(@PathVariable String idImage){
        Optional<ImageDataEntity> image = storageService.findById(idImage);
        if(image.isPresent()){
            String fileName = image.get().getName();
            byte[] imageData=storageService.downloadImage(fileName);
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(imageData);
        } else {
            ResponseObjectService responseObj = new ResponseObjectService();
            responseObj.setStatus("fail");
            responseObj.setMessage("invalid user id");
            responseObj.setPayload(null);
            return new ResponseEntity<ResponseObjectService>(responseObj, HttpStatus.OK);
        }
    }
}
