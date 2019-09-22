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

var dataTypes = [];

/** This functions initialize all widgets of page. */
$(function() {
    initDialogs();
    initButtons();
    initDatePickers();
    initDynaTree();
    $("#tabs" ).tabs();
    $("select" ).selectmenu();
});

/** This function initializes all jQuery UI dialogs. */
function initDialogs() {
    $("#alert-dialog" ).dialog({
        autoOpen: false,
        modal: true
    });
    dialogNewAction = $( "#add-auth-rule" ).dialog({
        autoOpen: false,
        resizable: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Add": function() {
                var dataController = $("#data-controller").val();
                addAuthRule(dataTypes, $("#data-use" ).val(), null, dataController,
                    $("#valid-to").val(), true);
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
    dialogNewProvenance = $( "#add-allowable-provenance" ).dialog({
        autoOpen: false,
        resizable: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Add": function() {
                var dataController = $("#data-controller-prov").val();
                addAuthRule(dataTypes, null, $("#data-provenance").val(),
                    dataController, $("#valid-to-prov").val(),
                    false);
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
    updateActionDialog = $("#update-dialog-action").dialog({
        autoOpen: false,
        resizable: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Update": function() {
                var id = $.data(this, "id").id;
                var actionId = id.split("-")[1];
                var dataController = id.split("-")[2];
                var dataType = id.split("-")[3];
                updateAllowableAction($("#action-" + actionId),
                    actionId, dataType, $("#data-use2" ).val(),
                    dataController, $("#valid-to2" ).val());
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
    updateProvenanceDialog = $("#update-dialog-provenance").dialog({
        autoOpen: false,
        resizable: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Update": function() {
                var id = $.data(this, "id").id;
                var actionId = id.split("-")[1];
                var dataController = id.split("-")[2];
                var dataType = id.split("-")[3];
                updateAllowableProvenance($("#provenance-" + actionId),
                    actionId, dataType, $("#data-provenance2" ).val(),
                    dataController, $("#valid-to-update-prov" ).val());
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
    lockDialog = $( "#lock-dialog" ).dialog({
        autoOpen: false,
        resizable: false,
        height: 400,
        width: 500,
        modal: true,
        buttons: {
            "Lock": function() {
                var dataController = $("#data-controller2" ).val();
                lockData($("#panel"), $(".data-controller"),
                    dataTypes, dataController);
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
    actionDeletion = $( "#confirm-deletion" ).dialog({
        autoOpen: false,
        resizable: false,
        height:200,
        modal: true,
        buttons: {
            "Delete": function() {
                var id = $.data(this, "id" ).id.split("-")[1];
                deleteAuthRule($("#action-" + id), id, true);
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
    provenanceDeletion = $( "#confirm-deletion" ).dialog({
        autoOpen: false,
        resizable: false,
        height:200,
        modal: true,
        buttons: {
            "Delete": function() {
                var id = $.data(this, "id" ).id.split("-")[1];
                deleteAuthRule($("#provenance-" + id), id, false);
                $( this ).dialog( "close" );
            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        }
    });
}

/** This function initializes all jQuery UI buttons. */
function initButtons() {
    $("button" ).button();
    $( "#add-action" ).button({
        icons: {
            primary: "ui-icon-circle-plus"
        },
        text: true
    }).on( "click", function() {
        dialogNewAction.data("id", this).dialog( "open" );
    });
    $( "#add-provenance" ).button({
        icons: {
            primary: "ui-icon-circle-plus"
        },
        text: true
    }).on( "click", function() {
        dialogNewProvenance.data("id", this).dialog( "open" );
    });
    $( ".lock" ).button({
        icons: {
            primary: "ui-icon-locked"
        },
        text: true
    }).on( "click", function() {
        lockDialog.dialog("open");
    });
    $( "#period" ).button({
        icons: {
            primary: "ui-icon-check"
        },
        text: true
    }).on( "click", function() {
        getAuthLogs($("#auth-logs-table"), $("#from" ).val(),
            $("#to" ).val());
    });
}

/** This function initializes all jQuery UI date pickers. */
function initDatePickers() {
    $(".valid-to" ).datepicker({
        minDate: 1,
        defaultDate: "+1w",
        dateFormat: "yy-mm-dd",
        changeMonth: true,
        changeYear: true
    });
    $( "#from" ).datepicker({
        defaultDate: "+1w",
        changeMonth: true,
        numberOfMonths: 3,
        dateFormat: "yy-mm-dd",
        onClose: function( selectedDate ) {
            $( "#to" ).datepicker( "option", "minDate", selectedDate );
        }
    }, "setDate", new Date());
    $( "#to" ).datepicker({
        defaultDate: "+1w",
        changeMonth: true,
        numberOfMonths: 3,
        dateFormat: "yy-mm-dd",
        onClose: function( selectedDate ) {
            $( "#from" ).datepicker( "option", "maxDate", selectedDate );
        }
    });
}

/**
 * This function initializes tree-based checkboxes based on the hierarchy of
 * data.
 */
function initDynaTree() {
    $(".data-types").dynatree({
        checkbox: true,
        selectMode: 3,
        onSelect: function(select, node) {
            // Display list of selected nodes
            var selNodes = node.tree.getSelectedNodes();
            dataTypes = [];
            for (var i = 0; i < selNodes.length; i++)
                dataTypes.push(selNodes[i].data.key);
        },
        onKeydown: function(node, event) {
            if( event.which == 32 ) {
                node.toggleSelect();
                return false;
            }
        }
    });
}
