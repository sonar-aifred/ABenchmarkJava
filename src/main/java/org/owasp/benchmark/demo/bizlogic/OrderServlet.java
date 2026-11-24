package org.owasp.benchmark.demo.bizlogic;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** HTTP entry point for the order demo. See {@link OrderService} for the intentional flaws. */
@WebServlet(value = "/demo/orders")
public class OrderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String action = request.getParameter("action");
        String user = request.getRemoteUser() == null ? "anonymous" : request.getRemoteUser();

        if ("create".equals(action)) {
            long price = Long.parseLong(request.getParameter("unitPriceCents"));
            int quantity = Integer.parseInt(request.getParameter("quantity"));
            OrderService.Order order = OrderService.create(user, price, quantity);
            response.getWriter().println("created " + order.id + " total=" + order.totalCents);
            return;
        }

        OrderService.Order order = OrderService.find(Long.parseLong(request.getParameter("id")));
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if ("coupon".equals(action)) {
            OrderService.applyCoupon(order, request.getParameter("code"));
        } else if ("pay".equals(action)) {
            OrderService.pay(order, Long.parseLong(request.getParameter("amountCents")));
        } else if ("ship".equals(action)) {
            OrderService.ship(order);
        } else if ("refund".equals(action)) {
            OrderService.refund(order, Long.parseLong(request.getParameter("amountCents")));
        }
        response.getWriter().println(order.id + " " + order.status + " total=" + order.totalCents);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        OrderService.Order order = OrderService.find(Long.parseLong(request.getParameter("id")));
        if (order == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        response.getWriter()
                .println(order.id + " owner=" + order.owner + " paid=" + order.paidCents);
    }
}
