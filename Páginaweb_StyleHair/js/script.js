// Inicializar EmailJS
emailjs.init({
    publicKey: "UwD1Ydc9kAdYEPfhR"
});

// Obtener el formulario
const form = document.getElementById("contact-form");

// Escuchar el envío
form.addEventListener("submit", function (e) {

    // Evita que el formulario recargue la página
    e.preventDefault();

    // Si el checkbox no está marcado, enviar "No"
    const checkbox = document.getElementById("subscribe");

    if (!checkbox.checked) {
        checkbox.value = "No";
    } else {
        checkbox.value = "Sí";
    }

    // Enviar el formulario
    emailjs.sendForm(
        "service_3dubh0e",
        "template_0lepvcs",
        this
    )
    .then(() => {

        alert("✅ Tu mensaje fue enviado correctamente.");

        form.reset();

    })
    .catch((error) => {

        console.error(error);

        alert("❌ Ocurrió un error al enviar el mensaje.");

    });

});