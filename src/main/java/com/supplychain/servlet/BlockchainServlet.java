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
 * Servlet for handling blockchain-related operations.
 */
public class BlockchainServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(BlockchainServlet.class.getName());

    @Override
    public void init() throws ServletException {
        LOGGER.info("BlockchainServlet initialized successfully");
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
        response.getWriter().println("<h2>Blockchain Explorer</h2>");
        response.getWriter().println("<p>Blockchain features coming soon!</p>");
        response.getWriter().println("<a href='dashboard'>Back to Dashboard</a>");
        response.getWriter().println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}