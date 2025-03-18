package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class ServletJsonResponse {

    public static void send(HttpServletResponse response, int statusCode, Object o) {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        try {
            response.getWriter().print(new ObjectMapper().writeValueAsString(o));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
