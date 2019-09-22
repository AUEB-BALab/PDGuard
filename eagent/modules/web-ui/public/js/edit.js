/*-
 * Copyright 2014-2015 Thodoris Sotiropoulos
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

var dataTypeHeader = "<thead><tr><th>DataType</th>"
    + "<th>Actions</th><th>Provenances</th>"
    + "</tr></thead><tbody></tbody>";

var actionsHeader = "<thead><tr><th>Id</th>"
    + "<th>Data use</th>"
    + "<th>Valid from</th><th>Valid to</th>"
    + "<th>Edit</th></tr></thead><tbody></tbody>";

var provenanceHeader = "<thead><tr><th>Id</th>"
    + "<th>Data Provenace</th>"
    + "<th>Valid from</th><th>Valid to</th>"
    + "<th>Edit</th></tr></thead><tbody></tbody>";

String.prototype.capitalize = function() {
    return this.charAt(0).toUpperCase() + this.slice(1).toLowerCase();
};

/**
 * This functions creates a <span> tag after a specific HTML element.
 *
 * This <span> tag contains a message given as parameter in order to inform user
 * for the result of one action.
 *
 * For example a <span> tag which contains "Email is available" after form field
 * of email will be created to inform user can use given email.
 *
 * @param element HTML element to create <span> tag after this.
 * @param message Message to be included in <span> tag.
 */
function alertMessages(element, message) {
    var id = "alert-" + element.attr("id");
    removeMessage(id);
    element.after("<span id=" + id + " " +  "class='alert'>" + message + "</span>");
}

/**
 * Remove element with the given id from page.
 *
 * @param id Id of HTML element which is going to be removed.
 */
function removeMessage(id) {
    var element = $("#" + id);
    if (element.length > 0)
        element.remove();
}

/**
 * This function fills the body of a table according to data contained to JSON
 * array passed as parameter.
 *
 * Every item contained in JSON array is a JSON object with values of the id,
 * data type, data use, data controller, valid from, valid to so that every
 * column of table is filled respectively.
 *
 * Each JSON object of JSON array is a different row of the table.
 *
 * @param element Table element to filled with authorization rules.
 * @param data JSON array which includes data of authorization rules of a data
 * subject.
 */
function displayAuthRules(element, data) {
    var alertMessage = $("#auth-rules-alert");
    if (data.length == 0) {
        element.hide();
        alertMessage.show();
    } else {
        element.show();
        alertMessage.hide();
        for (var i = 0; i < data.length; i++) {
            var dataController = data[i].dataController;
            var dataType = data[i].dataType;
            var actions = data[i].actions;
            var provenances = data[i].provenances;
            var table = createAuthRuleTable(element, dataController);
            var allowableActions = createAllowableActions(dataController, dataType);
            var allowableProvenances = createAllowableProvenances(dataController,
                dataType);
            addAllowableActions($("#actions-table-" + dataController + "-" +
                dataType), actions);
            addAllowableProvences($("#provenances-table-" + dataController + "-" +
                dataType), provenances);
            addAuthorizationRule(table, dataController, dataType,
                allowableActions, allowableProvenances);
            $("#" + dataType).click(function(event) {
                setUpdatable([event.target.id], event.target.className,
                    event.target.checked);
            });
        }
    }
}

/**
 * Creates a new table of authorization rules if there is not already
 * one.
 *
 * @param element Parent HTML element which contains table of authorization
 * rules.
 * @param dataController Data controller organization which is associated with
 * authorization rules.
 * @returns {*|jQuery|HTMLElement} Table of authorization rules.
 */
function createAuthRuleTable(element, dataController) {
    if ($("#table-" + dataController).length == 0)
        element.append("<table class='table table-striped'"
            + "id='table-" + dataController + "'>"
            + "<caption>" + dataController + "</caption>" + dataTypeHeader
            + "</table>");
    return $("#table-" + dataController);
}

/**
 * This method creates a new dialog window which contains the allowable
 * actions of data.
 *
 * @param dataController Data controller's organization which stores data.
 * @param dataType Type of data.
 * @returns {jQuery} Dialog window of allowable actions of data.
 */
function createAllowableActions(dataController, dataType) {
    var id = dataController + "-" + dataType;
    $("body").append('<div class="actions" id="' + id
        + '" title="Allowable actions">'
        + '<div class="jumbotron"><table id="actions-table-'
        + id + '" class="table table-striped">'
        + actionsHeader + '</table></div></div>"');
    return $('#' + id).dialog({
        autoOpen: false,
        resizable: false,
        width: 900,
        modal: true
    });
}

