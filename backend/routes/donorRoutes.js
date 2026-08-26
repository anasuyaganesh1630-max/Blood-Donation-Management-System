const express = require("express");
const router = express.Router();
const db = require("../db");

// Test Route
router.get("/", (req, res) => {
    res.send("Donor Route Working");
});

// Add Donor
router.post("/add", (req, res) => {

    const {
        name,
        age,
        gender,
        email,
        blood_group,
        city,
        phone
    } = req.body;

    const sql = `
        INSERT INTO donors
        (name, age, gender, email, blood_group, city, phone)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    `;

    db.query(
        sql,
        [name, age, gender, email, blood_group, city, phone],
        (err, result) => {

            if (err) {
                console.log(err);
                return res.status(500).json(err);
            }

            res.json({
                message: "Donor Registered Successfully"
            });

        }
    );
});

// View All Donors
router.get("/all", (req, res) => {

    db.query(
        "SELECT * FROM donors",
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json(result);

        }
    );

});
router.get("/compatible/:bloodGroup", (req, res) => {

    const bloodGroup = req.params.bloodGroup;

    const compatibility = {

        "A+": ["A+", "A-", "O+", "O-"],
        "A-": ["A-", "O-"],

        "B+": ["B+", "B-", "O+", "O-"],
        "B-": ["B-", "O-"],

        "AB+": ["A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"],
        "AB-": ["A-", "B-", "AB-", "O-"],

        "O+": ["O+", "O-"],
        "O-": ["O-"]
    };

    const compatibleGroups =
    compatibility[bloodGroup];

    db.query(
        "SELECT * FROM donors WHERE blood_group IN (?)",
        [compatibleGroups],
        (err, result) => {

            if(err){
                return res.status(500).json(err);
            }

            res.json(result);

        }
    );

});
router.get("/search/:blood_group", (req, res) => {

    const blood_group = req.params.blood_group;

    db.query(
        "SELECT * FROM donors WHERE blood_group = ?",
        [blood_group],
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json(result);

        }
    );

});

// Blood Group Statistics
router.get("/stats", (req, res) => {

    const sql = `
        SELECT blood_group,
        COUNT(*) AS total
        FROM donors
        GROUP BY blood_group
    `;

    db.query(sql, (err, result) => {

        if (err) {
            return res.status(500).json(err);
        }

        res.json(result);

    });

});

// City Statistics
router.get("/citystats", (req, res) => {

    const sql = `
        SELECT city,
        COUNT(*) AS total
        FROM donors
        GROUP BY city
    `;

    db.query(sql, (err, result) => {

        if (err) {
            return res.status(500).json(err);
        }

        res.json(result);

    });

});

// Update Donor
router.put("/update/:id", (req, res) => {

    const id = req.params.id;

    const {
        name,
        age,
        gender,
        email,
        blood_group,
        city,
        phone
    } = req.body;

    const sql = `
        UPDATE donors
        SET
            name = ?,
            age = ?,
            gender = ?,
            email = ?,
            blood_group = ?,
            city = ?,
            phone = ?
        WHERE donor_id = ?
    `;

    db.query(
        sql,
        [
            name,
            age,
            gender,
            email,
            blood_group,
            city,
            phone,
            id
        ],
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json({
                message: "Donor Updated Successfully"
            });

        }
    );

});

// Delete Donor
router.delete("/delete/:id", (req, res) => {

    const id = req.params.id;

    db.query(
        "DELETE FROM donors WHERE donor_id = ?",
        [id],
        (err, result) => {

            if (err) {
                return res.status(500).json(err);
            }

            res.json({
                message: "Donor Deleted Successfully"
            });

        }
    );

});

router.get("/all", (req, res) => {

    const sql = "SELECT * FROM donors";

    db.query(sql, (err, result) => {

        if(err){
            return res.status(500).json(err);
        }

        res.json(result);
    });
});
module.exports = router;