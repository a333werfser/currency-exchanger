package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrenciesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] urlSegments = String.valueOf(req.getRequestURL()).split("/");
        String lastSegment = urlSegments[urlSegments.length - 1];
        CurrenciesDao currenciesDao = new CurrenciesDao();
        try (PrintWriter out = resp.getWriter()) {
            if (lastSegment.equals("currency")) {
                resp.setStatus(400);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } else if (currenciesDao.getAllCodes().contains(lastSegment)) {
                resp.setContentType("application/json");
                resp.setStatus(200);
                out.print(new ObjectMapper().writeValueAsString(currenciesDao.get(lastSegment)));
            } else {
                resp.setStatus(404);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
