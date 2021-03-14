from django.contrib import messages
from django.contrib.auth.forms import AdminPasswordChangeForm
from django.contrib.auth.models import User
from django.contrib.auth import update_session_auth_hash, authenticate, login, logout
from django.shortcuts import render, redirect
from django.http import HttpResponseRedirect
from django.core.exceptions import ValidationError
from django.core.mail import EmailMessage
from django.contrib.auth.decorators import login_required
from social_django.models import UserSocialAuth

import django.contrib.auth.password_validation as validators

from .models import UserProfile, UserForm, MergeForm, UserProfileForm, SitePhoto
from .forms import LoginForm, RegisterForm, SignUpForm, ChangePasswordForm

from edu.models import Administrator, Educator, Student


def index(request):
    # Redirect to education page
    return HttpResponseRedirect('/edu')


# Login page
def login_view(request):
    if request.POST:
        form = LoginForm({'username': request.POST['username'],
                          'password': request.POST['password'],
                          })
        user = authenticate(username=request.POST['username'],
                            password=request.POST['password'])
        if user is not None:
            if user.is_active:
                login(request, user, backend='django_tutortime.backends.EmailOrUsernameModelBackend')
                # Probably some check here to see if they need objects made for them
                return HttpResponseRedirect('/')
    else:
        print(locals())
        form = LoginForm()
    return render(request, 'login_page.html', {'forms': form})


# registration page
def register_view(request, errors=None):
    if request.POST:
        form = RegisterForm({'username': request.POST['username'],
                             'password': request.POST['password'],
                             'password1': request.POST['password1'],
                             })
        if form.is_valid():
            username = request.POST['username']
            password = request.POST['password']
            user = User.objects.create_user(username=username, password=password)
            # here you might want to add other objects to the user ( like if there is a checkbox for "register as player")
            user = authenticate(username=request.POST['username'],
                                password=request.POST['password'])
            login(request, user, backend='django_tutortime.backends.EmailOrUsernameModelBackend')
            return HttpResponseRedirect("/")
    else:
        form = RegisterForm()
    return render(request, "register_page.html", {'forms': form})


def test_view(request):
    return render(request, 'auth_test.html')


# quick little logout script
def logout_view(request):
    logout(request)
    return HttpResponseRedirect("/")


# ***
# Below are James' views that are currently being used
# ***
def signup(request):
    if request.method == 'POST':
        form  = SignUpForm(request.POST)
        pform = UserProfileForm(request.POST, request.FILES)
        if form.is_valid() and pform.is_valid():

            # User-specific items
            user = form.save()
            login(request, user)

            # UserProfile items
            profile = UserProfile()
            profile.user = user

            # Set phone number and profile images, if they were specified
            if pform.cleaned_data['phone_number']:
                profile.phone_number = pform.cleaned_data['phone_number']
            if request.FILES and 'profile_image' in request.FILES:
                profile.profile_image = request.FILES['profile_image']

            # Save the profile
            profile.save()

            # Only redirect if their was a next value in the URL
            if 'next' in request.GET:
                return redirect(request.GET['next'])
            else:
                return redirect('/')
    else:
        form  = SignUpForm()
        pform = UserProfileForm()
    return render(request, 'signup.html', {'form': form, 'pform': pform})


def signup2(request):
    if request.method == 'POST':
        form  = SignUpForm(request.POST)
        pform = UserProfileForm(request.POST, request.FILES)
        if form.is_valid() and pform.is_valid():

            # User-specific items
            user = form.save()
            login(request, user)

            # UserProfile items
            profile = UserProfile()
            profile.user = user

            # Set phone number and profile images, if they were specified
            if pform.cleaned_data['phone_number']:
                profile.phone_number = pform.cleaned_data['phone_number']
            if request.FILES and 'profile_image' in request.FILES:
                profile.profile_image = request.FILES['profile_image']

            # Save the profile
            profile.save()

            # Only redirect if their was a next value in the URL
            if 'next' in request.GET:
                return redirect(request.GET['next'])
            else:
                return redirect('/')
    else:
        form  = SignUpForm()
        pform = UserProfileForm()
    return render(request, 'signup2.html', {'form': form, 'pform': pform})


