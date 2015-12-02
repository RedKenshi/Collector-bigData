package models;

/**
 * Created by Maxime on 19/11/2015.
 */

public class Key {

    private String value;
    private Integer rateLimit;
    private String purpose;

    public Key(String aValue, Integer aRateLimit, String aPurpose){
        value = aValue;
        rateLimit = aRateLimit;
        purpose = aPurpose;
    }

    public void showDetails(){
        System.out.println("Clé de valeur : " + value + ", rôle : " + purpose);
    }

    public String getPurpose() {
        return purpose;
    }

    public String getValue(){
        return value;
    }

    public Integer getRateLimit() {
        return rateLimit;
    }
}
