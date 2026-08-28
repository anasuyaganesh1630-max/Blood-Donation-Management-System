const express = require("express");
const router = express.Router();

const db = require("../db");

// =====================================================
// GET ALL DONORS
// =====================================================

router.get("/all", (req, res) => {

    const sql = `
        SELECT
            id,
            name,
            email,
            role,
            status
        FROM users
        WHERE role = 'donor' AND status = 'approved'
        ORDER BY id DESC
    `;

    db.query(sql, (err, results) => {

        if (err) {

            console.error("Donor fetch error:", err);

            return res.status(500).json({
                message: "Database error"
            });
        }

        res.json(results);

    });

});


// =====================================================
// GET ONE DONOR
// =====================================================
router.get("/:id", (req, res) => {

    const donorId = req.params.id;

    const sql = `
        SELECT id, name, email, role, status, created_at
        FROM users
        WHERE id = ?
        AND role = 'donor'
    `;

    db.query(
        sql,
        [donorId],
        (err, results) => {

            if (err) {

                console.error(err);

                return res.status(500).json({
                    message: "Database error"
                });

            }

            if (results.length === 0) {

                return res.status(404).json({
                    message: "Donor not found"
                });

            }

            res.json(results[0]);

        }
    );

});

module.exports = router;