def changepassword(request):
    error_message     = ""
    success_message   = ""
    validation_errors = []
    if request.method == 'POST':
        form = ChangePasswordForm({'username': request.POST['username'],
                                   'old_password': request.POST['old_password'],
                                   'new_password1': request.POST['new_password1'],
                                   'new_password2': request.POST['new_password2'],
                                   })
        user = authenticate(username=request.POST['username'],
                            password=request.POST['old_password'])
        if user is not None:
            if request.POST['new_password1'] != request.POST['new_password2']:
                error_message = "*New passwords do not match*"
                logout(request)
            else:
                # Ensure the password validated against our rules
                try:
                    validators.validate_password(password=request.POST['new_password1'], user=user)
                except ValidationError as e:
                    validation_errors = set(list(e.messages))   # Unique error messages
                if validation_errors:
                    error_message = "*New password does not conform to password strength rules*"
                else:
                    # Change the user's password and log them in
                    user.set_password(request.POST['new_password1'])
                    user.save()
                    login(request, user, backend='django_tutortime.backends.EmailOrUsernameModelBackend')
                    success_message = "*Password changed successfully for user {}*".format(request.POST['username'])
        else:
            error_message = "*The Username and/or Old password are incorrect*"
    else:
        # If logged in and navigating here, log the user out
        username = ''
        if request.user is not None:
            username = request.user.username
            logout(request)
        form = ChangePasswordForm(request.user, initial={'username': username})
    content = {'form': form, 'error_message': error_message, 'validation_errors': validation_errors, 'success_message': success_message}
    return render(request, 'changepassword.html', content)


# This view is redirected to after Facebook Login.
# Here we can check to see, if this is a new user account,
# whether we need to link to an existing account with
# the same first-name/last-name OR the same email.
# A check for first-name/last-name is performed first.
# Only if that fails is an email check performed.
# This view is also used to generate a UserProfile for
# users who have created an account through Facebook
# but don't have a UserProfile yet (since the signup()
# function was not used in that code path).
@login_required
def checkfb(request):
    # The user associated with this request
    current_user = request.user

    # Check to make sure this user has a Facebook login
    # associated with them. Otherwise, there is no reason
    # to continue.
    facebook_login = None
    try:
        facebook_login = current_user.social_auth.get(provider='facebook')
    except UserSocialAuth.DoesNotExist:
        facebook_login = None

    # Continue if this user has an attached facebook login
    if facebook_login is not None:
        # We only need to continue to check this user if they
        # do not have a UserProfile. This means that they generated
        # their account using Facebook and this is the first time
        # they are logging in. So, this is a shortcut for knowing
        # we need to check their account AND that we need to create
        # a UserProfile for them.
        if not UserProfile.objects.filter(user=current_user).exists():
            # No matter what, create and save a UserProfile for this user
            profile      = UserProfile()
            profile.user = current_user
            # TODO: Insert Facebook profile image here!!!
            profile.save()

            # Next, check to see if there is another user with the
            # same first name and last name as this user.
            # Note: We only get the first one we find here...
            other_user = None
            if User.objects.filter(first_name__iexact=current_user.first_name, last_name__iexact=current_user.last_name).exclude(id=current_user.id).exists():
                # Found user with different ID but same first-name and last-name
                other_users = User.objects.filter(first_name__iexact=current_user.first_name, last_name__iexact=current_user.last_name).exclude(id=current_user.id).all()
                other_user  = other_users[0]
            elif User.objects.filter(email__iexact=current_user.email).exclude(id=current_user.id).exists():
                # Found user with different ID but same email
                other_users = User.objects.filter(email__iexact=current_user.email).exclude(id=current_user.id).all()
                other_user  = other_users[0]

            # If another user was found, direct the current user to a page
            # with a form where they are asked to confirm if they would like
            # to "link" Facebook to the existing account instead.
            if other_user is not None:
                form    = MergeForm()
                content = {'current_user': current_user, 'other_user': other_user, 'form': form}
                return render(request, 'checkfb.html', content)

        else:
            # A user profile exists. Take this opportunity to set the UserProfile
            # image to be the Facebook profile picture, if no image is set already :-)
            profile = UserProfile.objects.get(user=current_user)
            # TODO: Insert Facebook profile image here!!!

    # Fall back to the home page
    return redirect('/')


