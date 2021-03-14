import datetime
import json
import time
from django.http import HttpResponseRedirect, HttpResponse, JsonResponse
from django.shortcuts import get_object_or_404
from django.shortcuts import render
from django.utils.safestring import mark_safe
from django.contrib.auth.decorators import login_required
from django.views.decorators.csrf import csrf_exempt

from django.contrib.auth.models import User
from .models import Administrator, Educator, Student, Course, Badge, BadgesEarned, CoursesTeached, CoursesAttended, TutoringSession, TutorProblem, TuteeAnswer, AdministratorForm, EducatorForm, StudentForm, CourseForm, TutorProblemForm, TuteeAnswerForm, TutoringSessionForm, BadgeForm, TutorProblems, TuteeAnswers, TutoringSessions, TutoringSessionRoom, TutoringSessionMatchForm
from .utils import saveImageToObject, loadImageBufferFromObject, deployTutorProblem, deployTuteeAnswer, deployTutoringSession, deployBadge, assignTutorProblem, assignTuteeAnswer, assignTutoringSession, assessTutorProblem, assessTuteeAnswer, assessTutoringSession, saveTextToObject, loadTextFromObject


# Display our top-level school page with links to classes, educators (teachers), students, and other items
def index(request):

    # Fetch a list of all "active" classes
    current_courses = Course.objects.filter(active=True).all()

    # Provide a context and display the top-level page
    content = {'current_courses': current_courses}
    return render(request, 'edu/toplevel.html', content)


# Single admin-related actions
@login_required
def administrator(request, admin_arg):

    # Parse out course argument information
    a_args = admin_arg.split("/")
    admin_id   = a_args[0]
    comm = ""    # Team "command". Could be something like: "edit"
    if len(a_args) > 1:
        comm = a_args[1]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_admin = None
    error_message = ""
    form = None
    editing = False

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Figure out if this is an "edit" or a "create"
        cur_entity = None
        if admin_id != "create" and Administrator.objects.filter(id=admin_id).exists():
            cur_admin = Administrator.objects.get(id=admin_id)
            cur_entity = cur_admin
        else:
            cur_admin = Administrator()

        # Process the form submission
        form = AdministratorForm(cur_entity, request.POST)
        if form.is_valid():

            # Update values in the database
            if cur_entity is None:
                cur_admin.user = form.cleaned_data.get('user')
            cur_admin.first_name = form.cleaned_data.get('first_name')
            cur_admin.last_name = form.cleaned_data.get('last_name')
            cur_admin.bc_addr = form.cleaned_data.get('bc_addr')
            cur_admin.email = form.cleaned_data.get('email')
            cur_admin.phone_number = form.cleaned_data.get('phone_number')
            cur_admin.gender = form.cleaned_data.get('gender')
            cur_admin.bio = form.cleaned_data.get('bio')
            cur_admin.save()

            # At this point the DB should be updated. Redirect to the admin page
            return HttpResponseRedirect("/edu/administrator/{}".format(cur_admin.id))

        # Form wasn't valid, redirect to all admins page
        return HttpResponseRedirect("/edu/alladmins")

    else:
        # Get request or any other method.
        # See if we are viewing an existing admin, editing an existing admin,
        # or creating a new admin
        if admin_id == "create":
            # Only administrators can create other administrators
            if admin is None:
                error_message = "*You must be an administrator to register another administrator*"
            else:
                # Creating new, prepare form
                editing = True
                form = AdministratorForm(None)

        else:
            # Viewing or editing an existing admin.
            # Fetch the desired admin, redirecting to a 404 if it doesn't exist
            cur_admin = get_object_or_404(Administrator, id=admin_id)

            # See if we are viewing or editing
            if comm == "edit":
                # Editing an existing admin
                if admin is None:
                    # Only administrators can edit an administrator
                    error_message = "*You must be an administrator to edit an administrator*"
                else:
                    # We're editing a course, prepare a form
                    editing = True
                    form = AdministratorForm(cur_admin)

        # Create a context and render
        content = {'admin_id': admin_id, 'cur_admin': cur_admin, 'error_message': error_message, 'form': form, 'editing': editing, 'admin': admin, 'teacher': teacher, 'student': student}
        return render(request, 'edu/administrator.html', content)


# Single teacher-related actions
@login_required
def teacher(request, teacher_arg):

    # Parse out course argument information
    t_args = teacher_arg.split("/")
    teacher_id   = t_args[0]
    comm = ""    # Team "command". Could be something like: "edit"
    if len(t_args) > 1:
        comm = t_args[1]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_teacher = None
    error_message = ""
    form = None
    editing = False
    courses_teached = None
    tutor_problems = None
    tutor_answers = None
    tutor_sessions = None
    is_this_teacher = False

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Figure out if this is an "edit" or a "create"
        cur_entity = None
        if teacher_id != "create" and Educator.objects.filter(id=teacher_id).exists():
            cur_teacher = Educator.objects.get(id=teacher_id)
            cur_entity = cur_teacher
        else:
            cur_teacher = Educator()

        # Process the form submission
        form = EducatorForm(cur_entity, request.POST)
        if form.is_valid():

            # Update values in the database
            if cur_entity is None:
                cur_teacher.user = form.cleaned_data.get('user')
            cur_teacher.first_name = form.cleaned_data.get('first_name')
            cur_teacher.last_name = form.cleaned_data.get('last_name')
            cur_teacher.bc_addr = form.cleaned_data.get('bc_addr')
            cur_teacher.email = form.cleaned_data.get('email')
            cur_teacher.phone_number = form.cleaned_data.get('phone_number')
            cur_teacher.gender = form.cleaned_data.get('gender')
            cur_teacher.bio = form.cleaned_data.get('bio')
            cur_teacher.save()

            # At this point the DB should be updated. Redirect to the admin page
            return HttpResponseRedirect("/edu/teacher/{}".format(cur_teacher.id))

        # Form wasn't valid, redirect to all teachers page
        return HttpResponseRedirect("/edu/allteachers")

    else:
        # Get request or any other method.
        # See if we are viewing an existing teacher, editing an existing teacher,
        # or creating a new teacher
        if teacher_id == "create":
            # Only administrators can create teachers
            if admin is None:
                error_message = "*You must be an administrator to register a teacher*"
            else:
                # Creating new, prepare form
                editing = True
                form = EducatorForm(None)

        else:
            # Viewing or editing an existing teacher.
            # Fetch the desired teacher, redirecting to a 404 if it doesn't exist
            cur_teacher = get_object_or_404(Educator, id=teacher_id)

            if teacher and teacher.id == teacher_id:
                is_this_teacher = True

            # Fetch a list of courses this teacher teaches
            courses_teached = cur_teacher.courses_teached.all()

            # Fetch all tutoring problems, answers, and sessions assigned to this teacher
            tutor_problems = TutorProblem.objects.filter(approver_bc_addr=cur_teacher.bc_addr, assessed=False).all()
            tutor_answers = TuteeAnswer.objects.filter(approver_bc_addr=cur_teacher.bc_addr, assessed=False).all()
            tutor_sessions = TutoringSession.objects.filter(approver=cur_teacher, assessed=False).all()

            # See if we are viewing or editing
            if comm == "edit":
                # Editing an existing teacher
                if admin is None and (teacher is None or teacher.id != teacher_id):
                    # Only administrators and the teacher can edit themselves
                    error_message = "*You must be an administrator or this teacher to edit this teacher*"
                else:
                    # We're editing a teacher, prepare a form
                    editing = True
                    form = EducatorForm(cur_teacher)

        # Create a context and render the season page
        content = {'teacher_id': teacher_id, 'cur_teacher': cur_teacher, 'error_message': error_message, 'form': form, 'editing': editing, 'admin': admin, 'teacher': teacher, 'student': student, 'courses_teached': courses_teached, 'tutor_problems': tutor_problems, 'tutor_answers': tutor_answers, 'tutor_sessions': tutor_sessions, 'is_this_teacher': is_this_teacher}
        return render(request, 'edu/teacher.html', content)


