import random
import string 
import pandas as pd  
S = 10  # number of characters in the string.  

array_credits_value = [10, 20, 25, 50, 100, 200]
assigned = "F"

df = pd.DataFrame()

df["code"] = ""
df["credit"] = 0
df["assigned"] = "F"


for i in range(1000):
    ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k = S))    
    #print("The randomly generated string is : " + str(ran)) # print the random data  
    credit =  random.choice(array_credits_value)
    df.loc[i] = [ran, credit, assigned]

print(df.head(10))

df.to_csv('codes.csv', encoding='utf-8', index=False)
