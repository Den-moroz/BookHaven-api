package core.basesyntax.bookstore.order;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import core.basesyntax.bookstore.dto.order.CreateOrderRequestDto;
import core.basesyntax.bookstore.dto.order.OrderDto;
import core.basesyntax.bookstore.dto.order.UpdateOrderStatusDto;
import core.basesyntax.bookstore.dto.orderitem.OrderItemDto;
import core.basesyntax.bookstore.model.Order;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
public class OrderControllerTest {
    protected static MockMvc mockMvc;
    private static final Long VALID_ORDER_ID = 1L;
    private static final Long VALID_ORDER_ITEM_ID = 1L;
    private static final Long INVALID_ORDER_ID = -1L;
    private static final Long INVALID_ORDER_ITEM_ID = -1L;
    private static final CreateOrderRequestDto VALID_REQUEST =
            new CreateOrderRequestDto("123, Kyiv");
    private static final CreateOrderRequestDto INVALID_REQUEST =
            new CreateOrderRequestDto(null);
    private static final OrderDto VALID_RESPONSE = new OrderDto();
    private static final OrderItemDto VALID_ORDER_ITEM = new OrderItemDto();
    private static final UpdateOrderStatusDto VALID_UPDATE_REQUEST =
            new UpdateOrderStatusDto(Order.Status.DELIVERED);
    private static final OrderDto VALID_UPDATE_RESPONSE = new OrderDto();

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        VALID_ORDER_ITEM.setId(2L);
        VALID_ORDER_ITEM.setBookId(1L);
        VALID_ORDER_ITEM.setQuantity(10);

        VALID_RESPONSE.setUserId(1L);
        VALID_RESPONSE.setStatus(Order.Status.PENDING.toString());
        VALID_RESPONSE.setTotal(new BigDecimal("100.00"));
        VALID_RESPONSE.setOrderItems(List.of(VALID_ORDER_ITEM));

        VALID_UPDATE_RESPONSE.setUserId(1L);
        VALID_UPDATE_RESPONSE.setStatus(VALID_UPDATE_REQUEST.status().toString());
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
                    new ClassPathResource("database/order/setup-order-controller.sql")
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
    @DisplayName("Test placeAnOrder endpoint with valid request")
    public void placeAnOrder_validRequest_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    OrderDto actual = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            OrderDto.class
                    );
                    Assertions.assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_RESPONSE,
                            actual,
                            "id", "orderDate", "total"
                    );
                    Assertions.assertTrue(expression);
                });
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test placeAnOrder endpoint with invalid request")
    public void placeAnOrder_invalidRequest_returnBadRequest() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(INVALID_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test getAll endpoint")
    public void getAll_validOrder_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<OrderDto> actualList = objectMapper.readValue(result.getResponse()
                                    .getContentAsString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class,
                                    OrderDto.class));
                    Assertions.assertNotNull(actualList);
                    Assertions.assertEquals(2, actualList.size());
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_RESPONSE,
                            actualList.get(1),
                            "id", "orderDate"
                    );
                    Assertions.assertTrue(expression);
                });
    }

    @WithMockUser(username = "email@i.ua", roles = {"ADMIN"})
    @Test
    @DisplayName("Test getAllByOrderId endpoint")
    public void getAllByOrderId_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/orders/{orderId}/items", VALID_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    List<OrderItemDto> actualList = objectMapper.readValue(result.getResponse()
                                    .getContentAsString(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class,
                                    OrderItemDto.class));
                    Assertions.assertNotNull(actualList);
                    Assertions.assertEquals(1, actualList.size());
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_ORDER_ITEM,
                            actualList.get(0),
                            "id"
                    );
                    Assertions.assertTrue(expression);
                });
    }

    @WithMockUser(username = "email@i.ua", roles = {"ADMIN"})
    @Test
    @DisplayName("Test update endpoint with valid request")
    public void update_validRequest_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/orders/{id}", VALID_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    OrderDto actual = objectMapper.readValue(result.getResponse()
                                    .getContentAsString(), OrderDto.class);
                    Assertions.assertNotNull(actual);
                    Assertions.assertEquals(VALID_UPDATE_RESPONSE.getStatus(), actual.getStatus());
                    Assertions.assertEquals(VALID_UPDATE_RESPONSE.getUserId(), actual.getUserId());
                });
    }

    @WithMockUser(username = "email@i.ua", roles = {"ADMIN"})
    @Test
    @DisplayName("Test update endpoint with invalid orderId")
    public void update_invalidOrderId_returnNotFound() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.patch("/orders/{id}", INVALID_ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(VALID_UPDATE_REQUEST))
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test getByIdAndOrderId endpoint")
    public void getByIdAndOrderId_returnResponse() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/orders/{orderId}/items/{itemId}",
                                VALID_ORDER_ID, VALID_ORDER_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> {
                    OrderItemDto actual = objectMapper.readValue(result.getResponse()
                            .getContentAsString(), OrderItemDto.class);
                    Assertions.assertNotNull(actual);
                    boolean expression = EqualsBuilder.reflectionEquals(
                            VALID_ORDER_ITEM,
                            actual,
                            "id"
                    );
                    Assertions.assertTrue(expression);
                });
    }

    @WithMockUser(username = "email@i.ua")
    @Test
    @DisplayName("Test getByIdAndOrderId endpoint with invalid orderId or itemId")
    public void getByIdAndOrderId_invalidIds_returnNotFound() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/orders/{orderId}/items/{itemId}",
                                INVALID_ORDER_ID, INVALID_ORDER_ITEM_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
