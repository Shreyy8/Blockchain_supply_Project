<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Product - Supply Chain Blockchain</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        .form-container {
            max-width: 600px;
            margin: 2rem auto;
        }
        .invalid-feedback {
            display: block;
        }
        .character-count {
            font-size: 0.875rem;
            color: #6c757d;
        }
        .character-count.warning {
            color: #fd7e14;
        }
        .character-count.danger {
            color: #dc3545;
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

    <div class="container">
        <div class="form-container">
            <!-- Header -->
            <div class="card shadow">
                <div class="card-header bg-primary text-white">
                    <h4 class="mb-0">
                        <i class="fas fa-plus-circle me-2"></i>Create New Product
                    </h4>
                </div>
                <div class="card-body">
                    <!-- Error Messages -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle me-2"></i>${error}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Success Messages -->
                    <c:if test="${not empty success}">
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle me-2"></i>${success}
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    </c:if>

                    <!-- Product Form -->
                    <form id="productForm" action="products" method="post" novalidate>
                        <input type="hidden" name="action" value="create">
                        
                        <!-- Product Name -->
                        <div class="mb-3">
                            <label for="name" class="form-label">
                                Product Name <span class="text-danger">*</span>
                            </label>
                            <input type="text" 
                                   class="form-control" 
                                   id="name" 
                                   name="name" 
                                   value="${param.name}" 
                                   maxlength="200"
                                   required>
                            <div class="invalid-feedback" id="nameError"></div>
                            <div class="character-count" id="nameCount">0/200 characters</div>
                        </div>

                        <!-- Product Description -->
                        <div class="mb-3">
                            <label for="description" class="form-label">Description</label>
                            <textarea class="form-control" 
                                      id="description" 
                                      name="description" 
                                      rows="4" 
                                      maxlength="1000"
                                      placeholder="Enter product description (optional)">${param.description}</textarea>
                            <div class="invalid-feedback" id="descriptionError"></div>
                            <div class="character-count" id="descriptionCount">0/1000 characters</div>
                        </div>

                        <!-- Origin Location -->
                        <div class="mb-3">
                            <label for="origin" class="form-label">
                                Origin Location <span class="text-danger">*</span>
                            </label>
                            <input type="text" 
                                   class="form-control" 
                                   id="origin" 
                                   name="origin" 
                                   value="${param.origin}" 
                                   maxlength="200"
                                   placeholder="e.g., China, USA, Germany"
                                   required>
                            <div class="invalid-feedback" id="originError"></div>
                            <div class="character-count" id="originCount">0/200 characters</div>
                        </div>

                        <!-- Form Actions -->
                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="products" class="btn btn-secondary me-md-2">
                                <i class="fas fa-times me-1"></i>Cancel
                            </a>
                            <button type="submit" class="btn btn-primary" id="submitBtn">
                                <i class="fas fa-save me-1"></i>Create Product
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Help Section -->
            <div class="card mt-3">
                <div class="card-header">
                    <h6 class="mb-0">
                        <i class="fas fa-info-circle me-2"></i>Guidelines
                    </h6>
                </div>
                <div class="card-body">
                    <ul class="mb-0">
                        <li><strong>Product Name:</strong> Must be unique, 2-200 characters, alphanumeric with spaces and basic punctuation</li>
                        <li><strong>Description:</strong> Optional, maximum 1000 characters</li>
                        <li><strong>Origin:</strong> Required, 2-200 characters, represents where the product was manufactured or sourced</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const form = document.getElementById('productForm');
            const nameInput = document.getElementById('name');
            const descriptionInput = document.getElementById('description');
            const originInput = document.getElementById('origin');
            const submitBtn = document.getElementById('submitBtn');

            // Character counting and validation
            function setupCharacterCount(input, countElement, maxLength) {
                function updateCount() {
                    const length = input.value.length;
                    countElement.textContent = length + '/' + maxLength + ' characters';
                    
                    if (length > maxLength * 0.9) {
                        countElement.className = 'character-count danger';
                    } else if (length > maxLength * 0.8) {
                        countElement.className = 'character-count warning';
                    } else {
                        countElement.className = 'character-count';
                    }
                }
                
                input.addEventListener('input', updateCount);
                updateCount(); // Initial count
            }

            setupCharacterCount(nameInput, document.getElementById('nameCount'), 200);
            setupCharacterCount(descriptionInput, document.getElementById('descriptionCount'), 1000);
            setupCharacterCount(originInput, document.getElementById('originCount'), 200);

            // Real-time validation
            function validateField(input, errorElement, validationFn, errorMessage) {
                function validate() {
                    const isValid = validationFn(input.value);
                    if (isValid) {
                        input.classList.remove('is-invalid');
                        input.classList.add('is-valid');
                        errorElement.textContent = '';
                    } else {
                        input.classList.remove('is-valid');
                        input.classList.add('is-invalid');
                        errorElement.textContent = errorMessage;
                    }
                    return isValid;
                }
                
                input.addEventListener('blur', validate);
                input.addEventListener('input', function() {
                    if (input.classList.contains('is-invalid')) {
                        validate();
                    }
                });
                
                return validate;
            }

            // Validation functions
            const validateName = validateField(
                nameInput, 
                document.getElementById('nameError'),
                (value) => value.trim().length >= 2 && value.trim().length <= 200 && /^[a-zA-Z0-9\s\-_.,()]+$/.test(value.trim()),
                'Product name must be 2-200 characters and contain only letters, numbers, spaces, and basic punctuation'
            );

            const validateDescription = validateField(
                descriptionInput,
                document.getElementById('descriptionError'),
                (value) => value.length <= 1000,
                'Description must not exceed 1000 characters'
            );

            const validateOrigin = validateField(
                originInput,
                document.getElementById('originError'),
                (value) => value.trim().length >= 2 && value.trim().length <= 200 && /^[a-zA-Z0-9\s\-_.,()]+$/.test(value.trim()),
                'Origin must be 2-200 characters and contain only letters, numbers, spaces, and basic punctuation'
            );

            // Form submission validation
            form.addEventListener('submit', function(e) {
                e.preventDefault();
                
                const isNameValid = validateName();
                const isDescriptionValid = validateDescription();
                const isOriginValid = validateOrigin();
                
                if (isNameValid && isDescriptionValid && isOriginValid) {
                    // Show loading state
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Creating...';
                    
                    // Submit form
                    form.submit();
                } else {
                    // Scroll to first error
                    const firstError = form.querySelector('.is-invalid');
                    if (firstError) {
                        firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                        firstError.focus();
                    }
                }
            });

            // Prevent double submission
            form.addEventListener('submit', function() {
                submitBtn.disabled = true;
            });
        });
    </script>
</body>
</html>