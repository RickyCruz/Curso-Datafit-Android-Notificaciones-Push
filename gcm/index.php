<!DOCTYPE html>
<html>
<head>
    <title>Notificaciones Push</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
</head>
<body style="min-height: 2000px; padding-top: 70px;">
<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                    aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Menú</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Notificaciones Push</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
        </div><!--/.nav-collapse -->
    </div>
</nav>

<?php
include_once 'model/User.php';
$users    = User::getAllUsers();
$numUsers = ($users != false) ? count($users) : 0;
?>
<div class="container">
    <?php if ($numUsers > 0): ?>
        <?php foreach ($users as $user): ?>
            <div class="col-md-3">
                <div class="panel panel-primary">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            <?= $user["name"] . " [" . $user["email"] . "]"; ?>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <form id="<?= $user["id"] ?>" name="" method="post"
                              onsubmit="return sendPushNotification('<?= $user["id"] ?>')">
                            <div class="form-group">
                                <label for="msg">Mensaje</label>
                                <textarea class="form-control" rows="3" id="msg" name="message" cols="25"></textarea>
                                <input type="hidden" name="regId" value="<?= $user["gcm_regid"] ?>"/>
                            </div>
                            <center>
                                <input type="submit" class="btn btn-primary" value="Enviar Notificación" onclick=""/>
                            </center>
                        </form>
                    </div>
                </div>
            </div>
        <?php endforeach; ?>
    <?php else: ?>
        <div class="alert alert-danger alert-dismissible fade in" role="alert">
            <h4>No hay usuarios registrados :'(</h4>

            <p>Forever alone</p>
        </div>
    <?php endif; ?>
</div>


<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
<script type="text/javascript">
    function sendPushNotification(id) {
        var formId = $('form#' + id);
        var data   = formId.serialize();
        formId.unbind('submit');
        $.ajax({
            url: "send_message.php",
            type: 'GET',
            data: data,
            success: function (data, textStatus, xhr) {
                $('#msg').val('');
            }
        });
        return false;
    }
</script>

</body>
</html>
