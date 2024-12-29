import string
from itertools import permutations
# Function to remove specific characters at positions
def remove_char(string, pos):
 new_string = ""
 for i in range(len(string)):
 if str(i) not in pos:
 new_string += string[i]
 return new_string
# Input symbols
NT_symbol = input("Enter the NonTerminal Symbols: ").split(" ")
T_symbol = input("Enter the Terminal Symbols: ").split(" ")
main = {}
new_NT_for_T = {} # Dictionary to store new non-terminals for terminals
# Eliminating NULL Production
n = [] # List to store nullable Non-Terminals
# Input production rules
for i in NT_symbol:
 main[i] = input(i + " -> ").split("/")
# Find nullable productions
for i in main:
 for k in main[i]:
 if "^" in k: # '^' represents epsilon/null production
 n.append(i)
n = [*set(n)] # Removing duplicates from nullable Non-Terminals
# Handle nullable productions
for i in main:
 for k in main[i]:
 pos = ""
 m = []
 possible_comb = []
 for u in range(len(k)):
 if k[u] in n:
 pos += str(u)
 for b in range(len(pos)):
 temp = [''.join(p) for p in permutations(pos, b + 1)]
 possible_comb = possible_comb + temp
 for b in possible_comb:
 new_k = k
 if k != "^":
 m.append(remove_char(k, b))
 main[i] = main[i] + list(set(m))
 main[i] = [production.replace('^', '') for production in main[i] if
production.replace('^', '') != ""]
# Removing unit productions
unit_prod = {}
for i in NT_symbol:
 unit_prod[i] = []
for i in main:
 for b in main[i]:
 if b in NT_symbol:
 unit_prod[i].append(b)
# Remove unit productions and replace them
for i in unit_prod:
 for j in unit_prod[i]:
 main[i] = [x for x in main[i] if x != j]
 main[i] = main[i] + [x for x in main[j] if x not in main[i]]
# Printing the grammar after removing NULL and Unit productions
print("\nAFTER REMOVING NULL AND UNIT PRODUCTION")
for i in main:
 valid_productions = [p for p in main[i] if p != ""]
 print(f"{i} -> " + "/".join(valid_productions))
# Converting to CNF form
print("\nConverting to CNF form: ")
letter = len(string.ascii_uppercase) # To generate new non-terminal symbols
# Map each terminal to a unique non-terminal
for term in T_symbol:
 new_NT_for_T[term] = string.ascii_uppercase[letter - 1]
 letter -= 1
 main[new_NT_for_T[term]] = [term]
for sym in NT_symbol:
 for i in range(len(main[sym])):
 b = main[sym][i]
 new_b = ""
 # Replace terminals with their unique non-terminal mapping
 for char in b:
 if char in T_symbol:
 new_b += new_NT_for_T[char] # Use the pre-mapped nonterminal for this terminal
 else:
 new_b += char
 main[sym][i] = new_b
 # Handle productions with more than 2 symbols
 if len(main[sym][i]) > 2:
 new_var = main[sym][i]
 while len(new_var) > 2:
 new_NT = string.ascii_uppercase[letter - 1]
 letter -= 1
 NT_symbol.append(new_NT)
 main[new_NT] = [new_var[-2:]]
 new_var = new_var[:-2] + new_NT
 main[sym][i] = new_var
# Output the CNF form grammar
for i in main:
 valid_productions = [p for p in main[i] if p != ""]
 print(f"{i} -> " + "/".join(valid_productions))
