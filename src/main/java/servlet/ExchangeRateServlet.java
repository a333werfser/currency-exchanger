package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrenciesDao;
import dao.ExchangeRatesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;
import util.ServletErrorMessage;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String lastSegment = getLastURISegment(req.getRequestURI());
        try (PrintWriter out = resp.getWriter()){
            if (lastSegment.length() != 6) {
                resp.setStatus(400);
                resp.setContentType("application/json");
                out.print(new ObjectMapper().writeValueAsString(new ServletErrorMessage(
                        "Коды валют пары отсутствуют в адресе")));
            } else if (isExchangeRateExists(lastSegment)) {
                resp.setStatus(200);
                resp.setContentType("application/json");
                String baseCurrencyCode = lastSegment.substring(0, 3);
                String targetCurrencyCode = lastSegment.substring(3, 6);
                out.print(new ObjectMapper().writeValueAsString(new ExchangeRatesDao().get(
                        new CurrenciesDao().get(baseCurrencyCode).getId(),
                        new CurrenciesDao().get(targetCurrencyCode).getId()
                )));
            } else {
                resp.setStatus(404);
                resp.setContentType("application/json");
                out.print(new ObjectMapper().writeValueAsString(new ServletErrorMessage(
                        "Обменный курс для пары не найден")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getLastURISegment(String URI) {
        String[] uriSegments = URI.split("/");
        return uriSegments[uriSegments.length - 1];
    }

    public boolean isExchangeRateExists(String lastSegment) {
        boolean isExists = false;
        CurrenciesDao currenciesDao = new CurrenciesDao();
        ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();
        String baseCurrencyCode = lastSegment.substring(0, 3);
        String targetCurrencyCode = lastSegment.substring(3, 6);
        List<String> allCodes = currenciesDao.getAllCodes();
        int baseCurrencyId;
        int targetCurrencyId;
        if (allCodes.contains(baseCurrencyCode) && allCodes.contains(targetCurrencyCode)) {
            baseCurrencyId = currenciesDao.get(baseCurrencyCode).getId();
            targetCurrencyId = currenciesDao.get(targetCurrencyCode).getId();
        } else {
            return false;
        }
        Map<Integer, Integer> idPairs = exchangeRatesDao.getAllIdPairs();
        for (Entry<Integer, Integer> entry : idPairs.entrySet()) {
            isExists = entry.getKey() == baseCurrencyId && entry.getValue() == targetCurrencyId;
            break;
        }
        return isExists;
    }
}
