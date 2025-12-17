package com.supplychain.servlet;

import com.supplychain.exception.AuthenticationException;
import com.supplychain.model.User;
import com.supplychain.service.UserService;
import com.supplychain.util.DatabaseConnectionManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet for handling user authentication (login/logout).
 */
public class AuthServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());
    private UserService userService;

    @Override
    public void init() throws ServletException {
        try {
            this.userService = new UserService();
            LOGGER.info("AuthServlet initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize AuthServlet", e);
            throw new ServletException("Failed to initialize authentication service", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("logout".equals(action)) {
            handleLogout(request, response);
        } else {
            // Redirect to login page
            response.sendRedirect("login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("login".equals(action)) {
            handleLogin(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }

    /**
     * Handles user login authentication.
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate input
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        try {
            // Authenticate user
            User user = userService.authenticateUser(username.trim(), password);
            
            if (user != null) {
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("role", user.getRole().toString());
                
                LOGGER.info("User logged in successfully: " + username + " (Role: " + user.getRole() + ")");
                
                // Redirect to dashboard
                response.sendRedirect("dashboard");
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("login.jsp").forward(request, response);
            }
            
        } catch (AuthenticationException e) {
            LOGGER.log(Level.WARNING, "Authentication failed for user: " + username, e);
            request.setAttribute("error", "Invalid username or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error during authentication", e);
            request.setAttribute("error", "System error. Please try again later.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    /**
     * Handles user logout.
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            String username = (String) session.getAttribute("username");
            session.invalidate();
            LOGGER.info("User logged out: " + username);
        }
        
        request.setAttribute("success", "You have been logged out successfully");
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        if (userService != null) {
            // Clean up resources if needed
            LOGGER.info("AuthServlet destroyed");
        }
    }
}