package core.basesyntax.bookstore.shoppingcart;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.basesyntax.bookstore.dto.cartitem.CartItemDto;
import core.basesyntax.bookstore.dto.cartitem.CreateCartItemDto;
import core.basesyntax.bookstore.dto.cartitem.UpdateCartItemDto;
import core.basesyntax.bookstore.dto.shoppingcart.ShoppingCartDto;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;
    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = -1L;
    private static final CreateCartItemDto VALID_REQUEST = new CreateCartItemDto();
    private static final CartItemDto VALID_RESPONSE = new CartItemDto();
    private static final ShoppingCartDto VALID_SHOPPING_CART_DTO = new ShoppingCartDto();
    private static final UpdateCartItemDto VALID_UPDATE_REQUEST = new UpdateCartItemDto();
    private static final CartItemDto VALID_UPDATE_RESPONSE = new CartItemDto();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        VALID_REQUEST.setBookId(1L);
        VALID_REQUEST.setQuantity(10);

        VALID_RESPONSE.setId(1L);
        VALID_RESPONSE.setBookId(1L);
        VALID_RESPONSE.setQuantity(VALID_REQUEST.getQuantity());
        VALID_RESPONSE.setBookTitle("Title 1");

        VALID_SHOPPING_CART_DTO.setUserId(1L);
        VALID_SHOPPING_CART_DTO.setCartItems(Set.of(VALID_RESPONSE));

        VALID_UPDATE_REQUEST.setQuantity(100);

        VALID_UPDATE_RESPONSE.setId(VALID_RESPONSE.getId());
        VALID_UPDATE_RESPONSE.setBookId(VALID_RESPONSE.getBookId());
        VALID_UPDATE_RESPONSE.setQuantity(VALID_UPDATE_REQUEST.getQuantity());
        VALID_UPDATE_RESPONSE.setBookTitle(VALID_RESPONSE.getBookTitle());
    }

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext webApplicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource(
                            "database/shoppingcart/setup-shopping-cart-controller.sql")
            );
        }
    }

    @AfterAll
    static void afterAll(
            @Autowired DataSource dataSource
    ) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("database/delete-all-from-db.sql")
            );
        }
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test addCartItem endpoint with valid request")
    public void addCartItem_validRequest_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    CartItemDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            CartItemDto.class
                    );
                    assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_RESPONSE,
                            actual,
                            "id"
                    );
                    assertTrue(expression);
                });
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test getShoppingCart endpoint")
    public void getShoppingCart_validUser_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    ShoppingCartDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            ShoppingCartDto.class
                    );
                    assertNotNull(actual);
                    assertEquals(2, actual.getCartItems().size());
                    assertEquals(actual.getUserId(),
                            VALID_SHOPPING_CART_DTO.getUserId());
                });
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test update endpoint with valid request")
    public void update_validRequest_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/cart/cart-items/{id}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    CartItemDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            CartItemDto.class
                    );
                    assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_UPDATE_RESPONSE,
                            actual,
                            "id"
                    );
                    assertTrue(expression);
                });
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test update endpoint with invalid ID")
    public void update_invalidId_returnNotFound() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.put("/cart/cart-items/{id}", INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test deleteCartItem endpoint")
    public void deleteCartItem_returnNoContent() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/cart/{cartItemId}", VALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test deleteCartItem endpoint with invalid ID")
    public void deleteCartItem_invalidId_returnNotFound() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete("/cart/{cartItemId}", INVALID_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
