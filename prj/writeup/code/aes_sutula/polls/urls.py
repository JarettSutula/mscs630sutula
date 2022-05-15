from django.urls import path

from .views import homePageView, loginFormView, addCredentialsView, showCredentials, createUserView

urlpatterns = [
    path('', homePageView, name='home'),
    path('login/', loginFormView, name='loginform'),
    path('createuser', createUserView, name='createuser'),
    path('addcredentials/', addCredentialsView, name='addcredentials'),
    path('showcreds', showCredentials, name='showcreds')
]