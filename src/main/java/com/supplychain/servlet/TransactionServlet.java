package com.supplychain.servlet;

import com.supplychain.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Servlet for handling transaction-related operations.
 */
public class TransactionServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TransactionServlet.class.getName());

    @Override
    public void init() throws ServletException {
        LOGGER.info("TransactionServlet initialized successfully");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // For now, show a simple message
        response.setContentType("text/html");
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>Transaction Management</h2>");
        response.getWriter().println("<p>Transaction features coming soon!</p>");
        response.getWriter().println("<a href='dashboard'>Back to Dashboard</a>");
        response.getWriter().println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}