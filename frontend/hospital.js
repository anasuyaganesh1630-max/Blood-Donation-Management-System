async function submitRequest() {

    const data = {
        patient_name: document.getElementById("patient_name").value,
        age: document.getElementById("age").value,
        gender: document.getElementById("gender").value,
        blood_group: document.getElementById("blood_group").value,
        hospital: document.getElementById("hospital_name").value,
        city: document.getElementById("city").value,
        priority: document.getElementById("priority").value
    };

    const response = await fetch(
        "http://localhost:5000/api/requests/add",
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        }
    );

    const result = await response.json();

    alert(result.message);
addNotification(
    "New blood request submitted for " +
    data.patient_name +
    " (" +
    data.blood_group +
    ")"
);
    document.getElementById("requestForm").reset();
}