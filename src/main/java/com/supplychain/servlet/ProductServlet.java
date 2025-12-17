package com.supplychain.servlet;

import com.supplychain.exception.ServiceException;
import com.supplychain.exception.ValidationException;
import com.supplychain.model.Product;
import com.supplychain.model.User;
import com.supplychain.service.ProductService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling product-related operations.
 */
public class ProductServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ProductServlet.class.getName());
    private ProductService productService;

    @Override
    public void init() throws ServletException {
        try {
            this.productService = new ProductService();
            LOGGER.info("ProductServlet initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize ProductServlet", e);
            throw new ServletException("Failed to initialize product service", e);
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

        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                // Show create product form
                request.getRequestDispatcher("WEB-INF/jsp/product-form.jsp").forward(request, response);
            } else if ("view".equals(action)) {
                // View specific product
                String productId = request.getParameter("id");
                if (productId != null) {
                    Product product = productService.getProductById(productId);
                    request.setAttribute("product", product);
                    request.getRequestDispatcher("WEB-INF/jsp/product-view.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID required");
                }
            } else {
                // List products based on user role
                List<Product> products;
                switch (user.getRole()) {
                    case MANAGER:
                        products = productService.getAllProducts();
                        break;
                    case SUPPLIER:
                        products = productService.getProductsByUser(user.getUserId(), 0);
                        break;
                    case RETAILER:
                        products = productService.getProductsReceivedByUser(user.getUserId(), 0);
                        break;
                    default:
                        products = List.of();
                }
                
                request.setAttribute("products", products);
                request.getRequestDispatcher("WEB-INF/jsp/products.jsp").forward(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in ProductServlet", e);
            request.setAttribute("error", "Error processing request: " + e.getMessage());
            request.getRequestDispatcher("WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        
        try {
            if ("create".equals(action)) {
                handleProductCreation(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (ValidationException e) {
            LOGGER.log(Level.WARNING, "Validation error creating product", e);
            request.setAttribute("error", e.getMessage());
            request.setAttribute("name", request.getParameter("name"));
            request.setAttribute("description", request.getParameter("description"));
            request.setAttribute("origin", request.getParameter("origin"));
            request.getRequestDispatcher("WEB-INF/jsp/product-form.jsp").forward(request, response);
        } catch (ServiceException e) {
            LOGGER.log(Level.SEVERE, "Service error creating product", e);
            request.setAttribute("error", "System error: " + e.getMessage());
            request.getRequestDispatcher("WEB-INF/jsp/product-form.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error creating product", e);
            request.setAttribute("error", "An unexpected error occurred. Please try again.");
            request.getRequestDispatcher("WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }

    /**
     * Handles product creation with comprehensive validation and error handling.
     */
    private void handleProductCreation(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException, ValidationException, ServiceException {
        
        // Check user permissions
        if (user.getRole() != com.supplychain.model.UserRole.MANAGER && 
            user.getRole() != com.supplychain.model.UserRole.SUPPLIER) {
            throw new ValidationException("You don't have permission to create products");
        }
        
        // Get and validate parameters
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String origin = request.getParameter("origin");
        
        // Create product with validation
        Product product = productService.createProduct(name, description, origin, user.getUserId());
        
        // Success response
        request.getSession().setAttribute("success", "Product created successfully: " + product.getProductId());
        response.sendRedirect("products");
    }
}