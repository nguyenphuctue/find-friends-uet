package com.uet.psnbackend.service;

import com.uet.psnbackend.entity.IdObjectEntity;
import com.uet.psnbackend.entity.ImageDataEntity;
import com.uet.psnbackend.repository.StorageRepository;
import com.uet.psnbackend.util.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public ResponseObjectService uploadImage(MultipartFile file) throws IOException {
        ImageDataEntity imageData = storageRepository.save(ImageDataEntity.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());
        ResponseObjectService responseObj = new ResponseObjectService();
        if (imageData != null) {
            IdObjectEntity idObjectEntity= new IdObjectEntity(imageData.getId());
            responseObj.setStatus("success");
            responseObj.setMessage("upload image successful");
            responseObj.setPayload(idObjectEntity);
            return responseObj;
        } else {
            responseObj.setStatus("fail");
            responseObj.setMessage("upload image fail");
            responseObj.setPayload("null");
            return responseObj;
        }

    }

    public byte[] downloadImage(String fileName) {
        Optional<ImageDataEntity> dbImageData = storageRepository.findByName(fileName);
        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
        return images;
    }

    public Optional<ImageDataEntity> findById(String idImage){
        return storageRepository.findById(idImage);
    }
}
