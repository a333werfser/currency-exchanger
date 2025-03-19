package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrenciesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import util.ServletErrorMessage;
import util.ServletJsonResponse;

import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        List<Currency> currencies = new CurrenciesDao().getAll();
        ServletJsonResponse.send(response, 200, currencies);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String fullname = request.getParameter("fullname");
        String code = request.getParameter("code");
        String sign = request.getParameter("sign");

        CurrenciesDao currenciesDao = new CurrenciesDao();

        if (fullname == null || code == null || sign == null) {
            ServletJsonResponse.send(response, 400, new ServletErrorMessage(
                    "Отсутствует нужное поле формы"));
        } else if (currenciesDao.getAllCodes().contains(code)) {
            ServletJsonResponse.send(response, 409, new ServletErrorMessage(
                    "Валюта с таким кодом уже существует"));
        } else {
            Currency currency = new Currency(code, fullname, sign);
            currenciesDao.add(currency);
            Currency addedCurrency = currenciesDao.get(code);
            ServletJsonResponse.send(response, 201, addedCurrency);
        }
    }
}
