package dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {
    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(new CurrencyDao().getAllCodes());
        System.out.println(new CurrencyDao().getAllCodes().contains("EUR"));
        System.out.println(new ObjectMapper().writeValueAsString(new CurrencyDao().get("EUR")));
    }
}
