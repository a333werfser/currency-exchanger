package util;

import dao.CurrenciesDao;
import dao.ExchangeRatesDao;
import jakarta.servlet.http.HttpServletRequest;
import models.Currency;
import models.ExchangeRate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServletUtil {

    public static String getLastURISegment(HttpServletRequest request) {
        String[] uriSegments = request.getRequestURI().split("/");
        return uriSegments[uriSegments.length - 1];
    }

    public static boolean codesNotExist(String baseCurrencyCode, String targetCurrencyCode) {
        List<String> codes = new CurrenciesDao().getAllCodes();
        return !codes.contains(baseCurrencyCode) || !codes.contains(targetCurrencyCode);
    }

    public static boolean exchangeRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        boolean exists = false;
        if (codesNotExist(baseCurrencyCode, targetCurrencyCode)) {
            return exists;
        }
        CurrenciesDao currenciesDao = new CurrenciesDao();
        int baseCurrencyId = currenciesDao.get(baseCurrencyCode).getId();
        int targetCurrencyId = currenciesDao.get(targetCurrencyCode).getId();

        for (ExchangeRate exchangeRate : new ExchangeRatesDao().getAll()) {
            if (exchangeRate.getBaseCurrency().getId() == baseCurrencyId &&
            exchangeRate.getTargetCurrency().getId() == targetCurrencyId) {
                exists = true;
                break;
            }
        }
        return exists;
    }

    public static ExchangeRate[] getSimilarExchangeRates(String code1, String code2) {
        if (codesNotExist(code1, code2)) {
            return null;
        }

        ExchangeRate[] array = new ExchangeRate[2];

        List<ExchangeRate> someExchangeRates = new ArrayList<>();

        for (ExchangeRate exchangeRate : new ExchangeRatesDao().getAll()) {
            if (exchangeRate.getTargetCurrency().getCode().equals(code1) ||
                exchangeRate.getTargetCurrency().getCode().equals(code2)) {
                someExchangeRates.add(exchangeRate);
            }
        }

        for (int i = 0; i < someExchangeRates.size(); i++) {
            Currency baseCurrency = someExchangeRates.get(i).getBaseCurrency();

            for (int j = 0; j < someExchangeRates.size(); j++) {
                int index = 0;
                if (i != j) {
                    Currency baseCurrency2 = someExchangeRates.get(j).getBaseCurrency();
                    if (baseCurrency.getCode().equals(baseCurrency2.getCode())) {
                        array[index++] = someExchangeRates.get(i);
                        array[index] = someExchangeRates.get(j);
                    }
                }
            }
        }
        return array;
    }
}
