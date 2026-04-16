package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dao.CategoryDao;
import com.example.FabriqBackend.dto.AttireCreateDto;
import com.example.FabriqBackend.dto.AttireUpdateDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.model.Category;
import com.example.FabriqBackend.service.Interface.IAttireService;
import com.example.FabriqBackend.service.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "attires")
@Slf4j

public class AttireServiceImpl implements IAttireService {

    private final AttireDao attireDao;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;
    private final CategoryDao categoryDao;

    @Value("${aws.s3.bucket.name}")
    private String attireBucketName;

    @CacheEvict(value = "attires", allEntries = true)
    public ResponseEntity<?> createAttire(AttireCreateDto dto, MultipartFile image) {
        log.info("createAttire called for categoryId={} filename={}", dto != null ? dto.getCategoryId() : null, image != null ? image.getOriginalFilename() : null);
        try {
            String currentTenantId = TenantContext.getCurrentTenant();
            if (currentTenantId == null || currentTenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Tenant ID not found. Please ensure you are authenticated.");
            }

            Category category = categoryDao.findByCategoryId(dto.getCategoryId())
                    .orElseThrow(() -> {
                        return new RuntimeException("Category not found with id: " + dto.getCategoryId());
                    });

            Attire attire = modelMapper.map(dto, Attire.class);
            attire.setId(null);
            attire.setCategory(category);

            if (image != null && !image.isEmpty()) {
                String imageUrl = s3Service.uploadFile(image);
                attire.setImageUrl(imageUrl);
            }

            Attire savedAttire = attireDao.save(attire);
            log.info("Attire created id={} code={}", savedAttire.getId(), savedAttire.getAttireCode());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAttire);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create attire: " + e.getMessage());
        }
    }

    @Cacheable(value = "attires", key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allAttires'")
    public List<Attire> getAllAttire() {
        String currentTenantId = TenantContext.getCurrentTenant();
        log.info("Fetching all attires for tenant: " + currentTenantId);
        return attireDao.findAll();
    }

    @CacheEvict(value = "attires", allEntries = true)
    public ResponseEntity<?> deleteAttire(Integer id) {
        return attireDao.findById(id)
                .map(attire -> {
                    String imageUrl = attire.getImageUrl();
                    if (imageUrl != null && !imageUrl.isBlank()) {
                        try {
                            s3Service.deleteFile(imageUrl);
                        } catch (Exception e) {
                            System.err.println("Failed to delete image from S3: " + e.getMessage());
                        }
                    }

                    attireDao.deleteById(id);
                    return new ResponseEntity<>(HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attire not found"));
    }

    @CacheEvict(value = "attires", allEntries = true)
    public ResponseEntity<?> updateAttire(Integer id, AttireUpdateDto dto, MultipartFile image) {
        log.info("updateAttire called for id={} filename={}", id, image != null ? image.getOriginalFilename() : null);
        try {
            String currentTenantId = TenantContext.getCurrentTenant();

            if (currentTenantId == null || currentTenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Tenant ID not found.");
            }

            return attireDao.findById(id)
                    .map(attire -> {
                        dto.applyTo(attire);

                        if (dto.getCategoryId() != null) {
                            Category category = categoryDao.findByCategoryId(
                                            dto.getCategoryId())
                                    .orElseThrow(() -> {
                                        log.warn("Category not found during update for id={}", dto.getCategoryId());
                                        return new RuntimeException("Category not found");
                                    });
                            attire.setCategory(category);
                        }

                        if (image != null && !image.isEmpty()) {
                            String previousImageUrl = attire.getImageUrl();
                            try {
                                String newImageUrl = s3Service.uploadFile(image);
                                attire.setImageUrl(newImageUrl);

                                if (previousImageUrl != null && !previousImageUrl.isBlank()
                                        && !previousImageUrl.equals(newImageUrl)) {
                                    try {
                                        s3Service.deleteFile(previousImageUrl);
                                    } catch (Exception e) {
                                        log.error("Failed to delete previous image from S3 for attire id={} url={}: {}", id, previousImageUrl, e.getMessage(), e);
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to upload image: " + e.getMessage());
                            }
                        }
                        Attire saved = attireDao.save(attire);
                        log.info("Updated attire id={} code={}", saved.getId(), saved.getAttireCode());
                        return ResponseEntity.ok(saved);
                    })
                    .orElseGet(() -> {
                        log.warn("Attempted to update non-existent attire id={}", id);
                        return ResponseEntity.notFound().build();
                    });

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update attire: " + e.getMessage());
        }
    }

    @Cacheable(
            value = "attireById",
            key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #id"
    )
    public Attire getAttireById(Integer id) {
        return attireDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Attire not found"));
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':attireByCode:' + #attireCode")
    public ResponseEntity<?> getAttireByAttireCode(String attireCode) {
        Attire attire = attireDao.findByAttireCode(attireCode);
        if (attire != null) {
            return ResponseEntity.ok(attire);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attire not found");
        }
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':attiresByStatus:' + #status")
    public List<Attire> getAttireByStatus(String status) {
        return attireDao.findByAttireStatus(status);
    }

    public List<Attire> getAttireByCategoryId(Integer categoryId) {
        return attireDao.findByCategoryCategoryId(categoryId);
    }

}
