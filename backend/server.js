const express = require("express");
const cors = require("cors");
require("./db");
const donorRoutes = require("./routes/donorRoutes");
const requestRoutes = require("./routes/requestRoutes");

const app = express();

app.use(cors());
app.use(express.json());

app.use("/api/donors", donorRoutes);
app.use("/api/requests", requestRoutes);

app.get("/", (req, res) => {
  res.send("Blood Donation System Running");
});

app.listen(5000, () => {
  console.log("Server Running On Port 5000");
});