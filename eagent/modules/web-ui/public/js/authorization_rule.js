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
 * This functions returns a set of data controllers who hold data of data
 * subject.
 *
 * It gets a JSON array with all the authorization rules which data subject
 * has defined and get the list of data controllers which is included in JSON
 * array and it stores them to a set of data controllers.
 *
 * @param data JSON array which contains all authorization rules defined by
 * data subject.
 * @returns {{}} Set of data controllers.
 */
function getDataControllers(data) {
    var dataControllers = {};
    for (var i = 0; i < data.length; i++)
        dataControllers[data[i].dataController] = true;
    return dataControllers;
}

/**
 * This functions gets the row where a specific authorization rule is placed
 * on table which is displayed to user (data subject) of web UI.
 *
 * This authorization rule must be associated with specific type of data, data
 * controller as well as there must be no allowable action to performed on these
 * data. (Column of data use must contains 'LOCKED').
 *
 * If there is not such authorization rule, then null is returned.
 *
 * @param table Table of authorization rules.
 * @returns {*} Row of table (authorization rule).
 */
function getAuthRule(table) {
    var authRule = null;
    table.find("tr").each(function () {
        var row = $(this);
        var dataUseColumn = $.trim(row.find("td:eq(1)").html());
        if (dataUseColumn == "LOCKED") {
            authRule = $(this);
        }
    });
    return authRule;
}
