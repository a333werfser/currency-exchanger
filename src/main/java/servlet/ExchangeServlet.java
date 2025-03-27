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
            return;
        }
        else if (ServletUtil.exchangeRateExists(to, from)) {
            ExchangeRate exchangeRate = exchangeRatesDao.get(currenciesDao.get(to).getId(),
                    currenciesDao.get(from).getId());
            BigDecimal rate = new BigDecimal(1).divide(exchangeRate.getRate(), 3, RoundingMode.HALF_UP);
            BigDecimal amount = new BigDecimal(amountParam);
            BigDecimal convertedAmount = rate.multiply(amount);
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(currenciesDao.get(to),
                    currenciesDao.get(from),
                    rate, amount, convertedAmount);
            ServletJsonResponse.send(response, 200, exchangeRateDto);
            return;
        }

        ExchangeRate[] similarExchangeRates = exchangeRatesDao.getSimilarExchangeRates(from, to);

        if (similarExchangeRates != null) {
            ExchangeRate exchangeRateWithBaseCurrency = null;
            ExchangeRate exchangeRateWithTargetCurrency = null;

            for (ExchangeRate exchangeRate : similarExchangeRates) {
                if (exchangeRate.getTargetCurrency().getCode().equals(from))
                    exchangeRateWithBaseCurrency = exchangeRate;
                else
                    exchangeRateWithTargetCurrency = exchangeRate;
            }

            BigDecimal rate = calculateRate(exchangeRateWithBaseCurrency, exchangeRateWithTargetCurrency);
            BigDecimal amount = new BigDecimal(amountParam);
            BigDecimal convertedAmount = rate.multiply(amount);

            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(exchangeRateWithBaseCurrency.getTargetCurrency(),
                    exchangeRateWithTargetCurrency.getTargetCurrency(), rate, amount, convertedAmount);
            ServletJsonResponse.send(response, 200, exchangeRateDto);
        }
    }

    private BigDecimal calculateRate(ExchangeRate exchangeRate1, ExchangeRate exchangeRate2) {
        BigDecimal calculatedRate;
        BigDecimal rate1 = exchangeRate1.getRate();
        BigDecimal rate2 = exchangeRate2.getRate();

        BigDecimal reversedRate1 = new BigDecimal(1).divide(rate1, 3, RoundingMode.HALF_UP);
        BigDecimal reversedRate2 = new BigDecimal(1).divide(rate2, 3, RoundingMode.HALF_UP);

        if (reversedRate1.compareTo(reversedRate2) > 0) {
            calculatedRate = reversedRate1.divide(reversedRate2, 3, RoundingMode.HALF_UP);
        }
        else {
            BigDecimal quotient = reversedRate2.divide(reversedRate1, 3, RoundingMode.HALF_UP);
            calculatedRate = new BigDecimal(1).divide(quotient, 3, RoundingMode.HALF_UP);
        }
        return calculatedRate;
    }

}
