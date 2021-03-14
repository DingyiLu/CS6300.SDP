import os
from django.db import models
from django.forms import ModelForm
from django import forms
from django.contrib.auth.models import User


#########
# UTILITY FUNCTIONS
#########
# Temporary static photos will be uploaded to: /media/edu/static_problems/<problem ID>/somefile.jpg
def get_static_problem_path(instance, filename):
    return os.path.join('edu/static_problems', str(instance.id), filename)


# Temporary static photos will be uploaded to: /media/edu/static_solutions/<solution ID>/somefile.jpg
def get_static_solution_path(instance, filename):
    return os.path.join('edu/static_solutions', str(instance.id), filename)


# Temporary static photos will be uploaded to: /media/edu/static_answer/<answer ID>/somefile.jpg
def get_static_answer_path(instance, filename):
    return os.path.join('edu/static_answers', str(instance.id), filename)


#########
# MAIN ENTITIES
#########
# Create an Administrator model
class Administrator(models.Model):
    GENDERS = (
        ('M', 'Male'),
        ('F', 'Female'),
        ('N', 'Prefer not to say'),
    )
    user         = models.ForeignKey(User, on_delete=models.CASCADE)
    first_name   = models.CharField("First Name", max_length=255)
    last_name    = models.CharField("Last Name", max_length=255)
    bc_addr      = models.CharField("Blockchain Address", max_length=255, unique=True)
    email        = models.EmailField("Email Address", max_length=255)
    phone_number = models.CharField("Phone Number", max_length=30, null=True, blank=True)
    gender       = models.CharField("Gender", max_length=1, choices=GENDERS)
    bio          = models.CharField("Bio", max_length=2000, null=True, blank=True)

    date_joined = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "{} {}".format(self.first_name, self.last_name)

    def __repr__(self):
        return self.__str__()


# Create an Educator model
class Educator(models.Model):
    GENDERS = (
        ('M', 'Male'),
        ('F', 'Female'),
        ('N', 'Prefer not to say'),
    )
    user         = models.ForeignKey(User, on_delete=models.CASCADE)
    first_name   = models.CharField("First Name", max_length=255)
    last_name    = models.CharField("Last Name", max_length=255)
    bc_addr      = models.CharField("Blockchain Address", max_length=255, unique=True)
    email        = models.EmailField("Email Address", max_length=255)
    phone_number = models.CharField("Phone Number", max_length=30, null=True, blank=True)
    gender       = models.CharField("Gender", max_length=1, choices=GENDERS)
    bio          = models.CharField("Bio", max_length=2000, null=True, blank=True)

    courses_teached = models.ManyToManyField('Course', through='CoursesTeached', related_name='Courses_Teached')

    date_joined = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "{} {}".format(self.first_name, self.last_name)

    def __repr__(self):
        return self.__str__()


# Create an Student model
class Student(models.Model):
    GENDERS = (
        ('M', 'Male'),
        ('F', 'Female'),
        ('N', 'Prefer not to say'),
    )
    GRADES = (
        ('K', 'Kindergarten'),
        ('1', 'First'),
        ('2', 'Second'),
        ('3', 'Third'),
        ('4', 'Fourth'),
        ('5', 'Fifth'),
        ('6', 'Sixth'),
        ('7', 'Seventh'),
        ('8', 'Eight'),
        ('9', 'Ninth'),
        ('10', 'Tenth'),
        ('11', 'Eleventh'),
        ('12', 'Twelth'),
    )
    user         = models.ForeignKey(User, on_delete=models.CASCADE)
    first_name   = models.CharField("First Name", max_length=255)
    last_name    = models.CharField("Last Name", max_length=255)
    nickname     = models.CharField("Nickname", max_length=255, null=True, blank=True)
    bc_addr      = models.CharField("Blockchain Address", max_length=255, unique=True)
    email        = models.EmailField("Email Address", max_length=255)
    phone_number = models.CharField("Phone Number", max_length=30, null=True, blank=True)
    gender       = models.CharField("Gender", max_length=1, choices=GENDERS)
    grade        = models.CharField("Grade", max_length=2, choices=GRADES)
    bio          = models.CharField("Bio", max_length=2000, null=True, blank=True)

    courses_attended  = models.ManyToManyField('Course', through='CoursesAttended', related_name='Courses_Attended')
    tutor_problems    = models.ManyToManyField('TutorProblem', through='TutorProblems', related_name='Tutor_Problems')
    tutee_answers     = models.ManyToManyField('TuteeAnswer', through='TuteeAnswers', related_name='Tutee_Answers')
    tutoring_sessions = models.ManyToManyField('TutoringSession', through='TutoringSessions', related_name='Tutoring_Sessions')
    badges            = models.ManyToManyField('Badge', through='BadgesEarned', related_name='Badges_Earned')

    # Statistical entries
    approved_tutor_problems  = models.IntegerField("Approved Tutor Problems", default=0)
    approved_tutee_answers   = models.IntegerField("Approved Tutee Answers", default=0)
    approved_session_seconds = models.IntegerField("Approved Tutoring Session Seconds", default=0)
    badge_count              = models.IntegerField("Digital Badge Count", default=0)

    date_joined = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "{} {} (Grade={})".format(self.first_name, self.last_name, self.grade)

    def __repr__(self):
        return self.__str__()


