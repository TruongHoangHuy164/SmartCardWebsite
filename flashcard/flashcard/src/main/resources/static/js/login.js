// login.js - Enable 'Remember Me' functionality for login form

$(document).ready(function () {
  var $rememberMeCheckbox = $("#rememberMe");
  var $usernameInput = $('[name="username"]');
  var $passwordInput = $('[name="password"]');

  // Load saved credentials if available
  if (localStorage.getItem("rememberMe") === "true") {
    $rememberMeCheckbox.prop("checked", true);
    $usernameInput.val(localStorage.getItem("savedUsername") || "");
    $passwordInput.val(localStorage.getItem("savedPassword") || "");
  }

  // On form submit, save or clear credentials
  $(".login-form").on("submit", function () {
    if ($rememberMeCheckbox.is(":checked")) {
      localStorage.setItem("rememberMe", "true");
      localStorage.setItem("savedUsername", $usernameInput.val());
      localStorage.setItem("savedPassword", $passwordInput.val());
    } else {
      localStorage.removeItem("rememberMe");
      localStorage.removeItem("savedUsername");
      localStorage.removeItem("savedPassword");
    }
  });
});
