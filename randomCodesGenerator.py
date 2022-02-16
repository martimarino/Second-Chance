import random
import string
import pandas as pd

S = 10  # number of characters in the string.

array_credits_value = [10, 20, 25, 50, 100, 200]

print("Hello World!")

df = pd.DataFrame()

df["code"] = ""
df["credit"] = 0


for i in range(1000):
    ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k = S))
    credit =  random.choice(array_credits_value)
    df.loc[i] = [ran, credit]

print(df.head(10))

df.to_csv('codes.csv', index=False)