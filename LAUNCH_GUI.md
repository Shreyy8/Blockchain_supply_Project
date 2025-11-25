# 🚀 How to Launch the GUI Application

## ⚠️ IMPORTANT: Two-Step Process

Due to database connection pooling, you need to run these commands **separately**:

### Step 1: Initialize Database (Run First!)
```powershell
mvn compile exec:java "-Dexec.mainClass=com.supplychain.util.InitializeDatabase"
```

**Wait for this message:**
```
✓ Database initialized successfully!
=================================================
Database initialization complete!
=================================================
```

**Press Enter to close the window.**

---

### Step 2: Launch GUI (Run Second!)
```powershell
mvn compile exec:java "-Dexec.mainClass=com.supplychain.Main"
```

**The login window will appear.**

---

## 🔐 Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Manager | `admin` | `admin123` |
| Supplier | `supplier1` | `pass123` |
| Retailer | `retailer1` | `pass123` |

---

## ❓ Why Two Steps?

The database initialization creates its own connection pool and shuts it down when complete. The GUI application then creates a fresh connection pool that can properly access the initialized tables.

Running them separately ensures:
1. ✅ Tables are created with correct schema
2. ✅ Users are inserted with SHA-256 hashed passwords  
3. ✅ Connection pool is fresh for the GUI
4. ✅ No timing issues with authentication

---

## 🔧 If Login Still Fails

1. **Close the GUI application**
2. **Run Step 1 again** (reinitialize database)
3. **Wait for completion message**
4. **Run Step 2 again** (launch GUI)

---

## 📝 For Your Presentation

**Before presenting:**
1. Open TWO PowerShell windows
2. In Window 1: Run the database initialization
3. In Window 2: Keep ready to launch GUI
4. During presentation: Just run Window 2 command

This way you can quickly restart the GUI without reinitializing the database each time.

---

## ✅ Quick Reference

```powershell
# Window 1: Initialize (run once before presentation)
cd D:\Blockchain_project
mvn compile exec:java "-Dexec.mainClass=com.supplychain.util.InitializeDatabase"

# Window 2: Launch GUI (can run multiple times)
cd D:\Blockchain_project
mvn compile exec:java "-Dexec.mainClass=com.supplychain.Main"
```

---

**Status:** This two-step process ensures reliable database connectivity! 🎯
