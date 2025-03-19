package servlet;

import dao.CurrenciesDao;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.ServletErrorMessage;
import util.ServletJsonResponse;
import util.ServletUtil;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String lastSegment = ServletUtil.getLastURISegment(request);
        CurrenciesDao currenciesDao = new CurrenciesDao();
        if (lastSegment.equals("currency")) {
            ServletJsonResponse.send(response, 400, new ServletErrorMessage(
                    "Коды валют отсутствуют в адресе"));
        } else if (!currenciesDao.getAllCodes().contains(lastSegment)) {
            ServletJsonResponse.send(response, 404, new ServletErrorMessage(
                    "Валюта с таким кодом отсутствует в БД"));
        } else {
            ServletJsonResponse.send(response, 200, currenciesDao.get(lastSegment));
        }
    }
}
