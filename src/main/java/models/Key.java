package models;

/**
 * Created by Maxime on 19/11/2015.
 */

public class Key {

    private String value;
    private Integer rateLimit;

    public Key(String aValue, Integer aRateLimit){
        value = aValue;
        rateLimit = aRateLimit;
    }

    public String getValue(){
        return value;
    }

    public Integer getRateLimit() {
        return rateLimit;
    }
}
