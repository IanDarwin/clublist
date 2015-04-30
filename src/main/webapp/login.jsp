<jsp:directive.page language="java" />
<jsp:directive.page contentType="text/html" />
<!DOCTYPE html>
<html>
<head>
	<title>Club List Login Page</title>
</head>
<body>

    <form action="j_security_check" method="POST">

		<p>Please login here</p>

		<div class="dialog">
				User name:<br/>
				<input type="text" name="j_username"/>
				<br/>
				Password:<br/>
				<input type="password" name="j_password"/>
		</div>

        <div class="actionButtons">
            <input type="submit" id="submit" value="Login"/>
        </div>

    </form>

</body>
</html>
