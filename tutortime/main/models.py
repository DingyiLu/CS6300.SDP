import os
import sys
from io import BytesIO
from PIL import Image, ExifTags
from django import forms
from django.db import models
from django.forms import ModelForm
from django.contrib.auth.models import User
from django.core.files.uploadedfile import InMemoryUploadedFile


# Utility function to use to rescale a Model's ImageField to a desired max width.
# Also performs a rotation of the image based on EXIF data, if necessary.
# Note that this function is meant to be called from within a Mode's save() function.
# The argument is an model ImageField and a max width (Integer), in pixels.
# The function returns a new ImageField that the calling Model should use.
def rotate_and_rescale_image_field(image_field, max_width):

    # Open the currently uploaded image.
    im = Image.open(image_field)
    rotated = False
    resized = False

    # Make an attempt at modifying the image
    try:
        # First, perform the rotation, if needed
        for orientation in ExifTags.TAGS.keys():
            if ExifTags.TAGS[orientation] == 'Orientation':
                break
        exif = dict(im._getexif().items())
        if exif[orientation] == 3:
            im = im.rotate(180, expand=True)
            rotated = True
        elif exif[orientation] == 6:
            im = im.rotate(270, expand=True)
            rotated = True
        elif exif[orientation] == 8:
            im = im.rotate(90, expand=True)
            rotated = True
    except:
        print("Image Rotate Exception!\n")

    # Next, resize the image, if needed
    w = im.width
    h = im.height
    print("Processing image with current width {} and height {}\n".format(w, h))
    if w > max_width:
        # Resize a new image
        new_h = int((max_width * 1.0 / w) * h)
        print("New width will be {} and new height will be {}\n".format(max_width, new_h))
        im = im.resize((max_width, new_h), Image.ANTIALIAS)
        resized = True

    # Save a new version of the image if it was rotated or resized
    if rotated or resized:
        output = BytesIO()
        im.save(output, format='JPEG', quality=90)  # 90% to avoid image "bloat"
        output.seek(0)
        # Return this new ImageField with the intention of replacing the old one
        return InMemoryUploadedFile(output, 'ImageField', "%s.jpg" % image_field.name.split('.')[0], 'image/jpeg', sys.getsizeof(output), None)

    # If no modification was necessary or there was an error, return the original image field
    return image_field


# Innit member user profile images will be stored in:  /media/user_photos/<user ID>/somefile.jpg
def get_user_profile_image_path(instance, filename):
    return os.path.join('user_photos', str(instance.user.id), filename)


# Carousel images to be displayed on the main page will be uploaded to: main/static/carousel_images/<index>/ for each <index> (1, 2, 3, ..., etc.)
def get_carousel_image_path(instance, filename):
    return os.path.join('main/static/carousel_images', str(instance.index), filename)


# Innit event images will be uploaded to main/static/event_images/<event date>/
def get_event_image_path(instance, filename):
    return os.path.join('main/static/event_images', str(instance.date), filename)


# Innit Misc. Site Photos will be uploaded to main/static/site_photos/<id>/
def get_site_photo_image_path(instance, filename):
    return os.path.join('main/static/site_photos', str(instance.id), filename)


# The following information helps represent an INNIT user.
# This model serves as an extension of the normal User class to add other features.
class UserProfile(models.Model):
    MAX_PHOTO_WIDTH = 250   # Maximum profile photo width, in pixels
    user            = models.ForeignKey(User, on_delete=models.CASCADE)
    phone_number    = models.CharField("Phone Number", max_length=30)
    profile_image   = models.ImageField("Profile Photo", upload_to=get_user_profile_image_path, blank=True, null=True)
    asked_fb        = models.BooleanField("Asked about Facebook?", default=False)

    # Here we override the model's save() function so that we can resize incoming images
    # to a maximum width in pixels before they are saved to disk
    def save(self, *args, **kwargs):
        # Only if the profile_image is present
        if self.profile_image:
            self.profile_image = rotate_and_rescale_image_field(self.profile_image, self.MAX_PHOTO_WIDTH)
        super(UserProfile, self).save(*args, **kwargs)

    def __str__(self):
        return "Member: {} {} ({}) ".format(self.user.first_name, self.user.last_name, self.user.username)

    def __repr__(self):
        return self.__str__()


# The following two forms are used together when editing a user. This includes the additional
# fields needed to complete a "user profile"
class UserForm(ModelForm):
    class Meta:
        model = User
        fields = ["first_name", "last_name", "email"]

    def __init__(self, *args, **kwargs):
        super(UserForm, self).__init__(*args, **kwargs)
        self.fields['first_name'].required = True
        self.fields['last_name'].required = True
        self.fields['email'].required = True


class UserProfileForm(ModelForm):
    class Meta:
        model = UserProfile
        fields = ["phone_number", "profile_image"]

    def __init__(self, *args, **kwargs):
        super(UserProfileForm, self).__init__(*args, **kwargs)
        self.fields['phone_number'].required = False
        self.fields['profile_image'].required = False


