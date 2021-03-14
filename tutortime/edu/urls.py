from django.conf.urls import url

from . import views

urlpatterns = [
    # School home page
    url(r'^$', views.index, name='index'),

    # "All" entries of a given type
    url(r'^allcourses', views.allcourses, name='allcourses'),
    url(r'^alladmins', views.alladmins, name='alladmins'),
    url(r'^allteachers', views.allteachers, name='allteachers'),
    url(r'^allstudents', views.allstudents, name='allstudents'),

    # Individual courses, administrators, teachers, and students.
    # Create new, edit, and view specific ones with variable
    url(r'^course/(.+)', views.course, name='course'),
    url(r'^administrator/(.+)', views.administrator, name='administrator'),
    url(r'^teacher/(.+)', views.teacher, name='teacher'),
    url(r'^student/(.+)', views.student, name='student'),

    # Admin add student to a course, must be an admin to access this page
    # TODO url(r'^addstudentcourse', views.addstudentcourse, name='addstudentcourse'),

    # Problem and answer pages
    # View all problems known about by this school in a list, with creator listed
    url(r'^allproblems', views.allproblems, name='allproblems'),
    # Create or view a specific problem by ID.
    # Only admins/teachers, the creator, or a student who has submitted an answer
    # in connection to a problem can view a problem.
    url(r'^problem/(.+)', views.problem, name='problem'),
    # View all answers known about by this school in a list, with answerer listed
    url(r'^allanswers', views.allanswers, name='allanswers'),
    # Create or view a specific answer by ID.
    # Only admins/teachers or the answerer may view an answer
    url(r'^answer/(.+)', views.answer, name='answer'),

    # View all badges by all students at this school
    url(r'^allbadges', views.allbadges, name='allbadges'),
    # Create or view a specific digital badge by ID
    url(r'^badge/(.+)', views.badge, name='badge'),

    # Tutoring leader board
    # Includes leaders in tutor/tutee hours, sessions, badges, etc.
    url(r'^leaders', views.leaders, name='leaders'),

    # Tutoring session waiting room (that forwards along to a session)
    url(r'^sessionwaiting', views.sessionwaiting, name='sessionwaiting'),

    # Review past tutoring session (as instructor)
    url(r'^sessionreview/(.+)', views.sessionreview, name='sessionreview'),

    # Teacher view for all sessions
    url(r'^allsessions', views.allsessions, name='allsessions'),

    # AJAX endpoints for processing POST data
    url(r'^ajax/createanswer', views.ajaxcreateanswer, name='ajaxcreateanswer'),
    url(r'^ajax/createsession', views.ajaxcreatesession, name='ajaxcreatesession'),
]
