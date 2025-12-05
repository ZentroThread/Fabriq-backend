package com.example.FabriqBackend.service.impl;

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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "create attire")
public class AttireServiceImpl implements IAttireService {

    private final AttireDao attireDao;
    private final ModelMapper modelMapper;
    private final S3Service s3Service;
    private final CategoryDao categoryDao;


    @CacheEvict(key = "'allAttires'")
    public ResponseEntity<?> createAttire(AttireCreateDto dto, MultipartFile image){
        try {
            // Upload image to S3 if provided
            Category category = categoryDao.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));

            Attire attire = modelMapper.map(dto, Attire.class);
            attire.setId(null); // Ensure ID is null to force insert instead of update

            // Generate unique attireCode if not provided or if duplicate exists
//            if (attire.getAttireCode() == null || attire.getAttireCode().isEmpty() || attireDao.findByAttireCode(attire.getAttireCode()) != null) {
//                attire.setAttireCode("ATT-" + System.currentTimeMillis());
//            }

            attire.setCategory(category);

            // 5. Upload image to S3 if provided
            if (image != null && !image.isEmpty()) {

                String imageUrl = s3Service.uploadFile(image);
                attire.setImageUrl(imageUrl);
            }

            // 6. Save to database
            Attire savedAttire = attireDao.save(attire);

            // 7. Return success response
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAttire);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create attire: " + e.getMessage());
        }

    }

    @Cacheable(key = "'allAttires'")
    public List<Attire> getAllAttire() {
        return attireDao.findAll();
    }

    @CacheEvict(key = "#id + ':deletedAttire'")
    public ResponseEntity<?> deleteAttire(Integer id) {
        attireDao.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @CachePut(key = "'updatedAttire:' + #id")
    public ResponseEntity<?> updateAttire(Integer id, AttireUpdateDto attireUpdateDto) {

        Attire attire1 = attireDao.findById(id)
                .map(attire -> {
                    modelMapper.map(attireUpdateDto, attire);

                    Attire updatedAttire = attireDao.save(attire);
                    return ResponseEntity.ok().body(updatedAttire);
                })
                .orElseGet(() -> ResponseEntity.notFound().build()).getBody();
        return ResponseEntity.ok(attire1);
    }

    @Cacheable(key = "'attireById:' + #id")
    public ResponseEntity<?> getAttireById(Integer id) {
        Attire attire = attireDao.findById(id).orElse(null);
        if (attire != null) {
            return ResponseEntity.ok(attire);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attire not found");
        }
    }

    @Cacheable(key = "'attireByCode:' + #attireCode")
    public ResponseEntity<?> getAttireByAttireCode(String attireCode) {
        Attire attire = attireDao.findByAttireCode(attireCode);
        if (attire != null) {
            return ResponseEntity.ok(attire);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Attire not found");
        }
    }

    @Cacheable(key = "'attiresByStatus:' + #status")
    public List<Attire> getAttireByStatus(String status) {
        return attireDao.findByAttireStatus(status);
    }

    public List<Attire> getAttireByCategoryId(Integer categoryId) {
        return attireDao.findByCategoryCategoryId(categoryId);
    }

}
