package cn.denvie.api.gateway.core;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Api请求分发器。
 *
 * @author DengZhaoyong
 * @version 1.0.0
 */
@WebServlet(name = "apiGatewayServlet", urlPatterns = "/api", loadOnStartup = 2)
public class ApiGatewayServlet extends HttpServlet {

    private ApplicationContext context;
    private ApiGatewayHandler apiHandler;

    @Override
    public void init() throws ServletException {
        super.init();
        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        apiHandler = context.getBean(ApiGatewayHandler.class);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        apiHandler.handle(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        apiHandler.handle(req, resp);
    }

}
