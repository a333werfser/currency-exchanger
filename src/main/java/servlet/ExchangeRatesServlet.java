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
import util.ServletJsonResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<ExchangeRate> exchangeRates = new ExchangeRatesDao().getAll();
        try (PrintWriter out = resp.getWriter()){
            resp.setStatus(200);
            resp.setContentType("application/json");
            out.print(new ObjectMapper().writeValueAsString(exchangeRates));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");

        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            ServletJsonResponse.send(response, 400, new ServletErrorMessage(
                    "Отсутствует нужное поле формы"));
        } else if (codesNotExist(baseCurrencyCode, targetCurrencyCode)) {
            ServletJsonResponse.send(response, 404, new ServletErrorMessage(
                    "Одна (или обе) валюты из валютной пары не существуют в БД"));
        } else if (exchangeRateExists(baseCurrencyCode, targetCurrencyCode)) {
            ServletJsonResponse.send(response, 409, new ServletErrorMessage(
                    "Валютная пара с такими кодами уже существует"));
        } else {
            new ExchangeRatesDao().add(new ExchangeRate(
                    new CurrenciesDao().get(baseCurrencyCode),
                    new CurrenciesDao().get(targetCurrencyCode),
                    new BigDecimal(rate)));
            ExchangeRate exchangeRate = new ExchangeRatesDao().get(
                    new CurrenciesDao().get(baseCurrencyCode).getId(),
                    new CurrenciesDao().get(targetCurrencyCode).getId());
            ServletJsonResponse.send(response, 201, exchangeRate);
        }
    }

    public boolean codesNotExist(String baseCurrencyCode, String targetCurrencyCode) {
        List<String> codes = new CurrenciesDao().getAllCodes();
        return !codes.contains(baseCurrencyCode) || !codes.contains(targetCurrencyCode);
    }

    public boolean exchangeRateExists(String baseCurrencyCode, String targetCurrencyCode) {
        boolean exists = false;
        if (codesNotExist(baseCurrencyCode, targetCurrencyCode)) {
            return exists;
        }
        CurrenciesDao currenciesDao = new CurrenciesDao();
        int baseCurrencyId = currenciesDao.get(baseCurrencyCode).getId();
        int targetCurrencyId = currenciesDao.get(targetCurrencyCode).getId();

        for (Map.Entry<Integer, Integer> entry : new ExchangeRatesDao().getAllIdPairs().entrySet()) {
            if (entry.getKey() == baseCurrencyId && entry.getValue() == targetCurrencyId) {
                exists = true;
                break;
            }
        }
        return exists;
    }
}