# Create an Course model
class Course(models.Model):
    GRADES = (
        ('K', 'Kindergarten'),
        ('1', 'First'),
        ('2', 'Second'),
        ('3', 'Third'),
        ('4', 'Fourth'),
        ('5', 'Fifth'),
        ('6', 'Sixth'),
        ('7', 'Seventh'),
        ('8', 'Eight'),
        ('9', 'Ninth'),
        ('10', 'Tenth'),
        ('11', 'Eleventh'),
        ('12', 'Twelth'),
    )
    title   = models.CharField("Title", max_length=255)
    year    = models.CharField("School Year", max_length=4)
    grade   = models.CharField("Grade", max_length=2, choices=GRADES)
    teacher = models.ForeignKey('Educator', related_name='Course_Teacher', null=True, blank=True, on_delete=models.CASCADE)
    active  = models.BooleanField("Active Course?", default=False)
    done    = models.BooleanField("Course Complete?", default=False)
    info    = models.CharField("Course Information", max_length=2000, null=True, blank=True)

    students = models.ManyToManyField('Student', through='CoursesAttended')

    date_created = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "{}, {} (Grade={}, Teacher={}, Active?={})".format(self.title, self.year, self.grade, self.teacher, self.active)

    def __repr__(self):
        return self.__str__()


# Class to track individual problems and solutions generated by a tutor.
# Note that the final version of this website should be able to be "model free"
# in the sense that the blockchain should be query-able for problems generated by
# particular student blockchain addresses
class TutorProblem(models.Model):
    GRADES = (
        ('K', 'Kindergarten'),
        ('1', 'First'),
        ('2', 'Second'),
        ('3', 'Third'),
        ('4', 'Fourth'),
        ('5', 'Fifth'),
        ('6', 'Sixth'),
        ('7', 'Seventh'),
        ('8', 'Eight'),
        ('9', 'Ninth'),
        ('10', 'Tenth'),
        ('11', 'Eleventh'),
        ('12', 'Twelth'),
    )
    creator          = models.ForeignKey(Student, on_delete=models.CASCADE)
    creator_bc_addr  = models.CharField("Creator Blockchain Address", max_length=255, null=True, blank=True)

    problem_url      = models.CharField("Problem Image URL", max_length=255, null=True, blank=True)
    solution_url     = models.CharField("Solution Image URL", max_length=255, null=True, blank=True)

    approver         = models.ForeignKey(Educator, on_delete=models.CASCADE, null=True, blank=True)
    approver_bc_addr = models.CharField("Approver Blockchain Address", max_length=255, null=True, blank=True)
    assigned         = models.BooleanField("Assigned to Educator?", default=False)
    assessed         = models.BooleanField("Assessed by Educator?", default=False)
    approved         = models.BooleanField("Approved by Educator?", default=False, null=True, blank=True)
    grade            = models.CharField("Grade Level", max_length=2, choices=GRADES, null=True, blank=True)
    comments         = models.CharField("Educator Comments", max_length=2000, null=True, blank=True)

    date_created = models.DateTimeField(auto_now_add=True)

    address = models.CharField("Blockchain Address", max_length=255, null=True, blank=True)

    def __str__(self):
        return "Grade={}, ID={}, Approved?={}, BC-Addr={}".format(self.grade, self.id, self.approved, self.address)

    def __repr__(self):
        return self.__str__()


