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
import util.ServletUtil;

import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<ExchangeRate> exchangeRates = new ExchangeRatesDao().getAll();
        ServletJsonResponse.send(response, 200, exchangeRates);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String baseCurrencyCode = request.getParameter("baseCurrencyCode");
        String targetCurrencyCode = request.getParameter("targetCurrencyCode");
        String rate = request.getParameter("rate");

        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            ServletJsonResponse.send(response, 400, new ServletErrorMessage(
                    "Отсутствует нужное поле формы"));
        } else if (ServletUtil.codesNotExist(baseCurrencyCode, targetCurrencyCode)) {
            ServletJsonResponse.send(response, 404, new ServletErrorMessage(
                    "Одна (или обе) валюты из валютной пары не существуют в БД"));
        } else if (ServletUtil.exchangeRateExists(baseCurrencyCode, targetCurrencyCode)) {
            ServletJsonResponse.send(response, 409, new ServletErrorMessage(
                    "Валютная пара с такими кодами уже существует"));
        } else {
            CurrenciesDao currenciesDao = new CurrenciesDao();
            ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();

            exchangeRatesDao.add(new ExchangeRate(
                    currenciesDao.get(baseCurrencyCode),
                    currenciesDao.get(targetCurrencyCode),
                    new BigDecimal(rate)));
            ExchangeRate addedExchangeRate = exchangeRatesDao.get(
                    currenciesDao.get(baseCurrencyCode).getId(),
                    currenciesDao.get(targetCurrencyCode).getId());

            ServletJsonResponse.send(response, 201, addedExchangeRate);
        }
    }
}
