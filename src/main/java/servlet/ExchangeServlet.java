package servlet;

import dao.CurrenciesDao;
import dao.ExchangeRatesDao;
import dto.ExchangeRateDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;
import util.ServletErrorMessage;
import util.ServletJsonResponse;
import util.ServletUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet("/exchange/*")
public class ExchangeServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amountParam = request.getParameter("amount");

        CurrenciesDao currenciesDao = new CurrenciesDao();
        ExchangeRatesDao exchangeRatesDao = new ExchangeRatesDao();

        if (from == null || to == null || amountParam == null) {
            ServletJsonResponse.send(response, 400,
                                   new ServletErrorMessage("Отсутствует нужное поле формы"));
            return;
        }

        if (ServletUtil.exchangeRateExists(from, to)) {
            BigDecimal rate = exchangeRatesDao.get(currenciesDao.get(from).getId(),
                                                   currenciesDao.get(to).getId()).getRate();
            BigDecimal amount = new BigDecimal(amountParam);
            BigDecimal convertedAmount = rate.multiply(amount);
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(currenciesDao.get(from),
                                                                  currenciesDao.get(to),
                                                                  rate, amount, convertedAmount);
            ServletJsonResponse.send(response, 200, exchangeRateDto);
        } else if (ServletUtil.exchangeRateExists(to, from)) {
            ExchangeRate exchangeRate = exchangeRatesDao.get(currenciesDao.get(to).getId(),
                    currenciesDao.get(from).getId());
            BigDecimal rate = new BigDecimal(1).divide(exchangeRate.getRate(), 3, RoundingMode.HALF_UP);
            BigDecimal amount = new BigDecimal(amountParam);
            BigDecimal convertedAmount = rate.multiply(amount);
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(currenciesDao.get(to),
                    currenciesDao.get(from),
                    rate, amount, convertedAmount);
            ServletJsonResponse.send(response, 200, exchangeRateDto);
        }
    }
}
