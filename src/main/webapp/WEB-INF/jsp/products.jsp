<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Products - Supply Chain Blockchain</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .product-card {
            transition: transform 0.2s, box-shadow 0.2s;
        }
        .product-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }
        .status-badge {
            font-size: 0.75rem;
        }
        .search-container {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem 0;
        }
        .filter-section {
            background-color: #f8f9fa;
            border-radius: 0.5rem;
            padding: 1rem;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="dashboard">
                <i class="fas fa-cube me-2"></i>Supply Chain
            </a>
            <div class="navbar-nav ms-auto">
                <span class="navbar-text me-3">
                    <i class="fas fa-user me-1"></i>${sessionScope.username} (${sessionScope.role})
                </span>
                <a class="nav-link" href="auth?action=logout">
                    <i class="fas fa-sign-out-alt me-1"></i>Logout
                </a>
            </div>
        </div>
    </nav>

    <!-- Search Header -->
    <div class="search-container">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8">
                    <h2 class="mb-2">
                        <i class="fas fa-box me-2"></i>Product Management
                    </h2>
                    <p class="mb-0">Track and manage products in the supply chain</p>
                </div>
                <div class="col-md-4 text-md-end">
                    <c:if test="${sessionScope.role == 'MANAGER' || sessionScope.role == 'SUPPLIER'}">
                        <a href="products?action=create" class="btn btn-light btn-lg">
                            <i class="fas fa-plus me-2"></i>Add Product
                        </a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <div class="container mt-4">
        <!-- Messages -->
        <c:if test="${not empty sessionScope.success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle me-2"></i>${sessionScope.success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="success" scope="session"/>
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-triangle me-2"></i>${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <!-- Filters and Search -->
        <div class="filter-section">
            <div class="row g-3 align-items-end">
                <div class="col-md-4">
                    <label for="searchInput" class="form-label">Search Products</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-search"></i></span>
                        <input type="text" class="form-control" id="searchInput" placeholder="Search by name or ID...">
                    </div>
                </div>
                <div class="col-md-3">
                    <label for="statusFilter" class="form-label">Filter by Status</label>
                    <select class="form-select" id="statusFilter">
                        <option value="">All Statuses</option>
                        <option value="CREATED">Created</option>
                        <option value="IN_TRANSIT">In Transit</option>
                        <option value="DELIVERED">Delivered</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label for="originFilter" class="form-label">Filter by Origin</label>
                    <select class="form-select" id="originFilter">
                        <option value="">All Origins</option>
                    </select>
                </div>
                <div class="col-md-2">
                    <button type="button" class="btn btn-outline-secondary w-100" onclick="clearFilters()">
                        <i class="fas fa-times me-1"></i>Clear
                    </button>
                </div>
            </div>
        </div>

        <!-- Products Grid -->
        <div class="row" id="productsContainer">
            <c:choose>
                <c:when test="${not empty products}">
                    <c:forEach var="product" items="${products}">
                        <div class="col-md-6 col-lg-4 mb-4 product-item" 
                             data-name="${product.name}" 
                             data-id="${product.productId}"
                             data-status="${product.status}"
                             data-origin="${product.origin}">
                            <div class="card product-card h-100">
                                <div class="card-header d-flex justify-content-between align-items-center">
                                    <h6 class="mb-0 text-truncate me-2">${product.name}</h6>
                                    <span class="badge status-badge 
                                        <c:choose>
                                            <c:when test='${product.status == "CREATED"}'>bg-primary</c:when>
                                            <c:when test='${product.status == "IN_TRANSIT"}'>bg-warning</c:when>
                                            <c:when test='${product.status == "DELIVERED"}'>bg-success</c:when>
                                            <c:otherwise>bg-secondary</c:otherwise>
                                        </c:choose>">
                                        ${product.status}
                                    </span>
                                </div>
                                <div class="card-body">
                                    <p class="card-text">
                                        <small class="text-muted">ID: ${product.productId}</small>
                                    </p>
                                    <c:if test="${not empty product.description}">
                                        <p class="card-text">${product.description}</p>
                                    </c:if>
                                    <div class="row text-center">
                                        <div class="col-6">
                                            <i class="fas fa-map-marker-alt text-primary"></i>
                                            <div class="small">Origin</div>
                                            <div class="fw-bold">${product.origin}</div>
                                        </div>
                                        <div class="col-6">
                                            <i class="fas fa-location-arrow text-success"></i>
                                            <div class="small">Current</div>
                                            <div class="fw-bold">${product.currentLocation}</div>
                                        </div>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <small class="text-muted">
                                            <i class="fas fa-clock me-1"></i>
                                            <fmt:formatDate value="${product.createdAt}" pattern="MMM dd, yyyy"/>
                                        </small>
                                        <div class="btn-group" role="group">
                                            <a href="products?action=view&id=${product.productId}" 
                                               class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <c:if test="${sessionScope.role == 'MANAGER'}">
                                                <button type="button" class="btn btn-sm btn-outline-secondary"
                                                        onclick="editProduct('${product.productId}')">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="col-12">
                        <div class="text-center py-5">
                            <i class="fas fa-box-open fa-4x text-muted mb-3"></i>
                            <h4 class="text-muted">No Products Found</h4>
                            <p class="text-muted">
                                <c:choose>
                                    <c:when test="${sessionScope.role == 'MANAGER' || sessionScope.role == 'SUPPLIER'}">
                                        Start by creating your first product.
                                    </c:when>
                                    <c:otherwise>
                                        No products are available for your role.
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <c:if test="${sessionScope.role == 'MANAGER' || sessionScope.role == 'SUPPLIER'}">
                                <a href="products?action=create" class="btn btn-primary">
                                    <i class="fas fa-plus me-2"></i>Create First Product
                                </a>
                            </c:if>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- No Results Message (Hidden by default) -->
        <div id="noResults" class="text-center py-5" style="display: none;">
            <i class="fas fa-search fa-4x text-muted mb-3"></i>
            <h4 class="text-muted">No Products Match Your Search</h4>
            <p class="text-muted">Try adjusting your search criteria or filters.</p>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const searchInput = document.getElementById('searchInput');
            const statusFilter = document.getElementById('statusFilter');
            const originFilter = document.getElementById('originFilter');
            const productsContainer = document.getElementById('productsContainer');
            const noResults = document.getElementById('noResults');
            const productItems = document.querySelectorAll('.product-item');

            // Populate origin filter
            const origins = new Set();
            productItems.forEach(item => {
                const origin = item.dataset.origin;
                if (origin) origins.add(origin);
            });
            
            origins.forEach(origin => {
                const option = document.createElement('option');
                option.value = origin;
                option.textContent = origin;
                originFilter.appendChild(option);
            });

            // Filter function
            function filterProducts() {
                const searchTerm = searchInput.value.toLowerCase();
                const statusValue = statusFilter.value;
                const originValue = originFilter.value;
                let visibleCount = 0;

                productItems.forEach(item => {
                    const name = item.dataset.name.toLowerCase();
                    const id = item.dataset.id.toLowerCase();
                    const status = item.dataset.status;
                    const origin = item.dataset.origin;

                    const matchesSearch = searchTerm === '' || 
                                        name.includes(searchTerm) || 
                                        id.includes(searchTerm);
                    const matchesStatus = statusValue === '' || status === statusValue;
                    const matchesOrigin = originValue === '' || origin === originValue;

                    if (matchesSearch && matchesStatus && matchesOrigin) {
                        item.style.display = 'block';
                        visibleCount++;
                    } else {
                        item.style.display = 'none';
                    }
                });

                // Show/hide no results message
                if (visibleCount === 0 && productItems.length > 0) {
                    noResults.style.display = 'block';
                    productsContainer.style.display = 'none';
                } else {
                    noResults.style.display = 'none';
                    productsContainer.style.display = 'flex';
                }
            }

            // Event listeners
            searchInput.addEventListener('input', filterProducts);
            statusFilter.addEventListener('change', filterProducts);
            originFilter.addEventListener('change', filterProducts);

            // Clear filters function
            window.clearFilters = function() {
                searchInput.value = '';
                statusFilter.value = '';
                originFilter.value = '';
                filterProducts();
            };

            // Edit product function
            window.editProduct = function(productId) {
                // This would typically open a modal or navigate to edit page
                alert('Edit functionality for product ' + productId + ' would be implemented here.');
            };
        });
    </script>
</body>
</html>