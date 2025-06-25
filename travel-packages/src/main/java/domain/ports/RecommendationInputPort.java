package domain.ports;

import domain.model.Recommendation;
import java.sql.SQLException;
import java.util.List;

public interface RecommendationInputPort {
    List<Recommendation> getTravelPackages(String city) throws SQLException;
}