package org.owasp.benchmark.helpers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Entry point that passes request input to {@link TemplateRenderer}, giving the vulnerable
 * commons-text interpolator (CVE-2022-42889) a real call path from application code.
 */
@WebServlet(value = "/template/render")
public class TemplateServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String template = request.getParameter("template");
        if (template == null) {
            template = "";
        }
        response.getWriter().println(TemplateRenderer.render(template));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
