Notice: development on this project has stopped, and it's entirely likely that it will stop working at some point. The codebase is messy and I intend to start over entirely.

# FocusAPI

Java API for accessing course/grade information via Focus for Schools. Currently in very early development. Contact me at [stephan.lensky@gmail.com](mailto:stephan.lensky@gmail.com) if you have any questions.

Using this API is dead simple. Here's an example:
```
// initializes the focus object and logs in
Focus focus = new Focus(user, password, Focus.School.ASD);

// stores the current marking period (a marking period is a combination of a semester and a year)
MarkingPeriod mp = focus.getStudentInfo.getCurrentMarkingPeriod();

//prints out the user's courses for the current marking period
System.out.println(focus.getStudentInfo().getCoursesFromSchedule(mp));

```

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