/**
 * This method creates a new dialog window which contains the allowable
 * provenances of data.
 *
 * @param dataController Data controller's organization which stores data.
 * @param dataType Type of data.
 * @returns {jQuery} Dialog window of allowable provenances of data.
 */
function createAllowableProvenances(dataController, dataType) {
    var id = dataController + "-" + dataType;
    $("body").append('<div class="provenances" id="provenance-' + id
        + '" title="Allowable provenances">'
        + '<div class="jumbotron"><table id="provenances-table-'
        + id + '" class="table table-striped">'
        + provenanceHeader + '</table></div></div>"');
    return $('#provenance-' + id).dialog({
        autoOpen: false,
        resizable: false,
        width: 900,
        modal: true
    });
}

/**
 * Add all allowable actions associated with a data controller and
 * data type to the corresponding table.
 *
 * @param element HTML table of allowable actions.
 * @param actions JSON array with allowable actions.
 */
function addAllowableActions(element, actions) {
    for (var j = 0; j < actions.length; j++)
        addAllowableAction(element, actions[j].id,
            actions[j].dataUse, actions[j].validFrom,
            actions[j].validTo);
}

/**
 * Add all allowable provenances associated with a data controller and
 * data type to the corresponding table.
 *
 * @param element HTML table of allowable provenances.
 * @param provenances JSON array with allowable provenances.
 */
function addAllowableProvences(element, provenances) {
    for (var j = 0; j < provenances.length; j++)
        addAllowableProvenance(element, provenances[j].id,
            provenances[j].dataProvenance, provenances[j].validFrom,
            provenances[j].validTo);
}

/**
 * This functions add information about new authorization action to the table of
 * the already defined authorization actions to a specific data type
 * by data subject.
 *
 * @param element element Table element to be filled with new authorization rule
 * in case addition of it succeeded.
 * @param id ID of allowable action.
 * @param dataUse Allowable use of data.
 * @param validFrom Date when validity period of this rule begins.
 * @param validTo Date when validity period of this rules ends.
 */
function addAllowableAction(element, id, dataUse, validFrom, validTo) {
    if (dataUse != null)
        dataUse = dataUse.replace(new RegExp("_", 'g'), " ");
    var elementId = element.attr("id");
    var dataContoller = elementId.split("-")[2];
    var dataType = elementId.split("-")[3];
    element.find("tbody").append("<tr id='action-" + id + "'><td>" + id + "</td>"
        + "<td>" + ((dataUse == null) ? "LOCKED": dataUse.charAt(0).toUpperCase()
        + dataUse.slice(1).toLowerCase()) + "</td>"
        + "<td>" + new Date(validFrom).toLocaleDateString() + "</td>"
        + "<td>" + new Date(validTo).toLocaleDateString() + "</td>"
        + "<td><button id='edit-"+ id + "-" + dataContoller + "-" + dataType
        + "' class='edit-action'></button>"
        + "<button id='delete-" + id + "-" + dataContoller + "-" + dataType
        + "' class='delete-action'></button>"
        + "</td></tr>");
    $(".edit-action").button({
        icons: {
            primary: "ui-icon-wrench"
        },
        text: false
    }).on( "click", function() {
        updateActionDialog.data('id', this).dialog( "open" );
    });
    $(".delete-action").button({
        icons: {
            primary: "ui-icon-trash"
        },
        text: false
    }).on( "click", function() {
        actionDeletion.data('id', this).dialog( "open" );
    });
}

/**
 * This functions add information about new authorization provenance to the
 * table of the already defined authorization provenances to a specific
 * data type by data subject.
 *
 * @param element element Table element to be filled with new authorization rule
 * in case addition of it succeeded.
 * @param id ID of allowable action.
 * @param dataProvenance Allowable provenance of data.
 * @param validFrom Date when validity period of this rule begins.
 * @param validTo Date when validity period of this rules ends.
 */
