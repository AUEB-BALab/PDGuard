QUnit.test("Test alertMessages", function( assert ) {
    var element = $("<input id='element' type='text'>");
    alertMessages(element, "A message");
    var span = element.next();
    assert.equal(span.attr("id"), "alert-element");
    assert.equal(span.attr("class"), "alert");
    assert.equal(span.text(), "A message");
});

QUnit.test("Test removeMessage", function( assert ) {
    var element = $("<input id='element' type='text'>");
    assert.ok(element.length > 0);
    removeMessage("element");
    assert.ok($("#element").length == 0);
});

QUnit.test("Test displayAuthRules", function (assert) {
    var table = $("<table id='auth-rules-table'><tbody></tbody></table>");
    var json =  $.parseJSON('[{'
        + '"id":"1",'
        + '"dataType":"INITIALS",'
        + '"category":"PERSONAL",'
        + '"dataUse":"COMPOSE EMAIL TO SUBJECT",'
        + '"dataController":"ABC",'
        + '"validFrom":"1273185387",'
        + '"validTo":"1273185387",'
        + '"dataProvenance":"PUBLIC"'
        + '}]');
    displayAuthRules(table, json);
    var tableBody = table.find("tbody");
    assert.equal(table.attr("id"), "auth-rules-table");
    assert.ok(tableBody.length == 1);
    assert.ok(tableBody.find("tr").length == 1);
    var columns = tableBody.find("tr").find("td");
    assert.ok(columns.length == 9);
    assert.equal(columns[0].innerHTML, json[0].id);
    assert.equal(columns[1].innerHTML, json[0].dataType);
    assert.equal(columns[2].innerHTML, json[0].category);
    assert.equal(columns[3].innerHTML, json[0].dataUse);
    assert.equal(columns[4].innerHTML, json[0].dataController);
    assert.equal(columns[5].innerHTML, new Date(json[0].validFrom).toLocaleDateString());
    assert.equal(columns[6].innerHTML, new Date(json[0].validTo).toLocaleString());
    assert.equal(columns[7].innerHTML, json[0].dataProvenance);
    assert.equal(columns[8].innerHTML, '<button id="edit-' + json[0].id
            + '" class="edit"></button><button id="delete-' + json[0].id
            + '" class="delete"></button>');
});

QUnit.test("Test addAuthRuleToTable", function(assert) {
    var table = $("<table id='auth-rules-table'><tbody></tbody></table>");
    var id = 1;
    var dataType = "PERSONAL";
    var dataUse = "ANALYTICS";
    var dataController = "ABC";
    var validFrom = "2015-05-05";
    var validTo = "2017-05-21";
    var dataProvenance = "PUBLIC";
    var category = null;
    addAuthRuleToTable(table, id, dataType, dataUse, dataController, validFrom,
            validTo, dataProvenance, category);
    var tableBody = table.find("tbody");
    assert.equal(table.attr("id"), "auth-rules-table");
    assert.ok(tableBody.length == 1);
    assert.ok(tableBody.find("tr").length == 1);
    var columns = tableBody.find("tr").find("td");
    assert.ok(columns.length == 9);
    assert.equal(columns[0].innerHTML, id);
    assert.equal(columns[1].innerHTML, dataType);
    assert.equal(columns[2].innerHTML, "-");
    assert.equal(columns[3].innerHTML, dataUse);
    assert.equal(columns[4].innerHTML, dataController);
    assert.equal(columns[5].innerHTML, new Date(validFrom).toLocaleDateString());
    assert.equal(columns[6].innerHTML, new Date(validTo).toLocaleDateString());
    assert.equal(columns[7].innerHTML, dataProvenance);
    assert.equal(columns[8].innerHTML, '<button id="edit-' + id
            + '" class="edit"></button><button id="delete-' + id
            + '" class="delete"></button>');
});

