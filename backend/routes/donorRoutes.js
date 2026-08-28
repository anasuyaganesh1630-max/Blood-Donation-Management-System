const express = require("express");
const router = express.Router();
const db = require("../db");

console.log("donorRoutes.js loaded");

// TEST ROUTE
router.get("/", (req, res) => {
    res.json({
        success: true,
        message: "Donor Route Working"
    });
});

router.post("/donor", (req, res) => {

    const {
        name,
        email,
        password,
        age,
        gender,
        blood_group,
        city,
        phone
    } = req.body;

    if (
        !name ||
        !email ||
        !password ||
        !age ||
        !gender ||
        !blood_group ||
        !city ||
        !phone
    ) {
        return res.status(400).json({
            message: "Please fill all donor fields"
        });
    }

    const sql = `
        INSERT INTO donors
        (
            name,
            email,
            password,
            age,
            gender,
            blood_group,
            city,
            phone,
            status
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;

    db.query(
        sql,
        [
            name,
            email,
            password,
            age,
            gender,
            blood_group,
            city,
            phone,
            "pending"
        ],
        (err, result) => {

            if (err) {

                console.error(
                    "DONOR REGISTRATION ERROR:",
                    err
                );

                return res.status(500).json({
                    message: "Failed to register donor",
                    error: err.message
                });
            }

            return res.status(201).json({
                success: true,
                message:
                    "Donor registration submitted. Please wait for admin approval.",
                donor_id: result.insertId
            });
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
// ==========================================
// APPROVED DONOR BLOOD GROUP STATISTICS
// ==========================================

router.get("/stats", (req, res) => {

    const sql = `
        SELECT
            d.blood_group,
            COUNT(*) AS total
        FROM donors d
        INNER JOIN users u
            ON d.email = u.email
        WHERE u.role = 'donor'
        AND u.status = 'approved'
        GROUP BY d.blood_group
        ORDER BY d.blood_group
    `;

    db.query(sql, (err, result) => {

        if (err) {
            console.log(err);
            return res.status(500).json(err);
        }

        res.json(result);

    });

});

// City Statistics
router.get("/citystats", (req, res) => {

    const sql = `
        SELECT
            d.city,
            COUNT(*) AS total
        FROM donors d
        INNER JOIN users u
            ON d.email = u.email
        WHERE u.role = 'donor'
        AND u.status = 'approved'
        GROUP BY d.city
        ORDER BY total DESC
    `;

    db.query(sql, (err, result) => {

        if (err) {
            console.log(err);
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
router.get("/approved", (req, res) => {

    const sql = `
        SELECT
            d.donor_id,
            d.name,
            d.age,
            d.gender,
            d.email,
            d.blood_group,
            d.city,
            d.phone
        FROM donors d
        INNER JOIN users u
            ON d.email = u.email
        WHERE u.role = 'donor'
        AND u.status = 'approved'
        ORDER BY d.name ASC
    `;

    db.query(sql, (err, results) => {

        if (err) {

            console.log(err);

            return res.status(500).json({
                message: "Failed to load approved donors"
            });

        }

        res.json(results);

    });

});
// ==========================================
// UPDATE APPROVED DONOR USER
// ==========================================

router.put("/user-update/:id", (req, res) => {

    const { id } = req.params;

    const {
        name,
        email
    } = req.body;

    const sql = `
        UPDATE users
        SET name = ?, email = ?
        WHERE id = ?
        AND role = 'donor'
    `;

    db.query(
        sql,
        [name, email, id],
        (err, result) => {

            if (err) {

                console.log(err);

                return res.status(500).json({
                    message: "Failed to update donor"
                });
            }

            res.json({
                message: "Donor updated successfully"
            });

        }
    );
});


// ==========================================
// DELETE APPROVED DONOR
// ==========================================

router.delete("/user-delete/:id", (req, res) => {

    const { id } = req.params;

    const sql = `
        DELETE FROM users
        WHERE id = ?
        AND role = 'donor'
    `;

    db.query(
        sql,
        [id],
        (err, result) => {

            if (err) {

                console.log(err);

                return res.status(500).json({
                    message: "Failed to delete donor"
                });
            }

            res.json({
                message: "Donor deleted successfully"
            });

        }
    );
});
module.exports = router;