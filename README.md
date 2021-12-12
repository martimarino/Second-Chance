# Large Scale Project
## _VintedUnipi_

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white) ![Neo4J](https://img.shields.io/badge/Neo4j-008CC1?style=for-the-badge&logo=neo4j&logoColor=white)

VintedUnipi is an e-commerce that offers the possibility of selling and buying vinted clothes.
Every user can add his or her products on the profile or buy some clothes that are selled by people in the nearby or through direct searches. 

## Features

### Admin

- Register/Login/Logout

- Delete item

- Delete post

- Delete review

- Delete user

- Stats

- Generate codes for account balances

### User

- Register/Login/Logout

- Follow/Unfollow item

- Follow/Unfollow post

- Find and show item

- Like post

- Create/Find post

- Change post details

 ### Queries

- View most liked items

- A user can make a filtered search by location or category

- View user rating

- View local users with rating

- View most rated users

- View most followed users

### Details

There is not a cart but the user can buy one item at a time clicking on the "shop" button.
The part regarding users correlation is managed with Neo4j.
A review is done referred to the user with a rating (1-5 stars) and a comment regarding the product.


## Link

[Dataset1] containing clothes.

[Dataset2] containing reviews.

## Actors

Actors  | Role
------------- | -------------
Normal User  | Can buy or sell
Admin | Can delete items, posts and every inappropriate content

## License

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [Dataset1]: <https://www.kaggle.com/agrigorev/clothing-dataset-full>
   
   [Dataset2]: <https://www.kaggle.com/asmaoueslati/womensclothingecommerce>
   
 