# Class to track individual answers generated by a tutee in response to a problem.
class TuteeAnswer(models.Model):
    problem           = models.ForeignKey('TutorProblem', related_name='Tutor_Problem', null=True, blank=True, on_delete=models.CASCADE)

    answerer          = models.ForeignKey(Student, on_delete=models.CASCADE)
    answerer_bc_addr  = models.CharField("Answerer Blockchain Address", max_length=255, null=True, blank=True)

    answer_url        = models.CharField("Answer Image URL", max_length=255, null=True, blank=True)

    approver          = models.ForeignKey(Educator, on_delete=models.CASCADE, null=True, blank=True)
    approver_bc_addr  = models.CharField("Approver Blockchain Address", max_length=255, null=True, blank=True)
    assigned          = models.BooleanField("Assigned to Educator?", default=False)
    assessed          = models.BooleanField("Assessed by Educator?", default=False)
    approved          = models.BooleanField("Approved by Educator?", default=False, null=True, blank=True)
    correct           = models.BooleanField("Correct According to Educator?", default=False, null=True, blank=True)
    comments          = models.CharField("Educator Comments", max_length=2000, null=True, blank=True)

    date_created      = models.DateTimeField(auto_now_add=True)

    address = models.CharField("Blockchain Address", max_length=255, null=True, blank=True)

    def __str__(self):
        return "ID={}, Problem={}, BC-Addr={}".format(self.id, self.problem, self.address)

    def __repr__(self):
        return self.__str__()


# Represent a tutoring session chat room
class TutoringSessionRoom(models.Model):
    room_name = models.CharField("Room Name", max_length=255)
    grade     = models.CharField("Grade", max_length=2)
    tutors    = models.IntegerField("Active Tutors", default=0)
    tutees    = models.IntegerField("Active Tutees", default=0)
    active    = models.IntegerField("Total Active Students", default=0)


# Represent tutoring sessions between a tutor and a tutee
class TutoringSession(models.Model):
    GRADES = (
        ('K', 'Kindergarten'),
        ('1', 'First'),
        ('2', 'Second'),
        ('3', 'Third'),
        ('4', 'Fourth'),
        ('5', 'Fifth'),
        ('6', 'Sixth'),
        ('7', 'Seventh'),
        ('8', 'Eight'),
        ('9', 'Ninth'),
        ('10', 'Tenth'),
        ('11', 'Eleventh'),
        ('12', 'Twelth'),
    )
    tutor             = models.ForeignKey(Student, on_delete=models.CASCADE, related_name="TutoringSession_Tutor")
    tutor_grade       = models.CharField("Tutor Grade at Time", max_length=2, choices=GRADES, null=True, blank=True)
    tutee             = models.ForeignKey(Student, on_delete=models.CASCADE, related_name="TutoringSession_Tutee")
    tutee_grade       = models.CharField("Tutee Grade at Time", max_length=2, choices=GRADES, null=True, blank=True)
    start             = models.DateTimeField(null=True, blank=True)
    end               = models.DateTimeField(null=True, blank=True)
    duration_seconds  = models.IntegerField(default=0)

    problem_id_list   = models.CharField("Problem ID Listing", max_length=2000, null=True, blank=True)
    answer_id_list    = models.CharField("Answer ID Listing", max_length=2000, null=True, blank=True)
    chat_log_url      = models.CharField("Chat Log URL", max_length=255, null=True, blank=True)

    approver          = models.ForeignKey(Educator, on_delete=models.CASCADE, null=True, blank=True)
    assigned          = models.BooleanField("Assigned to Educator?", default=False)
    assessed          = models.BooleanField("Assessed by Educator?", default=False)
    approved          = models.BooleanField("Approved by Educator?", default=False, null=True, blank=True)
    comments          = models.CharField("Educator Comments", max_length=2000, null=True, blank=True)

    address = models.CharField("Blockchain Address", max_length=255, null=True, blank=True)

    def __str__(self):
        return "Tutor={}, Tutee={}, Start={} (Approved?={}, Approver={}), BC-Addr={}".format(self.tutor, self.tutee, self.start, self.approved, self.approver, self.address)

    def __repr__(self):
        return self.__str__()


