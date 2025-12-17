<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Supply Chain Blockchain</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .error-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .error-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6">
                    <div class="error-card p-5 text-center">
                        <i class="fas fa-exclamation-triangle fa-4x text-warning mb-4"></i>
                        <h2 class="mb-3">Oops! Something went wrong</h2>
                        
                        <c:choose>
                            <c:when test="${not empty error}">
                                <p class="text-muted mb-4">${error}</p>
                            </c:when>
                            <c:otherwise>
                                <p class="text-muted mb-4">
                                    We encountered an unexpected error while processing your request.
                                    Please try again or contact support if the problem persists.
                                </p>
                            </c:otherwise>
                        </c:choose>
                        
                        <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                            <button onclick="history.back()" class="btn btn-outline-primary me-md-2">
                                <i class="fas fa-arrow-left me-1"></i>Go Back
                            </button>
                            <a href="dashboard" class="btn btn-primary">
                                <i class="fas fa-home me-1"></i>Dashboard
                            </a>
                        </div>
                        
                        <hr class="my-4">
                        
                        <p class="small text-muted mb-0">
                            Error ID: ${pageContext.request.getAttribute('javax.servlet.error.request_uri')}
                            <br>
                            Time: <span id="errorTime"></span>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script>
        document.getElementById('errorTime').textContent = new Date().toLocaleString();
    </script>
</body>
</html>