package com.supplychain.gui;

import com.supplychain.exception.ConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LoginFrame GUI component.
 * Tests component initialization, layout, and basic functionality.
 */
class LoginFrameTest {
    
    private LoginFrame loginFrame;
    
    @BeforeEach
    void setUp() {
        // Create login frame on EDT (Event Dispatch Thread)
        try {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    loginFrame = new LoginFrame();
                } catch (Exception e) {
                    fail("Failed to create LoginFrame: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            fail("Failed to initialize LoginFrame on EDT: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test LoginFrame initialization")
    void testLoginFrameInitialization() {
        assertNotNull(loginFrame, "LoginFrame should be initialized");
        assertEquals("Supply Chain Management System - Login", loginFrame.getTitle());
        assertEquals(JFrame.EXIT_ON_CLOSE, loginFrame.getDefaultCloseOperation());
    }
    
    @Test
    @DisplayName("Test LoginFrame size")
    void testLoginFrameSize() {
        Dimension size = loginFrame.getSize();
        
        assertEquals(400, size.width, "Frame width should be 400");
        assertEquals(300, size.height, "Frame height should be 300");
    }
    
    @Test
    @DisplayName("Test LoginFrame is not resizable")
    void testLoginFrameNotResizable() {
        assertFalse(loginFrame.isResizable(), "LoginFrame should not be resizable");
    }
    
    @Test
    @DisplayName("Test LoginFrame contains required components")
    void testLoginFrameComponents() {
        // Get all components recursively
        Component[] components = getAllComponents(loginFrame);
        
        // Check for text fields
        boolean hasTextField = false;
        boolean hasPasswordField = false;
        boolean hasButton = false;
        boolean hasLabel = false;
        
        for (Component component : components) {
            if (component instanceof JTextField && !(component instanceof JPasswordField)) {
                hasTextField = true;
            } else if (component instanceof JPasswordField) {
                hasPasswordField = true;
            } else if (component instanceof JButton) {
                hasButton = true;
            } else if (component instanceof JLabel) {
                hasLabel = true;
            }
        }
        
        assertTrue(hasTextField, "LoginFrame should contain username text field");
        assertTrue(hasPasswordField, "LoginFrame should contain password field");
        assertTrue(hasButton, "LoginFrame should contain login button");
        assertTrue(hasLabel, "LoginFrame should contain labels");
    }
    
    @Test
    @DisplayName("Test LoginFrame has login button")
    void testLoginFrameHasLoginButton() {
        JButton loginButton = findButton(loginFrame, "Login");
        
        assertNotNull(loginButton, "LoginFrame should have a Login button");
        assertTrue(loginButton.isEnabled(), "Login button should be enabled initially");
    }
    
    @Test
    @DisplayName("Test LoginFrame has username field")
    void testLoginFrameHasUsernameField() {
        JTextField usernameField = findTextField(loginFrame);
        
        assertNotNull(usernameField, "LoginFrame should have a username text field");
    }
    
    @Test
    @DisplayName("Test LoginFrame has password field")
    void testLoginFrameHasPasswordField() {
        JPasswordField passwordField = findPasswordField(loginFrame);
        
        assertNotNull(passwordField, "LoginFrame should have a password field");
    }
    
    @Test
    @DisplayName("Test LoginFrame has title label")
    void testLoginFrameHasTitleLabel() {
        JLabel titleLabel = findLabelContaining(loginFrame, "Blockchain");
        
        assertNotNull(titleLabel, "LoginFrame should have a title label");
    }
    
    @Test
    @DisplayName("Test initial session state")
    void testInitialSessionState() {
        assertNull(loginFrame.getCurrentSessionId(), "Initial session ID should be null");
        assertNull(loginFrame.getCurrentUser(), "Initial user should be null");
    }
    
    @Test
    @DisplayName("Test LoginFrame layout structure")
    void testLoginFrameLayoutStructure() {
        Container contentPane = loginFrame.getContentPane();
        
        assertNotNull(contentPane, "Content pane should not be null");
        assertTrue(contentPane.getComponentCount() > 0, "Content pane should have components");
    }
    
    // Helper methods to find components
    
    private Component[] getAllComponents(Container container) {
        java.util.List<Component> componentList = new java.util.ArrayList<>();
        for (Component component : container.getComponents()) {
            componentList.add(component);
            if (component instanceof Container) {
                Component[] subComponents = getAllComponents((Container) component);
                for (Component subComponent : subComponents) {
                    componentList.add(subComponent);
                }
            }
        }
        return componentList.toArray(new Component[0]);
    }
    
    private JButton findButton(Container container, String text) {
        Component[] components = getAllComponents(container);
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(text)) {
                    return button;
                }
            }
        }
        return null;
    }
    
    private JTextField findTextField(Container container) {
        Component[] components = getAllComponents(container);
        for (Component component : components) {
            if (component instanceof JTextField && !(component instanceof JPasswordField)) {
                return (JTextField) component;
            }
        }
        return null;
    }
    
    private JPasswordField findPasswordField(Container container) {
        Component[] components = getAllComponents(container);
        for (Component component : components) {
            if (component instanceof JPasswordField) {
                return (JPasswordField) component;
            }
        }
        return null;
    }
    
    private JLabel findLabelContaining(Container container, String text) {
        Component[] components = getAllComponents(container);
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getText() != null && label.getText().contains(text)) {
                    return label;
                }
            }
        }
        return null;
    }
}
