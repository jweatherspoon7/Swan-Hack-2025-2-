package swanhack.EveryOrg;

public class Charity {

    private String ein;
    private String name;
    private String description;
    private String logoUrl;

    // extra detail fields
    private String website;
    private String everyOrgUrl;
    private String location;

    public Charity() {}

    public Charity(String ein, String name, String description, String logoUrl, String location, String website, String everyOrgUrl) {
        this.ein = ein;
        this.name = name;
        this.description = description;
        this.logoUrl = logoUrl;
        this.location = location;
        this.website = website;
        this.everyOrgUrl = everyOrgUrl;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEveryOrgUrl() {
        return everyOrgUrl;
    }

    public void setEveryOrgUrl(String everyOrgUrl) {
        this.everyOrgUrl = everyOrgUrl;
    }
}