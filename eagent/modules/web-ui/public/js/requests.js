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

/**
 * This function gets the authorization rules of data subject every time page is
 * loaded.
 */
$(function() {
    $("#from").datepicker("setDate", new Date());
    $("#to").datepicker("setDate", new Date());
    var today = new Date();
    var tomorrow = new Date(today.getTime() + 24 * 60 * 60 * 1000);
    if (document.location.href.match(/[^\/]+$/)[0] == "mainpage") {
        getAuthRules($("#panel"), $(".data-controller"));
        getAuthLogs($("#auth-logs-table"), today.getYear() + "-" + today.getMonth()
            + "-" + today.getDay(), tomorrow.getYear() + "-" + tomorrow.getMonth()
            + "-" + tomorrow.getDay())
    }
});

/**
 * This function creates an AJAX request in order to get the authorization rules
 * of data subject.
 *
 * It sends an AJAX request to server side to retrieve and display all
 * authorization rules which data subject (user of web UI) has defined so far.
 *
 * Then server returns back a JSON array which includes all authorization rules
 * as requested and then a function is used to display them in friendly format
 * for data subject.
 *
 * @param element Table element to be filled with authorization rules.
 * @param select HTML element to filled with data controllers as options.
 */
function getAuthRules(element, select) {
    $.ajax({
        url: "/rules",
        data: {

        },
        dataType: 'json',
        success: function(data) {
            displayAuthRules(element, data);
            loadDataControllers(select, getDataControllers(data));
        },
        error: function() {
            alert("Something wrong");
        }
    });
}

/**
 * This function creates an AJAX request in order to add a new authorization
 * rule to the list of data subject.
 *
 * It sends an AJAX request to server side to add new authorization rule to
 * to the list of authorization rules which data subject (user of web UI)
 * has defined so far.
 *
 * Then server returns back a JSON object which includes an explanatory
 * message to be displayed to data subject and id of the authorization rule in
 * case the addition of it succeeded.
 *
 * @param dataType An array with all data types which user wants to lock.
 * @param dataUse Allowable action performed on data.
 * @param dataProvenance Allowable provenance of data.
 * @param dataController Name of data controller who holds data.
 * @param validTo Expiration date of authorization rule.
 * @param isAction True if an allowable action is going to be added;
 * false if an allowable provenance is going to be added.
 */
function addAuthRule(dataType, dataUse, dataProvenance,
        dataController, validTo, isAction) {
    var dialog = $("#alert-dialog");
    var json = {};
    json["dataType"] = dataType;
    if (dataUse != null)
        json["dataUse"] = dataUse;
    if (dataProvenance != null)
        json["dataProvenance"] = dataProvenance;
    json["dataController"] = dataController;
    if (validTo != "")
        json["validTo"] = validTo;
    else {
        validTo = new Date().setYear(new Date().getFullYear() + 2);
    }
    $.ajax({
        url: "/create",
        data: JSON.stringify(json),
        dataType: 'json',
        type: "POST",
        contentType: "application/json; charset=utf-8",
        success: function(data) {
            var message = "<h3>Result of your action: </h3>";
            for (var i = 0; i < dataType.length; i++) {
                var result = data[dataType[i]];
                message += "<b>Data: </b>" + dataType[i] + "<br><b>Result: </b>"
                        + result.message + "<br>";
                var element = $("#" + (isAction ? "actions-table-" : "provenances-table-")
                    + dataController + "-" + dataType[i]);
                if (result.id != undefined) {
                    if (isAction)
                        addAction(element, result.id, dataUse, validTo);
                    else
                        addProvenance(element, result.id, dataProvenance, validTo);
                }
            }
            openDialog(dialog, message, 600, 600);
        },
        error: function() {
            alert("Something wrong");
        }
    });
}

/**
 * Add new allowable action to the corresponding HTML table.
 *
 * @param table HTML table.
 * @param id Id of allowable action.
 * @param dataUse Allowable use of data.
 * @param validTo Expiration date of authorization rule.
 */
