<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Supply Chain Blockchain</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .sidebar {
            min-height: 100vh;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }
        .stat-card {
            transition: transform 0.3s;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
        .main-content {
            margin-left: 0;
        }
        @media (min-width: 768px) {
            .main-content {
                margin-left: 250px;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <div class="sidebar position-fixed d-none d-md-block" style="width: 250px;">
        <div class="p-3">
            <h4 class="text-white mb-4">
                <i class="fas fa-cube me-2"></i>Supply Chain
            </h4>
            <nav class="nav flex-column">
                <a class="nav-link text-white active" href="dashboard">
                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                </a>
                <a class="nav-link text-white" href="products">
                    <i class="fas fa-box me-2"></i>Products
                </a>
                <a class="nav-link text-white" href="transactions">
                    <i class="fas fa-exchange-alt me-2"></i>Transactions
                </a>
                <a class="nav-link text-white" href="blockchain">
                    <i class="fas fa-cubes me-2"></i>Blockchain
                </a>
                <hr class="text-white">
                <a class="nav-link text-white" href="auth?action=logout">
                    <i class="fas fa-sign-out-alt me-2"></i>Logout
                </a>
            </nav>
        </div>
        <div class="position-absolute bottom-0 p-3 w-100">
            <div class="text-white-50 small">
                <div><strong>${sessionScope.username}</strong></div>
                <div>${sessionScope.role}</div>
            </div>
        </div>
    </div>

    <!-- Mobile Navigation -->
    <nav class="navbar navbar-dark bg-dark d-md-none">
        <div class="container-fluid">
            <a class="navbar-brand" href="#">
                <i class="fas fa-cube me-2"></i>Supply Chain
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mobileNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="mobileNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" href="dashboard">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="products">
                            <i class="fas fa-box me-2"></i>Products
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="transactions">
                            <i class="fas fa-exchange-alt me-2"></i>Transactions
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="blockchain">
                            <i class="fas fa-cubes me-2"></i>Blockchain
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="auth?action=logout">
                            <i class="fas fa-sign-out-alt me-2"></i>Logout
                        </a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="main-content">
        <div class="container-fluid p-4">
            <!-- Header -->
            <div class="row mb-4">
                <div class="col">
                    <h2 class="fw-bold">Dashboard</h2>
                    <p class="text-muted">Welcome back, ${sessionScope.username}!</p>
                </div>
            </div>

            <!-- Statistics Cards -->
            <div class="row g-4 mb-4">
                <div class="col-md-4">
                    <div class="card stat-card border-0 shadow h-100">
                        <div class="card-body text-center">
                            <i class="fas fa-box fa-3x text-primary mb-3"></i>
                            <h3 class="fw-bold">${totalProducts}</h3>
                            <p class="text-muted mb-0">Total Products</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card border-0 shadow h-100">
                        <div class="card-body text-center">
                            <i class="fas fa-exchange-alt fa-3x text-success mb-3"></i>
                            <h3 class="fw-bold">${totalTransactions}</h3>
                            <p class="text-muted mb-0">Total Transactions</p>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card stat-card border-0 shadow h-100">
                        <div class="card-body text-center">
                            <i class="fas fa-cubes fa-3x text-info mb-3"></i>
                            <h3 class="fw-bold">${totalBlocks}</h3>
                            <p class="text-muted mb-0">Blockchain Blocks</p>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Role-specific Content -->
            <div class="row g-4">
                <!-- Manager View -->
                <c:if test="${sessionScope.role == 'MANAGER'}">
                    <div class="col-md-6">
                        <div class="card border-0 shadow">
                            <div class="card-header bg-primary text-white">
                                <h5 class="mb-0"><i class="fas fa-box me-2"></i>Recent Products</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty recentProducts}">
                                        <div class="list-group list-group-flush">
                                            <c:forEach var="product" items="${recentProducts}">
                                                <div class="list-group-item d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <h6 class="mb-1">${product.name}</h6>
                                                        <small class="text-muted">${product.origin} → ${product.currentLocation}</small>
                                                    </div>
                                                    <span class="badge bg-secondary">${product.status}</span>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted text-center">No products found</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card border-0 shadow">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0"><i class="fas fa-exchange-alt me-2"></i>Recent Transactions</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty recentTransactions}">
                                        <div class="list-group list-group-flush">
                                            <c:forEach var="transaction" items="${recentTransactions}">
                                                <div class="list-group-item">
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <h6 class="mb-1">${transaction.transactionType}</h6>
                                                        <small><fmt:formatDate value="${transaction.timestamp}" pattern="MMM dd, HH:mm"/></small>
                                                    </div>
                                                    <small class="text-muted">${transaction.fromParty} → ${transaction.toParty}</small>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted text-center">No transactions found</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Supplier View -->
                <c:if test="${sessionScope.role == 'SUPPLIER'}">
                    <div class="col-md-6">
                        <div class="card border-0 shadow">
                            <div class="card-header bg-warning text-dark">
                                <h5 class="mb-0"><i class="fas fa-box me-2"></i>My Products</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty myProducts}">
                                        <div class="list-group list-group-flush">
                                            <c:forEach var="product" items="${myProducts}">
                                                <div class="list-group-item d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <h6 class="mb-1">${product.name}</h6>
                                                        <small class="text-muted">${product.currentLocation}</small>
                                                    </div>
                                                    <span class="badge bg-secondary">${product.status}</span>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted text-center">No products found</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- Retailer View -->
                <c:if test="${sessionScope.role == 'RETAILER'}">
                    <div class="col-md-6">
                        <div class="card border-0 shadow">
                            <div class="card-header bg-info text-white">
                                <h5 class="mb-0"><i class="fas fa-box me-2"></i>Received Products</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty receivedProducts}">
                                        <div class="list-group list-group-flush">
                                            <c:forEach var="product" items="${receivedProducts}">
                                                <div class="list-group-item d-flex justify-content-between align-items-center">
                                                    <div>
                                                        <h6 class="mb-1">${product.name}</h6>
                                                        <small class="text-muted">From: ${product.origin}</small>
                                                    </div>
                                                    <span class="badge bg-secondary">${product.status}</span>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted text-center">No products received</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:if>

                <!-- My Transactions (for Suppliers and Retailers) -->
                <c:if test="${sessionScope.role != 'MANAGER'}">
                    <div class="col-md-6">
                        <div class="card border-0 shadow">
                            <div class="card-header bg-success text-white">
                                <h5 class="mb-0"><i class="fas fa-exchange-alt me-2"></i>My Transactions</h5>
                            </div>
                            <div class="card-body">
                                <c:choose>
                                    <c:when test="${not empty myTransactions}">
                                        <div class="list-group list-group-flush">
                                            <c:forEach var="transaction" items="${myTransactions}">
                                                <div class="list-group-item">
                                                    <div class="d-flex justify-content-between align-items-center">
                                                        <h6 class="mb-1">${transaction.transactionType}</h6>
                                                        <small><fmt:formatDate value="${transaction.timestamp}" pattern="MMM dd, HH:mm"/></small>
                                                    </div>
                                                    <small class="text-muted">${transaction.fromParty} → ${transaction.toParty}</small>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="text-muted text-center">No transactions found</p>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:if>
            </div>

            <!-- Quick Actions -->
            <div class="row mt-4">
                <div class="col">
                    <div class="card border-0 shadow">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="fas fa-bolt me-2"></i>Quick Actions</h5>
                        </div>
                        <div class="card-body">
                            <div class="row g-3">
                                <c:if test="${sessionScope.role == 'SUPPLIER' || sessionScope.role == 'MANAGER'}">
                                    <div class="col-md-3">
                                        <a href="products?action=create" class="btn btn-primary w-100">
                                            <i class="fas fa-plus me-2"></i>Add Product
                                        </a>
                                    </div>
                                </c:if>
                                <div class="col-md-3">
                                    <a href="transactions?action=create" class="btn btn-success w-100">
                                        <i class="fas fa-exchange-alt me-2"></i>New Transaction
                                    </a>
                                </div>
                                <div class="col-md-3">
                                    <a href="blockchain" class="btn btn-info w-100">
                                        <i class="fas fa-cubes me-2"></i>View Blockchain
                                    </a>
                                </div>
                                <c:if test="${sessionScope.role == 'MANAGER'}">
                                    <div class="col-md-3">
                                        <button class="btn btn-warning w-100" onclick="mineBlock()">
                                            <i class="fas fa-hammer me-2"></i>Mine Block
                                        </button>
                                    </div>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function mineBlock() {
            if (confirm('Are you sure you want to mine a new block? This will process all pending transactions.')) {
                fetch('api/blockchain/mine', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Block mined successfully!');
                        location.reload();
                    } else {
                        alert('Error mining block: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Error mining block');
                });
            }
        }
    </script>
</body>
</html>