<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Create a TweeRaffle</title>
	<link rel="stylesheet" href="tweeraffle.css" />
</head>
<body>
	<form method="post" action="/create">
		<p>
			Choose a hashtag for your raffle #<input type="text" name="hashtag"/>
		</p>
		<p>
			What's the oldest tweet allowed?
			<select name="age">
				<option value="0">Only allow new tweets</option>
				<option value="15">15 minutes ago</option>
				<option value="60">1 hour ago</option>
				<option value="120">2 hours ago</option>
				<option value="300">5 hours ago</option>
				<option value="3600">1 day ago</option>
				<option value="25200">1 week ago</option>
			</select>
		</p>
		<p>
			<input type="submit" value="Start the rafflin'!"/>
		</p>
	</form>
</body>
</html>