# Single student-related actions
@login_required
def student(request, student_arg):

    # Parse out course argument information
    s_args = student_arg.split("/")
    student_id   = s_args[0]
    comm = ""    # Team "command". Could be something like: "edit"
    if len(s_args) > 1:
        comm = s_args[1]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_student = None
    error_message = ""
    form = None
    editing = False
    courses_attended = None
    tutor_problems = None
    tutor_answers = None
    tutor_sessions_tutor = None
    tutor_sessions_tutee = None
    badges = None
    is_this_student = False

    # Some stats
    total_approved_problems = 0
    total_correct_answers = 0
    total_tutor_hours = 0.0
    total_tutee_hours = 0.0

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Figure out if this is an "edit" or a "create"
        cur_entity = None
        if student_id != "create" and Student.objects.filter(id=student_id).exists():
            cur_student = Student.objects.get(id=student_id)
            cur_entity = cur_student
        else:
            cur_student = Student()

        # Process the form submission
        form = StudentForm(cur_entity, request.POST)
        if form.is_valid():

            # Update values in the database
            if cur_entity is None:
                cur_student.user = form.cleaned_data.get('user')
            cur_student.first_name = form.cleaned_data.get('first_name')
            cur_student.last_name = form.cleaned_data.get('last_name')
            cur_student.bc_addr = form.cleaned_data.get('bc_addr')
            cur_student.email = form.cleaned_data.get('email')
            cur_student.phone_number = form.cleaned_data.get('phone_number')
            cur_student.gender = form.cleaned_data.get('gender')
            cur_student.grade = form.cleaned_data.get('grade')
            cur_student.bio = form.cleaned_data.get('bio')
            cur_student.save()

            # At this point the DB should be updated. Redirect to the admin page
            return HttpResponseRedirect("/edu/student/{}".format(cur_student.id))

        # Form wasn't valid, redirect to all students page
        return HttpResponseRedirect("/edu/allstudents")

    else:
        # Get request or any other method.
        # See if we are viewing an existing student, editing an existing student,
        # or creating a new student
        if student_id == "create":
            # Only administrators can create students
            if admin is None:
                error_message = "*You must be an administrator to register a student*"
            else:
                # Creating new, prepare form
                editing = True
                form = StudentForm(None)

        else:
            # Viewing or editing an existing student.
            # Fetch the desired student, redirecting to a 404 if it doesn't exist
            cur_student = get_object_or_404(Student, id=student_id)

            if student and student.id == student_id:
                is_this_student = True

            # Fetch a list of courses this student attends
            courses_attended = cur_student.courses_attended.all()

            # Fetch all tutoring problems, answers, and sessions both as a tutor and as
            # a tutee that this student took part in
            tutor_problems = cur_student.tutor_problems.filter(approved=True).all()
            tutor_answers = cur_student.tutee_answers.all()
            tutor_sessions_tutor = cur_student.tutoring_sessions.filter(tutor=cur_student).all()
            tutor_sessions_tutee = cur_student.tutoring_sessions.filter(tutee=cur_student).all()

            # Calculate some stats
            total_approved_problems = len(cur_student.tutor_problems.filter(approved=True).all())
            total_correct_answers = len(cur_student.tutee_answers.filter(correct=True).all())
            for sess in tutor_sessions_tutor:
                total_tutor_hours += ((sess.duration_seconds * 1.0)/60/60)
            for sess in tutor_sessions_tutee:
                total_tutee_hours += ((sess.duration_seconds * 1.0)/60/60)

            total_tutor_hours = "{:.2f}".format(total_tutor_hours)
            total_tutee_hours = "{:.2f}".format(total_tutee_hours)

            # Get a list of all badges this student has earned
            badges = cur_student.badges.all()

            # See if we are viewing or editing
            if comm == "edit":
                # Editing an existing teacher
                if admin is None and (student is None or student.id != student_id):
                    # Only administrators and the student can edit themselves
                    error_message = "*You must be an administrator or this student to edit this student*"
                else:
                    # We're editing a student, prepare a form
                    editing = True
                    form = StudentForm(cur_student)

        # Create a context and render the season page
        content = {'student_id': student_id, 'cur_student': cur_student, 'error_message': error_message, 'form': form, 'editing': editing, 'admin': admin, 'teacher': teacher, 'student': student, 'courses_attended': courses_attended, 'tutor_problems': tutor_problems, 'tutor_answers': tutor_answers, 'tutor_sessions_tutor': tutor_sessions_tutor, 'tutor_sessions_tutee': tutor_sessions_tutee, 'badges': badges, 'is_this_student': is_this_student, 'total_approved_problems': total_approved_problems, 'total_correct_answers': total_correct_answers, 'total_tutor_hours': total_tutor_hours, 'total_tutee_hours': total_tutee_hours}
        return render(request, 'edu/student.html', content)


