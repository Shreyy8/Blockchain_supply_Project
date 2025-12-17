<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Blockchain Supply Chain Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .hero-section {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 100px 0;
        }
        .feature-card {
            transition: transform 0.3s;
        }
        .feature-card:hover {
            transform: translateY(-5px);
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="#">
                <i class="fas fa-cube me-2"></i>Supply Chain Blockchain
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="login.jsp">Login</a>
            </div>
        </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section text-center">
        <div class="container">
            <h1 class="display-4 fw-bold mb-4">Blockchain Supply Chain Management</h1>
            <p class="lead mb-4">Secure, transparent, and traceable supply chain management powered by blockchain technology</p>
            <a href="login.jsp" class="btn btn-light btn-lg">Get Started</a>
        </div>
    </section>

    <!-- Features Section -->
    <section class="py-5">
        <div class="container">
            <div class="row text-center mb-5">
                <div class="col">
                    <h2 class="fw-bold">Key Features</h2>
                    <p class="text-muted">Revolutionizing supply chain management with blockchain technology</p>
                </div>
            </div>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card h-100 feature-card border-0 shadow">
                        <div class="card-body text-center p-4">
                            <i class="fas fa-shield-alt fa-3x text-primary mb-3"></i>
                            <h5 class="card-title">Secure Transactions</h5>
                            <p class="card-text">All transactions are cryptographically secured and immutable on the blockchain</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 feature-card border-0 shadow">
                        <div class="card-body text-center p-4">
                            <i class="fas fa-search fa-3x text-success mb-3"></i>
                            <h5 class="card-title">Full Traceability</h5>
                            <p class="card-text">Track products from origin to destination with complete transparency</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 feature-card border-0 shadow">
                        <div class="card-body text-center p-4">
                            <i class="fas fa-users fa-3x text-info mb-3"></i>
                            <h5 class="card-title">Multi-Role Access</h5>
                            <p class="card-text">Role-based access for managers, suppliers, and retailers</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- How It Works Section -->
    <section class="py-5 bg-light">
        <div class="container">
            <div class="row text-center mb-5">
                <div class="col">
                    <h2 class="fw-bold">How It Works</h2>
                </div>
            </div>
            <div class="row g-4">
                <div class="col-md-3 text-center">
                    <div class="mb-3">
                        <i class="fas fa-plus-circle fa-3x text-primary"></i>
                    </div>
                    <h5>1. Create Products</h5>
                    <p class="text-muted">Register new products in the system with detailed information</p>
                </div>
                <div class="col-md-3 text-center">
                    <div class="mb-3">
                        <i class="fas fa-exchange-alt fa-3x text-success"></i>
                    </div>
                    <h5>2. Track Transfers</h5>
                    <p class="text-muted">Record all product transfers between supply chain participants</p>
                </div>
                <div class="col-md-3 text-center">
                    <div class="mb-3">
                        <i class="fas fa-cube fa-3x text-info"></i>
                    </div>
                    <h5>3. Mine Blocks</h5>
                    <p class="text-muted">Transactions are grouped into blocks and added to the blockchain</p>
                </div>
                <div class="col-md-3 text-center">
                    <div class="mb-3">
                        <i class="fas fa-chart-line fa-3x text-warning"></i>
                    </div>
                    <h5>4. View Analytics</h5>
                    <p class="text-muted">Monitor supply chain performance with detailed analytics</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer class="bg-dark text-white py-4">
        <div class="container text-center">
            <p>&copy; 2024 Blockchain Supply Chain Management System. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>