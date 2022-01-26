import random
import string
import pandas as pd
from random_object_id import generate

S = 10  # number of characters in the string.

array_credits_value = [10, 20, 25, 50, 100, 200]

print("Hello World!")

df = pd.DataFrame()

df["_id"] = ""
df["code"] = ""
df["credit"] = 0


for i in range(1000):
    ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k = S))
    #print("The randomly generated string is : " + str(ran)) # print the random data
    credit =  random.choice(array_credits_value)
    object_id = generate()
    df.loc[i] = [object_id, ran, credit]

print(df.head(10))

df.to_csv('codes.csv', encoding='utf-8', index=False)