# Create an Student digital badge
class Badge(models.Model):
    title = models.CharField("Title", max_length=255, null=True, blank=True)
    info  = models.CharField("Info", max_length=2000, null=True, blank=True)
    student = models.ForeignKey(Student, on_delete=models.CASCADE)
    teacher = models.ForeignKey(Educator, on_delete=models.CASCADE)

    date_created = models.DateTimeField(auto_now_add=True)

    address = models.CharField("Blockchain Address", max_length=255, null=True, blank=True)

    def __str__(self):
        return "Title={}, Student={}, Assigned By Teacher={}, BC-Addr={}".format(self.title, self.student, self.teacher, self.address)

    def __repr__(self):
        return self.__str__()


#########
# RELATIONSHIP TABLES
#########


# Represent courses teached (by teacher)
class CoursesTeached(models.Model):
    teacher      = models.ForeignKey(Educator, on_delete=models.CASCADE)
    course       = models.ForeignKey(Course, on_delete=models.CASCADE)
    date_started = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "Teacher={}, Course={}, Started={}".format(self.teacher, self.course, self.date_started)


# Represent courses attended (by student)
class CoursesAttended(models.Model):
    student      = models.ForeignKey(Student, on_delete=models.CASCADE)
    course       = models.ForeignKey(Course, on_delete=models.CASCADE)
    date_started = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "Student={}, Course={}, Started={}".format(self.student, self.course, self.date_started)


# Represent tutor and their problems
class TutorProblems(models.Model):
    tutor      = models.ForeignKey(Student, on_delete=models.CASCADE)
    problem    = models.ForeignKey(TutorProblem, on_delete=models.CASCADE)
    date_added = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "Tutor={}, Problem={}".format(self.tutor, self.problem)


# Represent tutee and their answers
class TuteeAnswers(models.Model):
    tutee      = models.ForeignKey(Student, on_delete=models.CASCADE)
    answer     = models.ForeignKey(TuteeAnswer, on_delete=models.CASCADE)
    date_added = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "Tutee={}, Answer={}".format(self.tutee, self.answer)


# Represent tutoring sessions a student was a part of (as either a tutor or tutee)
class TutoringSessions(models.Model):
    student    = models.ForeignKey(Student, on_delete=models.CASCADE)
    session    = models.ForeignKey(TutoringSession, on_delete=models.CASCADE)
    date_added = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "Student={}, Session={}".format(self.student, self.session)


# Represent badges earned (by student)
class BadgesEarned(models.Model):
    badge         = models.ForeignKey(Badge, on_delete=models.CASCADE)
    student       = models.ForeignKey(Student, on_delete=models.CASCADE)
    date_assigned = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return "Teacher={}, Course={}, Started={}".format(self.teacher, self.course, self.date_started)


#########
# FORMS
#########


# Class to represent a form to fill out for registering a new Administrator
class AdministratorForm(ModelForm):
    class Meta:
        model   = Administrator
        fields  = ['user', 'first_name', 'last_name', 'bc_addr', 'email', 'phone_number', 'gender', 'bio']

    # Initializer for editing existing
    def __init__(self, existing, *args, **kwargs):
        super(AdministratorForm, self).__init__(*args, **kwargs)
        if existing is not None:
            self.fields.pop('user')  # This can't be changed
            self.fields['first_name'].initial = existing.first_name
            self.fields['last_name'].initial = existing.last_name
            self.fields['bc_addr'].initial = existing.bc_addr
            self.fields['email'].initial = existing.email
            self.fields['phone_number'].initial = existing.phone_number
            self.fields['gender'].initial = existing.gender
            self.fields['bio'].initial = existing.bio


# Class to represent a form to fill out for registering/editing an Educator
class EducatorForm(ModelForm):
    class Meta:
        model   = Educator
        fields  = ['user', 'first_name', 'last_name', 'bc_addr', 'email', 'phone_number', 'gender', 'bio']

    # Initializer for editing existing
    def __init__(self, existing, *args, **kwargs):
        super(EducatorForm, self).__init__(*args, **kwargs)
        if existing is not None:
            self.fields.pop('user')  # This can't be changed
            self.fields['first_name'].initial = existing.first_name
            self.fields['last_name'].initial = existing.last_name
            self.fields['bc_addr'].initial = existing.bc_addr
            self.fields['email'].initial = existing.email
            self.fields['phone_number'].initial = existing.phone_number
            self.fields['gender'].initial = existing.gender
            self.fields['bio'].initial = existing.bio


