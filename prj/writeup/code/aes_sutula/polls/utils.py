import os
import pymongo
from dotenv import load_dotenv
import certifi
import re

def start_db():
    """This starts the connection to the mongo server.
    If a file wants to access the 'users' collection, call 
    my_db = start_db(), then call collection_link(my_db, 'users').
    """

    # load the .env file in local directories for DB access.
    load_dotenv()
    DB_USERNAME = os.getenv('DB_USERNAME')
    DB_PASSWORD = os.getenv('DB_PASSWORD')

    connection_string = "mongodb+srv://"+DB_USERNAME+":"+DB_PASSWORD+"@gb-mentoring-cluster.jhwgr.mongodb.net/?retryWrites=true&w=majority"

    client = pymongo.MongoClient(connection_string, tlsCAfile = certifi.where())
    db_handle = client.get_database('jspm')
    
    return db_handle

def collection_link(db_handle, collection_name):
    """Connect to a specific collection given a specified database and
    collection. Reduces need to call start_db() multiple times for
    multiple collections.
    """
    db = db_handle
    return db.get_collection(collection_name)