# View used to process a confirmation or denail to the question of whether to
# link an existing user account to the current Facebook-enabled (different) account.
@login_required
def mergefb(request, fb_user_id, existing_user_id):
    if request.method == "POST":
        form = MergeForm(request.POST)
        if form.is_valid():
            #  Check if the choice is to merge
            if form.cleaned_data['merge'] and form.cleaned_data['merge'] is True:
                # The choice is to merge!
                # This is pretty easy. The steps are:
                #   - Logout user from Facebook user
                #   - Login the user to the old user
                #   - Delete the Facebook user
                #   - Call the Facebook Connect link to
                #     establish the new link
                if User.objects.filter(id=fb_user_id).exists() and User.objects.filter(id=existing_user_id).exists():
                    fb_user       = User.objects.get(id=fb_user_id)
                    existing_user = User.objects.get(id=existing_user_id)
                    logout(request)
                    login(request, existing_user, backend='django_tutortime.backends.EmailOrUsernameModelBackend')
                    fb_user.delete()
                    return redirect('/oauth/login/facebook/')

    # Fall back to the home page
    return redirect('/')


# View for a user's main INNIT user profile.
# From here a user can view any of the various "current" leagues
# they're currently registered for, among other items.
# They can also connect/disconnect their profile to/from Facebook
@login_required
def user_profile(request, user_arg):

    # Parse out user argument information
    u_args = user_arg.split("/")
    user_id   = u_args[0]
    u_comm = ""    # User "command". Could be something like: "edit"
    if len(u_args) > 1:
        u_comm = u_args[1]

    # Only a particular authenticated user can view or edit their own profile
    current_user = request.user
    if str(current_user.id) == str(user_id):

        # Fetch this user's profile object, if it exists
        profile = None
        if UserProfile.objects.filter(user=current_user).exists():
            # Get existing profile
            profile = UserProfile.objects.get(user=current_user)

        # Check to see if this is a POST, in which case, it's a User edit so
        # we need to take care of updating the database first
        if request.method == "POST":
            uform = UserForm(request.POST)
            pform = UserProfileForm(request.POST, request.FILES)
            if uform.is_valid() and pform.is_valid():

                # Update the user's fields (which are all required in the form)
                print("All valid!\n")
                current_user.first_name = uform.cleaned_data['first_name']
                current_user.last_name = uform.cleaned_data['last_name']
                current_user.email = uform.cleaned_data['email']
                current_user.save()

                # Check to see if there is already a UserProfile object, or whether
                # we will create one now
                if profile is None:
                    # Initialize a profile
                    profile = UserProfile()
                    profile.user = current_user

                # Set phone number and profile images, if they were specified
                if pform.cleaned_data['phone_number']:
                    profile.phone_number = pform.cleaned_data['phone_number']
                else:
                    # Erase phone number
                    profile.phone_number = ""
                if request.FILES and 'profile_image' in request.FILES:
                    profile.profile_image = request.FILES['profile_image']
                    print("Profile image provided!\n")
                elif pform.cleaned_data['profile_image'] is False:
                    print("User wants to remove profile image!")
                    profile.profile_image = None

                # Save the profile
                profile.save()

            else:
                print("User errors: {}\n".format(uform.errors))
                print("Profile errors: {}\n".format(pform.errors))

        # Prep editing forms just in case the user want to edit their profile
        uform = UserForm(initial={'username': current_user.username, 'first_name': current_user.first_name, 'last_name': current_user.last_name, 'email': current_user.email})
        pform = None
        if profile is not None:
            pform = UserProfileForm(initial={'phone_number': profile.phone_number, 'profile_image': profile.profile_image})
        else:
            pform = UserProfileForm()

        # Determine this user's connection status to Facebook and whether they can safetly
        # disconnect so long as they have a usable password
        can_disconnect = True
        facebook_login = None
        try:
            facebook_login = current_user.social_auth.get(provider='facebook')
        except UserSocialAuth.DoesNotExist:
            facebook_login = None
        if facebook_login is not None and not current_user.has_usable_password():
            can_disconnect = False

        # Check current EDU status
        admin = None
        teacher = None
        student = None
        if Administrator.objects.filter(user=current_user).exists():
            admin = Administrator.objects.get(user=current_user)
        elif Educator.objects.filter(user=current_user).exists():
            teacher = Educator.objects.get(user=current_user)
        elif Student.objects.filter(user=current_user).exists():
            student = Student.objects.get(user=current_user)

        # Render the user's profile page
        content = {'current_user': current_user, 'command': u_comm, 'uform': uform, 'pform': pform, 'profile': profile, 'facebook_login': facebook_login, 'can_disconnect': can_disconnect, 'admin': admin, 'teacher': teacher, 'student': student}
        return render(request, 'user_profile2.html', content)

    # By default, redirect home if the user doesn't match
    return redirect('/')


