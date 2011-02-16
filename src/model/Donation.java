package model;

/**
 * Class representing Donation model
 *
 * @author Mike McClay
 */
public class Donation {

    /////////////////////////////////////////////////
    // ATTRIBUTES

    /**
     * Information collected when a donation is made
     */
    private String name;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String email;
    private float donationAmount;
    private String creditCardType;
    private String causeName;
    private String campaignName;
    private String facebookAuth;
    private String comment;


    /////////////////////////////////////////////////
    // CONSTRUCTOR

    /**
     * Single constructor
     *
     * @param name
     * @param city
     * @param state
     * @param zip
     * @param email
     * @param donationAmount
     * @param creditCardType
     * @param causeName
     * @param campaignName
     * @param facebookAuth
     * @param comment
     */
    public Donation(String name,
                 String city,
                 String state,
                 String zip,
                 String phone,
                 String email,
                 float donationAmount,
                 String creditCardType,
                 String causeName,
                 String campaignName,
                 String facebookAuth,
                 String comment) {
        this.name = name;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
        this.donationAmount = donationAmount;
        this.creditCardType = creditCardType;
        this.causeName = causeName;
        this.campaignName = campaignName;
        this.facebookAuth = facebookAuth;
        this.comment = comment;
    }

    /////////////////////////////////////////////////
    // METHODS - Simple Getters and toString()

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public float getDonationAmount() {
        return donationAmount;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public String getCauseName() {
        return causeName;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getFacebookAuth() {
        return facebookAuth;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Donation{" +
                "name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", email='" + email + '\'' +
                ", donationAmount=" + donationAmount +
                ", creditCardType='" + creditCardType + '\'' +
                ", causeName='" + causeName + '\'' +
                ", campaignName='" + campaignName + '\'' +
                ", facebookAuth='" + facebookAuth + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    /**
     * Takes a given string input line and creates a donation
     *
     * @param line comma separated attributes
     * @return new donation
     */
    public static Donation parseDonationFromLine(String line) {
        String[] attributes = line.split(",");

        // parse the donation amount
        float donationAmount = 0;
        try {
            donationAmount = Float.parseFloat(attributes[6]);
        }
        catch (Exception e) {
            System.out.println("Could not parse as float: " + attributes[6]);
            // continue anyway because we do not care about donation amounts right now anyhow
        }

        Donation donation = new Donation(attributes[0],
                                         attributes[1],
                                         attributes[2],
                                         attributes[3],
                                         attributes[4],
                                         attributes[5],
                                         donationAmount,
                                         attributes[7],
                                         attributes[8],
                                         attributes[9],
                                         attributes[10],
                                         attributes[11]);

        System.out.println("New donation received: " + donation);

        return donation;
    }
}
