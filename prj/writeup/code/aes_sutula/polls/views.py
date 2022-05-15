from pydoc import plain
from django.shortcuts import render
from django.http import HttpResponse, HttpResponseRedirect
from .utils import collection_link, start_db
from .models import LogInForm, UserForm, CredentialForm, ShowCredentialForm
import bcrypt
from .aes import *

# initialize database and collections
db_handle = start_db()
users = collection_link(db_handle, 'users')
logins = collection_link(db_handle, 'logins')

# Create your views here.
def homePageView(request):
    """View of the home page."""
    return render(request, 'home.html')

def loginFormView(request):
    """View of the login page."""
    return render(request, 'login.html')

def addCredentialsView(request):
    """Adds a new set of credentials to the user object."""
    form = CredentialForm()
    submitted = False

    if 'username' in request.session and request.method == 'POST':
        form = CredentialForm(request.POST)
        if form.is_valid():
            submitted = True
            # Base form fields
            website = form.cleaned_data.get("website")
            website_username = form.cleaned_data.get("website_username")
            website_password = form.cleaned_data.get("website_password")
            account_username = form.cleaned_data.get("account_username")
            account_password = form.cleaned_data.get("account_password")

            # get the profile with already present credentials
            db = start_db()
            users = collection_link(db, 'users')
            current_profile = users.find_one({'username': request.session['username']})
            new_credentials = current_profile['credentials']

            # encrypt the credentials using AES.
            # first, get the key from the user's password.
            key_hex = plaintext_to_hex(account_password)
            # get all three plaintexts of credentials to encrypt
            website_pt = plaintext_to_hex(website)
            website_username_pt = plaintext_to_hex(website_username)
            website_password_pt = plaintext_to_hex(website_password)
            
            encrypted_website = aes_encryption(website_pt, key_hex)
            encrypted_website_username = aes_encryption(website_username_pt, key_hex)
            encrypted_website_password = aes_encryption(website_password_pt, key_hex)

            # append new credentials from the form.
            new_cred = {'website': encrypted_website,
                    'website_username': encrypted_website_username,
                    'website_password': encrypted_website_password
                    }

            new_credentials.append(new_cred)

            # update the user with the newly appended credentials list
            users.update_one({'username': request.session['username']},
                                {'$set': {'credentials': new_credentials}})

            submitted = True

    elif 'username' in request.session:
        form = CredentialForm(initial={'account_username': request.session['username']})

    else:
        form = CredentialForm()

    return render(request, 'addcreds.html', {'form': form, 'submitted': submitted})

def showCredentials(request):
    """View of decrypted credentials page."""
    form = ShowCredentialForm()
    submitted = False
    creds = {}

    if 'username' in request.session and request.method == 'POST':
        form = ShowCredentialForm(request.POST)
        if form.is_valid():
            submitted = True
            # Base form fields
            username = form.cleaned_data.get("username")
            password = form.cleaned_data.get("password")

            # get the profile with already present credentials
            db = start_db()
            users = collection_link(db, 'users')
            current_profile = users.find_one({'username': request.session['username']})
            credentials = current_profile['credentials']

            # decrypt the credentials using AES.
            # first, get the key from the user's password.
            key_hex = plaintext_to_hex(password)
            # for every element in credentials, decrypt using key.
            for credential in credentials:
                website_ct = aes_decryption(credential['website'], key_hex)
                website_username_ct = aes_decryption(credential['website_username'], key_hex)
                website_password_ct = aes_decryption(credential['website_password'], key_hex)
                # now decrypted, we can place them back in 'credential'.
                credential['website'] = hex_to_plaintext(website_ct)
                credential['website_username'] = hex_to_plaintext(website_username_ct)
                credential['website_password'] = hex_to_plaintext(website_password_ct)

            # update creds to display on the page using the decrypted credentials
            creds = credentials

            submitted = True

    elif 'username' in request.session:
        form = ShowCredentialForm(initial={'username': request.session['username']})

    else:
        form = ShowCredentialForm()

    return render(request, 'showcreds.html', {'form': form, 'submitted': submitted, 'creds': creds})

def createUserView(request):
    """Validates user creation form and returns appropriate response.
    If the form is valid, insert inputs into database and return
    a HTTPResponseRedirect. If not, return the previously filled
    form values and alert user of validation errors.
    """
    submitted = False
    if request.method == 'POST':
        form = UserForm(request.POST)
        if form.is_valid():
            # Base form fields
            username = form.cleaned_data.get("username")
            password = form.cleaned_data.get("password")
            email = form.cleaned_data.get("email")

            # Object to be passed into users
            user_context= { 'username': username,
                       'email':email,
                       'credentials': []
                      }

            # Make sure the user's username/password is stored safely in logins.
            # Hash it in binary string first!
            byte_password = password.encode('UTF-8')
            hashed_password = bcrypt.hashpw(byte_password, bcrypt.gensalt())

            login_context = { 'username': username,
                          'password': hashed_password
                        }

            users.insert_one(user_context)
            logins.insert_one(login_context)

            return HttpResponseRedirect('/createuser?submitted=True')
    else:
        form = UserForm()
        if 'submitted' in request.GET:
            submitted = True

    return render(request, 'form.html', {'form': form, 'submitted': submitted})

def loginFormView(request):
    """Provides login form for new users.
    Creates a session if the login information is correct.
    """
    form = LogInForm()
    if request.method == 'POST':
        form = LogInForm(request.POST)
        if form.is_valid():
            cd = form.cleaned_data
            username = form.cleaned_data.get("username")
            # if cleaners pass, that means the login should be successful.
            # create session with username for use across web server.
            request.session['username'] = username
            # set session expiration time for 10 minutes.
            request.session.set_expiry(600)
            # redirect user home.
            print(request.session['username'])
            return HttpResponseRedirect('/')

    elif request.method == 'GET':
        # see if the session has a username.
        if 'username' in request.session:
            # if we are back here while we are logged in, delete the session.
            request.session.flush()
            # if we want to go back to home instead of log in page after, uncomment this.
            return HttpResponseRedirect('/')

    else:
        form = LogInForm()

    return render(request, 'login.html', {'form': form})

