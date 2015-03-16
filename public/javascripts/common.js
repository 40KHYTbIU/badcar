/**
 * Created by Mike on 17/03/15.
 */

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
            success: function (data) { alert("Success: " + data); },
            error:   function (data) { alert("Error: " + data);   }
        });
    } else
        alert("Input all fileds!");
}


$(document).ready(function () {
   var $subscribe = $('#subscribe');
   $subscribe.click(function(){subscribe()});
});