package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ExchangeRatesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.ExchangeRate;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
}