# Single course-related actions
@login_required
def course(request, course_arg):

    # Parse out course argument information
    c_args = course_arg.split("/")
    course_id   = c_args[0]
    comm = ""    # Team "command". Could be something like: "edit"
    if len(c_args) > 1:
        comm = c_args[1]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_course = None
    error_message = ""
    form = None
    course_teacher = None
    students = None
    total_students = 0
    editing = False

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Figure out if this is an "edit" or a "create"
        cur_entity = None
        if course_id != "create" and Course.objects.filter(id=course_id).exists():
            cur_course = Course.objects.get(id=course_id)
            cur_entity = cur_course
        else:
            cur_course = Course()

        # Process the form submission
        form = CourseForm(cur_entity, request.POST)
        if form.is_valid():

            # Update values in the database
            cur_course.title = form.cleaned_data.get('title')
            cur_course.year = form.cleaned_data.get('year')
            cur_course.grade = form.cleaned_data.get('grade')
            cur_course.teacher = form.cleaned_data.get('teacher')
            cur_course.active = form.cleaned_data.get('active')
            cur_course.done = form.cleaned_data.get('done')
            cur_course.info = form.cleaned_data.get('info')
            cur_course.save()

            # Make sure there is a "courses teached" entry
            if not CoursesTeached.objects.filter(teacher=cur_course.teacher, course=cur_course).exists():
                if cur_course is not None and cur_course.teacher is not None:
                    ct = CoursesTeached()
                    ct.teacher = cur_course.teacher
                    ct.course = cur_course
                    ct.save()

            # At this point the DB should be updated. Redirect to the course page
            return HttpResponseRedirect("/edu/course/{}".format(cur_course.id))

    else:
        # Get request or any other method.
        # See if we are viewing an existing course, editing an existing course,
        # or creating a new course
        if course_id == "create":
            # Only administrators can create a new course
            if admin is None:
                error_message = "*You must be an administrator to register a new course*"
            else:
                # Creating new, prepare form
                editing = True
                form = CourseForm(None)

        else:
            # Viewing or editing an existing course.
            # Fetch the desired course, redirecting to a 404 if it doesn't exist
            cur_course = get_object_or_404(Course, id=course_id)

            # Fetch the teacher and a list of all students for this course
            course_teacher = cur_course.teacher
            students = cur_course.students.all().order_by('last_name', 'first_name')
            total_students = len(students)

            # See if we are viewing or editing
            if comm == "edit":
                # Editing an existing course
                if cur_course.active is True:
                    # Not permitted if course is active and only
                    error_message = "*Course is active. No course editing is permitted*"
                elif admin is None:
                    # Only administrators can edit a course
                    error_message = "*You must be an administrator to edit a course*"
                else:
                    # We're editing a course, prepare a form
                    editing = True
                    form = CourseForm(cur_course)

        # Create a context and render the season page
        content = {'course_id': course_id, 'cur_course': cur_course, 'error_message': error_message, 'form': form, 'course_teacher': course_teacher, 'students': students, 'total_students': total_students, 'editing': editing, 'admin': admin, 'teacher': teacher, 'student': student}
        return render(request, 'edu/course.html', content)


# Single problem-related actions
@login_required
def problem(request, prob_arg):

    # Parse out problem argument information
    p_args = prob_arg.split("/")
    problem_id = p_args[0]
    comm = ""    # "command". Could be something like: "edit"
    if len(p_args) > 1:
        comm = p_args[1]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_problem = None
    error_message = ""
    form = None
    creating_student = False
    editing_admin = False
    editing_teacher = False
    is_assigned_educator = False

    # Image related variables
    problem_url = None
    solution_url = None

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Figure out if this is an "edit" or a "create"
        form_entity = None
        if problem_id != "create" and TutorProblem.objects.filter(id=problem_id).exists():
            # Existing edit
            cur_problem = TutorProblem.objects.get(id=problem_id)
            form_entity = cur_problem
        else:
            # Create new
            cur_problem = TutorProblem()

        # Process the form submission
        who = ''
        if student is not None:
            who = 'student'
        elif teacher is not None:
            who = 'teacher'
        elif admin is not None:
            who = 'admin'
        form = TutorProblemForm(form_entity, who, request.POST)
        if form.is_valid():

            # First fetch the items from the form
            if form.cleaned_data.get('approved') is not None:
                cur_problem.approved = form.cleaned_data.get('approved')
            if form.cleaned_data.get('grade') is not None:
                cur_problem.grade = form.cleaned_data.get('grade')
            if form.cleaned_data.get('comments') is not None:
                cur_problem.comments = form.cleaned_data.get('comments')

            # If this is a create by the student, we need to extract the
            # problem and solution canvas images the student drew and save
            # those off for later
            if problem_id == "create" and student is not None:
                cur_problem.creator         = student
                cur_problem.creator_bc_addr = cur_problem.creator.bc_addr
                # Extract the PROBLEM canvas data and save to object storage as a .PNG
                # image. Also, populate the relevant fields for where to find
                # this image again, when needed.
                canvasDataProblem = request.POST.get('canvasDataProblem', '')
                bucket = "problems"
                objName = "problem." + str(int(round(time.time() * 1000))) + ".student." + str(student.id) + ".png"
                theURL = saveImageToObject(canvasDataProblem, bucket, objName)
                cur_problem.problem_url = theURL
                # Extract the SOLUTION canvas data and save to object storage as a .PNG
                # image. Also, populate the relevant fields for where to find
                # this image again, when needed.
                canvasDataSolution = request.POST.get('canvasDataSolution', '')
                bucket = "solutions"
                objName = "solution." + str(int(round(time.time() * 1000))) + ".student." + str(student.id) + ".png"
                theURL = saveImageToObject(canvasDataSolution, bucket, objName)
                cur_problem.solution_url = theURL

                # Deploy to the Ethereum network as a smart contract
                cur_problem.address = deployTutorProblem(student.bc_addr, cur_problem.problem_url, cur_problem.solution_url)

                # Save to the database
                cur_problem.save()

            elif admin is not None:
                # Admin assignment of teacher
                if 'approver_choices' in form.cleaned_data:
                    # Fetch the educator by ID
                    if Educator.objects.filter(id=int(form.cleaned_data.get('approver_choices'))).exists():
                        cur_problem.approver         = Educator.objects.get(id=int(form.cleaned_data.get('approver_choices')))
                        cur_problem.approver_bc_addr = cur_problem.approver.bc_addr
                        cur_problem.assigned         = True

                        # Update the Ethereum smart contract with assignment
                        assignTutorProblem(cur_problem.address, cur_problem.approver_bc_addr)

                        # Save to the database
                        cur_problem.save()

            elif teacher is not None:
                # Teacher assessment of problem and solution
                if cur_problem.grade is not None:
                    cur_problem.assessed = True
                    cur_problem.approved = form.cleaned_data.get('approved')
                    cur_problem.grade    = form.cleaned_data.get('grade')
                    cur_problem.comments = form.cleaned_data.get('comments')

                    # Update the Ethereum smart contract with assessment
                    assessTutorProblem(cur_problem.address, cur_problem.approved, cur_problem.comments, cur_problem.grade)

                    # Save to the database
                    cur_problem.save()

                    # If the problem was approved, update the student's count
                    if cur_problem.approved is True:
                        cur_problem.creator.approved_tutor_problems += 1
                        cur_problem.creator.save()


            # Add a "tutor problems" entry if it doesn't already exist
            if not TutorProblems.objects.filter(tutor=cur_problem.creator, problem=cur_problem).exists():
                if cur_problem is not None and cur_problem.creator is not None:
                    tp = TutorProblems()
                    tp.tutor = cur_problem.creator
                    tp.problem = cur_problem
                    tp.save()

            # At this point the DB should be updated. Redirect to the page
            return HttpResponseRedirect("/edu/problem/{}".format(cur_problem.id))

    else:
        # Get request or any other method.
        # See if we are viewing an existing problem, editing an existing problem,
        # or creating a new problem
        if problem_id == "create":
            # Only students can create a new problem
            if student is None:
                error_message = "*You must be a student to create a new tutoring problem*"
            else:
                # Prepare a form for creating this problem
                creating_student = True
                form = TutorProblemForm(None, 'student')
        else:
            # Viewing or editing an existing problem.
            # Fetch the desired problem, redirecting to a 404 if it doesn't exist
            cur_problem = get_object_or_404(TutorProblem, id=problem_id)

            # See if we are viewing or editing
            if comm == "edit":
                # Editing an existing problem.
                # An administrator may edit to assign to a teacher.
                # A teacher may edit to assess and provide commentary.
                # A student may not edit.
                if student is not None:
                    # Students may not edit
                    error_message = "*Students are not permitted to change a problem after creation*"
                elif teacher is not None:
                    # Editing in teacher-mode.
                    # Prepare a form for assessment
                    editing_teacher = True
                    form = TutorProblemForm(cur_problem, 'teacher')
                elif admin is not None:
                    # Editing in admin-mode.
                    # Prepare a form for assignment
                    editing_admin = True
                    form = TutorProblemForm(cur_problem, 'admin')

            # Fetch public, anonymous-access links for images
            problem_url = cur_problem.problem_url
            solution_url = cur_problem.solution_url

            # Check to see if the assigned teacher is accessing this problem
            if teacher is not None and teacher == cur_problem.approver:
                is_assigned_educator = True

            # For now, this is locked down to only administrators, teachers, the student
            # creator, and students with answers related to this problem (TODO)
            if admin is None and teacher is None and (student is None or cur_problem not in student.tutor_problems.all()):
                # Redirect to the "all problems" page since this person shouldn't be allowed to view
                return HttpResponseRedirect("/edu/allproblems")

        # Create a context and render
        content = {'problem_id': problem_id, 'cur_problem': cur_problem, 'error_message': error_message, 'form': form, 'creating_student': creating_student, 'editing_teacher': editing_teacher, 'editing_admin': editing_admin, 'admin': admin, 'teacher': teacher, 'student': student, 'problem_url': problem_url, 'solution_url': solution_url, 'is_assigned_educator': is_assigned_educator}
        return render(request, 'edu/problem.html', content)


