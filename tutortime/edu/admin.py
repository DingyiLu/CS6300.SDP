from django.contrib import admin

# Register your models here.
from .models import Administrator, Educator, Student, Course, TutorProblem, TuteeAnswer, TutoringSessionRoom, TutoringSession, Badge, CoursesTeached, CoursesAttended, TutorProblems, TuteeAnswers, TutoringSessions, BadgesEarned

admin.site.register(Administrator)
admin.site.register(Educator)
admin.site.register(Student)
admin.site.register(Course)
admin.site.register(TutorProblem)
admin.site.register(TuteeAnswer)
admin.site.register(TutoringSessionRoom)
admin.site.register(TutoringSession)
admin.site.register(Badge)
admin.site.register(CoursesTeached)
admin.site.register(CoursesAttended)
admin.site.register(TutorProblems)
admin.site.register(TuteeAnswers)
admin.site.register(TutoringSessions)
admin.site.register(BadgesEarned)