# View to allow users to set an account password if they've disconnected a social
# media account from their INNIT account.
@login_required
def setpassword(request):
    PasswordForm = AdminPasswordChangeForm
    # if request.user.has_usable_password():
    #    PasswordForm = PasswordChangeForm
    # else:
    #    PasswordForm = AdminPasswordChangeForm

    if request.method == 'POST':
        form = PasswordForm(request.user, request.POST)
        if form.is_valid():
            form.save()
            update_session_auth_hash(request, form.user)
            return redirect("/user/{}".format(request.user.id))
        else:
            messages.error(request, 'Please correct the error below.')
    else:
        form = PasswordForm(request.user)
    return render(request, 'setpassword.html', {'form': form})


# For Google verification
def google(request):
    return render(request, 'google225ba453ee99be0e.html')


# For Facebook privacy policy information
def privacy(request):
    return render(request, 'privacy.html')


# For the "Contact" portion of the website and sending emails to us
def contact(request):

    # Process an email, if everything is right
    if request.method == 'POST':
        if 'name' in request.POST and 'email' in request.POST and 'comments' in request.POST:
            contact_email = EmailMessage(
                'New INNIT Contact Comment from {}'.format(request.POST['name']),
                request.POST['comments'],
                request.POST['email'],
                ['innitlife@gmail.com'],
            )
            contact_email.send()

    # Only go to "next" if their was a next value in the URL
    if request.method == 'POST' and 'next' in request.POST:
        return redirect(request.POST.get('next'))
    return redirect('/')


# Secret view to test sending emails to various email addresses
def secret_email(request):

    # Fetch the top-level photo, if it exists
    top_photo = None
    if SitePhoto.objects.filter(tag="vball-top-level").exists():
        top_photo = SitePhoto.objects.get(tag="vball-top-level")

    # Process an email, if everything is right
    if request.method == 'POST':
        if 'name' in request.POST and 'email' in request.POST and 'toemail' in request.POST and 'comments' in request.POST:
            contact_email = EmailMessage(
                'INNIT test email from {}'.format(request.POST['name']),
                request.POST['comments'],
                request.POST['email'],
                [request.POST['toemail']],
            )
            contact_email.send()
    return render(request, 'secret_email.html', {'top_photo': top_photo})
