package com.uet.psnbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ImageData")
@Builder
public class ImageDataEntity {
    @Id
    private String id;
    private String name;
    private String type;
    private byte[] imageData;
}
