"""intest URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/1.11/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  url(r'^$', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  url(r'^$', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.conf.urls import url, include
    2. Add a URL to urlpatterns:  url(r'^blog/', include('blog.urls'))
"""
# from django.conf import settings
from django.conf.urls import include, url
from django.contrib import admin
from django.contrib.auth import views as auth_views
from main import views as main_views

urlpatterns = [
    # User login/logout
    url(r'^login/$', auth_views.LoginView.as_view(template_name='login3.html'), name='login'),
    url(r'^logout/$', auth_views.LogoutView.as_view(next_page='/'), name='logout'),

    # Custom user sign-up and change password views
    url(r'^signup/$', main_views.signup2, name='signup'),
    url(r'^changepassword/$', main_views.changepassword, name='changepassword'),

    # User profile view
    url(r'^user/(.+)', main_views.user_profile, name='user_profile'),

    # Secret email testing area
    url(r'^email$', main_views.secret_email, name='secret_email'),

    # Social-media linked logins and set-password capability
    url(r'^oauth/', include('social_django.urls', namespace='social')),
    url(r'^setpassword/$', main_views.setpassword, name='setpassword'),
    url(r'^privacy', main_views.privacy, name='privacy'),  # For Facebook Privacy Policy info
    url(r'^checkfb/', main_views.checkfb, name='checkfb'),
    url(r'^mergefb/([0-9]+)/([0-9]+)', main_views.mergefb, name='mergefb'),

    # Everything else
    url(r'^admin/', admin.site.urls),
    url(r'^auth/', include('main.urls')),
    url(r'^edu/', include('edu.urls')),
    url(r'^contact/$', main_views.contact, name='contact'),
    url(r'^google', main_views.google, name='google'),  # For Google search results
    url(r'^$', include('main.urls')),
]
