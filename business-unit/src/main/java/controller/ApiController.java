package controller;

import io.javalin.Javalin;
import service.AnalysisService;
import java.sql.SQLException;

public class ApiController {
    public ApiController(Javalin app, AnalysisService service) {
        app.get("/recommendations/{city}", ctx -> {
            try {
                String city = ctx.pathParam("city");
                ctx.json(service.getTravelPackages(city));
            } catch (SQLException e) {
                ctx.status(500).json("Error al acceder al datamart: " + e.getMessage());
            }
        });
    }
}