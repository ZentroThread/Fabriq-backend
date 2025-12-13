package com.example.FabriqBackend;

import com.example.FabriqBackend.service.aws.S3Service;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
		RedisAutoConfiguration.class,
		RedisRepositoriesAutoConfiguration.class
})
class FabriqBackendApplicationTests {

	@MockitoBean
	private S3Service s3Service;

	@Test
	void contextLoads() {
	}

}
