const express = require("express");
const router = express.Router();

const db = require("../db");


router.post("/login", (req, res) => {

    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({
            message: "Email and password are required"
        });
    }

    const sql = `
        SELECT id, name, email, role, status
        FROM users
        WHERE email = ? AND password = ?
    `;

    db.query(
        sql,
        [email, password],
        (err, results) => {

            if (err) {

                console.log(err);

                return res.status(500).json({
                    message: "Database error"
                });
            }

            // User doesn't exist
            if (results.length === 0) {

                return res.status(401).json({
                    message: "Invalid email or password"
                });
            }

            const user = results[0];


            // ==========================
            // CHECK ACCOUNT STATUS
            // ==========================

            if (user.role !== "admin" && user.status === "pending") {

                return res.status(403).json({
                    message:
                        "Your registration is waiting for admin approval."
                });
            }


            if (user.role !== "admin" && user.status === "rejected") {

                return res.status(403).json({
                    message:
                        "Your registration has been rejected by the administrator."
                });
            }


            // ==========================
            // LOGIN SUCCESS
            // ==========================

            res.json({

                message: "Login successful",

                user: {
                    id: user.id,
                    name: user.name,
                    email: user.email,
                    role: user.role,
                    status: user.status
                }

            });

        }
    );

});


module.exports = router;