QUnit.test("Test loadDataControllers", function(assert) {
    var select = $("<select id='data-controller'></select>");
    select.selectmenu();
    var dataControllers = {"ABC": true, "FGH": true};
    loadDataControllers(select, dataControllers);
    var options = [];
    select.find("option").each(function() {
        options.push($(this).val());
    });
    assert.ok(options.length == 2);
    assert.equal(options[0], "ABC");
    assert.equal(options[1], "FGH");
});

QUnit.test("Test deleteAuthRuleFromTable", function(assert) {
    var row = $("<tr id = 'element'><td>C1</td><td>C2</td><td>C3</td></tr>");
    assert.ok(row.length != 0);
    deleteAuthRuleFromTable(row, 2);
    row = $("#element");
    assert.ok(row.length == 0);
    row = $("<tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td></tr>");
    assert.ok(row.length != 0);
    assert.equal(row.find("td:eq(2)").html(), "C3");
    deleteAuthRuleFromTable(row, 1);
    assert.ok(row.length != 0);
    assert.equal(row.find("td:eq(3)").html(), "LOCKED");
});

QUnit.test("Test updateAuthRuleTable", function(assert) {
    var row = $("<tr id = 'element'><td>C1</td><td>C2</td><td>C3</td>"
            + "<td>C4</td><td>C5</td><td>C6</td><td>C7</td><td>C8</td></tr>");
    updateAuthRuleTable(row, "NC1", "NC2", "NC3", "NC4", "NC5", "NC6", "NC7", "NC8");
    assert.equal(row.find("td:eq(0)").html(), "NC1");
    assert.equal(row.find("td:eq(1)").html(), "NC2");
    assert.equal(row.find("td:eq(2)").html(), "NC8");
    assert.equal(row.find("td:eq(3)").html(), "NC3");
    assert.equal(row.find("td:eq(4)").html(), "NC4");
    assert.equal(row.find("td:eq(5)").html(), "Invalid Date");
    assert.equal(row.find("td:eq(6)").html(), "Invalid Date");
    assert.equal(row.find("td:eq(7)").html(), "NC7");
});

QUnit.test("Test openDialog", function(assert) {
    var dialog = $("<div><span id='icon'></span></div>")
    openDialog(dialog, "message");
    assert.ok(dialog.dialog("isOpen"));
});

QUnit.test("Test displayAuthLogs", function(assert) {
    var dataController = "ABC";
    var app = "CDF";
    var dataType = "NAME";
    var dataUse = "ANALYTICS";
    var interactionPurpose = "ADVERT";
    var requestType = "DECRYPTION";
    var requestDate = "2015-11-10";
    var requestResult = "ALLOWED";
    var data = {};
    data["dataController"] = dataController;
    data["app"] = app;
    data["dataType"] = dataType;
    data["dataUse"] = dataUse;
    data["interactionPurpose"] = interactionPurpose;
    data["requestType"] = requestType;
    data["requestDate"] = requestDate;
    data["requestResult"] = requestResult;
    var json = [data];
    var table = $("<table id='auth-logs-table'><tbody></tbody></table>");
    displayAuthLogs(table, json);
    var tableBody = table.find("tbody");
    assert.equal(table.attr("id"), "auth-logs-table");
    assert.ok(tableBody.length == 1);
    assert.ok(tableBody.find("tr").length == 1);
    var columns = tableBody.find("tr").find("td");
    assert.ok(columns.length == 8);
    assert.equal(columns[0].innerHTML, json[0].dataController);
    assert.equal(columns[1].innerHTML, json[0].app);
    assert.equal(columns[2].innerHTML, json[0].dataType);
    assert.equal(columns[3].innerHTML, json[0].dataUse);
    assert.equal(columns[4].innerHTML, json[0].interactionPurpose);
    assert.equal(columns[5].innerHTML, json[0].requestType);
    assert.equal(columns[6].innerHTML, json[0].requestDate);
    assert.equal(columns[7].innerHTML, json[0].requestResult);
});
