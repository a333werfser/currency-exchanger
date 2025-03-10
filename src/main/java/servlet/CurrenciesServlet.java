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
}
