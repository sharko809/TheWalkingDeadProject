/**
 * Created by Pavel on 25.04.2016.
 */
$(document).ready(function () {

    var counter = 0;
     $('#thisDiv').on('click', successLogin);
    // $('#logoDiv').on('click', swapElements(counter));


    $("#buttonSignIn").click(function (event) {
        event.preventDefault();
        $.ajax({
            type: 'post',
            url: '/security_check',
            dataType: 'json',
            contentType: "application/json",
            data: JSON.stringify({
                email: $('#j_username').val(),
                password: $('#j_password').val(),
            }),
            success: function (response) {
                if (response.length) {
                    var errors_out = "";
                    for (var i in response) {
                        errors_out += response[i].errorMessage + "</br>"
                    }
                    $('#messageRegistration')
                        .removeClass()
                        .empty();
                    $('#messageSignIn')
                        .addClass('alert alert-danger')
                        .html(errors_out);
                    $('#j_password').val("");
                } else {
                    location.reload();
                }
            },
            error: function (jqXHR, exception) {
                window.location.href = "/error"
            }
        });
    });

    function successLogin () {
        counter++;
        if (counter >= 5) {
            $('#mainLogo').hide();
            $('#mainLolo').show();
            counter = 0;
        } else {
            $('#mainLogo').show();
            $('#mainLolo').hide();
        }
    }
});

