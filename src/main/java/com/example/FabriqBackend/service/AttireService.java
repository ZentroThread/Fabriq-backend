package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dto.AttireUpdateDto;
import com.example.FabriqBackend.model.Attire;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "attires")
public class AttireService {

    private final AttireDao attireDao;
    private final ModelMapper modelMapper;

    @CachePut(key = "#attire.tenantId + ':attire:' + #attire.attireName")
    public ResponseEntity<?> createAttire(Attire attire) {
        attireDao.save(attire);
        return new ResponseEntity<>(attire, HttpStatus.CREATED);
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
