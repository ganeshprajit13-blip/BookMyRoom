package com.library.ebook.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/ebooks returns list of e-books")
    void testGetEbooks() throws Exception {
        mockMvc.perform(get("/api/ebooks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/contacts returns contacts list")
    void testGetContacts() throws Exception {
        mockMvc.perform(get("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/ebooks/licenses returns active licenses")
    void testGetLicenses() throws Exception {
        mockMvc.perform(get("/api/ebooks/licenses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/reports/dashboard returns comprehensive municipal statistics")
    void testGetDashboardStats() throws Exception {
        mockMvc.perform(get("/api/reports/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEbooks").exists())
                .andExpect(jsonPath("$.activeLicenses").exists())
                .andExpect(jsonPath("$.totalPatrons").exists())
                .andExpect(jsonPath("$.activeCheckouts").exists())
                .andExpect(jsonPath("$.digitalAssetValue").exists())
                .andExpect(jsonPath("$.budgetUtilization").exists());
    }

    @Test
    @DisplayName("GET /api/reports/balance-sheet returns balance sheet accounts")
    void testGetBalanceSheet() throws Exception {
        mockMvc.perform(get("/api/reports/balance-sheet")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAssets").exists())
                .andExpect(jsonPath("$.totalLiabilities").exists());
    }

    @Test
    @DisplayName("GET /api/reports/profit-loss returns profit and loss accounts")
    void testGetProfitAndLoss() throws Exception {
        mockMvc.perform(get("/api/reports/profit-loss")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncome").exists())
                .andExpect(jsonPath("$.totalExpenses").exists())
                .andExpect(jsonPath("$.netProfitOrLoss").exists());
    }

    @Test
    @DisplayName("POST /api/ebooks/access-keys/revoke-expired revokes expired keys")
    void testRevokeExpiredAccessKeysEndpoint() throws Exception {
        mockMvc.perform(post("/api/ebooks/access-keys/revoke-expired")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