# Single answer-related actions
@login_required
def answer(request, answer_arg):

    # Parse out answer argument information
    a_args = answer_arg.split("/")
    answer_id = a_args[0]
    comm = ""    # "command". Could be something like: "edit"
    if len(a_args) > 1:
        comm = a_args[1]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_answer = None
    error_message = ""
    form = None
    creating_student = False
    editing_admin = False
    editing_teacher = False
    is_assigned_educator = False

    # Associated problem
    problem_id = None
    problem = None

    # Image related variables
    answer_url = None

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Figure out if this is an "edit" or a "create"
        form_entity = None
        if answer_id != "create" and TuteeAnswer.objects.filter(id=answer_id).exists():
            # Existing edit
            cur_answer = TuteeAnswer.objects.get(id=answer_id)
            form_entity = cur_answer
        else:
            # Create new
            cur_answer = TuteeAnswer()

        # Process the form submission
        who = ''
        if student is not None:
            who = 'student'
        elif teacher is not None:
            who = 'teacher'
        elif admin is not None:
            who = 'admin'
        form = TuteeAnswerForm(form_entity, who, request.POST)
        if form.is_valid():

            # First fetch the items from the form
            if form.cleaned_data.get('approved') is not None:
                cur_answer.approved = form.cleaned_data.get('approved')
            if form.cleaned_data.get('correct') is not None:
                cur_answer.correct = form.cleaned_data.get('correct')
            if form.cleaned_data.get('comments') is not None:
                cur_answer.comments = form.cleaned_data.get('comments')

            # If this is a create by the student, we need to extract the
            # answer canvas image the student drew and save
            # it off for later
            if answer_id == "create" and student is not None:
                # Extract the associated problem
                problem_id                  = int(comm)
                problem                     = get_object_or_404(TutorProblem, id=problem_id)
                cur_answer.problem          = problem
                cur_answer.answerer         = student
                cur_answer.answerer_bc_addr = cur_answer.answerer.bc_addr
                # Extract the ANSWER canvas data and save to object storage as a .PNG
                # image. Also, populate the relevant fields for where to find
                # this image again, when needed.
                canvasDataAnswer = request.POST.get('canvasDataAnswer', '')
                bucket = "answers"
                objName = "answer." + str(int(round(time.time() * 1000))) + ".student." + str(student.id) + ".png"
                theURL = saveImageToObject(canvasDataAnswer, bucket, objName)
                cur_answer.answer_url = theURL

                # Deploy to the Ethereum network as a smart contract
                cur_answer.address = deployTuteeAnswer(cur_answer.problem.address, student.bc_addr, cur_answer.answer_url)

                # Save to the database
                cur_answer.save()

            elif admin is not None:
                # Admin assignment of teacher
                if 'approver_choices' in form.cleaned_data:
                    # Fetch the educator by ID
                    if Educator.objects.filter(id=int(form.cleaned_data.get('approver_choices'))).exists():
                        cur_answer.approver         = Educator.objects.get(id=int(form.cleaned_data.get('approver_choices')))
                        cur_answer.approver_bc_addr = cur_answer.approver.bc_addr
                        cur_answer.assigned         = True

                        # Update the Ethereum smart contract with assignment
                        assignTuteeAnswer(cur_answer.address, cur_answer.approver_bc_addr)

                        # Save to the database
                        cur_answer.save()

            elif teacher is not None:
                # Teacher assessment of problem and solution
                cur_answer.assessed = True
                cur_answer.approved = form.cleaned_data.get('approved')
                cur_answer.correct    = form.cleaned_data.get('correct')
                cur_answer.comments = form.cleaned_data.get('comments')

                # Update the Ethereum smart contract with assessment
                assessTuteeAnswer(cur_answer.address, cur_answer.approved, cur_answer.correct, cur_answer.comments)

                # Save to the database
                cur_answer.save()

                # If the answer was approved, update the student's count
                if cur_answer.approved is True:
                    cur_answer.answerer.approved_tutee_answers += 1
                    cur_answer.answerer.save()

            # Add a "tutee answer" entry if it doesn't already exist
            if not TuteeAnswers.objects.filter(tutee=cur_answer.answerer, answer=cur_answer).exists():
                if cur_answer is not None and cur_answer.answerer is not None:
                    ta = TuteeAnswers()
                    ta.tutee = cur_answer.answerer
                    ta.answer = cur_answer
                    ta.save()

            # At this point the DB should be updated. Redirect to the page
            return HttpResponseRedirect("/edu/answer/{}".format(cur_answer.id))

    else:
        # Get request or any other method.
        # See if we are viewing an existing answer, editing an existing answer,
        # or creating a new answer
        if answer_id == "create":
            # Only students can create a new answer
            problem_id = int(comm)
            problem = get_object_or_404(TutorProblem, id=problem_id)
            if student is None:
                error_message = "*You must be a student to create a new tutoring answer*"
            else:
                # Prepare a form for creating this answer
                creating_student = True
                form = TuteeAnswerForm(None, 'student')
        else:
            # Viewing or editing an existing answer.
            # Fetch the desired answer, redirecting to a 404 if it doesn't exist
            cur_answer = get_object_or_404(TuteeAnswer, id=answer_id)

            # See if we are viewing or editing
            if comm == "edit":
                # Editing an existing answer.
                # An administrator may edit to assign to a teacher.
                # A teacher may edit to assess and provide commentary.
                # A student may not edit.
                if student is not None:
                    # Students may not edit
                    error_message = "*Students are not permitted to change an answer after creation*"
                elif teacher is not None:
                    # Editing in teacher-mode.
                    # Prepare a form for assessment
                    editing_teacher = True
                    form = TuteeAnswerForm(cur_answer, 'teacher')
                elif admin is not None:
                    # Editing in admin-mode.
                    # Prepare a form for assignment
                    editing_admin = True
                    form = TuteeAnswerForm(cur_answer, 'admin')

            # Fetch public, anonymous-access link for image
            answer_url = cur_answer.answer_url

            # Check to see if the assigned teacher is accessing this answer
            if teacher is not None and teacher == cur_answer.approver:
                is_assigned_educator = True

            # For now, this is locked down to only administrators, teachers, and the student creator
            if admin is None and teacher is None and (student is None or cur_answer not in student.tutee_answers.all()):
                # Redirect to the "all answers" page since this person shouldn't be allowed to view
                return HttpResponseRedirect("/edu/allanswers")

        # Create a context and render
        content = {'answer_id': answer_id, 'cur_answer': cur_answer, 'error_message': error_message, 'form': form, 'creating_student': creating_student, 'editing_teacher': editing_teacher, 'editing_admin': editing_admin, 'admin': admin, 'teacher': teacher, 'student': student, 'answer_url': answer_url, 'is_assigned_educator': is_assigned_educator, 'problem_id': problem_id, 'problem': problem}
        return render(request, 'edu/answer.html', content)


