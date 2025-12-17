<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Supply Chain Blockchain</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .login-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body class="d-flex align-items-center">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-4">
                <div class="login-card p-4">
                    <div class="text-center mb-4">
                        <i class="fas fa-cube fa-3x text-primary mb-3"></i>
                        <h3 class="fw-bold">Supply Chain Login</h3>
                        <p class="text-muted">Enter your credentials to access the system</p>
                    </div>

                    <!-- Display error message if login failed -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>${error}
                        </div>
                    </c:if>

                    <!-- Display success message if any -->
                    <c:if test="${not empty success}">
                        <div class="alert alert-success" role="alert">
                            <i class="fas fa-check-circle me-2"></i>${success}
                        </div>
                    </c:if>

                    <form id="loginForm" action="auth" method="post" novalidate>
                        <input type="hidden" name="action" value="login">
                        
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-user"></i></span>
                                <input type="text" class="form-control" id="username" name="username" 
                                       value="${param.username}" maxlength="20" required>
                            </div>
                            <div class="invalid-feedback" id="usernameError"></div>
                        </div>

                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-lock"></i></span>
                                <input type="password" class="form-control" id="password" name="password" 
                                       maxlength="100" required>
                                <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                                    <i class="fas fa-eye"></i>
                                </button>
                            </div>
                            <div class="invalid-feedback" id="passwordError"></div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-primary btn-lg" id="loginBtn">
                                <i class="fas fa-sign-in-alt me-2"></i>Login
                            </button>
                        </div>
                    </form>

                    <hr class="my-4">

                    <div class="text-center">
                        <h6 class="text-muted mb-3">Demo Accounts</h6>
                        <div class="row g-2">
                            <div class="col-12">
                                <button class="btn btn-outline-primary btn-sm w-100" onclick="fillCredentials('admin', 'password123')">
                                    <i class="fas fa-user-tie me-1"></i>Manager (admin)
                                </button>
                            </div>
                            <div class="col-6">
                                <button class="btn btn-outline-success btn-sm w-100" onclick="fillCredentials('supplier1', 'password')">
                                    <i class="fas fa-truck me-1"></i>Supplier
                                </button>
                            </div>
                            <div class="col-6">
                                <button class="btn btn-outline-info btn-sm w-100" onclick="fillCredentials('retailer1', 'password')">
                                    <i class="fas fa-store me-1"></i>Retailer
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="text-center mt-4">
                        <a href="index.jsp" class="text-decoration-none">
                            <i class="fas fa-arrow-left me-1"></i>Back to Home
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function fillCredentials(username, password) {
            document.getElementById('username').value = username;
            document.getElementById('password').value = password;
        }
    </script>
</body>
</html>