function addAllowableProvenance(element, id, dataProvenance, validFrom, validTo) {
    if (dataProvenance != null)
        dataProvenance = dataProvenance.replace(new RegExp("_", 'g'), " ");
    var elementId = element.attr("id");
    var dataContoller = elementId.split("-")[2];
    var dataType = elementId.split("-")[3];
    element.find("tbody").append("<tr id='provenance-" + id + "'><td>" + id + "</td>"
        + "<td>" + ((dataProvenance == null) ? "LOCKED"
            : dataProvenance.charAt(0).toUpperCase()
        + dataProvenance.slice(1).toLowerCase()) + "</td>"
        + "<td>" + new Date(validFrom).toLocaleDateString() + "</td>"
        + "<td>" + new Date(validTo).toLocaleDateString() + "</td>"
        + "<td><button id='edit-"+ id + "-" + dataContoller + "-" + dataType
        + "' class='edit-provenance'></button>"
        + "<button id='delete-" + id + "-" + dataContoller + "-" + dataType
        + "' class='delete-provenance'></button>"
        + "</td></tr>");
    $(".edit-provenance").button({
        icons: {
            primary: "ui-icon-wrench"
        },
        text: false
    }).on( "click", function() {
        updateProvenanceDialog.data('id', this).dialog( "open" );
    });
    $(".delete-provenance").button({
        icons: {
            primary: "ui-icon-trash"
        },
        text: false
    }).on( "click", function() {
        provenanceDeletion.data('id', this).dialog( "open" );
    });
}

/**
 * This functions add information about new authorization rule to the table of
 * the already defined authorization rules by data subject.
 *
 * @param element element Table element to be filled with new authorization rule
 * in case addition of it succeeded.
 * @param dataController Data controller's organization which stores data.
 * @param dataTypeUnderScored Type of data.
 * @param allowableActions Dialog window of allowable actions.
 * @param allowableProvenances Dialog window of allowable provenances.
 */
function addAuthorizationRule(element, dataController, dataTypeUnderScored,
        allowableActions, allowableProvenances) {
    var dataType = dataTypeUnderScored.replace(new RegExp("_", 'g'), " ");
    dataController = dataController.replace(new RegExp(" ", 'g'), "_");
    var id = dataController + "-" + dataTypeUnderScored;
    element.find("tbody").append("<tr id='auth-rule-" + dataType + "'>"
        + "<td>" + dataType.charAt(0).toUpperCase() + dataType.slice(1)
            .toLowerCase() + "</td>"
        + "<td><button id='action-" + id
        + "' class='actions'></button></td>"
        + "<td><button id='provenance-" + id
        + "' class='provenances'></buttom></td></tr>");
    $("#action-" + id).button({
        icons: {
            primary: "ui-icon-zoomin"
        },
        text: false
    }).on( "click", function() {
        allowableActions.dialog( "open" );
    });
    $("#provenance-" + id).button({
        icons: {
            primary: "ui-icon-zoomin"
        },
        text: false
    }).on( "click", function() {
        allowableProvenances.dialog( "open" );
    });
}

/**
 * This functions creates a HTML select widget with data controllers who
 * hold data of data subject as options.
 *
 * @param select HTML select to be filled with data controllers as options.
 * @param dataControllers Set of data controllers who hold data of data subject.
 *
 * @returns {{}} Set of all data controllers which hold data of the data
 * subject.
 */
function loadDataControllers(select, dataControllers) {
    for (var dataController in dataControllers)
        select.append("<option value='" + dataController + "'>"
                + dataController + "</option>");
    select.selectmenu("refresh");
}

/**
 * This function removes a specific authorization rule from table displayed to
 * user (data subject) of web UI.
 *
 * If there is only one authorization rule defined on specific data and it is
 * associated with a specific data controller, then function does not delete
 * authorization rule from table but it sets column which represents the
 * use of data empty.
 *
 * @param row Row of table (authorization rule) to be deleted.
 * @param counter Number of authorization rules defined by user and it is
 * associated with specific type of data and data controller.
 */
function deleteAuthRuleFromTable(row, counter) {
    if (counter > 1)
        row.remove();
    else
        $.trim(row.find("td:eq(1)").html("LOCKED"));
}

/**
 * This function updates information associated with a specific allowable action
 * on the table displayed to user (data subject) of web UI.
 *
 * @param element Row of table (authorization rule) to be updated.
 * @param id ID of action rule.
 * @param dataUse Allowable use of data.
 * @param validFrom Date when authorization rule starts to be valid.
 * @param validTo Expiration date of authorization rule.
 */
