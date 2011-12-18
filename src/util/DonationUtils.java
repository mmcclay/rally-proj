package util;

import find.DonorFinder;
import model.Donation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Donation utililty class
 */
public class DonationUtils {

    //////////////////////////////////
    // ATTRIBUTEs

    /**
     * Singleton instance
     */
    private static final DonationUtils instance = new DonationUtils();

    /**
     * Map holding count of messages sent to each user
     */
    private static final Map<String, Integer> emailToMessagesSentCountMap = new HashMap<String, Integer>();

    /**
     * Constants for high \ low relevance
     */
    private static final int RELEVANCE_LOW = 30;
    private static final int RELEVANCE_HIGH = 80;
    private static final int LOW_MESSAGE_THRESHOLD = 3;
    private static final int HIGH_MESSAGE_THRESHOLD = 7;

    //////////////////////////////////
    // METHODS

    public static DonationUtils getInstance() {
        return instance;
    }


    /**
     * Handles a new donation by adding it to the list of donations
     * and considering who to send messages to
     *
     * @param newDonation
     * @param donorFinder
     */
    public void handleNewDonation(Donation newDonation, DonorFinder donorFinder) {
        // test
        DonationUtils donationUtils = DonationUtils.getInstance();

        Set<String> donorEmailSet = donorFinder.getDonorEmailSet();
        for (String donorEmail : donorEmailSet) {

            if (donorEmail.equals(newDonation.getEmail())) {
                // new donor email is the same, ignore this one
                continue;
            }

            System.out.println("EVALUATING DONATIONS BY " + donorEmail.toUpperCase());
            List<Donation> donationList = donorFinder.getDonationsForEmail(donorEmail);

            int maxRelevance = 0;
            for (Donation donation : donationList) {
                // for now, just make the group relevance the max relevance of all donations
                // for this donor.  Better might be to weight relevance by age of the donation
                // i.e. any donations within last x days are weighted more than those older than x days.
                // or come up with a smart way to weight based off of other relevances in group
                // Note: I dont think averaging straight away is good. Consider a donor who has made
                // 2 donations, 1 has relevancy 0, the other 100%. Averaging would give a 50% relevancy
                // which would imply not a great relevancy, when in fact it is probably very relevant                
                maxRelevance = Math.max(donationUtils.computeRelevance(donation, newDonation),
                                        maxRelevance);
            }

            // determine whether we should send the message
            boolean shouldSendMessage =
                        donationUtils.shouldSendMessage(donorEmail, maxRelevance);

            // send message with new donation comment if ok to do so
            if (shouldSendMessage) {
                donationUtils.sendMessage(donorEmail, newDonation.getComment());
            }
            else {
                System.out.println("NOT SENDING MESSAGE TO " + donorEmail);
            }

            System.out.println();

        }
        donorFinder.addDonation(newDonation);
    }

    /**
     * Computes the relevancy between two donations
     * @param donation1
     * @param donation2
     * @return relevancy
     */
    private int computeRelevance(Donation donation1,
                                Donation donation2) {

        // get all match booleans. assume all are not null for simplicity
        boolean cityMatch = donation1.getCity().equals(donation2.getCity());
        boolean zipMatch = donation1.getZip().equals(donation2.getZip());
        boolean stateMatch = donation1.getState().equals(donation2.getState());
        boolean campaignMatch = donation1.getCampaignName().equals(donation2.getCampaignName());
        boolean causeMatch = donation1.getCauseName().equals(donation2.getCauseName());

        // Keep track of max to make refactoring easier
        // order of logic can be changed below, values can be swapped etc,
        // but logic will still find max. No dependence on logic order 

        int maxRelevancy = 0;
        if (zipMatch) {
            // zip match
            maxRelevancy = Math.max(maxRelevancy, 57);

            if (campaignMatch) {
                // zip and campaign match
                maxRelevancy = Math.max(maxRelevancy, 88);
            }

            if (causeMatch) {
                // zip and cause match
                maxRelevancy = Math.max(maxRelevancy, 74);
            }
        }

        if (cityMatch) {
            // city match
            maxRelevancy = Math.max(maxRelevancy, 44);

            if (campaignMatch) {
                // city and campaign match
                maxRelevancy = Math.max(maxRelevancy, 81);
            }

            if (causeMatch) {
                // city and cause match
                maxRelevancy = Math.max(maxRelevancy, 71);
            }
        }

        if (stateMatch) {
            maxRelevancy = Math.max(maxRelevancy, 22);
        }

        if (causeMatch) {
            maxRelevancy = Math.max(maxRelevancy, 24);
        }

        if (campaignMatch) {
            maxRelevancy = Math.max(maxRelevancy, 43);
        }

        System.out.println("Computed relevance " + maxRelevancy + " with " + donation1);

        return maxRelevancy;
    }

    /**
     * Uses business logic to determine if sending message is a good idea
     *
     * @param donorEmail
     * @param relevance
     * @return
     */
    private boolean shouldSendMessage(String donorEmail, int relevance) {

        int pUnsubscribe = 0;
        int pDonate = 0;

        // use relevance to compute initial probability values
        if (relevance < RELEVANCE_LOW) {
            pUnsubscribe = 5;
            pDonate = 1;
        }
        else if (relevance < RELEVANCE_HIGH) {
            pUnsubscribe = 3;
            pDonate = 2;
        }
        else {
            pUnsubscribe = 1;
            pDonate = 4;
        }

        // add in the multiple email affect
        int currentMessageCount = getNumMessagesInLastDay(donorEmail);

        currentMessageCount++; // increment to test effects of this new message
        if (currentMessageCount <= LOW_MESSAGE_THRESHOLD) {
            pUnsubscribe += 5;
        }
        else if (currentMessageCount <= HIGH_MESSAGE_THRESHOLD) {
            pUnsubscribe += 7;
        }
        else {
            pUnsubscribe += 9;
        }

        // offset compute this based off of the middle odds:
        // relevance between 30 and 80, message count between 3 and 8 gives p(U) = 10 and p(D) = 2
        int offset = 10 - 2;

        System.out.println("Using Relevance " + relevance + ", numMessages: " + currentMessageCount + ", calculated: " +
                           "P(U)=" + pUnsubscribe + ", P(D)=" + pDonate + ".");

        // could make this more complex by incorporating donator's donation amounts
        return (pDonate + offset) > pUnsubscribe;
    }

    /**
     * Gets the number of messages posted to donor in last day
     *
     * @param email
     * @return  number of messages sent in last day
     */
    private int getNumMessagesInLastDay(String email) {
        // fully implemented, this would query the database:
        // "select count(*) from messages where email = 'email' and create_date > current date - 1 day
        // for now though just use whatever is in the map and assume that it is everything within the last day

        if (emailToMessagesSentCountMap.containsKey(email)) {
            return emailToMessagesSentCountMap.get(email);
        }
        else {
            return 0;
        }
    }

    /**
     * Sends message to user and increments count
     *
     * @param email
     * @param message
     */
    private void sendMessage(String email, String message) {

        // SEND MESSAGE
        System.out.println("SENDING MESSAGE TO " + email.toUpperCase() + ": " + message);

        // increment message sent count
        int currentMessageCount = getNumMessagesInLastDay(email);

        emailToMessagesSentCountMap.put(email, currentMessageCount + 1);
    }
}
