const express = require("express");
const router = express.Router();

const db = require("../db");


// ===============================
// HOSPITAL REGISTRATION
// ===============================

router.post("/hospital", (req, res) => {

    const {
        name,
        email,
        password
    } = req.body;

    if (!name || !email || !password) {
        return res.status(400).json({
            message: "Please fill all required fields"
        });
    }

    const sql = `
        INSERT INTO users
        (name, email, password, role, status)
        VALUES (?, ?, ?, ?, ?)
    `;

    db.query(
        sql,
        [
            name,
            email,
            password,
            "hospital",
            "pending"
        ],
        (err, result) => {

            if (err) {

                console.log(err);

                if (err.code === "ER_DUP_ENTRY") {
                    return res.status(400).json({
                        message: "Email already registered"
                    });
                }

                return res.status(500).json({
                    message: "Hospital registration failed"
                });
            }

            res.status(201).json({
                message:
                    "Hospital registration submitted. Please wait for admin approval."
            });

        }
    );

});


// ===============================
// DONOR REGISTRATION
// ===============================

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
            message: "Please fill all required fields"
        });
    }

    const sql = `
        INSERT INTO users
        (name, email, password, role, status)
        VALUES (?, ?, ?, ?, ?)
    `;

    db.query(
        sql,
        [
            name,
            email,
            password,
            "donor",
            "pending"
        ],
        (err, result) => {

            if (err) {

                console.log(err);

                if (err.code === "ER_DUP_ENTRY") {
                    return res.status(400).json({
                        message: "Email already registered"
                    });
                }

                return res.status(500).json({
                    message: "Donor registration failed"
                });
            }

            const donorSql = `
                INSERT INTO donors
                (name, age, gender, email, blood_group, city, phone)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            `;

            db.query(
                donorSql,
                [
                    name,
                    age,
                    gender,
                    email,
                    blood_group,
                    city,
                    phone
                ],
                (donorErr) => {

                    if (donorErr) {

                        console.log(donorErr);

                        return res.status(500).json({
                            message: "Donor details could not be saved"
                        });
                    }

                    res.status(201).json({
                        message:
                            "Donor registration submitted. Please wait for admin approval."
                    });

                }
            );

        }
    );

});

// ==========================================
// GET ALL PENDING REGISTRATIONS
// ==========================================

router.get("/pending", (req, res) => {

    const sql = `
        SELECT id, name, email, role, status, created_at
        FROM users
        WHERE role IN ('donor', 'hospital')
        AND status = 'pending'
        ORDER BY id DESC
    `;

    db.query(sql, (err, results) => {

        if (err) {
            console.log(err);

            return res.status(500).json({
                message: "Failed to load registrations"
            });
        }

        res.json(results);
    });
});


// ==========================================
// APPROVE REGISTRATION
// ==========================================

router.put("/approve/:id", (req, res) => {

    const { id } = req.params;

    const sql = `
        UPDATE users
        SET status = 'approved'
        WHERE id = ?
        AND role IN ('donor', 'hospital')
    `;

    db.query(sql, [id], (err, result) => {

        if (err) {
            console.log(err);

            return res.status(500).json({
                message: "Approval failed"
            });
        }

    

        res.json({
            success:true,
            message: "Registration approved successfully"
        });
    });
});


// ==========================================
// REJECT REGISTRATION
// ==========================================

router.put("/reject/:id", (req, res) => {

 const { id } = req.params;

    const sql = `
        UPDATE users
        SET status = 'rejected'
        WHERE id = ?
        AND role IN ('donor', 'hospital')
    `;

    db.query(sql, [id], (err, result) => {

        if (err) {
            console.log(err);

            return res.status(500).json({
                success:false,
                message: "Rejection failed"
            });
        }


        res.json({
            success:true,
            message: "Registration rejected"
        });
    });
});
// ==========================================
// APPROVED HOSPITALS
// ==========================================

router.get("/hospitals", (req, res) => {

    const sql = `
        SELECT id, name, email, status
        FROM users
        WHERE role = 'hospital'
        AND status = 'approved'
        ORDER BY name ASC
    `;

    db.query(sql, (err, results) => {

        if (err) {

            console.log(err);

            return res.status(500).json({
                message: "Failed to load hospitals"
            });

        }

        res.json(results);

    });

});


// ==========================================
// UPDATE HOSPITAL
// ==========================================

router.put("/hospital-update/:id", (req, res) => {

    const { id } = req.params;

    const {
        name,
        email
    } = req.body;

    const sql = `
        UPDATE users
        SET name = ?, email = ?
        WHERE id = ?
        AND role = 'hospital'
    `;

    db.query(
        sql,
        [name, email, id],
        (err, result) => {

            if (err) {

                console.log(err);

                return res.status(500).json({
                    message: "Failed to update hospital"
                });

            }

            res.json({
                message: "Hospital updated successfully"
            });

        }
    );

});


// ==========================================
// DELETE HOSPITAL
// ==========================================

router.delete("/hospital-delete/:id", (req, res) => {

    const { id } = req.params;

    const sql = `
        DELETE FROM users
        WHERE id = ?
        AND role = 'hospital'
    `;

    db.query(
        sql,
        [id],
        (err, result) => {

            if (err) {

                console.log(err);

                return res.status(500).json({
                    message: "Failed to delete hospital"
                });

            }

            res.json({
                message: "Hospital deleted successfully"
            });

        }
    );

});
// ==========================================
// GET APPROVED HOSPITALS
// ==========================================

router.get("/approved-hospitals", (req, res) => {

    const sql = `
        SELECT
            id,
            name,
            email,
            role,
            status,
            created_at
        FROM users
        WHERE role = 'hospital'
        AND status = 'approved'
        ORDER BY name ASC
    `;

    db.query(sql, (err, results) => {

        if (err) {

            console.log(err);

            return res.status(500).json({
                message: "Failed to load approved hospitals"
            });

        }

        res.json(results);

    });

});
module.exports = router;