function addAction(table, id, dataUse, validTo) {
    var authRule = getAuthRule(table);
    if (authRule != null)
        updateAllowableActionTable(authRule, id, dataUse,
            new Date().toDateString(), validTo);
    else
        addAllowableAction(table, id, dataUse,
            new Date().getTime(), validTo);
}

/**
 * Add new allowable provenance to the corresponding HTML table.
 *
 * @param table HTML table.
 * @param id Id of allowable provenance.
 * @param dataProvenance Allowable provenance of data.
 * @param validTo Expiration date of authorization rule.
 */
function addProvenance(table, id, dataProvenance, validTo) {
    var authRule = getAuthRule(table);
    if (authRule != null)
        updateAllowableProvenanceTable(table, id, dataProvenance,
            new Date().getTime(), validTo);
    else
        addAllowableProvenance(table, id, dataProvenance,
            new Date().getTime(), validTo);
}

/**
 * This function creates an AJAX request in order to update an existing
 * allowable action rule from the list of data subject.
 *
 * It sends an AJAX request to server side to update an existing authorization
 * rule associated with the allowable action of data from the list of
 * authorization rules which data subject (user of web UI)
 * has defined so far.
 *
 * Then server sends back its response in a text format which includes an
 * explanatory message to be displayed to data subject and then updates
 * authorization rule with the new parameters.
 *
 * @param element Row of table (authorization rule) to be updated.
 * @param id ID of authorization rule.
 * @param dataType Type of data.
 * @param dataUse Allowable use of data.
 * @param dataController Name of organization which hold data.
 * @param validTo Expiration date of authorization rule.
 */
function updateAllowableAction(element, id, dataType, dataUse,
        dataController, validTo) {
    var dialog = $("#alert-dialog");
    $.ajax({
        url: "/update_action",
        data: {
            dataType: dataType,
            dataUse: dataUse,
            dataController: dataController,
            validTo: validTo,
            id: id
        },
        dataType: 'text',
        success: function(data) {
            validTo = validTo == "" ? new Date().setYear(new Date().getFullYear() + 2):
                validTo;
            openDialog(dialog, data, 250, 300);
            if (data == "Your authorization rule was updated successfully")
                updateAllowableActionTable(element, id, dataUse,
                    new Date().toDateString(), validTo);
        },
        error: function() {
            alert("Something wrong");
        }
    });
}

/**
 * This function creates an AJAX request in order to update an existing
 * allowable action rule from the list of data subject.
 *
 * It sends an AJAX request to server side to update an existing authorization
 * rule associated with the allowable provenance of data from the list of
 * authorization rules which data subject (user of web UI)
 * has defined so far.
 *
 * Then server sends back its response in a text format which includes an
 * explanatory message to be displayed to data subject and then updates
 * authorization rule with the new parameters.
 *
 * @param element Row of table (authorization rule) to be updated.
 * @param id ID of authorization rule.
 * @param dataType Type of data.
 * @param dataProvenance Allowable provenance of data.
 * @param dataController Name of organization which hold data.
 * @param validTo Expiration date of authorization rule.
 */
function updateAllowableProvenance(element, id, dataType, dataProvenance,
        dataController, validTo) {
    var dialog = $("#alert-dialog");
    $.ajax({
        url: "/update_provenance",
        data: {
            dataType: dataType,
            dataProvenance: dataProvenance,
            dataController: dataController,
            validTo: validTo,
            id: id
        },
        dataType: 'text',
        success: function(data) {
            validTo = validTo == "" ? new Date().setYear(new Date().getFullYear() + 2):
                validTo;
            openDialog(dialog, data, 250, 300);
            if (data == "Your authorization rule was updated successfully")
                updateAllowableProvenanceTable(element, id, dataProvenance,
                    new Date().toDateString(), validTo);
        },
        error: function() {
            alert("Something wrong");
        }
    });
}

/**
 * This function creates an AJAX request in order to delete a specific
 * authorization rule from the list of data subject.
 *
 * It sends an AJAX request to server side to delete a specific authorization
 * rule from the list of authorization rules which data subject (user of web UI)
 * has defined so far.
 *
 * Then server sends its response in a text format and it includes an explanatory
 * message to be displayed to data subject in order to inform him if the
 * deletion of authorization rule was successful or not.
 *
 * @param row Row of table (authorization rule) which is going to be deleted.
 * @param removeAction True if an allowable action is going to be deleted;
 * false if an allowable provenance is going to be deleted.
 *
 * @param id ID of authorization rule which is going to be deleted.
 */
