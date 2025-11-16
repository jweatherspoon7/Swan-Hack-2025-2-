package swanhack.EveryOrg;

public class Charity {

    private String ein;
    private String name;
    private String description;
    private String logoUrl;

    // extra detail fields
    private String website;
    private String donationUrl;
    private String city;
    private String state;

    public Charity() {}

    public Charity(String ein, String name, String description, String logoUrl) {
        this.ein = ein;
        this.name = name;
        this.description = description;
        this.logoUrl = logoUrl;
    }

    public String getEin() { return ein; }
    public void setEin(String ein) { this.ein = ein; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public String getDonationUrl() { return donationUrl; }
    public void setDonationUrl(String donationUrl) { this.donationUrl = donationUrl; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}