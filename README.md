# FocusAPI

Java API for accessing course/grade information via Focus for Schools. Currently in very early development. Contact me at [stephan.lensky@gmail.com](mailto:stephan.lensky@gmail.com) if you have any questions.

Note that currently the password masking used when logging in with test mode is incompatible with Eclipse, so if you want to use it, either export as a `.jar` and run from the commandline, or comment out the password input line in `src/test/java/com/slensky/FocusAPI/test.java` and hardcode in your password.

## Currently Working

- Core login functionality
- Changing the user's marking period
- Pulling courses, overall grades, and upcoming events from the portal page
- Retrieving full course info from the user's schedule

## Planned Features

- Retrieving assignments for classes
- Retrieving final exam grades and overall final grades
- Retrieving events from Focus's calendar
- Retrieving information about the student
