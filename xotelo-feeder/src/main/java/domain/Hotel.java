package domain;

public class Hotel {
    private String name;
    private String key;
    private String accommodationType;
    private String url;
    private double rating;
    private int priceMin;
    private int priceMax;
    private String city;

    public Hotel(String name, String key, int priceMin, int priceMax,
                 double rating, String accommodationType, String url, String city) {
        this.name = name;
        this.key = key;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.rating = rating;
        this.url = url;
        this.accommodationType = accommodationType;
        this.city = city;
    }

    public String getName() { return name; }

    public String getKey() { return key; }

    public String getAccommodationType() { return accommodationType; }

    public String getUrl() { return url; }

    public double getRating() { return rating; }

    public int getPriceMin() { return priceMin; }

    public int getPriceMax() { return priceMax; }

    public String getCity() { return city; }
}


