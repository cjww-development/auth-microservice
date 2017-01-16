[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/cjww-development/auth-microservice.svg?branch=master)](https://travis-ci.org/cjww-development/auth-microservice)

auth-microservice
=================

Scala Play! auth microservice for authenticating users

How to run
==========

```````````````
sbt run
```````````````

This will start the application on port **8601**

| Path                                                                               | Supported Methods | Description  |
| ---------------------------------------------------------------------------------- | ------------------| ------------ |
|```/auth/create-new-user```                                                |       POST        | creates a new user account |

###POST &nbsp;&nbsp;&nbsp;&nbsp; /auth/create-new-user

    Responds with:

| Status        |Code                   |
|:--------------|-----------------------|
| 201           | Created               |
| 400           | Bad request           |
| 403           | Forbidden             |
| 500           | Internal server error |