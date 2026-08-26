const express = require("express");
const router = express.Router();
const db = require("../db");

// Add Blood Request
router.post("/add", (req, res) => {

    const {
        patient_name,
        age,
        gender,
        blood_group,
        hospital,
        city,
        priority
    } = req.body;

    const sql = `
    INSERT INTO blood_requests
    (patient_name,age,gender, blood_group, hospital, city, priority)
    VALUES (?,?,?, ?, ?, ?, ?)
    `;

    db.query(
        sql,
        [patient_name,age,gender, blood_group, hospital, city, priority],
        (err, result) => {

            if (err) {
                console.log(err);
                return res.status(500).json(err);
            }

            res.json({
                message: "Request Added Successfully"
            });
        }
    );
});

// Total Requests Count
router.get("/requestcount", (req, res) => {

    db.query(
        "SELECT COUNT(*) AS total FROM blood_requests",
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json(result[0]);

        }
    );

});

// View All Requests
router.get("/all", (req, res) => {

    db.query(
        "SELECT * FROM blood_requests",
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json(result);

        }
    );

});

// Delete Request
router.delete("/delete/:id", (req, res) => {

    const id = req.params.id;

    db.query(
        "DELETE FROM blood_requests WHERE request_id = ?",
        [id],
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json({
                message: "Request Deleted Successfully"
            });

        }
    );

});

// Update Request
router.put("/update/:id", (req, res) => {

    const id = req.params.id;

    const {
    patient_name,
    age,
    gender,
    blood_group,
    hospital,
    city,
    priority
} = req.body;

    db.query(
        `UPDATE blood_requests
         SET patient_name = ?,
             age=?,
             gender=?,
             blood_group = ?,
             hospital = ?,
             city = ?
         WHERE request_id = ?`,
        [
            patient_name,
            age,
            gender,
            blood_group,
            hospital,
            city,
            id
        ],
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json({
                message: "Request Updated Successfully"
            });

        }
    );

});

module.exports = router;
