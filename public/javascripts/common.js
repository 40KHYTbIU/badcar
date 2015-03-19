/**
 * Created by Mike on 17/03/15.
 */
var notifyModal;

function subscribe() {
    var $username = $('#user-name');
    var $number = $('#car-number');
    var $email = $('#user-email');
    var serverUrl = '/subscribe';

    if ($username.val() != "" && $number.val() != "" && $email.val() != "") {
        var oData = JSON.stringify({'username':$username.val(),'number':$number.val(), 'email': $email.val()});
        $.ajax({
            type: "POST",
            url: serverUrl,
            data: oData,
            processData: false,
            contentType: "application/json",
            success: function (data) {
                if (data == "Ok") notifyModal.modal('hide');
                else alert("Error: " + data);
            },
            error:   function (data) { alert("Error: " + data);   }
        });
    } else
        alert("Input all fileds!");
}


$(document).ready(function () {
    notifyModal = $('#notifyModal');
    var $subscribe = $('#subscribe');
    $subscribe.click(function () { subscribe() });
});