const express = require("express");
const cors = require("cors");
const path = require("path");

const app = express();

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Serve frontend
app.use(express.static(path.join(__dirname, "../frontend")));

// Routes
const donorRoutes = require("./routes/donorRoutes");
app.use("/api/donors", donorRoutes);

const requestRoutes = require("./routes/requestRoutes");
app.use("/api/requests", requestRoutes);

const registrationRoutes = require("./routes/registrationRoutes");
app.use("/api/registration", registrationRoutes);

const loginRoutes = require("./routes/loginRoutes");
app.use("/api/login", loginRoutes);

// Test
app.get("/", (req, res) => {
    res.send("Blood Donation Backend Running");
});


// Start server
app.listen(5000, () => {
    console.log("Server Running On Port 5000");
});