QUnit.test("Test verifyPasswords", function( assert ) {
    var pswrd1 = $("<input type='password' value='password'>");
    var pswrd2 = $("<input type='password' value='password'>");
    assert.ok(verifyPasswords(pswrd1, pswrd2));
    var alertMessage = pswrd2.next();
    assert.equal(alertMessage.text(), "Passwords match");
    pswrd2.val("wrongpassword");
    assert.notOk(verifyPasswords(pswrd1, pswrd2));
    alertMessage = pswrd2.next();
    assert.equal(alertMessage.text(), "Passwords don't match.");
    pswrd1.val("short");
    pswrd2.val("short");
    assert.notOk(verifyPasswords(pswrd1, pswrd2));
    alertMessage = pswrd2.next();
    assert.equal(alertMessage.text(), "Password must contain at least 6 "
        + "characters.");
    pswrd1.val("");
    assert.notOk(verifyPasswords(pswrd1, pswrd2));
    alertMessage = pswrd2.next();
    assert.equal(alertMessage.text(), "Password field is required.");
});

QUnit.test("Test validateEmailValue", function( assert ) {
    var correctEmail1 = "abc@def.gh";
    var correctEmail2 = "abc@def.gh.eb";
    var wrongEmail1 = "abcdfgc";
    var wrongEmail2 = "dsdsd@fsfs";
    var wrongEmail3 = "fddsf.fdsfds.fsdf";
    var wrongEmail4 = "fdf@fsfs.fdsf.fds";
    var emailField = $("<input type='text'>");
    emailField.val(correctEmail1);
    assert.ok(validateEmailValue(emailField));
    emailField.val(correctEmail2);
    assert.ok(validateEmailValue(emailField));
    emailField.val(wrongEmail1);
    assert.notOk(validateEmailValue(emailField));
    emailField.val(wrongEmail2);
    assert.notOk(validateEmailValue(emailField));
    emailField.val(wrongEmail3);
    assert.notOk(validateEmailValue(emailField));
});