# Tutoring session waiting room
@login_required
def sessionwaiting(request):

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    error_message = ""
    form = None

    # Ensure that this user is a student
    student = None
    if Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission.
    # Only students are allowed to initiate a session.
    if request.method == 'POST' and student is not None:

        # Process the form submission
        form = TutoringSessionMatchForm(request.POST)
        if form.is_valid():

            # Determine whether the student desires to be a Tutor or a Tutee
            role = form.cleaned_data.get('role')
            grade = None
            if 'grade' in form.cleaned_data:
                grade = form.cleaned_data.get('grade')
            if grade is None:
                grade = student.grade

            # Forward this information along so that a session can be created
            return session(request, student, role, grade)

    elif student is not None:

        # Render a tutoring session match making form page
        form = TutoringSessionMatchForm()

    else:
        error_message = "*Must be a student to participate in a tutoring session!*"

    # Catch-all redirect to home page
    content = {'form': form, 'error_message': error_message}
    return render(request, 'edu/sessionwaiting.html', content)


# Live tutoring session between two students
@login_required
def session(request, student, role, grade):

    # This is a bit of a hacky way of doing this but...
    # A database table tracks all tutoring session "room" names that have
    # been created. This view function controls the creation and destruction
    # of these room records in the database. We first check to see if an
    # "active" room exists at the grade level we're interested in. If it does,
    # then we feed this room name to the session.html template and the appropriate
    # Django channel (WebSocket) connection is made to that room and we mark the
    # room as "full" with 2 students.
    # If an "active" and not "full" does not exist in the database,
    # then we create one in this view, add it to the database, and pass it
    # to the template. Within the template, a call is made in the connect() function
    # of the consumers.py ChatConsumer class to set the DB record to "active" if it
    # is not already "active" to represent the fact that we have connected.
    # A cleanup routine is performed to remove "stale" entries in the database.
    # NOTE: There are multiple timing windows in this implementation where >2 students
    # could perhaps end up in the same room or duplicate rooms could be created or
    # students coming in from different places may never get matched if they attempt
    # to join at the same time. A mature implementation would require a more sophisticated
    # system such as "queueing" to ensure accurate match-making. This should work for
    # this prototype, however.

    # Variables for the tutoring session
    student_name = ""
    tutor = None
    tutee = None
    tutor_string = ""
    tutee_string = ""
    tutor_problems = None

    # Only proceed if this is a student
    if student is not None:

        student_name = "%s %s" % (student.first_name, student.last_name)

        # Behavior differences:
        #
        # (a) If this student wants to be a tutor, check to see if there are any
        # tutoring sessions that have a tutee but no tutor. Start by looking
        # for the tutor's current grade level and working down until the first
        # match is found.
        #
        # (b) If this student wants to be a tutee, check to see if there is
        # any tutoring sessions that have a tutor but no tutee and that matches
        # the desired grade level.
        grade = str(grade).upper()  # For grade conversion of:  k => K
        room = None
        if role == "Tutor":
            tutor = student
            tutor_string = "{} {} (Grade={})".format(tutor.first_name, tutor.last_name, tutor.grade)
            if TutoringSessionRoom.objects.filter(tutors=0, tutees=1).exists():
                # Start with this student's grade and work down, matching with
                # the first matching available session
                options = TutoringSessionRoom.objects.filter(tutors=0, tutees=1).all()
                if student.grade == "K":
                    for option in options:
                        if option.grade == "K":
                            room = option
                            break
                else:
                    cur_grade = int(student.grade)
                    while cur_grade > 0:
                        for option in options:
                            if option.grade == str(cur_grade):
                                room = option
                                break
                        if room is not None:
                            break

            # Create a room if none matching found
            if room is None:
                # Create a new room. Use the current user's student id and the current system
                # time to give the room an unique, WebSocket-friendly name
                room = TutoringSessionRoom()
                millis = int(round(time.time() * 1000))
                room.room_name = "session_%s_%s" % (student.id, millis)
                room.grade = student.grade   # Use tutor's grade
            
            # Setup tutor and save
            room.tutors = 1
            room.save()

            # Fetch a list of all of the *approved* tutoring problems of the appropriate
            # grade level that this tutor has created
            tutor_problems = TutorProblem.objects.filter(creator=tutor, approved=True).all()

        elif role == "Tutee":
            tutee = student
            tutee_string = "{} {} (Grade={})".format(tutee.first_name, tutee.last_name, tutee.grade)
            if TutoringSessionRoom.objects.filter(grade=grade, tutors=1, tutees=0).exists():
                # Match with the first entry found
                options = TutoringSessionRoom.objects.filter(grade=grade, tutors=1, tutees=0).all()
                room = options[0]
                room.tutees = 1
                room.save()
            else:
                # Create a new room. Use the current user's student id and the current system
                # time to give the room an unique, WebSocket-friendly name
                room = TutoringSessionRoom()
                millis = int(round(time.time() * 1000))
                room.room_name = "session_%s_%s" % (student.id, millis)
                room.grade = grade  # Use passed-in grade
                room.tutees = 1
                room.save()

        # We now have a room to join that is recorded in the database.
        # Pass the room name along to template so that the relevant consumer can process it.

        # Populate content and render page
        content = {'student': student, 'student_name': student_name, 'tutor': tutor, 'tutee': tutee, 'tutor_string': tutor_string, 'tutee_string': tutee_string, 'room_name': room.room_name, 'room_name_json': mark_safe(json.dumps(room.room_name)), 'tutor_problems': tutor_problems}
        return render(request, 'edu/session.html', content)

    # Catch-all behavior will be to redirect to the home page
    return HttpResponseRedirect("/edu")


