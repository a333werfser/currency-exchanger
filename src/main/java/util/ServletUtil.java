package util;

import dao.CurrenciesDao;
import dao.ExchangeRatesDao;
import jakarta.servlet.http.HttpServletRequest;
import models.ExchangeRate;

import java.util.List;

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
}
