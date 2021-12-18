# importing the pandas library
import pandas as pd
import random

# reading the csv file
# IMPORTANTE: se si presenta il seguente errore: Error tokenizing data. C error. sostituire il delimiter con ';'
df = pd.read_csv("men.csv", index_col=False, delimiter=",")

print(df)

# Dentro l'array inserire tutti i campi possibili che verranno estratti a caso
array = ["M", "F"]

# Lunghezza del file csv
print(len(df))

# Il file CSV contiene tutti campi float, se vogliamo inserire stringhe dobbiamo fare il cast del campo
df['gender'] = df['gender'].astype(str)

# Alla colonna "gender" per ogni riga i modifica la cella [i, gender]
for i in range(len(df)):
    df.at[i, 'gender'] = random.choice(array)

# Stampa i primi 5 record
print(df.head(5))

# writing into the file
df.to_csv("file_updated.csv", index=False)