# Viewing, Assigning, Assessing a tutoring session that has taken places
@login_required
def sessionreview(request, session_arg):

    # Parse out session argument information
    s_args = session_arg.split("/")
    session_id = s_args[0]
    comm = ""    # "command". Could be something like: "edit"
    if len(s_args) > 1:
        comm = s_args[1]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_session = None
    error_message = ""
    form = None
    editing_admin = False
    editing_teacher = False
    is_assigned_educator = False

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Fetch the needed session
        cur_session = get_object_or_404(TutoringSession, id=session_id)

        # Process the form submission
        who = ''
        if student is not None:
            who = 'student'
        elif teacher is not None:
            who = 'teacher'
        elif admin is not None:
            who = 'admin'
        form = TutoringSessionForm(cur_session, who, request.POST)
        if form.is_valid():

            # First fetch the items from the form
            cur_session.approved = form.cleaned_data.get('approved')
            cur_session.comments = form.cleaned_data.get('comments')

            # Admin assignment or teacher assessment
            if admin is not None:
                # Admin assignment of teacher
                if 'approver_choices' in form.cleaned_data:
                    if Educator.objects.filter(id=int(form.cleaned_data.get('approver_choices'))).exists():
                        cur_session.approver = Educator.objects.get(id=int(form.cleaned_data.get('approver_choices')))
                        cur_session.assigned = True
                        cur_session.save()

                        # Update the Ethereum smart contract with assignment
                        assignTuteeAnswer(cur_session.address, cur_session.approver.bc_addr)

            elif teacher is not None:
                # Teacher assessment of session
                cur_session.assessed = True
                cur_session.approved = form.cleaned_data.get('approved')
                cur_session.comments = form.cleaned_data.get('comments')
                cur_session.save()

                # Update the Ethereum smart contract with assessment
                assessTutoringSession(cur_session.address, cur_session.approved, cur_session.comments)

                # If the session was approved, update each students' counts
                if cur_session.approved is True:
                    cur_session.tutor.approved_session_seconds += cur_session.duration_seconds
                    cur_session.tutor.save()
                    cur_session.tutee.approved_session_seconds += cur_session.duration_seconds
                    cur_session.tutee.save()

            # At this point the DB should be updated. Redirect to the page
            return HttpResponseRedirect("/edu/sessionreview/{}".format(cur_session.id))

    else:
        # Get request or any other method.
        # See if we are viewing an existing session or editing an existing session

        # Fetch the needed session
        cur_session = get_object_or_404(TutoringSession, id=session_id)

        # See if we are viewing or editing
        if comm == "edit":
            # Editing an existing session.
            # An administrator may edit to assign to a teacher.
            # A teacher may edit to assess and provide commentary.
            # A student may not edit.
            if student is not None:
                # Students may not edit
                error_message = "*Students are not permitted to edit a tutoring session*"
            elif teacher is not None:
                # Editing in teacher-mode.
                # Prepare a form for assessment
                editing_teacher = True
                form = TutoringSessionForm(cur_session, 'teacher')
            elif admin is not None:
                # Editing in admin-mode.
                # Prepare a form for assignment
                editing_admin = True
                form = TutoringSessionForm(cur_session, 'admin')

        # For now, this is locked down to only administrators, teachers, and the student creator
        if admin is None and teacher is None and (student is None or cur_session not in student.tutoring_sessions.all()):
            # Redirect to the "all sessions" page since this person shouldn't be allowed to view
            return HttpResponseRedirect("/edu/allsessions")

        # Get a list of problems and answers that were tackled during this session
        problem_list = []
        answer_list = []
        problem_IDs = []
        answer_IDs = []
        if cur_session.problem_id_list is not None and type(cur_session.problem_id_list) == str:
            problem_IDs = cur_session.problem_id_list.split(',')
            for pid in problem_IDs:
                if pid != '':
                    problem_list.append(TutorProblem.objects.get(id=int(pid)))
        if cur_session.answer_id_list is not None and type(cur_session.answer_id_list) == str:
            answer_IDs = cur_session.answer_id_list.split(',')
            for pid in answer_IDs:
                if pid != '':
                    answer_list.append(TuteeAnswer.objects.get(id=int(pid)))

        # Get the chat log.
        # NOTE: This is a bit of a hacky way of doing this!!
        # We're extracting the bucketname and the object name from the URL
        # and using a utility function to load it into a Python string
        data = cur_session.chat_log_url.split('/')
        chat_log = loadTextFromObject(data[-2], data[-1])

        # Check to see if the assigned teacher is accessing this problem
        if teacher is not None and teacher == cur_session.approver:
            is_assigned_educator = True

        # Create a context and render
        content = {'cur_session': cur_session, 'error_message': error_message, 'form': form, 'editing_teacher': editing_teacher, 'editing_admin': editing_admin, 'admin': admin, 'teacher': teacher, 'student': student, 'problem_list': problem_list, 'answer_list': answer_list, 'chat_log': chat_log, 'is_assigned_educator': is_assigned_educator}
        return render(request, 'edu/sessionreview.html', content)


