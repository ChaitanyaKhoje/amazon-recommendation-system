# Amazon recommendation system

## Project Abstract

Customer reviews on products on Amazon website play a critical role in customer's decision of buying a product.
Customer's choice of products, likes dislikes are affected by what other customers feel about those product.
This project aims to provide a more seamless and personalized experience to the customers by combining sentiment
analysis with  recommendation system techniques like collaborative filtering, product-to-product relation filtering.
It also aims at identifying trends in the market area-wise, which products/categories are in huge demand around the
year and the seasonality within the sale around the year where certain festivals/occasions affect the market in a
different way where products that are not related to each other are also bought together, for instance;
Christmas sale consists of products like ornaments, Christmas Trees, gift baskets etc. which are not related to
each other but should be recommended according to the season.

**Possible solutions:**

A sentiment analysis of users' reviews into positive, neutral, negative will help in understanding what users feel
about the product they bought. Product-to-Product relationships will tell us how products are related to each other
which will further help in finding the correct products to recommend and a customer-to-customer
collaborative filtering of products being bought will tell us how a user might be interested in the products that
another user has already bought as they have common interests.


Following are the technologies and languages used in this project.

Languages: Java, Python
Technologies: Apache Kafka (or Spark/Storm), Zookeeper, different libraries for sentiment analysis.

## Design

![alt text](https://github.com/ChaitanyaKhoje/amazon-recommendation-system/blob/master/img/RecommenderSystem-Page-2.png)


Dataset source:
---
http://jmcauley.ucsd.edu/data/amazon/

Citation:
---
R. He, J. McAuley. Modeling the visual evolution of fashion trends
with one-class collaborative filtering. WWW, 2016
J. McAuley, C. Targett, J. Shi, A. van den Hengel. Image-based
recommendations on styles and substitutes. SIGIR, 2015