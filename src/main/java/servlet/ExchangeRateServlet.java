package servlet;

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

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String lastSegment = ServletUtil.getLastURISegment(request);
        if (lastSegment.equals("exchangeRate")) {
            ServletJsonResponse.send(response, 400, new ServletErrorMessage(
                    "Коды валют пары отсутствуют в адресе"));
            return;
        }
        String baseCurrencyCode = lastSegment.substring(0, 3);
        String targetCurrencyCode = lastSegment.substring(3, 6);

        if (!ServletUtil.exchangeRateExists(baseCurrencyCode, targetCurrencyCode)) {
            ServletJsonResponse.send(response, 404, new ServletErrorMessage(
                    "Обменный курс для пары не найден"));
        } else {
            CurrenciesDao currenciesDao = new CurrenciesDao();
            ExchangeRate exchangeRate = new ExchangeRatesDao().get(
                    currenciesDao.get(baseCurrencyCode).getId(),
                    currenciesDao.get(targetCurrencyCode).getId()
            );
            ServletJsonResponse.send(response, 200, exchangeRate);
        }
    }
}