# Single badge-related actions
@login_required
def badge(request, badge_arg):

    # Parse out course argument information
    b_args = badge_arg.split("/")
    badge_id   = b_args[0]
    teacher_id = None
    student_id = None
    if len(b_args) > 1:
        teacher_id = b_args[1]
    if len(b_args) > 2:
        student_id = b_args[2]

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    cur_badge = None
    error_message = ""
    form = None
    editing = False

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Check to see if we are processing an incoming form submission
    if request.method == 'POST':

        # Only creation of new badges allowed. No editing existing
        cur_badge = Badge()

        # Process the form submission
        form = BadgeForm(request.POST)
        if form.is_valid():

            # Update values in the database
            teacher = get_object_or_404(Educator, id=teacher_id)
            # A form with the student_id preset or where they made the choice
            # with a selector
            if student_id is not None:
                student = get_object_or_404(Student, id=student_id)
            else:
                student = get_object_or_404(Student, id=int(form.cleaned_data.get('student_choices')))
            cur_badge.title = form.cleaned_data.get('title')
            cur_badge.info = form.cleaned_data.get('info')
            cur_badge.student = student
            cur_badge.teacher = teacher

            # Deploy to the Ethereum network as a smart contract
            cur_badge.address = deployBadge(cur_badge.title, cur_badge.info, cur_badge.student.bc_addr, cur_badge.teacher.bc_addr)

            # Save to the database
            cur_badge.save()

            # Increment student's badge count
            cur_badge.student.badge_count += 1
            cur_badge.student.save()

            # Add a "badges earned" entry if it doesn't already exist
            if not BadgesEarned.objects.filter(badge=cur_badge, student=cur_badge.student).exists():
                if cur_badge is not None and cur_badge.student is not None:
                    be = BadgesEarned()
                    be.badge = cur_badge
                    be.student = cur_badge.student
                    be.save()

            # At this point the DB should be updated. Redirect to the admin page
            return HttpResponseRedirect("/edu/badge/{}".format(cur_badge.id))

        # Form wasn't valid, redirect to all admins page
        return HttpResponseRedirect("/edu/allbadges")

    else:
        # Get request or any other method.
        # See if we are viewing an existing admin or creating a new admin
        if badge_id == "create":
            # Only teachers can create badges
            if teacher is None:
                error_message = "*You must be a teacher to create digital badges*"
            else:
                # Prepare a form for assignment to a student
                teacher = get_object_or_404(Educator, id=teacher_id)
                if student_id is not None:
                    student = get_object_or_404(Student, id=student_id)
                editing = True
                form = BadgeForm()

        else:
            # Viewing an existing badge.
            # Fetch the desired badge, redirecting to a 404 if it doesn't exist
            cur_badge = get_object_or_404(Badge, id=badge_id)

        # Create a context and render the season page
        content = {'cur_badge': cur_badge, 'error_message': error_message, 'form': form, 'editing': editing, 'admin': admin, 'teacher': teacher, 'student': student, 'student_id': student_id, 'teacher_id': teacher_id}
        return render(request, 'edu/badge.html', content)


