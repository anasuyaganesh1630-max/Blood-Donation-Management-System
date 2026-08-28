async function submitRequest(event) {

    if (event) {
        event.preventDefault();
    }

    const data = {
        hospital_name: document.getElementById("hospital").value,
        patient_name: document.getElementById("patient_name").value,
        age: document.getElementById("age").value,
        gender: document.getElementById("gender").value,
        patient_status: document.getElementById("patient_status").value,
        blood_group: document.getElementById("blood_group").value,
        units: document.getElementById("units").value,
        city: document.getElementById("city").value,
        phone: document.getElementById("phone").value,
        reason: document.getElementById("reason").value
    };

    try {

        const response = await fetch(
            "http://localhost:5000/api/blood_Requests/add",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            }
        );

        const result = await response.json();

        if (!response.ok) {
            alert(result.message);
            return;
        }

        alert("Blood request submitted successfully!");

        document.getElementById("bloodRequestForm").reset();

    } catch (error) {

        console.error(error);

        alert(
            "Cannot connect to server. Make sure your backend is running."
        );
    }
}