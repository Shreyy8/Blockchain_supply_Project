package com.supplychain.servlet;

import com.supplychain.model.User;
import com.supplychain.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for displaying the main dashboard with system statistics.
 */
public class DashboardServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DashboardServlet.class.getName());
    
    private ProductService productService;

    @Override
    public void init() throws ServletException {
        try {
            this.productService = new ProductService();
            LOGGER.info("DashboardServlet initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize DashboardServlet", e);
            throw new ServletException("Failed to initialize dashboard services", e);
        }
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

        try {
            // Get dashboard statistics
            int totalProducts = productService.getTotalProductCount();
            int totalTransactions = 0; // Will implement later
            int totalBlocks = 0; // Will implement later
            
            // Get recent activities based on user role
            request.setAttribute("totalProducts", totalProducts);
            request.setAttribute("totalTransactions", totalTransactions);
            request.setAttribute("totalBlocks", totalBlocks);
            
            // Get role-specific data
            switch (user.getRole()) {
                case MANAGER:
                    // Managers can see all system statistics
                    request.setAttribute("recentProducts", productService.getRecentProducts(5));
                    // request.setAttribute("recentTransactions", transactionService.getRecentTransactions(5));
                    break;
                case SUPPLIER:
                    // Suppliers see their own products and transactions
                    request.setAttribute("myProducts", productService.getProductsByUser(user.getUserId(), 5));
                    // request.setAttribute("myTransactions", transactionService.getTransactionsByUser(user.getUserId(), 5));
                    break;
                case RETAILER:
                    // Retailers see products they've received and their transactions
                    request.setAttribute("receivedProducts", productService.getProductsReceivedByUser(user.getUserId(), 5));
                    // request.setAttribute("myTransactions", transactionService.getTransactionsByUser(user.getUserId(), 5));
                    break;
            }
            
            // Forward to dashboard JSP
            request.getRequestDispatcher("WEB-INF/jsp/dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard data", e);
            request.setAttribute("error", "Error loading dashboard data");
            request.getRequestDispatcher("WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Dashboard is read-only, redirect to GET
        doGet(request, response);
    }
}