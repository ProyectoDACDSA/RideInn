package api;

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
    public void setName(String name) { this.name = name; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getAccommodationType() { return accommodationType; }
    public void setAccommodationType(String accommodationType) { this.accommodationType = accommodationType; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getPriceMin() { return priceMin; }
    public void setPriceMin(int priceMin) { this.priceMin = priceMin; }

    public int getPriceMax() { return priceMax; }
    public void setPriceMax(int priceMax) { this.priceMax = priceMax; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}


