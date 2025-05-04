package model;

public class Hotel {
    private final String name;
    private final String key;
    private final String accommodation_type;
    private final String url;
    private final ReviewSummary review_summary;
    private final PriceRanges price_ranges;
    private final Geo geo;
    private String city;

    public Hotel(String key, String name, String accommodation_type, String url,
                 ReviewSummary review_summary, PriceRanges price_ranges, Geo geo, String city) {
        this.key = key;
        this.name = name;
        this.accommodation_type = accommodation_type;
        this.url = url;
        this.review_summary = review_summary;
        this.price_ranges = price_ranges;
        this.geo = geo;
        this.city = city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {return city;}
    public String getName() { return name; }
    public String getKey() { return key; }
    public String getAccommodationType() { return accommodation_type; }
    public String getUrl() { return url; }
    public double getRating() { return review_summary != null ? review_summary.rating : 0.0; }
    public int getReviewCount() { return review_summary != null ? review_summary.count : 0; }
    public double getMinPrice() { return price_ranges != null ? price_ranges.minimum : 0.0; }
    public double getMaxPrice() { return price_ranges != null ? price_ranges.maximum : 0.0; }
    public double getLatitude() { return geo != null ? geo.latitude : 0.0; }
    public double getLongitude() { return geo != null ? geo.longitude : 0.0; }

    private static class ReviewSummary {
        private double rating;
        private int count;
    }

    private static class PriceRanges {
        private double maximum;
        private double minimum;
    }

    private static class Geo {
        private double latitude;
        private double longitude;
    }
}

