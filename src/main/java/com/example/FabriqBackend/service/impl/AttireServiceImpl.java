package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dao.CategoryDao;
import com.example.FabriqBackend.dto.AttireCreateDto;
import com.example.FabriqBackend.dto.AttireUpdateDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.model.Category;
import com.example.FabriqBackend.service.IAttireService;
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
public class AttireServiceImpl implements IAttireService {

    private final AttireDao attireDao;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;
    private final CategoryDao categoryDao;

    @Value("${aws.s3.bucket.name}")
    private String attireBucketName;

    @CacheEvict(value = "attires", allEntries = true)
    public ResponseEntity<?> createAttire(AttireCreateDto dto, MultipartFile image) {
        try {
            String currentTenantId = TenantContext.getCurrentTenant();
            if (currentTenantId == null || currentTenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Tenant ID not found. Please ensure you are authenticated.");
            }

            // Find category
            Category category = categoryDao.findByCategoryId(dto.getCategoryId())
                    .orElseThrow(() -> {
                        return new RuntimeException("Category not found with id: " + dto.getCategoryId());
                    });

            Attire attire = modelMapper.map(dto, Attire.class);
            attire.setId(null);
            attire.setCategory(category);

            if (image != null && !image.isEmpty()) {
                String imageUrl = s3Service.uploadFile(image,attireBucketName);
                attire.setImageUrl(imageUrl);
            }

            Attire savedAttire = attireDao.save(attire);
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
    System.out.println("Fetching all attires for tenant: " + currentTenantId);
    // Use findAll() from TenantAwareDao which automatically filters by tenant using SpEL
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
                            // Log the error and continue with deletion of DB record
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
        try {
            String currentTenantId = TenantContext.getCurrentTenant();

            if (currentTenantId == null || currentTenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Tenant ID not found.");
            }

            return attireDao.findById(id)
                    .map(attire -> {
                        // ✨ Clean one-liner
                        dto.applyTo(attire);

                        // Handle category separately (needs DAO access)
                        if (dto.getCategoryId() != null) {
                            Category category = categoryDao.findByCategoryId(
                                            dto.getCategoryId())
                                    .orElseThrow(() -> new RuntimeException("Category not found"));
                            attire.setCategory(category);
                        }

                        // Handle image: upload new image and remove previous one from S3 to avoid orphaned files
                        if (image != null && !image.isEmpty()) {
                            String previousImageUrl = attire.getImageUrl();
                            try {
                                String newImageUrl = s3Service.uploadFile(image);
                                attire.setImageUrl(newImageUrl);

                                // If there was a previous image and it's different from the new one, try to delete it
                                if (previousImageUrl != null && !previousImageUrl.isBlank()
                                        && !previousImageUrl.equals(newImageUrl)) {
                                    try {
                                        s3Service.deleteFile(previousImageUrl);
                                    } catch (Exception e) {
                                        // Log failure but don't abort the update (optional: change to abort if desired)
                                        System.err.println("Failed to delete previous image from S3: " + e.getMessage());
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to upload image: " + e.getMessage());
                            }
                        }
                        return ResponseEntity.ok(attireDao.save(attire));
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update attire: " + e.getMessage());
        }
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':attireById:' + #id")
    public ResponseEntity<?> getAttireById(Integer id) {
        Attire attire = attireDao.findById(id).orElse(null);
        if (attire != null) {
            return ResponseEntity.ok(attire);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attire not found");
        }
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
