package id.my.jvm.oauth2_pkce;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class Oauth2PkceApplicationTests {

	@Test
	void contextLoads() {
	}

}
