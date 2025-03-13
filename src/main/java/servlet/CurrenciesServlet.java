package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<Currency> currencies = new CurrencyDao().getAll();
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.print(new ObjectMapper().writeValueAsString(currencies));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String fullname = req.getParameter("fullname");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        CurrencyDao currencyDao = new CurrencyDao();
        List<String> allCodesList = currencyDao.getAllCodes();

        try (PrintWriter out = resp.getWriter()){
            if (fullname == null || code == null || sign == null ) {
                resp.setStatus(400);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } else if (allCodesList.contains(code)) {
                resp.setStatus(409);
                resp.sendError(HttpServletResponse.SC_CONFLICT);
            } else {
                Currency currency = new Currency(code, fullname, sign);
                currencyDao.add(currency);
                resp.setStatus(201);
                resp.setContentType("application/json");
                out.print(new ObjectMapper().writeValueAsString(currencyDao.get(currency.getCode())));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