# Class to represent a form to fill out for registering/editing a Student
class StudentForm(ModelForm):
    class Meta:
        model   = Student
        fields  = ['user', 'first_name', 'last_name', 'bc_addr', 'email', 'phone_number', 'gender', 'grade', 'bio']

    # Initializer for editing existing
    def __init__(self, existing, *args, **kwargs):
        super(StudentForm, self).__init__(*args, **kwargs)
        if existing is not None:
            self.fields.pop('user')  # This can't be changed
            self.fields['first_name'].initial = existing.first_name
            self.fields['last_name'].initial = existing.last_name
            self.fields['bc_addr'].initial = existing.bc_addr
            self.fields['email'].initial = existing.email
            self.fields['phone_number'].initial = existing.phone_number
            self.fields['gender'].initial = existing.gender
            self.fields['grade'].initial = existing.grade
            self.fields['bio'].initial = existing.bio


# Class to represent a form to fill out for registering a new Course
class CourseForm(ModelForm):
    class Meta:
        model   = Course
        fields  = ['title', 'year', 'grade', 'teacher', 'active', 'done', 'info']

    # Initializer for editing existing
    def __init__(self, existing, *args, **kwargs):
        super(CourseForm, self).__init__(*args, **kwargs)
        if existing is not None:
            self.fields['title'].initial = existing.title
            self.fields['year'].initial = existing.year
            self.fields['grade'].initial = existing.grade
            self.fields['teacher'].initial = existing.teacher
            self.fields['active'].initial = existing.active
            self.fields['done'].initial = existing.done
            self.fields['info'].initial = existing.info


# Class to represent a form to fill out for registering a new Tutor Problem
class TutorProblemForm(ModelForm):
    class Meta:
        model   = TutorProblem
        # Only use needed fields
        fields = ['approved', 'grade', 'comments']

    # Initialize and lock down based on who's interacting
    def __init__(self, problem, who, *args, **kwargs):
        super(TutorProblemForm, self).__init__(*args, **kwargs)
        if problem is None and who == "student":
            # This is a create by a student.
            # Approval fields are not needed
            self.fields['approved'].required = False
            self.fields['grade'].required = False
            self.fields['comments'].required = False
            self.fields.pop('approved')
            self.fields.pop('grade')
            self.fields.pop('comments')
            # NOTE: A "canvasData" element will also exist which will hold the "data:image/png;base64"
            # encoded text form of a canvas image the user drew
        else:
            # This is an edit by an admin or teacher.
            # Change behavior based on whether this is an admin or a teacher.
            if who == "admin":
                # Admins can only assign.
                self.fields['approved'].required = False
                self.fields['grade'].required = False
                self.fields['comments'].required = False
                self.fields.pop('approved')
                self.fields.pop('grade')
                self.fields.pop('comments')
                # Add a field to select a teacher to assign to. The view logic will take
                # care of modifying the other parts of the entity
                self.fields['approver_choices'] = forms.ChoiceField(widget=forms.Select(attrs={'class': 'regDropDown'}), choices=[(t.id, str(t)) for t in Educator.objects.all()])
                self.fields['approver_choices'].required = True
            elif who == "teacher":
                # Teachers must approve or deny
                self.fields['approved'].required = True
                self.fields['grade'].required = True
                self.fields['comments'].required = True