# Display all courses
@login_required
def allcourses(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    active_courses = Course.objects.filter(active=True).all()
    past_courses = Course.objects.filter(done=True).all()
    content = {'active_courses': active_courses, 'past_courses': past_courses, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/allcourses.html', content)


# Display all administrators
@login_required
def alladmins(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    all_admins = Administrator.objects.all()
    content = {'all_admins': all_admins, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/alladmins.html', content)


# Display all teachers
@login_required
def allteachers(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    all_teachers = Educator.objects.all()
    content = {'all_teachers': all_teachers, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/allteachers.html', content)


# Display all students
@login_required
def allstudents(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    all_students = Student.objects.all()
    content = {'all_students': all_students, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/allstudents.html', content)


# Display all tutor problems
@login_required
def allproblems(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    approved_problems = TutorProblem.objects.filter(approved=True).all()
    assigned_problems = TutorProblem.objects.filter(assigned=True, assessed=False).all()
    unassigned_problems = TutorProblem.objects.filter(assigned=False, assessed=False).all()
    content = {'approved_problems': approved_problems, 'assigned_problems': assigned_problems, 'unassigned_problems': unassigned_problems, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/allproblems.html', content)


# Display all tutee answers
@login_required
def allanswers(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    correct_answers = TuteeAnswer.objects.filter(approved=True, correct=True).all()
    assigned_answers = TuteeAnswer.objects.filter(assigned=True, assessed=False).all()
    unassigned_answers = TuteeAnswer.objects.filter(assigned=False, assessed=False).all()
    content = {'correct_answers': correct_answers, 'assigned_answers': assigned_answers, 'unassigned_answers': unassigned_answers, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/allanswers.html', content)


# Display all tutoring sessions
@login_required
def allsessions(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    approved_sessions = TutoringSession.objects.filter(approved=True).all()
    assigned_sessions = TutoringSession.objects.filter(assigned=True, assessed=False).all()
    unassigned_sessions = TutoringSession.objects.filter(assigned=False, assessed=False).all()
    content = {'approved_sessions': approved_sessions, 'assigned_sessions': assigned_sessions, 'unassigned_sessions': unassigned_sessions, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/allsessions.html', content)


# Display all student badges
@login_required
def allbadges(request):

    # Keep track of current user
    current_user = request.user

    # Determine whether the logged in user is an Administrator, Teacher, or Student
    admin = None
    teacher = None
    student = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)
    elif Educator.objects.filter(user=current_user).exists():
        teacher = Educator.objects.get(user=current_user)
    elif Student.objects.filter(user=current_user).exists():
        student = Student.objects.get(user=current_user)

    # Gather other relevant info and render
    all_badges = Badge.objects.all()
    content = {'all_badges': all_badges, 'admin': admin, 'teacher': teacher, 'student': student}
    return render(request, 'edu/allbadges.html', content)


# Display student leaderboard
@login_required
def leaders(request):

    # Various listings of students
    tutor_students = None
    tutee_students = None
    tutoring_session_students = None
    badge_students = None

    # Gather the Top 10 of each category
    tutor_students            = Student.objects.all().order_by('-approved_tutor_problems')[:10]
    tutee_students            = Student.objects.all().order_by('-approved_tutee_answers')[:10]
    tutoring_session_students = Student.objects.all().order_by('-approved_session_seconds')[:10]
    badge_students            = Student.objects.all().order_by('-badge_count')[:10]

    # Render the leadersboards
    content = {'tutor_students': tutor_students, 'tutee_students': tutee_students, 'tutoring_session_students': tutoring_session_students, 'badge_students': badge_students}
    return render(request, 'edu/leaders.html', content)


# NOTE: The following function is not used and is deprecated
# Utility view to fetch an image from object storage and prepare it as a URL for
# viewing within a webpage
def getImage(request, entity):
    # Parse out the bucket and key
    args = entity.split("/")
    bucket = args[0]
    key = args[1]
    buffer = loadImageBufferFromObject(bucket, key)
    print(str(buffer))
    return HttpResponse(buffer, content_type="image/png")


# AJAX function to a tutee to create an answer from a form
@login_required
@csrf_exempt
def ajaxcreateanswer(request):

    # Ensure that this is a POST
    if request.method == 'POST':

        print("POST DATA:\n" + json.dumps(dict(request.POST)))

        # Parse out the needed data and fill in a new answer
        cur_answer = TuteeAnswer()
        problemID       = int(request.POST.get('problemID'))
        tuteeID         = int(request.POST.get('tuteeID'))
        answerImgBase64 = request.POST.get('answerImgBase64')
        # Only proceed if the tutor and problem exist in our DB
        if Student.objects.filter(id=tuteeID).exists() and TutorProblem.objects.filter(id=problemID).exists():

            # Parse out the normal set of data
            answerer            = Student.objects.get(id=tuteeID)
            problem             = TutorProblem.objects.get(id=problemID)
            cur_answer.problem  = problem
            cur_answer.answerer = answerer
            cur_answer.answerer_bc_addr = cur_answer.answerer.bc_addr

            # Extract the ANSWER canvas data and save to object storage as a .PNG
            # image. Also, populate the relevant fields for where to find
            # this image again, when needed.
            bucket = "answers"
            objName = "answer." + str(int(round(time.time() * 1000))) + ".student." + str(answerer.id) + ".png"
            theURL = saveImageToObject(answerImgBase64, bucket, objName)
            cur_answer.answer_url = theURL

            # Deploy to the Ethereum network as a smart contract
            cur_answer.address = deployTuteeAnswer(cur_answer.problem.address, answerer.bc_addr, cur_answer.answer_url)

            # Save to the database
            cur_answer.save()

            # Add a "tutee answer" entry if it doesn't already exist
            if not TuteeAnswers.objects.filter(tutee=cur_answer.answerer, answer=cur_answer).exists():
                if cur_answer is not None and cur_answer.answerer is not None:
                    ta = TuteeAnswers()
                    ta.tutee = cur_answer.answerer
                    ta.answer = cur_answer
                    ta.save()

            # Return JSON data for the answerID and answerURL
            returnData = {}
            returnData["answerID"] = cur_answer.id
            returnData["answerURL"] = cur_answer.answer_url
            return JsonResponse(returnData)

    # Return a server error if we get here
    return HttpResponse(status=500)


# AJAX function to create a tutoring session
@login_required
@csrf_exempt
def ajaxcreatesession(request):

    # Ensure that this is a POST
    if request.method == 'POST':

        print("POST DATA:\n" + json.dumps(dict(request.POST)))

        # Parse out the needed data to fill in a new session
        cur_session   = TutoringSession()
        tutorID       = int(request.POST.get('tutorID'))
        tuteeID       = int(request.POST.get('tuteeID'))
        start         = int(request.POST.get('start'))
        end           = int(request.POST.get('end'))
        duration      = int(request.POST.get('duration'))
        problemIDList = request.POST.get('problemIDList')
        answerIDList  = request.POST.get('answerIDList')
        chatLog       = request.POST.get('chatLog')

        # Only proceed if the tutor and tutee exist in our DB
        if Student.objects.filter(id=tuteeID).exists() and Student.objects.filter(id=tuteeID).exists():

            # Parse out the normal set of data
            cur_session.tutor            = Student.objects.get(id=tutorID)
            cur_session.tutor_grade      = cur_session.tutor.grade
            cur_session.tutee            = Student.objects.get(id=tuteeID)
            cur_session.tutee_grade      = cur_session.tutee.grade
            cur_session.start            = datetime.datetime.fromtimestamp(start)   # Convert from epoch seconds
            cur_session.end              = datetime.datetime.fromtimestamp(end)     # Convert from epoch seconds
            cur_session.duration_seconds = duration
            cur_session.problem_id_list  = problemIDList
            cur_session.answer_id_list   = answerIDList

            # TODO: Auto-assign approving educator if one is available!

            # Save the chat log off as an object storage object and record the URL
            bucket = "chatlogs"
            objName = "chatlog." + str(int(round(time.time() * 1000))) + ".tutor." + str(cur_session.tutor.id) + ".tutee." + str(cur_session.tutee.id) + ".txt"
            theURL = saveTextToObject(chatLog, bucket, objName)
            cur_session.chat_log_url = theURL

            # Save to the database
            cur_session.save()

            # Deploy to the Ethereum network as a smart contract *after* initial database
            # save so that we can first get an ID
            cur_session.address = deployTutoringSession(cur_session.id, cur_session.tutor.bc_addr, cur_session.tutee.bc_addr, cur_session.tutor_grade, cur_session.tutee_grade, cur_session.start.strftime("%m/%d/%Y, %H:%M:%S"), cur_session.end.strftime("%m/%d/%Y, %H:%M:%S"), cur_session.duration_seconds, cur_session.chat_log_url, cur_session.problem_id_list, cur_session.answer_id_list)

            # Save again to record the blockchain address
            cur_session.save()

            # Add relevant "TutoringSessions" entries for the tutor and tutee
            ts = TutoringSessions()
            ts.student = cur_session.tutor
            ts.session = cur_session
            ts.save()
            ts = TutoringSessions()
            ts.student = cur_session.tutee
            ts.session = cur_session
            ts.save()

    # Return an empty response
    return HttpResponse(status=200)


# Administrator view to add students to courses
""" @login_required
def addstudentcourse(request):

    # Keep track of current user
    current_user = request.user

    # Other variables for context
    form = None

    # Determine whether the logged in user is an Administrator
    admin = None
    if Administrator.objects.filter(user=current_user).exists():
        admin = Administrator.objects.get(user=current_user)

    # Only proceed if this user is an admin. Otherwise, redirect to the home page
    if admin is None:
        return HttpResponseRedirect("/edu")

    # If this is a POST request, then we should be processing an incoming form
    if request.method == 'POST':
        
        # Check whether the form is valid and process it
        form = AddStudentToCourseForm(request.POST)
        if form.is_valid():
        
            # Add the appropriate students to the appropriate courses
            for cur_student in form.cleaned_data.get('student_choices'):
                for cur_course in form.cleaned_data.get('course_choices'):
                    # Add an appropriate entry, if it doesn't already exist
                    if not CoursesAttended.objects.filter(student=cur_student, course=cur_course).exists():
                        ca = CoursesAttended()
                        ca.student = cur_student
                        ca.course = cur_course
                        ca.save()

            # Redirect to the school home page
            return HttpResponseRedirect("/edu")

    # Otherwise, the user is requesting a page with the form embedded in it to fill out
    form = AddStudentToCourseForm()
    content = {'form': form}
    return render(request, 'edu/addstudentcourse.html', content) """
