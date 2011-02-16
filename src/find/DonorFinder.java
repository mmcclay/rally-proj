package find;

import model.Donation;

import java.util.List;
import java.util.Set;

/**
 * Interface for loading donors
 */
public interface DonorFinder {

    /**
     * Loads donors from various sources (db, file, etc)
     */
    void loadDonors();

    /**
     * Gets all donor emails
     *
     * @return Set of donor emails
     */
    Set<String> getDonorEmailSet();

    /**
     * Get all donations made by email
     *
     * @param email
     * @return List of donations made by email address
     */
    List<Donation> getDonationsForEmail(String email);

    /**
     * Adds a new donation that has come in
     * 
     * @param donation
     */
    void addDonation(Donation donation);
}
