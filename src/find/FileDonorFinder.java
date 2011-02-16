package find;

import model.Donation;
import util.ApplicationProperties;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/**
 * Implementation of FileDonorFinder which loads donors from file
 */
public class FileDonorFinder implements DonorFinder {

    ///////////////////////////////////////////////////////
    // ATTRIBUTES

    // file name property
    private static final String FILE_PROP = "fileForFileDonorLoader";

    /**
     * Singleton instance
     */
    private static FileDonorFinder instance = new FileDonorFinder();

    /**
     * Map to hold donor information
     */
    private Map<String, List<Donation>> emailToDonationListMap =
                                        new HashMap<String,List<Donation>>();

    ///////////////////////////////////////////////////////
    // METHODS

    /**
     * Getter for singleton instance
     *
     * @return instance
     */
    public static final FileDonorFinder getInstance() {
        return instance;
    }

    /**
     * Loads donors from file
     */
    public void loadDonors() {
        String fileName = ApplicationProperties.getPropertyValue(FILE_PROP);

        try {
            BufferedReader bis = new BufferedReader(new FileReader(fileName));
            String line;
            Donation donation;
            while ((line = bis.readLine()) != null) {
                if (line.length() == 0) {
                    continue;
                }

                // create a new donation from the read line
                donation = Donation.parseDonationFromLine(line);

                addDonation(donation);
            }
        }
        catch (Exception e) {
            System.out.println("Exception loading file[" + fileName + "]: " + e);
        }
    }

    /**
     * Gets all donor emails
     *
     * @return Set of donor emails
     */
    public Set<String> getDonorEmailSet() {
        return emailToDonationListMap.keySet();
    }

    /**
     * Get all donations made by email
     *
     * @param email
     * @return List of donations made by email address
     */
    public List<Donation> getDonationsForEmail(String email) {
        return emailToDonationListMap.get(email);
    }

    /**
     * Adds a new donation
     *
     * Synchronized in case multiple threads attempt at same time
     *
     * @param donation
     */
    public synchronized void addDonation(Donation donation) {
        // if this is the first donotion by the donation, create a new list entry in the map for it
        if (!emailToDonationListMap.containsKey(donation.getEmail())) {
            emailToDonationListMap.put(donation.getEmail(), new ArrayList<Donation>());
        }

        // get the donation list out for this email
        List<Donation> donationList = emailToDonationListMap.get(donation.getEmail());

        // add donation
        donationList.add(donation);

        /*** Add donation to file ***/
    }

}
