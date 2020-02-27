# book-catalog-api project

This API has been built with the Quarkus framework, after taking a look at some other like Spring with the objective of speeding up the initial
development and environment set up. Reasons for choosing Quarkus over Spring wre:
- Easy to configure and create a project
- Lots of integration with reactive style programming tools
- Faster start up time and smaller than Spring
- Thought with containerization in mind, so it's easy to puto in a container and deploy in cloud

The chosen Database was a MongoDB in the cloud, creating a free account and a cluster in MongoAtlas. This way there is no need to install and run an instance of MongoDB locally to test the application. 

The API exposes 4 endpoints to create, update, retrieve and delete (CRUD) a catalog of books. These 4 endpoints are REST, using a reactivo mongo client and Java reactive streams to achieve a full non blocking API.

##REST endpoints
#####POST /catalog/books
Sample request body:
````
{
    "isbn": "book4",
    "name": "libro de prueba editado",
    "genre": "ROMANCE",
    "author": "Aristoteles",
    "description": "Libro bastante aburrido de filosofia",
    "language": "Spanish",
    "numPages": 8544
}
````
Responds with an status of 201 and the entity created (book). In case an exception occurs in the backend, responds with a status of 500.


#####GET /catalog/books
Retrieves the list of all books in the Database.

Sample response body:
````
[
  {
    "isbn": "1234",
    "name": "Los pilares de la tierra",
    "genre": "ADVENTURE",
    "author": "Ken Follet",
    "description": "Great book",
    "language": "English",
    "numPages": 1120
  },
  {
    "isbn": "2121",
    "name": "20 sombras de Grey",
    "genre": "HUMOR",
    "author": "Dont remember",
    "description": "Este libro es malisimo pero te ries",
    "language": "Spanish",
    "numPages": 500
  }
]
````
Responds with an status of 200 and the list of books. In case an exception occurs in the backend, responds with a status of 500.

#####GET /catalog/books/{isbn}
Retrieves a book with the ISBN especified. 
Sample response body:
````
{
    "isbn": "book4",
    "name": "libro de prueba editado",
    "genre": "ROMANCE",
    "author": "Aristoteles",
    "description": "Libro bastante aburrido de filosofia",
    "language": "Spanish",
    "numPages": 8544
}
````
Responds with an status of 200 and the book. If no book is found, responds with a 204 (no content),
In case an exception occurs in the backend, responds with a status of 500.

#####PUT /catalog/books/
Updates the books passed in the request body with specified fields.
Sample response request:
````
{
    "isbn": "book4",
    "name": "libro de prueba editado",
    "genre": "ROMANCE",
    "author": "Aristoteles",
    "description": "Libro bastante aburrido de filosofia",
    "language": "Spanish",
    "numPages": 8544
}
````
Responds with an status of 200 and a boolean depending on the book having been updated or not. For that it uses the "acknowledged" feature of mongoDB.
 
In case an exception occurs in the backend, responds with a status of 500.

#####DELETE /catalog/books/{isbn}
Deletes a book with the ISBN specified. 
Sample response body:
````
{
    "isbn": "book4",
    "name": "libro de prueba editado",
    "genre": "ROMANCE",
    "author": "Aristoteles",
    "description": "Libro bastante aburrido de filosofia",
    "language": "Spanish",
    "numPages": 8544
}
````
Responds with an status of 200 and a boolean depending on the book having been deleted or not. For that it uses the "acknowledged" feature of mongoDB.
 
In case an exception occurs in the backend, responds with a status of 500.


Apart of book ones, the API exposes an endpoint for checking if a user is registered. This is used by the front end for checking a user log in. It is very simple and with no security at all, only for the sake of being able to log in in the app. Normally, the password would be hashed before sending it to the endpoint, and a token will be returned for a valid user, but we assume that is out of scope for this exercise.

####POST /users
Sample request body
```
{
    "username": "username",
    "password": "password"
{
````
It responds with a boolean with the log in status. If an exception is found, it responds with a false status.

##Websocket endpoint
####/notifications
Apart from the REST endpoints, the server defines a websocket for connecting to the clients and broadcast notifications. 
The web clients will subscribe to this websocket and will be notified in real time about any changes done in the book catalog.

