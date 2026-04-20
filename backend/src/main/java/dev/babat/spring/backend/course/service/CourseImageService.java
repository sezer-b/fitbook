package dev.babat.spring.backend.course.service;

import dev.babat.spring.backend.course.entity.CourseEntity;
import dev.babat.spring.backend.course.entity.CourseImageEntity;
import dev.babat.spring.backend.course.repository.CourseImageRepository;
import dev.babat.spring.backend.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseImageService {

    private static final int MAX_IMAGES = 6;
    private static final int MAX_WIDTH = 1200;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/jpg", "image/png");

    private final CourseRepository courseRepository;
    private final CourseImageRepository courseImageRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public String uploadImage(UUID courseId, UUID providerId, MultipartFile file) throws IOException {
        CourseEntity course = courseRepository.findByIdAndProviderId(courseId, providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        int currentCount = courseImageRepository.countByCourseId(courseId);
        if (currentCount >= MAX_IMAGES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum of " + MAX_IMAGES + " images allowed");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only JPEG and PNG images are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size must not exceed 5MB");
        }

        String filename = UUID.randomUUID() + ".jpg";
        Path uploadPath = Paths.get(uploadDir, "courses", courseId.toString());
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(filename);

        BufferedImage original = ImageIO.read(file.getInputStream());
        if (original == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image file");
        }

        BufferedImage resized = resizeIfNeeded(original);
        ImageIO.write(resized, "jpg", filePath.toFile());

        int nextOrder = currentCount + 1;
        String url = baseUrl + "/uploads/courses/" + courseId + "/" + filename;

        CourseImageEntity image = new CourseImageEntity();
        image.setCourse(course);
        image.setUrl(url);
        image.setDisplayOrder(nextOrder);
        courseImageRepository.save(image);

        return url;
    }

    @Transactional
    public void deleteImage(UUID courseId, UUID imageId, UUID providerId) {
        courseRepository.findByIdAndProviderId(courseId, providerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found"));

        CourseImageEntity image = courseImageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        Path filePath = Paths.get(uploadDir).resolve(
                image.getUrl().replace(baseUrl + "/", "")
        );

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {}

        courseImageRepository.delete(image);
        reorderImages(courseId);
    }

    private void reorderImages(UUID courseId) {
        List<CourseImageEntity> images = courseImageRepository
                .findByCourseIdOrderByDisplayOrderAsc(courseId);
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setDisplayOrder(i + 1);
        }
        courseImageRepository.saveAll(images);
    }

    private BufferedImage resizeIfNeeded(BufferedImage original) {
        if (original.getWidth() <= MAX_WIDTH) return original;
        int newHeight = (int) ((double) original.getHeight() / original.getWidth() * MAX_WIDTH);
        BufferedImage resized = new BufferedImage(MAX_WIDTH, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, MAX_WIDTH, newHeight, null);
        g.dispose();
        return resized;
    }
}