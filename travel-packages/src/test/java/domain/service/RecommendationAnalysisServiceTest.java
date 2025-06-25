package domain.service;

import domain.model.Recommendation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RecommendationAnalysisServiceTest {

    private RecommendationAnalysisService analysisService;

    @BeforeEach
    void setUp() {
        analysisService = new RecommendationAnalysisService();
    }

    @Test
    void testGetTravelPackages_WithNoResults() throws SQLException {
        String city = "CiudadInventadaSinDatos";

        List<Recommendation> recommendations = analysisService.getTravelPackages(city);

        assertNotNull(recommendations, "La lista de recomendaciones no debe ser nula");
        assertTrue(recommendations.isEmpty(), "La lista debe estar vacía si no hay viajes u hoteles para la ciudad");
    }
}