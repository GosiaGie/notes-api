package com.notes_api.controller;

import com.jayway.jsonpath.JsonPath;
import com.notes_api.entity.User;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.notes_api.repository.ItemRepository;
import com.notes_api.repository.UserRepository;
import com.notes_api.security.jtw.JwtIssuer;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Collections;
import java.util.UUID;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.jpa.hibernate.ddl-auto=update",
        "spring.jpa.show-sql=true",
        "org.hibernate.envers.audit_table_suffix=_aud",
        "spring.jpa.properties.hibernate.envers.store_data_at_delete=true",
        "spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect"
})

@AutoConfigureMockMvc
class ItemRestIntegrationTest {
    private UUID userId;
    private static final String LOGIN = "TestLogin";
    private static final String PASSWORD = "TestPassword";
    private static final String ROLE_USER = "USER";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtIssuer jwtIssuer;

    @DynamicPropertySource
    static void configureProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @ServiceConnection
    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @BeforeEach
    void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        User savedUser = userRepository.save(User.builder()
                .login(LOGIN)
                .password(PASSWORD)
                .build());
        this.userId = savedUser.getId();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @BeforeAll
    static void beforeAll() {
        mySQLContainer.start();
    }

    @AfterAll
    static void afterAll() {
        mySQLContainer.stop();
    }

    @Test
    void tooManyLoginRequests() throws Exception {
        String requestJson = "{\"login\": \"wrong login\", \"password\": \"wrong password\"}";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(header().exists("Retry-After"))
                .andExpect(header().string("Retry-After", matchesPattern("\\d+")))
                .andExpect(status().isTooManyRequests());
    }

    @AutoConfigureMockMvc
    @Test
    void shouldSaveEditAndShowHistoryWithVersion() throws Exception {
        String token = jwtIssuer.issue(userId, LOGIN, Collections.singletonList(ROLE_USER));

        String requestJson = "{\"title\": \"start title\", \"content\": \"start content\"}"; //starting values, Item not in DB yet
        String responseCreatingItem = mockMvc.perform(post("/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(jsonPath("$.version").value(0))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        //Item in DB, getting its itemId
        UUID itemId = UUID.fromString(JsonPath.read(responseCreatingItem, "$.id"));

        String updateJson1 = "{\"title\": \"title after edit 1\", \"content\": \"content after edit 1\"," +
                "\"version\": "+ 0 +"}";
        mockMvc.perform(patch("/items/" + itemId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.version").value(1))
                .andReturn().getResponse().getContentAsString();

        //getting updated version number
        String updateJson2 = "{\"title\": \"title after edit 2\", \"content\": \"content after edit 2\"," +
                " \"version\": "+ 1 +"}";
        mockMvc.perform(patch("/items/" + itemId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson2))
                .andExpect(jsonPath("$.version").value(2))
                .andExpect(status().isOk());

        mockMvc.perform(get("/items/" + itemId + "/history")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].title").value("start title"))
                .andExpect(jsonPath("$[1].title").value("title after edit 1"))
                .andExpect(jsonPath("$[2].title").value("title after edit 2"))
                .andExpect(jsonPath("$[0].content").value("start content"))
                .andExpect(jsonPath("$[1].content").value("content after edit 1"))
                .andExpect(jsonPath("$[2].content").value("content after edit 2"));
    }

}