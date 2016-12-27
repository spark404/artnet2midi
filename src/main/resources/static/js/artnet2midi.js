// Initialization of the page
var serverOffButton = $('#server-off');
var serverOnButton = $('#server-on');

serverOffButton.prop('disabled', true);
serverOffButton.click(clickServerStop);

serverOnButton.prop('disabled', true);
serverOnButton.click(clickServerStart);

updateControllerList();

// Deal with the drop down of the settings
$('#dropdown-interfaces').children()[1].innerHTML='';
$.getJSON( "settings/interfaces", function( data ) {
    $.each( data, function( index, item ) {
        console.info(item);
        addDropdownItem($('#dropdown-interfaces').children()[1], item.interfaceName +  "-" + item.interfaceAddress);
        $('#server-on').prop('disabled', false); // FIXME should be central
    });
});

// Functions
function addDropdownItem(dropdown, title) {
    var item = document.createElement("li");
    var link = document.createElement("a");

    link.setAttribute("href", "#");
    link.onclick = selectDropdownItem;
    link.innerText=title;

    item.appendChild(link);
    dropdown.appendChild(item);
}

function selectDropdownItem(event) {
    if (event.type == "click") {
        clickedElement  = event.target;
        setDropdownTitle(clickedElement.innerText);
    }
}

function setDropdownTitle(title) {
    $('#dropdown-interfaces').children()[0].innerHTML = title + " <b class=\"caret\"></b>"
}

function clickServerStart(event) {
    if (event.type != "click") {
        return false;
    }
    $('#server-on').prop('disabled', true);
    hideErrorMessage();
    console.info("Start clicked");
    $.post("/settings/nodestart", gatherStartDetails(), function(data, status) {
        console.info("Data " + data);
        console.info("Status " + status);
        $('#server-off').prop('disabled', false);
    }).fail(function() {
        displayErrorMessage("Some thing went wrong!");
        $('#server-on').prop('disabled', false);
    });

}

function clickServerStop(event) {
    if (event.type != "click") {
        return false;
    }
    $('#server-off').prop('disabled', true);
    $.post("/settings/nodestop", "", function(data, status) {
        console.info("Data " + data);
        console.info("Status " + status);
        $('#server-on').prop('disabled', false);
    });
}

function gatherStartDetails() {
    var netInterface = $('#dropdown-interfaces').children()[0].childNodes[0].textContent.split("-")[0].trim();
    var network = $('#net').val();
    if (network == "") {
        $("#net").val("0");
        network = "0"
    }
    var subnet = $("#subnet").val();
    if (subnet == "") {
        $("#subnet").val("0");
        subnet = "0"
    }

    return {networkInterface:netInterface, network:network, subnet:subnet}
 }

function displayErrorMessage(message) {
    $("#errormessage").html("<span class=\"glyphicon glyphicon-exclamation-sign\" aria-hidden=\"true\"></span>" +
    "<span class=\"sr-only\">Error:</span>" +
    " " + message);
    $("#errormessage").removeClass("hidden");
}

function hideErrorMessage() {
    $("#errormessage").addClass("hidden");
}

function updateControllerList(){
    $.getJSON('settings/discovery', function(data) {
        $.each( data, function( index, item ) {
            updateControllerToTable(item.shortName, item.lastSeen)
        });
        setTimeout(updateControllerList,1000);
    }).fail(function() {
        setTimeout(updateControllerList,5000);
    });
}

function updateControllerToTable(name, lastSeen) {
    var table = $('#controller-list').find("tbody");
    var found = false;
    $.each(table.children(), function(index, item) {
        var currentItem = item.children[0].innerText;
        if (currentItem == name) {
            item.children[1].innerText = lastSeen;
            found = true;
        }
    });
    if (!found) {
        $('#controller-list').find('> tbody:last-child')
            .append('<tr><td>' + name + '</td><td>' + lastSeen + '</td></tr>');
    }
}