$("#grab").click(function () {
    var hash = $(location).attr('hash').substr(1);
    var token = hash.substring(hash.lastIndexOf("access_token=") + 13, hash.lastIndexOf("&token_type"));
    $("#token").val(token);
});
