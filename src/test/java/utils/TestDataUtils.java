package utils;

import java.util.Random;

public class TestDataUtils {

    private static final Random RND = new Random();

    public static final String NAME            = "Rahul Sharma";
    public static final String DOB             = "01/15/1995";
    public static final String PAN             = "ABCDE1234F";
    public static final String AADHAAR         = randomDigits(12);
    public static final String MOBILE          = "9" + randomDigits(9);
    public static final String EMAIL           = "user" + randomDigits(4) + "@testmail.com";
    public static final String REG_NUMBER      = "UP34AD" + (1000 + RND.nextInt(9000));
    public static final String MODEL           = randomModel();
    public static final String UPI_ID          = "testuser" + randomDigits(3) + "@upi";
    public static final String NOMINEE_NAME    = "Priya Sharma";
    public static final String NOMINEE_DOB     = "03/20/1998";
    public static final String NOMINEE_AADHAAR = randomDigits(12);

    public static int randomIndex(int size) {
        return 1 + RND.nextInt(size - 1);
    }

    private static String randomDigits(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(RND.nextInt(10));
        return sb.toString();
    }

    private static String randomModel() {
        String[] models = {"Honda City", "Maruti Swift", "Hyundai Creta", "Tata Nexon", "Toyota Innova"};
        return models[RND.nextInt(models.length)];
    }
}