function updateAllowableActionTable(element, id, dataUse,
        validFrom, validTo) {
    dataUse = dataUse.replace(new RegExp("_", 'g'), " ");
    $.trim(element.find("td:eq(0)").html(id));
    $.trim(element.find("td:eq(1)").html(dataUse.charAt(0).toUpperCase()
        + dataUse.slice(1).toLowerCase()));
    $.trim(element.find("td:eq(2)").html(new Date(validFrom).toLocaleDateString()));
    $.trim(element.find("td:eq(3)").html(new Date(validTo).toLocaleDateString()));
}

/**
 * This function updates information associated with a specific allowable
 * provenance on the table displayed to user (data subject) of web UI.
 *
 * @param element Row of table (authorization rule) to be updated.
 * @param id ID of action rule.
 * @param dataProvenance Allowable provenance of data.
 * @param validFrom Date when authorization rule starts to be valid.
 * @param validTo Expiration date of authorization rule.
 */
function updateAllowableProvenanceTable(element, id, dataProvenance,
        validFrom, validTo) {
    dataProvenance = dataProvenance.replace(new RegExp("_", 'g'), " ");
    $.trim(element.find("td:eq(0)").html(id));
    $.trim(element.find("td:eq(1)").html(dataProvenance.charAt(0).toUpperCase()
        + dataProvenance.slice(1).toLowerCase()));
    $.trim(element.find("td:eq(2)").html(new Date(validFrom).toLocaleDateString()));
    $.trim(element.find("td:eq(3)").html(new Date(validTo).toLocaleDateString()));
}

/**
 * This function creates and opens a dialog window to inform user (data subject)
 * about the outcome of the addition of authorization rule which they have
 * requested.
 *
 * @param dialogComponent Dialog to be opened.
 * @param message Explanatory message about the outcome of request.
 * @param height Height of dialog.
 * @param width Width of dialog.
 */
function openDialog(dialogComponent, message, height, width) {
    alertMessages($("#icon"), message);
    var dialog = dialogComponent.dialog({
        autoOpen: false,
        resizable: false,
        height: height,
        width: width,
        modal: true,
        buttons: {
            "OK": function () {
                $(this).dialog("close");
            }
        }
    });
    dialog.dialog("open");
}

/**
 * This function fills the body of a table according to data contained to JSON
 * array passed as parameter.
 *
 * Every item contained in JSON array is a JSON object with values of the data
 * controller, data type, data use, interactionPurpose, request type,
 * request date, request result so that every column of table is filled
 * respectively.
 *
 * Each JSON object of JSON array is a different row of the table.
 *
 * If there is not any request (JSON array is empty) which has been sent during
 * the period defined by user then an explanatory message displayed to user.
 *
 * @param element Table element to filled with authorization logs.
 * @param data JSON array which includes data of authorization logs of a data
 * subject.
 */
function displayAuthLogs(element, data) {
    element.find("tbody").html("");
    if (element.next().is("p"))
        element.next().remove();
    if (data.length > 0) {
        for (var i = 0; i < data.length; i++) {
            element.find("tbody").append("<tr><td>"
                + ((data[i].dataController == null) ? "UNKNOWN" : data[i].dataController) + "</td>"
                + "<td>" + ((data[i].app == null) ? "UNKNOWN" : data[i].app )+ "</td>"
                + "<td>" + data[i].dataType.replace(new RegExp("_", 'g'), " ")
                    .capitalize() + "</td>"
                + "<td>" + ((data[i].dataUse == null) ? "-": data[i].dataUse.replace(
                        new RegExp("_", 'g'), " ").capitalize()) + "</td>"
                + "<td>" + ((data[i].interactionPurpose == null)
                    ? "-": data[i].interactionPurpose.replace(new RegExp("_", 'g'), " "))
                    .capitalize() + "</td>"
                + "<td>" + ((data[i].dataProvenance == null) ? "-": data[i].dataProvenance.replace(
                    new RegExp("_", 'g'), " ").capitalize()) + "</td>"
                + "<td>" + ((data[i].update == null)
                    ? "-": data[i].update) + "</td>"
                + "<td>" + data[i].requestType.capitalize() + "</td>"
                + "<td>" + data[i].requestDate + "</td>"
                + "<td>" + data[i].requestResult.replace(new RegExp("_", 'g'), " ")
                    .capitalize() + "</td></tr>");
        }
    } else {
        element.after("<p style='text-align: center'>There are not any requests"
            + " associated with your personal data this period.");
    }
}
