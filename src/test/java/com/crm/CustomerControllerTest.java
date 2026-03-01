package com.crm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.crm.entity.Customer;
import com.crm.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void getAll_empty() throws Exception {
        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void create_and_getById() throws Exception {
        Customer customer = Customer.builder()
                .name("Иван Петров")
                .email("ivan@test.ru")
                .phone("+7 999 123-45-67")
                .build();

        String json = objectMapper.writeValueAsString(customer);
        String responseBody = mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Иван Петров"))
                .andExpect(jsonPath("$.email").value("ivan@test.ru"));
    }

    @Test
    void update() throws Exception {
        Customer saved = customerRepository.save(Customer.builder()
                .name("Старое имя")
                .email("old@test.ru")
                .build());
        Long id = saved.getId();

        Customer updated = Customer.builder()
                .name("Новое имя")
                .email("new@test.ru")
                .phone("+7 999 000-00-00")
                .build();
        mockMvc.perform(put("/api/customers/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое имя"));

        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(jsonPath("$.name").value("Новое имя"));
    }

    @Test
    void deleteCustomer() throws Exception {
        Customer saved = customerRepository.save(Customer.builder()
                .name("На удаление")
                .build());
        Long id = saved.getId();

        mockMvc.perform(delete("/api/customers/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/customers/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getInvoices_empty() throws Exception {
        Customer saved = customerRepository.save(Customer.builder()
                .name("Клиент без счетов")
                .build());
        mockMvc.perform(get("/api/customers/" + saved.getId() + "/invoices"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
