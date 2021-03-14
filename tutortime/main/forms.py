from django import forms as _forms
from django.contrib.auth.models import User
from django.contrib.auth.forms import UserCreationForm, PasswordChangeForm

from django.core.exceptions import ValidationError


class RegisterForm(_forms.Form):
    username = _forms.CharField(max_length=30)
    password = _forms.CharField(max_length=30, widget=_forms.PasswordInput)
    password1 = _forms.CharField(max_length=30, widget=_forms.PasswordInput, label='Password Again')

    def clean(self):

        if (self.cleaned_data.get('password')) != (self.cleaned_data.get('password1')):
            print(self.cleaned_data)
            raise ValidationError("Passwords Dont Match!!")
        if self.cleaned_data.get('username') in [x.username for x in User.objects.all()]:
            raise ValidationError("Username is in Use! Pick Another!")
        return self.cleaned_data


class LoginForm(_forms.Form):
    username = _forms.CharField(max_length=30)
    password = _forms.CharField(max_length=30, widget=_forms.PasswordInput)


class SignUpForm(UserCreationForm):
    first_name = _forms.CharField(max_length=30, required=True)
    last_name  = _forms.CharField(max_length=30, required=True)
    email      = _forms.EmailField(max_length=254, required=True)

    class Meta:
        model  = User
        fields = ('username', 'first_name', 'last_name', 'email', 'password1', 'password2', )

    def clean_email(self):
        email = self.cleaned_data.get('email')
        username  = self.cleaned_data.get('username')
        if email and User.objects.filter(email=email).exclude(username=username).exists():
            raise _forms.ValidationError(u'Email address already registered with another user.')
        return email


class ChangePasswordForm(PasswordChangeForm):
    username = _forms.CharField(max_length=100, required=True)

    class Meta:
        model  = User
        fields = ('username', 'old_password', 'new_password1', 'new_password2')
