package id.my.jvm.oauth2_pkce.note;

import com.jayway.jsonpath.JsonPath;
import id.my.jvm.oauth2_pkce.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by IntelliJ IDEA.
 * Project : oauth2-pkce
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 27/09/26
 * Time: 06.04
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class NoteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private static JwtRequestPostProcessor adminToken(String... scopes) {
        return jwt().jwt(jwt -> jwt.subject("admin").claim("scope", String.join(" ", scopes)))
                .authorities(Arrays.stream(scopes)
                        .<GrantedAuthority>map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                        .toList());
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/notes")).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectWriteWithReadOnlyScope() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .with(adminToken("notes.read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"x\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldValidateRequestBody() throws Exception {
        mockMvc.perform(post("/api/notes")
                        .with(adminToken("notes.write"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.title").exists());
    }

    @Test
    void shouldPerformCrudOnOwnNotes() throws Exception {
        String body = mockMvc.perform(post("/api/notes")
                        .with(adminToken("notes.write"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"First note\",\"content\":\"Hello PKCE\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title").value("First note"))
                .andReturn().getResponse().getContentAsString();
        Integer id = JsonPath.read(body, "$.id");

        mockMvc.perform(get("/api/notes/{id}", id).with(adminToken("notes.read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Hello PKCE"));

        mockMvc.perform(put("/api/notes/{id}", id)
                        .with(adminToken("notes.write"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"content\":\"Changed\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));

        mockMvc.perform(get("/api/notes").with(adminToken("notes.read")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.page.totalElements").isNumber());

        mockMvc.perform(delete("/api/notes/{id}", id).with(adminToken("notes.write")))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/notes/{id}", id).with(adminToken("notes.read")))
                .andExpect(status().isNotFound());
    }
}
