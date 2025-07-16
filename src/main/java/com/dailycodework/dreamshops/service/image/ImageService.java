package com.dailycodework.dreamshops.service.image;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dailycodework.dreamshops.dto.ImageDto;
import com.dailycodework.dreamshops.exceptions.ResourceNotFoundException;
import com.dailycodework.dreamshops.model.Image;
import com.dailycodework.dreamshops.model.Product;
import com.dailycodework.dreamshops.repository.ImageRepository;
import com.dailycodework.dreamshops.service.product.IProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService {
    private final ImageRepository imageRepository;
    private final IProductService productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No image found with id: " + id));
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete,
                () -> {
                    throw new ResourceNotFoundException(
                            "No image found with id: " + id);
                });
    }

    @Override
    public List<ImageDto> saveImages(List<MultipartFile> files,
            Long productId) {
        //
        Product product = productService.getProductById(productId);

        List<ImageDto> savedImageDto = new ArrayList<>();

        // Iterate through the list of files and save each image
        for (MultipartFile file : files) {
            try {
                // Create a new Image object and set its properties
                Image image = new Image();
                image.setFileName(file.getOriginalFilename());
                image.setFileType(file.getContentType());
                image.setImage(new SerialBlob(file.getBytes()));
                image.setProduct(product);

                // Build the download URL
                String buildDownloadUrl = "/api/v1/images/download/";
                String downloadUrl = buildDownloadUrl + image.getId();
                image.setDownloadUrl(downloadUrl);
                Image savedImage = imageRepository.save(image);

                // Build the downloadURL using the newly assigned ID and save
                // it
                savedImage
                        .setDownloadUrl(buildDownloadUrl + savedImage.getId());
                imageRepository.save(savedImage);

                // Return an ImageDto to the client (to
                // avoid leaking the Blob data)
                ImageDto imageDto = new ImageDto();
                imageDto.setId(savedImage.getId());
                imageDto.setFileName(savedImage.getFileName());
                imageDto.setDownloadUrl(savedImage.getDownloadUrl());
                savedImageDto.add(imageDto);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        return savedImageDto;
    }

    @Override
    public void updateImage(MultipartFile file, Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No image found with id: " + id));

        try {
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to update image", e);
        }
    }
}
