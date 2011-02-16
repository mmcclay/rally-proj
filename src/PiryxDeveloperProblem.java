import find.DonorFinder;
import find.FileDonorFinder;
import model.Donation;
import util.ApplicationProperties;
import util.DonationUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Point of entry class into application
 *
 * @author Mike McClay
 */
public class PiryxDeveloperProblem {

    ///////////////////////////////////////
    // ATTRIBUTES
    private static final String USAGE = "Usage: PiryxDeveloperProblem";
    private static final String EXIT_KEYWORD = "quit";
    private static final String PROPERTIES_PATH = "./resources/application.properties";
    private static final String COMMAND_LINE_USAGE = "Enter a csv donor, type '" + EXIT_KEYWORD + "' (no quotes) to stop";
    private static final String COMMAND_LINE_ERROR_USAGE =
            "We did not understand your input, please use a format similar to: " +
            "John,San Francisco,California,94123,415-533-6439,j@gmail.com,100,Amex,CauseX,CampaignX,auth,Should be sent";



    ////////////////////////////////////////
    // MAIN METHOD
    public static void main(String[] args) {

        // make sure inputs to test are ok
        if (args.length != 0) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // load the properties to be used
        ApplicationProperties.loadProperties(PROPERTIES_PATH);

        // get the configured donor finder
        DonorFinder donorFinder = getDonorFinder();

        // load the donors
        donorFinder.loadDonors();

        DonationUtils donationUtils = DonationUtils.getInstance();

        System.out.println(COMMAND_LINE_USAGE);
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
        String line = null;
        try {
            while (!EXIT_KEYWORD.equals(line)){
                line = br.readLine();

                if (!EXIT_KEYWORD.equals(line)){
                    // read donation from line
                    try {
                        Donation donation = Donation.parseDonationFromLine(line);
                        donationUtils.handleNewDonation(donation, donorFinder);
                    }
                    catch (Exception e) {
                        System.out.println(COMMAND_LINE_ERROR_USAGE);
                    }
                    System.out.println(COMMAND_LINE_USAGE);

                }
            }
            System.out.println("Thank you for using the system, good bye.");
        }
        catch (Exception e) {
            System.out.println("Exception reading line: " + e);
        }
    }

    /**
     * Factory for the configured donor finder
     * @return
     */
    private static DonorFinder getDonorFinder() {
        String donorFinderType = ApplicationProperties.getPropertyValue("donorLoaderType");
        if ("F".equals(donorFinderType)) {
            return FileDonorFinder.getInstance();
        }

        System.out.println("Invalid donor loader type specified in properties: " + donorFinderType);
        return null;
    }
}
