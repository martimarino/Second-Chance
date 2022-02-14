# Large Scale Project
## _SecondChance_

![IntelliJ IDEA](https://img.shields.io/badge/IntelliJIDEA-000000.svg?style=for-the-badge&logo=intellij-idea&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) ![MongoDB](https://img.shields.io/badge/MongoDB-%234ea94b.svg?style=for-the-badge&logo=mongodb&logoColor=white) ![Neo4J](https://img.shields.io/badge/Neo4j-008CC1?style=for-the-badge&logo=neo4j&logoColor=white)


Second Chance is an e-commerce that offers the possibility of selling and buying vinted clothes.
Every user can add his or her products on the profile or buy some items that are selled by people in the nearby or through direct searches. 

## Features

### Admin

*	Visualize statistics (specifying a k parameter)
    * View top k rated user per country
    * View top k interesting insertions per category
    * View top k viewed insertions per category
    * View top k users with more purchased items
    * View top k users with more sold items
    * Number of likes per category
*	Suspend a user
*	Delete an insertion


### User

*	Sign in in the application
*	Visualize home
    * Visualize viral insertions
    * Visualize feed
    * Visualize his/her profile
    * Visualize personal information
    * Browse his/her insertions
    * Visualize insertions he/she is interested in
    * Browse his/her orders
    * Visualize followers and following users
    * Add credit to the personal balance account
*	Create a new insertion
*	Delete an insertion of your own
*	Search insertions by seller, brand or using filters
*	Visualize an insertion
    * Like an insertion
    * Buy relative item
*	Search a user
*	Follow/unfollow a user
*	Visualize suggested users
*	Logout from the application



 ### Queries
 
 #### MongoDB
 
*	Search insertions by seller, brand or filters
*	Search user by username or filters
*	Get viral insertions
*	Top k rated users by country
*	Top k users with more purchased items
*	Top k users with more sold items
*	Top k interesting insertions by category
*	Top k viewed insertions by category

 
 #### Neo4J
 
*	Suggest new sellers based on the same country and followers
*	Suggest new insertions based on similar interested insertions and category
*	Get the k insertions posted by the followers of a specific user
*	Show to the admin the amount of like per category


### Details

There is not a cart: a user can buy one item at a time clicking on the "shop" button.
The part regarding users correlation is managed with Neo4j.

A review is done a rating (1-5 stars) and a comment regarding the product and is associated to the user.
Every user has a personal wallet that can recharge inserting a code in a specific field.
When a purchase is copmuted the amount is decreased.

Every post has a "sold" field and an ID: when a user purchases an item the ID relative to the post is memorized in an array of the user collection.

Suggestions are based on different infomations like similar purchases, location, search parameters, likes and feed.

## Link

[Dataset1] [Dataset2] [Dataset3] containing items.

[Dataset4] containing reviews.

[Site] for genersting rasndom users.

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
User | personal information, [reviews embedded], suspended (bool)
Admin | codes, credit
Insertion | details, #interested, #views
Code | code, credit
Balance | username, credit

#### USER

Field | Values | Type
------------- | ------------- | -------------
ID | 61fd8cc3edf5f2f4bd1366b9 | Varchar
ADDRESS | 913-2627 Donec St. | String
CITY | Vienna | String
COUNTRY | {Italy, Canada, Spain, Austria, Germany, France, Brazil, Netherlands, Poland, Ireland, United Kingdom (Great Britain)} | String
EMAIL | sem.ut@aol.edu | String
NAME | Ruben Torphy | String
PASSWORD | 45a0af6e6952e0a7e2a0a25f951a271 | String
SUSPENDED | false | Boolean
USERNAME | Añes | String
REVIEWS | [] | Array
RATING | 4.5 | Double
SOLD | [] | Array
PURCHASED | [] |Array

#### INSERTION

Field | Values | Type
------------- | ------------- | -------------
ID | 61fd8cc3edf5f2f4bd1366b8 | Varchar
CATEGORY | {clothing,accessories, bags, beauty, house, jewelry, kids, shoes} | String
DESCRIPTION | text | String
GENDER | {M, F, U} | String
PRICE | 10,56 | Double
INTERESTED| 10 | Integer
VIEWS | 10 | Integer
STATUS | {new, excellent, good, used, very used} | String
COLOR | {red, orange, yellow} | String
SIZE | {XS, S, M, L, XL} | String
BRAND | {Micheal Kors} | String
COUNTRY | {Italy, Canada, Spain, Austria, Germany, France, Brazil, Netherlands, Poland, Ireland, United Kingdom (Great Britain)} | String
IMAGE_URL | http://www.something.com | String
TIMESTAMP | 2020-02-07 05:11:36 | String
SELLER | username | String

####  CODE

Field | Values | Type
------------- | ------------- | -------------
CODE | 3SSXTPFQTG | Varchar
CREDIT | 200 | Int32

####  ORDER

Field | Values | Type
------------- | ------------- | -------------
USERNAME | username | String
CREDIT| 10,56 | Double


## Neo4j

### Vertices

* User

* Insertion

### Edges

* (u:User)-[:POSTED]->(i:Insertion)

* (u:User)-[:FOLLOWS]->(v:User)

* (u:User)-[:INTERESTED]->(i:Insertion)


## License

MIT

**Free Software, Hell Yeah!**

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [Dataset1]: <https://data.world/wordlift/shopping-demo/workspace/file?filename=amazon-fashion.csv>
      
   [Dataset2]: <https://data.world/jfreex/products-catalog-from-newchiccom>
   
   [Dataset3]: <https://data.world/promptcloud/amazon-australia-product-listing/workspace/project-summary?agentid=promptcloud&datasetid=amazon-australia-product-listing>
   
   [Dataset4]: <https://www.kaggle.com/asmaoueslati/womensclothingecommerce>
   
   [Site]: <https://generatedata.com/generator>
 