# Simple form to confirm or deny a link to another user account for Facebook
class MergeForm(forms.Form):
    merge = forms.BooleanField(label='Link Account?')

    def __init__(self, *args, **kwargs):
        super(MergeForm, self).__init__(*args, **kwargs)
        self.fields['merge'].required = False


class BlogCategory(models.Model):
    title = models.CharField("Category Title", max_length=100, db_index=True)
    slug  = models.SlugField(max_length=100, db_index=True)

    def __unicode__(self):
        return '%s' % self.title

    def get_absolute_url(self):
        return ('view_blog_category', None, {'slug': self.slug})

    def __str__(self):
        return "Blog Category: {}".format(self.title)

    def __repr__(self):
        return self.__str__()


# This table will store blog entries created by (admin) authors for the main Innit page
class BlogEntry(models.Model):
    title    = models.CharField("Blog Title", max_length=100, unique=True)
    author   = models.ForeignKey(User, on_delete=models.CASCADE)
    slug     = models.SlugField(max_length=100, unique=True)
    body     = models.TextField("Blog Body")
    posted   = models.DateTimeField("Date-time Posted", db_index=True, auto_now_add=True)
    category = models.ForeignKey(BlogCategory, related_name='Blog_Category', on_delete=models.CASCADE)

    def __unicode__(self):
        return '%s' % self.title

    def get_absolute_url(self):
        return ('view_blog_post', None, {'slug': self.slug})

    def __str__(self):
        return "{} (Author: {}, Date: {})".format(self.title, self.author, self.posted)

    def __repr__(self):
        return self.__str__()


# This table will store images that are to be presented on the main Innit page in the carousel widget.
# Programatically, the website's logic will extract the appropriate image from the model DB
# to display on the main page of the website. These images will be stored in "main_images/<image ID>/filename"
class CarouselImages(models.Model):
    index = models.IntegerField("Carousel Index", unique=True)
    image = models.ImageField("Image", upload_to=get_carousel_image_path, blank=True, null=True)


# This table will store INNIT event entries.
# Note that each event may have a image (or photo) uploaded and displayed for it
class Event(models.Model):
    name        = models.CharField("Event Name", max_length=100)
    date        = models.DateField("Event Date")
    address     = models.CharField("Event Address", max_length=100)
    attendees   = models.ManyToManyField(User, blank=True, null=True)
    description = models.CharField("Description", max_length=2000)
    cost        = models.CharField("Cost", max_length=30)
    image       = models.ImageField("Event Image", upload_to=get_event_image_path, blank=True, null=True)

    def __str__(self):
        return self.name

    def __repr__(self):
        return self.__str__()


# This table will store miscellaneous content photos that can be used on the site in various places.
# The intention is to use an entry's "tag" field to figure out it's purpose. Other areas of the site
# can reference this field and rest assured that only one record can exist per tag. To change the photo,
# just update the existing record.
class SitePhoto(models.Model):

    # Add entries to this list and use throughout the site, as needed
    TAGS = (
           ('vball-top-level', 'Volleyball - Top Level Page Header'),
           ('vball-top-level2', 'Volleyball - Top Level Page Header 2nd'),
           ('vball-top-level3', 'Volleyball - Top Level Page Header 3rd'),
           ('vball-top-level4', 'Volleyball - Top Level Page Header 4th'),
           ('vball-top-level5', 'Volleyball - Top Level Page Header 5th'),
           ('pball-top-level', 'Pickleball - Top Level Page Header'),
           ('pball-top-level2', 'Pickleball - Top Level Page Header 2nd'),
           ('pball-top-level3', 'Pickleball - Top Level Page Header 3rd'),
           ('pball-top-level4', 'Pickleball - Top Level Page Header 4th'),
           ('pball-top-level5', 'Pickleball - Top Level Page Header 5th'),
           ('sports-top-level', 'Sports - Top Level Page Header'),
    )
    tag         = models.CharField("Photo Tag", max_length=100, choices=TAGS, null=True, blank=True, unique=True)  # Only one image of each tag type
    description = models.CharField("Photo Description", max_length=300, null=True, blank=True)  # More informative description
    image       = models.ImageField("Image File", upload_to=get_site_photo_image_path, blank=True, null=True)
    max_width   = models.IntegerField(default=1000)   # Maximum pixel width of this image

    # Here we override the model's save() function so that we can resize incoming images
    # to a maximum width in pixels before they are saved to disk
    def save(self, *args, **kwargs):
        # Only if the champion_photo is present
        if self.image:
            self.image = rotate_and_rescale_image_field(self.image, self.max_width)
        super(SitePhoto, self).save(*args, **kwargs)

    def __str__(self):
        return 'ID: {}, Tag: {}, Description: {}'.format(self.id, self.tag, self.description)

    def __repr__(self):
        return self.__str__()