# Class to represent a form to fill out for registering a new Tutee Answer
class TuteeAnswerForm(ModelForm):
    class Meta:
        model   = TuteeAnswer
        # Only use needed fields
        fields  = ['approved', 'correct', 'comments']

    # Initialize and lock down based on who's interacting
    def __init__(self, answer, who, *args, **kwargs):
        super(TuteeAnswerForm, self).__init__(*args, **kwargs)
        if answer is None and who == "student":
            # This is a create by a student.
            # Approval fields are not needed
            self.fields['approved'].required = False
            self.fields['correct'].required = False
            self.fields['comments'].required = False
            self.fields.pop('approved')
            self.fields.pop('correct')
            self.fields.pop('comments')
            # NOTE: A "canvasData" element will also exist which will hold the "data:image/png;base64"
            # encoded text form of a canvas image the user drew
        else:
            # This is an edit by an admin or teacher.
            # Change behavior based on whether this is an admin or a teacher.
            if who == "admin":
                # Admins can only assign.
                self.fields['approved'].required = False
                self.fields['correct'].required = False
                self.fields['comments'].required = False
                self.fields.pop('approved')
                self.fields.pop('correct')
                self.fields.pop('comments')
                # Add a field to select a teacher to assign to. The view logic will take
                # care of modifying the other parts of the entity
                self.fields['approver_choices'] = forms.ChoiceField(widget=forms.Select(attrs={'class': 'regDropDown'}), choices=[(t.id, str(t)) for t in Educator.objects.all()])
                self.fields['approver_choices'].required = True
            elif who == "teacher":
                # Teachers must approve or deny
                self.fields['approved'].required = True
                self.fields['correct'].required = True
                self.fields['comments'].required = True


# Class to represent a Tutoring Session Matchmaking form for the sessionwaiting.html view
class TutoringSessionMatchForm(forms.Form):
    GRADES = (
        ('K', 'Kindergarten'),
        ('1', 'First'),
        ('2', 'Second'),
        ('3', 'Third'),
        ('4', 'Fourth'),
        ('5', 'Fifth'),
        ('6', 'Sixth'),
        ('7', 'Seventh'),
        ('8', 'Eight'),
        ('9', 'Ninth'),
        ('10', 'Tenth'),
        ('11', 'Eleventh'),
        ('12', 'Twelth'),
    )
    role      = forms.ChoiceField(widget=forms.Select(attrs={'class': 'regDropDown'}), choices=[("Tutor", "Tutor"), ("Tutee", "Tutee")])
    grade = forms.ChoiceField(widget=forms.Select(attrs={'class': 'regDropDown'}), choices=GRADES)
    # Optional. Otherwise, will use student's current grade as tutee
    grade.required = False


# Class to represent a form to populate for a tutoring session
class TutoringSessionForm(ModelForm):
    class Meta:
        model   = TutoringSession
        # Only use needed fields
        fields  = ['approved', 'comments']

    # Initialize and lock down based on who's interacting
    def __init__(self, session, who, *args, **kwargs):
        super(TutoringSessionForm, self).__init__(*args, **kwargs)
        # Change behavior based on whether this is an admin or a teacher.
        if who == "admin":
            # Admins can only assign.
            self.fields['approved'].required = False
            self.fields['comments'].required = False
            self.fields.pop('approved')
            self.fields.pop('comments')
            # Add a field to select a teacher to assign to. The view logic will take
            # care of modifying the other parts of the entity
            self.fields['approver_choices'] = forms.ChoiceField(widget=forms.Select(attrs={'class': 'regDropDown'}), choices=[(t.id, str(t)) for t in Educator.objects.all()])
            self.fields['approver_choices'].required = True
        elif who == "teacher":
            # Teachers must approve or deny
            self.fields['approved'].required = True
            self.fields['comments'].required = True


# Class to represent a form to fill out for registering a new Course
class BadgeForm(ModelForm):
    class Meta:
        model   = Badge
        fields  = ['title', 'info']

    # Add a student selector if no student is specified
    #def __init__(self, student, *args, **kwargs):
    #    super(BadgeForm, self).__init__(*args, **kwargs)
    #    # If the student is None, provide a selector to pick a student
    #    if student is None:
    #        self.fields['student_choices'] = forms.ChoiceField(widget=forms.Select(attrs={'class': 'regDropDown'}), choices=[(t.id, str(t)) for t in #Student.objects.all()])
    #        self.fields['student_choices'].required = False


# Class to represent form for an Administrator to add a student to a course
# class AddStudentToCourseForm(forms.Form):
#     fields = []
#     fields['student_choices']          = forms.MultipleChoiceField(widget=forms.CheckboxSelectMultiple, choices=((st.id, str(st)) for st in Student.objects.all()))
#     fields['student_choices'].required = False
#     fields['course_choices']           = forms.MultipleChoiceField(widget=forms.CheckboxSelectMultiple, choices=((cs.id, str(cs)) for cs in Course.objects.filter(active=True)))
#     fields['course_choices'].required  = False
