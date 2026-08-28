const express = require("express");
const router = express.Router();

const db = require("../db");


// TEST ROUTE
router.get("/", (req, res) => {
    res.json({
        message: "Blood Request Route Working"
    });
});


// ADD BLOOD REQUEST
router.post("/add", (req, res) => {

    console.log("=================================");
    console.log("BLOOD REQUEST RECEIVED");
    console.log(req.body);
    console.log("=================================");

    const {
        hospital_name,
        patient_name,
        age,
        gender,
        patient_status,
        blood_group,
        units,
        city,
        phone,
        reason
    } = req.body;


    if (
        !hospital_name ||
        !patient_name ||
        !age ||
        !gender ||
        !patient_status ||
        !blood_group ||
        !units ||
        !city ||
        !phone ||
        !reason
    ) {
        return res.status(400).json({
            message: "Please fill all fields"
        });
    }


    const sql = `
        INSERT INTO requests
        (
            hospital_name,
            patient_name,
            age,
            gender,
            patient_status,
            blood_group,
            units,
            city,
            phone,
            reason,
            status
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    `;


    db.query(
        sql,
        [
            hospital_name,
            patient_name,
            age,
            gender,
            patient_status,
            blood_group,
            units,
            city,
            phone,
            reason,
            "Pending"
        ],
        (err, result) => {

            if (err) {

                console.log("DATABASE ERROR:");
                console.log(err);

                return res.status(500).json({
                    message: "Database error: " + err.message
                });
            }

console.log(
    "Blood request saved. ID:",
    result.insertId
);

// ==========================================
// CREATE BLOOD REQUEST NOTIFICATION
// ==========================================

const notificationMessage =
    `New blood request from ${hospital_name} for ${blood_group} blood - ${units} unit(s) for patient ${patient_name}`;

const notificationSql = `
    INSERT INTO notifications
    (message, type, is_read)
    VALUES (?, ?, ?)
`;

db.query(
    notificationSql,
    [
        notificationMessage,
        "blood_request",
        0
    ],
    (notificationErr) => {

        if (notificationErr) {

            console.error(
                "NOTIFICATION ERROR:",
                notificationErr
            );

            // Request was already saved,
            // so don't fail the blood request.
        }

        res.status(201).json({
            success: true,
            message: "Blood request submitted successfully",
            request_id: result.insertId
        });

    }
);

        }
    );

});
// =====================================================
// GET PENDING REQUESTS
// =====================================================

router.get("/pending", (req, res) => {

    const sql = `
        SELECT *
        FROM requests
        WHERE status = 'pending'
        ORDER BY request_id DESC
    `;

    db.query(sql, (err, results) => {

        if (err) {
            console.log("PENDING REQUEST ERROR:", err);

            return res.status(500).json({
                message: "Failed to load pending requests"
            });
        }

        res.json(results);
    });

});


// ==========================================
// APPROVE / REJECT BLOOD REQUEST
// ==========================================

// ==========================================
// APPROVE / REJECT BLOOD REQUEST
// ==========================================

router.put("/status/:id", (req, res) => {

    const { id } = req.params;
    const { status } = req.body;

    // Only these two statuses are allowed
    if (status !== "approved" && status !== "rejected") {
        return res.status(400).json({
            success: false,
            message: "Invalid request status"
        });
    }

    const sql = `
        UPDATE requests
        SET status = ?
        WHERE request_id = ?
    `;

    db.query(sql, [status, id], (err, result) => {

        if (err) {
            console.error("REQUEST STATUS ERROR:", err);

            return res.status(500).json({
                success: false,
                message: "Failed to update blood request",
                error: err.message
            });
        }

        if (result.affectedRows === 0) {
            return res.status(404).json({
                success: false,
                message: "Blood request not found"
            });
        }

        res.json({
            success: true,
            message:
                status === "approved"
                    ? "Blood request approved successfully"
                    : "Blood request rejected successfully"
        });

    });

});


// =====================================================
// DELETE REQUEST
// =====================================================

router.delete("/delete/:id", (req, res) => {

    const { id } = req.params;

    db.query(
        "DELETE FROM requests WHERE request_id = ?",
        [id],
        (err, result) => {

            if (err) {

                console.log("DELETE REQUEST ERROR:", err);

                return res.status(500).json({
                    message: "Failed to delete blood request"
                });

            }

            if (result.affectedRows === 0) {

                return res.status(404).json({
                    message: "Blood request not found"
                });

            }

            res.json({
                message: "Blood request deleted successfully"
            });

        }
    );

});
router.get("/all", (req, res) => {

    const sql = `
        SELECT *
        FROM requests
        ORDER BY request_id DESC
    `;

    db.query(sql, (err, results) => {

        if (err) {

            console.log("GET REQUESTS ERROR:", err);

            return res.status(500).json({
                message: "Failed to load blood requests"
            });

        }

        res.json(results);

    });

});
// ==========================================
// GET APPROVED HOSPITAL BLOOD REQUESTS
// ==========================================

router.get("/approved", (req, res) => {

    const sql = `
        SELECT *
        FROM requests
        WHERE status = 'approved'
        ORDER BY request_id DESC
    `;

    db.query(sql, (err, results) => {

        if (err) {

            console.error(err);

            return res.status(500).json({
                message: "Failed to load approved requests"
            });

        }

        res.json(results);

    });

});
module.exports = router;