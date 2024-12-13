# Automated Teller Machine (ATM) Simulator

## Description
The ATM Simulator is a virtual representation of the physical ATM machine, replicating its complete functionality in a digital environment. It is fully compatible with **JDBC** and **FXML**, allowing seamless interaction between the graphical interface and the database. The simulator handles various operations such as transferring funds, deducting balances, and maintaining transaction records. 

### Features
- **Virtual ATM Interface**: Mimics real-world ATM functionality.
- **Database Integration**: Connects to SQL for data persistence and retrieval.
- **Transaction Automation**: Automatically calculates transfers and deductions.
- **User Interaction**: Provides an intuitive GUI for users to interact with the system.
- **Transaction History**: Logs all user actions for record-keeping.

---

## File Descriptions

### **Core Files**
- **ATMApplication.java**  
  Launches the application using SceneBuilder and ensures smooth GUI operation.

- **DatabaseConn.java**  
  Manages the connection to the SQL database.

- **SceneSwitch.java**  
  Handles transitions between different pages of the application.

---

### **Transaction Management**
- **Transaction.java**  
  Manages transaction data within SQL, including:  
  - `getTransactionId()` / `setTransactionId(int transactionId)`  
  - `getTransactionType()` / `setTransactionType(String transactionType)`  
  - `getAmount()` / `setAmount(double amount)`  
  - `getTransactionDate()` / `setTransactionDate(String transactionDate)`

---

### **User Session**
- **UserSession.java**  
  Creates and manages a session for the active user.

---

### **Controllers**
- **DepositController.java**  
  Integrates FXML and JDBC, handling all deposit-related operations.

- **HistoryController.java**  
  Logs and records user activity for future reference.

- **MenuController.java**  
  Acts as the main menu, interacting with JDBC to fetch user data.

- **PinChangeController.java**  
  Enables PIN code updates for individual users.

- **StartController.java**  
  Verifies user presence and PIN code validity in a graphical format.

- **Transfer2Controller.java**  
  Provides a graphical interface for money transfers.

- **TransferController.java**  
  Handles SQL operations for transferring funds.

- **WithdrawalController.java**  
  Manages the main database for withdrawals.

---

## Team Members
- **Aidin**: Team Lead  
- **Aktan**: Presentation Lead  
- **Adilet**: Support and Helper  

---

## How to Run
1. Ensure all dependencies, including JDBC drivers, are installed.
2. Connect the application to your SQL database using the configurations in `DatabaseConn.java`.
3. Launch the application using `ATMApplication.java`.
4. Follow the graphical interface to perform ATM operations.

---

## Technologies Used
- **Java**  
- **FXML**  
- **JDBC**  
- **SQL**  

## UML Diagram
![Screenshot 2024-12-12 224414](https://github.com/user-attachments/assets/c8fa35b9-6b6b-467f-a0e8-0ce8f80053e8)

## Screenshots
![Screenshot 2024-12-12 223855](https://github.com/user-attachments/assets/79533b26-82fe-4b3d-b97d-bf48f65f2b7e)
![Screenshot 2024-12-12 223724](https://github.com/user-attachments/assets/7e335f87-f6a5-42d4-9462-df9126adb8d1)
![Screenshot 2024-12-12 223736](https://github.com/user-attachments/assets/0ad191a0-22a7-43a1-8856-6259b388e9b1)
![Screenshot 2024-12-12 223757](https://github.com/user-attachments/assets/c16715ff-8ade-4fc2-a0b9-8b9966bc6ae6)
![Screenshot 2024-12-12 223814](https://github.com/user-attachments/assets/05a2fd6a-9693-4a77-9ac9-4a7934c63e08)
![Screenshot 2024-12-12 223827](https://github.com/user-attachments/assets/613c4307-fa22-4310-b5b4-494dc88a4bc1)
![Screenshot 2024-12-12 223836](https://github.com/user-attachments/assets/a0ee8d99-1d48-40b3-87c3-d6a68297975f)

For any questions or contributions, feel free to contact the team members.
