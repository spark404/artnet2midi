<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="icon" href="favicon.ico">

    <title>ArtNet2Midi</title>

    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/bootstrap-theme.css" rel="stylesheet">

    <link href="css/starter-template.css" rel="stylesheet">
</head>

<body>

<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">ArtNet2Midi</a>
        </div>
        <div id="navbar" class="collapse navbar-collapse">
            <ul class="nav navbar-nav">
                <li class="active"><a href="#">Settings</a></li>
                <li><a href="universe/0">Universe 0</a></li>
                <li><a href="universe/1">Universe 1</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<div class="container">

    <div class="starter-template">
        <h1>ArtNet2Midi</h1>
        <p class="lead">Main settings page</p>
    </div>

    <div class="row">
        <div class="col-md-4">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">ArtNet Client</h3>
                </div>
                <div class="panel-body">
                    <p>Select network interface</p>
                    <p>
                    <div class="dropdown" style="float: right;">
                        <a href="#" data-toggle="dropdown" class="dropdown-toggle">Dropdown <b class="caret"></b></a>
                        <ul class="dropdown-menu">
                            <li><a href="#">Action</a></li>
                            <li><a href="#">Another action</a></li>
                        </ul>
                    </div>
                    </p>
                    <p style="clear: both;">Configure network Id</p>
                    <p>
                        <input type="text" class="form-control" placeholder="Sub" style="width: 50px; float: right;">
                        <span style="float:right; padding: 5px;">:</span>
                        <input type="text" class="form-control" placeholder="Net" style="width: 50px; float: right;">
                    </p>
                    <p style="clear: both;">Start / Stop the ArtNet Client</p>
                    <p align="right">
                        <button id="server-on" type="button" class="btn btn-success">Start</button>
                        <button id="server-off" type="button" class="btn btn-danger">Stop</button>
                    </p>
                </div>
            </div>
        </div>
        <div class="col-md-8">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3 class="panel-title">Controller(s)</h3>
                </div>
                <div class="panel-body">
                    <table class="table table-striped">
                        <thead>
                          <tr><th>Id</th><th>Last seen</th></tr>
                        </thead>
                        <tbody>
                            <tr><td>ArtNet2Midi</td><td>18:15</td></tr>
                            <tr><td>DMX Console</td><td>18:15</td></tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div><!-- /.container -->


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="js/bootstrap.min.js"></script>
<script>
    $('#server-off').prop('disabled', true);
</script>
</body>
</html>

