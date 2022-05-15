import os, sys

from django.forms.fields import CharField
from django.forms.widgets import PasswordInput
from dns.rdataclass import CH
currentdir = os.path.dirname(os.path.realpath(__file__))
parentdir = os.path.dirname(currentdir)
sys.path.append(parentdir)

from django.core.exceptions import ValidationError
from django.db import models
from django import forms
from .utils import start_db, collection_link

import bcrypt

class UserForm(forms.Form):
    """Contains fields for profile creation."""
    username = forms.CharField(max_length=100, label='User Name')
    password = forms.CharField(widget=forms.PasswordInput)
    email = forms.EmailField(required=False, label='Your Email Address')

class LogInForm(forms.Form):
    """Login form with username and password fields."""
    username = forms.CharField(max_length=100, label='User Name')
    password = forms.CharField(widget=forms.PasswordInput)

    def clean_password(self):
        """Validates that the username and password exist in database."""
        username = self.cleaned_data['username']
        password = self.cleaned_data['password']

        db = start_db()
        logins = collection_link(db, 'logins')
    
        # ensure that username and password are valid in database.
        user = logins.find_one({'username':username})
        # if user is not found, it returns None - invalid username.
        if user == None:
            raise ValidationError('There is no account associated with this username.')
        
        # if there is an account with that username, check the password.
        byte_password = password.encode('UTF-8')
        correct_password = bcrypt.checkpw(byte_password, user['password'])
        
        if not correct_password:
            raise ValidationError('Incorrect username or password, please try again.')

class CredentialForm(forms.Form):
    """Add a new credential to a user's account."""
    website = forms.CharField(max_length=100, label="Website")
    website_username = forms.CharField(max_length=100, label="Website Username")
    website_password = forms.CharField(max_length=100, label="Website Password")
    account_username = forms.CharField(widget=forms.TextInput(attrs={'readonly':'readonly'}), label="JSPM Username")
    account_password = forms.CharField(widget=forms.PasswordInput, label="JSPM Password")

    def clean_account_password(self):
        """Validates that the username and password exist in database."""
        username = self.cleaned_data['account_username']
        password = self.cleaned_data['account_password']

        db = start_db()
        logins = collection_link(db, 'logins')
    
        # ensure that username and password are valid in database.
        user = logins.find_one({'username':username})
        # if user is not found, it returns None - invalid username.
        if user == None:
            raise ValidationError('There is no account associated with this username.')
        
        # if there is an account with that username, check the password.
        byte_password = password.encode('UTF-8')
        correct_password = bcrypt.checkpw(byte_password, user['password'])
        
        if not correct_password:
            raise ValidationError('Incorrect username or password, please try again.')
        
        return password

class ShowCredentialForm(forms.Form):
    """Shows credentials on a user's account."""
    username = forms.CharField(widget=forms.TextInput(attrs={'readonly':'readonly'}), label="Username")
    password = forms.CharField(widget=forms.PasswordInput, label="Password")

    def clean_password(self):
        """Validates that the username and password exist in database."""
        username = self.cleaned_data['username']
        password = self.cleaned_data['password']

        db = start_db()
        logins = collection_link(db, 'logins')
    
        # ensure that username and password are valid in database.
        user = logins.find_one({'username':username})
        # if user is not found, it returns None - invalid username.
        if user == None:
            raise ValidationError('There is no account associated with this username.')
        
        # if there is an account with that username, check the password.
        byte_password = password.encode('UTF-8')
        correct_password = bcrypt.checkpw(byte_password, user['password'])
        
        if not correct_password:
            raise ValidationError('Incorrect username or password, please try again.')
        
        return password