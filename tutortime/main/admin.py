from django.contrib import admin

# Register your models here.
from .models import UserProfile, BlogCategory, BlogEntry, CarouselImages, Event, SitePhoto

admin.site.register(UserProfile)
admin.site.register(BlogCategory)
admin.site.register(BlogEntry)
admin.site.register(CarouselImages)
admin.site.register(Event)
admin.site.register(SitePhoto)
