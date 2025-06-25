package domain;

public record Hotel(String name, String key, int priceMin, int priceMax, double rating, String accommodationType,
                    String url, String city) {
}