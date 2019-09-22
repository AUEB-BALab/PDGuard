QUnit.test("Test getDataControllers", function(assert) {
    var json =  $.parseJSON('[{"dataController": "ABC"}, {"dataController": "FGH"}'
            + ', {"dataController": "ABC"}]');
    var dataControllers = getDataControllers(json);
    var keys = [];
    for (var dataController in dataControllers)
        keys.push(dataController);
    assert.ok(keys.length == 2);
    assert.equal(keys[0], "ABC");
    assert.equal(keys[1], "FGH");
});

QUnit.test("Test countAuthRules", function(assert) {
    var rows = $("<table><tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td><td>C5</td></tr>"
            + "<tr><td>C1</td><td>C3</td><td>C2</td><td>C4</td><td>C5</td></tr>"
            + "<tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td><td>C5</td></tr></table>");
    assert.equal(countAuthRules(rows, "C3", "C5"), 1);
    rows = $("<table></table>");
    assert.equal(countAuthRules(rows, "C3", "C5"), 0);
    rows = $("<table><tr></tr></table>");
    assert.equal(countAuthRules(rows, "C3", "C5"), 0);
});

QUnit.test("Test getAuthRule", function(assert) {
    var rows = $("<table><tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td><td>C5</td></tr>"
        + "<tr><td>C1</td><td>C3</td><td>C6</td><td>LOCKED</td><td>C4</td></tr>"
        + "<tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td><td>C5</td></tr></table>");
    assert.equal(getAuthRule(rows, "C3", "C4").html(), "<td>C1</td><td>C3</td>"
            + "<td>C6</td><td>LOCKED</td><td>C4</td>");
    rows = $("<table><tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td></tr>"
        + "<tr><td>C1</td><td>C3</td><td>C4</td><td>C4</td></tr>"
        + "<tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td></tr></table>");
    assert.equal(getAuthRule(rows, "C3", "C5"), null);
    rows = $("<table></table>");
    assert.equal(getAuthRule(rows, "C2", "C5"), null);
    rows = $("<table><tr></tr></table>");
    assert.equal(getAuthRule(rows, "C2", "C5"), null);
});

QUnit.test("Test getDataProvenance", function(assert) {
    var rows = $("<table><tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td><td>C5</td>"
    + "<td>C6</td><td>C7</td><td>C8</td></tr>"
    + "<tr><td>C1</td><td>C3</td><td>C6</td><td>LOCKED</td><td>C4</td><td>C5</td>"
    + "<td>C6</td><td>C7</td><td>C8</td></tr>"
    + "<tr><td>C1</td><td>C2</td><td>C3</td><td>C4</td><td>C5</td><td>C6</td><td>C7</td>"
    + "<td>C8</td></tr></table>");
    assert.equal(getDataProvenance(rows, "C2", "C5"), "C8");
    rows = $("<table><tr></tr></table>");
    assert.equal(getDataProvenance(rows, "C2", "C5"), null);
});

QUnit.test("Test getCategory", function(assert) {
    var rows = $("<table><tr><td>C1</td><td>C2</td><td>C3</td></tr></table>");
    assert.equal(getCategory(rows, "C2"), "C3");
    assert.equal(getCategory(rows, "C1"), null);
});
