$.get("changes.json", renderChanges);

var changesGlobal = null;

function renderChanges(changes) {
    changesGlobal = changes.paths;
    var $tpl = $("#tpl");
    var $mainDiv = $("#main-div");

    for(var i = 0; i < changes.paths.length; i++) {
        var change = changes.paths[i];
        var $row = $tpl.clone();
        $row.removeAttr('id');
        $row.find('.path').append(change.path);
        if(i % 2 !== 1) {
            $row.addClass('bg-light');
        }

        var $branches = $row.find('.branches');
        $branches.addClass(calculateTextColor(change));
        $row.find('.glyphicon-plus').click(toggleBranches($row, i));
        $branches.append('<span>' + change.branches.length + ' branches</span>');
        $mainDiv.append($row);
        $row.show();
    }
}

function toggleBranches($element, index) {
    return function() {
        var change = changesGlobal[index];
        var $branches = $element.find(".branches");
        $branches.empty();

        if($branches.hasClass('expanded')) {
            var $minusSign = $element.find('.glyphicon-minus');
            $minusSign.removeClass('glyphicon-minus');
            $minusSign.addClass('glyphicon-plus');

            $branches.append('<span>' + change.branches.length + ' branches</span>');
            $branches.removeClass('expanded');
        } else {
            $branches.addClass('expanded');
            for (var j = 0; j < change.branches.length; j++) {
                var branch = change.branches[j];
                $branches.append('<div>' + branch.branch + ' (' + branch.noOfChanges + ')</div>');
            }
            var $plusSign = $element.find(".glyphicon-plus");
            $plusSign.removeClass('glyphicon-plus');
            $plusSign.addClass('glyphicon-minus');
        }
    }
}

function calculateTextColor(change) {
    var totalChanges = 0;
    for(var i = 0; i < change.branches.length; i++) {
        totalChanges += change.branches[i].noOfChanges;
    }
    if(totalChanges < 50) {
        return '';
    } else if(totalChanges < 100) {
        return 'text-warning';
    } else {
        return 'text-danger';
    }
}