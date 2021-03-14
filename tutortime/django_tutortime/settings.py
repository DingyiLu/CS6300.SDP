"""
For more information on this file, see
https://docs.djangoproject.com/en/1.11/topics/settings/

For the full list of settings and their values, see
https://docs.djangoproject.com/en/1.11/ref/settings/
"""

import os
import socket

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.11/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '!j9#d(3kv8w=*2jn$jcfqmt!8@y88$eom%f^polw3=oyyz11w7'

# Run in "DEBUG" mode for additional logging
DEBUG = True

# Allow hosting from the following hostnames
ALLOWED_HOSTS = ['ec2-54-191-201-92.us-west-2.compute.amazonaws.com', 'localhost', '127.0.0.1']

# Application definition
INSTALLED_APPS = [
    'django.contrib.admin',
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'main',
    'bootstrap3',
    'edu',
    'paypal.standard.ipn',
    'social_django',
    'channels'
]

MIDDLEWARE = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
    # 'social_django.middleware.SocialAuthExceptionMiddleware',
]

ROOT_URLCONF = 'django_tutortime.urls'

TEMPLATES = [
    {
        'BACKEND': 'django.template.backends.django.DjangoTemplates',
        'DIRS': [(os.path.join(BASE_DIR, 'templates')), ],
        'APP_DIRS': True,
        'OPTIONS': {
            'context_processors': [
                'django.template.context_processors.debug',
                'django.template.context_processors.request',
                'django.contrib.auth.context_processors.auth',
                'django.contrib.messages.context_processors.messages',
                'social_django.context_processors.backends',
                'social_django.context_processors.login_redirect',
            ],
        },
    },
]

# Point to the correct WSGI endpoint
WSGI_APPLICATION = 'django_tutortime.wsgi.application'

# Set this for channel routing
# Note that this assumes that Redis is available on local host to
# serve the channel communication layer on port 6379. One could be
# started using docker with:
#   docker run -p 6379:6379 -d redis:2.8
ASGI_APPLICATION = "django_tutortime.routing.application"
CHANNEL_LAYERS = {
    'default': {
        'BACKEND': 'channels_redis.core.RedisChannelLayer',
        'CONFIG': {
            "hosts": [('127.0.0.1', 6379)],
        },
    },
}


# Database
# https://docs.djangoproject.com/en/1.11/ref/settings/#databases


if DEBUG:
    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.sqlite3',
            'NAME': os.path.join(BASE_DIR, 'db.sqlite3'),
        }
    }
else:
    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.mysql',
            'NAME': 'django',
            'USER': 'django',
            'PASSWORD': 'ChJ456>s4J9;zF=d',
            'HOST': 'localhost',
            'PORT': '3306'
        }
    }


# Password validation
# https://docs.djangoproject.com/en/1.11/ref/settings/#auth-password-validators

AUTH_PASSWORD_VALIDATORS = [
    {
        'NAME': 'django.contrib.auth.password_validation.UserAttributeSimilarityValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.MinimumLengthValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.CommonPasswordValidator',
    },
    {
        'NAME': 'django.contrib.auth.password_validation.NumericPasswordValidator',
    },
]


# Internationalization
# https://docs.djangoproject.com/en/1.11/topics/i18n/

LANGUAGE_CODE = 'en-us'

TIME_ZONE = 'UTC'

USE_I18N = True

USE_L10N = True

USE_TZ = True


# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.11/howto/static-files/

# Static content
STATIC_URL = '/static/'
STATIC_ROOT = os.path.join(BASE_DIR, 'static')

# Uploaded content (from the admin panel, etc.) will go here.
# Note, you will need to have an "Alias /media/ /actual/path/to/innit/media/"
# directive in the Apache2 conf file in order for these files to be served
# by the web server correctly.
MEDIA_URL = '/media/'
MEDIA_ROOT = os.path.join(BASE_DIR, 'media')


LOGIN_URL = '/login/'
LOGIN_REDIRECT_URL = '/'


# The following is for connecting to our email server
EMAIL_BACKEND       = 'django.core.mail.backends.smtp.EmailBackend'
EMAIL_HOST          = 'localhost'
EMAIL_PORT          = 25
EMAIL_HOST_USER     = ''
EMAIL_HOST_PASSWORD = ''
EMAIL_USE_TLS       = True
DEFAULT_FROM_EMAIL  = 'INNIT <admin@innitlife.com>'