function deleteAuthRule(row, id, removeAction) {
    var dialog = $("#alert-dialog");
    $.ajax({
        url: "/delete",
        data: {
            id: id,
            removeAction: removeAction
        },
        dataType: 'text',
        success: function(data) {
            openDialog(dialog, data, 250, 300);
            var table = row.parent();
            if (data == "Your authorization rule was deleted successfully")
                deleteAuthRuleFromTable(row, table.find("tr").length);
        },
        error: function() {
            alert("Something wrong");
        }
    });
}

/**
 * This function creates an AJAX request in order to get the authorization logs
 * associated with data subject
 *
 * It sends an AJAX request to server side to retrieve and display all
 * authorization requests which have been conducted in the period defined by
 * data subject.
 *
 * Then server returns back a JSON array which includes all information
 * describing authorization requests and then a function is used to display them
 * in friendly format for data subject.
 *
 * @param element Table which includes authorization rules.
 * @param from Start date of period.
 * @param to End date of period.
 */
function getAuthLogs(element, from, to) {
    $.ajax({
        url: "/logs",
        data: {
            from: from,
            to: to
        },
        dataType: 'json',
        success: function(data) {
            displayAuthLogs(element, data);
        },
        error: function() {
            alert("Something wrong");
        }
    });
}

/**
 * This function creates an AJAX request in order to lock specific data types.
 *
 * Then server returns back a JSON array which includes an explanatory message
 * to user if the lock of the data was successful or not.
 *
 * @param element Table element to be filled with authorization rules.
 * @param select HTML element to filled with data controllers as options.
 * @param dataTypes An array with all data types which user wants to lock.
 * @param dataController Name of data controller who holds data.
 */
function lockData(element, select, dataTypes, dataController) {
    var dialog = $("#alert-dialog");
    var json = {};
    json["dataTypes"] = dataTypes;
    json["dataController"] = dataController;
    $.ajax({
        url: "/lock",
        data: JSON.stringify(json),
        dataType: 'json',
        type: "POST",
        contentType: "application/json; charset=utf-8",
        success: function(data) {
            var message = "<h3>Result of your action: </h3>";
            for (var i = 0; i < dataTypes.length; i++) {
                var response = data[dataTypes[i]];
                message += "<b>Data: </b>" + dataTypes[i] + "<br><b>Result: </b>"
                + response + "<br>";
            }
            openDialog(dialog, message, 600, 600);
            var fixedElements = $("#fixed-elem").detach();
            element.empty().append(fixedElements);
            $(".actions").remove();
            $(".provenances").remove();
            select.empty();
            getAuthRules(element, select);
        },
        error: function() {
            alert("Something wrong");
        }
    });
}

/**
 * This function creates an AJAX request in order to set
 * data types updatable.
 *
 * Then server returns back a JSON array which includes an explanatory message
 * to user if the action was successful or not.
 *
 * @param dataTypes An array with all data types which user wants to lock.
 * @param dataController Name of data controller who holds data.
 * @param updatable True if data type is going to be updatable; false otherwise.
 */
function setUpdatable(dataTypes, dataController, updatable) {
    var dialog = $("#alert-dialog");
    var json = {};
    json["dataTypes"] = dataTypes;
    json["dataController"] = dataController;
    json["updatable"] = updatable;
    $.ajax({
        url: "/updatable",
        data: JSON.stringify(json),
        dataType: 'json',
        type: "POST",
        contentType: "application/json; charset=utf-8",
        success: function(data) {
            var message = "<h3>Result of your action: </h3>";
            for (var i = 0; i < dataTypes.length; i++) {
                var response = data[dataTypes[i]];
                message += "<b>Data: </b>" + dataTypes[i] + "<br><b>Result: </b>"
                    + response + "<br>";
            }
            openDialog(dialog, message, 600, 600);
        },
        error: function() {
            alert("Something wrong");
        }
    });
}
