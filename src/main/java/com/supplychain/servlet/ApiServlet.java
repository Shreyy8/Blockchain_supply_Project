package com.supplychain.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplychain.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * REST API servlet for AJAX operations.
 */
public class ApiServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ApiServlet.class.getName());
    private ObjectMapper objectMapper;

    @Override
    public void init() throws ServletException {
        try {
            this.objectMapper = new ObjectMapper();
            LOGGER.info("ApiServlet initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize ApiServlet", e);
            throw new ServletException("Failed to initialize API service", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        if (user == null) {
            sendErrorResponse(response, "Authentication required", 401);
            return;
        }

        String pathInfo = request.getPathInfo();
        
        try {
            if ("/blockchain/mine".equals(pathInfo)) {
                // Mine a new block (Manager only)
                if (!"MANAGER".equals(user.getRole().toString())) {
                    sendErrorResponse(response, "Access denied. Manager role required.", 403);
                    return;
                }
                
                // Placeholder for blockchain mining
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "Block mining feature coming soon");
                
                response.getWriter().write(objectMapper.writeValueAsString(result));
                
            } else {
                sendErrorResponse(response, "API endpoint not found", 404);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error in API call: " + pathInfo, e);
            sendErrorResponse(response, "Internal server error: " + e.getMessage(), 500);
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}