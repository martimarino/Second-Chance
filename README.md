# Large Scale Project
## _SecondChance_

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white) ![Neo4J](https://img.shields.io/badge/Neo4j-008CC1?style=for-the-badge&logo=neo4j&logoColor=white) ![Vue.js](https://img.shields.io/badge/vuejs-%2335495e.svg?style=for-the-badge&logo=vuedotjs&logoColor=%234FC08D)![LaTeX](https://img.shields.io/badge/latex-%23008080.svg?style=for-the-badge&logo=latex&logoColor=white)


VintedUnipi is an e-commerce that offers the possibility of selling and buying vinted clothes.
Every user can add his or her products on the profile or buy some clothes that are selled by people in the nearby or through direct searches. 

## Features

### Admin

- Register/Login/Logout

- Delete insertions or reviews

- Suspend user

- Generate stats

- Generate codes for account balances

### User

- Register, login and logout

- Browse the feed

- Create, update or delete an insertion

- Like/unlike an insertion

- Find users/insertions/brands

- Follow/unfollow a user

- View suggested sellers and insertions

- Buy an item (order)

- Write a review after a purchase



 ### Queries
 
 #### MongoDB
 
- Search by category/brand

- View most active users (n° insertions)

- View most active sellers (n° sold items)

- View top k rated user for current user country

- View top k most interesting insertions per category

- View top k most viewed insertions per category

- View the best/worst rated reviews of a user

 - View most ordered items per category
 
 #### Neo4J
 
- Suggest new sellers based on similar interested insertions, location and category

- Suggest new insertions based on similar interested insertions and category

- View most followed users

- View who is interested in an insertion 

### Details

There is not a cart: a user can buy one item at a time clicking on the "shop" button.
The part regarding users correlation is managed with Neo4j.

A review is done a rating (1-5 stars) and a comment regarding the product and is associated to the user.
Every user has a personal wallet that can recharge inserting a code in a specific field.
When a purchase is copmuted the amount is decreased.

Every post has a "sold" field and an ID: when a user purchases an item the ID relative to the post is memorized in an array of the user collection.

Suggestions are based on different infomations like similar purchases, location, search parameters, likes and feed.

## Link

[Dataset1] [Dataset2] containing items.

[Dataset3] containing reviews.

## Actors

Actors  | Role
------------- | -------------
Normal User  | Can buy or sell
Admin | Can delete items, posts and every inappropriate content. \Can suspend a user, generate statistics and codes.

## Considerations

### Datasets fields selection
category,name,price,currency,likes_count,status,brand,codCountry,variation_0_color(color),image_url,id,SIZE,SELLER,VIEW,GENDER,

### Nations considered
Italy, Canada, Spain, Austria, Germany, France,Brazil, Netherlands,Poland,Ireland,United Kingdom

## MongoDB

### Collections

Collection  |  What inside
------------- | -------------
User | personal information, orders, reviews, suspended (bool)
Admin | codes
Insertion | title, details, #interested, #views

#### USER

Field | Type
------------- | -------------
ADDRESS | String
BALANCE | Double
CITY | String
COUNTRY | String
EMAIL | String
NAME | String
PASSWORD | String
RATING | Double
SUSPENDED | String
USERNAME | String

#### INSERTION

Field | Values | Type
------------- | ------------- | -------------
ID | 8552148 | Varchar
CATEGORY | {clothing,accessories, bags, beauty, house, jewelry, kids, shoes} | String
DESCRIPTION | text | String
GENDER | {M, F, U} | String
PRICE | 10,56 | Double
CURRENCY | {EUR, USD} | String
INTERESTED| 10 | Integer
VIEWS | 10 | Integer
STATUS | {new, excellent, good, used, very used} | String
COLOR | {red, orange, yellow} | String
SIZE | {XS, S, M, L, XL} | String
BRAND | {Micheal Kors} | String
COUNTRY | {Italy, Canada, Spain, Austria, Germany, France, Brazil, Netherlands, Poland, Ireland, United Kingdom (Great Britain)} | String
IMAGE_URL | http://www.something.com | String
TIMESTAMP | 2020-02-07 05:11:36 +0000 | String
SELLER | username | String

####  REVIEW

Field | Values | Type
------------- | ------------- | -------------
ID | 8552148 | Varchar
TEXT | text | String
TIMESTAMP | 2020-02-07 05:11:36 +0000 | String
TITLE | Fantastic! | String
SELLLER | username | String
REVIEWER | username | String
RATING | 3,5 | Float

####  ORDER

Field | Values | Type
------------- | ------------- | -------------
ID | 8552148 | Varchar
TIMESTAMP | 2020-02-07 05:11:36 +0000 | String
IMAGE | url image | String
BUYER | username | String
SELLER | username | String
PRICE| 10,56 | Float


## Neo4j

### Vertices

- User

- Insertion

### Edges

- User - User: follow

- User - Insertion: view, intereste, published

## License

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [Dataset1]: <https://www.kaggle.com/agrigorev/clothing-dataset-full>
      
   [Dataset2]: <https://data.world/jfreex/products-catalog-from-newchiccom>
   
   [Dataset3]: <https://www.kaggle.com/asmaoueslati/womensclothingecommerce>

   
 
